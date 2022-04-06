
package org.luwrain.io.bookdoc;

import java.util.*;

public final class Attributes
{
    public String tagName = null;
    public final Map<String, Object> attrMap = new HashMap<>();
    public final List<Attributes> parentAttr = new ArrayList<>();

    public String getIdWithParents()
    {
	if (attrMap != null)
	{
	    for(Map.Entry<String, Object> i: attrMap.entrySet())
		if (i.getKey().toLowerCase().equals("id"))
		    return i.getValue().toString();
	}
	if (parentAttr == null)
	    return null;
	final List<Attributes> reversed = new ArrayList<>(parentAttr);
	Collections.reverse(reversed);
	for(Attributes a: reversed)
	    if (a.attrMap != null)
		for(Map.Entry<String, Object> i: a.attrMap.entrySet())
		    if (i.getKey().toLowerCase().equals("id"))
			return i.getValue().toString();
	return null;
    }

        public String[] getIdsWithParents()
    {
	final List<String> res = new ArrayList<>();
	if (attrMap != null)
	{
	    for(Map.Entry<String, Object> i: attrMap.entrySet())
		if (i.getKey().toLowerCase().equals("id"))
		    res.add(i.getValue().toString());
	}
	if (parentAttr == null)
	    return res.toArray(new String[res.size()]);
	final List<Attributes> reversed = new ArrayList<>(parentAttr);
	Collections.reverse(reversed);
	for(Attributes a: reversed)
	    if (a.attrMap != null)
		for(Map.Entry<String, Object> i: a.attrMap.entrySet())
		    if (i.getKey().toLowerCase().equals("id"))
			res.add(i.getValue().toString());
	    return res.toArray(new String[res.size()]);
    }


    @Override public String toString()
    {
	final StringBuilder b = new StringBuilder();
	b.append(toStringWithoutParent());
		if (parentAttr != null)
		{
		    final List<Attributes> reversed = new ArrayList<>(parentAttr);
		    Collections.reverse(reversed);
	    for(Attributes a: reversed)
		b.append(a.toStringWithoutParent());
		}
		return new String(b);
    }

    public String toStringWithoutParent()
    {
	final StringBuilder b = new StringBuilder();
	if (tagName != null && !tagName.isEmpty())
	    b.append(tagName); else
	    b.append("NO_TAG_NAME");
	if (attrMap != null && !attrMap.isEmpty())
	{
	    b.append(": ");
	    for(Map.Entry<String, Object> e: attrMap.entrySet())
		b.append(e.getKey()).append("=").append(e.getValue().toString());
	}
	b.append(";");
	return new String(b);
    }
}
