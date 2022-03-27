
package org.luwrain.io.bookdoc.loaders;

import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

import org.luwrain.io.bookdoc.*;
import org.luwrain.io.filters.*;

import static org.luwrain.io.bookdoc.loaders.Utils.*;

public final class DefaultLoader extends Loader
{
    static private final String
	LOG_COMPONENT = "bookdoc",
	DEFAULT_CHARSET = "UTF-8";

    static private final FileContentType contentType = new FileContentType();
    final URL requestedUrl;
    final String requestedContentType;
    final String requestedTagRef;
    private String requestedCharset = "";

    private URL responseUrl = null;
    private String responseContentType = "";
    private String responseContentEncoding = "";

    private String selectedContentType = "";
    private String selectedCharset = "";

    private Path tmpFile;

    public DefaultLoader(URI uri, String contentType)
    {
	if (uri == null)
	    throw new NullPointerException("uri can't be null");
	this.requestedContentType = contentType != null?contentType:"";
	try {
	    final URL url = uri.toURL();
	    this.requestedTagRef = url.getRef();
	    this.requestedUrl = new URL(url.getProtocol(), IDN.toASCII(url.getHost()),
					url.getPort(), url.getFile());
	}
	catch(MalformedURLException e)
	{
	    throw new IllegalArgumentException(e);
	}
    }

    @Override public Result load() throws IOException
    {
	try {
	    Log.debug(LOG_COMPONENT, "fetching " + requestedUrl.toString());
	    fetch();
	    this.selectedContentType = requestedContentType.isEmpty()?responseContentType:requestedContentType;
	    if (selectedContentType.isEmpty() || ContentTypes.isUnknown(selectedContentType))
		this.selectedContentType = contentType.suggestContentType(requestedUrl, ContentTypes.ExpectedType.TEXT);
	    if (selectedContentType.isEmpty())
		throw new IOException("Unable to understand the content type");
	    Log.debug(LOG_COMPONENT, "selected content type is " + selectedContentType);
	    final Result res;
	    this.selectedCharset = Utils.extractCharset(selectedContentType);
	    if (!this.requestedCharset.isEmpty())
		this.selectedCharset = this.requestedCharset;
	    if (this.selectedCharset.isEmpty())
		this.selectedCharset = DEFAULT_CHARSET;
		final DocumentBuilder builder = DocumentBuilder.newBuilder(extractBaseContentType(selectedContentType));
		if (builder == null)
		    throw new IOException("No suitable handler for the content type: " + selectedContentType);
		res = new Result();
		final Properties props = new Properties();
		props.setProperty("url", responseUrl.toString());
		props.setProperty("charset", selectedCharset);
		res.doc = builder.buildDoc(tmpFile.toFile(), props);
	    if (res.doc == null)
		throw new IOException("No suitable handler for the content type: " + selectedContentType);
	    //	    res.doc.setProperty("hash", getTmpFileHash());
	    res.doc.setProperty("url", responseUrl.toString());
	    res.doc.setProperty("contenttype", selectedContentType);
	    if (requestedTagRef != null)
		res.doc.setProperty("startingref", requestedTagRef);
	    return res;
	}
	finally {
	    if (tmpFile != null)
	    {
		Log.debug(LOG_COMPONENT, "deleting temporary file " + tmpFile.toString());
		Files.delete(tmpFile);
		tmpFile = null;
	    }
	}
    }

    private void fetch() throws IOException
    {
	final URLConnection con;
	try {
	    con = Connections.connect(requestedUrl.toURI(), 0);
	}
	catch(URISyntaxException e)
	{
	    throw new IOException(e);
	}
	final InputStream responseStream = con.getInputStream();
	try {
	    this.responseUrl = con.getURL();
	    if (responseUrl == null)
		this.responseUrl = requestedUrl;
	    this.responseContentType = con.getContentType();
	    if (responseContentType == null)
		responseContentType = "";
	    this.responseContentEncoding = con.getContentEncoding();
	    if (responseContentEncoding == null)
		responseContentEncoding = "";
	    if (responseContentEncoding.toLowerCase().trim().equals("gzip"))
		downloadToTmpFile(new GZIPInputStream(responseStream)); else
		downloadToTmpFile(responseStream);
	}
	finally {
	    responseStream.close();
	}
    }

    private void downloadToTmpFile(InputStream s) throws IOException
    {
	NullCheck.notNull(s, "s");
	tmpFile = Files.createTempFile("tmplwr-reader-", ".dat");
	Log.debug(LOG_COMPONENT, "creating temporary file " + tmpFile.toString());
	Files.copy(s, tmpFile, StandardCopyOption.REPLACE_EXISTING);
    }

    /*
    private String getTmpFileHash()
    {
	try {
	    final InputStream is = new FileInputStream(tmpFile.toFile());
	    try {
		return org.luwrain.util.Sha1.getSha1(is);
	    }
	    finally {
		is.close();
	    }
	}
	catch(Exception e)
	{
	    Log.error(LOG_COMPONENT, "unable to get the hash of the temporary file:" + e.getClass().getName() + ":" + e.getMessage());
	    return "";
	}
    }
    */

    private String makeTitleFromUrl()
    {
	final String path = responseUrl.getPath();
	if (path == null || path.isEmpty())
	    return responseUrl.toString();
	final int lastSlashPos = path.lastIndexOf("/");
	final String fileName = (lastSlashPos >= 0 && lastSlashPos + 1 < path.length())?path.substring(lastSlashPos + 1):path;
	try {
	    return URLDecoder.decode(fileName, "UTF-8");
	}
	catch(IOException e)
	{
	    return fileName;
	}
    }
}
