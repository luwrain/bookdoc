// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

public final class Geom
{
    public int x, y;
    public int width, height;
    public boolean allSubnodesSingleLine;

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
