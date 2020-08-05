package intellibitz.sted.event;

import java.util.EventListener;

public interface IStatusListener
        extends EventListener {
    void statusPosted(StatusEvent event);
}