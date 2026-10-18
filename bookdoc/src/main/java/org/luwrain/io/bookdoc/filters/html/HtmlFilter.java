// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.filters.html;

import java.io.*;
import java.util.*;
import java.net.*;
import org.apache.logging .log4j.*;
import com.google.auto.service.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import org.luwrain.io.bookdoc.*;
import org.luwrain.io.bookdoc.filters.*;

import static java.util.Objects.*;

@AutoService(Filter.class)
public final class HtmlFilter extends AttrsBase implements Filter
{
    static private final Logger log = LogManager.getLogger();
    static private final String DEFAULT_CHARSET = "UTF-8";

    private org.jsoup.nodes.Document jsoupDoc = null;
    private URL url = null;

@Override public Doc load(InputStream is, Properties props) throws IOException
    {
	requireNonNull(is, "is can't be empty");
	requireNonNull(props, "props can't be empty");
		final String urlStr = props.getProperty(Filter.PROP_URL);
		log.trace("Parsing HTML from {}", urlStr);
	if (urlStr == null || urlStr.isBlank())
throw new IOException("no '" + Filter.PROP_URL + "' property");
	    this.url = new URL(urlStr);
	final String charsetValue = props.getProperty(Filter.PROP_CHARSET);
	final String charset;
		if (charsetValue != null && !charsetValue.isEmpty())
		    charset = charsetValue; else
		    charset = DEFAULT_CHARSET;
		log.trace("Charset is {}", charset);
	this.jsoupDoc = Jsoup.parse(is, charset, url.toString());
	final Doc doc = constructDoc();
doc.setProperty("url", url.toString());
doc.setProperty("contenttype", "FIXMEContentTypes.TEXT_HTML_DEFAULT");
doc.setProperty("charset", charset);
	return doc;
    }

	    private Doc constructDoc()
    {
	final Map<String, String> meta = new HashMap<>();
	collectMeta(jsoupDoc.head(), meta);
	addAttrs(jsoupDoc.body());
	final Root root = new Root(new NodeParser(new Context(url), jsoupDoc.body(), false)
				   .process().resNodes);
	final Doc doc = new Doc(root, jsoupDoc.title());
	//	doc.setHrefs(allHrefs.toArray(new String[allHrefs.size()]));
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

    @Override public String getContentType()
    {
	return "text/html";
    }
}
