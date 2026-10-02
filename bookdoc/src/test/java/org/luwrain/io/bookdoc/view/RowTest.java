// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import org.junit.*;

import org.luwrain.io.bookdoc.*;

import static org.junit.Assert.*;

public class RowTest
{
    private RowPart[] parts(Paragraph para, int[][] ranges)
    {
        final RowPart[] res = new RowPart[ranges.length];
        for (int i = 0; i < ranges.length; ++i)
            res[i] = new RowPart(para.getRuns().get(0), ranges[i][0], ranges[i][1], ranges[i][2]);
        return res;
    }

    @Test public void textConcatenation()
    {
        final Paragraph para = TestDocFactory.paragraph("abcdef");
        final RowPart[] parts = parts(para, new int[][] {{0, 3, 0}, {3, 6, 0}});
        final Row row = new Row(parts, 0, 2);
        assertEquals("abcdef", row.getText());
    }

    @Test public void getRunsUnique()
    {
        final Paragraph para = TestDocFactory.paragraph("abcdef");
        final RowPart[] parts = parts(para, new int[][] {{0, 3, 0}, {3, 6, 0}});
        final Row row = new Row(parts, 0, 2);
        final Run[] runs = row.getRuns();
        assertEquals(1, runs.length);
        assertSame(para.getRuns().get(0), runs[0]);
    }

    @Test public void runBeginsAt()
    {
        final Paragraph para = TestDocFactory.paragraph("abcdef");
        final Run run = para.getRuns().get(0);
        final RowPart[] parts = parts(para, new int[][] {{0, 3, 0}, {3, 6, 0}});
        final Row row = new Row(parts, 0, 2);
        assertEquals(0, row.runBeginsAt(run));
    }

    @Test public void singlePartRow()
    {
        final Paragraph para = TestDocFactory.paragraph("abc");
        final RowPart[] parts = parts(para, new int[][] {{0, 3, 0}});
        final Row row = new Row(parts, 0, 1);
        assertEquals(0, row.runBeginsAt(para.getRuns().get(0)));
    }

    @Test public void getRunUnderPos()
    {
        final Paragraph para = TestDocFactory.paragraph("abcdef");
        final Run run = para.getRuns().get(0);
        final RowPart[] parts = parts(para, new int[][] {{0, 3, 0}, {3, 6, 0}});
        final Row row = new Row(parts, 0, 2);
        assertSame(run, row.getRunUnderPos(0));
        assertSame(run, row.getRunUnderPos(5));
    }

    @Test public void invalidConstructor()
    {
        final Paragraph para = TestDocFactory.paragraph("abcdef");
        final RowPart[] parts = parts(para, new int[][] {{0, 3, 0}, {3, 6, 0}});
        assertThrows(IllegalArgumentException.class, () -> new Row(parts, -1, 2));
        assertThrows(IllegalArgumentException.class, () -> new Row(parts, 0, -1));
        assertThrows(IllegalArgumentException.class, () -> new Row(parts, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> new Row(parts, 2, 1));
    }
}