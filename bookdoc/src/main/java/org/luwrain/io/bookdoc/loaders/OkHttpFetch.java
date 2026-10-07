// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.loaders;

import java.util.concurrent.*;
import java.io.*;
import java.nio.file.*;
import org.apache.logging.log4j.*;

import okhttp3.*;
import org.luwrain.util.*;

import static java.util.Objects.*;

final class OkHttpFetch implements AutoCloseable
{
    static private final Logger log = LogManager.getLogger();

    final OkHttpClient client;
    final String url;
    private final TempDir tempDir;
    private final Path tmpFile;
    private MediaType contentType = null;
    private long contentLength = -1;
    private String responseUrl = null;

    public OkHttpFetch(OkHttpClient client, String url)
    {
	this.client = requireNonNull(client, "client can't be null");
	this.url = requireNonNull(url, "url can't be null");
	if (url.isBlank())
	    throw new IllegalArgumentException("url can't be blank");
	this.tempDir = new TempDir();
	this.tmpFile = tempDir.getPath().resolve("download");
    }

    void fetch() throws IOException
    {
	final Request.Builder requestBuilder = new Request.Builder()
	.url(url)
	.header("User-Agent", Connections.DEFAULT_USER_AGENT);
	final Request request = requestBuilder.build();
	final Call call = client.newCall(request);
	try (final Response response = call.execute()) {
	    final int code = response.code();
	    log.trace("Response code {} for {}", code, url);
	    if (code != 200)
	    {
		log.error("Response code {} for {}", code, url);
		throw new IOException("Invalid response code: " + code);
	    } else
		log.trace("Response code {} for {}", code, url);
	    final ResponseBody body = response.body();
	    if (body == null)
	    {
		log.error("Empty response body for {}", url);
		throw new IOException("Empty response body for " + url);
	    }
	    contentLength = body.contentLength();
	    contentType = body.contentType();
	    responseUrl = response.request().url().toString();
	    log.trace("Content length={}, contentType={}, responseUrl={}", contentLength, contentType, responseUrl);
	    try (final InputStream is = body.byteStream();
		 final OutputStream os = Files.newOutputStream(tmpFile)) {
		StreamUtils.copyAllBytes(is, os, null, null);
	    }
	}
    }

    MediaType getContentType()
    {
	if (contentType == null)
	    throw new IllegalStateException("The file is not fetched");
	return contentType;
    }

    long getContentLength()
    {
	if (contentLength < 0)
	    throw new IllegalStateException("The file is not fetched");
	return contentLength;
    }

    String getResponseUrl()
    {
	if (responseUrl == null)
	    throw new IllegalStateException("The file is not fetched");
	return responseUrl;
    }

    Path getPath()
    {
	return tmpFile;
    }

    @Override public void close()
    {
	tempDir.close();
    }
}
