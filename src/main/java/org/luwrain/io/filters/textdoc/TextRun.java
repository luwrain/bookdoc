
package org.luwrain.io.filters.textdoc;

import com.google.gson.annotations.*;

public class TextRun implements Run
{
    @SerializedName("atext")
    private String text = null;

    @SerializedName("attr")
    private Attributes attr = null;
}
