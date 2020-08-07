package sted.event

import java.util.*

interface IThreadListener : EventListener {
    fun threadRunStarted(threadEvent: ThreadEvent?)
    fun threadRunning(threadEvent: ThreadEvent?)
    fun threadRunFailed(threadEvent: ThreadEvent?)
    fun threadRunFinished(threadEvent: ThreadEvent?)
}