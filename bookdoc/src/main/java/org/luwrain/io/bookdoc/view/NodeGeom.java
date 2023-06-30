
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
	final Node[] subnodes = node.getSubnodes();
	NullCheck.notNullItems(subnodes, "subnodes");
	if (node instanceof TableRow)
	{
	    final TableRow tableRow = (TableRow)node;
	    for(Node n: subnodes)
		calcHeight(n);
	    tableRow.setNodeHeight(0);
	    for(Node n: subnodes)
		if (tableRow.getNodeHeight() < n.getNodeHeight())
		    tableRow.setNodeHeight(n.getNodeHeight());
	    return;
	}
	//Not a paragraph and not a table row
	for(Node n: subnodes)
	    calcHeight(n);
	int height = 0;
	for(Node n: subnodes)
	    height += n.getNodeHeight();
	if (!node.allSubnodesSingleLine())
    if (subnodes.length > 0)
	height += (subnodes.length - 1);
    node.setNodeHeight(height);
    }

    void calcPosition(Node node)
    {
	NullCheck.notNull(node, "node");
	final Node[] subnodes = node.getSubnodes();
	NullCheck.notNullItems(subnodes, "subnodes");
		if  (node.getType() == Node.Type.ROOT)
	{
	    node.setNodeX(0);
	    node.setNodeY(0);
	}
	//Assuming node.x and node.y already set appropriately
		    final int baseX = node.getNodeX();
		    		    final int baseY = node.getNodeY();
	if (node instanceof TableRow)
	{
	    final TableRow tableRow = (TableRow)node;
	    int offset = 0;
	    for(Node n: subnodes)
	    {
		n.setNodeX(baseX + offset);
		offset += (n.width + 1);
		n.setNodeY(baseY);
		calcPosition(n);
	    }
	    return;
	} //table row
	int offset = 0;
	/*
	if (node instanceof Paragraph && ((Paragraph)node).getRowParts().length > 0)
	    offset = 1;
	*/
	for(Node n: subnodes)
	{
	    n.setNodeX(baseX);
	    n.setNodeY(baseY + offset);
	    offset += n.getNodeHeight();
	    if (!node.allSubnodesSingleLine())
		offset++;
	    calcPosition(n);
	}
    }
}
