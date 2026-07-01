// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.bookdoc.view;

import org.luwrain.io.bookdoc.*;
import static java.util.Objects.*;

class BoundingInfo
{
    interface Acceptor
    {
	void accept(Run run, int posFrom, int posTo);
    }

    final Run runFrom;
    final Run runTo;
    final int posFrom;
    final int posTo;

    BoundingInfo(Run runFrom, int posFrom, Run runTo, int posTo)
    {
	if (runFrom == null && runTo == null)
	    throw new IllegalArgumentException("runFrom and runTo may not be null simultainously");
	if (runFrom != null && posFrom < 0)
	    throw new IllegalArgumentException("posFrom may not be negative");
	if (runTo != null && posTo < 0)
	    throw new IllegalArgumentException("posTo may not be negative");

	if (runFrom == runTo && posTo < posFrom)
	    throw new IllegalArgumentException("posTo (" + posTo + ") may not be less than posFrom (" + posFrom + ") with the same runFrom and runTo");
	
	this.runFrom = runFrom;
	this.posFrom = posFrom;
	this.runTo = runTo;
	this.posTo = posTo;
	    }

    void filter(Run[] runs, Acceptor acceptor)
    {
	//	NullCheck.notNullItems(runs, "runs");
	requireNonNull(acceptor, "acceptor can't be null");
	boolean accepting = runFrom == null;
	for(Run r: runs)
	{
	    if (accepting)
	    {
		if (r == runTo)
		{
		    acceptor.accept(r, 0, Math.min(r.getText().length(), posTo));
		    return;
		}
		acceptor.accept(r, 0, r.getText().length());
		continue;
	    }
	    //not accepting
	    if (r == runFrom)
	    {
		if (r == runTo)
		{
		    //runFrom == runTo, nothing strange
		    acceptor.accept(r, posFrom, Math.min(r.getText().length(), posTo));
		    return;
		}
		acceptor.accept(r, posFrom, r.getText().length());
		accepting = true;
		continue;
	    } // encountering runFrom
	    if (r == runTo)//runTo met before we accepted anything, as you wish...
		return;
	}
    }
}
