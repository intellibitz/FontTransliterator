package sted.event

interface IStatusEventSource {
    fun fireStatusPosted()
    fun addStatusListener(statusListener: IStatusListener)
}