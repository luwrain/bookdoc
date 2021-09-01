
package org.luwrain.io.filters.html;

import java.util.*;

import org.jsoup.nodes.*;

import org.luwrain.io.filters.*;

class Base
{
    static final String LOG_COMPONENT = "reader";

    private final LinkedList<org.luwrain.io.filters.textdoc.Attributes> extraInfoStack = new LinkedList<>();

    protected void addAttrs(Element el)
    {
	NullCheck.notNull(el, "el");
	final org.luwrain.io.filters.textdoc.Attributes attr = new org.luwrain.io.filters.textdoc.Attributes();
	attr.tagName = el.nodeName();
	final Attributes a = el.attributes();
	if (a != null)
	    for(Attribute aa: a.asList())
	    {
		final String key = aa.getKey();
		final String value = aa.getValue();
		if (key != null && !key.isEmpty() && value != null)
		    attr.attrMap.put(key, value);
	    }
	if (!extraInfoStack.isEmpty())
	    attr.parentAttr.addAll(extraInfoStack);
	extraInfoStack.add(attr);
    }

    protected void releaseExtraInfo()
    {
	if (!extraInfoStack.isEmpty())
	    extraInfoStack.pollLast();
    }

    protected org.luwrain.io.filters.textdoc.Attributes getAttributes()
    {
	return extraInfoStack.isEmpty()?null:extraInfoStack.getLast();
    }

    static protected void collectMeta(Element el, Map<String, String> meta)
    {
	NullCheck.notNull(el, "el");
	NullCheck.notNull(meta, "meta");
	if (el.nodeName().equals("meta"))
	{
	    final String name = el.attr("name");
	    final String content = el.attr("content");
	    if (name != null && !name.isEmpty() && content != null)
		meta.put(name, content);
	}
	if (el.childNodes() != null)
	    for(Node n: el.childNodes())
		if (n instanceof Element)
		    collectMeta((Element)n, meta);
    }
}
