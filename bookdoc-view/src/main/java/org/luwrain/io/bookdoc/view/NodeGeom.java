/*
   Copyright 2016-2023 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.io.bookdoc.view;

import java.util.*;

import org.luwrain.io.bookdoc.*;

final class NodeGeom
{
    void calcWidth(Container c, int recommended)
    {
	final List<ContainerItem> items = c.getItems();
	final int numItems = items.size();
	if (c instanceof TableRow)
	{
	    final TableRow tableRow = (TableRow)c;
	    final int cellWidth = (recommended - numItems + 1) >= numItems?(recommended - numItems + 1) / numItems:1;
	    for(ContainerItem i: items)
		calcWidth((Container)i, cellWidth);
	    tableRow.getGeom().width = 0;
	    for(ContainerItem i: items)
		tableRow.getGeom().width += i.getGeom().width;
	    tableRow.getGeom().width += (numItems - 1);//One additional empty column after each cell
	    if (tableRow.getGeom().width < recommended)
		tableRow.getGeom().width = recommended;
	    return;
	}
	c.getGeom().width = recommended;
	for(ContainerItem i: items)
	{
	    calcWidth((Container)i, recommended);
	    if (c.getGeom().width < i.getGeom().width)
	        c.getGeom().width = i.getGeom().width;
	}
    }

    void calcHeight(Paragraph p)
    {
	    if (p.getView().getRowParts().length == 0)
	    {
		p.getGeom().height = 0;
		return;
	    }
	    int maxRelRowNum = 0;
	    for(RowPart r: (RowPart[])p.getView().getRowParts())
		if (r.relRowNum > maxRelRowNum)
		    maxRelRowNum = r.relRowNum;
	    p.getGeom().height = maxRelRowNum + 1;
    }

	    void calcHeight(Container c)
	    {
		final List<ContainerItem> items = c.getItems();
		final int numItems = items.size();
	if (c instanceof TableRow)
	{
	    final TableRow tableRow = (TableRow)c;
	    for(ContainerItem i: items)
		calcHeight((Container)i);
	    tableRow.getGeom().height = 0;
	    for(ContainerItem i: items)
		if (tableRow.getGeom().height < i.getGeom().height)
		    tableRow.getGeom().height = i.getGeom().height;
	    return;
	}
	//Not a paragraph and not a table row
	for(ContainerItem i: items)
	    calcHeight((Container)i);
	int height = 0;
	for(ContainerItem i: items)
	    height += i.getGeom().height;
	if (!c.getGeom().allSubnodesSingleLine)
    if (numItems > 0)
	height += (numItems - 1);
	c.getGeom().height = height;
    }

    void calcPosition(Container c)
    {
	final List<ContainerItem> items = c.getItems();
	final Geom g = c.getGeom();
		if  (c instanceof Root)
		    g.setPos(0, 0);
	//Assuming node.x and node.y already set appropriately
		final int
		baseX = g.x,
baseY = g.y;
	if (c instanceof TableRow)
	{
	    final TableRow tableRow = (TableRow)c;
	    int offset = 0;
	    for(ContainerItem i: items)
	    {
		i.getGeom().x = baseX + offset;
		offset += (i.getGeom().width + 1);
		i.getGeom().y = baseY;
		calcPosition((Container)i);
	    }
	    return;
	} //table row
	int offset = 0;
	/*
	if (node instanceof Paragraph && ((Paragraph)node).getRowParts().length > 0)
	    offset = 1;
	*/
	for(ContainerItem i: items)
	{
	    i.getGeom().setPos(baseX, baseY + offset);
	    offset += i.getGeom().height;
	    if (!i.getGeom().allSubnodesSingleLine)
		offset++;
	    calcPosition((Container)i);
	}
    }
}
