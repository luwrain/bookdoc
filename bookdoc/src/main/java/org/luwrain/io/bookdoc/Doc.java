// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.*;

/**
 * A single document loaded from a book.
 *
 * <p>A document stores a {@link Root} node containing the document content, a
 * set of named properties and the list of document URLs. The class supports
 * several predefined property names, but callers are free to store additional
 * named properties via {@link #setProperty(String, String)}.</p>
 */
public final class Doc
{
    /** The property containing the document URL. */
    static public final String
    PROP_URL = "url",
    /** The property containing the starting element URL. */
    PROP_STARTING_REF = "startingref",
    /** The property containing the DAISY local path. */
    PROP_DAISY_LOCAL_PATH = "daisy.localpath",
    /** The property containing the document title. */
    PROP_TITLE = "title";

    private final Root root;
    private final Map<String, String> props = new HashMap();
    private String[] hrefs = new String[0];

    /**
     * Creates a document with the specified root and title.
     *
     * @param root the document root; must not be {@code null}
     * @param title the document title; may be {@code null}, in which case no
     *              title property is set
     * @throws NullPointerException if {@code root} is {@code null}
     */
    public Doc(Root root, String title)
    {
	if (root == null)
	    throw new NullPointerException("root can't be null");
	this.root = root;
	if (title != null)
	    props.put(PROP_TITLE, title);
    }

    /**
     * Creates a document with the specified root and no title.
     *
     * @param root the document root; must not be {@code null}
     * @throws NullPointerException if {@code root} is {@code null}
     */
    public Doc(Root root)
    {
	this(root, null);
    }

    /**
     * Returns the root node of this document.
     *
     * @return the document root; never {@code null}
     */
    public Root getRoot()
    {
	return root;
    }

    /**
     * Sets the value of a named property.
     *
     * @param propName the property name; must not be {@code null} or empty
     * @param propValue the property value; must not be {@code null}
     * @throws NullPointerException if {@code propName} or {@code propValue} is {@code null}
     * @throws IllegalArgumentException if {@code propName} is empty
     */
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

    /**
     * Returns the value of a named property.
     *
     * @param propName the property name; may be {@code null}
     * @return the property value, or {@code null} if the property is not set
     */
    public String getProperty(String propName)
    {
	return props.get(propName);
    }

    /**
     * Returns all document URLs stored in this document.
     *
     * @return an array of document URLs; never {@code null}
     */
    public String[] getHrefs()
    {
	return this.hrefs;
    }

    /**
     * Replaces the list of document URLs stored in this document.
     *
     * @param hrefs the new array of document URLs; may be {@code null}
     */
    public void setHrefs(String[] hrefs)
    {
	this.hrefs = hrefs.clone();
    }
}
