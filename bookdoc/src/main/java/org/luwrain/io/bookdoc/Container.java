// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.*;

import static java.util.Objects.*;

/**
 * A node that contains a list of child items.
 *
 * <p>A container is an ordered collection of items of type {@code T}, where
 * {@code T} is a subtype of {@link ContainerItem}. Containers form the
 * structural basis of a document tree; root, lists, tables and headings are
 * all containers.</p>
 *
 * @param <T> the type of the items stored in this container
 */
public class Container<T extends ContainerItem> extends Node
{
    protected List<T> items = null;

    /**
     * Returns the items stored in this container.
     *
     * @return an unmodifiable list of items; never {@code null}
     */
    public List<T> getItems()
    {
	if (this.items == null)
	    this.items = new ArrayList<>();
	return Collections.unmodifiableList(this.items);
    }

    /**
     * Returns the number of items stored in this container.
     *
     * @return the item count; never negative
     */
    public int getItemCount()
    {
	return items != null?items.size():0;
    }

    /**
     * Appends an item to this container.
     *
     * @param item the item to add; must not be {@code null}
     * @throws NullPointerException if {@code item} is {@code null}
     */
    public void addItem(T item)
    {
	requireNonNull(item, "item can't be null");
	if (this.items == null)
	    this.items = new ArrayList<>();
	this.items.add(item);
    }

    /**
     * Appends all supplied items to this container.
     *
     * @param items the items to add; must not be {@code null}
     * @throws NullPointerException if {@code items} is {@code null}
     */
    public void addAllItems(List<T> items)
    {
	requireNonNull(items, "items can't be null");
	if (this.items == null)
	    this.items = new ArrayList<>();
	this.items.addAll(items);
    }

    /*
    public void setAttributes(Attributes attr)
    {
	this.attributes = requireNonNull(attr, "attr can't be null");
    }
    */
}
