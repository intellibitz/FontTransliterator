package sted.event

import java.util.*

interface IMessageListener : EventListener {
    fun messagePosted(event: MessageEvent?)
}