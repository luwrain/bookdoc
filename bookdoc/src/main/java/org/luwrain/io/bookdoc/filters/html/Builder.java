
package org.luwrain.io.filters.html2;

import java.io.*;
import java.util.*;
import java.net.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import org.luwrain.io.filters.*;
import org.luwrain.io.bookdoc.*;

public final class Builder extends AttrsBase
{
    static private final String
	DEFAULT_CHARSET = "UTF-8";

    private org.jsoup.nodes.Document jsoupDoc = null;
    private URL docUrl = null;

    private final LinkedList<String> hrefStack = new LinkedList();
    private final List<String> allHrefs = new ArrayList<>();

public Doc buildDoc(File file, Properties props) throws IOException
    {
	final InputStream is = new FileInputStream(file);
	try {
	    return buildDoc(is, props);
	}
	finally {
	    is.close();
	}
    }

public Doc buildDoc(String text, Properties props)
{
    final InputStream is = new ByteArrayInputStream(text.getBytes());
    try {
	try {
	    return buildDoc(is, props);
	}
	finally {
	    is.close();
	}
    }
    catch(IOException e)
    {
	Log.error(LOG_COMPONENT, "unable to read HTML from a string:" + e.getClass().getName() + ":" + e.getMessage());
	return null;
    }
    }

public Doc buildDoc(InputStream is, Properties props) throws IOException
    {
		final String urlStr = props.getProperty("url");
	if (urlStr == null || urlStr.isEmpty())
throw new IOException("no \'url\' property");
	    this.docUrl = new URL(urlStr);
	final String charsetValue = props.getProperty("charset");
	final String charset;
		if (charsetValue != null && !charsetValue.isEmpty())
		    charset = charsetValue; else
		    charset = DEFAULT_CHARSET;
	this.jsoupDoc = Jsoup.parse(is, charset, docUrl.toString());
	final Doc doc = constructDoc();
doc.setProperty("url", docUrl.toString());
doc.setProperty("contenttype", "FIXMEContentTypes.TEXT_HTML_DEFAULT");
doc.setProperty("charset", charset);
	return doc;
    }

	    private Doc constructDoc()
    {
	final Root root = new Root();
	final Map<String, String> meta = new HashMap<>();
	collectMeta(jsoupDoc.head(), meta);
	addAttrs(jsoupDoc.body());
	//	root.getItems().addAll(onNode(jsoupDoc.body(), false));
	final Doc doc = new Doc(root, jsoupDoc.title());
	doc.setHrefs(allHrefs.toArray(new String[allHrefs.size()]));
	return doc;
    }






    /*
    private void onImg(Element el, List<Run> runs)
    {
	 final String value = el.attr("alt");
	 if (value != null && !value.isEmpty())
	     runs.add(new TextRun("[" + value + "]", getLastHref(), getAttributes()));
	 }
    */


    /*
    private void onPre(Element el, List<ContainerItem> items, List<Run> runs)
    {
	//	commitParagraph(items, runs);
	addAttrs(el);
	try {
	    for(ContainerItem n: onNode(el, true))
		items.add(n);
	    commitParagraph(items, runs);
	}
	finally {
	    releaseAttrs();
	}
    }
    */

}
