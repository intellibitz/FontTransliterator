package sted.event

import java.util.*

interface FontMapChangeListener : EventListener {
    fun stateChanged(e: FontMapChangeEvent)
}