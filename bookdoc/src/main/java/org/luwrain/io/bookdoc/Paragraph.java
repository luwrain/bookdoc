// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.*;

import com.google.gson.annotations.*;
import org.luwrain.io.bookdoc.view.ParagraphView;

public final class Paragraph extends Node implements ContainerItem
{
    private List<Run> runs = null;
    private ParagraphView view = null;
    
    public Paragraph(List<Run> runs, Attributes attributes)
    {
	this.runs = new ArrayList<>();
	this.runs.addAll(runs);
	this.attributes = attributes;
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

    public ParagraphView getView()
    {
	if (view == null)
	    view = new ParagraphView();
	return view;
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
