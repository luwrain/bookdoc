// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import org.junit.jupiter.api.*;

import org.luwrain.io.bookdoc.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.luwrain.io.bookdoc.view.TestDocFactory.*;

public class IteratorTest
{
    private View twoParagraphView()
    {
        final Doc doc = new Doc(root(
				     paragraph("first"),
				     paragraph("second")), null);
	doc.getRoot().getItems().get(0).setContainer(doc.getRoot());
		doc.getRoot().getItems().get(1).setContainer(doc.getRoot());
        return new View(doc, 10);
    }

    @Test public void noContent()
    {
        final Doc doc = new Doc(new Root(java.util.Arrays.asList()), null);
        final View view = new View(doc, 10);
        final Iterator it = view.getIterator();
	assertNotNull(it);
        assertTrue(it.noContent());
        assertEquals(-1, it.getIndex());
        assertEquals(0, it.getCount());
    }

    @Test public void basicNavigation()
    {
        final Iterator it = twoParagraphView().getIterator();
        assertEquals(0, it.getIndex());
        assertEquals(2, it.getCount());
        assertTrue(it.canMoveNext());
        assertFalse(it.canMovePrev());
	//Making a step
        assertTrue(it.moveNext());
        assertEquals(1, it.getIndex());
        assertFalse(it.canMoveNext());
        assertTrue(it.canMovePrev());
	//Backstep
        assertTrue(it.movePrev());
        assertEquals(0, it.getIndex());
    }

    @Test public void moveBeginAndEnd()
    {
        final Iterator it = twoParagraphView().getIterator();
        it.moveEnd();
        assertEquals(1, it.getIndex());
        it.moveBeginning();
        assertEquals(0, it.getIndex());
    }

    @Test public void getTextAndParagraph()
    {
        final Iterator it = twoParagraphView().getIterator();
        assertEquals("first", it.getText());
        assertNotNull(it.getParagraph());
        it.moveNext();
        assertEquals("second", it.getText());
        assertNotNull(it.getParagraph());
    }

    @Test public void getContainer()
    {
        final Iterator it = twoParagraphView().getIterator();
        assertNotNull(it.getContainer(), "The iterator provides non-null node");
    }

    @Test public void indexInParagraph()
    {
        final Doc doc = new Doc(TestDocFactory.root(
            TestDocFactory.paragraph("hello world")), null);
        final View view = new View(doc, 6);
        final Iterator it = view.getIterator();

        assertEquals(0, it.getIndexInParagraph());
        assertTrue(it.isParagraphBeginning());

        it.moveNext();
        assertEquals(1, it.getIndexInParagraph());
        assertFalse(it.isParagraphBeginning());
    }

    @Test public void coversPos()
    {
        final Iterator it = twoParagraphView().getIterator();
        assertTrue(it.coversPos(0, 0));
        assertFalse(it.coversPos(0, 1));
    }

    @Test public void getRunUnderPos()
    {
        final Iterator it = twoParagraphView().getIterator();
        assertNotNull(it.getRunUnderPos(0));
        assertThrows(IllegalArgumentException.class, () -> it.getRunUnderPos(-1));
    }

    @Test public void searchForward()
    {
        final Iterator it = twoParagraphView().getIterator();
        final boolean found = it.searchForward((node, para, row) -> row.getText().equals("second"));
        assertTrue(found);
        assertEquals(1, it.getIndex());
    }

    @Test public void searchBackward()
    {
        final Iterator it = twoParagraphView().getIterator();
        it.moveEnd();
        final boolean found = it.searchBackward((node, para, row) -> row.getText().equals("first"));
        assertTrue(found);
        assertEquals(0, it.getIndex());
    }

    @Test public void searchNotFound()
    {
        final Iterator it = twoParagraphView().getIterator();
        final boolean found = it.searchForward((node, para, row) -> row.getText().equals("nonexistent"));
        assertFalse(found);
        assertEquals(0, it.getIndex());
    }

    @Test public void cloneAndEquals()
    {
        final Iterator it = twoParagraphView().getIterator();
        it.moveNext();
        final Iterator clone = it.clone();
        assertEquals(it, clone);
        assertEquals(it.getIndex(), clone.getIndex());
    }

    @Test public void hasRunOnRow()
    {
        final Iterator it = twoParagraphView().getIterator();
        final Run[] runs = it.getRuns();
        assertTrue(runs.length > 0);
        assertTrue(it.hasRunOnRow(runs[0]));
    }
}
