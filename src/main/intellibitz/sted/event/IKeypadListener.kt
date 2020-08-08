package sted.event

import java.util.*

interface IKeypadListener : EventListener {
    fun keypadReset(event: KeypadEvent)
}