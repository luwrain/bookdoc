
package org.luwrain.io.filters.fb2;

import java.io.*;
import java.net.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public final class Filter
{
    private final URI uri;
        private org.jsoup.nodes.Document jsoupDoc = null;

    public Filter(URI uri)
    {
	 if (uri == null)
	     throw new NullPointerException("uri can't be null");
	 this.uri = uri;
    }
    
    public void read() throws IOException
    {
	try (final InputStream is = uri.toURL().openStream()) {
	    this.jsoupDoc = Jsoup.parse(is, "UTF-8", uri.toString());
	}
	onBody(jsoupDoc.body());
    }

    private void onBody(Node body)
    {
	for(Node n: body.childNodes())
	{
	    System.out.println(n.toString());
	}
    }
}
