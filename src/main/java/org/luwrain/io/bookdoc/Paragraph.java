
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
	return attrs;
    }
    }
