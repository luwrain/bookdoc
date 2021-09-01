
package org.luwrain.io.filters.textdoc;

import com.google.gson.annotations.*;

public class TextRun implements Run
{
    @SerializedName("atext")
    private String text = null;

    @SerializedName("href")
    private String href = null;

    @SerializedName("attr")
    private Attributes attr = null;

    public TextRun(String text, String href, Attributes attr)
    {
	this.text = text;
	this.href = href;
	this.attr = attr;
    }
}
