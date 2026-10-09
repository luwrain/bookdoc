// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.*;

public class SetContainerVisitor extends Visitor
{
    @Override public void visitEveryNode(Node node)
    {
	if (node instanceof Container cont)
	    for(final var i: cont.getItems())
		if (i instanceof ContainerItem c)
		    c.setContainer(cont);
    }
}
