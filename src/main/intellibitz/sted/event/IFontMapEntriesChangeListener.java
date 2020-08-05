package intellibitz.sted.event;

import java.util.EventListener;

public interface IFontMapEntriesChangeListener
        extends EventListener {
    void stateChanged(FontMapEntriesChangeEvent e);
}