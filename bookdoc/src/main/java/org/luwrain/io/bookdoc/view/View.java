// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import java.net.*;
import java.util.*;
import org.apache.logging.log4j.*;

import org.luwrain.io.bookdoc.*;

import static java.util.Objects.*;

public class View
{
    static private final Logger log = LogManager.getLogger();
    static public final String DEFAULT_ITERATOR_INDEX_PROPERTY = "defaultiteratorindex";

    protected final Doc doc;
    protected final Root root;
    protected final Paragraph[] paragraphs; //Only paragraphs which appear in document, no paragraphs without row parts
    protected final RowPart[] rowParts;
    protected final Row[] rows;
    protected final int lineCount;

    public View(Doc doc, int width)
    {
	requireNonNull(doc, "doc can't be null");
	this.doc = doc;
	this.root = doc.getRoot();
	final NodeGeom geom = new NodeGeom();
	geom.calcWidth(root, width);
	final DefaultRowPartsBuilder rowPartsBuilder = new DefaultRowPartsBuilder();
	rowPartsBuilder.onNode(root);
	rowParts = rowPartsBuilder.getRowParts();
	//	NullCheck.notNullItems(rowParts, "rowParts");
	if (rowParts.length <= 0)
	{
	    paragraphs = new Paragraph[0];
	    rows = new Row[0];
	    lineCount = 0;
	    return;
	}
	paragraphs = rowPartsBuilder.getParagraphs();
	geom.calcHeight(root);
	geom.calcPosition(root);
	calcAbsRowNums(rowParts);
	rows = buildRows(rowParts);
	lineCount = calcRowsPosition(rows);
	setDefaultIteratorIndex();
    }

    public Layout createLayout()
    {
	final Layout layout = new Layout(doc, root, rows, rowParts, paragraphs, lineCount);
	return layout;
    }

    protected void calcAbsRowNums(RowPart[] parts)
    {
	//	NullCheck.notNullItems(parts, "parts");
	if (parts.length < 1)
	    return;
	RowPart first = parts[0];
	parts[0].absRowNum = 0;
	for(int i = 1;i < parts.length;++i)
	{
	    final RowPart part = parts[i];
	    if (!first.onTheSameRow(part))
	    {
		part.absRowNum = first.absRowNum + 1;
		first = part;
	    } else
		part.absRowNum = first.absRowNum;
	}
    }

    static protected Row[] buildRows(RowPart[] parts)
    {
	//	NullCheck.notNullItems(parts, "parts");
	final int rowCount = parts[parts.length - 1].absRowNum + 1;
	final int[] fromParts = new int[rowCount];
	final int[] toParts = new int[rowCount];
	for(int i = 0;i < rowCount;++i)
	{
	    fromParts[i] = -1;
	    toParts[i] = -1;
	}
	for(int i = 0;i < parts.length;++i)
	{
	    final int rowIndex = parts[i].absRowNum;
	    if (fromParts[rowIndex] == -1 || toParts[rowIndex] > i)
		fromParts[rowIndex] = i;
	    if(toParts[rowIndex] < i + 1)
		toParts[rowIndex] = i + 1;
	}
	final Row[] rows = new Row[rowCount];
	for (int i = 0;i < rowCount;++i)
	    if (fromParts[i] >= 0 && toParts[i] >= 0)
		rows[i] = new Row(parts, fromParts[i], toParts[i]); else
		throw new RuntimeException("Trying to create an empty row");
	//		rows[i] = new Row();
	return rows;
    }

    protected int calcRowsPosition(Row[] rows)
    {
	//	NullCheck.notNullItems(rows, "rows");
	int maxLineNum = 0;
	int lastX = 0;
	int lastY = 0;
	for(Row r: rows)
	{
	    //Generally admissible situation as not all rows should have associated parts
	    //	    if (r.isEmpty())
	    //	    {
	    //		r.x = lastX;
	    //		r.y = lastY + 1;
	    //		++lastY;
	    //		continue;
	    //	    }
	    final Run run = r.getFirstRun();
	    requireNonNull(run, "run can't be null");
	    final Node parent = run.getParentNode();
	    requireNonNull(parent, "parent can't be null");
	    if (parent instanceof Paragraph)
	    {
		final Paragraph paragraph = (Paragraph)parent;
		r.x = paragraph.getGeom().x;
		r.y = paragraph.getGeom().y + r.getRowY();
	    } else 
	    {
		r.x = parent.getGeom().x;
		r.y = parent.getGeom().y;
	    }
	    lastX = r.x;
	    lastY = r.y;
	    if (r.y > maxLineNum)
		maxLineNum = r.y;
	}
	return maxLineNum + 1;
    }

    public org.luwrain.io.bookdoc.view.Iterator getIterator()
    {
	return new Iterator(this);
    }

    public org.luwrain.io.bookdoc.view.Iterator getIterator(int startingIndex)
    {
	return new Iterator(this, startingIndex);
    }

    Paragraph[] getParagraphs() {
	return paragraphs.clone();
    }

    Row[] getRows() {
	return rows.clone();
    }

    RowPart[] getRowParts() {
	return rowParts.clone();
    }

    private void setDefaultIteratorIndex()
    {
	final String id = doc.getProperty("startingref");
	if (id.isEmpty())
	    return;
	log.trace("Preparing default iterator index for " + id);
	final var it = getIterator();
	while (it.canMoveNext())
	{
	    //	    if (!it.isEmptyRow())
	    {
		final var data = it.getNode().getAttr();
		if (data != null && data.hasIdWithParents(id))
		    break;
		final Run[] runs = it.getRuns();
		Run foundRun = null;
		for(Run r: runs)
		    if (r instanceof TextRun)
		    {
			final TextRun textRun = (TextRun)r;
			if (textRun.getAttrs().hasIdWithParents(id))
			    foundRun = textRun;
		    }
		if (foundRun != null)
		    break;
	    }
	    it.moveNext();
	}
	if (!it.canMoveNext())//FIXME:
	{
	    log.trace("No iterator position found for " + id);
	    doc.setProperty(DEFAULT_ITERATOR_INDEX_PROPERTY, "");
	    return;
	}
	doc.setProperty("defaultiteratorindex", "" + it.getIndex());
	log.trace("Default iterator index set to " + it.getIndex());
    }

    static public String[] getParagraphLines(Paragraph para, int width)
    {
	requireNonNull(para, "para can't be null");
	final DefaultRowPartsBuilder builder = new DefaultRowPartsBuilder();
	builder.onNode(para, width);
	final RowPart[] parts = builder.getRowParts();
	for(RowPart r: parts)
	    r.absRowNum = r.relRowNum;
	final Row[] rows = buildRows(parts);
	final List<String> lines = new ArrayList();
	for(Row r: rows)
	    lines.add(r.getText());
	return lines.toArray(new String[lines.size()]);
    }
}
