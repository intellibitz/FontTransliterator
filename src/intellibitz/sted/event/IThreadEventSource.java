package intellibitz.sted.event;

public interface IThreadEventSource
        extends Runnable {
    void fireThreadRunStarted();

    void fireThreadRunning();

    void fireThreadRunFailed();

    void fireThreadRunFinished();

    /**
     * registers listeners with this event source
     *
     * @param threadListener IThreadListener interested in ThreadEvent
     * @see intellibitz.sted.event.IThreadListener
     */
    void addThreadListener(IThreadListener threadListener);

    /**
     * @return Object the status of this Thread
     */
    Object getMessage();

    /**
     * @return Object the result of this Thread operation
     */
    Object getResult();

    /**
     * @return int the progress of the Thread operation.. can be used as a
     * JProgressBar value
     */
    int getProgress();

    /**
     * @return int the progress maximum value of the Thread operation.. can be
     * used as a JProgressBar#maximum
     */
    int getProgressMaximum();
}
