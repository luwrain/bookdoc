
package org.luwrain.main.bookdoc;

import org.luwrain.io.bookdoc.*;

final class OutputVisitor extends Visitor
{
    static private final String HEADING_PREFIX = "heading ";


    @Override public void visit(Paragraph p)
    {
	final String style;
	if (p.getAttributes().attrMap.containsKey(Attributes.STYLE))
	    style = p.getAttributes().attrMap.get(Attributes.STYLE).toString(); else
	    style = null;
	if (style != null && style.startsWith(HEADING_PREFIX))
	    System.out.print("%h" + style.substring(HEADING_PREFIX.length()) + " ");
	System.out.println(p.getText());
	System.out.println();
    }
}


