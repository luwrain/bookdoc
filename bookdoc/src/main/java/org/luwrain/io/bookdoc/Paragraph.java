// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.*;

import com.google.gson.annotations.*;
import org.luwrain.io.bookdoc.view.ParagraphView;

/**
 * A paragraph of a document.
 *
 * <p>A paragraph is a node that owns a sequence of {@link Run} objects. Unlike
 * containers, a paragraph does not hold child nodes; its content is represented
 * by runs. A paragraph also maintains a {@link ParagraphView} used during
 * rendering.</p>
 */
public final class Paragraph extends Node implements ContainerItem
{
    private List<Run> runs = null;
    private ParagraphView view = null;

    /**
     * Creates a paragraph with the supplied runs and attributes.
     *
     * @param runs the runs forming the paragraph text; must not be {@code null}
     * @param attributes the paragraph attributes; may be {@code null}
     */
    public Paragraph(List<Run> runs, Attributes attributes)
    {
	this.runs = new ArrayList<>();
	this.runs.addAll(runs);
	this.attributes = attributes;
    }

    /**
     * Creates an empty paragraph with no attributes.
     */
    public Paragraph()
    {
	this(Arrays.asList(new Run[0]), null);
    }

    /**
     * Returns the runs forming this paragraph text.
     *
     * @return the list of runs; never {@code null}
     */
    public List<Run> getRuns()
    {
	if (this.runs == null)
	    this.runs = new ArrayList<>();
	return this.runs;
    }

    /**
     * Returns the view associated with this paragraph, creating it if
     * necessary.
     *
     * @return the paragraph view; never {@code null}
     */
    public ParagraphView getView()
    {
	if (view == null)
	    view = new ParagraphView();
	return view;
    }

    /**
     * Returns the concatenated text of all runs in this paragraph.
     *
     * @return the paragraph text; never {@code null}
     */
    public String getText()
    {
	if (runs == null)
	    return "";
	final StringBuilder b = new StringBuilder();
	for(Run r: runs)
	    b.append(r.getText());
	return new String(b);
    }

    @Override public String toString()
    {
	return getText();
    }
}
