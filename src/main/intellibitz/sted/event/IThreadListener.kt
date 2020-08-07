package sted.event;

import java.util.EventListener;

public interface IThreadListener
        extends EventListener {
    /**
     * invoked when a Thread begins its run
     *
     * @param threadEvent ThreadEvent
     * @see ThreadEvent
     */
    void threadRunStarted(ThreadEvent threadEvent);

    /**
     * invoked when a Thread is running.. may be used to track the progress of
     * the Thread
     *
     * @param threadEvent ThreadEvent
     * @see ThreadEvent
     */
    void threadRunning(ThreadEvent threadEvent);

    /**
     * invoked when a Thread run fails
     *
     * @param threadEvent ThreadEvent
     * @see ThreadEvent
     */
    void threadRunFailed(ThreadEvent threadEvent);

    /**
     * invoked after a Thread completes its run
     *
     * @param threadEvent ThreadEvent
     * @see ThreadEvent
     */
    void threadRunFinished(ThreadEvent threadEvent);
}
