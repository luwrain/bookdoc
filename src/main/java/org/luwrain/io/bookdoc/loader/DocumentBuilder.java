
package org.luwrain.io.bookdoc.loaders;

import java.io.*;
import java.util.*;

import org.luwrain.io.bookdoc.*;

interface DocumentBuilder
{
    Doc buildDoc(File file, Properties props) throws IOException;

    static DocumentBuilder newBuilder(String contentType)
    {
	return null;
    }
}
