// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.net.*;
import java.io.IOException;

import org.luwrain.io.bookdoc.loaders.*;

/**
 * A document loader.
 *
 * <p>A loader reads a document from a source and produces a {@link Doc}
 * instance. Concrete implementations implement {@link #load()}.</p>
 */
public abstract class Loader
{
    /**
     * Loads the document.
     *
     * @return the loaded document
     * @throws IOException if the document cannot be read
     */
    public abstract Doc load() throws IOException;

    /**
     * Creates the default loader for a URL string and content type.
     *
     * @param url the document URL
     * @param contentType the content type, or {@code null} if unknown
     * @return the default loader
     * @throws IllegalArgumentException if {@code url} is not a valid URL
     */
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

    /**
     * Creates the default loader for a URL and content type.
     *
     * @param url the document URL
     * @param contentType the content type, or {@code null} if unknown
     * @return the default loader
     */
    static public Loader newDefaultLoader(URL url, String contentType)
    {
	return new LoaderImpl(url, contentType);
    }
}
