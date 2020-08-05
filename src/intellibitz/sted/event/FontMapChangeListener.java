package intellibitz.sted.event;

import java.util.EventListener;

public interface FontMapChangeListener
        extends EventListener {
    void stateChanged(FontMapChangeEvent e);
}
