// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

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
    final LinkedList<Attributes> attributesStack = new LinkedList<>();
    
    void addAttributes(Node node)
    {
	final org.luwrain.io.bookdoc.Attributes attr = new org.luwrain.io.bookdoc.Attributes();
	attr.tagName = node.nodeName();
	final var a = node.attributes();
	if (a != null)
	    for(org.jsoup.nodes.Attribute aa: a.asList())
	    {
		final String key = aa.getKey();
		final String value = aa.getValue();
		if (key != null && !key.isEmpty() && value != null)
		    attr.attrMap.put(key, value);
	    }
	if (!attributesStack.isEmpty())
	    attr.parentAttr.addAll(attributesStack);
	attributesStack.add(attr);
    }

    void releaseAttributes()
    {
	attributesStack.pollLast();
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
