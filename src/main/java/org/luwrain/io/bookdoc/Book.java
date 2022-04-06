
package org.luwrain.io.bookdoc;

import java.util.*;
import java.net.*;

public interface Book
{
	static public final String
	    PROP_DAISY_LOCAL_PATH = "daisy.localpath";

    public enum Flags {OPEN_IN_SECTION_TREE};

    String getBookId();
    Set<Flags> getBookFlags();
    Section[] getBookSections();
    Doc getDoc(String href) throws java.io.IOException;
    Doc getDefaultDoc();
    Doc[] getDocs();
    String getResourceUrl(String resName);
    Audio findAudioForId(String ids);
    String findTextForAudio(String audioFileUrl, long msec);

    static public final class Section
    {

	
	public final int level;
	public final String title;
	public final String href;
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

    static public Book newDaisy22(Doc nccDoc)
    {
	if (nccDoc == null)
	    throw new NullPointerException("nccDoc can't be null");
	final org.luwrain.io.filters.daisy22.Daisy22 d = new org.luwrain.io.filters.daisy22.Daisy22();
	d.init(nccDoc);
	return d;
    }
}
