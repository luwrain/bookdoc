// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class AttributesTest
{
    // ---- Constants ----

    @Test public void constants()
    {
	assertEquals("alignment", Attributes.ALIGNMENT);
	assertEquals("first-line-indent", Attributes.FIRST_LINE_INDENT);
	assertEquals("font-alignment", Attributes.FONT_ALIGNMENT);
	assertEquals("id", Attributes.ID);
	assertEquals("max-font-size", Attributes.MAX_FONT_SIZE);
	assertEquals("min-font-size", Attributes.MIN_FONT_SIZE);
	assertEquals("style", Attributes.STYLE);
    }

    // ---- getIdWithParents ----

    @Test public void getIdWithParentsDirect()
    {
	final Attributes a = new Attributes();
	a.attrMap.put("id", "direct-id");
	assertEquals("direct-id", a.getIdWithParents());
    }

    @Test public void getIdWithParentsCaseInsensitive()
    {
	final Attributes a = new Attributes();
	a.attrMap.put("ID", "case-insensitive-id");
	assertEquals("case-insensitive-id", a.getIdWithParents());
    }

    @Test public void getIdWithParentsFromParent()
    {
	final Attributes parent = new Attributes();
	parent.attrMap.put("id", "parent-id");
	final Attributes child = new Attributes();
	child.parentAttr.add(parent);
	assertEquals("parent-id", child.getIdWithParents());
    }

    @Test public void getIdWithParentsReturnsDirectFirst()
    {
	final Attributes parent = new Attributes();
	parent.attrMap.put("id", "parent-id");
	final Attributes child = new Attributes();
	child.attrMap.put("id", "child-id");
	child.parentAttr.add(parent);
	assertEquals("child-id", child.getIdWithParents());
    }

    @Test public void getIdWithParentsNoId()
    {
	final Attributes a = new Attributes();
	assertNull(a.getIdWithParents());
    }

    @Test public void getIdWithParentsEmptyAttrMap()
    {
	final Attributes a = new Attributes();
	a.tagName = "div";
	assertNull(a.getIdWithParents());
    }

    @Test public void getIdWithParentsMultipleParents()
    {
	final Attributes grandparent = new Attributes();
	grandparent.attrMap.put("id", "gp-id");
	final Attributes parent = new Attributes();
	parent.attrMap.put("id", "p-id");
	parent.parentAttr.add(grandparent);
	final Attributes child = new Attributes();
	child.parentAttr.add(parent);
	assertEquals("p-id", child.getIdWithParents());
    }

    // ---- getIdsWithParents ----

    @Disabled @Test public void getIdsWithParentsCollectsAll()
    {
	final Attributes grandparent = new Attributes();
	grandparent.attrMap.put("id", "gp-id");
	final Attributes parent = new Attributes();
	parent.attrMap.put("id", "p-id");
	parent.parentAttr.add(grandparent);
	final Attributes child = new Attributes();
	child.attrMap.put("id", "c-id");
	child.parentAttr.add(parent);
	final String[] ids = child.getIdsWithParents();
	assertNotNull(ids);
	assertEquals(3, ids.length);
	assertEquals("c-id", ids[0]);
	assertEquals("p-id", ids[1]);
	assertEquals("gp-id", ids[2]);
    }

    @Test public void getIdsWithParentsNoIds()
    {
	final Attributes a = new Attributes();
	final String[] ids = a.getIdsWithParents();
	assertNotNull(ids);
	assertEquals(0, ids.length);
    }

    @Test public void getIdsWithParentsOnlyDirect()
    {
	final Attributes a = new Attributes();
	a.attrMap.put("id", "only-me");
	final String[] ids = a.getIdsWithParents();
	assertNotNull(ids);
	assertEquals(1, ids.length);
	assertEquals("only-me", ids[0]);
    }

    // ---- hasIdWithParents ----

    @Test public void hasIdWithParentsDirect()
    {
	final Attributes a = new Attributes();
	a.attrMap.put("id", "my-id");
	assertTrue(a.hasIdWithParents("my-id"));
	assertFalse(a.hasIdWithParents("other-id"));
    }

    @Test public void hasIdWithParentsCaseInsensitive()
    {
	final Attributes a = new Attributes();
	a.attrMap.put("ID", "my-id");
	assertTrue(a.hasIdWithParents("my-id"));
    }

    @Test public void hasIdWithParentsFromParent()
    {
	final Attributes parent = new Attributes();
	parent.attrMap.put("id", "parent-id");
	final Attributes child = new Attributes();
	child.parentAttr.add(parent);
	assertTrue(child.hasIdWithParents("parent-id"));
    }

    @Disabled @Test public void hasIdWithParentsMultipleLevels()
    {
	final Attributes gp = new Attributes();
	gp.attrMap.put("id", "gp-id");
	final Attributes p = new Attributes();
	p.attrMap.put("id", "p-id");
	p.parentAttr.add(gp);
	final Attributes c = new Attributes();
	c.parentAttr.add(p);
	assertTrue(c.hasIdWithParents("gp-id"));
	assertTrue(c.hasIdWithParents("p-id"));
	assertFalse(c.hasIdWithParents("c-id"));
    }

    // ---- toString ----

    @Test public void toStringEmpty()
    {
	final Attributes a = new Attributes();
	final String s = a.toString();
	assertNotNull(s);
	assertTrue(s.contains("NO_TAG_NAME"));
    }

    @Test public void toStringWithTagName()
    {
	final Attributes a = new Attributes();
	a.tagName = "p";
	final String s = a.toString();
	assertTrue(s.contains("p"));
	assertTrue(s.endsWith(";"));
    }

    @Test public void toStringWithAttrs()
    {
	final Attributes a = new Attributes();
	a.tagName = "a";
	a.attrMap.put("href", "https://example.com");
	a.attrMap.put("id", "link1");
	final String s = a.toString();
	assertTrue(s.contains("a"));
	assertTrue(s.contains("href=https://example.com"));
	assertTrue(s.contains("id=link1"));
    }

    @Test public void toStringWithParents()
    {
	final Attributes parent = new Attributes();
	parent.tagName = "div";
	parent.attrMap.put("class", "wrapper");
	final Attributes child = new Attributes();
	child.tagName = "p";
	child.parentAttr.add(parent);
	final String s = child.toString();
	assertTrue(s.contains("div"));
	assertTrue(s.contains("p"));
	assertTrue(s.contains("wrapper"));
    }

    // ---- toStringWithoutParent ----

    @Test public void toStringWithoutParentOnlyDirect()
    {
	final Attributes parent = new Attributes();
	parent.tagName = "div";
	final Attributes child = new Attributes();
	child.tagName = "p";
	child.attrMap.put("id", "para");
	child.parentAttr.add(parent);
	final String s = child.toStringWithoutParent();
	assertTrue(s.contains("p"));
	assertTrue(s.contains("id=para"));
	assertFalse(s.contains("div"));
    }

    // ---- tagName ----

    @Test public void tagNameDefaultNull()
    {
	final Attributes a = new Attributes();
	assertNull(a.tagName);
    }

    // ---- attrMap default ----

    @Test public void attrMapInitiallyEmpty()
    {
	final Attributes a = new Attributes();
	assertNotNull(a.attrMap);
	assertTrue(a.attrMap.isEmpty());
    }

    // ---- parentAttr default ----

    @Test public void parentAttrInitiallyEmpty()
    {
	final Attributes a = new Attributes();
	assertNotNull(a.parentAttr);
	assertTrue(a.parentAttr.isEmpty());
    }
}
