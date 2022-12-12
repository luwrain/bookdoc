
package org.luwrain.io.filters.daisy22;

import java.util.*;
import org.luwrain.io.bookdoc.*;
import org.luwrain.io.bookdoc.Book.*;

class SectionsVisitor extends Visitor
{
    final List<Section> sections = new ArrayList<>();

    @Override public void visit(Heading h)
    {
	final List<String> hrefs = new ArrayList<>();
	final Visitor hrefsVisitor = new Visitor(){
		@Override public void visit(Run run)
		{
		    if (run.getHref() != null && !run.getHref().isEmpty())
			hrefs.add(run.getHref());
		}
	    };
	Visitor.walk(h, hrefsVisitor);
	if (!hrefs.isEmpty())
	    sections.add(new Section(h.getLevel(), h.getText(), hrefs.get(0)));
    }
}
