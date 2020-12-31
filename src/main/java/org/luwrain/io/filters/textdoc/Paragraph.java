
package org.luwrain.io.filters.textdoc;

import java.util.*;

import com.google.gson.annotations.*;

public final class Paragraph implements ContainerItem
{
    @SerializedName("runs")
    private List<Run> runs = null;

    public void addRun(Run run)
    {
	if (run == null)
	    throw new NullPointerException("run can't be null");
	if (this.runs == null)
	    this.runs = new ArrayList();
	this.runs.add(run);
    }

    public Run[] getRuns()
    {
	return this.runs != null?this.runs.toArray(new Run[this.runs.size()]):new Run[0];
    }

    public void clear()
    {
	this.runs = null;
    }
}
