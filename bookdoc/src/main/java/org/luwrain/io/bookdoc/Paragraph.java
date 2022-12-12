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

import java.util.*;

import com.google.gson.annotations.*;

public final class Paragraph extends Node implements ContainerItem
{
    private List<Run> runs = null;
    private Attributes attrs = null;

    public Paragraph(List<Run> runs, Attributes attrs)
    {
	this.runs = new ArrayList<>();
	this.runs.addAll(runs);
	this.attrs = attrs;
    }

    public Paragraph()
    {
	this(Arrays.asList(new Run[0]), null);
    }

    public List<Run> getRuns()
    {
	if (this.runs == null)
	    this.runs = new ArrayList<>();
	return this.runs;
    }

    public void setAttributes(Attributes attrs)
    {
	this.attrs = attrs;
    }

    public Attributes getAttributes()
    {
	if (attrs == null)
	    attrs = new Attributes();
	return attrs;
    }

    public String getText()
    {
	if (runs == null)
	    return "";
	final StringBuilder b = new StringBuilder();
	for(Run r: runs)
	    b.append(r.getText());
	return new String(b);
    }

        @Override public String toString()
    {
	return getText();
    }
	    
    }
