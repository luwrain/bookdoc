
package org.luwrain.io.bookdoc.view;

import java.util.*;

import org.luwrain.io.bookdoc.*;

class TextExtractor
{
    static protected final String LOG_COMPONENT = "document";

    final LinkedList<String> lines = new LinkedList();

    protected void onParagraphLines(RowPart[] rowParts)
    {
	NullCheck.notNullItems(rowParts, "rowParts");
	if (rowParts.length == 0)
	    return;
	int lineNum = rowParts[0].relRowNum;
	int i = 0;
	while (i < rowParts.length)
	{
	    final StringBuilder b = new StringBuilder();
	    while(i < rowParts.length && rowParts[i].relRowNum == lineNum)
	    {
		b.append(rowParts[i].getText());
		++i;
	    }
	    final String s = new String(b);
	    if (!s.isEmpty())
		lines.add(s);
	    if (i < rowParts.length)
		lineNum = rowParts[i].relRowNum;
	}
    }

    protected void addEmptyLine()
    {
	if (lines.isEmpty())
	    return;
	if (lines.getLast().isEmpty())
	    return;
	lines.add("");
    }

    public String[] getLines()
    {
	return lines.toArray(new String[lines.size()]);
    }
}
