// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.*;

import static java.util.Objects.*;

public class Container<T extends ContainerItem> extends Node
{
    protected List<T> items = null;
    protected Geom geom = null;
    protected Attributes attr = null;

    public List<T> getItems()
    {
	if (this.items == null)
	    this.items = new ArrayList<>();
	return Collections.unmodifiableList(this.items);
    }

    public void addItem(T item)
    {
	requireNonNull(item, "item can't be null");
	if (this.items == null)
	    this.items = new ArrayList<>();
	this.items.add(item);
    }

    public void setAttributes(Attributes attr)
    {
	this.attr = requireNonNull(attr, "attr can't be null");
    }

        public Geom getGeom()
    {
	if (geom == null)
	    geom = new Geom();
	return geom;
    }
}
