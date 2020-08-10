package sted.event

import java.util.*

interface IFontMapEntriesChangeListener : EventListener {
    fun stateChanged(fontMapEntriesChangeEvent: FontMapEntriesChangeEvent)
}