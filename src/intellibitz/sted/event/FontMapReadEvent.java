package intellibitz.sted.event;

public class FontMapReadEvent
        extends ThreadEvent {
    public FontMapReadEvent(IThreadEventSource runnable) {
        super(runnable);
    }
}
