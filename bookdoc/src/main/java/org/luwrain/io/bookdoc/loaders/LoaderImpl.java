// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

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
import okhttp3.HttpUrl;

import org.luwrain.io.bookdoc.*;
import org.luwrain.io.bookdoc.filters.*;
import org.luwrain.util.*;

import static java.util.Objects.*;

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

    private String responseUrl = null;

    LoaderImpl(OkHttpClient client, URL url, String contentType)
    {
	this.client = requireNonNull(client, "client can't be null");
	requireNonNull(url, "url can't be null");
	this.requestedContentType = requireNonNullElse(contentType, "").trim();
	this.requestedUrl = url;
	this.requestedTagRef = requestedUrl.getRef();
    }

    public LoaderImpl(URL url, String contentType)
    {
	this(newHttpClient(), url, contentType);
    }

    public LoaderImpl(HttpUrl url, String contentType)
    {
	this(newHttpClient(), url.url(), contentType);
    }

    @Override public Doc load() throws IOException
    {
	try (final var fetch = new OkHttpFetch(client, requestedUrl.toString())) {
	    log.trace("Fetching {}", requestedUrl);
	    fetch.fetch();

	    // The actual URL of the response, taking redirects into account.
	    responseUrl = fetch.getResponseUrl();

	    // Content type and charset from the response headers.
	    final MediaType responseMediaType = fetch.getContentType();
	    final String responseContentType = mediaTypeToContentType(responseMediaType);
	    final Charset responseCharset = responseMediaType != null?responseMediaType.charset():null;
	    log.trace("Response content type is {}, charset is {}", responseContentType, responseCharset);

	    // Content type and charset from the explicitly requested content type.
	    final MediaType requestedMediaType = requestedContentType.isBlank()?null:MediaType.parse(requestedContentType);
	    final String requestedContentTypeOnly = mediaTypeToContentType(requestedMediaType);
	    final Charset requestedCharset = requestedMediaType != null?requestedMediaType.charset():null;
	    log.trace("Requested content type is {}, charset is {}", requestedContentTypeOnly, requestedCharset);

	    // Determine the effective content type: explicit request wins,
	    // then the response header, then Tika autodetection.
	    if (requestedContentTypeOnly != null && !requestedContentTypeOnly.isBlank())
		contentType = requestedContentTypeOnly;
	    else if (responseContentType != null && !responseContentType.isBlank())
		contentType = responseContentType;
	    else
	    {
		final String detected = tika.detect(requestedUrl.toString());
		log.trace("Trying to autodetect the content type, detecting {}", detected);
		contentType = detected;
	    }
	    log.trace("Content type is {}", contentType);

	    if (contentType == null || contentType.isBlank())
		throw new IOException("Unable to choose the content type for " + requestedUrl.toString());

	    // Determine the effective charset: explicit request wins,
	    // then the response header, then UTF-8.
	    if (requestedCharset != null)
		charset = requestedCharset;
	    else if (responseCharset != null)
		charset = responseCharset;
	    else
		charset = StandardCharsets.UTF_8;
	    log.trace("Charset is {}", charset);

	    final var filter = Filter.loadForContentType(contentType);
	    if (filter == null)
		throw new IOException("No suitable handler for the content type: " + contentType);

	    final Properties props = new Properties();
	    props.setProperty(Filter.PROP_URL, responseUrl);
	    props.setProperty(Filter.PROP_CHARSET, charset.toString());

	    final Doc doc;
	    try (final InputStream is = Files.newInputStream(fetch.getPath())) {
		doc = filter.load(is, props);
	    }
	    if (doc == null)
		throw new IOException("No suitable handler for the content type: " + contentType);

	    doc.setProperty(Doc.PROP_URL, responseUrl);
	    doc.setProperty("contenttype", contentType);
	    if (requestedTagRef != null)
		doc.setProperty(Doc.PROP_STARTING_REF, requestedTagRef);
	    return doc;
	}
    }

    static private String mediaTypeToContentType(MediaType mediaType)
    {
	if (mediaType == null)
	    return null;
	final var b = new StringBuilder();
	b.append(mediaType.type());
	if (!requireNonNullElse(mediaType.subtype(), "").isBlank())
	    b.append("/").append(mediaType.subtype());
	return new String(b);
    }

    private String makeTitleFromUrl()
    {
	final String path = requestedUrl.getPath();
	if (path == null || path.isEmpty())
	    return requestedUrl.toString();
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
