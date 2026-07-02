// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import java.util.*;

import org.luwrain.io.bookdoc.*;

import static java.util.Objects.*;

/**
 * Splits run text into {@link RowPart} objects for line wrapping at a given
 * maximum row length. Words are kept intact whenever possible; a word is
 * broken across lines only if it is longer than the available line width
 * and the current line is empty.
 *
 * <p>This class is stateful: it accumulates row parts across multiple
 * calls to {@link #onRun(Run, String, int, int, int)} and tracks the
 * current line offset and row index.</p>
 *
 * @see RowPart
 * @see DefaultRowPartsBuilder
 */
final class RowPartsSplitter
{
    static private final String LOG_COMPONENT = "doctree";

    /** Accumulated row parts, in order. */
    final List<RowPart> res = new ArrayList();

    /** The index of the next row to be added to the current paragraph. */
    private int index = 0;
    /** Number of characters already placed on the current (incomplete) row. */
    private int offset = 0;

    /**
     * Feeds a fragment of run text into the splitter, producing one or more
     * row parts that fit within the specified maximum row length.
     *
     * <p>Removes leading spaces at row breaks only if the space appears
     * at a break point and the next character is non-space.</p>
     *
     * @param run       The run this text belongs to; must not be null
     * @param text      The full text of the run; must not be null
     * @param boundFrom The starting character position within text (inclusive)
     * @param boundTo   The ending character position within text (exclusive)
     * @param maxRowLen The maximum number of characters per row
     * @throws IllegalArgumentException if any argument is out of range
     */
    void onRun(Run run, String text, int boundFrom, int boundTo, int maxRowLen)
    {
        requireNonNull(run, "run can't be null");
        requireNonNull(text, "text can't be null");
        if (boundFrom < 0 || boundTo < 0)
            throw new IllegalArgumentException("boundFrom (" + boundFrom + ") and boundTo (" + boundTo + ") may not be negative");
        if (boundFrom > text.length() || boundTo > text.length())
            throw new IllegalArgumentException("boundFrom (" + boundFrom + ") and boundTo (" + boundTo + ") may not be greater than length of the text (" + text.length() + ")");
        if (boundFrom > boundTo)
            throw new IllegalArgumentException("boundFrom (" + boundFrom + ") may not be greater than boundTo (" + boundTo + ")");
        if (offset > maxRowLen)
            throw new RuntimeException("offset (" + offset + ") may not be greater than maxRowLen (" + maxRowLen + ")");
        if (boundFrom == boundTo)
            return;
        int nextStepFrom = boundFrom;
        while (nextStepFrom < boundTo)
        {
            final int stepFrom = nextStepFrom;
            final int roomOnLine = maxRowLen - offset;//Available space on current line
            if (roomOnLine == 0)
            {
                //Try again on the next line
                ++index;
                offset = 0;
                continue;
            }
            final int remains = boundTo - stepFrom;
            //Both remains and roomOnLine are greater than zero
            if (remains <= roomOnLine)
            {
                //Everything fits on the current line
                res.add(makeTextPart(run, stepFrom, boundTo));
                offset += remains;
                return;
            }
            int stepTo = findWordsFittingOnLIne(text, stepFrom, boundTo, roomOnLine);
            if (stepTo == stepFrom)//No word ends before the end of the row
            {
                if (offset > 0)
                {
                    //Trying to do the same once again from the beginning of the next line in hope a whole line is enough
                    offset = 0;
                    ++index;
                    continue;
                }
                //The only thing we can do is split the line in the middle of the word, no another way
                stepTo = stepFrom + roomOnLine;
            } //no fitting words
            if (stepTo <= stepFrom)
                throw new RuntimeException("stepTo (" + stepTo + ") == stepFrom (" + stepFrom + ")");
            if (stepTo - stepFrom > roomOnLine)
                throw new RuntimeException("Exceeding room on line (" + roomOnLine + "), stepFrom=" + stepFrom + ", stepTo=" + stepTo);
            res.add(makeTextPart(run, stepFrom, stepTo));
            ++index;
            offset = 0;
            nextStepFrom = findNextWord(stepTo, text, boundTo);
        } //main loop;
    }

    /**
     * Finds the rightmost word boundary within the given length restriction.
     *
     * @param text          The text to search within
     * @param posFrom       The starting position
     * @param boundTo       The upper bound of the search
     * @param lenRestriction The maximum allowed length from posFrom
     * @return The character position just after the last word that fits
     */
    private int findWordsFittingOnLIne(String text, int posFrom, int boundTo, int lenRestriction)
    {
        int pos = 0;
        int nextWordEnd = posFrom;
        while (nextWordEnd - posFrom <= lenRestriction)
        {
            pos = nextWordEnd;//It is definitely before the row end
            while (nextWordEnd < boundTo && Character.isWhitespace(text.charAt(nextWordEnd)))
                ++nextWordEnd;
            while (nextWordEnd < boundTo && !Character.isWhitespace(text.charAt(nextWordEnd)))
                ++nextWordEnd;
            if (nextWordEnd == pos)
                return pos;
        }
        return pos;
    }

    /**
     * Finds the start of the next word after the given position.
     *
     * @param pos     The position to start searching from
     * @param text    The text to search within
     * @param boundTo The upper bound
     * @return The position of the first non-whitespace character at or after pos
     */
    private int findNextWord(int pos, String text, int boundTo)
    {
        requireNonNull(text, "text can't be null");
        int i = pos;
        while (i < boundTo && Character.isWhitespace(text.charAt(i)))
            ++i;
        if (i >= boundTo)
            return pos;
        return i;
    }

    /**
     * Creates a row part for the given range of the run's text.
     *
     * @param run     The associated run
     * @param posFrom The starting character position
     * @param posTo   The ending character position (exclusive)
     * @return A new row part
     */
    private RowPart makeTextPart(Run run, int posFrom, int posTo)
    {
        requireNonNull(run, "run can't be null");
        return new RowPart(run, posFrom, posTo, index);
    }
}
