package sted.actions

import sted.fontmap.Converter
import sted.io.Resources
import sted.ui.MenuHandler.Companion.instance
import sted.ui.STEDWindow
import sted.ui.TabDesktop
import java.awt.event.ActionEvent
import javax.swing.JCheckBoxMenuItem
import javax.swing.event.TableModelEvent
import javax.swing.table.TableModel

class TransliterateAction : TableModelListenerAction() {
    /**
     * This fine grain notification tells listeners the exact range of cells,
     * rows, or columns that changed.
     */
    override fun tableChanged(e: TableModelEvent) {
        isEnabled = (e.source as TableModel).rowCount > 0 &&
                stedWindow.getDesktop()
                    .getFontMapperDesktopFrame()
                    .enableConverterIfFilesLoaded()
    }

    override fun actionPerformed(e: ActionEvent) {
        val fileToConvert = stedWindow.desktop
            .desktopModel.inputFile
        val convertedFile = stedWindow.desktop
            .desktopModel.outputFile
        if (fileToConvert == null || convertedFile == null) {
            fireMessagePosted("Select valid files for both input and output")
            return
        }
        if (fileToConvert.name == convertedFile.name) {
            fireMessagePosted(
                "Input and Output files are same.. select different files"
            )
            return
        }
        fireStatusPosted("Begin Converting...")
        val converter = getConverter(stedWindow.desktop)
        // the converter does not update the GUI.. so start an independent thread
        // the GUI will listen to the converter and update the status thru event handling -- THREAD SAFE!!!
        converter.start()
        isEnabled = false
    }

    fun getConverter(desktop: TabDesktop): Converter {
        val converter = Converter(
            desktop.fontMap,
            desktop.desktopModel.inputFile,
            desktop.desktopModel.outputFile
        )
        val preserve = instance!!.getMenuItem(
            Resources.ACTION_PRESERVE_TAGS
        ) as JCheckBoxMenuItem?
        converter.setHTMLAware(preserve!!.isSelected)
        val reverse = instance!!.getMenuItem(
            Resources.ACTION_TRANSLITERATE_REVERSE
        ) as JCheckBoxMenuItem?
        converter.setReverseTransliterate(reverse!!.isSelected)
        converter.addThreadListener(desktop)
        val stop = instance!!.getAction(
            Resources.ACTION_STOP_NAME
        ) as TransliterateStopAction?
        stop!!.setConverter(converter)
        stop.isEnabled = true
        return converter
    }
}