// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import java.net.*;
import java.util.*;
import org.apache.logging.log4j.*;

import org.luwrain.io.bookdoc.*;

import static java.util.Objects.*;

/**
 * Renders a {@link Doc} into a two-dimensional grid of {@link Row} objects
 * suitable for screen-reader navigation. The rendering pipeline is:
 *
 * <ol>
 *   <li>Calculate geometry (widths and heights) for all nodes via {@link NodeGeom}</li>
 *   <li>Split paragraph runs into {@link RowPart} objects via {@link DefaultRowPartsBuilder}</li>
 *   <li>Calculate vertical positions of all nodes</li>
 *   <li>Group row parts into {@link Row} objects and assign absolute row numbers</li>
 *   <li>Calculate final row coordinates</li>
 *   <li>Optionally set the default iterator index if the document has a starting reference</li>
 * </ol>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * Doc doc = ...; // load a document
 * View view = new View(doc, 80);
 * Layout layout = view.createLayout();
 * for (int i = 0; i < layout.getLineCount(); ++i)
 *     System.out.println(layout.getLine(i));
 * }</pre>
 *
 * @see Layout
 * @see Iterator
 * @see Row
 * @see RowPart
 */
public class View
{
    static private final Logger log = LogManager.getLogger();

    /**
     * The property key used to store the default iterator index in the document.
     */
    static public final String DEFAULT_ITERATOR_INDEX_PROPERTY = "defaultiteratorindex";

    protected final Doc doc;
    protected final Root root;
    /** Only paragraphs which appear in the document; paragraphs without row parts are excluded. */
    protected final Paragraph[] paragraphs;
    protected final RowPart[] rowParts;
    protected final Row[] rows;
    protected final int lineCount;

    /**
     * Creates a new view by rendering the given document at the specified width.
     * The entire rendering pipeline is executed during construction.
     *
     * @param doc   The document to render; must not be null
     * @param width The maximum line width in characters
     * @throws NullPointerException if doc is null
     */
    public View(Doc doc, int width)
    {
        requireNonNull(doc, "doc can't be null");
        this.doc = doc;
        this.root = doc.getRoot();
        final NodeGeom geom = new NodeGeom();
        geom.calcWidth(root, width);
        final DefaultRowPartsBuilder rowPartsBuilder = new DefaultRowPartsBuilder();
        rowPartsBuilder.onNode(root);
        rowParts = rowPartsBuilder.getRowParts();
        if (rowParts.length <= 0)
        {
            paragraphs = new Paragraph[0];
            rows = new Row[0];
            lineCount = 0;
            return;
        }
        paragraphs = rowPartsBuilder.getParagraphs();
        geom.calcHeight(root);
        geom.calcPosition(root);
        calcAbsRowNums(rowParts);
        rows = buildRows(rowParts);
        lineCount = calcRowsPosition(rows);
        setDefaultIteratorIndex();
    }

    /**
     * Creates a {@link Layout} from this view. The layout provides
     * access to the rendered lines with their absolute screen positions.
     *
     * @return A new layout instance
     */
    public Layout createLayout()
    {
        final Layout layout = new Layout(doc, root, rows, rowParts, paragraphs, lineCount);
        return layout;
    }

    /**
     * Assigns absolute row numbers to each row part. Row parts that belong
     * to the same logical row (same parent paragraph and same relative row
     * number) receive the same absolute row number.
     *
     * @param parts The array of row parts to number; must not be null
     */
    protected void calcAbsRowNums(RowPart[] parts)
    {
        if (parts.length < 1)
            return;
        RowPart first = parts[0];
        parts[0].absRowNum = 0;
        for (int i = 1; i < parts.length; ++i)
        {
            final RowPart part = parts[i];
            if (!first.onTheSameRow(part))
            {
                part.absRowNum = first.absRowNum + 1;
                first = part;
            } else
                part.absRowNum = first.absRowNum;
        }
    }

    /**
     * Builds an array of {@link Row} objects by grouping consecutive row parts
     * that share the same absolute row number.
     *
     * @param parts The array of row parts; must not be null and must not be empty
     * @return An array of rows
     * @throws RuntimeException if an attempt is made to create an empty row
     */
    static protected Row[] buildRows(RowPart[] parts)
    {
        final int rowCount = parts[parts.length - 1].absRowNum + 1;
        final int[] fromParts = new int[rowCount];
        final int[] toParts = new int[rowCount];
        for (int i = 0; i < rowCount; ++i)
        {
            fromParts[i] = -1;
            toParts[i] = -1;
        }
        for (int i = 0; i < parts.length; ++i)
        {
            final int rowIndex = parts[i].absRowNum;
            if (fromParts[rowIndex] == -1 || toParts[rowIndex] > i)
                fromParts[rowIndex] = i;
            if (toParts[rowIndex] < i + 1)
                toParts[rowIndex] = i + 1;
        }
        final Row[] rows = new Row[rowCount];
        for (int i = 0; i < rowCount; ++i)
            if (fromParts[i] >= 0 && toParts[i] >= 0)
                rows[i] = new Row(parts, fromParts[i], toParts[i]);
            else
                throw new RuntimeException("Trying to create an empty row");
        return rows;
    }

    /**
     * Calculates absolute (x, y) positions for each row based on the
     * geometry of their parent nodes.
     *
     * @param rows The array of rows to position; must not be null
     * @return The total number of lines (max y + 1)
     */
    protected int calcRowsPosition(Row[] rows)
    {
        int maxLineNum = 0;
        int lastX = 0;
        int lastY = 0;
        for (Row r : rows)
        {
            final Run run = r.getFirstRun();
            requireNonNull(run, "run can't be null");
            final Node parent = run.getParentNode();
            requireNonNull(parent, "parent can't be null");
            if (parent instanceof Paragraph)
            {
                final Paragraph paragraph = (Paragraph) parent;
                r.x = paragraph.getGeom().x;
                r.y = paragraph.getGeom().y + r.getRowY();
            } else
            {
                r.x = parent.getGeom().x;
                r.y = parent.getGeom().y;
            }
            lastX = r.x;
            lastY = r.y;
            if (r.y > maxLineNum)
                maxLineNum = r.y;
        }
        return maxLineNum + 1;
    }

    /**
     * Creates a new iterator positioned at the first row of this view.
     *
     * @return A new iterator
     */
    public org.luwrain.io.bookdoc.view.Iterator getIterator()
    {
        return new Iterator(this);
    }

    /**
     * Creates a new iterator positioned at the given row index.
     *
     * @param startingIndex The initial row index
     * @return A new iterator
     */
    public org.luwrain.io.bookdoc.view.Iterator getIterator(int startingIndex)
    {
        return new Iterator(this, startingIndex);
    }

    Paragraph[] getParagraphs()
    {
        return paragraphs.clone();
    }

    Row[] getRows()
    {
        return rows.clone();
    }

    RowPart[] getRowParts()
    {
        return rowParts.clone();
    }

    /**
     * Searches the document for a node whose identifier matches the
     * starting reference property and sets the default iterator index
     * accordingly. The default iterator index is stored as a document
     * property so that the initial view position can be restored later.
     */
    private void setDefaultIteratorIndex()
    {
        final String id = doc.getProperty("startingref");
        if (id == null || id.isEmpty())
            return;
        log.trace("Preparing default iterator index for " + id);
        final var it = getIterator();
        while (it.canMoveNext())
        {
            {
                final var data = it.getNode();
                if (data != null)
                {
                    final var attr = data.getAttr();
                    if (attr != null && attr.hasIdWithParents(id))
                        break;
                }
                final Run[] runs = it.getRuns();
                Run foundRun = null;
                for (Run r : runs)
                    if (r instanceof TextRun)
                    {
                        final TextRun textRun = (TextRun) r;
                        if (textRun.getAttrs().hasIdWithParents(id))
                            foundRun = textRun;
                    }
                if (foundRun != null)
                    break;
            }
            it.moveNext();
        }
        if (!it.canMoveNext())
        {
            log.trace("No iterator position found for " + id);
            doc.setProperty(DEFAULT_ITERATOR_INDEX_PROPERTY, "");
            return;
        }
        doc.setProperty(DEFAULT_ITERATOR_INDEX_PROPERTY, "" + it.getIndex());
        log.trace("Default iterator index set to " + it.getIndex());
    }

    /**
     * Convenience method that splits a single paragraph into lines
     * at the given width without creating a full view.
     *
     * @param para  The paragraph to split; must not be null
     * @param width The maximum line width in characters
     * @return An array of strings, one per line
     */
    static public String[] getParagraphLines(Paragraph para, int width)
    {
        requireNonNull(para, "para can't be null");
        final DefaultRowPartsBuilder builder = new DefaultRowPartsBuilder();
        builder.onNode(para, width);
        final RowPart[] parts = builder.getRowParts();
        for (RowPart r : parts)
            r.absRowNum = r.relRowNum;
        final Row[] rows = buildRows(parts);
        final List<String> lines = new ArrayList();
        for (Row r : rows)
            lines.add(r.getText());
        return lines.toArray(new String[lines.size()]);
    }
}
