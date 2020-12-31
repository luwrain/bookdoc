
package org.luwrain.io.filters.textdoc;

import java.util.*;

import com.google.gson.annotations.*;

public class Container
{
    @SerializedName("containerItem")
    private List<ContainerItem> items = null;

    public final void addItem(ContainerItem item)
    {
	if (item == null)
	    throw new NullPointerException("item can't be null");
	if (this.items == null)
	    this.items = new ArrayList();
	this.items.add(item);
    }

    public final ContainerItem[] getItems()
    {
	return this.items != null?this.items.toArray(new ContainerItem[this.items.size()]):new ContainerItem[0];
    }

    public final void clear()
    {
	this.items = null;
    }
}
