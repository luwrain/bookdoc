// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.regex.*;
import java.net.*;

/**
 * An audio fragment associated with a particular location in a book.
 *
 * <p>An audio fragment references an audio resource by URL and identifies a
 * time interval inside that resource. Instances are immutable. A fragment may
 * cover the entire resource, everything starting at a particular position, or
 * a finite interval bounded by a begin and an end position.</p>
 *
 * <p>All positions are expressed in milliseconds. A negative {@code endPos}
 * means that no upper bound is specified.</p>
 */
public final class Audio
{
    /** The URL of the audio resource. Never {@code null}. */
    public final String src;
    /** The beginning of the fragment in milliseconds. May be negative if unspecified. */
    public final long beginPos;
    /** The end of the fragment in milliseconds. A negative value means that the fragment has no upper bound. */
    public final long endPos;

    /**
     * Creates a fragment starting at the specified position and continuing to
     * the end of the resource.
     *
     * @param src the audio resource URL; must not be {@code null}
     * @param beginPos the starting position in milliseconds
     * @throws NullPointerException if {@code src} is {@code null}
     */
    public Audio(String src, long beginPos)
    {
	if (src == null)
	    throw new NullPointerException("src can't be null");
	this.src = src;
	this.beginPos = beginPos;
	this.endPos = -1;
    }

    /**
     * Creates a fragment covering the whole audio resource.
     *
     * @param src the audio resource URL; must not be {@code null}
     * @throws NullPointerException if {@code src} is {@code null}
     */
    public Audio(String src)
    {
	if (src == null)
	    	    throw new NullPointerException("src can't be null");
	this.src = src;
	this.beginPos = -1;
	this.endPos = -1;
    }

    /**
     * Creates a fragment covering a finite interval of the audio resource.
     *
     * @param src the audio resource URL; must not be {@code null}
     * @param beginPos the starting position in milliseconds
     * @param endPos the ending position in milliseconds
     * @throws NullPointerException if {@code src} is {@code null}
     */
    public Audio(String src, long beginPos, long endPos)
    {
	if (src == null)
	    	    throw new NullPointerException("src can't be null");
	this.src = src;
	this.beginPos = beginPos;
	this.endPos = endPos;
    }

    /**
     * Checks whether this fragment covers the supplied position in the
     * supplied audio resource.
     *
     * @param audioFileUrl the URL of the audio resource to check
     * @param msec the position to check, in milliseconds
     * @return {@code true} if the resource matches and the position lies within
     *         this fragment, {@code false} otherwise
     */
    public boolean covers(String audioFileUrl, long msec)
    {
	if (!src.equals(audioFileUrl))
	    return false;
	if (endPos < 0)
	    return msec >= beginPos;
	return msec >= beginPos && msec <= endPos;
    }

    /**
     * Checks whether this fragment covers the supplied position in the
     * supplied audio resource. Unlike {@link #covers(String, long)}, this
     * method resolves both URLs against a common base URL before comparing
     * them, which is useful for relative references.
     *
     * @param audioFileUrl the URL of the audio resource to check
     * @param msec the position to check, in milliseconds
     * @param baseUrl the base URL used for resolving relative references
     * @return {@code true} if the resolved resource URLs match and the position
     *         lies within this fragment, {@code false} otherwise
     */
    public boolean covers(String audioFileUrl, long msec, URL baseUrl)
    {
	try {
	    if (!(new URL(baseUrl, src).toString()).equals(new URL(baseUrl,audioFileUrl).toString()))
		return false;
	}
	catch(MalformedURLException e)
	{
	    e.printStackTrace();
	    return false;
	}
	if (endPos < 0)
	    return msec >= beginPos;
	return msec >= beginPos && msec <= endPos;
    }

    @Override public String toString()
    {
	return "Audio: " + src + " (from " + beginPos + ", to " + endPos + ")";
    }
}
