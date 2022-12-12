
package org.luwrain.io.filters;

import java.util.*;

public final class Log
{
    public enum Level {DEBUG, INFO, WARNING, ERROR, FATAL};

    static public class Message
    {
	public final Level level;
	public final String component;
	public final String message;
	Message(Level level, String component, String message)
	{
	    NullCheck.notNull(level, "level");
	    NullCheck.notNull(component, "component");
	    NullCheck.notNull(message, "message");
	    this.level = level;
	    this.component = component;
	    this.message = message;
	}
    }

    public interface Listener
    {
	void onLogMessage(Message message);
    }

    static private final List<Listener> listeners = new ArrayList<Listener>();
    static private boolean briefMode = false;
    static private final Object syncObj = new Object();

    static public void addListener(Listener listener)
    {
		    NullCheck.notNull(listener, "listener");
	synchronized (syncObj) {
	    for(int i = 0;i < listeners.size();++i)
		if (listeners.get(i) == listener)
		    return;
	    listeners.add(listener);
	}
    }

    static public void removeListener(Listener listener)
    {
	NullCheck.notNull(listener, "listener");
	synchronized (syncObj) {
	    for(int i = 0;i < listeners.size();++i)
		if (listeners.get(i) == listener)
		{
		    listeners.remove(i);
		    return;
		}
	}
    }

    static void enableBriefMode()
    {
	briefMode = true;
    }

    static private void message(Level level, String component, String message)
    {
	if (level == null || component == null || message == null)
	    return;
	synchronized (syncObj)  {
	    final Message msg = new Message(level, component, message);
	    for(Listener l: listeners)
		l.onLogMessage(msg);
	    switch(level)
	    {
	    case DEBUG:
		if (!briefMode)
		{
		    if (component.equals("init") || component.equals("core"))
			System.out.println(cap(message)); else
			System.out.println(component .toUpperCase()+ ": " + cap(message));
		}
		break;
	    case INFO:
		if (component.equals("init") || component.equals("core"))
		    System.out.println(cap(message)); else
		    System.out.println(component .toUpperCase()+ ": " + cap(message));
		break;
	    default:
		System.out.println(level.toString() + ": " + component.toUpperCase() + ": " + message);
	    }
	}
    }

    public static void debug(String component, String message)
    {
	if (message != null)
	    message(Level.DEBUG, (component != null && !component.isEmpty())?component.trim():"-", message.trim());
    }

    static public void info(String component, String message)
    {
	if (message != null)
	    message(Level.INFO, (component != null && !component.isEmpty())?component.trim():"-", message.trim());
    }

    static public void warning(String component, String message)
    {
	if (message != null)
	    message(Level.WARNING, (component != null && !component.isEmpty())?component.trim():"-", message.trim());
    }

    static public void error(String component, String message)
    {
	if (message != null)
	    message(Level.ERROR, (component != null && !component.isEmpty())?component.trim():"-", message.trim());
    }

    static public void fatal(String component, String message)
    {
	if (message != null)
	    message(Level.FATAL, (component != null && !component.isEmpty())?component.trim():"-", message.trim());
    }

    static private String cap(String str)
    {
	if (str.isEmpty())
	    return str;
	final char ch = str.charAt(0);
	if (Character.isLetter(ch) && Character.toUpperCase(ch) != ch)
	    return Character.toUpperCase(ch) + str.substring(1);
	return str;
    }
}
