
package org.luwrain.io.bookdoc;

import java.util.*;

public class Container extends Node
{
    private List<ContainerItem> items = null;

    public List<ContainerItem> getItems()
    {
	if (this.items == null)
	    this.items = new ArrayList<>();
	return this.items;
    }

    public void addItem(ContainerItem item)
    {
	if (item == null)
	    throw new NullPointerException("item can't be null");
	if (this.items == null)
	    this.items = new ArrayList<>();
	this.items.add(item);
    }

    public void setAttributes(Attributes attr)
    {
    }
}
