// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.*;
import org.apache.logging.log4j.*;

import static java.util.Objects.*;

public class Visitor
{
    static private final Logger log = LogManager.getLogger();
    
    public void visitEveryNode(Node node) {}
    public void visit(Heading heading) {}
    public void visit(Paragraph paragraph) {}
    public void visit(Run run) {}

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
