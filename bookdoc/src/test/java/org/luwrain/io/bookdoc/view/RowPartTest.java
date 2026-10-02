// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import org.junit.*;

import org.luwrain.io.bookdoc.*;

import static org.junit.Assert.*;

public class RowPartTest
{
    @Test public void emptyConstructor()
    {
        final Paragraph para = TestDocFactory.paragraph("something");
        final Run run = para.getRuns().get(0);
        final RowPart part = new RowPart(run);
        assertTrue(part.isEmpty());
        assertEquals("", part.getText());
        assertEquals(0, part.relRowNum);
    }

    @Test public void textConstructor()
    {
        final Paragraph para = TestDocFactory.paragraph("hello world");
        final Run run = para.getRuns().get(0);
        final RowPart part = new RowPart(run, 0, 5, 2);
        assertFalse(part.isEmpty());
        assertEquals("hello", part.getText());
        assertEquals(2, part.relRowNum);
    }

    @Test public void invalidTextConstructor()
    {
        final Paragraph para = TestDocFactory.paragraph("hello world");
        final Run run = para.getRuns().get(0);

        assertThrows(IllegalArgumentException.class, () -> new RowPart(run, -1, 5, 0));
        assertThrows(IllegalArgumentException.class, () -> new RowPart(run, 0, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new RowPart(run, 5, 5, 0));
        assertThrows(IllegalArgumentException.class, () -> new RowPart(run, 0, 5, -1));
    }

    @Test public void onTheSameRow()
    {
        final Paragraph para = TestDocFactory.paragraph("hello world");
        final Run run = para.getRuns().get(0);
        final RowPart a = new RowPart(run, 0, 5, 1);
        final RowPart b = new RowPart(run, 6, 11, 1);
        assertTrue(a.onTheSameRow(b));

        final RowPart c = new RowPart(run, 0, 5, 2);
        assertFalse(a.onTheSameRow(c));

        final Paragraph other = TestDocFactory.paragraph("other");
        final Run otherRun = other.getRuns().get(0);
        final RowPart d = new RowPart(otherRun, 0, 5, 1);
        assertFalse(a.onTheSameRow(d));

        final RowPart empty = new RowPart(run);
        assertFalse(a.onTheSameRow(empty));
        assertFalse(empty.onTheSameRow(a));
    }
}