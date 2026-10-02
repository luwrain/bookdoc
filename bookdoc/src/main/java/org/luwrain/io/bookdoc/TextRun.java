// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

public class TextRun implements Run
{
    private String text = null;
    private Paragraph paragraph = null;
    private String href = null;
    private Attributes attr = null;

    public TextRun(String text)
    {
	this(text, null, null, null);
    }

    public TextRun(String text, Paragraph paragraph)
    {
	this(text, paragraph, null, null);
    }

        public TextRun(String text, String href, Attributes attr)
    {
	this(text, null, href, attr);
    }
    
    public TextRun(String text, Paragraph paragraph, String href, Attributes attr)
    {
	if (text == null)
	    throw new NullPointerException("text can't be null");
	this.text = text;
	this.paragraph = paragraph;
	this.href = href;
	this.attr = attr;
    }

    @Override public String getText() {
	return text;
    }
    
    @Override public String getHref() {
	return href;
    }
    
    @Override public Attributes getAttrs() {
	return attr;
    }
    
    public void setParagraph(Paragraph parentNode)
    {
	this.paragraph = paragraph;
    }
    
    @Override public Paragraph getParagraph()
    {
	return paragraph;
    }

    @Override public String toString()
    {
	return text != null?text:"";
    }
}
