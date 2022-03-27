
package org.luwrain.io.bookdoc;

import java.util.*;

public class Container implements Node
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
