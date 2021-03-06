
package org.luwrain.io.filters.daisy22;

import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.io.filters.*;
import org.luwrain.io.bookdoc.*;

import org.luwrain.io.bookdoc.Book.Section;
import org.luwrain.io.filters.daisy22.Smil.Entry;
import org.luwrain.io.filters.daisy22.Smil.Entry.Type;

public final class Daisy22 implements Book
{
    static private final String
	LOG_COMPONENT = "daisy";

    protected final Map<String, Doc> docs = new HashMap<>();
    protected final Map<String, Entry> smils = new HashMap<>();
    protected Doc nccDoc = null;
    protected URL nccDocUrl = null;
    protected Book.Section[] bookSections = new Book.Section[0];

        @Override public String getResourceUrl(String resName)
    {
	try {
	    if (nccDocUrl == null)
		return new URL(resName).toString();
	    return new URL(nccDocUrl, resName).toString();
	}
	catch(MalformedURLException e)
	{
	    throw new IllegalArgumentException(e);
	}
    }

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
	final String urlStr, noRefUrlStr, ref;
	try {
	    final URL
	    url = new URL(href),
	    noRefUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile());
	    urlStr = url.toString();
	    noRefUrlStr = noRefUrl.toString();
	    ref = url.getRef();
	}
	catch(MalformedURLException e)
	{
	    throw new IllegalArgumentException(e);
	}
	//Checking the SMILs for the requested URL
	if (smils.containsKey(noRefUrlStr))
	{
	    final Entry entry = smils.get(noRefUrlStr);
	    final Entry requested = entry.findById(ref);
	    //There is an appropriate SMIL entry
	    if (requested != null)
	    {
		if (requested.type == Type.PAR || requested.type == Type.SEQ)
		{
		    final List<String> links = new ArrayList<>();
		    collectTextStartingAtEntry(requested, links);
		    if (!links.isEmpty())
			return getDoc(links.get(0));
		    return null;
		} else
		    if (requested.type == Type.TEXT)
			return getDoc(requested.src); else
			return null;
	    }
	} //SMILs
	if (docs.containsKey(noRefUrlStr))
	{
	    final Doc res = docs.get(noRefUrlStr);
	    if (res != null && ref != null)
		res.setProperty(Doc.PROP_STARTING_REF, ref); else
		res.setProperty("startingref", "");
	    return res;
	}
	if (nccDoc.getProperty(Doc.PROP_URL).equals(urlStr))
	{
	    if (ref != null)
		nccDoc.setProperty(Doc.PROP_STARTING_REF, ref); else
		nccDoc.setProperty("startingref", "");
	    return nccDoc;
	}
	return null;
    }

    @Override public Doc[] getDocs()
    {
	final List<Doc> res = new ArrayList();
	for(Map.Entry<String, Doc> e: docs.entrySet())
	    res.add(e.getValue());
	return res.toArray(new Doc[res.size()]);
    }

    @Override public Audio findAudioForId(String id)
    {
	for(Map.Entry<String, Entry> e: smils.entrySet())
	{
	    final Entry entry = findSmilEntryWithText(e.getValue(), id);
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
	for(Map.Entry<String, Entry> e: smils.entrySet())
	{
	    final Smil.Entry entry = findSmilEntryWithAudio(e.getValue(), audioFileUrl, msec);
	    if (entry != null)
	    {
		final List<String> links = new ArrayList<>();
		collectTextStartingAtEntry(entry, links);
		if (links.size() > 0)
		    return links.get(0);
	    }
	}
	return null;
    }

    public void init(Doc nccDoc)
    {
	try {
	final URL nccDocUrl = new URL(nccDoc.getProperty(Doc.PROP_URL));
		nccDoc.setProperty(Doc.PROP_DAISY_LOCAL_PATH, nccDocUrl.getFile());//FIXME:Leave only base file name
	final String[] allHrefs = nccDoc.getHrefs();
	final List<String> textSrcs = new ArrayList<>();
	for(String h: allHrefs)
	{
		URL url = new URL(nccDocUrl, h);
		url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile());
		if (url.getFile().toLowerCase().endsWith(".smil"))
		    loadSmil(url.toString(), textSrcs); else
		    textSrcs.add(h);
	}
	for(String s: textSrcs)
	{
		URL url = new URL(nccDocUrl, s);
		url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile());
		loadDoc(s, url.toString());
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
	catch(IOException e)
	{
	    throw new RuntimeException(e);
	}
    }

    @Override public Book.Section[] getBookSections()
    {
	return bookSections;
    }

    private void loadSmil(String url, List<String> textSrcs)
    {
	if (smils.containsKey(url))
	    return;
	try {
	final Entry smil = Smil.fromUrl(url);
	smils.put(url, smil);
	smil.saveTextSrc(textSrcs);
	    smil.allSrcToUrls(url);
	}
	catch(IOException e)
	{
	    throw new RuntimeException(e);
	}
    }

    private void loadDoc(String localPath, String url)
    {
	if (docs.containsKey(url))
	    return;
	final Doc doc;
		try {
	final Loader loader = Loader.newDefaultLoader(new URI(url), null);
doc = loader.load();
		}
	catch(Exception e)
	{
	    throw new RuntimeException(e);
	}
	doc.setProperty(PROP_DAISY_LOCAL_PATH, localPath);
	docs.put(url, doc);
    }

    static private Entry findSmilEntryWithText(Entry entry, String src)
    {
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
	    throw new IllegalStateException("Unknown SMIL entry type: " + entry.type);
	}
    }

private Entry findSmilEntryWithAudio(Smil.Entry entry, String audioFileUrl, long msec)
    {
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

    static private void collectTextStartingAtEntry(Entry entry, List<String> links)
    {
	switch(entry.type)
	{
	case AUDIO:
	    return;
	case TEXT:
	    links.add(entry.src);
	    return;
	case PAR:
		for(Entry e: entry.entries)
		    collectTextStartingAtEntry(e, links);
	    return;
	case FILE:
	case SEQ:
	    if (entry.entries.length >= 1)
		collectTextStartingAtEntry(entry.entries[0], links);
	    return;
	default:
	    throw new IllegalStateException("Unknown SMIL entry type: " + entry.type);
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
}
