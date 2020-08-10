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
    private var eventListenerList = EventListenerList()
    var inputFile: File = File("")
    var outputFile: File = File("")
    val fontMap: FontMap = FontMap()

    fun clear() {
        eventListenerList = EventListenerList()
        inputFile = File("")
        outputFile = File("")
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
        // the fontmap related events.. the model when change will fire these
        val fontMapChangeEvent = FontMapChangeEvent(fontMap)
        while (i >= 0) {
            if (listeners[i] === FontMapChangeListener::class.java) {
                (listeners[i + 1] as FontMapChangeListener)
                    .stateChanged(fontMapChangeEvent)
            }
            i -= 2
        }
    }

    val isReadyForTransliteration: Boolean
        get() {
            return inputFile.isFile && outputFile.isFile
        }

    fun addFontMapChangeListener(fontMapChangeListener: FontMapChangeListener?) {
        eventListenerList.add(FontMapChangeListener::class.java, fontMapChangeListener)
    }

    fun removeFontMapChangeListener(fontMapChangeListener: FontMapChangeListener?) {
        eventListenerList.remove(FontMapChangeListener::class.java, fontMapChangeListener)
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