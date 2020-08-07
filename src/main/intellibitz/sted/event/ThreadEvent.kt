package sted.event;

import java.util.EventObject;

/**
 * holds the source Thread information
 */
public class ThreadEvent
        extends EventObject
        implements Cloneable {
    private IThreadEventSource threadEventSource;

    /**
     * @param runnable IThreadEventSource
     * @see IThreadEventSource
     */
    public ThreadEvent(IThreadEventSource runnable) {
        super(runnable);
        threadEventSource = runnable;
    }

    /**
     * @return IThreadEventSource the Source which generated this ThreadEvent
     * @see IThreadEventSource
     */
    public IThreadEventSource getEventSource() {
        return threadEventSource;
    }

    public Object clone()
            throws CloneNotSupportedException {
        return super.clone();
    }
}
