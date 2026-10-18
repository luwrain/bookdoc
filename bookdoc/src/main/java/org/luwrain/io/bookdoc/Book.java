// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.*;
import java.net.*;

/**
 * A book consisting of one or more documents.
 *
 * <p>The book interface abstracts access to the document collection of a
 * publication. It provides the list of book sections, the default document,
 * individual documents and audio-related lookups.</p>
 */
public interface Book
{
    /** The property containing the DAISY local path. */
    static public final String
	    PROP_DAISY_LOCAL_PATH = "daisy.localpath";

    /** The flags supported by books. */
    public enum Flags {OPEN_IN_SECTION_TREE};

    /**
     * Returns the identifier of this book.
     *
     * @return the book identifier
     */
    String getBookId();

    /**
     * Returns the flags assigned to this book.
     *
     * @return the set of flags; never {@code null}
     */
    Set<Flags> getBookFlags();

    /**
     * Returns the sections of this book.
     *
     * @return an array of sections; never {@code null}
     */
    Section[] getBookSections();

    /**
     * Loads the document addressed by the supplied URL.
     *
     * @param href the document URL
     * @return the loaded document
     * @throws java.io.IOException if the document cannot be loaded
     */
    Doc getDoc(String href) throws java.io.IOException;

    /**
     * Returns the default document of this book.
     *
     * @return the default document
     */
    Doc getDefaultDoc();

    /**
     * Returns all documents in this book.
     *
     * @return an array of documents; never {@code null}
     */
    Doc[] getDocs();

    /**
     * Resolves the supplied resource name to a URL.
     *
     * @param resName the resource name
     * @return the resolved resource URL
     */
    String getResourceUrl(String resName);

    /**
     * Finds the audio fragment associated with the supplied identifier.
     *
     * @param ids the element identifier
     * @return the associated audio fragment, or {@code null} if there is none
     */
    Audio findAudioForId(String ids);

    /**
     * Finds the text associated with the supplied position in an audio resource.
     *
     * @param audioFileUrl the URL of the audio resource
     * @param msec the position in milliseconds
     * @return the associated text, or {@code null} if there is none
     */
    String findTextForAudio(String audioFileUrl, long msec);

    /**
     * A section of a book.
     */
    static public final class Section
    {
	/** The nesting level of this section. */
	public final int level;
	/** The title of this section. */
	public final String title;
	/** The URL of the document containing this section. */
	public final String href;

	/**
	 * Creates a book section.
	 *
	 * @param level the nesting level; must not be negative
	 * @param title the section title; must not be {@code null}
	 * @param href the document URL; must not be {@code null}
	 * @throws NullPointerException if {@code title} or {@code href} is {@code null}
	 * @throws IllegalArgumentException if {@code level} is negative
	 */
	public Section(int level, String title, String href)
	{
	    if (title == null)
		throw new NullPointerException("title can' tbe null");
	    if (href == null)
		throw new NullPointerException("href can't be null");
	    if (level < 0)
		throw new IllegalArgumentException("level (" + String.valueOf(level) + ") can't be negative");
	    this.level = level;
	    this.title = title;
	    this.href = href;
	}

	@Override public String toString()
	{
	    return title;
	}
    }

    /**
     * Creates a DAISY 2.02 book from the supplied NCC document.
     *
     * @param nccDoc the NCC document; must not be {@code null}
     * @return the newly created book
     * @throws NullPointerException if {@code nccDoc} is {@code null}
     */
    static public Book newDaisy22(Doc nccDoc)
    {
	if (nccDoc == null)
	    throw new NullPointerException("nccDoc can't be null");
	final org.luwrain.io.filters.daisy22.Daisy22 d = new org.luwrain.io.filters.daisy22.Daisy22();
	d.init(nccDoc);
	return d;
    }
}
