
package org.luwrain.io.filters;

import java.io.*;

import java.net.*;

public final class ConsoleApp
{
    static public void main(String[] args) throws Exception
    {
	if (args.length == 0 || args[0].equals("--help"))
	{
	    printHelp();
	    System.exit(0);
	}
	
	if (args[0].toLowerCase().endsWith(".fb2"))
	    new org.luwrain.io.filters.fb2.Filter(new File(args[0]).toPath().toUri()).read();
    }

    static void printHelp()
    {
	System.out.println("Usage:");
	System.out.println("\tlwr-filters files");
    }
}
