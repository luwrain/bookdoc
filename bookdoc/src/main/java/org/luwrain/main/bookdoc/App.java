
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
