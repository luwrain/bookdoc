// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.*;
import static java.util.Objects.*;

public final class Root extends Container<ContainerItem>
{
    public Root(List<ContainerItem> items)
    {
	requireNonNull(items, "items can't be null");
	this.items = new ArrayList<>(items);
    }
}
