// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import org.junit.jupiter.api.*;

import org.luwrain.io.bookdoc.*;

import static org.junit.jupiter.api.Assertions.*;

public class TextExtractorFragmentTest
{
    @Test public void fragmentInsideSingleRun()
    {
        final Paragraph para = TestDocFactory.paragraph("hello world");
        final Run run = para.getRuns().get(0);
        final TextExtractorFragment extractor = new TextExtractorFragment(10, run, 6, run, 11);
        extractor.onNode(para);
        final String[] lines = extractor.getLines();
        assertEquals(1, lines.length);
        assertEquals("world", lines[0]);
    }

    @Test public void fragmentAcrossRuns()
    {
        final Paragraph para = TestDocFactory.paragraph("hello ", "world");
        final Run run1 = para.getRuns().get(0);
        final Run run2 = para.getRuns().get(1);
        final TextExtractorFragment extractor = new TextExtractorFragment(10, run1, 3, run2, 5);
        extractor.onNode(para);
        final String[] lines = extractor.getLines();
        assertEquals(1, lines.length);
        assertEquals("lo world", lines[0]);
    }

    @Test public void fragmentAcrossParagraphs()
    {
        final Paragraph para1 = TestDocFactory.paragraph("first");
        final Paragraph para2 = TestDocFactory.paragraph("second");
        final Run run1 = para1.getRuns().get(0);
        final Run run2 = para2.getRuns().get(0);
        final Root root = TestDocFactory.root(para1, para2);

        final TextExtractorFragment extractor = new TextExtractorFragment(10, run1, 2, run2, 3);
        extractor.onNode(root);
        final String[] lines = extractor.getLines();
        assertTrue(lines.length >= 1);
    }

    @Test public void invalidArguments()
    {
        final Paragraph para = TestDocFactory.paragraph("hello");
        final Run run = para.getRuns().get(0);
        assertThrows(IllegalArgumentException.class, () -> new TextExtractorFragment(-1, run, 0, run, 5));
        assertThrows(IllegalArgumentException.class, () -> new TextExtractorFragment(10, run, -1, run, 5));
        assertThrows(IllegalArgumentException.class, () -> new TextExtractorFragment(10, run, 0, run, -1));
        assertThrows(NullPointerException.class, () -> new TextExtractorFragment(10, null, 0, run, 5));
        assertThrows(NullPointerException.class, () -> new TextExtractorFragment(10, run, 0, null, 5));
    }
}
