
package org.luwrain.io.filters.daisy22;

import java.io.*;
import java.util.*;

import org.junit.*;

import org.luwrain.io.bookdoc.*;
import org.luwrain.io.bookdoc.Loader.*;
import org.luwrain.io.bookdoc.Book.*;

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
int emptyRunCount = 0, idRunCount = 0;
System.out.println("proba");
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
	final String id = r.getAttrs().getIdWithParents();
	if (id == null || id.isEmpty())
	{
	    emptyRunCount++;
	    continue;
	}
	idRunCount++;
    }
}
    System.out.println("Runs without ID: " + emptyRunCount);
    System.out.println("Runs with ID: " + idRunCount);

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
