// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import org.junit.jupiter.api.*;

import org.luwrain.io.bookdoc.*;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public class LayoutTest
{
    @Test public void lineCount()
    {
        final Doc doc = new Doc(TestDocFactory.root(
            TestDocFactory.paragraph("first"),
            TestDocFactory.paragraph("second")), null);
        final View view = new View(doc, 10);
        final Layout layout = view.createLayout();
        assertEquals(2, layout.getLineCount());
    }

    @Test public void getLine()
    {
        final Doc doc = new Doc(TestDocFactory.root(TestDocFactory.paragraph("hello")), null);
        final View view = new View(doc, 10);
        final Layout layout = view.createLayout();
        assertEquals("hello", layout.getLine(0));
    }

    @Test public void negativeIndex()
    {
        final Doc doc = new Doc(TestDocFactory.root(TestDocFactory.paragraph("hello")), null);
        final View view = new View(doc, 10);
        final Layout layout = view.createLayout();
        assertThrows(IllegalArgumentException.class, () -> layout.getLine(-1));
    }

    @Test public void tableLineComposition()
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
        final String line = layout.getLine(0);
        assertTrue(line.contains("one"));
        assertTrue(line.contains("two"));
    }
}
