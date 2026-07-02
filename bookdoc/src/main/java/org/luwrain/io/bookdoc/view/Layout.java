// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import java.util.*;

import org.luwrain.io.bookdoc.*;

/**
 * Represents the final rendered layout of a document as a set of
 * absolute-positioned text lines. Each line collects one or more
 * {@link Row} objects that appear at the same vertical coordinate.
 *
 * <p>The layout is obtained from a {@link View} via
 * {@link View#createLayout()} and provides convenient access to
 * fully composed text lines suitable for display.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * Layout layout = view.createLayout();
 * for (int i = 0; i < layout.getLineCount(); ++i)
 *     System.out.println(layout.getLine(i));
 * }</pre>
 *
 * @see View
 * @see Row
 */
public final class Layout
{
    protected final Doc document;
    protected final Node root;
    /** Only paragraphs which appear in the document; paragraphs without row parts are excluded. */
    protected final Paragraph[] paragraphs;
    protected final RowPart[] rowParts;
    protected final Row[] rows;
    protected final Line[] lines;

    /**
     * Constructs a layout from the given rendering data. Rows are grouped
     * by their vertical coordinate into {@link Line} objects.
     *
     * @param document   The source document
     * @param root       The root node of the document
     * @param rows       All rows in the rendered document
     * @param rowParts   All row parts
     * @param paragraphs All paragraphs that have row parts
     * @param lineCount  The total number of distinct vertical lines
     */
    Layout(Doc document, Node root,
           Row[] rows, RowPart[] rowParts,
           Paragraph[] paragraphs, int lineCount)
    {
        this.document = document;
        this.root = root;
        this.paragraphs = paragraphs;
        this.rows = rows;
        this.rowParts = rowParts;
        lines = new Line[lineCount];
        for (int i = 0; i < lines.length; ++i)
            lines[i] = new Line();
        for (Row row : rows)
        {
            final Line line = lines[row.y];
            line.add(row);
        }
    }

    /**
     * Returns the total number of lines in this layout.
     *
     * @return The line count
     */
    public int getLineCount()
    {
        return lines.length;
    }

    /**
     * Returns the text of the line at the given index. Rows that belong
     * to the same line are concatenated with appropriate left-padding
     * to respect their horizontal offsets.
     *
     * @param index The line index; must not be negative
     * @return The composed line text
     * @throws IllegalArgumentException if index is negative
     */
    public String getLine(int index)
    {
        if (index < 0)
            throw new IllegalArgumentException("index (" + index + ") may not be negative");
        final Line line = lines[index];
        StringBuilder b = new StringBuilder();
        for (Row row : line.rows)
        {
            while (b.length() < row.x)
                b.append(" ");
            b.append(row.getText());
        }
        return b.toString();
    }

    /**
     * Internal representation of a single visual line, which may consist
     * of multiple rows placed at the same vertical position but different
     * horizontal offsets (e.g., table cells on the same line).
     */
    static protected class Line
    {
        Row[] rows = new Row[0];

        /**
         * Adds a row to this line.
         *
         * @param row The row to add
         */
        void add(Row row)
        {
            rows = Arrays.copyOf(rows, rows.length + 1);
            rows[rows.length - 1] = row;
        }
    }
}
