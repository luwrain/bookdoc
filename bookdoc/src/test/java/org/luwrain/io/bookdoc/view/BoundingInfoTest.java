// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import java.util.*;

import org.junit.jupiter.api.*;

import org.luwrain.io.bookdoc.*;

import static org.junit.jupiter.api.Assertions.*;

public class BoundingInfoTest
{
    @Test public void singleRunRange()
    {
        final Paragraph para = TestDocFactory.paragraph("hello world");
        final Run run = para.getRuns().get(0);
        final BoundingInfo info = new BoundingInfo(run, 6, run, 11);

        final List<String> fragments = new ArrayList<>();
        info.filter(new Run[] {run}, (r, from, to) -> fragments.add(r.getText().substring(from, to)));
        assertEquals(1, fragments.size());
        assertEquals("world", fragments.get(0));
    }

    @Test public void multiRunRange()
    {
        final Paragraph para = TestDocFactory.paragraph("hello ", "world");
        final Run run1 = para.getRuns().get(0);
        final Run run2 = para.getRuns().get(1);
        final BoundingInfo info = new BoundingInfo(run1, 3, run2, 5);

        final List<String> fragments = new ArrayList<>();
        info.filter(new Run[] {run1, run2}, (r, from, to) -> fragments.add(r.getText().substring(from, to)));
        assertEquals(2, fragments.size());
        assertEquals("lo ", fragments.get(0));
        assertEquals("world", fragments.get(1));
    }

    @Test public void openStart()
    {
        final Paragraph para = TestDocFactory.paragraph("hello ", "world");
        final Run run1 = para.getRuns().get(0);
        final Run run2 = para.getRuns().get(1);
        final BoundingInfo info = new BoundingInfo(null, -1, run2, 5);

        final List<String> fragments = new ArrayList<>();
        info.filter(new Run[] {run1, run2}, (r, from, to) -> fragments.add(r.getText().substring(from, to)));
        assertEquals(2, fragments.size());
        assertEquals("hello ", fragments.get(0));
        assertEquals("world", fragments.get(1));
    }

    @Test public void openEnd()
    {
        final Paragraph para = TestDocFactory.paragraph("hello ", "world");
        final Run run1 = para.getRuns().get(0);
        final Run run2 = para.getRuns().get(1);
        final BoundingInfo info = new BoundingInfo(run1, 3, null, -1);

        final List<String> fragments = new ArrayList<>();
        info.filter(new Run[] {run1, run2}, (r, from, to) -> fragments.add(r.getText().substring(from, to)));
        assertEquals(2, fragments.size());
        assertEquals("lo ", fragments.get(0));
        assertEquals("world", fragments.get(1));
    }

    @Test public void invalidConstructor()
    {
        final Paragraph para = TestDocFactory.paragraph("hello");
        final Run run = para.getRuns().get(0);
        assertThrows(IllegalArgumentException.class, () -> new BoundingInfo(null, -1, null, -1));
        assertThrows(IllegalArgumentException.class, () -> new BoundingInfo(run, -1, run, 5));
        assertThrows(IllegalArgumentException.class, () -> new BoundingInfo(run, 0, run, -1));
        assertThrows(IllegalArgumentException.class, () -> new BoundingInfo(run, 5, run, 3));
    }
}
