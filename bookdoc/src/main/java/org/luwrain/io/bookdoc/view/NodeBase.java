// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

/**
 * Base class providing geometric attributes and row-part storage for
 * document nodes. It holds the absolute (x, y) position, width, height,
 * and an array of {@link RowPart} objects associated with the node.
 *
 * <p>Subclasses should use the getter/setter pairs to manipulate
 * these properties.</p>
 *
 * @see RowPart
 * @see NodeGeom
 */
public class NodeBase
{
    /** Absolute horizontal position in the area. */
    private int x = -1;

    /** Absolute vertical position in the area. */
    private int y = -1;

    /** The width of this node in characters. */
    public int width = 0;

    /** The height of this node in lines. */
    private int height = 0;

    private RowPart[] rowParts = new RowPart[0];

    /**
     * Returns the absolute x-coordinate of this node.
     *
     * @return The x position, or -1 if not yet set
     */
    public int getNodeX()
    {
        return x;
    }

    /**
     * Sets the absolute x-coordinate of this node.
     *
     * @param value The new x position
     */
    public void setNodeX(int value)
    {
        x = value;
    }

    /**
     * Returns the absolute y-coordinate of this node.
     *
     * @return The y position, or -1 if not yet set
     */
    public int getNodeY()
    {
        return y;
    }

    /**
     * Sets the absolute y-coordinate of this node.
     *
     * @param value The new y position
     */
    public void setNodeY(int value)
    {
        y = value;
    }

    /**
     * Returns the width of this node in characters.
     *
     * @return The width
     */
    public int getNodeWidth()
    {
        return width;
    }

    /**
     * Sets the width of this node in characters.
     *
     * @param value The new width
     */
    public void setNodeWidth(int value)
    {
        width = value;
    }

    /**
     * Returns the height of this node in lines.
     *
     * @return The height
     */
    public int getNodeHeight()
    {
        return height;
    }

    /**
     * Sets the height of this node in lines.
     *
     * @param value The new height
     */
    public void setNodeHeight(int value)
    {
        height = value;
    }

    /**
     * Sets the row parts associated with this node. A null value is
     * treated as an empty array.
     *
     * @param rowParts The array of row parts
     */
    public void setRowParts(RowPart[] rowParts)
    {
        this.rowParts = rowParts != null ? rowParts : new RowPart[0];
    }

    /**
     * Returns the row parts associated with this node.
     *
     * @return A non-null array of row parts (may be empty)
     */
    public RowPart[] getRowParts()
    {
        return rowParts;
    }
}
