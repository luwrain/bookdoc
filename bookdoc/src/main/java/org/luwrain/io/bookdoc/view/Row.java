// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import java.util.*;

import org.luwrain.io.bookdoc.*;

/**
 * Represents a single visual row in a rendered document. A row is composed
 * of one or more {@link RowPart} objects concatenated together to form a
 * horizontal text line with a specific absolute position (x, y) on the
 * rendered area.
 *
 * <p>Each row holds references to the runs that contribute text to it,
 * allowing navigation back from the rendered representation to the
 * document model.</p>
 *
 * @see RowPart
 * @see View
 * @see Layout
 */
public final class Row
{
    /** Absolute horizontal position in the area. */
    int x = 0;
    /** Absolute vertical position in the area. */
    int y = 0;

    private final RowPart[] parts;
    private final int partsFrom;
    private final int partsTo;

    /**
     * Creates a new row from a slice of the row parts array.
     *
     * @param parts      The full array of row parts; must not be null
     * @param partsFrom  The starting index (inclusive) in the parts array
     * @param partsTo    The ending index (exclusive) in the parts array
     * @throws IllegalArgumentException if partsFrom or partsTo is negative,
     *         or if partsFrom is not less than partsTo
     */
    Row(RowPart[] parts, int partsFrom, int partsTo)
    {
        if (partsFrom < 0)
            throw new IllegalArgumentException("partsFrom (" + partsFrom + ") may not be negative");
        if (partsTo < 0)
            throw new IllegalArgumentException("partsTo (" + partsTo + ") may not be negative");
        if (partsFrom >= partsTo)
            throw new IllegalArgumentException("partsFrom (" + partsFrom + ") must be less than partsTo (" + partsTo + ")");
        this.parts = parts;
        this.partsFrom = partsFrom;
        this.partsTo = partsTo;
    }

    /**
     * Returns the full text of this row by concatenating all its row parts.
     *
     * @return The row text
     */
    public String getText()
    {
        final StringBuilder b = new StringBuilder();
        for (int i = partsFrom; i < partsTo; ++i)
            b.append(parts[i].getText());
        return b.toString();
    }

    /**
     * Returns the {@link Run} that occupies the specified horizontal
     * position within this row.
     *
     * @param pos The character position relative to the row start; must not be negative
     * @return The run at the given position, or null if no run matches
     */
    Run getRunUnderPos(int pos)
    {
        if (pos < 0)
            throw new IllegalArgumentException("pos may not be negative");
        final int index = getPartIndexUnderPos(pos);
        if (index < 0)
            return null;
        return parts[index].run;
    }

    /**
     * Returns all distinct runs that contribute text to this row.
     *
     * @return An array of runs (never null)
     */
    public Run[] getRuns()
    {
        final List<Run> res = new ArrayList();
        for (int i = partsFrom; i < partsTo; ++i)
        {
            final Run run = parts[i].run;
            int k = 0;
            for (k = 0; k < res.size(); ++k)
                if (res.get(k) == run)
                    break;
            if (k >= res.size())
                res.add(run);
        }
        return res.toArray(new Run[res.size()]);
    }

    /**
     * Finds the horizontal offset within this row where the specified
     * run begins.
     *
     * @param run The run to locate; must not be null
     * @return The character offset where the run starts, or -1 if the run
     *         is not present on this row
     */
    public int runBeginsAt(Run run)
    {
        int offset = 0;
        for (int i = partsFrom; i < partsTo; ++i)
        {
            final String text = parts[i].getText();
            if (text == null || text.isEmpty())
                continue;
            if (parts[i].run == run)
                return offset;
            offset += text.length();
        }
        return -1;
    }

    /**
     * Returns the row number relative to its parent paragraph.
     *
     * @return The relative row number within the paragraph
     */
    public int getRelNum()
    {
        return getFirstPart().relRowNum;
    }

    /**
     * Returns the first run of this row.
     *
     * @return The first run (never null for a properly constructed row)
     */
    public Run getFirstRun()
    {
        return getFirstPart().run;
    }

    /**
     * Returns the absolute horizontal position of this row.
     *
     * @return The x-coordinate
     */
    public int getRowX()
    {
        return x;
    }

    /**
     * Returns the absolute vertical position of this row.
     *
     * @return The y-coordinate
     */
    public int getRowY()
    {
        return y;
    }

    private RowPart getFirstPart()
    {
        return parts[partsFrom];
    }

    /**
     * Returns the index of the row part that covers the given character position.
     *
     * @param pos The character position relative to the row start; must not be negative
     * @return The part index, or -1 if no part matches
     */
    private int getPartIndexUnderPos(int pos)
    {
        if (pos < 0)
            throw new IllegalArgumentException("pos may not be negative");
        int offset = 0;
        for (int i = partsFrom; i < partsTo; ++i)
        {
            final String text = parts[i].getText();
            if (text == null || text.isEmpty())
                continue;
            if (pos >= offset && pos < offset + text.length())
                return i;
            offset += text.length();
        }
        return -1;
    }
}
