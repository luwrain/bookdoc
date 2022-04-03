
package org.luwrain.io.bookdoc;

public class TextRun implements Run
{
    private String text = null;
    private String href = null;
    private Attributes attr = null;

    public TextRun(String text, String href, Attributes attr)
    {
	this.text = text;
	this.href = href;
	this.attr = attr;
    }

    public String getText()
    {
	return text;
    }

    public String getHref()
    {
	return href;
    }

    @Override public String toString()
    {
	return text != null?text:"";
    }
}
