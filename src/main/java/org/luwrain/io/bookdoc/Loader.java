
package org.luwrain.io.bookdoc;

import java.net.URI;
import java.io.IOException;

import org.luwrain.io.bookdoc.loaders.*;

public abstract class Loader
{
    public abstract Result load() throws IOException;

    static public Loader newDefaultLoader(URI uri, String contentType)
    {
	return new DefaultLoader(uri, contentType);
    }

        static public final class Result
    {
	public Book book = null;
	public Doc doc = null;
    }
}
