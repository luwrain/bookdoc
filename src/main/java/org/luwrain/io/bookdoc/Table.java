
package org.luwrain.io.bookdoc;

import java.util.*;

public final class Table implements Node
{
    private int width= 0, height = 0;
    private final ArrayList<ArrayList<Node>> cells = new ArrayList();

    public Node getCell(int col, int row)
    {
	if (col >= width)
	    throw new IllegalArgumentException("col is out of range");
	if (row >= height)
	    throw new IllegalArgumentException("row is out of range");
	return cells.get(col).get(row);
    }

    public void resize(int width, int height)
    {
	if (width < 0)
	    throw new IllegalArgumentException("width can't be negative");
	if (height < 0)
	    throw new IllegalArgumentException("height can't be negative");
	if (cells.size() != this.width)
	    throw new IllegalStateException("The table has an invalid number of columns");
	if (width > this.width)
	{
	    for(int i = this.width;i < width;i++)
		cells.add(new ArrayList());
	} else
	    for(int i = this.width;i > width;i--)
		cells.remove(cells.size() - 1);
	for(ArrayList<Node> l: cells)
	{
	    if (l.size() != this.height)
		throw new IllegalStateException("The table column has an invalid height");
	    if (height > this.height)
	    {
		for(int i = this.height;i < height;i++)
		    l.add(Node.EMPTY);
	    } else
		for(int i = this.height;i > height;i--)
		    l.remove(l.size() - 1);
	}
	this.width = width;
	this.height = height;
    }
}
