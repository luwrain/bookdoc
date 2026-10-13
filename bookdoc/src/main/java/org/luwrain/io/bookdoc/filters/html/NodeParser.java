// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.filters.html;

import java.util.*;
import java.net.*;
import org.apache.logging.log4j.*;

import org.jsoup.nodes.*;
import org.luwrain.io.bookdoc.*;

import static java.util.Objects.*;

final class NodeParser
{
    static private final Logger log = LogManager.getLogger();

    final Context context;
    final org.jsoup.nodes.Node source;
    		final List<ContainerItem> resNodes = new ArrayList<>();
	final List<Run> runs = new ArrayList<>();
    final boolean preMode;

    NodeParser(Context context, org.jsoup.nodes.Node source, boolean preMode)
    {
	this.context = requireNonNull(context, "context can't be null");
	this.source = requireNonNull(source, "source can't be null");
	this.preMode = preMode;
    }

    NodeParser process()
    {
	if (source.childNodes() == null)
	    	    return this;
	for(var n: source.childNodes())
	{
	    if (n instanceof TextNode textNode)
	    {
		process(textNode);
		continue;
	    }
	    if (n instanceof Element el)
	    {
		process(el);
		continue;
	    }
	    if (n instanceof Comment)
		continue;
	    throw new IllegalStateException("unprocessed node of class " + n.getClass().getName());
	}
	commitParagraph();
	return this;
    }


    private void process(TextNode node)
    {
		final String text = node.text();
			    log.trace("Run {}", text);
	if (text == null || text.isBlank())
	    return;
	if (!preMode)
	{
	    runs.add(new TextRun(text, context.getActualAnchor(), context.getAttributes()));
	    return;
	}
	final String[] lines = text.split("\n", -1);
	if (lines.length == 0)
	    return;
	runs.add(new TextRun(lines[0], context.getActualAnchor(), context.getAttributes()));
		    for(int i = 1;i < lines.length;i++)
		    {
						commitParagraph();
			runs.add(new TextRun(lines[i], context.getActualAnchor(), context.getAttributes()));
		    }
    }

    void process(Element el)
    {
	final String tagName = tagName(el);
	if (tagName == null)
	{
	    log.warn("Element without a tag name");
	    return;
	}
	if (tagName.startsWith("g:") ||
	    tagName.startsWith("g-") ||
	    tagName.startsWith("fb:"))
	    return;
	log.trace("{}", tagName);
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
	    
	    /*
	case "pre":
	    onPre(el, nodes, runs);
	    break;
	    */
	    
	case "br":
	    commitParagraph();
	    break;
	    
	case "p":
	case "div":
	case "main":
	case "noscript":
	case "header":
	case "footer":
	case "navbar":
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
	case "aside": {
	    commitParagraph();
	context.addAttributes(el);
	final var p = new NodeParser(context, el, preMode).process();
	context.releaseAttributes();
	log.trace("Parsed, {} nodes", p.getResultNodes().size());
	resNodes.addAll(p.getResultNodes());
		break;
	    }

	case "h1":
	case "h2":
	case "h3":
	case "h4":
	case "h5":
	case "h66":
	case "h7":
	case "h8":
	case "h9": {
	    commitParagraph();
	context.addAttributes(el);
	/*
	final Heading h = new Heading(tagName.trim().charAt(1) - '0');
		h.getItems().addAll(onNode(el, preMode));
	h.setAttributes(getAttributes());
	*/
	context.releaseAttributes();
	//	nodes.add(h);
	    }
	break;
	
	case "li": {
	    	    commitParagraph();
	context.addAttributes(el);
	    final var listItem = new ListItem();
	    listItem.addAllItems(new NodeParser(context, el, preMode).process().getResultNodes());
	    //	    	h.setAttributes(getAttributes());
	context.releaseAttributes();
	    resNodes.add(listItem);
	    		    break;
	}
	    
	    	case "ul": {
	    	    commitParagraph();
	context.addAttributes(el);
	    final var ul = new UnorderedList();
	    ul.addAllItems(new NodeParser(context, el, preMode).process().getResultNodes());
	    //	    	h.setAttributes(getAttributes());
	context.releaseAttributes();
	    resNodes.add(ul);
	    		    break;
	}


	case "ol":
	case "table":
	case "th":
	case "tr":
	case "td":
	    {
			/*
	    commitParagraph();
	context.addAttributes(el);
		final Heading h = new Heading(1);
	h.getItems().addAll(onNode(el, preMode));
	h.setAttributes(getAttributes());
	releaseAttrs();
	nodes.add(h);
	break;
	*/
	    }
	    
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
	    context.addAttributes(el);
	    processTextElement(el);
	context.releaseAttributes();
	break;
	
	default:
	    log.warn("unprocessed tag {}", tagName);
	}
    }

void processTextElement(Element el)
    {
	final String tagName = tagName(el);
	if (tagName == null)
	{
	    	    log.warn("Element without a tag name");
 return;
	}
	//	if (tagName.equals("img"))
	    //	    onImg(el, runs);
	    final String anchor = tagName.equals("a")?getAnchor(el):null;
	    log.trace("Anchor {}", anchor);
	if (anchor != null)
	    context.anchorStack.add(anchor);
	try {
	    resNodes.addAll(new NodeParser(context, el, preMode).process().getResultNodes());
	}
	finally
	{
	    if (anchor != null)
		context.anchorStack.pollLast();
	}
    }

    String getAnchor(Element el)
    {
	final String value = el.attr("href");
	if (value == null)
	    return null;
	context.allAnchors.add(value);
	try {
	    return new URL(context.baseUrl, value).toString();
	}
	catch(MalformedURLException e)
	{
	    log.warn("Unable to construct an URL for base URL {} and anchor {}", context.baseUrl.toString(), value);
	    return value;
	}
    }

    void commitParagraph()
    {
		if (runs.isEmpty())
	    return;
	resNodes.add(new Paragraph(runs, context.getAttributes()));
	runs.clear();
    }

    String tagName(Element el)
    {
	final String name = el.nodeName();
	if (name == null || name.trim().isEmpty())
	    return null;
return name.trim().toLowerCase();
	}

    List<ContainerItem> getResultNodes()
    {
	return resNodes;
    }
    }
