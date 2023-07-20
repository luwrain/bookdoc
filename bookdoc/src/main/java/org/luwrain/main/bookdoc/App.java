/*
   Copyright 2016-2023 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.main.bookdoc;

import java.io.*;

import org.luwrain.io.bookdoc.*;

public final class App
{
    static public void main(String[] args) throws Exception
    {
	if (args.length == 0 || args[0].isEmpty())
	    stop("No input files");
	final Loader loader = Loader.newDefaultLoader(new File(args[0]).toURI(), null);
	final Doc doc = loader.load();
	Visitor.walk(doc.getRoot(), new OutputVisitor());
    }

    static private void stop(String message)
    {
	System.err.println("ERROR: bookdoc: " + message);
	System.exit(1);
    }
}
