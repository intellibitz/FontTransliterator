package sted.event

import java.util.*

open class ThreadEvent(val eventSource: IThreadEventSource) : EventObject(eventSource), Cloneable {
    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        return super.clone()
    }
}