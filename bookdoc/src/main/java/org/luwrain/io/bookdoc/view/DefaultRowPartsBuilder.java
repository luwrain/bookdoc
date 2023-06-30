
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
	onNode(node, 0);
    }

    void onNode(Node node, int width)
    {
	if (node instanceof Container)
	{
	    onContainer((Container)node, width);
	    return;
	}
	   	if (node instanceof Paragraph)
	{
	    onParagraph((Paragraph)node, width);
	    return;
	}
    }

void onContainer(Container c, int width)
    {
	/*
	if (node instanceof EmptyLine)
	{
	    final Paragraph para = (Paragraph)node;
	    final RowPart part = new RowPart(para.getRuns()[0]);
	    para.setRowParts(new RowPart[]{part});
	    parts.add(part);
	    return;
	}
	*/
	for(ContainerItem i: c.getItems())
	    onNode((Node)i);
    }

    private void onParagraph(Paragraph para, int width)
    {
	final RowPartsSplitter splitter = new RowPartsSplitter();
	for(Run r: para.getRuns())
	{
	    final String text = r.getText();
	    splitter.onRun(r, text, 0, text.length(), width > 0?width:para.getAttributes().width);
	}
	if (!splitter.res.isEmpty())
	{
	    para.getView().setRowParts(splitter.res.toArray(new RowPart[splitter.res.size()]));
	    paragraphs.add(para);
	    for(RowPart p: splitter.res)
		parts.add(p);
	}
    }
    static private RowPart makeTitlePart(Run run)
    {
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
