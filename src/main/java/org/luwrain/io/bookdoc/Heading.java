
package org.luwrain.io.bookdoc;

public final class Heading extends Container implements ContainerItem
{
    private final int level;

    public Heading(int level)
    {
	if (level <= 0)
	    throw new IllegalArgumentException("level must be greater than zero");
	this.level = level;
    }

    public final int getLevel()
    {
	return this.level;
    }
}
