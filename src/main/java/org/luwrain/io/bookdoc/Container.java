/*
   Copyright 2016-2022 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

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
