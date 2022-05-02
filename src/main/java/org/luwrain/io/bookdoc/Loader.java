/*
   Copyright 2016-2022 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.io.bookdoc;

import java.net.URI;
import java.io.IOException;

import org.luwrain.io.bookdoc.loaders.*;

public abstract class Loader
{
    public abstract Doc load() throws IOException;

    static public Loader newDefaultLoader(URI uri, String contentType)
    {
	return new DefaultLoader(uri, contentType);
    }
}
