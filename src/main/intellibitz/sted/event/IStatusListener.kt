package sted.event

import java.util.*

interface IStatusListener : EventListener {
    fun statusPosted(statusEvent: StatusEvent)
}