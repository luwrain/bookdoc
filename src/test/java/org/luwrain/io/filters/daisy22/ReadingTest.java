
package org.luwrain.io.filters.daisy22;

import java.io.*;
import java.util.*;

import org.junit.*;

import org.luwrain.io.bookdoc.*;
import org.luwrain.io.bookdoc.Loader.*;

public class ReadingTest extends Assert
{
    @Test public void main() throws Exception
    {
	final Properties props = loadProps();
	assertNotNull(props);
	final String bookFile = props.getProperty("book");
	assertNotNull(bookFile);
	final Loader loader = Loader.newDefaultLoader(new File(bookFile).toURI(), null);
	assertNotNull(loader);
	final Result res = loader.load();
	assertNotNull(res);
	
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
    }
}
