// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.loaders;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ContentTypesTest
{
    @Test public void constants()
    {
	assertEquals("application/msword", ContentTypes.APP_DOC);
	assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ContentTypes.APP_DOCX);
	assertEquals("application/pdf", ContentTypes.APP_PDF);
	assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ContentTypes.APP_XLSX);
	assertEquals("application/octet-stream", ContentTypes.DATA_BINARY);
	assertEquals("text/html", ContentTypes.TEXT_HTML);
	assertEquals("text/plain", ContentTypes.TEXT_PLAIN);
	assertEquals("content/unknown", ContentTypes.UNKNOWN);
    }

    @Test public void suggestHtml()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals(ContentTypes.TEXT_HTML, ct.suggest("page.html"));
	assertEquals(ContentTypes.TEXT_HTML, ct.suggest("page.htm"));
    }

    @Test public void suggestPlainText()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals(ContentTypes.TEXT_PLAIN, ct.suggest("readme.txt"));
    }

    @Test public void suggestDoc()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals(ContentTypes.APP_DOC, ct.suggest("document.doc"));
    }

    @Test public void suggestDocx()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals(ContentTypes.APP_DOCX, ct.suggest("document.docx"));
    }

    @Test public void suggestPdf()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals(ContentTypes.APP_PDF, ct.suggest("report.pdf"));
    }

    @Test public void suggestXlsx()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals(ContentTypes.APP_XLSX, ct.suggest("data.xlsx"));
    }

    @Test public void suggestBinary()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals(ContentTypes.DATA_BINARY, ct.suggest("file.dat"));
	assertEquals(ContentTypes.DATA_BINARY, ct.suggest("file.raw"));
    }

    @Test public void suggestFb2()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals("application/fb2", ct.suggest("book.fb2"));
    }

    @Test public void suggestXhtml()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals("application/xhtml", ct.suggest("page.xhtml"));
	assertEquals("application/xhtml", ct.suggest("page.xhtm"));
    }

    @Test public void suggestZip()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals("application/zip", ct.suggest("archive.zip"));
    }

    @Test public void suggestPostscript()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals("application/postscript", ct.suggest("document.ps"));
    }

    @Test public void suggestUnknown()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals("", ct.suggest("file.unknown"));
	assertEquals("", ct.suggest("file.xyz"));
    }

    @Test public void suggestNull()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals("", ct.suggest(null));
    }

    @Test public void suggestEmpty()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals("", ct.suggest(""));
    }

    @Test public void suggestCaseInsensitive()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals(ContentTypes.TEXT_HTML, ct.suggest("INDEX.HTML"));
	assertEquals(ContentTypes.TEXT_HTML, ct.suggest("Page.Htm"));
	assertEquals(ContentTypes.TEXT_PLAIN, ct.suggest("README.TXT"));
	assertEquals(ContentTypes.APP_PDF, ct.suggest("REPORT.PDF"));
    }

    @Test public void suggestWithPath()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals(ContentTypes.TEXT_HTML, ct.suggest("/home/user/docs/page.html"));
	assertEquals(ContentTypes.TEXT_HTML, ct.suggest("c:\\Documents\\page.htm"));
    }

    @Test public void suggestMultipleDots()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals(ContentTypes.TEXT_HTML, ct.suggest("archive.tar.html"));
	assertEquals(ContentTypes.TEXT_PLAIN, ct.suggest("backup.2024.txt"));
    }

    @Test public void suggestEachTypeOnce()
    {
	final ContentTypes ct = new ContentTypes();
	// zip
	assertEquals("application/zip", ct.suggest("a.zip"));
	// fb2
	assertEquals("application/fb2", ct.suggest("a.fb2"));
	// xhtml
	assertEquals("application/xhtml", ct.suggest("a.xhtml"));
	assertEquals("application/xhtml", ct.suggest("a.xhtm"));
	// postscript
	assertEquals("application/postscript", ct.suggest("a.ps"));
    }
}
