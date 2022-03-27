
package org.luwrain.io.bookdoc;

import java.net.URI;
import java.io.IOException;

public abstract class Loader
{
    public abstract Result load() throws IOException;

    static public Loader newDefaultLoader(URI uri)
    {
	return null;
    }

        static public final class Result
    {
	public Book book = null;
	public Doc doc = null;
    }
}
