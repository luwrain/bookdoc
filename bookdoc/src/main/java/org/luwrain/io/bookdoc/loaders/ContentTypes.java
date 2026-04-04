
package org.luwrain.io.bookdoc.loaders;

import java.util.*;
import java.util.regex.*;

public final class ContentTypes
{
    static public final String
	APP_DOC = "application/msword",
	APP_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
	APP_PDF = "application/pdf",
	APP_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
	DATA_BINARY = "application/octet-stream",
	TEXT_HTML = "text/html",
	TEXT_PLAIN = "text/plain",
	UNKNOWN = "content/unknown";

        private final Map<String, String> contentTypes = new HashMap<>();

public ContentTypes()
    {
	//Raw 
	contentTypes.put(".*\\.dat$", DATA_BINARY);
	contentTypes.put(".*\\.raw$", DATA_BINARY);

	//Text
	contentTypes.put(".*\\.txt$", TEXT_PLAIN);
	contentTypes.put(".*\\.htm$", TEXT_HTML);
	contentTypes.put(".*\\.html$", TEXT_HTML);
	contentTypes.put(".*\\.doc$", APP_DOC);
	contentTypes.put(".*\\.docx$", APP_DOCX);
		contentTypes.put(".*\\.xlsx$", APP_XLSX);
			contentTypes.put(".*\\.pdf$", APP_PDF);
		//FIXME:
	contentTypes.put(".*\\.xhtml$", "application/xhtml");
	contentTypes.put(".*\\.xhtm$", "application/xhtml");
	contentTypes.put(".*\\.ps$", "application/postscript");
	contentTypes.put(".*\\.zip$", "application/zip");
	contentTypes.put(".*\\.fb2$", "application/fb2");
    }


        public String suggest(String fileName)
    {
	if (fileName == null || fileName.isEmpty())
	    return "";
	for(Map.Entry<String, String> e: contentTypes.entrySet())
	    if (match(e.getKey(), fileName))
		return e.getValue();
	return "";
    }

    static private boolean match(String pattern, String line)
    {
	final Pattern pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
	final Matcher matcher = pat.matcher(line);
	return matcher.find();
    }
}
