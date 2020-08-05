package intellibitz.sted.event;

import java.util.EventObject;

/**
 * holds the source Thread information
 */
public class KeypadEvent
        extends EventObject
        implements Cloneable {
    private IKeypadEventSource keypadEventSource;

    /**
     * @param src the source for this event
     */
    public KeypadEvent(IKeypadEventSource src) {
        super(src);
        keypadEventSource = src;
    }

    /**
     * @return IKeypadEventSource the Source which generated this Event
     */
    public IKeypadEventSource getEventSource() {
        return keypadEventSource;
    }

    public Object clone()
            throws CloneNotSupportedException {
        return super.clone();
    }
}