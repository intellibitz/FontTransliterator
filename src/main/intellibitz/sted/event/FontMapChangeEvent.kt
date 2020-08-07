package sted.event

import sted.fontmap.FontMap
import javax.swing.event.ChangeEvent

class FontMapChangeEvent(fontMap: FontMap?) : ChangeEvent(fontMap) {
    val fontMap: FontMap
        get() = getSource() as FontMap
}