// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.*;

/**
 * The attributes attached to a document node or run.
 *
 * <p>Attributes are represented by a tag name and a map of named values. They
 * may also carry a list of parent attribute sets, which allows checks to
 * inspect inherited attributes.</p>
 */
public final class Attributes
{
    /** The alignment attribute name. */
    static public final String
	ALIGNMENT = "alignment",
	/** The first line indent attribute name. */
	FIRST_LINE_INDENT = "first-line-indent",
	/** The font alignment attribute name. */
	FONT_ALIGNMENT = "font-alignment",
	/** The element identifier attribute name. */
	ID = "id",
	/** The maximum font size attribute name. */
	MAX_FONT_SIZE = "max-font-size",
	/** The minimum font size attribute name. */
	MIN_FONT_SIZE = "min-font-size",
	/** The style attribute name. */
	STYLE = "style";

    /** The tag name associated with these attributes. May be {@code null}. */
    public String tagName = null;
    /** The map of attribute values. */
    public final Map<String, Object> attrMap = new HashMap<>();
    /** The list of parent attribute sets. */
    public final List<Attributes> parentAttr = new ArrayList<>();

    /**
     * Returns the identifier of this attribute set, searching this set first
     * and then walking through parent attribute sets.
     *
     * @return the first identifier found, or {@code null} if there is none
     */
    public String getIdWithParents()
    {
	if (attrMap != null)
	{
	    for(Map.Entry<String, Object> i: attrMap.entrySet())
		if (i.getKey().toLowerCase().equals(ID))
		    return i.getValue().toString();
	}
	if (parentAttr == null)
	    return null;
	final List<Attributes> reversed = new ArrayList<>(parentAttr);
	Collections.reverse(reversed);
	for(Attributes a: reversed)
	    if (a.attrMap != null)
		for(Map.Entry<String, Object> i: a.attrMap.entrySet())
		    if (i.getKey().toLowerCase().equals(ID))
			return i.getValue().toString();
	return null;
    }

    /**
     * Returns all identifiers found in this attribute set and in its parent
     * attribute sets, in the order they are discovered.
     *
     * @return an array of identifiers; never {@code null}
     */
    public String[] getIdsWithParents()
    {
	final List<String> res = new ArrayList<>();
	if (attrMap != null)
	{
	    for(Map.Entry<String, Object> i: attrMap.entrySet())
		if (i.getKey().toLowerCase().equals(ID))
		    res.add(i.getValue().toString());
	}
	if (parentAttr == null)
	    return res.toArray(new String[res.size()]);
	final List<Attributes> reversed = new ArrayList<>(parentAttr);
	Collections.reverse(reversed);
	for(Attributes a: reversed)
	    if (a.attrMap != null)
		for(Map.Entry<String, Object> i: a.attrMap.entrySet())
		    if (i.getKey().toLowerCase().equals(ID))
			res.add(i.getValue().toString());
	return res.toArray(new String[res.size()]);
    }

    /**
     * Checks whether the supplied identifier is present in this attribute set
     * or in any of its parent attribute sets.
     *
     * @param id the identifier to search for; may be {@code null}
     * @return {@code true} if the identifier is found, {@code false} otherwise
     */
    public boolean hasIdWithParents(String id)
    {
	if (attrMap != null)
	{
	    for(Map.Entry<String, Object> i: attrMap.entrySet())
		if (i.getKey().equalsIgnoreCase(ID) && i.getValue().equals(id))
		    return true;
	}
	if (parentAttr == null)
	    return false;
	for(Attributes a: parentAttr)
	    if (a.attrMap != null)
		for(Map.Entry<String, Object> i: a.attrMap.entrySet())
		    if (i.getKey().equalsIgnoreCase(ID) && i.getValue().equals(id))
			return true;
	return false;
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

    /**
     * Returns the string representation of this attribute set without
     * traversing its parent attribute sets.
     *
     * @return the string representation of this attribute set
     */
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
