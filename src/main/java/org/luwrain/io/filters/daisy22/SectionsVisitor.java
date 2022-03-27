
package org.luwrain.io.filters.daisy22;

import java.util.*;
import org.luwrain.io.bookdoc.*;

class SectionsVisitor extends Visitor
{
    final List<Book.Section> sections = new ArrayList<>();

    /*
    @Override public void visit(Section node)
    {
	final LinkedList<String> hrefs = new LinkedList<String>();
	collectHrefs(node, hrefs);
	if (!hrefs.isEmpty())
	    sections.add(new Book.Section(node.getSectionLevel(), node.getCompleteText().trim(), hrefs.getFirst()));
    }
    */


    /*
    static private void collectHrefs(Node node, LinkedList<String> hrefs)
    {
	NullCheck.notNull(node, "node");
	NullCheck.notNull(hrefs, "hrefs");
	if (node instanceof Paragraph)
	{
	    final Paragraph para = (Paragraph)node;
	    for(Run r: para.getRuns())
		    if (r.href() != null && !r.href().isEmpty())
			hrefs.add(r.href());
	} else
	    for(Node n: node.getSubnodes())
		    collectHrefs(n, hrefs);
    }
    */
}
