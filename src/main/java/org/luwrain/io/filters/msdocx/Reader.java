
package org.luwrain.io.filters.msdocx;

//http://poi.apache.org/apidocs/4.1/org/apache/poi/xwpf/usermodel/XWPFParagraph.html?sa=X&ved=2ahUKEwjW3bOUgonnAhX7xcQBHZg5BOYQFjAAegQIAhAB
//https://poi.apache.org/apidocs/4.0/org/apache/poi/xwpf/usermodel/XWPFTableCell.html
//https://stackoverflow.com/questions/19676895/apache-poi-get-headings-from-doc-file?sa=X&ved=2ahUKEwiKmfH4gonnAhXlwsQBHZsmAgMQFjAAegQIARAB
//https://poi.apache.org/apidocs/dev/org/apache/poi/xwpf/usermodel/XWPFDocument.html

import java.util.*;
import java.io.*;

import com.google.gson.*;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.xwpf.extractor.*;

public final class Reader
{
    public final Document output = new Document();

    public void read(File file) throws IOException
    {
	final InputStream is = new FileInputStream(file);
	try {
	    final XWPFDocument doc = new XWPFDocument(is);
	    final XWPFStyles styles = doc.getStyles();         
	    final List<IBodyElement> body = doc.getBodyElements();
	    for(IBodyElement el: body)
		onElement(el, styles);
	    /*
	    final Range range = doc.getRange();
	    for(int i = 0;i < range.numParagraphs();i++)
	    {
		final Paragraph p = range.getParagraph(i);
		final String t = cleanLine(getParagraphText(p)).trim();
		if (t.isEmpty())
		    continue;
		final StringBuilder b = new StringBuilder();
		if (p.getLvl() < 9)
		    b.append("%h").append(String.valueOf(p.getLvl() + 1)).append(" ");
		b.append(t);
		System.out.println(new String(b));
		System.out.println();
	    }
	    final WordExtractor extractor = new WordExtractor(doc);
	    try {
		for(String s: extractor.getFootnoteText())
		{
		    final String ss = cleanLine(s).trim();
		    if (ss.isEmpty())
			continue;
		    System.out.println("%footnote " + ss);
		    System.out.println("");
		}
	    }
	    finally {
		extractor.close();
	    }
	    */
	}
	finally {
	    is.close();
	}
    }

    private void onElement(IBodyElement el, XWPFStyles styles)
    {
			if (el instanceof XWPFParagraph)
		{
		    onParagraph((XWPFParagraph)el, styles);
return;
		}
			if (el instanceof XWPFTable)
			{
			    onTable((XWPFTable)el, styles);
			    return;
			}
		System.err.println("WARNING: Unhandled body element: " + el.getClass().getName());
	    }

    private void onTable(XWPFTable table, XWPFStyles styles)
    {
	for(XWPFTableRow r: table.getRows())
	    for(XWPFTableCell c: r.getTableCells())
		for(IBodyElement e: c.getBodyElements())
		    onElement(e, styles);
    }

    private void onParagraph(XWPFParagraph p, XWPFStyles styles)
    {
	final String text = p.getText().trim();
	if (text.trim().isEmpty())
	{
	    output.addItem(new Item(Item.TYPE_PARAGRAPH));
	    return;
	}
	final Item item = new Item(Item.TYPE_PARAGRAPH);
	final StringBuilder b = new StringBuilder();
	final String styleId = p.getStyleID();
	if (styleId != null)
	{
	    final XWPFStyle style = styles.getStyle(styleId);
	    if(style != null)
	    {
		final String name = style.getName();
		if (name != null)
		    item.style = name.trim();
	    }
	}
item.text = cleanLine(text).trim();

if (p.getAlignment() != null)
    item.alignment = p.getAlignment().toString();
item.firstLineIndent = p.getFirstLineIndent();
item.fontAlignment = p.getFontAlignment();

int minFontSize = 1000;
int maxFontSize = 0;

for(XWPFRun run: p.getRuns())
{
    minFontSize = Math.min(run.getFontSize(), minFontSize);
        maxFontSize = Math.max(run.getFontSize(), maxFontSize);
}
item.minFontSize = minFontSize;
item.maxFontSize = maxFontSize;
	output.addItem(item);
    }

    public String toJson()
    {
	final Gson gson = new Gson();
	return gson.toJson(output);
    }

    static String cleanLine(String s)
    {
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < s.length();i++)
	{
	    final char c = s.charAt(i);
	    if (Character.isISOControl(c))
		b.append(' '); else
		b.append(c);
	}
	return new String(b);
    }

    /*
    static String getParagraphText(Paragraph p)
    {
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < p.numCharacterRuns();i++)
	{
	    final CharacterRun r = p.getCharacterRun(i);
	    b.append(r.text());
	}
	return new String(b);
    }
*/
}
