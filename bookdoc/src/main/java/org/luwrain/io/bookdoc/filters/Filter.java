// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.filters;

import java.util.*;
import java.io.*;

import org.luwrain.io.bookdoc.*;

import static java.util.Objects.*;

public interface Filter
{
    static public final String
	PROP_URL = "url",
	PROP_CHARSET = "charset";
    
    Doc load(InputStream is, Properties props) throws IOException;
    String getContentType();

    static public List<Filter> loadAll()
    {
	final var res = new ArrayList<Filter>();
	for(var f: ServiceLoader.load(Filter.class))
	    res.add(f);
	return res;
    }

    static public Filter loadForContentType(String contentType)
    {
	requireNonNull(contentType, "contentType can't be null");
	if (contentType.isEmpty())
	    throw new IllegalArgumentException("contentType can't be empty");
	final var f = loadAll();
	for(var ff: f)
	    if (contentType.equalsIgnoreCase(requireNonNull(ff.getContentType(), "getContentType() can't return null")))
		return ff;
	return null;
    }
}
