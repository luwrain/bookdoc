// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import org.luwrain.io.bookdoc.*;

final class TestRun implements Run
{
    private final String text;
    private final Node parent;
    private final Attributes attrs;

    TestRun(String text, Node parent)
    {
        this(text, parent, null);
    }

    TestRun(String text, Node parent, Attributes attrs)
    {
        if (text == null)
            throw new NullPointerException("text can't be null");
        this.text = text;
        this.parent = parent;
        this.attrs = attrs;
    }

    @Override public String getText()
    {
        return text;
    }

    @Override public Node getParentNode()
    {
        return parent;
    }

    @Override public String getHref()
    {
        return null;
    }

    @Override public Attributes getAttrs()
    {
        return attrs;
    }

    @Override public String toString()
    {
        return text;
    }
}
