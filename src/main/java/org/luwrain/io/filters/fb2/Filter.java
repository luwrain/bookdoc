
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
	    if (isElement(n, "fictionbook"))
		readChildren((Element)n);
    }

    private void readChildren(Element root)
    {
	for(Node n: root.childNodes())
	{
	    if (isElement(n, "section"))
	    {
		onSection((Element)n);
		continue;
	    }
	    if (isElement(n, "p"))
	    {
		onParagraph((Element)n);
		continue;
	    }
	    printInfo(n);
	}
    }

    private void onSection(Element sect)
    {
	System.out.println("Section");
	readChildren(sect);
    }

    private void onParagraph(Element p)
    {
	final StringBuilder b = new StringBuilder();
	for(Node n: p.childNodes())
	{
	    if (n instanceof TextNode)
	    {
		final TextNode textNode = (TextNode)n;
		b.append(textNode.text());
		continue;
	    }
	    System.out.println("p " + n.getClass().getName());
	}
	System.out.println(new String(b));
	System.out.println("");
    }

    private boolean isElement(Node n, String tagName)
    {
	if (!(n instanceof Element))
	    return false;
	final Element el = (Element)n;
	return el.tagName().toLowerCase().equals(tagName);
    }

    private void printInfo(Node n)
    {
	if (n instanceof Element)
	{
	    final Element el = (Element)n;
	    System.out.println(el.tagName());
	    return;
	}
	System.out.println(n.getClass().getName()); 
    }
}
