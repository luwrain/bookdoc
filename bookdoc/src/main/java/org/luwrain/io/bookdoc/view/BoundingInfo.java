// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import org.luwrain.io.bookdoc.*;
import static java.util.Objects.*;

/**
 * Describes a bounding region in the document defined by a starting run/offset
 * and an ending run/offset. Used by {@link TextExtractorFragment} to filter
 * which portions of runs should be included in the extracted text.
 *
 * <p>The bounding info can represent several cases:</p>
 * <ul>
 *   <li>A range within a single run (runFrom == runTo)</li>
 *   <li>A range spanning multiple runs (runFrom != runTo)</li>
 *   <li>An open start (runFrom == null) — include everything from the beginning</li>
 *   <li>An open end (runTo == null) — include everything until the end</li>
 * </ul>
 *
 * @see TextExtractorFragment
 */
class BoundingInfo
{
    /**
     * Callback for receiving filtered run fragments.
     */
    interface Acceptor
    {
        /**
         * Accepts a fragment of a run's text.
         *
         * @param run     The run
         * @param posFrom The starting character position (inclusive)
         * @param posTo   The ending character position (exclusive)
         */
        void accept(Run run, int posFrom, int posTo);
    }

    final Run runFrom;
    final Run runTo;
    final int posFrom;
    final int posTo;

    /**
     * Creates a new bounding info.
     *
     * @param runFrom The starting run (may be null for an open start)
     * @param posFrom The starting character offset; ignored if runFrom is null
     * @param runTo   The ending run (may be null for an open end)
     * @param posTo   The ending character offset; ignored if runTo is null
     * @throws IllegalArgumentException if both runFrom and runTo are null,
     *         if runFrom is non-null and posFrom is negative,
     *         if runTo is non-null and posTo is negative,
     *         or if runFrom == runTo and posTo &lt; posFrom
     */
    BoundingInfo(Run runFrom, int posFrom, Run runTo, int posTo)
    {
        if (runFrom == null && runTo == null)
            throw new IllegalArgumentException("runFrom and runTo may not be null simultaneously");
        if (runFrom != null && posFrom < 0)
            throw new IllegalArgumentException("posFrom may not be negative");
        if (runTo != null && posTo < 0)
            throw new IllegalArgumentException("posTo may not be negative");

        if (runFrom == runTo && posTo < posFrom)
            throw new IllegalArgumentException("posTo (" + posTo + ") may not be less than posFrom (" + posFrom + ") with the same runFrom and runTo");

        this.runFrom = runFrom;
        this.posFrom = posFrom;
        this.runTo = runTo;
        this.posTo = posTo;
    }

    /**
     * Iterates over the given runs and invokes the acceptor for each
     * fragment that falls within the bounded region. Runs before the
     * starting boundary are skipped; runs after the ending boundary
     * stop the iteration.
     *
     * @param runs     The array of runs to filter; must not be null and must not contain null elements
     * @param acceptor The callback to receive accepted fragments; must not be null
     */
    void filter(Run[] runs, Acceptor acceptor)
    {
        requireNonNull(acceptor, "acceptor can't be null");
        boolean accepting = runFrom == null;
        for (Run r : runs)
        {
            if (accepting)
            {
                if (r == runTo)
                {
                    acceptor.accept(r, 0, Math.min(r.getText().length(), posTo));
                    return;
                }
                acceptor.accept(r, 0, r.getText().length());
                continue;
            }
            //not accepting
            if (r == runFrom)
            {
                if (r == runTo)
                {
                    //runFrom == runTo, nothing strange
                    acceptor.accept(r, posFrom, Math.min(r.getText().length(), posTo));
                    return;
                }
                acceptor.accept(r, posFrom, r.getText().length());
                accepting = true;
                continue;
            } // encountering runFrom
            if (r == runTo)//runTo met before we accepted anything, as you wish...
                return;
        }
    }
}
