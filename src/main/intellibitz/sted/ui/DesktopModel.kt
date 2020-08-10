package sted.ui

import sted.event.FontMapChangeEvent
import sted.event.FontMapChangeListener
import sted.fontmap.FontMap
import sted.io.FontMapXMLWriter
import java.io.File
import javax.swing.event.EventListenerList
import javax.xml.transform.TransformerException

/**
 * Contains FontMap and all its related Entities.
 */
class DesktopModel {
    private val eventListenerList = EventListenerList()
    var inputFile: File? = null
    var outputFile: File? = null
    lateinit var fontMap: FontMap

    // the fontmap related events.. the model when change will fire these
    private var fontMapChangeEvent: FontMapChangeEvent? = null
    fun clear() {
        fontMapChangeEvent = null
    }

    // Notify all listeners that have registered interest for
    // notification on this event type.  The event instance
    // is lazily created using the parameters passed into
    // the fire method.
    fun fireFontMapChangedEvent() {
        // Guaranteed to return a non-null array
        val listeners = eventListenerList.listenerList
        // Process the listeners last to first, notifying
        // those that are interested in this event
        var i = listeners.size - 2
        while (i >= 0) {
            if (listeners[i] === FontMapChangeListener::class.java) {
                // Lazily create the event:
                if (fontMapChangeEvent == null) {
                    fontMapChangeEvent = FontMapChangeEvent(fontMap)
                }
                (listeners[i + 1] as FontMapChangeListener)
                    .stateChanged(fontMapChangeEvent!!)
            }
            i -= 2
        }
    }

    val isReadyForTransliteration: Boolean
        get() {
            var flag = false
            if (inputFile != null && outputFile != null) {
                flag = true
            }
            return flag
        }

    fun addFontMapChangeListener(fontMapChangeListener: FontMapChangeListener?) {
        eventListenerList
            .add(FontMapChangeListener::class.java, fontMapChangeListener)
    }

    fun removeFontMapChangeListener(fontMapChangeListener: FontMapChangeListener?) {
        eventListenerList
            .remove(FontMapChangeListener::class.java, fontMapChangeListener)
    }

    @Throws(TransformerException::class)
    fun saveFontMap(): FontMap {
        FontMapXMLWriter.write(fontMap)
        fontMap.entries.clearUndoRedo()
        fontMap.isDirty = false
        fireFontMapChangedEvent()
        return fontMap
    }
}