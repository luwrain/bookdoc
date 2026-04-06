// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.*;

import com.google.gson.annotations.*;

public final class Paragraph extends Node implements ContainerItem
{
    private List<Run> runs = null;
    private Attributes attrs = null;
    //    private ParagraphView view = null;
    private Geom geom = null;

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

    /*
    public ParagraphView getView()
    {
	if (view == null)
	    view = new ParagraphView();
	return view;
    }
    */

    @Override public Geom getGeom()
    {
	if (geom == null)
	    geom = new Geom();
	return geom;
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
