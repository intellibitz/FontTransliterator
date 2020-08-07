package sted.event

import java.util.*

class MessageEvent(val eventSource: IMessageEventSource) : EventObject(eventSource), Cloneable {
    var message: String? = null

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        return super.clone()
    }
}