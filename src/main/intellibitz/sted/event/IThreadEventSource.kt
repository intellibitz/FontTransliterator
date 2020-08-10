package sted.event

interface IThreadEventSource : Runnable {
    fun fireThreadRunStarted()
    fun fireThreadRunning()
    fun fireThreadRunFailed()
    fun fireThreadRunFinished()

    /**
     * registers listeners with this event source
     *
     * @param threadListener IThreadListener interested in ThreadEvent
     * @see sted.event.IThreadListener
     */
    fun addThreadListener(threadListener: IThreadListener)

    /**
     * @return Object the status of this Thread
     */
    var message: Any

    /**
     * @return Object the result of this Thread operation
     */
    var result: Any

    /**
     * @return int the progress of the Thread operation.. can be used as a
     * JProgressBar value
     */
    var progress: Int

    /**
     * @return int the progress maximum value of the Thread operation.. can be
     * used as a JProgressBar#maximum
     */
    var progressMaximum: Int
}