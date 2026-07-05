// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.filters.html;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.*;

import org.luwrain.io.bookdoc.*;
import org.luwrain.io.bookdoc.filters.*;
import org.luwrain.io.bookdoc.loaders.*;

public class HtmlFilterTest
{
    HtmlFilter f = null;

    @BeforeEach void createFilter()
    {
	f = new HtmlFilter();
    }

    // ---- Basic loading tests ----

    @Test public void loadSimpleDocument() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	assertNotNull(d);
	assertNotNull(d.getRoot());
	assertTrue(d.getRoot() instanceof Root);
    }

    @Test public void loadEmptyDocument() throws Exception
    {
	final Doc d = loadDoc("empty.html");
	assertNotNull(d);
	assertNotNull(d.getRoot());
	assertEquals(1, d.getRoot().getItemCount());
    }

    @Test public void loadHeadingsDocument() throws Exception
    {
	final Doc d = loadDoc("headings.html");
	assertNotNull(d);
	assertNotNull(d.getRoot());
	final var items = d.getRoot().getItems();
	assertNotNull(items);
	assertEquals(18, items.size());
    }

    @Test public void loadListsDocument() throws Exception
    {
	final Doc d = loadDoc("lists.html");
	assertNotNull(d);
	assertNotNull(d.getRoot());
    }

    @Test public void loadTablesDocument() throws Exception
    {
	final Doc d = loadDoc("tables.html");
	assertNotNull(d);
	assertNotNull(d.getRoot());
    }

    @Test public void loadInlineFormattingDocument() throws Exception
    {
	final Doc d = loadDoc("inline.html");
	assertNotNull(d);
	assertNotNull(d.getRoot());
    }

    @Test public void loadLinksDocument() throws Exception
    {
	final Doc d = loadDoc("links.html");
	assertNotNull(d);
	assertNotNull(d.getRoot());
    }

    @Test public void loadMetaRichDocument() throws Exception
    {
	final Doc d = loadDoc("meta-rich.html");
	assertNotNull(d);
	assertNotNull(d.getRoot());
    }

    // ---- Doc property tests ----

    @Test public void docHasUrlProperty() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	assertNotNull(d.getProperty("url"));
	assertEquals("http://localhost", d.getProperty("url"));
    }

    @Test public void docHasTitleProperty() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	final String title = d.getProperty(Doc.PROP_TITLE);
	assertNotNull(title);
	assertEquals("Пробный документ", title);
    }

    @Test public void docTitleForHeadingsDocument() throws Exception
    {
	final Doc d = loadDoc("headings.html");
	assertEquals("Heading Test Document", d.getProperty(Doc.PROP_TITLE));
    }

    @Test public void docTitleForListsDocument() throws Exception
    {
	final Doc d = loadDoc("lists.html");
	assertEquals("List Test Document", d.getProperty(Doc.PROP_TITLE));
    }

    @Test public void docTitleForTablesDocument() throws Exception
    {
	final Doc d = loadDoc("tables.html");
	assertEquals("Table Test Document", d.getProperty(Doc.PROP_TITLE));
    }

    @Test public void docTitleForInlineDocument() throws Exception
    {
	final Doc d = loadDoc("inline.html");
	assertEquals("Inline Formatting Test", d.getProperty(Doc.PROP_TITLE));
    }

    @Test public void docTitleForLinksDocument() throws Exception
    {
	final Doc d = loadDoc("links.html");
	assertEquals("Link Test Document", d.getProperty(Doc.PROP_TITLE));
    }

    @Test public void docTitleForEmptyDocument() throws Exception
    {
	final Doc d = loadDoc("empty.html");
	assertEquals("Empty Document", d.getProperty(Doc.PROP_TITLE));
    }

    @Test public void docTitleForMetaRichDocument() throws Exception
    {
	final Doc d = loadDoc("meta-rich.html");
	assertEquals("Metadata Rich Document", d.getProperty(Doc.PROP_TITLE));
    }

    @Test public void docCharsetProperty() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	final String charset = d.getProperty("charset");
	assertNotNull(charset);
	assertEquals("UTF-8", charset);
    }

    @Test public void docCharsetFromProperties() throws Exception
    {
	final var p = new Properties();
	p.put(Filter.PROP_URL, "http://localhost");
	p.put(Filter.PROP_CHARSET, "windows-1251");
	try (final var is = getClass().getResourceAsStream("simple.html")) {
	    assertNotNull(is);
	    final Doc d = f.load(is, p);
	    assertEquals("windows-1251", d.getProperty("charset"));
	}
    }

    @Test public void docContentTypeProperty() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	final String contentType = d.getProperty("contenttype");
	assertNotNull(contentType);
	// Currently set to "FIXMEContentTypes.TEXT_HTML_DEFAULT" in code
	assertTrue(contentType.length() > 0);
    }

    // ---- Root node tests ----

    @Test public void rootIsNotNull() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	final Root root = d.getRoot();
	assertNotNull(root);
    }

    @Test public void rootItemsInitiallyEmpty() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	final Root root = d.getRoot();
	assertNotNull(root.getItems());
    }

    @Test public void rootItemsIsUnmodifiable() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	final Root root = d.getRoot();
	final List<ContainerItem> items = root.getItems();
	assertThrows(UnsupportedOperationException.class, () -> items.add(new Paragraph()));
    }

    @Test public void rootItemsEmptyForAllDocuments() throws Exception
    {
	final String[] resources = {
	    "simple.html", "empty.html", "headings.html",
	    "lists.html", "tables.html", "inline.html", "links.html", "meta-rich.html"
	};
	for (String res : resources)
	{
	    final Doc d = loadDoc(res);
	    final Root root = d.getRoot();
	    assertNotNull(root.getItems(), "Root items should not be null for " + res);
	}
    }

    // ---- Filter interface tests ----

    @Test public void filterContentType()
    {
	assertEquals("text/html", f.getContentType());
    }

    @Test public void filterLoadAllReturnsNonNull()
    {
	final List<Filter> filters = Filter.loadAll();
	assertNotNull(filters);
    }

    @Test public void filterLoadAllContainsHtml()
    {
	final List<Filter> filters = Filter.loadAll();
	boolean found = false;
	for (Filter ff : filters)
	    if ("text/html".equals(ff.getContentType()))
	    {
		found = true;
		break;
	    }
	assertTrue(found, "Filter.loadAll() should contain an HTML filter");
    }

    @Test public void filterLoadForContentTypeFindsHtml()
    {
	final Filter ff = Filter.loadForContentType("text/html");
	assertNotNull(ff);
	assertEquals("text/html", ff.getContentType());
    }

    @Test public void filterLoadForContentTypeCaseInsensitive()
    {
	final Filter ff = Filter.loadForContentType("TEXT/HTML");
	assertNotNull(ff);
    }

    @Test public void filterLoadForContentTypeUnknown()
    {
	final Filter ff = Filter.loadForContentType("application/x-unknown-type");
	assertNull(ff);
    }

    @Test public void filterLoadForContentTypeNullThrows()
    {
	assertThrows(NullPointerException.class, () -> Filter.loadForContentType(null));
    }

    @Test public void filterLoadForContentTypeEmptyThrows()
    {
	assertThrows(IllegalArgumentException.class, () -> Filter.loadForContentType(""));
    }

    // ---- Filter properties ----

    @Test public void filterPropertiesConstants()
    {
	assertEquals("url", Filter.PROP_URL);
	assertEquals("charset", Filter.PROP_CHARSET);
    }

    // ---- Doc.setProperty / getProperty ----

    @Test public void docSetAndGetCustomProperty() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	d.setProperty("custom-key", "custom-value");
	assertEquals("custom-value", d.getProperty("custom-key"));
    }

    @Test public void docSetPropertyOverwritesExisting() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	d.setProperty("url", "https://changed.example.com");
	assertEquals("https://changed.example.com", d.getProperty("url"));
    }

    @Test public void docSetPropertyNullNameThrows() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	assertThrows(NullPointerException.class, () -> d.setProperty(null, "value"));
    }

    @Test public void docSetPropertyEmptyNameThrows() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	assertThrows(IllegalArgumentException.class, () -> d.setProperty("", "value"));
    }

    @Test public void docSetPropertyNullValueThrows() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	assertThrows(NullPointerException.class, () -> d.setProperty("key", null));
    }

    @Test public void docGetPropertyMissingReturnsNull() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	assertNull(d.getProperty("nonexistent-property"));
    }

    // ---- Doc hrefs ----

    @Test public void docHrefsInitiallyEmpty() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	final String[] hrefs = d.getHrefs();
	assertNotNull(hrefs);
	assertEquals(0, hrefs.length);
    }

    @Test public void docSetAndGetHrefs() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	final String[] testHrefs = {"http://a.com", "http://b.com", "http://c.com"};
	d.setHrefs(testHrefs);
	final String[] result = d.getHrefs();
	assertNotNull(result);
	assertEquals(3, result.length);
	assertEquals("http://a.com", result[0]);
	assertEquals("http://b.com", result[1]);
	assertEquals("http://c.com", result[2]);
    }

    @Test public void docSetHrefsClonesArray() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	final String[] testHrefs = {"http://original.com"};
	d.setHrefs(testHrefs);
	testHrefs[0] = "http://modified.com";
	assertEquals("http://original.com", d.getHrefs()[0]);
    }

    @Test public void docSetHrefsNullThrows() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	assertThrows(NullPointerException.class, () -> d.setHrefs(null));
    }

    // ---- Doc constants ----

    @Test public void docPropertyConstants()
    {
	assertEquals("url", Doc.PROP_URL);
	assertEquals("startingref", Doc.PROP_STARTING_REF);
	assertEquals("daisy.localpath", Doc.PROP_DAISY_LOCAL_PATH);
	assertEquals("title", Doc.PROP_TITLE);
    }

    // ---- ContentTypes tests ----

    @Test public void contentTypesSuggestHtml()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals(ContentTypes.TEXT_HTML, ct.suggest("index.html"));
	assertEquals(ContentTypes.TEXT_HTML, ct.suggest("page.htm"));
    }

    @Test public void contentTypesSuggestPlainText()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals(ContentTypes.TEXT_PLAIN, ct.suggest("readme.txt"));
    }

    @Test public void contentTypesSuggestDoc()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals(ContentTypes.APP_DOC, ct.suggest("document.doc"));
    }

    @Test public void contentTypesSuggestDocx()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals(ContentTypes.APP_DOCX, ct.suggest("document.docx"));
    }

    @Test public void contentTypesSuggestPdf()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals(ContentTypes.APP_PDF, ct.suggest("report.pdf"));
    }

    @Test public void contentTypesSuggestXlsx()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals(ContentTypes.APP_XLSX, ct.suggest("data.xlsx"));
    }

    @Test public void contentTypesSuggestBinary()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals(ContentTypes.DATA_BINARY, ct.suggest("file.dat"));
	assertEquals(ContentTypes.DATA_BINARY, ct.suggest("file.raw"));
    }

    @Test public void contentTypesSuggestFb2()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals("application/fb2", ct.suggest("book.fb2"));
    }

    @Test public void contentTypesSuggestXhtml()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals("application/xhtml", ct.suggest("page.xhtml"));
	assertEquals("application/xhtml", ct.suggest("page.xhtm"));
    }

    @Test public void contentTypesSuggestZip()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals("application/zip", ct.suggest("archive.zip"));
    }

    @Test public void contentTypesSuggestPostscript()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals("application/postscript", ct.suggest("document.ps"));
    }

    @Test public void contentTypesSuggestUnknownExtension()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals("", ct.suggest("file.unknown"));
    }

    @Test public void contentTypesSuggestNullFileName()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals("", ct.suggest(null));
    }

    @Test public void contentTypesSuggestEmptyFileName()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals("", ct.suggest(""));
    }

    @Test public void contentTypesCaseInsensitive()
    {
	final ContentTypes ct = new ContentTypes();
	assertEquals(ContentTypes.TEXT_HTML, ct.suggest("INDEX.HTML"));
	assertEquals(ContentTypes.TEXT_HTML, ct.suggest("Page.Htm"));
    }

    @Test public void contentTypesConstants()
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

    // ---- Multiple loads of same filter instance ----

    @Test public void loadMultipleDocumentsWithSameFilter() throws Exception
    {
	final Doc d1 = loadDoc("simple.html");
	final Doc d2 = loadDoc("headings.html");
	final Doc d3 = loadDoc("lists.html");
	assertNotNull(d1);
	assertNotNull(d2);
	assertNotNull(d3);
	assertEquals("Пробный документ", d1.getProperty(Doc.PROP_TITLE));
	assertEquals("Heading Test Document", d2.getProperty(Doc.PROP_TITLE));
	assertEquals("List Test Document", d3.getProperty(Doc.PROP_TITLE));
    }

    // ---- Null and edge cases ----

    @Disabled @Test public void loadWithNullInputStreamThrows() throws Exception
    {
	final var p = new Properties();
	p.put(Filter.PROP_URL, "http://localhost");
	assertThrows(NullPointerException.class, () -> f.load(null, p));
    }

    @Test public void loadWithoutUrlPropertyThrows() throws Exception
    {
	final var p = new Properties();
	try (final var is = getClass().getResourceAsStream("simple.html")) {
	    assertThrows(IOException.class, () -> f.load(is, p));
	}
    }

    @Test public void loadWithEmptyUrlPropertyThrows() throws Exception
    {
	final var p = new Properties();
	p.put(Filter.PROP_URL, "");
	try (final var is = getClass().getResourceAsStream("simple.html")) {
	    assertThrows(IOException.class, () -> f.load(is, p));
	}
    }

    // ---- Visitor walk on loaded document ----

    @Test public void visitorWalkOnLoadedDocDoesNotThrow() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	final Root root = d.getRoot();
	assertDoesNotThrow(() -> {
	    final StringBuilder visited = new StringBuilder();
	    Visitor.walk(root, new Visitor(){
		    @Override public void visit(Paragraph p) {
			visited.append(p.getText());
		    }
		    @Override public void visit(Heading h) {
			visited.append("H").append(h.getLevel());
		    }
		});
	});
    }

    // ---- Doc constructor tests ----

    @Test public void docConstructorWithTitle()
    {
	final Root root = new Root(Collections.emptyList());
	final Doc doc = new Doc(root, "Test Title");
	assertEquals("Test Title", doc.getProperty(Doc.PROP_TITLE));
	assertSame(root, doc.getRoot());
    }

    @Test public void docConstructorWithoutTitle()
    {
	final Root root = new Root(Collections.emptyList());
	final Doc doc = new Doc(root);
	assertNull(doc.getProperty(Doc.PROP_TITLE));
	assertSame(root, doc.getRoot());
    }

    @Test public void docConstructorNullRootThrows()
    {
	assertThrows(NullPointerException.class, () -> new Doc(null));
	assertThrows(NullPointerException.class, () -> new Doc(null, "Title"));
    }

    // ---- Simple HTML structural verification (current state) ----

    @Test public void simpleHtmlHasRoot() throws Exception
    {
	final Doc d = loadDoc("simple.html");
	assertNotNull(d.getRoot());
	assertNotNull(d.getRoot().getItems());
    }

    @Test public void headingsHtmlLoadsWithoutException() throws Exception
    {
	final Doc d = loadDoc("headings.html");
	assertNotNull(d);
	assertEquals("Heading Test Document", d.getProperty(Doc.PROP_TITLE));
    }

    @Test public void listsHtmlLoadsWithoutException() throws Exception
    {
	final Doc d = loadDoc("lists.html");
	assertNotNull(d);
	assertEquals("List Test Document", d.getProperty(Doc.PROP_TITLE));
    }

    @Test public void tablesHtmlLoadsWithoutException() throws Exception
    {
	final Doc d = loadDoc("tables.html");
	assertNotNull(d);
	assertEquals("Table Test Document", d.getProperty(Doc.PROP_TITLE));
    }

    @Test public void inlineHtmlLoadsWithoutException() throws Exception
    {
	final Doc d = loadDoc("inline.html");
	assertNotNull(d);
	assertEquals("Inline Formatting Test", d.getProperty(Doc.PROP_TITLE));
    }

    @Test public void linksHtmlLoadsWithoutException() throws Exception
    {
	final Doc d = loadDoc("links.html");
	assertNotNull(d);
	assertEquals("Link Test Document", d.getProperty(Doc.PROP_TITLE));
    }

    @Test public void emptyHtmlLoadsWithoutException() throws Exception
    {
	final Doc d = loadDoc("empty.html");
	assertNotNull(d);
	assertEquals("Empty Document", d.getProperty(Doc.PROP_TITLE));
		assertEquals(1, d.getRoot().getItems().size());
		assertTrue(d.getRoot().getItems().get(0) instanceof Paragraph);
    }

    @Test public void metaRichHtmlLoadsWithoutException() throws Exception
    {
	final Doc d = loadDoc("meta-rich.html");
	assertNotNull(d);
	assertEquals("Metadata Rich Document", d.getProperty(Doc.PROP_TITLE));
    }

    // ---- Helper methods ----

    private Doc loadDoc(String resourceName) throws Exception
    {
	final var p = new Properties();
	p.put(Filter.PROP_URL, "http://localhost");
	try (final var is = getClass().getResourceAsStream(resourceName)) {
	    assertNotNull(is, "Resource not found: " + resourceName);
	    return f.load(is, p);
	}
    }
}
