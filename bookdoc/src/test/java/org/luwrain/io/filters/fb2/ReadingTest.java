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

package org.luwrain.io.filters.fb2;

import java.io.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@Ignore
public class ReadingTest
{
    static final File DIR = new File(new File("test"), "fb2");

    @Test public void proba() throws Exception
    {
	if (!DIR.exists())
	    return;
	for(File f: DIR.listFiles())
	{
	    System.out.println("Checking " + f.getName());
	    final FictionBook fb = new FictionBook(f);

	    System.out.println("Authors: " + fb.getAuthors().size());

	    final Body body = fb.getBody();
	    assertNotNull(body);
	    System.out.println("Sections: " + body.sections.size());
	    assertTrue(body.sections.size() > 0);
	}
    }
}
