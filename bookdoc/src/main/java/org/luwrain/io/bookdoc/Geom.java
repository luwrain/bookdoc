// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

/**
 * The layout geometry of a document node.
 *
 * <p>Geometry describes the position and size of a node when it is rendered.
 * The {@code allSubnodesSingleLine} flag indicates that all subnodes may be
 * placed on a single line.</p>
 */
public final class Geom
{
    /** The horizontal position. */
    public int x;
    /** The vertical position. */
    public int y;
    /** The width. */
    public int width;
    /** The height. */
    public int height;
    /** Indicates whether all subnodes can be laid out on a single line. */
    public boolean allSubnodesSingleLine;

    /**
     * Sets the position of this geometry.
     *
     * @param x the horizontal position; must not be negative
     * @param y the vertical position; must not be negative
     * @throws IllegalArgumentException if {@code x} or {@code y} is negative
     */
    public void setPos(int x, int y)
    {
	if (x < 0)
	    throw new IllegalArgumentException("x can't be negative");
	if (y < 0)
	    throw new IllegalArgumentException("y can't be negative");
	this.x = x;
	this.y = y;
    }
}
