
package org.luwrain.io.bookdoc;

import java.util.regex.*;
import java.net.*;

public final class Audio
{
    public final String src;
public final long beginPos;
public final long endPos;

    public Audio(String src, long beginPos)
    {
	if (src == null)
	    throw new NullPointerException("src can't be null");
	this.src = src;
	this.beginPos = beginPos;
	this.endPos = -1;
    }

    public Audio(String src)
    {
	if (src == null)
	    	    throw new NullPointerException("src can't be null");
	this.src = src;
	this.beginPos = -1;
	this.endPos = -1;
    }

    public Audio(String src, long beginPos, long endPos)
    {
	if (src == null)
	    	    throw new NullPointerException("src can't be null");
	this.src = src;
	this.beginPos = beginPos;
	this.endPos = endPos;
    }

    public boolean covers(String audioFileUrl, long msec)
    {
	if (!src.equals(audioFileUrl))
	    return false;
	if (endPos < 0)
	    return msec >= beginPos;
	return msec >= beginPos && msec <= endPos;
    }

    public boolean covers(String audioFileUrl, long msec, URL baseUrl)
    {
	try {
	    if (!(new URL(baseUrl, src).toString()).equals(new URL(baseUrl,audioFileUrl).toString()))
		return false;
	}
	catch(MalformedURLException e)
	{
	    e.printStackTrace();
	    return false;
	}
	if (endPos < 0)
	    return msec >= beginPos;
	return msec >= beginPos && msec <= endPos;
    }

    @Override public String toString()
    {
	return "Audio: " + src + " (from " + beginPos + ", to " + endPos + ")";
    }
}
