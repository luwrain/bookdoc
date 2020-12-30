
package org.luwrain.io.filters.msdocx;

import com.google.gson.annotations.*;

public class Item
{
    static public final String TYPE_PARAGRAPH = "paragraph";

    @SerializedName("type")
    public String type = null;

    @SerializedName("text")
    public String text = null;

    @SerializedName("style")
    public String style = null;

    @SerializedName("alignment")
    public String alignment = null;

    @SerializedName("firstLineIndent")
    public int firstLineIndent = 0;

    @SerializedName("fontAlignment")
    public int fontAlignment = 0;

    @SerializedName("minFontSize")
    public int minFontSize = 0;

    @SerializedName("maxFontSize")
    public int maxFontSize = 0;

    public Item(String type)
    {
	this.type = type;
    }
}
