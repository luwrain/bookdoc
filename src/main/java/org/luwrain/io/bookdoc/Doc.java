
package org.luwrain.io.bookdoc;

import java.util.*;

public final class Doc
{
static public final String
    PROP_URL = "url",
    PROP_STARTING_REF = "startingref",
    PROP_DAISY_LOCAL_PATH = "daisy.localpath";

    private final Node root = null;
    private final Map<String, String> props = new HashMap();

    public Node getRoot()
    {
	return root;
    }

    public void setProperty(String propName, String propValue)
    {
	if (propName == null)
	    throw new NullPointerException("propName can't be null");
	if (propName.isEmpty())
	    throw new IllegalArgumentException("propName can't be empty");
	if (propValue == null)
	    throw new NullPointerException("propValue can't be null");
	props.put(propName, propValue);
    }

    public String getProperty(String propName)
    {
	return props.get(propName);
    }

    public String[] getHrefs()
    {
	return new String[0];
    }
}
