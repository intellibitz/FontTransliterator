package sted.event

import java.util.*

/**
 * holds the source Thread information
 */
class KeypadEvent(val eventSource: IKeypadEventSource) : EventObject(eventSource), Cloneable {
    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        return super.clone()
    }
}