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

public final class Layout
{
    protected final Doc document;
    protected final Node root;
    protected final Paragraph[] paragraphs; //Only paragraphs which appear in document, no paragraphs without row parts
    protected final RowPart[] rowParts;
    protected final Row[] rows;
    protected final Line[] lines;

    Layout(Doc document, Node root,
	   Row[] rows, RowPart[] rowParts,
	   Paragraph[] paragraphs, int lineCount)
    {
	this.document = document;
	this.root = root;
	this.paragraphs = paragraphs;
	this.rows = rows;
	this.rowParts = rowParts;
	lines = new Line[lineCount];
	for(int i = 0;i < lines.length;++i)
	    lines[i] = new Line();
	for(Row row: rows)
	{
	    //	    if (row.isEmpty())
	    //		continue;
	    final Line line = lines[row.y];
	    line.add(row);
	}
    }

    public int getLineCount()
    {
	return lines.length;
    }

    public String getLine(int index)
    {
	if (index < 0)
	    throw new IllegalArgumentException("index (" + index + ") may not be negative");
	final Line line = lines[index];
	StringBuilder b = new StringBuilder();
	for(Row row: line.rows)
	{
	    while(b.length() < row.x)
		b.append(" ");
	    b.append(row.getText());
	}
	return b.toString();
    }

    static protected class Line
    {
	Row[] rows = new Row[0];

	void add(Row row)
	{
	    rows = Arrays.copyOf(rows, rows.length + 1);
	    rows[rows.length - 1] = row;
	}
    }
}
