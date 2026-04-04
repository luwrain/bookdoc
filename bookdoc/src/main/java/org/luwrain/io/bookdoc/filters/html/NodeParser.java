
package org.luwrain.io.bookdoc.filters.html;

import java.util.*;

import org.jsoup.nodes.*;
import org.luwrain.io.bookdoc.*;

import static java.util.Objects.*;

class NodeParser
{
    final org.jsoup.nodes.Node source;
    		final List<ContainerItem> resItems = new ArrayList<>();
	final List<Run> runs = new ArrayList<>();
    final boolean preMode = false;

    NodeParser(org.jsoup.nodes.Node source)
    {
	this.source = requireNonNull(source, "source can't be null");
    }

    NodeParser process()
    {
	if (source.childNodes() == null)
	    	    return this;
	for(var n: source.childNodes())
	{
	    if (n instanceof TextNode textNode)
	    {
		process(textNode);
		continue;
	    }
	    if (n instanceof Element el)
	    {
		process(el);
		continue;
	    }
	    if (n instanceof Comment)
		continue;
	    throw new IllegalStateException("unprocessed node of class " + n.getClass().getName());
	}
	commitParagraph();
	return this;
    }


    void process(TextNode node)
    {
    }

    void process(Element el)
    {
    }

    void commitParagraph()
    {
    }
}
