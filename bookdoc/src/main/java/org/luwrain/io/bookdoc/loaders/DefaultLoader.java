
package org.luwrain.io.bookdoc.loaders;

import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;
import java.io.*;
import java.nio.file.*;
import java.net.*;

import org.apache.logging.log4j.*;
import org.apache.tika.Tika;
import okhttp3.*;

import org.luwrain.io.bookdoc.*;
import org.luwrain.io.bookdoc.filters.*;
import org.luwrain.util.*;

import static java.util.Objects.*;
import static org.luwrain.io.bookdoc.loaders.Utils.*;

public final class DefaultLoader extends Loader implements AutoCloseable
{
    static private final Logger log = LogManager.getLogger();
    static private final String DEFAULT_CHARSET = "UTF-8";

    final Tika tika = new Tika();
    final OkHttpClient client;
    final URL requestedUrl;
    final String requestedContentType;
    final String requestedTagRef;
    private final TempDir tempDir;
        private final Path tmpFile;
    
    private String requestedCharset = "";
        private URL responseUrl = null;
    private String responseContentType = "";
    private String responseContentEncoding = "";
    private String selectedContentType = "";
    private String selectedCharset = "";



    public DefaultLoader(OkHttpClient client, URI uri, String contentType)
    {
	this.client = requireNonNull(client, "client can't be null");
	requireNonNull(uri, "uri can't be null");
	this.requestedContentType = requireNonNullElse(contentType, "");
	try {
this.requestedUrl = uri.toURL();
	}
	catch(MalformedURLException e)
	{
	    log.error("Unable to convert the address {} to URL", uri.toString(), e);
	    throw new IllegalArgumentException(e);
	}
		    this.requestedTagRef = requestedUrl.getRef();
		    this.tempDir = new TempDir();
		    this.tmpFile = tempDir.getPath().resolve("download");
    }

    public DefaultLoader(URI uri, String contentType)
    {
	this(newHttpClient(), uri, contentType);
    }

    @Override public Doc load() throws IOException
    {
	try {
	    fetch();
	    this.selectedContentType = requestedContentType.isBlank()?responseContentType:requestedContentType.trim();
	    if (selectedContentType.isEmpty() || selectedContentType.equalsIgnoreCase(ContentTypes.UNKNOWN))
		this.selectedContentType = new ContentTypes().suggest(requestedUrl.getFile());
	    if (selectedContentType.isEmpty())
		throw new IOException("Unable to understand the content type");
	    this.selectedCharset = Utils.extractCharset(selectedContentType);
	    if (!this.requestedCharset.isEmpty())
		this.selectedCharset = this.requestedCharset;
	    if (this.selectedCharset.isEmpty())
		this.selectedCharset = DEFAULT_CHARSET;
		final var filter = Filter.loadForContentType(extractBaseContentType(selectedContentType));
		if (filter == null)
		    throw new IOException("No suitable handler for the content type: " + selectedContentType);
		final Properties props = new Properties();
		props.setProperty("url", responseUrl.toString());
		props.setProperty("charset", selectedCharset);
		final Doc doc;
		try (final InputStream is = Files.newInputStream(tmpFile)) {
doc = filter.load(is, props);
		}
	    if (doc == null)
		throw new IOException("No suitable handler for the content type: " + selectedContentType);
	    //	    res.doc.setProperty("hash", getTmpFileHash());
	    doc.setProperty("url", responseUrl.toString());
	    doc.setProperty("contenttype", selectedContentType);
	    if (requestedTagRef != null)
		doc.setProperty("startingref", requestedTagRef);
	    return doc;
	}
	finally {
	}
    }

    private void fetch() throws IOException
    {
	final Request.Builder requestBuilder = new Request.Builder()
	.url(requestedUrl)
	    .header("User-Agent", Connections.DEFAULT_USER_AGENT);
	final Request request = requestBuilder.build();
	final Call call = client.newCall(request);
	try (final Response response = call.execute()) {
	    final int code = response.code();
	    log.trace("Response code {} for {}", code, requestedUrl.toString());
	    if (code != 200)
	    {
		log.error("Response code {} for {}", code, requestedUrl.toString());
		throw new IOException("Invalid response code: " + code);
	    } else
				log.trace("Response code {} for {}", code, requestedUrl.toString());
	    final ResponseBody body = response.body();
	    if (body == null)
	    {
		log.error("Empty response body for {}", requestedUrl.toString());
		throw new IOException("Empty response body for " + requestedUrl.toString());
	    }
	    final long contentLength = body.contentLength();
	    body.contentType();
	    log.trace("Content length is {}", contentLength);
	    try (final InputStream is = body.byteStream();
final OutputStream os = Files.newOutputStream(tmpFile)) {
		StreamUtils.copyAllBytes(is, os, null, null);
	    }
	}
    }

    private void downloadToTmpFile(InputStream s) throws IOException
    {
	requireNonNull(s, "s can't be null");
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

    @Override public void close()
    {
	tempDir.close();
    }

        static private OkHttpClient newHttpClient()
    {
	return new OkHttpClient.Builder()
	    .followRedirects(true)
	    .followSslRedirects(true)
	    .connectTimeout(15, TimeUnit.SECONDS)
	    .readTimeout(15, TimeUnit.SECONDS)
	    .build();
    }
}
