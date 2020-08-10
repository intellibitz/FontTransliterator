package sted.event

/**
 * Created by IntelliJ IDEA. User: sara Date: May 14, 2007 Time: 7:17:45 PM To
 * change this template use File | Settings | File Templates.
 */
interface IKeypadEventSource {
    fun fireKeypadReset()
    fun addKeypadListener(keypadListener: IKeypadListener)
}