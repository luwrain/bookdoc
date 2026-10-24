// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

/**
 * An element that can be stored in a {@link Container}.
 *
 * <p>Container items carry a back-reference to the container to which they
 * belong, as well as geometry and attributes.</p>
 */
public interface ContainerItem
{
    /**
     * Returns the container to which this item belongs.
     *
     * @return the owning container, or {@code null} if this item is not attached to a container
     */
    Container getContainer();

    /**
     * Sets the container to which this item belongs.
     *
     * @param container the owning container; may be {@code null}
     */
    void setContainer(Container container);

    /**
     * Returns the geometry of this item.
     *
     * @return the geometry, or {@code null} if not set
     */
    Geom getGeom();

    /**
     * Returns the attributes of this item.
     *
     * @return the attributes, or {@code null} if not set
     */
    Attributes getAttributes();

    //TODO: Javadoc
    void setIndexInContainer(int index);

    //TODO: Javadoc
    int getIndexInContainer();
}
