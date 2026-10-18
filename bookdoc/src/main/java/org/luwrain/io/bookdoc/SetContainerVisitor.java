// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc;

import java.util.*;
import org.apache.logging.log4j.*;

/**
 * A visitor that links document tree elements to their parents.
 *
 * <p>After a document is built, this visitor can be used to populate the
 * back-references of the tree: every {@link ContainerItem} is assigned its
 * owning container and every {@link TextRun} is assigned its owning paragraph.</p>
 */
public class SetContainerVisitor extends Visitor
{
    static private final Logger log = LogManager.getLogger();

    @Override public void visitEveryNode(Node node)
    {
	if (node instanceof Container cont)
	    for(final var i: cont.getItems())
		if (i instanceof ContainerItem c)
		    c.setContainer(cont); else
		    log.warn("Unlink container item of the class {}", i.getClass().getName());
    }

    @Override public void visit(Paragraph paragraph)
    {
	for(final var r: paragraph.getRuns())
	    if (r instanceof TextRun t)
		t.setParagraph(paragraph); else
		log.warn("Unlinked run of the class {}", r.getClass().getName());
    }
}
