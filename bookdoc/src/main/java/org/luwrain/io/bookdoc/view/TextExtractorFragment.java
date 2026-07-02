// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import java.util.*;

import org.luwrain.io.bookdoc.*;

import static java.util.Objects.*;

/**
 * Extracts a fragment of text from a document node tree, bounded by two
 * positions (each defined by a {@link Run} and a character offset). Only
 * the text between the starting and ending boundaries is included in the
 * result, respecting the specified maximum line width.
 *
 * <p>This class is useful for obtaining a portion of a document (e.g.,
 * a selection) as an array of formatted text lines.</p>
 *
 * @see TextExtractor
 * @see TextExtractorWhole
 * @see BoundingInfo
 */
final class TextExtractorFragment
{
    static private final String LOG_COMPONENT = "document";

    private final int width;
    private final Run runFrom;
    private final Run runTo;
    private final int posFrom;
    private final int posTo;

    private boolean accepting = false;
    private boolean finished = false;
    private final List<RowPart> parts = new LinkedList();

    /**
     * Creates a new fragment text extractor.
     *
     * @param width   The maximum number of characters per line; must not be negative
     * @param runFrom The run marking the start of the fragment; must not be null
     * @param posFrom The character offset within runFrom where the fragment starts
     * @param runTo   The run marking the end of the fragment; must not be null
     * @param posTo   The character offset within runTo where the fragment ends
     * @throws IllegalArgumentException if width is negative
     * @throws NullPointerException if runFrom or runTo is null
     */
    TextExtractorFragment(int width,
                          Run runFrom, int posFrom,
                          Run runTo, int posTo)
    {
        requireNonNull(runFrom, "runFrom can't be null");
        requireNonNull(runTo, "runTo can't be null");
        if (width < 0)
            throw new IllegalArgumentException("width (" + width + ") may not be negative");
        if (posFrom < 0)
            throw new IllegalArgumentException("posFrom (" + posFrom + ") may not be negative");
        if (posTo < 0)
            throw new IllegalArgumentException("posTo (" + posTo + ") may not be negative");
        this.width = width;
        this.runFrom = runFrom;
        this.posFrom = posFrom;
        this.runTo = runTo;
        this.posTo = posTo;
    }

    /**
     * Processes the given node and its subtree, extracting only the text
     * that falls within the bounded fragment. Nodes before the starting
     * boundary are skipped; nodes after the ending boundary stop the extraction.
     *
     * @param node The node to process; must not be null
     */
    void onNode(Node node)
    {
        requireNonNull(node, "node can't be null");
        if (finished)
            return;
        if (node instanceof Paragraph)
        {
            onParagraph((Paragraph) node);
            return;
        }
        if (node instanceof Container)
        {
            final Container<?> cont = (Container<?>) node;
            for (ContainerItem i : cont.getItems())
            {
                if (finished)
                    return;
                if (i instanceof Node)
                    onNode((Node) i);
            }
        }
    }

    private void onParagraph(Paragraph para)
    {
        requireNonNull(para, "para can't be null");
        if (finished)
            return;
        final List<Run> runList = para.getRuns();
        final Run[] runs = runList.toArray(new Run[runList.size()]);
        final Run boundingRun1 = searchForRun(runFrom, runs);
        final Run boundingRun2 = searchForRun(runTo, runs);
        if (!accepting && boundingRun1 == null && boundingRun2 == null)
            return;
        final RowPartsSplitter splitter = new RowPartsSplitter();
        if (boundingRun1 == null && boundingRun2 == null)
        {
            // Fully within the accepted range — take all runs
            for (Run r : runs)
            {
                final String text = r.getText();
                requireNonNull(text, "text can't be null");
                splitter.onRun(r, text, 0, text.length(), width);
            }
        } else
        {
            final BoundingInfo boundingInfo = prepareBoundingInfo(para, boundingRun1, boundingRun2);
            boundingInfo.filter(runs, (run, fromChar, toChar) -> {
                final String text = run.getText();
                requireNonNull(text, "text can't be null");
                if (fromChar < 0 || fromChar > text.length())
                    throw new RuntimeException("fromChar (" + fromChar + ") must be non-negative and not greater than " + text.length());
                if (toChar < 0 || toChar > text.length())
                    throw new RuntimeException("toChar (" + toChar + ") must be non-negative and not greater than " + text.length());
                splitter.onRun(run, text, fromChar, toChar, width);
            });
            // If we encountered the ending run, mark as finished
            if (boundingRun2 != null)
                finished = true;
        }
        if (splitter.res.isEmpty())
            return;
        for (RowPart p : splitter.res)
            parts.add(p);
    }

    private BoundingInfo prepareBoundingInfo(Paragraph para, Run run1, Run run2)
    {
        if (run1 != null)
            accepting = true;
        if (run1 == run2)
            return new BoundingInfo(runFrom, posFrom, runTo, posTo);
        if (run1 == null)
            return new BoundingInfo(null, -1, runTo, posTo);
        if (run2 == null)
            return new BoundingInfo(runFrom, posFrom, null, -1);
        return new BoundingInfo(runFrom, posFrom, runTo, posTo);
    }

    /**
     * Returns the extracted lines as an array of strings.
     *
     * @return An array of text lines
     */
    public String[] getLines()
    {
        final List<String> lines = new ArrayList<>();
        if (parts.isEmpty())
            return new String[0];
        int lineNum = parts.get(0).relRowNum;
        StringBuilder b = new StringBuilder();
        for (RowPart p : parts)
        {
            if (p.relRowNum != lineNum)
            {
                lines.add(b.toString());
                b = new StringBuilder();
                lineNum = p.relRowNum;
            }
            b.append(p.getText());
        }
        if (b.length() > 0)
            lines.add(b.toString());
        return lines.toArray(new String[lines.size()]);
    }

    /**
     * Searches for a given run within an array of runs.
     *
     * @param run  The run to search for; must not be null
     * @param runs The array to search within; must not be null and must not contain nulls
     * @return The matching run if found, null otherwise
     */
    static private Run searchForRun(Run run, Run[] runs)
    {
        requireNonNull(run, "run can't be null");
        for (Run r : runs)
        {
            requireNonNull(r, "runs array must not contain null elements");
            if (r == run)
                return run;
        }
        return null;
    }
}
