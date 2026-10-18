// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

/**
 * The default plain-text implementation of the {@link Run} interface.
 *
 * <p>A text run holds a text string, an optional owning paragraph, an optional
 * link URL and optional attributes. Instances are mutable with respect to the
 * owning paragraph, which is normally assigned when the run is attached to a
 * paragraph.</p>
 */
public class TextRun implements Run
{
    private String text = null;
    private Paragraph paragraph = null;
    private String href = null;
    private Attributes attr = null;

    /**
     * Creates a text run with no owning paragraph, link or attributes.
     *
     * @param text the run text; must not be {@code null}
     * @throws NullPointerException if {@code text} is {@code null}
     */
    public TextRun(String text)
    {
	this(text, null, null, null);
    }

    /**
     * Creates a text run attached to the specified paragraph.
     *
     * @param text the run text; must not be {@code null}
     * @param paragraph the owning paragraph; may be {@code null}
     * @throws NullPointerException if {@code text} is {@code null}
     */
    public TextRun(String text, Paragraph paragraph)
    {
	this(text, paragraph, null, null);
    }

    /**
     * Creates a text run with a link URL and attributes.
     *
     * @param text the run text; must not be {@code null}
     * @param href the link URL; may be {@code null}
     * @param attr the run attributes; may be {@code null}
     * @throws NullPointerException if {@code text} is {@code null}
     */
    public TextRun(String text, String href, Attributes attr)
    {
	this(text, null, href, attr);
    }

    /**
     * Creates a text run with an owning paragraph, link URL and attributes.
     *
     * @param text the run text; must not be {@code null}
     * @param paragraph the owning paragraph; may be {@code null}
     * @param href the link URL; may be {@code null}
     * @param attr the run attributes; may be {@code null}
     * @throws NullPointerException if {@code text} is {@code null}
     */
    public TextRun(String text, Paragraph paragraph, String href, Attributes attr)
    {
	if (text == null)
	    throw new NullPointerException("text can't be null");
	this.text = text;
	this.paragraph = paragraph;
	this.href = href;
	this.attr = attr;
    }

    @Override public String getText() {
	return text;
    }

    @Override public String getHref() {
	return href;
    }

    @Override public Attributes getAttrs() {
	return attr;
    }

    /**
     * Sets the paragraph to which this run belongs.
     *
     * @param paragraph the owning paragraph; may be {@code null}
     */
    public void setParagraph(Paragraph paragraph)
    {
	this.paragraph = paragraph;
    }

    @Override public Paragraph getParagraph()
    {
	return paragraph;
    }

    @Override public String toString()
    {
	return text != null?text:"";
    }
}
