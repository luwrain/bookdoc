
package org.luwrain.io.filters.daisy22;

import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.io.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.jsoup.parser.*;

import org.luwrain.io.bookdoc.Audio;
import org.luwrain.io.filters.Log;

final class Smil
{
    static private final String
	LOG_COMPONENT = "daisy",
	USER_AGENT = "Mozilla/5.0";

    static private final Pattern
	TIME_PATTERN = Pattern.compile("^npt=(?<sec>\\d+.\\d+)s$");

    static class Entry
    {
	enum Type { SEQ, PAR, AUDIO, TEXT, FILE};
	final Type type;
	final String id;
	final Entry[] entries;
	String src = null;
	final Audio audioInfo;
	Entry(Type type)
	{
	    if (type == null)
		throw new NullPointerException("type can't be null");
	    this.type = type;
	    this.id = "";
	    this.audioInfo = null;
	    this.entries = new Entry[0];
	}
	Entry(Type type, Entry[] entries)
	{
	    if (type == null)
		throw new NullPointerException("info can't be null");
	    if (entries == null)
		throw new NullPointerException("entries can't be null");
	    this.type = type;
	    this.id = "";
	    this.audioInfo = null;
	    this.entries = entries;
	}
	Entry(Type type, String id, Entry[] entries)
	{
	    if (type == null)
		throw new NullPointerException("info can't be null");
	    if (id == null)
		throw new NullPointerException("id can't be null");
	    if (entries == null)
		throw new NullPointerException("entries can't be null");
	    this.type = type;
	    this.id = id;
	    this.audioInfo = null;
	    this.entries = entries;
	}
	Entry (Type type, String id, String src)
	{
	    if (type == null)
		throw new NullPointerException("info can't be null");
	    if (id == null)
		throw new NullPointerException("id can't be null");
	    if (src == null)
		throw new NullPointerException("src can't be null");
	    this.type = type;
	    this.id = id;
	    this.src = src;
	    this.audioInfo = null;
	    this.entries = new Entry[0];
	}
	Entry (String id, String src, Audio audioInfo)
	{
	    if (id == null)
		throw new NullPointerException("id can't be null");
	    if (src == null)
		throw new NullPointerException("src can't be null");
	    if (audioInfo == null)
		throw new NullPointerException("audioInfo can't be null");
	    this.type = Type.AUDIO;
	    this.id = id;
	    this.src = src;
	    this.audioInfo = audioInfo;
	    this.entries = new Entry[0];
	}
	void saveTextSrc(List<String> res)
	{
	    if (type == Type.TEXT &&
		src != null && !src.isEmpty())
		res.add(src);
	    if (entries != null)
		for(Entry e: entries)
		    e.saveTextSrc(res);
	}
	void allSrcToUrls(String base)
	{
	    try {
		final URL baseUrl = new URL(base);
		if (src != null && !src.isEmpty())
		    src = new URL(baseUrl, src).toString();
		if (entries != null)
		    for(Entry e: entries)
		    e.allSrcToUrls(base);
	    }
	    catch(MalformedURLException e)
	    {
		throw new IllegalArgumentException(e);
	    }
	}
	Entry findById(String id)
	{
	    Log.debug("proba", "ID " + id);
	    if (this.id != null && this.id.equals(id))
		return this;
	    if (entries == null)
		return null;
	    Log.debug("proba", "" + entries.length);
	    for(Entry e: entries)
	    {
		Log.debug("proba", "Checking " + e.id );
		final Entry res = e.findById(id);
		if (res != null)
		    return res;
	    }
	    return null;
	}
    }

    static final class File extends Entry
    {
	File()
	{
	    super(Type.FILE);
	}
    }

    static Entry fromUrl(String href) throws IOException
    {
	final org.jsoup.nodes.Document doc;
	final URL url = new URL(href);
	if (!url.getProtocol().equals("file"))
	{
	    final Connection con=Jsoup.connect(href);
	    con.userAgent(USER_AGENT);
	    con.timeout(30000);
	    doc = con.get();
	} else
	    doc = Jsoup.parse(url.openStream(), "utf-8", "", Parser.xmlParser());
	return new Entry(Entry.Type.FILE, onNode(doc.body()));
    }

    static Entry fromFile(java.io.File file) throws IOException
    {
	final org.jsoup.nodes.Document doc = Jsoup.parse(new FileInputStream(file), "utf-8", "", Parser.xmlParser());
	return new Entry(Entry.Type.FILE, onNode(doc.body()));
    }

    static private Entry[] onNode(Node node)
    {
	final List<Entry> res = new ArrayList<>();
	for(Node n: node.childNodes())
	{
	    final String name = n.nodeName();
	    if (n instanceof Element)
	    {
		final Element el = (Element)n;
		switch(name.trim().toLowerCase())
		{
		case "seq":
		    res.add(new Entry(Entry.Type.SEQ, el.attr("id"), onNode(el)));
		    break;
		case "par":
		    res.add(new Entry(Entry.Type.PAR, el.attr("id"), onNode(el)));
		    break;
		case "audio":
		    res.add(onAudio(el));
		    break;
		case "text":
		    res.add(onText(el));
		    break;
		default:
		    throw new RuntimeException("Unknown SMIL tag: " + name);
		}
		continue;
	    }
	}
	return res.toArray(new Entry[res.size()]);
    }

    static private Entry onAudio(Element el)
    {
	final String id = el.attr("id");
	final String src = el.attr("src");
	final String beginValue = el.attr("clip-begin");
	final String endValue = el.attr("clip-end");
	long beginPos = -1, endPos = -1;
	if (beginValue != null)
	    beginPos = parseTime(beginValue);
	if (endValue != null)
	    endPos = parseTime(endValue);
	return new Entry(id, src, new Audio(src, beginPos, endPos));
    }

    static private Entry onText(Element el)
    {
	final String id = el.attr("id");
	final String src = el.attr("src");
	return new Entry(Entry.Type.TEXT, id, src);
    }

    static private long parseTime(String value)
    {
	final Matcher m = TIME_PATTERN.matcher(value);
	if(m.matches()) 
	{
	    try {
		float f = Float.parseFloat(m.group("sec"));
		f *= 1000;
		return new Float(f).longValue();
	    }
	    catch(NumberFormatException e)
	    {
		e.printStackTrace();
	    }
	}
	return -1;
    }
}
