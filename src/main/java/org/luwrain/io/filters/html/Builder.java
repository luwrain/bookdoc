
package org.luwrain.io.filters.html;

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
    static private final String DEFAULT_CHARSET = "UTF-8";

    private org.jsoup.nodes.Document jsoupDoc = null;
    private URL docUrl = null;

    private final LinkedList<String> hrefStack = new LinkedList();
    private final List<String> allHrefs = new LinkedList();

public Doc buildDoc(File file, Properties props) throws IOException
    {
	NullCheck.notNull(file, "file");
	NullCheck.notNull(props, "props");
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
    NullCheck.notNull(text, "text");
    NullCheck.notNull(props, "props");
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
	NullCheck.notNull(is, "is");
	NullCheck.notNull(props, "props");
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
	root.getItems().addAll(onNode(jsoupDoc.body(), false));
	final Doc doc = new Doc(root, jsoupDoc.title());
	doc.setHrefs(allHrefs.toArray(new String[allHrefs.size()]));
	return doc;
    }

    private List<ContainerItem> onNode(org.jsoup.nodes.Node node, boolean preMode)
    {
	NullCheck.notNull(node, "node");
	final List<ContainerItem> resItems = new ArrayList<>();
	final List<Run> runs = new ArrayList<>();
	final List<org.jsoup.nodes.Node> nodes = node.childNodes();
	if (nodes == null)
	    return Arrays.asList(new ContainerItem[0]);
	for(org.jsoup.nodes.Node n: nodes)
	{
	    if (n instanceof TextNode)
	    {
		final TextNode textNode = (TextNode)n;
		onTextNode(textNode, resItems, runs, preMode);
		/*
		final String text = textNode.text();
		if (text != null && !text.isEmpty())
		    runs.add(new org.luwrain.reader.TextRun(text, !hrefStack.isEmpty()?hrefStack.getLast():"", getCurrentExtraInfo()));
		*/
		continue;
	    }
	    if (n instanceof Element)
	    {
		final Element el = (Element)n;
		{
		    onElement((Element)n, resItems, runs, preMode);
		    continue;
		}
	    }

	    	    if (n instanceof Comment)
			continue;
			
	    
	    		Log.warning(LOG_COMPONENT, "unprocessed node of class " + n.getClass().getName());
	}
	commitParagraph(resItems, runs);
	return resItems;
    }

    private void onElementInPara(Element el, List<ContainerItem> nodes, List<Run> runs, boolean preMode)
    {
	NullCheck.notNull(el, "el");
	NullCheck.notNull(nodes, "nodes");
	NullCheck.notNull(runs, "runs");
	final String tagName;
	{
	    final String name = el.nodeName();
	    if (name == null || name.isEmpty())
		return;
	    tagName = name.trim().toLowerCase();
	}
	if (tagName.equals("img"))
	{
	    onImg(el, runs);
	    return;
	}
	final String href;
	if (tagName.equals("a"))
	    href = extractHref(el); else
	    href = null;
	if (href != null)
	    hrefStack.add(href);
	try {
	    final List<org.jsoup.nodes.Node> nn = el.childNodes();
	    if (nn == null)
		return;
	    for(org.jsoup.nodes.Node n: nn)
	    {
		if (n instanceof TextNode)
		{
		    onTextNode((TextNode)n, nodes, runs, preMode);
		    continue;
		}
		if (n instanceof Element)
		{
		    onElement((Element)n, nodes, runs, preMode);
		    continue;
		}
		if (n instanceof Comment)
		    continue;
		Log.warning(LOG_COMPONENT, "encountering unexpected node of class " + n.getClass().getName());
	    }
	}
	finally
	{
	    if (href != null)
		hrefStack.pollLast();
	}
    }

    private void onElement(Element el, List<ContainerItem> nodes, List<Run> runs, boolean preMode)
    {
	NullCheck.notNull(el, "el");
	NullCheck.notNull(nodes, "nodes");
	NullCheck.notNull(runs, "runs");
	final String tagName;
	{
	final String name = el.nodeName();
	if (name == null || name.trim().isEmpty())
	    return;
tagName = name.trim().toLowerCase();
	}
	if (tagName.startsWith("g:") ||
	    tagName.startsWith("g-") ||
	    tagName.startsWith("fb:"))
	    return;
	switch(tagName)
	{
	case "script":
	case "style":
	case "hr":
	case "input":
	case "button":
	case "nobr":
	case "wbr":
	case "map":
	case "svg":
	    return;
	case "pre":
	    onPre(el, nodes, runs);
	    break;
	case "br":
	    commitParagraph(nodes, runs);
	    break;
	case "p":
	case "div":
	case "main":
	case "noscript":
	case "header":
	case "footer":
	case "center":
	case "blockquote":
	case "tbody":
	case "figure":
	case "figcaption":
	case "caption":
	case "address":
	case "nav":
	case "article":
	case "noindex":
	case "iframe":
	case "form":
	case "section":
	case "dl":
	case "dt":
	case "dd":
	case "time":
	case "aside":
	    {
	    commitParagraph(nodes, runs);
	addAttrs(el);
	final List<ContainerItem > nn = onNode(el, preMode);
	releaseAttrs();
	nodes.addAll(nn);
	    }
	break;
	case "h1":
	case "h2":
	case "h3":
	case "h4":
	case "h5":
	case "h66":
	case "h7":
	case "h8":
	case "h9":
	    {
	    commitParagraph(nodes, runs);
	addAttrs(el);
	final Heading h = new Heading(tagName.trim().charAt(1) - '0');
	h.getItems().addAll(onNode(el, preMode));
	h.setAttributes(getAttributes());
	releaseAttrs();
	nodes.add(h);
	    }
	break;
	case "ul":
	case "ol":
	case "li":
	case "table":
	case "th":
	case "tr":
	case "td":
	    {
	    commitParagraph(nodes, runs);
	addAttrs(el);
	final Heading h = new Heading(1);
	h.getItems().addAll(onNode(el, preMode));
	h.setAttributes(getAttributes());
	releaseAttrs();
	nodes.add(h);
	    }
	break;
	case "img":
	case "a":
	case "tt":
	case "code":
	case "b":
	case "s":
	case "ins":
	case "em":
	case "i":
	case "u":
	case "big":
	case "small":
	case "strong":
	case "span":
	case "cite":
	case "font":
	case "sup":
	case "label":
	    addAttrs(el);
	onElementInPara(el, nodes, runs, preMode);
	releaseAttrs();
	break;
	default:
	    Log.warning(LOG_COMPONENT, "unprocessed tag:" + tagName);
	}
    }

    private void onTextNode(TextNode textNode, List<ContainerItem> nodes, List<Run> runs, boolean preMode)
    {
	NullCheck.notNull(textNode, "textNode");
	NullCheck.notNull(nodes, "nodes");
	NullCheck.notNull(runs, "runs");
	final String text = textNode.text();
	if (text == null || text.isEmpty())
	    return;
	if (!preMode)
	{
	    runs.add(new TextRun(text, !hrefStack.isEmpty()?hrefStack.getLast():"", getAttributes()));
	    return;
	}
	final String[] lines = text.split("\n", -1);
	if (lines.length == 0)
	    return;
		    runs.add(new TextRun(lines[0], !hrefStack.isEmpty()?hrefStack.getLast():"", getAttributes()));
		    for(int i = 1;i < lines.length;i++)
		    {
			commitParagraph(nodes, runs);
			runs.add(new TextRun(lines[i], !hrefStack.isEmpty()?hrefStack.getLast():"", getAttributes()));
		    }
    }

    private void commitParagraph(List<ContainerItem> nodes, List<Run> runs)
    {
	NullCheck.notNull(nodes, "nodes");
	NullCheck.notNull(runs, "runs");
	if (runs.isEmpty())
	    return;
	final Paragraph p = new Paragraph(runs);
	p.setAttributes(getAttributes());
	nodes.add(p);
	runs.clear();
    }

    /*
    private ContainerItem newContainerItem(String tagName, Container builder)
    {
	NullCheck.notEmpty(tagName, "tagName");
	switch(tagName)
	{
	case "ul":
	    return builder.newUnorderedList();
	case "ol":
	    return builder.newOrderedList();
	case "li":
	    return builder.newListItem();
	case "table":
	    return builder.newTable();
	case "tr":
	    return builder.newTableRow();
	case "th":
	case "td":
	    return builder.newTableCell();
	default:
	    Log.warning(LOG_COMPONENT, "unable to create the node for tag \'" + tagName + "\'");
	    return null;
	}
    }
    */

    private void onImg(Element el, List<Run> runs)
    {
	 NullCheck.notNull(el, "el");
	 NullCheck.notNull(runs, "runs");
	 final String value = el.attr("alt");
	 if (value != null && !value.isEmpty())
	     runs.add(new TextRun("[" + value + "]", !hrefStack.isEmpty()?hrefStack.getLast():"", getAttributes()));
	 }

    private String extractHref(Element el)
    {
	final String value = el.attr("href");
	if (value == null)
	    return null;
	allHrefs.add(value);
	try {
	    return new URL(docUrl, value).toString();
	}
	catch(MalformedURLException e)
	{
	    return value;
	}
    }

    private void onPre(Element el, List<ContainerItem> items, List<Run> runs)
    {
	NullCheck.notNull(el, "el");
	NullCheck.notNull(items, "items");
	NullCheck.notNull(runs, "runs");
	commitParagraph(items, runs);
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
}
