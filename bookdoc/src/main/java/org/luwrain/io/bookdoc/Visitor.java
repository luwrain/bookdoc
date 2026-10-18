// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.*;
import org.apache.logging.log4j.*;

import static java.util.Objects.*;

/**
 * A node tree visitor.
 *
 * <p>The visitor is used together with {@link #walk(Node, Visitor)}, which
 * traverses a document tree and invokes the appropriate callbacks. Since most
 * node classes are not fully handled yet, the generic callback
 * {@link #visitEveryNode(Node)} is called for every node encountered.</p>
 */
public class Visitor
{
    static private final Logger log = LogManager.getLogger();

    /**
     * Called for every node during traversal, before any specific callback.
     *
     * @param node the node being visited; never {@code null}
     */
    public void visitEveryNode(Node node) {}

    /**
     * Called for every heading node during traversal.
     *
     * @param heading the heading being visited; never {@code null}
     */
    public void visit(Heading heading) {}

    /**
     * Called for every paragraph node during traversal.
     *
     * @param paragraph the paragraph being visited; never {@code null}
     */
    public void visit(Paragraph paragraph) {}

    /**
     * Called for every run of text during traversal.
     *
     * @param run the run being visited; never {@code null}
     */
    public void visit(Run run) {}

    /**
     * Traverses the document tree starting at the supplied node and invokes
     * the callbacks of the supplied visitor in depth-first order.
     *
     * @param node the node to start traversal from; must not be {@code null}
     * @param visitor the visitor to notify; must not be {@code null}
     * @throws NullPointerException if {@code node} or {@code visitor} is {@code null}
     */
    static public void walk(Node node, Visitor visitor)
    {
	requireNonNull(node, "node can't be null");
	requireNonNull(visitor, "visitor can't be null");
	visitor.visitEveryNode(node);
	if (node instanceof Paragraph paragraph)
	{
	    visitor.visit(paragraph);
	    for(Run r: paragraph.getRuns())
		visitor.visit(r);
	    return;
	}
	if (node instanceof Heading heading)
	    visitor.visit(heading);
	//TODO: Other classes
	if (node instanceof Container cont)
	{
	    final List<ContainerItem> items = cont.getItems();
	    for(ContainerItem i: items)
		if (i instanceof Node childNode)
		    walk(childNode, visitor); else
		    log.warn("Unvisited container item of the class {}", i.getClass().getName());
	}
    }
}
