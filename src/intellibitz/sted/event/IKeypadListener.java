package intellibitz.sted.event;

import java.util.EventListener;

public interface IKeypadListener
        extends EventListener {
    void keypadReset(KeypadEvent event);
}