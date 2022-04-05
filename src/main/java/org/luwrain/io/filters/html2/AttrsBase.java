
package org.luwrain.io.filters.html2;

import java.util.*;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.luwrain.io.bookdoc.Attributes;
import org.luwrain.io.filters.*;

class AttrsBase
{
    static final String LOG_COMPONENT = "reader";

    private final List<Attributes> attrsStack = new ArrayList<>();

    protected void addAttrs(Element el)
    {
	NullCheck.notNull(el, "el");
	final org.luwrain.io.bookdoc.Attributes attr = new org.luwrain.io.bookdoc.Attributes();
	attr.tagName = el.nodeName();
	final org.jsoup.nodes.Attributes a = el.attributes();
	if (a != null)
	    for(org.jsoup.nodes.Attribute aa: a.asList())
	    {
		final String key = aa.getKey();
		final String value = aa.getValue();
		if (key != null && !key.isEmpty() && value != null)
		    attr.attrMap.put(key, value);
	    }
	if (!attrsStack.isEmpty())
	    attr.parentAttr.addAll(attrsStack);
	attrsStack.add(attr);
    }

    protected void releaseAttrs()
    {
	if (!attrsStack.isEmpty())
	    attrsStack.remove(attrsStack.size() - 1);
    }

    protected Attributes getAttributes()
    {
	return attrsStack.isEmpty()?null:attrsStack.get(attrsStack.size() - 1);
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
