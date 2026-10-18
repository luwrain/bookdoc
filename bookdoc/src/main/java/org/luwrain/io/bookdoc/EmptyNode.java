// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.*;

/**
 * An empty node with neither content nor attributes.
 *
 * <p>This class is used primarily through {@link Node#EMPTY} as a lightweight
 * replacement for a {@code null} node reference.</p>
 */
public final class EmptyNode extends Node
{
}
