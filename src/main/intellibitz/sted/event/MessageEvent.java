package intellibitz.sted.event;

import java.util.EventObject;

/**
 * holds the source Thread information
 */
public class MessageEvent
        extends EventObject
        implements Cloneable {
    private IMessageEventSource messageEventSource;
    private String message;

    /**
     * @param src the source for this event
     */
    public MessageEvent(IMessageEventSource src) {
        super(src);
        messageEventSource = src;
    }

    /**
     * @return IStatusEventSource the Source which generated this Event
     */
    public IMessageEventSource getEventSource() {
        return messageEventSource;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object clone()
            throws CloneNotSupportedException {
        return super.clone();
    }
}