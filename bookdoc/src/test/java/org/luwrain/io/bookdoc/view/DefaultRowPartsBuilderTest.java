// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import org.junit.jupiter.api.*;

import org.luwrain.io.bookdoc.*;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultRowPartsBuilderTest
{
    @Test public void singleParagraph()
    {
        final Paragraph para = TestDocFactory.paragraph("hello world");
        final Root root = TestDocFactory.root(para);
        final DefaultRowPartsBuilder builder = new DefaultRowPartsBuilder();
        builder.onNode(root, 6);

        assertNotNull(builder.getRowParts());
        assertTrue(builder.getRowParts().length > 0);
        assertEquals(1, builder.getParagraphs().length);
        assertSame(para, builder.getParagraphs()[0]);
    }

    @Test public void emptyParagraph()
    {
        final Paragraph para = new Paragraph();
        final Root root = TestDocFactory.root(para);
        final DefaultRowPartsBuilder builder = new DefaultRowPartsBuilder();
        builder.onNode(root, 10);

        assertEquals(0, builder.getRowParts().length);
        assertEquals(0, builder.getParagraphs().length);
    }

    @Test public void multipleParagraphsOrder()
    {
        final Paragraph para1 = TestDocFactory.paragraph("first");
        final Paragraph para2 = TestDocFactory.paragraph("second");
        final Root root = TestDocFactory.root(para1, para2);
        final DefaultRowPartsBuilder builder = new DefaultRowPartsBuilder();
        builder.onNode(root, 10);

        assertEquals(2, builder.getParagraphs().length);
        assertSame(para1, builder.getParagraphs()[0]);
        assertSame(para2, builder.getParagraphs()[1]);

        final RowPart[] parts = builder.getRowParts();
        assertEquals(2, parts.length);
        assertEquals("first", parts[0].getText());
        assertEquals("second", parts[1].getText());
    }

    @Test public void nestedContainer()
    {
        final Paragraph para = TestDocFactory.paragraph("nested text");
        final Heading heading = TestDocFactory.heading(1, para);
        final Root root = TestDocFactory.root(heading);
        final DefaultRowPartsBuilder builder = new DefaultRowPartsBuilder();
        builder.onNode(root, 20);

        assertEquals(1, builder.getParagraphs().length);
        assertEquals("nested text", builder.getRowParts()[0].getText());
    }

    @Test public void explicitWidthOverridesGeom()
    {
        final Paragraph para = TestDocFactory.paragraph("hello world");
        para.getGeom().width = 100;
        final DefaultRowPartsBuilder builder = new DefaultRowPartsBuilder();
        builder.onNode(para, 6);

        final RowPart[] parts = builder.getRowParts();
        assertEquals(2, parts.length);
        assertEquals("hello", parts[0].getText());
        assertEquals("world", parts[1].getText());
    }
}
