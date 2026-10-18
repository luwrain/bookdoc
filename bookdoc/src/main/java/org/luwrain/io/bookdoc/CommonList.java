// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

/**
 * The common base class for ordered and unordered lists.
 *
 * <p>This class defines the list type shared by {@link OrderedList} and
 * {@link UnorderedList}. A list is a container whose direct children are
 * {@link ListItem} elements.</p>
 */
abstract class CommonList extends Container
{
    /** The supported list types. */
    public enum Type {ORDERED, UNORDERED};
}
