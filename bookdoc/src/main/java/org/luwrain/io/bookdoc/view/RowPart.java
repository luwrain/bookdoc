// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import org.luwrain.io.bookdoc.*;

/**
 * Represents a fragment of a {@link Run} that appears on a single rendered
 * row. When a paragraph is split into lines, each run may be divided across
 * multiple row parts, each belonging to a different row.
 *
 * <p>A row part references:</p>
 * <ul>
 *   <li>The original run</li>
 *   <li>A character range (posFrom, posTo) within that run's text</li>
 *   <li>A relative row number within the parent paragraph</li>
 *   <li>An absolute row number within the document (assigned later)</li>
 * </ul>
 *
 * @see Row
 * @see RowPartsSplitter
 * @see Run
 */
public final class RowPart
{
    /** The run this part is associated with. */
    final Run run;
    /** Starting character position in the text of the corresponding run. */
    final int posFrom;
    /** Ending character position (exclusive) in the text of the corresponding run. */
    final int posTo;
    /** Row index relative to the parent paragraph. */
    final int relRowNum;
    /** Absolute row index in the document, assigned during view construction. */
    int absRowNum = 0;

    /**
     * Creates a row part for an empty run (one that produces no visible text).
     *
     * @param run The associated run; must not be null
     */
    RowPart(Run run)
    {
        this.run = run;
        this.posFrom = -1;
        this.posTo = -1;
        this.relRowNum = 0;
    }

    /**
     * Creates a row part for a non-empty text fragment.
     *
     * @param run       The associated run; must not be null
     * @param posFrom   The starting character position in the run's text
     * @param posTo     The ending character position (exclusive) in the run's text
     * @param relRowNum The relative row number within the parent paragraph
     * @throws IllegalArgumentException if posFrom or posTo is negative,
     *         posFrom is not less than posTo, or relRowNum is negative
     */
    RowPart(Run run, int posFrom, int posTo, int relRowNum)
    {
        if (posFrom < 0)
            throw new IllegalArgumentException("posFrom (" + posFrom + ") may not be negative");
        if (posTo < 0)
            throw new IllegalArgumentException("posTo (" + posTo + ") may not be negative");
        if (posFrom >= posTo)
            throw new IllegalArgumentException("posFrom (" + posFrom + ") must be less than posTo (" + posTo + ")");
        if (relRowNum < 0)
            throw new IllegalArgumentException("relRowNum (" + relRowNum + ") may not be negative");
        this.run = run;
        this.posFrom = posFrom;
        this.posTo = posTo;
        this.relRowNum = relRowNum;
    }

    /**
     * Checks whether this row part represents an empty fragment (no text).
     *
     * @return true if the part is empty, false otherwise
     */
    boolean isEmpty()
    {
        return posFrom == posTo;
    }

    /**
     * Returns the text of this row part — a substring of the associated run's text.
     *
     * @return The text fragment, or an empty string if this part is empty
     */
    String getText()
    {
        if (isEmpty())
            return "";
        return run.getText().substring(posFrom, posTo);
    }

    /**
     * Checks whether this row part and the given one belong to the same
     * logical row (same parent node and same relative row number).
     *
     * @param rowPart The other row part to compare; must not be null
     * @return true if both parts are on the same row, false otherwise
     */
    boolean onTheSameRow(RowPart rowPart)
    {
        if (isEmpty() || rowPart.isEmpty())
            return false;
        return run.getParentNode() == rowPart.run.getParentNode() && relRowNum == rowPart.relRowNum;
    }
}
