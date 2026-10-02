// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import org.junit.*;

import org.luwrain.io.bookdoc.*;

import static org.junit.Assert.*;

public class ViewTest
{
    @Test public void singleParagraphView()
    {
        final Doc doc = new Doc(TestDocFactory.root(TestDocFactory.paragraph("hello world")), null);
        final View view = new View(doc, 6);

        final Layout layout = view.createLayout();
        assertEquals(2, layout.getLineCount());
        assertEquals("hello", layout.getLine(0));
        assertEquals("world", layout.getLine(1));
    }

    @Test public void multipleParagraphsView()
    {
        final Doc doc = new Doc(TestDocFactory.root(
            TestDocFactory.paragraph("first"),
            TestDocFactory.paragraph("second")), null);
        final View view = new View(doc, 10);

        final Layout layout = view.createLayout();
        assertEquals(2, layout.getLineCount());
        assertEquals("first", layout.getLine(0));
        assertEquals("second", layout.getLine(1));
    }

    @Test public void emptyDocument()
    {
        final Doc doc = new Doc(new Root(java.util.Arrays.asList()), null);
        final View view = new View(doc, 10);

        assertEquals(0, view.createLayout().getLineCount());
        assertTrue(view.getIterator().noContent());
    }

    @Test public void tableLayout()
    {
        final Table table = TestDocFactory.table(
            TestDocFactory.row(
                TestDocFactory.cell(TestDocFactory.paragraph("one")),
                TestDocFactory.cell(TestDocFactory.paragraph("two"))
            )
        );
        final Doc doc = new Doc(TestDocFactory.root(table), null);
        final View view = new View(doc, 20);

        final Layout layout = view.createLayout();
        assertTrue(layout.getLineCount() >= 1);
        assertEquals("onetwo", layout.getLine(0).replaceAll(" ", ""));
    }

    @Test public void getIteratorWithIndex()
    {
        final Doc doc = new Doc(TestDocFactory.root(
            TestDocFactory.paragraph("first"),
            TestDocFactory.paragraph("second")), null);
        final View view = new View(doc, 10);

        final Iterator it = view.getIterator(1);
        assertEquals(1, it.getIndex());
        assertEquals("second", it.getText());
    }

    @Test public void invalidIteratorIndex()
    {
        final Doc doc = new Doc(TestDocFactory.root(TestDocFactory.paragraph("hello")), null);
        final View view = new View(doc, 10);
        assertThrows(IllegalArgumentException.class, () -> view.getIterator(-1));
        assertThrows(IllegalArgumentException.class, () -> view.getIterator(100));
    }

    @Test public void getParagraphLines()
    {
        final Paragraph para = TestDocFactory.paragraph("hello world");
        final String[] lines = View.getParagraphLines(para, 6);
        assertEquals(2, lines.length);
        assertEquals("hello", lines[0]);
        assertEquals("world", lines[1]);
    }

    @Test public void startingRefProperty()
    {
        final Attributes attrs = TestDocFactory.attrsWithId("target");
        final Paragraph para = TestDocFactory.paragraph("hello", attrs);
        final Doc doc = new Doc(TestDocFactory.root(para), null);
        doc.setProperty(Doc.PROP_STARTING_REF, "target");

        final View view = new View(doc, 10);
        assertEquals("0", doc.getProperty(View.DEFAULT_ITERATOR_INDEX_PROPERTY));
    }
}