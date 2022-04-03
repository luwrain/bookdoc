
package org.luwrain.io.bookdoc;

import java.util.*;

import com.google.gson.annotations.*;

public final class Paragraph extends Node implements ContainerItem
{
    private List<Run> runs = null;

    private Attributes attr = null;

    public Paragraph(List<Run> runs)
    {
	this.runs = new ArrayList<>();
	this.runs.addAll(runs);
    }

    public List<Run> getRuns()
    {
	if (this.runs == null)
	    this.runs = new ArrayList<>();
	return this.runs;
    }

    public void setAttributes(Attributes attr)
    {
	this.attr = attr;
    }
    }


