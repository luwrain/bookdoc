
package org.luwrain.io.bookdoc;

import java.util.*;

public final class Doc
{
static public final String
    PROP_URL = "url",
    PROP_STARTING_REF = "startingref",
    PROP_DAISY_LOCAL_PATH = "daisy.localpath",
    PROP_TITLE = "title";

    private final Root root;
    private final Map<String, String> props = new HashMap();
    private String[] hrefs = new String[0];

    public Doc(Root root, String title)
    {
	if (root == null)
	    throw new NullPointerException("root can't be null");
	this.root = root;
	if (title != null)
	    props.put(PROP_TITLE, title);
    }

    public Doc(Root root)
    {
	this(root, null);
    }

    public Root getRoot()
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
	return this.hrefs;
    }

    public void setHrefs(String[] hrefs)
    {
	this.hrefs = hrefs.clone();
    }
}
