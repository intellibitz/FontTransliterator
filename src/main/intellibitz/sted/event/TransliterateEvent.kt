package sted.event

import sted.fontmap.Converter

class TransliterateEvent(converter: Converter?) : ThreadEvent(converter!!) {
    val converter: Converter
        get() = getSource() as Converter
}