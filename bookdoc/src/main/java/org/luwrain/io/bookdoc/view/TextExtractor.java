// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import java.util.*;

import org.luwrain.io.bookdoc.*;

/**
 * Base class for text extractors that convert document content into arrays
 * of plain-text lines. Provides common utilities for accumulating lines
 * and managing paragraph separation.
 *
 * <p>Subclasses must implement the logic for traversing the document tree
 * and feeding row-part data into {@link #onParagraphLines(RowPart[])}.</p>
 *
 * @see TextExtractorWhole
 * @see TextExtractorFragment
 * @see RowPartsSplitter
 */
class TextExtractor
{
    static protected final String LOG_COMPONENT = "document";

    /** The accumulated text lines. */
    final LinkedList<String> lines = new LinkedList();

    /**
     * Converts an array of row parts into lines by grouping parts
     * that share the same relative row number.
     *
     * @param rowParts The row parts to process; must not be null
     */
    protected void onParagraphLines(RowPart[] rowParts)
    {
        if (rowParts.length == 0)
            return;
        int lineNum = rowParts[0].relRowNum;
        int i = 0;
        while (i < rowParts.length)
        {
            final StringBuilder b = new StringBuilder();
            while (i < rowParts.length && rowParts[i].relRowNum == lineNum)
            {
                b.append(rowParts[i].getText());
                ++i;
            }
            final String s = new String(b);
            if (!s.isEmpty())
                lines.add(s);
            if (i < rowParts.length)
                lineNum = rowParts[i].relRowNum;
        }
    }

    /**
     * Adds an empty line to the output, but only if the last added
     * line is not already empty and the output is not empty.
     */
    protected void addEmptyLine()
    {
        if (lines.isEmpty())
            return;
        if (lines.getLast().isEmpty())
            return;
        lines.add("");
    }

    /**
     * Returns the accumulated lines as an array of strings.
     *
     * @return An array of text lines
     */
    public String[] getLines()
    {
        return lines.toArray(new String[lines.size()]);
    }
}
