
package org.luwrain.io.bookdoc;

public class TextRun implements Run
{
    private String text = null;
    private String href = null;
    private Attributes attr = null;

    public TextRun(String text, String href, Attributes attr)
    {
	if (text == null)
	    throw new NullPointerException("text can't be null");
	this.text = text;
	this.href = href;
	this.attr = attr;
    }

    @Override public String getText() { return text; }
    @Override public String getHref() { return href; }
    @Override public Attributes getAttrs() { return attr; }

    @Override public String toString()
    {
	return text != null?text:"";
    }
}
