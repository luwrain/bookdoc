
package org.luwrain.io.bookdoc.loaders;

import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.net.*;

import org.apache.logging.log4j.*;
import org.apache.tika.Tika;
import okhttp3.*;

import org.luwrain.io.bookdoc.*;
import org.luwrain.io.bookdoc.filters.*;
import org.luwrain.util.*;

import static java.util.Objects.*;
import static org.luwrain.io.bookdoc.loaders.Utils.*;

public final class LoaderImpl extends Loader
{
    static private final Logger log = LogManager.getLogger();
    static private final String DEFAULT_CHARSET = "UTF-8";

    final OkHttpClient client;
    final Tika tika = new Tika();
    final URL requestedUrl;
    final String requestedContentType;
    final String requestedTagRef;

        private String contentType = null;
    private Charset charset = null;
    
    private String requestedCharset = "";
        private URL responseUrl = null;
    private String responseContentEncoding = "";

    LoaderImpl(OkHttpClient client, URI uri, String contentType)
    {
	this.client = requireNonNull(client, "client can't be null");
	requireNonNull(uri, "uri can't be null");
	this.requestedContentType = requireNonNullElse(contentType, "").trim();
	try {
this.requestedUrl = uri.toURL();
	}
	catch(MalformedURLException e)
	{
	    log.error("Unable to convert the address {} to URL", uri.toString(), e);
	    throw new IllegalArgumentException(e);
	}
		    this.requestedTagRef = requestedUrl.getRef();
    }

    public LoaderImpl(URI uri, String contentType)
    {
	this(newHttpClient(), uri, contentType);
    }

    @Override public Doc load() throws IOException
    {
	try (final var fetch = new OkHttpFetch(client, requestedUrl.toString())) {
	    log.trace("Fetching {}", requestedUrl);
	    fetch.fetch();
	    final String responseContentType;
	    if (fetch.getContentType() != null)
	    {
		final var b = new StringBuilder();
		b.append(fetch.getContentType().type());
		if (!requireNonNullElse(fetch.getContentType().subtype(), "").isBlank())
		    b.append("/").append(fetch.getContentType().subtype());
		responseContentType = new String(b);
	    } else
		responseContentType = null;
	    log.trace("Response content type is {}", responseContentType);
	    this.contentType =requireNonNullElse( requestedContentType.isBlank()?responseContentType:requestedContentType.trim(), "");
	    		log.trace("Content type is {}", contentType);
	    if (contentType.isBlank())
	    {
		final String detected = tika.detect(requestedUrl.toString());
		log.trace("Trying to autodetect the content type, detecting {}", detected);
		contentType = detected;
	    }
	    if (contentType.isBlank())
		throw new IOException("Unable to choose the content type for " + requestedUrl.toString());
	    charset = fetch.getContentType().charset();
	    /*
	    if (!this.requestedCharset.isEmpty())
		this.selectedCharset = this.requestedCharset;
	    */
	    if (charset == null)
		charset = StandardCharsets.UTF_8;
		final var filter = Filter.loadForContentType(contentType);
		if (filter == null)
		    throw new IOException("No suitable handler for the content type: " + contentType);
		final Properties props = new Properties();
		props.setProperty("url", responseUrl.toString());
		props.setProperty("charset", charset.toString());
		final Doc doc;
		try (final InputStream is = Files.newInputStream(fetch.getPath())) {
doc = filter.load(is, props);
		}
	    if (doc == null)
		throw new IOException("No suitable handler for the content type: " + contentType);
	    //	    res.doc.setProperty("hash", getTmpFileHash());
	    doc.setProperty("url", responseUrl.toString());
	    doc.setProperty("contenttype", contentType);
	    if (requestedTagRef != null)
		doc.setProperty("startingref", requestedTagRef);
	    return doc;
	}
    }

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
