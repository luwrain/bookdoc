/*
   Copyright 2016-2022 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

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

    public TextRun(String text)
    {
	this(text, null, null);
    }

    @Override public String getText() { return text; }
    @Override public String getHref() { return href; }
    @Override public Attributes getAttrs() { return attr; }

    @Override public String toString()
    {
	return text != null?text:"";
    }
}
