package sted.fontmap

import sted.ui.MapperPanel

class SampleTextConverter(private val mapperPanel: MapperPanel) : Converter() {
    override fun run() {
        val input = mapperPanel.inputText.text
        mapperPanel.outputText.text = ""
        if (isReady && !input.isNullOrBlank()) {
            mapperPanel.outputText.text = transliterate?.parseLine(input)
        }
    }
}