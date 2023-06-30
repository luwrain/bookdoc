
package org.luwrain.io.bookdoc.view;

import java.util.*;

import org.luwrain.io.bookdoc.*;

final class TextExtractorFragment
{
    static private final String LOG_COMPONENT = "document";

    private final int width;
    private final Run runFrom;
    private final Run runTo;
    private final int posFrom;
    private final int posTo;

    private boolean accepting = false;
    private final List<RowPart> parts = new LinkedList();

    TextExtractorFragment(int width,
			  Run runFrom, int posFrom,
			  Run runTo, int posTo)
    {
	NullCheck.notNull(runFrom, "runFrom");
	NullCheck.notNull(runTo, "runTo");
	if (width < 0)
	    throw new IllegalArgumentException("width (" + width + ") may not be negative");
	this.width = width;
	this.runFrom = runFrom;
	this.posFrom = posFrom;
	this.runTo = runTo;
	this.posTo = posTo;
    }

    void onNode(Node node)
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
	    onParagraph((Paragraph)node);
	    return;
	}
	for(Node n: node.getSubnodes())
	    onNode(n);
    }

    private void onParagraph(Paragraph para)
    {
	NullCheck.notNull(para, "para");
	final Run boundingRun1 = searchForRun(runFrom, para.getRuns());
	final Run boundingRun2 = searchForRun(runTo, para.getRuns());
	if (!accepting && boundingRun1 == null && boundingRun2 == null)
	    return;
	final RowPartsSplitter splitter = new RowPartsSplitter();
	if (boundingRun1 == null && boundingRun2 == null)
	{
	    for(Run r: para.getRuns())
	    {
		final String text = r.text();
		NullCheck.notNull(text, "text");
		splitter.onRun(r, text, 0, text.length(), width);
	    }
	} else
	{
	    final BoundingInfo boundingInfo = prepareBoundingInfo(para, boundingRun1, boundingRun2);
	    boundingInfo.filter(para.getRuns(), (run, fromChar,toChar)->{
		    final String text = run.text();
		    NullCheck.notNull(text, "text");
		    if (fromChar < 0 || fromChar >= text.length())
			throw new RuntimeException("fromChar (" + fromChar + ") must be non-negative and less than " + text.length());
	    	    if (toChar < 0 || toChar >= text.length())
			throw new RuntimeException("toChar (" + toChar + ") must be non-negative and less than " + text.length());
		    splitter.onRun(run, text, fromChar, toChar, width);
		});
	}
	if (splitter.res.isEmpty())
	    return;
	for(RowPart p: splitter.res)
	    parts.add(p);
    }

    private BoundingInfo prepareBoundingInfo(Paragraph para, Run run1, Run run2)
    {
	throw new RuntimeException("Not implemented yet");
    }

    //Returns the run, if it is encountered in the runs, null otherwise
    static private Run searchForRun(Run run, Run[] runs)
    {
	NullCheck.notNull(run, "run");
	NullCheck.notNullItems(runs, "runs");
	for(Run r: runs)
	    if (r == run)
		return run;
	return null;
    }
}
