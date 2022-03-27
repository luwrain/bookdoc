
package org.luwrain.io.bookdoc;

import java.util.*;

public class Container 
{
    private List<ContainerItem> items = null;

    public List<ContainerItem> getItems()
    {
	if (this.items == null)
	    this.items = new ArrayList<>();
	return this.items;
    }

    public void setAttributes(Attributes attr)
    {
    }
}
