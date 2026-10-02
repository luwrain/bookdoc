// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import java.util.*;

import org.luwrain.io.bookdoc.*;

/**
 * Helper factory for building test documents. Provides convenient methods
 * for constructing paragraphs, runs, containers, and tables used across
 * the view package tests.
 */
final class TestDocFactory
{
    private TestDocFactory()
    {
    }

    /**
     * Creates a paragraph with a single run holding the given text.
     */
    static Paragraph paragraph(String text)
    {
        final Paragraph para = new Paragraph();
        para.getRuns().add(new TestRun(text, para));
        return para;
    }

    /**
     * Creates a paragraph from several text fragments, each becoming a run.
     */
    static Paragraph paragraph(String... texts)
    {
        final Paragraph para = new Paragraph();
        for (String t : texts)
            para.getRuns().add(new TestRun(t, para));
        return para;
    }

    /**
     * Creates a paragraph with a single run holding the given text and
     * the given attributes.
     */
    static Paragraph paragraph(String text, Attributes attrs)
    {
        final Paragraph para = new Paragraph();
        para.getRuns().add(new TestRun(text, para, attrs));
        return para;
    }

    /**
     * Creates a root containing the given items.
     */
    static Root root(ContainerItem... items)
    {
        return new Root(Arrays.asList(items));
    }

    /**
     * Creates a heading with the given level containing the given items.
     */
    static Heading heading(int level, ContainerItem... items)
    {
        final Heading h = new Heading(level);
        for (ContainerItem i : items)
            h.addItem(i);
        return h;
    }

    /**
     * Creates a table cell containing the given items.
     */
    static TableCell cell(ContainerItem... items)
    {
        final TableCell c = new TableCell();
        for (ContainerItem i : items)
            c.addItem(i);
        return c;
    }

    /**
     * Creates a table row containing the given cells.
     */
    static TableRow row(TableCell... cells)
    {
        final TableRow r = new TableRow();
        for (TableCell c : cells)
            r.addItem(c);
        return r;
    }

    /**
     * Creates a table containing the given rows.
     */
    static Table table(TableRow... rows)
    {
        final Table t = new Table();
        for (TableRow r : rows)
            t.addItem(r);
        return t;
    }

    /**
     * Creates attributes with the given id.
     */
    static Attributes attrsWithId(String id)
    {
        final Attributes a = new Attributes();
        a.attrMap.put(Attributes.ID, id);
        return a;
    }
}