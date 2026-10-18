// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

/**
 * An element of an ordered or unordered list.
 *
 * <p>A list item is a {@link Container} because it may contain arbitrary
 * nested document elements such as paragraphs, headings, tables or even other
 * lists. This class implements {@link ListEntry}, which marks it as an element
 * that is allowed to be placed directly inside {@link CommonList} descendants,
 * and {@link ContainerItem}, which lets it participate in the full document
 * tree.</p>
 */
public class ListItem extends Container implements ListEntry, ContainerItem
{
}
