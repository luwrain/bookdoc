// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

/**
 * A heading inside a document.
 *
 * <p>A heading is a container, so it may contain the same rich content as any
 * other container. Its {@linkplain #getLevel() level} describes its hierarchy
 * position; lower values mean higher-level headings.</p>
 */
public final class Heading extends Container implements ContainerItem
{
    private final int level;

    /**
     * Creates a heading with the specified level.
     *
     * @param level the heading level; must be greater than zero
     * @throws IllegalArgumentException if {@code level} is not greater than zero
     */
    public Heading(int level)
    {
	if (level <= 0)
	    throw new IllegalArgumentException("level must be greater than zero");
	this.level = level;
    }

    /**
     * Returns the level of this heading.
     *
     * @return the heading level; always greater than zero
     */
    public final int getLevel()
    {
	return this.level;
    }
}
