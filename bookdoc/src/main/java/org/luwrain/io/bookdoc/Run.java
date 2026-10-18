// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

/**
 * A run of text inside a paragraph.
 *
 * <p>A run is the smallest textual unit of a document. Runs are owned by
 * paragraphs, not by nodes. Implementations include {@link TextRun}.</p>
 */
public interface Run
{
    /**
     * Returns the text of this run.
     *
     * @return the run text; never {@code null}
     */
    String getText();

    /**
     * Returns the paragraph to which this run belongs.
     *
     * @return the owning paragraph, or {@code null} if the run is not attached to a paragraph
     */
    Paragraph getParagraph();

    /**
     * Returns the URL associated with this run.
     *
     * @return the URL, or {@code null} if this run is not a link
     */
    String getHref();

    /**
     * Returns the attributes of this run.
     *
     * @return the run attributes, or {@code null} if none are set
     */
    Attributes getAttrs();
}
