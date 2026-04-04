
package org.luwrain.io.bookdoc.filters.html;

import java.util.*;
import org.apache.logging.log4j.*;

import org.jsoup.nodes.Node;
import org.luwrain.io.bookdoc.Attributes;

final class Context
{
    final LinkedList<String> anchorStack = new LinkedList<>();
    
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

}
