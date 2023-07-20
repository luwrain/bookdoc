/*
   Copyright 2016-2023 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.main.bookdoc;

import org.luwrain.io.bookdoc.*;

final class OutputVisitor extends Visitor
{
    static private final String HEADING_PREFIX = "heading ";


    @Override public void visit(Paragraph p)
    {
	final String style;
	if (p.getAttributes().attrMap.containsKey(Attributes.STYLE))
	    style = p.getAttributes().attrMap.get(Attributes.STYLE).toString(); else
	    style = null;
	if (style != null && style.startsWith(HEADING_PREFIX))
	    System.out.print("%h" + style.substring(HEADING_PREFIX.length()) + " ");
	System.out.println(p.getText());
	System.out.println();
    }
}


