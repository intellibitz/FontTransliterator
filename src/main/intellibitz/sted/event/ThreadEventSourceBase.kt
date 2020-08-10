package sted.event;

import javax.swing.event.EventListenerList;

public class ThreadEventSourceBase
        extends Thread
        implements IThreadEventSource {
    private final EventListenerList eventListenerList = new EventListenerList();
    private ThreadEvent threadEvent = new ThreadEvent(this);
    private Object message;
    private Object result;
    private int progress;
    private int progressMaximum;

    //
    public ThreadEventSourceBase() {
        super();
    }

    protected void createThreadEvent() {
        threadEvent = new ThreadEvent(this);
    }

    protected ThreadEvent getThreadEvent() {
        return threadEvent;
    }

    protected void setThreadEvent(ThreadEvent threadEvent) {
        this.threadEvent = threadEvent;
    }

    public void fireThreadRunStarted() {
        // Guaranteed to return a non-null array
        final Object[] listeners = eventListenerList.getListenerList();
        createThreadEvent();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IThreadListener.class) {
                ((IThreadListener) listeners[i + 1])
                        .threadRunStarted(threadEvent);
            }
        }
    }

    public void fireThreadRunning() {
        // Guaranteed to return a non-null array
        final Object[] listeners = eventListenerList.getListenerList();
        createThreadEvent();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IThreadListener.class) {
                ((IThreadListener) listeners[i + 1]).threadRunning(threadEvent);
            }
        }
    }

    public void fireThreadRunFailed() {
        // Guaranteed to return a non-null array
        final Object[] listeners = eventListenerList.getListenerList();
        createThreadEvent();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IThreadListener.class) {
                ((IThreadListener) listeners[i + 1])
                        .threadRunFailed(threadEvent);
            }
        }
    }

    public void fireThreadRunFinished() {
        // Guaranteed to return a non-null array
        final Object[] listeners = eventListenerList.getListenerList();
        createThreadEvent();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IThreadListener.class) {
                ((IThreadListener) listeners[i + 1])
                        .threadRunFinished(threadEvent);
            }
        }
    }

    public void addThreadListener(IThreadListener threadListener) {
        eventListenerList.add(IThreadListener.class, threadListener);
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public int getProgressMaximum() {
        return progressMaximum;
    }

    public void setProgressMaximum(int progressMaximum) {
        this.progressMaximum = progressMaximum;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
