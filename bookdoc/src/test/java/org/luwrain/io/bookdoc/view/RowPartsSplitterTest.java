// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import java.util.*;

import org.junit.*;

import org.luwrain.io.bookdoc.*;

import static org.junit.Assert.*;

public class RowPartsSplitterTest
{
    @Test public void singleLine()
    {
        final Paragraph para = TestDocFactory.paragraph("hello");
        final Run run = para.getRuns().get(0);
        final RowPartsSplitter splitter = new RowPartsSplitter();
        splitter.onRun(run, "hello", 0, 5, 10);
        assertEquals(1, splitter.res.size());
        assertEquals("hello", splitter.res.get(0).getText());
        assertEquals(0, splitter.res.get(0).relRowNum);
    }

    @Test public void twoLinesByWordBoundary()
    {
        final Paragraph para = TestDocFactory.paragraph("hello world");
        final Run run = para.getRuns().get(0);
        final RowPartsSplitter splitter = new RowPartsSplitter();
        splitter.onRun(run, "hello world", 0, 11, 6);
        assertEquals(2, splitter.res.size());
        assertEquals("hello", splitter.res.get(0).getText());
        assertEquals("world", splitter.res.get(1).getText());
        assertEquals(0, splitter.res.get(0).relRowNum);
        assertEquals(1, splitter.res.get(1).relRowNum);
    }

    @Test public void longWordSplit()
    {
        final Paragraph para = TestDocFactory.paragraph("abcdefghij");
        final Run run = para.getRuns().get(0);
        final RowPartsSplitter splitter = new RowPartsSplitter();
        splitter.onRun(run, "abcdefghij", 0, 10, 4);
        assertEquals(3, splitter.res.size());
        assertEquals("abcd", splitter.res.get(0).getText());
        assertEquals("efgh", splitter.res.get(1).getText());
        assertEquals("ij", splitter.res.get(2).getText());
    }

    @Test public void leadingSpacesAtBreak()
    {
        final Paragraph para = TestDocFactory.paragraph("hello world");
        final Run run = para.getRuns().get(0);
        final RowPartsSplitter splitter = new RowPartsSplitter();
        splitter.onRun(run, "hello world", 0, 11, 5);
        assertEquals(2, splitter.res.size());
        assertEquals("hello", splitter.res.get(0).getText());
        assertEquals("world", splitter.res.get(1).getText());
    }

    @Test public void emptyRange()
    {
        final Paragraph para = TestDocFactory.paragraph("hello");
        final Run run = para.getRuns().get(0);
        final RowPartsSplitter splitter = new RowPartsSplitter();
        splitter.onRun(run, "hello", 2, 2, 10);
        assertEquals(0, splitter.res.size());
    }

    @Test public void multipleRuns()
    {
        final Paragraph para = TestDocFactory.paragraph("abc", "def");
        final Run run1 = para.getRuns().get(0);
        final Run run2 = para.getRuns().get(1);
        final RowPartsSplitter splitter = new RowPartsSplitter();
        splitter.onRun(run1, "abc", 0, 3, 5);
        splitter.onRun(run2, "def", 0, 3, 5);
        assertEquals(1, splitter.res.size());
        assertEquals("abcdef", splitter.res.get(0).getText());
    }

    @Test public void invalidBounds()
    {
        final Paragraph para = TestDocFactory.paragraph("hello");
        final Run run = para.getRuns().get(0);
        final RowPartsSplitter splitter = new RowPartsSplitter();
        assertThrows(IllegalArgumentException.class, () -> splitter.onRun(run, "hello", -1, 5, 10));
        assertThrows(IllegalArgumentException.class, () -> splitter.onRun(run, "hello", 0, -1, 10));
        assertThrows(IllegalArgumentException.class, () -> splitter.onRun(run, "hello", 6, 5, 10));
        assertThrows(IllegalArgumentException.class, () -> splitter.onRun(run, "hello", 0, 6, 10));
    }
}