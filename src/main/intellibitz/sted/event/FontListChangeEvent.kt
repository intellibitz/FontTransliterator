package sted.event

import sted.fontmap.FontMap
import java.awt.Font
import javax.swing.event.ChangeEvent

class FontListChangeEvent(fontMap: FontMap?) : ChangeEvent(fontMap) {
    var fontChanged: Font? = null
    var fontIndex = 0
}