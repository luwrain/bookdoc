
package org.luwrain.io.filters.fb2;

import java.io.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

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

	    assertNotNull(fb.getBody());
	}
    }
}
