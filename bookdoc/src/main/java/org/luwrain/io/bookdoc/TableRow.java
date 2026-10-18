// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.*;

/**
 * A single row of a {@link Table}.
 *
 * <p>A table row is a container whose direct children are {@link TableCell}
 * elements. The number of items in the row describes the number of columns.</p>
 */
public final class TableRow extends Container<TableCell> implements ContainerItem
{
}
