
package org.luwrain.io.bookdoc.filters.html;

import java.util.*;
import java.net.*;
import org.apache.logging.log4j.*;

import org.jsoup.nodes.Node;
import org.luwrain.io.bookdoc.Attributes;

final class Context
{
    final LinkedList<String> anchorStack = new LinkedList<>();
    final Set<String> allAnchors = new HashSet<>();
    final URL baseUrl = null;
    
    void addAttributes(Node node)
    {
    }

    void releaseAttributes()
    {
    }

    Attributes getAttributes()
    {
	return null;
    }

String getActualAnchor()
    {
	if (anchorStack.isEmpty())
	    return null;
	return anchorStack.getLast();
    }


}
