
package org.luwrain.io.filters.daisy22;

import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.io.filters.*;
import org.luwrain.io.bookdoc.*;
import org.luwrain.io.bookdoc.Book.Section;
import org.luwrain.io.filters.daisy22.Smil.Entry;

final class Daisy2 implements Book
{
    static private final String
	LOG_COMPONENT = "daisy";

    protected final Map<URL, Doc> docs = new HashMap<>();
    protected final Map<URL, Smil.Entry> smils = new HashMap<>();
    protected Doc nccDoc = null;
    protected URL nccDocUrl = null;
    protected Book.Section[] bookSections = new Book.Section[0];

    @Override public String getBookId()
    {
	return "FIXME";
    }

    public Set<Flags> getBookFlags()
    {
	return EnumSet.of(Flags.OPEN_IN_SECTION_TREE);
    }

    @Override public Doc getDefaultDoc()
    {
	return nccDoc;
    }

    @Override public Doc getDoc(String href)
    {
	final URL url, noRefUrl;
	try {
	    url = new URL(href);
	    noRefUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile());
	}
	catch(MalformedURLException e)
	{
	    throw new IllegalArgumentException(e);
	}
	final String
	urlStr = url.toString(),
	noRefUrlStr = noRefUrl.toString();
	if (smils.containsKey(noRefUrl))
	{
	    final Smil.Entry entry = smils.get(noRefUrl);
	    final Smil.Entry requested = entry.findById(url.getRef());
	    if (requested != null)
	    {
		if (requested.type == Smil.Entry.Type.PAR || requested.type == Smil.Entry.Type.SEQ)
		{
		    final LinkedList<String> links = new LinkedList<String>();
		    collectTextStartingAtEntry(requested, links);
		    if (!links.isEmpty())
		    {
			final String link = links.getFirst();
			return getDoc(link);
		    }
		    return null;
		} else
		if (requested.type == Smil.Entry.Type.TEXT)
		    return getDoc(requested.src); else
		{
		    Log.warning("doctree-daisy", "URL " + href + " points to a SMIL entry, but its type is " + requested.type);
		    return null;
		}
	    }
	} //smils;
	if (docs.containsKey(noRefUrl))
	{
	    final Doc res = docs.get(noRefUrl);
	    if (res != null && url.getRef() != null)
		res.setProperty(Doc.PROP_STARTING_REF, url.getRef()); else
		res.setProperty("startingref", "");
	    return res;
	}
	if (nccDoc.getProperty(Doc.PROP_URL).equals(urlStr))
	{
	    if (url.getRef() != null)
		nccDoc.setProperty("startingref", url.getRef()); else
		nccDoc.setProperty("startingref", "");
	    return nccDoc;
	}
	Log.warning("doctree", "unable to find a document in Daisy2 book for URL:" + url.toString());
	return null;
    }

    @Override public Audio findAudioForId(String id)
    {
	NullCheck.notNull(id, "id");
	Log.debug("doctree-daisy", "searching audio for " + id);
	for(Map.Entry<URL, Smil.Entry> e: smils.entrySet())
	{
	    final Smil.Entry entry = findSmilEntryWithText(e.getValue(), id);
	    if (entry != null)
	    {
		final List<Audio> infos = new ArrayList<>();
		collectAudioStartingAtEntry(entry, infos);
		if (infos.size() > 0)
		    return infos.get(0);
	    }
	}
	return null;
    }

    @Override public     String findTextForAudio(String audioFileUrl, long msec)
    {
	NullCheck.notNull(audioFileUrl, "audioFileUrl");
	Log.debug("doctree-daisy", "text for " + audioFileUrl + " at " + msec);
	for(Map.Entry<URL, Smil.Entry> e: smils.entrySet())
	{
	    final Smil.Entry entry = findSmilEntryWithAudio(e.getValue(), audioFileUrl, msec);
	    if (entry != null)
	    {
		final LinkedList<String> links = new LinkedList<String>();
		collectTextStartingAtEntry(entry, links);
		if (links.size() > 0)
		    return links.getFirst();
	    }
	}
	return null;
    }

    void init(Doc nccDoc) throws IOException
    {
	final URL nccDocUrl = new URL(nccDoc.getProperty(Doc.PROP_URL));
		nccDoc.setProperty(Doc.PROP_DAISY_LOCAL_PATH, nccDocUrl.getFile());//FIXME:Leave only base file name
	final String[] allHrefs = nccDoc.getHrefs();
	final LinkedList<String> textSrcs = new LinkedList<String>();
	for(String h: allHrefs)
	{
		URL url = new URL(nccDocUrl, h);
		url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile());
		if (url.getFile().toLowerCase().endsWith(".smil"))
		    loadSmil(url, textSrcs); else
		    textSrcs.add(h);
	}
	for(String s: textSrcs)
	{
		URL url = new URL(nccDocUrl, s);
		url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile());
		loadDoc(s, url);
	    }
	this.nccDoc = nccDoc;
	this.nccDocUrl = nccDocUrl;
	final SectionsVisitor visitor = new SectionsVisitor();
	Visitor.walk(nccDoc.getRoot(), visitor);
	final Section[] sections = visitor.sections.toArray(new Section[visitor.sections.size()]);
	for(int i = 0;i < sections.length;++i)
	{
	    try {
		final URL url = new URL(sections[i].href);
		final URL fileUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile());
		if (fileUrl.getFile().toLowerCase().endsWith(".smil") && !url.getRef().isEmpty())
		{
		    final String text = smilEntryToText(fileUrl, url.getRef());
		    if (text != null)
			sections[i] = new Book.Section(sections[i].level, sections[i].title, text);
		}
	    }
	    catch(MalformedURLException e)
	    {
		e.printStackTrace();
	    }
	}
	this.bookSections = sections;
	/*
	try {
	    this.bookPath = Paths.get(nccDoc.getUrl().toURI()).getParent().resolve("luwrain.book");
	}
	catch(URISyntaxException e)
	{
	    e.printStackTrace();
	    bookPath = null;
	}
	if (bookPath != null)
	    Log.debug("doctree-daisy", "book path set to " + bookPath.toString()); else
	    Log.debug("doctree-daisy", "book path isn\'t set");
	*/
    }

    @Override public Book.Section[] getBookSections()
    {
	return bookSections;
    }

    private void loadSmil(URL url, List<String> textSrcs)
    {
	NullCheck.notNull(url, "url");
	if (smils.containsKey(url))
	    return;
	final Smil.Entry smil = Smil.fromUrl(url);
	if (smil == null)
	    throw new RuntimeException("Unable to load SMIL from " + url.toString());
	smils.put(url, smil);
	smil.saveTextSrc(textSrcs);
	try {
	    smil.allSrcToUrls(url); 
	}
	catch(MalformedURLException e)
	{
	    e.printStackTrace();
	}
    }

    private void loadDoc(String localPath, URL url)
    {
	if (docs.containsKey(url))
	    return;
	Loader.Result res;
	try {
res = loadDoc(url);
	}
	catch(Exception e)
	{
	    Log.error("doctree-daisy", "unable to read a document from URL " + url.toString());
	    e.printStackTrace();
	    return;
	}
	if (res.book != null)
	{
	    Log.debug("doctree-daisy", "the URL " + url + "references a book, not including to current one");
	    return;
	}
	res.doc.setProperty("daisy.localpath", localPath);
	docs.put(url, res.doc);
    }

    static private Smil.Entry findSmilEntryWithText(Smil.Entry entry, String src)
    {
	NullCheck.notNull(entry, "entry");
	NullCheck.notNull(src, "src");
	switch(entry.type )
	{
	case TEXT:
	    return (entry.src != null && entry.src.equals(src))?entry:null;
	case AUDIO:
	    return null;
	case FILE:
	case SEQ:
	    for (int i = 0;i < entry.entries.length;++i)
	    {
		final Smil.Entry res = findSmilEntryWithText(entry.entries[i], src);
		if (res == null)
		    continue;
		if (res != entry.entries[i])
		    return res;
		if (i == 0)
		    return entry;
		return entry.entries[i];
	    }
	    return null;
	case PAR:
	    for(Smil.Entry e: entry.entries)
	    {
		final Smil.Entry res = findSmilEntryWithText(e, src);
		if (res != null)
		    return entry;
	    }
	    return null;
	default:
	    Log.warning("doctree-daisy", "unknown SMIL entry type:" + entry.type);
	    return null;
	}
    }

private Smil.Entry findSmilEntryWithAudio(Smil.Entry entry, String audioFileUrl, long msec)
    {
	NullCheck.notNull(entry, "entry");
	NullCheck.notNull(audioFileUrl, "audioFileUrl");
	switch(entry.type )
	{
	case AUDIO:
	    return entry.audioInfo.covers(audioFileUrl, msec, nccDocUrl)?entry:null;
	case TEXT:
	    return null;
	case FILE:
	case SEQ:
	    for (int i = 0;i < entry.entries.length;++i)
	    {
		final Smil.Entry res = findSmilEntryWithAudio(entry.entries[i], audioFileUrl, msec);
		if (res == null)
		    continue;
		if (res != entry.entries[i])
		    return res;
		if (i == 0)
		    return entry;
		return entry.entries[i];
	    }
	    return null;
	case PAR:
	    for(Smil.Entry e: entry.entries)
	    {
		final Smil.Entry res = findSmilEntryWithAudio(e, audioFileUrl, msec);
		if (res != null)
		    return entry;
	    }
	    return null;
	default:
	    Log.warning("doctree-daisy", "unknown SMIL entry type:" + entry.type);
	    return null;
	}
    }

    static private void collectAudioStartingAtEntry(Entry entry, List<Audio> audioInfos)
    {
	NullCheck.notNull(entry, "entry");
	NullCheck.notNull(audioInfos, "audioInfos");
	switch(entry.type)
	{
	case AUDIO:
	    audioInfos.add(entry.audioInfo);
	    return;
	case TEXT:
	    return;
	case PAR:
		for(Smil.Entry e: entry.entries)
		    collectAudioStartingAtEntry(e, audioInfos);
	    return;
	case FILE:
	case SEQ:
	    if (entry.entries.length >= 1)
		collectAudioStartingAtEntry(entry.entries[0], audioInfos);
	    return;
	default:
	    Log.warning("doctree-daisy", "unknown SMIL entry type:" + entry.type);
	}
    }

    static private void collectTextStartingAtEntry(Smil.Entry entry, LinkedList<String> links)
    {
	NullCheck.notNull(entry, "entry");
	NullCheck.notNull(links, "links");
	switch(entry.type)
	{
	case AUDIO:
	    return;
	case TEXT:
	    links.add(entry.src);
	    return;
	case PAR:
		for(Smil.Entry e: entry.entries)
		    collectTextStartingAtEntry(e, links);
	    return;
	case FILE:
	case SEQ:
	    if (entry.entries.length >= 1)
		collectTextStartingAtEntry(entry.entries[0], links);
	    return;
	default:
	    Log.warning("doctree-daisy", "unknown SMIL entry type:" + entry.type);
	}
    }

    private String smilEntryToText(URL url, String id)
    {
		    if (!smils.containsKey(url))
			return null;
			final Smil.Entry entry = smils.get(url).findById(id);
			if (entry == null)
			    return null;
			final LinkedList<String> links = new LinkedList<String>();
			collectTextStartingAtEntry(entry, links);
			return !links.isEmpty()?links.getFirst():null;
		    }

    private Loader.Result loadDoc(URL url) throws IOException
    {
	final Loader loader;
	try {
	    loader = Loader.newDefaultLoader(url.toURI(), null);
	}
	catch(URISyntaxException e)
	{
	    throw new IllegalArgumentException(e);
	}
return loader.load();
    }
}
