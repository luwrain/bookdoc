// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.net.*;
import java.io.IOException;

import org.luwrain.io.bookdoc.loaders.*;

public abstract class Loader
{
    public abstract Doc load() throws IOException;

    static public Loader newDefaultLoader(String url, String contentType)
    {
	try {
	    return new LoaderImpl(new URL(url), contentType);
	}
	catch(MalformedURLException ex)
	{
	    throw new IllegalArgumentException(ex);
	}
    }

        static public Loader newDefaultLoader(URL url, String contentType)
    {
	return new LoaderImpl(url, contentType);
	    }
}
