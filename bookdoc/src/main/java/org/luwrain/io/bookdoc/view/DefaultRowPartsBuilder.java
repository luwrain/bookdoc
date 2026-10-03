// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import java.util.*;
import org.luwrain.io.bookdoc.*;

/**
 * Builds an ordered list of {@link RowPart} objects by traversing the document
 * tree and splitting paragraph text into lines. This is the standard
 * implementation used by {@link View} during document rendering.
 *
 * <p>The builder performs two main tasks:</p>
 * <ul>
 *   <li>Visits every node in the document tree, recursing into containers</li>
 *   <li>For each paragraph, uses {@link RowPartsSplitter} to break runs
 *       into row parts respecting the paragraph's geometry width</li>
 * </ul>
 *
 * <p>The builder tracks which paragraphs actually produce row parts (non-empty
 * paragraphs) and exposes them separately via {@link #getParagraphs()}.</p>
 *
 * @see RowPart
 * @see RowPartsSplitter
 * @see View
 */
public final class DefaultRowPartsBuilder
{
    private final List<RowPart> parts = new ArrayList();
    private final List<Paragraph> paragraphs = new ArrayList();

    /**
     * Processes the given node and its subtree. If no explicit width is
     * specified, uses the node's own geometry width (which must have been
     * calculated beforehand).
     *
     * @param node The node to process; must not be null
     */
    public void onNode(Node node)
    {
        onNode(node, 0);
    }

    /**
     * Processes the given node and its subtree with an explicit width override.
     *
     * @param node  The node to process; must not be null
     * @param width The width to use for paragraphs; 0 means use the paragraph's
     *              own geometry width
     */
    public void onNode(Node node, int width)
    {
        if (node instanceof Container cont)
        {
            onContainer(cont, width);
            return;
        }
        if (node instanceof Paragraph paragraph)
        {
            onParagraph(paragraph, width);
            return;
        }
    }

    /**
     * Recursively processes a container by iterating over its items.
     *
     * @param <E>   The type of container items
     * @param c     The container to process
     * @param width The width to use for paragraphs
     */
    public <E extends ContainerItem> void onContainer(Container<E> c, int width)
    {
        for (ContainerItem i : c.getItems())
            onNode((Node) i);
    }

    /**
     * Splits a paragraph into row parts and stores them both in the
     * paragraph's view and in the builder's global list.
     *
     * @param para  The paragraph to process
     * @param width The width to use; 0 means use the paragraph's own geometry width
     */
    public void onParagraph(Paragraph paragraph, int width)
    {
	if (width < 0)
	    throw new IllegalArgumentException("width can't be negative (" + width + ")");
	if (width == 0 && paragraph.getGeom().width <= 0)
	    throw new IllegalStateException("Paragraph's own width is equal or less than zero (" + paragraph.getGeom().width + ")");
        final var splitter = new RowPartsSplitter();
        for (Run r : paragraph.getRuns())
        {
            final String text = r.getText();
            splitter.onRun(r, text, 0, text.length(), width > 0 ? width : paragraph.getGeom().width);
        }
        if (!splitter.res.isEmpty())
        {
            paragraph.getView().setRowParts(splitter.res.toArray(new RowPart[splitter.res.size()]));
            paragraphs.add(paragraph);
                parts.addAll(splitter.res);
        }
    }

    /**
     * Returns all row parts accumulated so far, in document order.
     *
     * @return A non-null array of row parts
     */
    public RowPart[] getRowParts()
    {
        return parts.toArray(new RowPart[parts.size()]);
    }

    /**
     * Returns all paragraphs that produced at least one row part, in document order.
     *
     * @return A non-null array of paragraphs
     */
    public Paragraph[] getParagraphs()
    {
        return paragraphs.toArray(new Paragraph[paragraphs.size()]);
    }
}
