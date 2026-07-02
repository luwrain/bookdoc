// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import java.util.*;

import org.luwrain.io.bookdoc.*;

/**
 * Calculates the geometric layout of document nodes: widths, heights,
 * and absolute (x, y) positions. This class is used during view construction
 * and traverses the document tree in three passes:
 *
 * <ol>
 *   <li>{@link #calcWidth(Container, int)} — top-down width assignment</li>
 *   <li>{@link #calcHeight(Container)} — bottom-up height calculation</li>
 *   <li>{@link #calcPosition(Container)} — top-down position assignment</li>
 * </ol>
 *
 * <p>Table rows receive special treatment: their cells are laid out
 * horizontally with a one-character gap between them.</p>
 *
 * @see View
 * @see Geom
 */
final class NodeGeom
{
    /**
     * Calculates and assigns widths to all nodes in the container tree.
     * For table rows, the available width is distributed among cells.
     * For other containers, the width is propagated to children and the
     * maximum child width is adopted.
     *
     * @param c           The container to process
     * @param recommended The recommended width in characters
     */
    void calcWidth(Container c, int recommended)
    {
        final List<ContainerItem> items = c.getItems();
        final int numItems = items.size();
        if (c instanceof TableRow)
        {
            final TableRow tableRow = (TableRow) c;
            final int cellWidth = (recommended - numItems + 1) >= numItems ? (recommended - numItems + 1) / numItems : 1;
            for (ContainerItem i : items)
                calcWidth((Container) i, cellWidth);
            tableRow.getGeom().width = 0;
            for (ContainerItem i : items)
                tableRow.getGeom().width += i.getGeom().width;
            tableRow.getGeom().width += (numItems - 1);//One additional empty column after each cell
            if (tableRow.getGeom().width < recommended)
                tableRow.getGeom().width = recommended;
            return;
        }
        c.getGeom().width = recommended;
        for (ContainerItem i : items)
        {
            calcWidth((Container) i, recommended);
            if (c.getGeom().width < i.getGeom().width)
                c.getGeom().width = i.getGeom().width;
        }
    }

    /**
     * Calculates the height of a paragraph based on the number of
     * distinct relative row numbers among its row parts.
     *
     * @param p The paragraph to measure
     */
    void calcHeight(Paragraph p)
    {
        if (p.getView().getRowParts().length == 0)
        {
            p.getGeom().height = 0;
            return;
        }
        int maxRelRowNum = 0;
        for (RowPart r : (RowPart[]) p.getView().getRowParts())
            if (r.relRowNum > maxRelRowNum)
                maxRelRowNum = r.relRowNum;
        p.getGeom().height = maxRelRowNum + 1;
    }

    /**
     * Calculates the height of a container by summing the heights of its
     * children. Table rows take the height of their tallest cell.
     * An extra line is added between non-single-line children.
     *
     * @param c The container to measure
     */
    void calcHeight(Container c)
    {
        final List<ContainerItem> items = c.getItems();
        final int numItems = items.size();
        if (c instanceof TableRow)
        {
            final TableRow tableRow = (TableRow) c;
            for (ContainerItem i : items)
                calcHeight((Container) i);
            tableRow.getGeom().height = 0;
            for (ContainerItem i : items)
                if (tableRow.getGeom().height < i.getGeom().height)
                    tableRow.getGeom().height = i.getGeom().height;
            return;
        }
        //Not a paragraph and not a table row
        for (ContainerItem i : items)
            calcHeight((Container) i);
        int height = 0;
        for (ContainerItem i : items)
            height += i.getGeom().height;
        if (!c.getGeom().allSubnodesSingleLine)
            if (numItems > 0)
                height += (numItems - 1);
        c.getGeom().height = height;
    }

    /**
     * Assigns absolute (x, y) positions to all nodes in the container tree.
     * The root node is positioned at (0, 0). Table row cells are laid out
     * horizontally; other children are stacked vertically.
     *
     * @param c The container to position; its own (x, y) must already be set
     */
    void calcPosition(Container c)
    {
        final List<ContainerItem> items = c.getItems();
        final Geom g = c.getGeom();
        if (c instanceof Root)
            g.setPos(0, 0);
        //Assuming node.x and node.y already set appropriately
        final int baseX = g.x, baseY = g.y;
        if (c instanceof TableRow)
        {
            final TableRow tableRow = (TableRow) c;
            int offset = 0;
            for (ContainerItem i : items)
            {
                i.getGeom().x = baseX + offset;
                offset += (i.getGeom().width + 1);
                i.getGeom().y = baseY;
                calcPosition((Container) i);
            }
            return;
        } //table row
        int offset = 0;
        for (ContainerItem i : items)
        {
            i.getGeom().setPos(baseX, baseY + offset);
            offset += i.getGeom().height;
            if (!i.getGeom().allSubnodesSingleLine)
                offset++;
            calcPosition((Container) i);
        }
    }
}
