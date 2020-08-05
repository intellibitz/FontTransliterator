package intellibitz.sted.event;

public interface IStatusEventSource {
    void fireStatusPosted();

    void addStatusListener(IStatusListener statusListener);
}
