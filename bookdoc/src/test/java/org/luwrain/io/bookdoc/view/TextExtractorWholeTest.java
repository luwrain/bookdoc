// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import org.junit.*;

import org.luwrain.io.bookdoc.*;

import static org.junit.Assert.*;

public class TextExtractorWholeTest
{
    @Test public void singleParagraph()
    {
        final TextExtractorWhole extractor = new TextExtractorWhole(6);
        extractor.onNode(TestDocFactory.paragraph("hello world"));
        final String[] lines = extractor.getLines();
        assertEquals(2, lines.length);
        assertEquals("hello", lines[0]);
        assertEquals("world", lines[1]);
    }

    @Test public void multipleParagraphs()
    {
        final Root root = TestDocFactory.root(
            TestDocFactory.paragraph("first"),
            TestDocFactory.paragraph("second")
        );
        final TextExtractorWhole extractor = new TextExtractorWhole(10);
        extractor.onNode(root);
        final String[] lines = extractor.getLines();
        assertEquals(3, lines.length);
        assertEquals("first", lines[0]);
        assertEquals("", lines[1]);
        assertEquals("second", lines[2]);
    }

    @Test public void emptyNode()
    {
        final TextExtractorWhole extractor = new TextExtractorWhole(10);
        extractor.onNode(new Root(java.util.Arrays.asList()));
        assertEquals(0, extractor.getLines().length);
    }

    @Test public void invalidWidth()
    {
        assertThrows(IllegalArgumentException.class, () -> new TextExtractorWhole(-1));
    }

    @Test public void nestedContainer()
    {
        final Heading heading = TestDocFactory.heading(1, TestDocFactory.paragraph("nested"));
        final Root root = TestDocFactory.root(heading);
        final TextExtractorWhole extractor = new TextExtractorWhole(10);
        extractor.onNode(root);
        final String[] lines = extractor.getLines();
        assertEquals(1, lines.length);
        assertEquals("nested", lines[0]);
    }
}