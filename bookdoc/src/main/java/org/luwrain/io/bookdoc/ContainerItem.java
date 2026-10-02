// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

public interface ContainerItem
{
    Container getContainer();
    void setContainer(Container container);
    Geom getGeom();
    Attributes getAttributes();
}
