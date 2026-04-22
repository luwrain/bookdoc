// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.filters.html;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.*;

import org.luwrain.io.bookdoc.*;
import org.luwrain.io.bookdoc.filters.*;

public class HtmlFilterTest
{
    HtmlFilter f = null;

    @Test public void main() throws Exception
    {
	final var p = new Properties();
	p.put(Filter.PROP_URL, "http://localhost");
	final Doc d;
	try (final var is = getClass().getResourceAsStream("simple.html")) {
	    assertNotNull(is);
	    d = f.load(is, p);
	}
    }

    @BeforeEach void createFilter()
    {
	f = new HtmlFilter();
    }
}

