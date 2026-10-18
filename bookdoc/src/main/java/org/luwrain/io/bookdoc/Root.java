// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.*;
import static java.util.Objects.*;

/**
 * The root container of a document.
 *
 * <p>The root stores all top-level document elements in their order of
 * appearance. It is referenced by {@link Doc} and forms the beginning of the
 * document tree.</p>
 */
public final class Root extends Container<ContainerItem>
{
    /**
     * Creates a root container with the supplied items.
     *
     * @param items the top-level document items; must not be {@code null}
     * @throws NullPointerException if {@code items} is {@code null}
     */
    public Root(List<ContainerItem> items)
    {
	requireNonNull(items, "items can't be null");
	this.items = new ArrayList<>(items);
    }
}
