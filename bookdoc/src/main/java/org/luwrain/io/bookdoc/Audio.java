/*
   Copyright 2016-2022 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

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
