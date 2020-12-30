
package org.luwrain.io.filters.msdocx;

import java.util.*;

import com.google.gson.annotations.*;

public class Document
{
    @SerializedName("items")
    public List<Item> items = null;

	public Item[] getItems()
    {
	if (items == null)
	    return new Item[0];
	return items.toArray(new Item[items.size()]);
    }

    public int getItemCount()
    {
	return items != null?items.size():0;
    }

    public Item getItem(int index)
    {
	if (items == null)
	    return null;
	return items.get(index);
    }

    

    public void addItem(Item item)
    {
	if (item == null)
	    throw new NullPointerException("item can't be null");
	if (this.items == null)
	    this.items = new ArrayList();
	this.items.add(item);
    }
}
