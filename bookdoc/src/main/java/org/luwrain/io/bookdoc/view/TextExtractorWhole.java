// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import java.util.*;

import org.luwrain.io.bookdoc.*;

import static java.util.Objects.*;

/**
 * Extracts the full text content from a document node tree, splitting
 * paragraphs into lines according to the specified width. The result is
 * a sequence of text lines suitable for plain-text representation.
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * TextExtractorWhole extractor = new TextExtractorWhole(80);
 * extractor.onNode(rootNode);
 * String[] lines = extractor.getLines();
 * }</pre>
 *
 * @see TextExtractor
 * @see TextExtractorFragment
 * @see RowPartsSplitter
 */
public final class TextExtractorWhole extends TextExtractor
{
    static private final String LOG_COMPONENT = "document";

    private final int width;

    /**
     * Creates a new text extractor with the given maximum line width.
     *
     * @param width The maximum number of characters per line; must not be negative
     * @throws IllegalArgumentException if width is negative
     */
    public TextExtractorWhole(int width)
    {
        if (width < 0)
            throw new IllegalArgumentException("width (" + width + ") may not be negative");
        this.width = width;
    }

    /**
     * Processes the given node and its entire subtree, extracting text lines
     * for all paragraphs encountered. An empty line is inserted after each
     * paragraph to separate them in the output.
     *
     * @param node The node to process; must not be null
     * @throws NullPointerException if node is null
     */
    public void onNode(Node node)
    {
        requireNonNull(node, "node can't be null");
        if (node instanceof Paragraph)
        {
            onParagraph((Paragraph) node);
            addEmptyLine();
            return;
        }
        if (node instanceof Container)
        {
            final Container<?> cont = (Container<?>) node;
            for (ContainerItem i : cont.getItems())
                if (i instanceof Node)
                    onNode((Node) i);
        }
    }

    private void onParagraph(Paragraph para)
    {
        requireNonNull(para, "para can't be null");
        final RowPartsSplitter splitter = new RowPartsSplitter();
        for (Run r : para.getRuns())
        {
            final String text = r.getText();
            requireNonNull(text, "text can't be null");
            splitter.onRun(r, text, 0, text.length(), width);
        }
        if (splitter.res.isEmpty())
            return;
        onParagraphLines(splitter.res.toArray(new RowPart[splitter.res.size()]));
    }
}
