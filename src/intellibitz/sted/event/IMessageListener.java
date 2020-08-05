package intellibitz.sted.event;

import java.util.EventListener;

public interface IMessageListener
        extends EventListener {
    void messagePosted(MessageEvent event);
}