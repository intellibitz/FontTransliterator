package sted.event;

/**
 * Created by IntelliJ IDEA. User: sara Date: May 14, 2007 Time: 7:17:45 PM To
 * change this template use File | Settings | File Templates.
 */
public interface IKeypadEventSource {
    void fireKeypadReset();

    void addKeypadListener(IKeypadListener statusListener);
}