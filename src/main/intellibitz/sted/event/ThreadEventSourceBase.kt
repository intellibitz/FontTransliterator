package sted.event

import javax.swing.event.EventListenerList

open class ThreadEventSourceBase : Thread(), IThreadEventSource {
    private val eventListenerList = EventListenerList()
    lateinit var threadEvent: ThreadEvent
    override var message: Any = ""
    override var result: Any = ""
    override var progress = 0
    override var progressMaximum = 0

    private fun createThreadEvent() {
        threadEvent = ThreadEvent(this)
    }

    override fun fireThreadRunStarted() {
        // Guaranteed to return a non-null array
        val listeners = eventListenerList.listenerList
        createThreadEvent()
        // Process the listeners last to first, notifying
        // those that are interested in this event
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === IThreadListener::class.java) {
                (listeners[i + 1] as IThreadListener)
                    .threadRunStarted(threadEvent)
            }
            i -= 2
        }
    }

    override fun fireThreadRunning() {
        // Guaranteed to return a non-null array
        val listeners = eventListenerList.listenerList
        createThreadEvent()
        // Process the listeners last to first, notifying
        // those that are interested in this event
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === IThreadListener::class.java) {
                (listeners[i + 1] as IThreadListener).threadRunning(threadEvent)
            }
            i -= 2
        }
    }

    override fun fireThreadRunFailed() {
        // Guaranteed to return a non-null array
        val listeners = eventListenerList.listenerList
        createThreadEvent()
        // Process the listeners last to first, notifying
        // those that are interested in this event
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === IThreadListener::class.java) {
                (listeners[i + 1] as IThreadListener)
                    .threadRunFailed(threadEvent)
            }
            i -= 2
        }
    }

    override fun fireThreadRunFinished() {
        // Guaranteed to return a non-null array
        val listeners = eventListenerList.listenerList
        createThreadEvent()
        // Process the listeners last to first, notifying
        // those that are interested in this event
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === IThreadListener::class.java) {
                (listeners[i + 1] as IThreadListener)
                    .threadRunFinished(threadEvent)
            }
            i -= 2
        }
    }

    override fun addThreadListener(threadListener: IThreadListener) {
        eventListenerList.add(IThreadListener::class.java, threadListener)
    }
}