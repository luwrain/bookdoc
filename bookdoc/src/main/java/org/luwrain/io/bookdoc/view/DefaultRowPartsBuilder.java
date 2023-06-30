
package org.luwrain.io.bookdoc.view;

import java.util.*;
import org.luwrain.io.bookdoc.*;

final class DefaultRowPartsBuilder
{
    static private final String LOG_COMPONENT = "doctree";

    private final List<RowPart> parts = new ArrayList();
    private final List<Paragraph> paragraphs = new ArrayList();

void onNode(Node node)
    {
	NullCheck.notNull(node, "node"); 
	onNode(node, 0);
    }

void onNode(Node node, int width)
    {
	NullCheck.notNull(node, "node");
	if (node instanceof EmptyLine)
	{
	    final Paragraph para = (Paragraph)node;
	    final RowPart part = new RowPart(para.getRuns()[0]);
	    para.setRowParts(new RowPart[]{part});
	    parts.add(part);
	    return;
	}
   	if (node instanceof Paragraph)
	{
	    onParagraph((Paragraph)node, width);
	    return;
	}
	for(Node n: node.getSubnodes())
		onNode(n);
    }

    private void onParagraph(Paragraph para, int width)
    {
	NullCheck.notNull(para, "para");
	final RowPartsSplitter splitter = new RowPartsSplitter();
	for(Run r: para.getRuns())
	{
	    final String text = r.text();
	    NullCheck.notNull(text, "text");
	    splitter.onRun(r, text, 0, text.length(), width > 0?width:para.width);
	}
	if (!splitter.res.isEmpty())
	{
	    para.setRowParts(splitter.res.toArray(new RowPart[splitter.res.size()]));
	    paragraphs.add(para);
	    for(RowPart p: splitter.res)
		parts.add(p);
	}
    }
    static private RowPart makeTitlePart(Run run)
    {
	NullCheck.notNull(run, "run");
return new RowPart(run);
    }

RowPart[] getRowParts()
    {
	return parts.toArray(new RowPart[parts.size()]);
    }

Paragraph[] getParagraphs()
    {
	return paragraphs.toArray(new Paragraph[paragraphs.size()]);
    }
}
