
package org.luwrain.io.bookdoc;

import java.util.*;

public class Visitor
{
    public void visitEveryNode(Node node) {}
    public void visit(Heading heading) {}
    public void visit(Paragraph paragraph) {}
    public void visit(Run run) {}

    static public void walk(Node node, Visitor visitor)
    {
	if (node == null)
	    throw new NullPointerException("node can't be null");
	if (visitor == null)
	    throw new NullPointerException("visitor can't be null");
	visitor.visitEveryNode(node);
	if (node instanceof Paragraph)
	{
	    final Paragraph paragraph = (Paragraph)node;
	    visitor.visit(paragraph);
	    for(Run r: paragraph.getRuns())
		visitor.visit(r);
	    return;
	}
	if (node instanceof Heading)
	    visitor.visit((Heading)node);
	if (node instanceof Container)
	{
	    final Container cont = (Container)node;
	    final List<ContainerItem> items = cont.getItems();
	    for(ContainerItem i: items)
		if (i instanceof Node)
		    walk((Node)i, visitor);
	}
    }
}
