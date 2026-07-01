// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import java.util.*;

import org.luwrain.io.bookdoc.*;

import static java.util.Objects.*;

public final class TextExtractorWhole extends TextExtractor
{
    static private final String LOG_COMPONENT = "document";

    private final int width;
    private final List<RowPart> parts = new LinkedList();

    public TextExtractorWhole(int width)
    {
	if (width < 0)
	    throw new IllegalArgumentException("width (" + width + ") may not be negative");
	this.width = width;
    }

    public void onNode(Node node)
    {
	requireNonNull(node, "node can't be null");
	/*
	if (node instanceof EmptyLine)
	{
	    addEmptyLine();
	    return;
	}
	*/
   	if (node instanceof Paragraph)
	{
	    onParagraph((Paragraph)node);
	    addEmptyLine();
	    return;
	}
	/*
	for(Node n: node.getSubnodes())
	    onNode(n);
	*/
    }

    private void onParagraph(Paragraph para)
    {
	requireNonNull(para, "para can't be null");
	final RowPartsSplitter splitter = new RowPartsSplitter();
	for(Run r: para.getRuns())
	{
	    final String text = r.getText();
	    requireNonNull(text, "text can't be null");
	    splitter.onRun(r, text, 0, text.length(), width);
	}
	if (splitter.res.isEmpty())
	    return;
	onParagraphLines(splitter.res.toArray(new RowPart[splitter.res.size()]));
    }
}
