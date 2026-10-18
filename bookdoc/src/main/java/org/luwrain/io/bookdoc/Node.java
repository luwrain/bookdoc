// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

/**
 * The base class of all nodes in a book document tree.
 *
 * <p>A node may belong to a {@link Container}, may have {@link Geom layout
 * geometry} and may carry {@link Attributes}. Every runnable document element
 * is a node, including paragraphs, headings, tables, list items and others.
 * Text runs ({@code Run} instances) are not nodes; they are owned by
 * paragraphs.</p>
 */
public class Node
{
    /** A shared empty node that can be used instead of a {@code null} reference. */
    static public final EmptyNode EMPTY = new EmptyNode();

    private Container container = null;
    protected Geom geom = null;
    protected Attributes attributes = null;

    /**
     * Returns the parent container of this node.
     *
     * @return the parent container, or {@code null} if this node is not attached to any container
     */
    public final Container getContainer()
    {
	return container;
    }

    /**
     * Sets the parent container of this node.
     *
     * @param container the parent container; may be {@code null}
     */
    public final void setContainer(Container container)
    {
	this.container = container;
    }

    /**
     * Returns the geometry of this node, creating an empty geometry object if
     * necessary.
     *
     * @return the geometry; never {@code null}
     */
    public final Geom getGeom()
    {
	if (geom == null)
	    geom = new Geom();
	return geom;
    }

    /**
     * Returns the attributes of this node, creating an empty attribute set if
     * necessary.
     *
     * @return the attributes; never {@code null}
     */
    public final Attributes getAttributes()
    {
	if (attributes == null)
	    attributes = new Attributes();
	return attributes;
    }

    /**
     * Collects the plain text content of this node by concatenating the text
     * of every {@link Run} reachable from this node.
     *
     * @return the concatenated text of all descendant runs
     */
    public final String collectText()
    {
	final StringBuilder res = new StringBuilder();
	final Visitor textVisitor = new Visitor(){
		@Override public void visit(Run run) { res.append(run.toString());}
	    };
	Visitor.walk(this, textVisitor);
	return new String(res);
    }
}
