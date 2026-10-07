// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.loaders;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.io.*;
import java.net.*;

import okhttp3.mockwebserver.*;

import org.luwrain.io.bookdoc.*;


public class LoaderImplTest
{
    static private final String SIMPLE =
    "<html>\n" +
    "  <head>\n" +
    "    <title>Simple test</title>\n" +
    "  </head>\n" +
    "  <body>\n" +
    "    <p>Simple test text</p>\n" +
    "  </body>\n" +
    "</html>\n";

    private MockWebServer server;

    @BeforeEach public void startServer() throws IOException
    {
	server = new MockWebServer();
	server.start();
    }

    @AfterEach public void stopServer() throws IOException
    {
	server.shutdown();
    }

    @Test public void simpleDoc() throws IOException
    {
	server.enqueue(new MockResponse()
		       .setResponseCode(200)
		       .setHeader("Content-Length", SIMPLE.length())
		       .setHeader("Content-Type", "text/html")
		       .setBody(SIMPLE));
	final var loader = new LoaderImpl(server.url("/test.html"), null);
	final var doc = loader.load();
	assertNotNull(doc);
	final var root = doc.getRoot();
	assertNotNull(root);
	final var items = root.getItems();
	assertNotNull(items);
	assertEquals(3, items.size(), "Incorrect number of items in the root container");
	final ContainerItem
	i0 = items.get(0),
	i1 = items.get(1),
	i2 = items.get(2);
	assertEquals(Paragraph.class, i0.getClass());
	assertEquals(Paragraph.class, i1.getClass());
	assertEquals(Paragraph.class, i2.getClass());
	final Paragraph
	p0 = (Paragraph)i0,
	p1 = (Paragraph)i1,
	p2 = (Paragraph)i2;
	assertEquals(" ", p0.getText());
	assertEquals("Simple test text", p1.getText());
	assertEquals(" ", p2.getText());
    }

    @Test public void charsetFromResponseHeader() throws IOException
    {
	server.enqueue(new MockResponse()
		       .setResponseCode(200)
		       .setHeader("Content-Type", "text/html; charset=windows-1251")
		       .setBody(SIMPLE));
	final var loader = new LoaderImpl(server.url("/test.html"), null);
	final var doc = loader.load();
	assertNotNull(doc);
	assertEquals("windows-1251", doc.getProperty("charset"));
    }

    @Test public void charsetFromRequestedContentType() throws IOException
    {
	server.enqueue(new MockResponse()
		       .setResponseCode(200)
		       .setHeader("Content-Type", "text/html; charset=utf-8")
		       .setBody(SIMPLE));
	final var loader = new LoaderImpl(server.url("/test.html"), "text/html; charset=windows-1251");
	final var doc = loader.load();
	assertNotNull(doc);
	assertEquals("windows-1251", doc.getProperty("charset"));
    }

    @Test public void requestedCharsetWinsOverResponseCharset() throws IOException
    {
	server.enqueue(new MockResponse()
		       .setResponseCode(200)
		       .setHeader("Content-Type", "text/html; charset=utf-8")
		       .setBody(SIMPLE));
	final var loader = new LoaderImpl(server.url("/test.html"), "text/html; charset=iso-8859-1");
	final var doc = loader.load();
	assertNotNull(doc);
	assertEquals("iso-8859-1", doc.getProperty("charset"));
    }

    @Test public void defaultCharsetWhenNoneSpecified() throws IOException
    {
	server.enqueue(new MockResponse()
		       .setResponseCode(200)
		       .setHeader("Content-Type", "text/html")
		       .setBody(SIMPLE));
	final var loader = new LoaderImpl(server.url("/test.html"), null);
	final var doc = loader.load();
	assertNotNull(doc);
	assertEquals("UTF-8", doc.getProperty("charset"));
    }

    @Test public void requestedContentTypeWinsOverResponseContentType() throws IOException
    {
	server.enqueue(new MockResponse()
		       .setResponseCode(200)
		       .setHeader("Content-Type", "application/octet-stream")
		       .setBody(SIMPLE));
	final var loader = new LoaderImpl(server.url("/test.html"), "text/html");
	final var doc = loader.load();
	assertNotNull(doc);
	assertEquals("text/html", doc.getProperty("contenttype"));
    }

    @Test public void responseUrlTracksRedirect() throws IOException
    {
	final String redirectTarget = server.url("/final.html").toString();
	server.enqueue(new MockResponse()
		       .setResponseCode(302)
		       .setHeader("Location", redirectTarget));
	server.enqueue(new MockResponse()
		       .setResponseCode(200)
		       .setHeader("Content-Type", "text/html")
		       .setBody(SIMPLE));
	final var loader = new LoaderImpl(server.url("/start.html"), null);
	final var doc = loader.load();
	assertNotNull(doc);
	assertEquals(redirectTarget, doc.getProperty("url"));
    }

    @Test public void responseUrlWithoutRedirect() throws IOException
    {
	final String url = server.url("/test.html").toString();
	server.enqueue(new MockResponse()
		       .setResponseCode(200)
		       .setHeader("Content-Type", "text/html")
		       .setBody(SIMPLE));
	final var loader = new LoaderImpl(server.url("/test.html"), null);
	final var doc = loader.load();
	assertNotNull(doc);
	assertEquals(url, doc.getProperty("url"));
    }

    @Test public void autodetectionByTika() throws IOException
    {
	server.enqueue(new MockResponse()
		       .setResponseCode(200)
		       .setBody(SIMPLE));
	final var loader = new LoaderImpl(server.url("/test.html"), null);
	final var doc = loader.load();
	assertNotNull(doc);
	assertEquals("text/html", doc.getProperty("contenttype"));
    }

    @Test public void invalidResponseCodeThrows() throws IOException
    {
	server.enqueue(new MockResponse()
		       .setResponseCode(404)
		       .setBody("Not found"));
	final var loader = new LoaderImpl(server.url("/test.html"), null);
	assertThrows(IOException.class, () -> loader.load());
    }

    @Test public void noSuitableFilterThrows() throws IOException
    {
	server.enqueue(new MockResponse()
		       .setResponseCode(200)
		       .setHeader("Content-Type", "application/x-unknown-type")
		       .setBody("some data"));
	final var loader = new LoaderImpl(server.url("/test.unknown"), null);
	assertThrows(IOException.class, () -> loader.load());
    }
}
