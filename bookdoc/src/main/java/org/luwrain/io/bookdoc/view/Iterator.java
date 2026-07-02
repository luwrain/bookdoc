// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import org.luwrain.io.bookdoc.*;

import static java.util.Objects.*;

/**
 * Provides navigation over the rendered rows of a {@link View}. The iterator
 * maintains a current row index and supports moving forward, backward, and
 * searching for rows matching custom criteria.
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * View view = new View(doc, 80);
 * Iterator it = view.getIterator();
 * while (it.canMoveNext())
 * {
 *     System.out.println(it.getText());
 *     it.moveNext();
 * }
 * }</pre>
 *
 * @see View
 * @see Row
 * @see Paragraph
 */
public final class Iterator
{
    /**
     * Callback interface for row-based search operations.
     * Implementations define what constitutes a matching row.
     */
    public interface Matching
    {
        /**
         * Determines whether the given row matches the search criteria.
         *
         * @param node      The container node that holds the paragraph (may be null for title rows)
         * @param paragraph The paragraph that contains the row (may be null for title rows)
         * @param row       The row to check
         * @return true if the row matches, false otherwise
         */
        boolean isRowMatching(Node node, Paragraph paragraph, Row row);
    }

    protected final View view;
    protected final Paragraph[] paragraphs;
    protected final Row[] rows;
    protected int current = 0;

    /**
     * Creates a new iterator positioned at the first row.
     *
     * @param view The view to iterate over; must not be null
     */
    public Iterator(View view)
    {
        requireNonNull(view, "view can't be null");
        this.view = view;
        this.paragraphs = view.getParagraphs();
        this.rows = view.getRows();
        current = 0;
    }

    /**
     * Creates a new iterator positioned at the specified row index.
     *
     * @param view       The view to iterate over; must not be null
     * @param initialPos The initial row index; must be non-negative and less than row count
     * @throws IllegalArgumentException if initialPos is out of range
     */
    public Iterator(View view, int initialPos)
    {
        requireNonNull(view, "view can't be null");
        this.view = view;
        this.paragraphs = view.getParagraphs();
        this.rows = view.getRows();
        if (initialPos < 0 || initialPos >= rows.length)
            throw new IllegalArgumentException("Invalid row initialPos (" + initialPos + "), row count is " + rows.length);
        current = initialPos;
    }

    /**
     * Checks whether this iterator has no rows to traverse.
     *
     * @return true if there are no rows, false otherwise
     */
    public boolean noContent()
    {
        return rows.length == 0;
    }

    /**
     * Returns the current row index.
     *
     * @return The current index, or -1 if there is no content
     */
    public int getIndex()
    {
        if (noContent())
            return -1;
        return current;
    }

    /**
     * Returns the total number of rows in this view.
     *
     * @return The row count
     */
    public int getCount()
    {
        return rows.length;
    }

    /**
     * Returns the view this iterator belongs to.
     *
     * @return The associated view
     */
    public View getView()
    {
        return view;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof Iterator))
            return false;
        final Iterator it = (Iterator) o;
        return current == it.current;
    }

    @Override
    public org.luwrain.io.bookdoc.view.Iterator clone()
    {
        return new Iterator(view, current);
    }

    /**
     * Checks whether it is possible to move to the next row.
     *
     * @return true if there is a next row, false otherwise
     */
    public boolean canMoveNext()
    {
        if (noContent())
            return false;
        return current + 1 < rows.length;
    }

    /**
     * Checks whether it is possible to move to the previous row.
     *
     * @return true if there is a previous row, false otherwise
     */
    public boolean canMovePrev()
    {
        if (noContent())
            return false;
        return current > 0;
    }

    /**
     * Moves the iterator to the next row.
     *
     * @return true if the move was successful, false if already at the end
     */
    public boolean moveNext()
    {
        if (!canMoveNext())
            return false;
        ++current;
        return true;
    }

    /**
     * Moves the iterator to the previous row.
     *
     * @return true if the move was successful, false if already at the beginning
     */
    public boolean movePrev()
    {
        if (!canMovePrev())
            return false;
        --current;
        return true;
    }

    /**
     * Moves the iterator to the last row.
     */
    public void moveEnd()
    {
        current = rows.length > 0 ? rows.length - 1 : 0;
    }

    /**
     * Moves the iterator to the first row.
     */
    public void moveBeginning()
    {
        current = 0;
    }

    /**
     * Searches forward from the current position for a row matching the given criteria.
     * On success the iterator is positioned at the matching row.
     *
     * @param matching The matching criteria; must not be null
     * @return true if a matching row was found, false otherwise
     */
    public boolean searchForward(Matching matching)
    {
        requireNonNull(matching, "matching can't be null");
        if (noContent())
            return false;
        return searchForward(matching, current);
    }

    /**
     * Searches forward from the specified starting index for a row matching
     * the given criteria. On success the iterator is positioned at the matching row.
     *
     * @param matching   The matching criteria; must not be null
     * @param searchFrom The row index to start searching from; must not be negative
     * @return true if a matching row was found, false otherwise
     * @throws IllegalArgumentException if searchFrom is negative
     */
    public boolean searchForward(Matching matching, int searchFrom)
    {
        requireNonNull(matching, "matching can't be null");
        if (searchFrom < 0)
            throw new IllegalArgumentException("searchFrom (" + searchFrom + ") may not be negative");
        if (noContent())
            return false;
        return search(matching, searchFrom, 1);
    }

    /**
     * Searches backward from the current position for a row matching the given criteria.
     * On success the iterator is positioned at the matching row.
     *
     * @param matching The matching criteria; must not be null
     * @return true if a matching row was found, false otherwise
     */
    public boolean searchBackward(Matching matching)
    {
        requireNonNull(matching, "matching can't be null");
        if (noContent())
            return false;
        return searchBackward(matching, current);
    }

    /**
     * Searches backward from the specified starting index for a row matching
     * the given criteria. On success the iterator is positioned at the matching row.
     *
     * @param matching   The matching criteria; must not be null
     * @param searchFrom The row index to start searching from; must not be negative
     * @return true if a matching row was found, false otherwise
     * @throws IllegalArgumentException if searchFrom is negative
     */
    public boolean searchBackward(Matching matching, int searchFrom)
    {
        requireNonNull(matching, "matching can't be null");
        if (searchFrom < 0)
            throw new IllegalArgumentException("searchFrom (" + searchFrom + ") may not be negative");
        if (noContent())
            return false;
        return search(matching, searchFrom, -1);
    }

    /**
     * Performs a linear search for a matching row. Does not change the
     * current position on failure.
     *
     * @param matching   The matching criteria
     * @param searchFrom The starting index
     * @param step       The direction: 1 for forward, -1 for backward
     * @return true if a matching row was found, false otherwise
     */
    protected boolean search(Matching matching, int searchFrom, int step)
    {
        for (int i = searchFrom; i >= 0 && i < rows.length; i += step)
        {
            final Row row = rows[i];
            final Run firstRun = row.getFirstRun();
            if (firstRun == null)
                continue;
            final Node parent = firstRun.getParentNode();
            final Paragraph para;
            final Node node;
            if (parent instanceof Paragraph)
            {
                para = (Paragraph) parent;
                node = para.getParentNode();
            } else
            {
                para = null;
                node = parent;
            }
            if (matching.isRowMatching(node, para, row))
            {
                current = i;
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the text of the current row.
     *
     * @return The row text, or an empty string if there is no content
     */
    public String getText()
    {
        if (noContent())
            return "";
        final Row row = rows[current];
        return row.getText();
    }

    /**
     * Returns the index of the current row within its paragraph.
     *
     * @return The relative row index within the paragraph, or -1 if there is no content
     */
    public int getIndexInParagraph()
    {
        if (noContent())
            return -1;
        return getRow().getRelNum();
    }

    /**
     * Checks whether the current row is the first row of its paragraph.
     *
     * @return true if it is the paragraph beginning, false otherwise
     */
    public boolean isParagraphBeginning()
    {
        return getIndexInParagraph() == 0;
    }

    /**
     * Checks whether the specified run appears on the current row.
     *
     * @param run The run to look for; must not be null
     * @return true if the run is present on the current row
     */
    public boolean hasRunOnRow(Run run)
    {
        requireNonNull(run, "run can't be null");
        final Run[] runs = getRow().getRuns();
        for (Run r : runs)
            if (run == r)
                return true;
        return false;
    }

    /**
     * Returns all runs present on the current row.
     *
     * @return An array of runs; empty array if there is no content
     */
    public Run[] getRuns()
    {
        if (noContent())
            return new Run[0];
        return getRow().getRuns();
    }

    /**
     * Returns the horizontal offset within the current row where the
     * specified run begins.
     *
     * @param run The run to locate; must not be null
     * @return The character offset where the run starts on the current row
     */
    public int runBeginsAt(Run run)
    {
        requireNonNull(run, "run can't be null");
        return getRow().runBeginsAt(run);
    }

    /**
     * Returns the absolute horizontal position of the current row.
     *
     * @return The x-coordinate
     */
    public int getX()
    {
        return getRow().getRowX();
    }

    /**
     * Returns the absolute vertical position of the current row.
     *
     * @return The y-coordinate
     */
    public int getY()
    {
        return getRow().getRowY();
    }

    /**
     * Returns the container node that owns the current paragraph.
     *
     * @return The parent node of the current paragraph, or null if there is no content
     */
    public Node getNode()
    {
        return getParaContainer();
    }

    /**
     * Returns the paragraph associated with the current row.
     *
     * @return The paragraph, or null if there is no content or the row
     *         does not belong to a paragraph
     */
    public Paragraph getParagraph()
    {
        if (noContent())
            return null;
        final Node parent = getFirstRunOfRow().getParentNode();
        return (parent instanceof Paragraph) ? (Paragraph) parent : null;
    }

    /**
     * Returns the container that holds the current paragraph.
     *
     * @return The parent container, or null if there is no content
     */
    protected Node getParaContainer()
    {
        if (noContent())
            return null;
        final Paragraph para = getParagraph();
        return para != null ? para.getParentNode() : null;
    }

    /**
     * Checks whether the current row covers the specified absolute position.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @return true if the position falls within the current row
     */
    public boolean coversPos(int x, int y)
    {
        if (noContent())
            return false;
        final Row r = getRow();
        if (r.getRowY() != y)
            return false;
        if (x < r.getRowX())
            return false;
        if (x > r.getRowX() + getText().length())
            return false;
        return true;
    }

    /**
     * Returns the run that occupies the specified horizontal position
     * within the current row.
     *
     * @param pos The position relative to the row beginning; must not be negative
     * @return The run at the given position
     * @throws RuntimeException if the iterator has no content
     */
    public Run getRunUnderPos(int pos)
    {
        if (pos < 0)
            throw new IllegalArgumentException("pos may not be negative");
        if (noContent())
            throw new RuntimeException("The iterator is without content");
        return rows[current].getRunUnderPos(pos);
    }

    /**
     * Returns the current row.
     *
     * @return The current row
     * @throws RuntimeException if the iterator has no content
     */
    public Row getRow()
    {
        if (noContent())
            throw new RuntimeException("Iterator is without content");
        if (current < 0 || current >= rows.length)
            return null;
        return rows[current];
    }

    /**
     * Returns the first run of the current row.
     *
     * @return The first run, or null if there is no content
     */
    protected Run getFirstRunOfRow()
    {
        if (noContent())
            return null;
        return rows[current].getFirstRun();
    }
}
