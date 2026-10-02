// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import org.junit.jupiter.api.*;

import org.luwrain.io.bookdoc.*;

import static org.junit.jupiter.api.Assertions.*;

public class NodeGeomTest
{
    @Test public void widthForRoot()
    {
        final Root root = TestDocFactory.root(TestDocFactory.paragraph("hello"));
        final NodeGeom geom = new NodeGeom();
        geom.calcWidth(root, 80);
        assertEquals(80, root.getGeom().width);
    }

    @Test public void widthForTableRow()
    {
        final TableCell cell1 = TestDocFactory.cell(TestDocFactory.paragraph("one"));
        final TableCell cell2 = TestDocFactory.cell(TestDocFactory.paragraph("two"));
        final TableRow row = TestDocFactory.row(cell1, cell2);

        final NodeGeom geom = new NodeGeom();
        geom.calcWidth(row, 10);

        assertEquals(10, row.getGeom().width);
        assertTrue(cell1.getGeom().width > 0);
        assertTrue(cell2.getGeom().width > 0);
        assertTrue(cell1.getGeom().width + cell2.getGeom().width + 1 <= 10 || cell1.getGeom().width == 1);
    }

    @Test public void heightForParagraph()
    {
        final Paragraph para = TestDocFactory.paragraph("hello world");
        final DefaultRowPartsBuilder builder = new DefaultRowPartsBuilder();
        builder.onNode(para, 6);
        para.getView().setRowParts(builder.getRowParts());

        final NodeGeom geom = new NodeGeom();
        geom.calcHeight(para);
        assertEquals(2, para.getGeom().height);
    }

    @Test public void heightForContainer()
    {
        final Paragraph para1 = TestDocFactory.paragraph("first");
        final Paragraph para2 = TestDocFactory.paragraph("second");

        final DefaultRowPartsBuilder builder = new DefaultRowPartsBuilder();
        builder.onNode(para1, 10);
        para1.getView().setRowParts(builder.getRowParts());
        builder.onNode(para2, 10);
        para2.getView().setRowParts(builder.getRowParts());

        final Root root = TestDocFactory.root(para1, para2);
        final NodeGeom geom = new NodeGeom();
        geom.calcHeight(root);
        assertEquals(2, root.getGeom().height);
    }

    @Test public void heightForTableRow()
    {
        final Paragraph para1 = TestDocFactory.paragraph("a");
        final Paragraph para2 = TestDocFactory.paragraph("b", "c");
        para2.getView().setRowParts(new RowPart[] {
            new RowPart(para2.getRuns().get(0), 0, 1, 0),
            new RowPart(para2.getRuns().get(1), 0, 1, 0)
        });

        final TableCell cell1 = TestDocFactory.cell(para1);
        final TableCell cell2 = TestDocFactory.cell(para2);
        final TableRow row = TestDocFactory.row(cell1, cell2);

        final NodeGeom geom = new NodeGeom();
        geom.calcHeight(para1);
        geom.calcHeight(para2);
        geom.calcHeight(row);
        assertTrue(row.getGeom().height >= 1);
    }

    @Test public void positionForRoot()
    {
        final Paragraph para = TestDocFactory.paragraph("hello");
        final Root root = TestDocFactory.root(para);
        final NodeGeom geom = new NodeGeom();
        geom.calcWidth(root, 10);
        para.getView().setRowParts(new RowPart[] { new RowPart(para.getRuns().get(0), 0, 5, 0) });
        geom.calcHeight(para);
        geom.calcHeight(root);
        geom.calcPosition(root);

        assertEquals(0, root.getGeom().x);
        assertEquals(0, root.getGeom().y);
        assertEquals(0, para.getGeom().x);
        assertEquals(0, para.getGeom().y);
    }

    @Test public void positionForTableRow()
    {
        final Paragraph para1 = TestDocFactory.paragraph("one");
        final Paragraph para2 = TestDocFactory.paragraph("two");
        final TableCell cell1 = TestDocFactory.cell(para1);
        final TableCell cell2 = TestDocFactory.cell(para2);
        final TableRow row = TestDocFactory.row(cell1, cell2);

        final NodeGeom geom = new NodeGeom();
        geom.calcWidth(row, 10);
        geom.calcHeight(row);
        row.getGeom().setPos(0, 0);
        geom.calcPosition(row);

        assertEquals(0, cell1.getGeom().x);
        assertEquals(0, cell1.getGeom().y);
        assertEquals(cell1.getGeom().width + 1, cell2.getGeom().x);
        assertEquals(0, cell2.getGeom().y);
    }
}
