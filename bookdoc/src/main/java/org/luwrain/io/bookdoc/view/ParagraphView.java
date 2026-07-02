// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

/**
 * Holds the result of splitting a {@link org.luwrain.io.bookdoc.Paragraph} into
 * row parts. Each paragraph has its own {@code ParagraphView} that stores
 * an array of {@link RowPart} objects representing how the paragraph's text
 * is divided across lines for a given width.
 *
 * @see RowPart
 * @see DefaultRowPartsBuilder
 */
public final class ParagraphView
{
    private RowPart[] rowParts = new RowPart[0];

    /**
     * Returns the row parts associated with this paragraph view.
     *
     * @return A non-null array of row parts (may be empty)
     */
    RowPart[] getRowParts()
    {
        return rowParts;
    }

    /**
     * Sets the row parts for this paragraph view.
     *
     * @param rowParts The array of row parts to store;
     *                 if null, an empty array is used instead
     */
    void setRowParts(RowPart[] rowParts)
    {
        this.rowParts = rowParts != null ? rowParts : new RowPart[0];
    }
}
