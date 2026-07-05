// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.filters.html;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

public class ContextTest
{
    // ---- anchorStack ----

    @Test public void anchorStackInitiallyEmpty()
    {
	final Context ctx = new Context();
	assertNotNull(ctx.anchorStack);
	assertTrue(ctx.anchorStack.isEmpty());
    }

    @Test public void getActualAnchorReturnsNullWhenEmpty()
    {
	final Context ctx = new Context();
	assertNull(ctx.getActualAnchor());
    }

    @Test public void getActualAnchorReturnsLastPushed()
    {
	final Context ctx = new Context();
	ctx.anchorStack.add("https://example.com");
	assertEquals("https://example.com", ctx.getActualAnchor());
    }

    @Test public void getActualAnchorWithMultipleAnchors()
    {
	final Context ctx = new Context();
	ctx.anchorStack.add("https://first.com");
	ctx.anchorStack.add("https://second.com");
	assertEquals("https://second.com", ctx.getActualAnchor());
    }

    @Test public void getActualAnchorAfterPoll()
    {
	final Context ctx = new Context();
	ctx.anchorStack.add("https://first.com");
	ctx.anchorStack.add("https://second.com");
	ctx.anchorStack.pollLast();
	assertEquals("https://first.com", ctx.getActualAnchor());
    }

    // ---- allAnchors ----

    @Test public void allAnchorsInitiallyEmpty()
    {
	final Context ctx = new Context();
	assertNotNull(ctx.allAnchors);
	assertTrue(ctx.allAnchors.isEmpty());
    }

    @Test public void allAnchorsCollectsValues()
    {
	final Context ctx = new Context();
	ctx.allAnchors.add("https://example.com");
	ctx.allAnchors.add("https://test.org");
	assertEquals(2, ctx.allAnchors.size());
	assertTrue(ctx.allAnchors.contains("https://example.com"));
	assertTrue(ctx.allAnchors.contains("https://test.org"));
    }

    // ---- attributesStack ----

    @Test public void attributesStackInitiallyEmpty()
    {
	final Context ctx = new Context();
	assertNotNull(ctx.attributesStack);
	assertTrue(ctx.attributesStack.isEmpty());
    }

    @Test public void addAttributesSingleElement()
    {
	final Context ctx = new Context();
	final String html = "<div id=\"wrapper\" class=\"main\">Text</div>";
	final Document doc = Jsoup.parse(html);
	final Element div = doc.select("div").first();
	ctx.addAttributes(div);
	assertEquals(1, ctx.attributesStack.size());
	final org.luwrain.io.bookdoc.Attributes attr = ctx.attributesStack.getFirst();
	assertEquals("div", attr.tagName);
	assertEquals("wrapper", attr.attrMap.get("id"));
	assertEquals("main", attr.attrMap.get("class"));
    }

    @Test public void addAttributesNestedElements()
    {
	final Context ctx = new Context();
	final String html = "<div id=\"outer\"><p id=\"inner\">Text</p></div>";
	final Document doc = Jsoup.parse(html);
	final Element div = doc.select("div").first();
	final Element p = doc.select("p").first();
	ctx.addAttributes(div);
	ctx.addAttributes(p);
	assertEquals(2, ctx.attributesStack.size());
	final org.luwrain.io.bookdoc.Attributes inner = ctx.attributesStack.get(1);
	assertEquals("p", inner.tagName);
	assertEquals(1, inner.parentAttr.size());
	assertEquals("div", inner.parentAttr.get(0).tagName);
    }

    @Test public void releaseAttributesRemovesTop()
    {
	final Context ctx = new Context();
	final String html = "<div id=\"a\"><span id=\"b\">Text</span></div>";
	final Document doc = Jsoup.parse(html);
	final Element div = doc.select("div").first();
	final Element span = doc.select("span").first();
	ctx.addAttributes(div);
	ctx.addAttributes(span);
	assertEquals(2, ctx.attributesStack.size());
	ctx.releaseAttributes();
	assertEquals(1, ctx.attributesStack.size());
	assertEquals("div", ctx.attributesStack.getFirst().tagName);
    }

    @Test public void releaseAttributesOnEmptyStackDoesNotThrow()
    {
	final Context ctx = new Context();
	assertDoesNotThrow(() -> ctx.releaseAttributes());
    }

    @Test public void getAttributesReturnsNull()
    {
	final Context ctx = new Context();
	// Context.getAttributes() always returns null in current implementation
	assertNull(ctx.getAttributes());
    }

    // ---- getText ----

    @Test public void addAttributesPreservesJsoupAttributes()
    {
	final Context ctx = new Context();
	final String html = "<a href=\"https://luwrain.org\" id=\"link1\" class=\"external\">Link</a>";
	final Document doc = Jsoup.parse(html);
	final Element a = doc.select("a").first();
	ctx.addAttributes(a);
	assertEquals(1, ctx.attributesStack.size());
	final org.luwrain.io.bookdoc.Attributes attr = ctx.attributesStack.getFirst();
	assertEquals("a", attr.tagName);
	assertEquals("https://luwrain.org", attr.attrMap.get("href"));
	assertEquals("link1", attr.attrMap.get("id"));
	assertEquals("external", attr.attrMap.get("class"));
    }

    @Test public void addAttributesElementWithoutAttributes()
    {
	final Context ctx = new Context();
	final String html = "<br>";
	final Document doc = Jsoup.parse(html);
	final Element br = doc.select("br").first();
	ctx.addAttributes(br);
	assertEquals(1, ctx.attributesStack.size());
	final org.luwrain.io.bookdoc.Attributes attr = ctx.attributesStack.getFirst();
	assertEquals("br", attr.tagName);
	assertTrue(attr.attrMap.isEmpty());
    }

    @Test public void addAttributesDeepNesting()
    {
	final Context ctx = new Context();
	final String html = "<html><body><section><div><p>Deep</p></div></section></body></html>";
	final Document doc = Jsoup.parse(html);
	final Element p = doc.select("p").first();
	// Add from outermost to innermost
	ctx.addAttributes(doc.select("html").first());
	ctx.addAttributes(doc.select("body").first());
	ctx.addAttributes(doc.select("section").first());
	ctx.addAttributes(doc.select("div").first());
	ctx.addAttributes(p);
	assertEquals(5, ctx.attributesStack.size());
	final org.luwrain.io.bookdoc.Attributes inner = ctx.attributesStack.getLast();
	assertEquals("p", inner.tagName);
	assertEquals(4, inner.parentAttr.size());
	assertEquals("html", inner.parentAttr.get(0).tagName);
	assertEquals("body", inner.parentAttr.get(1).tagName);
	assertEquals("section", inner.parentAttr.get(2).tagName);
	assertEquals("div", inner.parentAttr.get(3).tagName);
    }
}
