// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

public class TextRun implements Run
{
    private String text = null;
    private Node parentNode = null;
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

    public TextRun(String text)
    {
	this(text, null, null);
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
    
    public void setParentNode(Node parentNode)
    {
	this.parentNode = parentNode;
    }
    
    @Override public Node getParentNode()
    {
	return parentNode;
    }

    @Override public String toString()
    {
	return text != null?text:"";
    }
}
