// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.loaders;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.io.*;
import java.net.*;

//import org.mockito.*;
import okhttp3.mockwebserver.*;
//import static org.mockito.Mockito.*;

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
}

