
package org.luwrain.io.bookdoc;

public class Node
{
    static public final EmptyNode EMPTY = new EmptyNode();

    public String getText()
    {
	final StringBuilder res = new StringBuilder();
	final Visitor textVisitor = new Visitor(){
		@Override public void visit(Run run) { res.append(run.toString());}
	    };
		Visitor.walk(this, textVisitor);
		return new String(res);
    }
}
