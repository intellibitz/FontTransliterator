package sted.event

import java.util.*

class StatusEvent(val eventSource: IStatusEventSource) : EventObject(eventSource), Cloneable {
    var status: String? = null

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        return super.clone()
    }
}