
package org.luwrain.io.bookdoc;

import java.util.*;

//import com.google.gson.annotations.*;

public final class Attributes
{
    public String tagName = null;
    public final Map<String, Object> attrMap = new HashMap<>();
    public final List<Attributes> parentAttr = new ArrayList<>();
}
