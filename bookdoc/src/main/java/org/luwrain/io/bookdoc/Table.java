// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.*;

/**
 * A table inside a document.
 *
 * <p>A table is a container whose direct children are {@link TableRow}
 * elements. Each row contains {@link TableCell} elements and describes one
 * row of the table.</p>
 */
public final class Table extends Container<TableRow> implements ContainerItem
{
}
