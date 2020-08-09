package sted.ui

import sted.fontmap.FontMap
import sted.fontmap.SampleTextConverter
import sted.io.Resources
import sted.ui.MenuHandler.Companion.menuHandler
import sted.widgets.FontChangeTextField
import javax.swing.JCheckBoxMenuItem
import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class DocumentListenerTextField : FontChangeTextField(), DocumentListener {
    private var converter: SampleTextConverter? = null
    private var fontMap: FontMap? = null
    private var mapperPanel: MapperPanel? = null

    /*
    public DocumentListenerTextField(MapperPanel mapperPanel,
            STEDWindow stedWindow)
    {
        this();
        this.mapperPanel = mapperPanel;
        this.stedWindow = stedWindow;
    }
*/
    fun load() {}
    override fun insertUpdate(e: DocumentEvent) {
        convertSampleText(e)
    }

    override fun removeUpdate(e: DocumentEvent) {
        convertSampleText(e)
    }

    override fun changedUpdate(e: DocumentEvent) {
        convertSampleText(e)
    }

    private fun convertSampleText(e: DocumentEvent) {
        if (e.document.length > 0) {
            if (converter == null) {
                converter = SampleTextConverter(mapperPanel!!)
            }
            converter!!.setFontMap(fontMap)
            val menuHandler = menuHandler
            val preserve = menuHandler.getMenuItem(Resources.ACTION_PRESERVE_TAGS) as JCheckBoxMenuItem?
            converter!!.setHTMLAware(preserve!!.isSelected)
            val reverse = menuHandler.getMenuItem(Resources.ACTION_TRANSLITERATE_REVERSE) as JCheckBoxMenuItem?
            converter!!.setReverseTransliterate(reverse!!.isSelected)
            SwingUtilities.invokeLater(converter)
        }
    }

    fun setFontMap(fontMap: FontMap?) {
        this.fontMap = fontMap
    }

    fun setFontMapperPanel(mapperPanel: MapperPanel?) {
        this.mapperPanel = mapperPanel
    }
}