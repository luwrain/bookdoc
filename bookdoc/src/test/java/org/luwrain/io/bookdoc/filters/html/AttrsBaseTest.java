// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.filters.html;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

public class AttrsBaseTest
{
    // ---- collectMeta tests ----

    @Test public void collectMetaFromHeadWithMetaTags()
    {
	final String html = "<html><head>"
	    + "<meta name=\"author\" content=\"Test Author\">"
	    + "<meta name=\"keywords\" content=\"java,html,test\">"
	    + "</head><body></body></html>";
	final Document doc = Jsoup.parse(html);
	final Map<String, String> meta = new HashMap<>();
	AttrsBase.collectMeta(doc.head(), meta);
	assertEquals(2, meta.size());
	assertEquals("Test Author", meta.get("author"));
	assertEquals("java,html,test", meta.get("keywords"));
    }

    @Test public void collectMetaFromEmptyHead()
    {
	final String html = "<html><head></head><body></body></html>";
	final Document doc = Jsoup.parse(html);
	final Map<String, String> meta = new HashMap<>();
	AttrsBase.collectMeta(doc.head(), meta);
	assertTrue(meta.isEmpty());
    }

    @Test public void collectMetaSkipsMetaWithoutName()
    {
	final String html = "<html><head>"
	    + "<meta charset=\"utf-8\">"
	    + "<meta name=\"author\" content=\"John\">"
	    + "</head><body></body></html>";
	final Document doc = Jsoup.parse(html);
	final Map<String, String> meta = new HashMap<>();
	AttrsBase.collectMeta(doc.head(), meta);
	assertEquals(1, meta.size());
	assertEquals("John", meta.get("author"));
    }

    @Disabled @Test public void collectMetaSkipsMetaWithoutContent()
    {
	final String html = "<html><head>"
	    + "<meta name=\"empty\">"
	    + "<meta name=\"author\" content=\"Jane\">"
	    + "</head><body></body></html>";
	final Document doc = Jsoup.parse(html);
	final Map<String, String> meta = new HashMap<>();
	AttrsBase.collectMeta(doc.head(), meta);
	assertEquals(1, meta.size());
	assertEquals("Jane", meta.get("author"));
    }

    @Test public void collectMetaNullElementThrows()
    {
	final Map<String, String> meta = new HashMap<>();
	assertThrows(NullPointerException.class, () -> AttrsBase.collectMeta(null, meta));
    }

    @Test public void collectMetaNullMapThrows()
    {
	final String html = "<html><head></head></html>";
	final Document doc = Jsoup.parse(html);
	assertThrows(NullPointerException.class, () -> AttrsBase.collectMeta(doc.head(), null));
    }

    @Test public void collectMetaDeeplyNested()
    {
	final String html = "<html><head>"
	    + "<noscript><meta name=\"noscript-meta\" content=\"noscript-value\"></noscript>"
	    + "</head><body></body></html>";
	final Document doc = Jsoup.parse(html);
	final Map<String, String> meta = new HashMap<>();
	AttrsBase.collectMeta(doc.head(), meta);
	assertEquals(1, meta.size());
	assertEquals("noscript-value", meta.get("noscript-meta"));
    }

    // ---- addAttrs / releaseAttrs / getAttributes tests ----

    @Test public void addAttrsSingleElement()
    {
	final String html = "<p id=\"para1\" class=\"intro\">Text</p>";
	final Document doc = Jsoup.parse(html);
	final Element p = doc.select("p").first();
	final TestAttrsBase base = new TestAttrsBase();
	base.addAttrs(p);
	final org.luwrain.io.bookdoc.Attributes attr = base.getAttributes();
	assertNotNull(attr);
	assertEquals("p", attr.tagName);
	assertEquals("para1", attr.attrMap.get("id"));
	assertEquals("intro", attr.attrMap.get("class"));
    }

    @Test public void addAttrsMultipleElementsNested()
    {
	final String html = "<div id=\"outer\"><p id=\"inner\">Text</p></div>";
	final Document doc = Jsoup.parse(html);
	final Element div = doc.select("div").first();
	final Element p = doc.select("p").first();
	final TestAttrsBase base = new TestAttrsBase();
	base.addAttrs(div);
	base.addAttrs(p);
	final org.luwrain.io.bookdoc.Attributes inner = base.getAttributes();
	assertNotNull(inner);
	assertEquals("p", inner.tagName);
	assertEquals("inner", inner.attrMap.get("id"));
	// Should have parent attributes
	assertEquals(1, inner.parentAttr.size());
	assertEquals("div", inner.parentAttr.get(0).tagName);
	assertEquals("outer", inner.parentAttr.get(0).attrMap.get("id"));
    }

    @Test public void releaseAttrsRestoresPrevious()
    {
	final String html = "<div id=\"outer\"><p id=\"inner\">Text</p></div>";
	final Document doc = Jsoup.parse(html);
	final Element div = doc.select("div").first();
	final Element p = doc.select("p").first();
	final TestAttrsBase base = new TestAttrsBase();
	base.addAttrs(div);
	base.addAttrs(p);
	base.releaseAttrs();
	final org.luwrain.io.bookdoc.Attributes attr = base.getAttributes();
	assertNotNull(attr);
	assertEquals("div", attr.tagName);
	assertEquals("outer", attr.attrMap.get("id"));
    }

    @Test public void releaseAttrsOnEmptyStackDoesNotThrow()
    {
	final TestAttrsBase base = new TestAttrsBase();
	assertDoesNotThrow(() -> base.releaseAttrs());
    }

    @Test public void getAttributesOnEmptyStackReturnsNull()
    {
	final TestAttrsBase base = new TestAttrsBase();
	assertNull(base.getAttributes());
    }

    @Test public void addAttrsPreservesPreviousParentAttr()
    {
	final String html = "<section id=\"s1\"><div id=\"d1\"><p id=\"p1\">Text</p></div></section>";
	final Document doc = Jsoup.parse(html);
	final Element section = doc.select("section").first();
	final Element div = doc.select("div").first();
	final Element p = doc.select("p").first();
	final TestAttrsBase base = new TestAttrsBase();
	base.addAttrs(section);
	base.addAttrs(div);
	base.addAttrs(p);
	final org.luwrain.io.bookdoc.Attributes inner = base.getAttributes();
	assertNotNull(inner);
	assertEquals("p", inner.tagName);
	assertEquals(2, inner.parentAttr.size());
	// parentAttr is added in order: first section, then div
	assertEquals("section", inner.parentAttr.get(0).tagName);
	assertEquals("div", inner.parentAttr.get(1).tagName);
    }

    // ---- Test helper class that exposes package-private methods ----

    static class TestAttrsBase extends AttrsBase
    {
	@Override public void addAttrs(Element el) { super.addAttrs(el); }
	@Override public void releaseAttrs() { super.releaseAttrs(); }
	@Override public org.luwrain.io.bookdoc.Attributes getAttributes() { return super.getAttributes(); }
    }
}
