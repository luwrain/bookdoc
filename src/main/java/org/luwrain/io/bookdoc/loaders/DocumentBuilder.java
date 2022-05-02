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

import java.io.*;
import java.util.*;

import org.luwrain.io.bookdoc.*;

interface DocumentBuilder
{
    Doc buildDoc(File file, Properties props) throws IOException;

    static DocumentBuilder newBuilder(String contentType)
    {
	if (contentType.equals(ContentTypes.TEXT_HTML))
	    return new DocumentBuilder(){
		@Override public     Doc buildDoc(File file, Properties props) throws IOException
		{
		    return new org.luwrain.io.filters.html2.Builder().buildDoc(file, props);
		}
	    };
	return null;
    }
}
