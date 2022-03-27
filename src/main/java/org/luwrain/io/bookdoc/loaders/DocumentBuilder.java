
package org.luwrain.io.bookdoc.loaders;

import java.io.*;
import java.util.*;

import org.luwrain.io.bookdoc.*;

interface DocumentBuilder
{
    Doc buildDoc(File file, Properties props) throws IOException;

    static DocumentBuilder newBuilder(String contentType)
    {
	if (contentType.equals(ContentTypes.TEXT_HTML_DEFAULT))
	    return new DocumentBuilder(){
		@Override public     Doc buildDoc(File file, Properties props) throws IOException
		{
		    return new org.luwrain.io.filters.html.Builder().buildDoc(file, props);
		}
	    };
	return null;
    }
}
