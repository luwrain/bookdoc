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

package org.luwrain.io.bookdoc.loaders;

import java.util.*;
import java.io.*;
import javax.activation.*;

final class Utils
{
    static String getDoctypeName(InputStream s) throws IOException
    {
	final org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(s, "us-ascii", "", org.jsoup.parser.Parser.xmlParser());
	List<org.jsoup.nodes.Node>nods = doc.childNodes();
	for (org.jsoup.nodes.Node node : nods)
	    if (node instanceof org.jsoup.nodes.DocumentType)
	    {
		org.jsoup.nodes.DocumentType documentType = (org.jsoup.nodes.DocumentType)node;                  
		final String res = documentType.attr("name");
		if (res != null)
		    return res;
	    }
	for (org.jsoup.nodes.Node node : nods)
	    if (node instanceof org.jsoup.nodes.Element)
	    {
		org.jsoup.nodes.Element el = (org.jsoup.nodes.Element)node;                  
		final String res = el.tagName();
		if (res != null)
		    return res;
	    }
	return "";
    }

    static String extractBaseContentType(String value)
    {
	try {
	    final MimeType mime = new MimeType(value);
	    final String res = mime.getBaseType();
	    return res != null?res:"";
	}
	catch(MimeTypeParseException e)
	{
	    return "";
	}
    }

    static String extractCharset(String value)
    {
	try {
	    final MimeType mime = new MimeType(value);
	    final String res = mime.getParameter("charset");
	    return res != null?res:"";
	}
	catch(MimeTypeParseException e)
	{
	    return "";
	}
    }

        static String extractHtmlCharset(File file) throws IOException
    {
	final String res = Encoding.getHtmlEncoding(file.toPath());
	if (res == null)
	    return "";
	return res;
    }

    /*
    static private ParaStyle translateParaStyle(String str)
    {
	NullCheck.notNull(str, "str");
	switch(str)
	{
	case "EMPTY_LINES":
	    return ParaStyle.EMPTY_LINES;
	case "INDENT":
	    return ParaStyle.INDENT;
	case "EACH_LINE":
	    return ParaStyle.EACH_LINE;
	default:
	    return null;
	}
    }

    */

}
