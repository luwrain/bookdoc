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

package org.luwrain.io.filters.daisy22;

import java.io.*;
import java.util.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.luwrain.io.bookdoc.*;
import org.luwrain.io.bookdoc.Loader.*;
import org.luwrain.io.bookdoc.Book.*;

@Disabled
public class ReadingTest
{
    @Test public void main() throws Exception
    {
	/*
	final Properties props = loadProps();
	assertNotNull(props);
	final String bookFile = props.getProperty("book");
	assertNotNull(bookFile);
	final Loader loader = Loader.newDefaultLoader(new File(bookFile).toURI(), null);
	assertNotNull(loader);
	final Doc doc = loader.load();
	assertNotNull(doc);
		final Root root = doc.getRoot();
	assertNotNull(root);
assertFalse(root.getItems().isEmpty());
final Book daisy = Book.newDaisy22(doc);
final Section[] sect = daisy.getBookSections();
assertNotNull(sect);
assertFalse(sect.length == 0);
for(Section s: sect)
{
    assertNotNull(s.href);
    assertFalse(s.href.isEmpty());
    final Doc d = daisy.getDoc(s.href);
    assertNotNull(d);
}
final Doc[] docs = daisy.getDocs();
assertNotNull(docs);
assertFalse(docs.length == 0);
int emptyRunCount = 0, idRunCount = 0, audioRunCount = 0;
for(Doc d: docs)
{
    final List<Run> runs = new ArrayList<>();
    final Visitor runsVisitor = new Visitor(){
	    @Override public void visit(Run run) { runs.add(run); }
	};
    Visitor.walk(d.getRoot(), runsVisitor);
    assertFalse(runs.isEmpty());
    for(Run r: runs)
    {
	if (r.getAttrs() == null)
	{
	    emptyRunCount++;
	    continue;
	}
	final String[] ids = r.getAttrs().getIdsWithParents();
	if (ids == null || ids.length == 0)
	{
	    emptyRunCount++;
	    continue;
	}
	idRunCount++;
	Audio audio = null;
final String url = d.getProperty(Doc.PROP_URL);
	for(String id: ids)
	{
		audio = daisy.findAudioForId(url + "#" + id);
		if (audio != null)
		    break;
	    }
	if (audio != null)
	    audioRunCount++;
    }
}
    System.out.println("Runs without ID: " + emptyRunCount);
    System.out.println("Runs with ID: " + idRunCount);
        System.out.println("Runs with audio: " + audioRunCount);

    }

    private Properties loadProps()
    {
	try {
	    try (final BufferedReader r = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("books.properties"), "UTF-8"))) {
		final Properties p = new Properties();
		p.load(r);
		return p;
	    }
	}
	catch(IOException e)
	{
	    System.out.println("No books.properties file in the resources");
	    return null;
	}
	*/
    }
}
