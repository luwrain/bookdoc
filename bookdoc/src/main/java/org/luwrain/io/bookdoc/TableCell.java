// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.*;

/**
 * A single cell of a {@link TableRow}.
 *
 * <p>A table cell is a generic container whose items may be any
 * {@link ContainerItem}, which lets a cell hold rich content such as
 * paragraphs, lists, headings or nested tables.</p>
 */
public final class TableCell extends Container<ContainerItem> implements ContainerItem
{
    /**
     * Returns the row index of this cell.
     *
     * @return the row index
     * @throws IllegalStateException always; this method is not implemented yet
     */
    public int getRowIndex()
    {
	throw new IllegalStateException("Not implemented");
    }

    /**
     * Returns the column index of this cell.
     *
     * @return the column index
     * @throws IllegalStateException always; this method is not implemented yet
     */
        public int getColIndex()
    {
	throw new IllegalStateException("Not implemented");
    }

}
