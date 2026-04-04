
package org.luwrain.io.bookdoc.loaders;

import java.io.*;
import java.util.*;

import org.luwrain.io.bookdoc.*;
//import org.luwrain.io.filters.msdocx.*;

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

	/*
		if (contentType.equals(ContentTypes.APP_DOCX))
	    return new DocumentBuilder(){
		@Override public     Doc buildDoc(File file, Properties props) throws IOException
		{
		    final DocxReader r = new DocxReader();
		    r.read(file);
		    return new Doc(r.output);
		}
	    };
	*/
		
	return null;
    }
}
