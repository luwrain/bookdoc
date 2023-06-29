
package org.luwrain.io.bookdoc.view;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.reader.*;

public final class Layout implements Lines
{
    protected final Document document;
    protected final Node root;
    protected final Paragraph[] paragraphs; //Only paragraphs which appear in document, no paragraphs without row parts
    protected final RowPart[] rowParts;
    protected final Row[] rows;
    protected final Line[] lines;

    Layout(Document document, Node root,
	   Row[] rows, RowPart[] rowParts,
	   Paragraph[] paragraphs, int lineCount)
    {
	NullCheck.notNull(document, "document");
	NullCheck.notNull(root, "root");
	NullCheck.notNullItems(rows, "rows");
	NullCheck.notNullItems(rowParts, "rowParts");
	NullCheck.notNullItems(paragraphs, "paragraphs");
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

    @Override public int getLineCount()
    {
	return lines.length;
    }

    @Override public String getLine(int index)
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
	    NullCheck.notNull(row, "row");
	    rows = Arrays.copyOf(rows, rows.length + 1);
	    rows[rows.length - 1] = row;
	}
    }
}
