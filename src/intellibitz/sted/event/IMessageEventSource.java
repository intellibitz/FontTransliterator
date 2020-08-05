package intellibitz.sted.event;

/**
 * Created by IntelliJ IDEA. User: sara Date: May 14, 2007 Time: 7:17:45 PM To
 * change this template use File | Settings | File Templates.
 */
public interface IMessageEventSource {
    void fireMessagePosted();

    void addMessageListener(IMessageListener messageListener);
}