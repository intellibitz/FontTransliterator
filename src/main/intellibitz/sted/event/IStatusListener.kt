package sted.event

import java.util.*

interface IStatusListener : EventListener {
    fun statusPosted(event: StatusEvent?)
}