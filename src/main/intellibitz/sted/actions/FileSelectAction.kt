package sted.actions

import sted.STEDGUI
import sted.io.Resources
import java.awt.event.ActionEvent
import java.io.File
import javax.swing.JFileChooser

class FileSelectAction : TableModelListenerAction() {
    override fun actionPerformed(e: ActionEvent) {
        val cmd = e.actionCommand
        var isInput = true
        if (Resources.ACTION_SELECT_INPUT_FILE_COMMAND.equals(cmd, ignoreCase = true)) {
            isInput = true
        } else if (Resources.ACTION_SELECT_OUTPUT_FILE_COMMAND.equals(cmd, ignoreCase = true)) {
            isInput = false
        }
        var file: File?
        file = if (isInput) {
            stedWindow.desktop
                .desktopModel
                .inputFile
        } else {
            stedWindow.desktop
                .desktopModel
                .outputFile
        }
        val jFileChooser: JFileChooser
        val result: Int
        if (file.path.isNullOrEmpty()) {
            jFileChooser = JFileChooser(System.getProperty("user.dir"))
            result = jFileChooser.showOpenDialog(stedWindow)
        } else {
            jFileChooser = JFileChooser(file.parent)
            result = jFileChooser.showSaveDialog(stedWindow)
        }
        if (result == JFileChooser.APPROVE_OPTION) {
            file = jFileChooser.selectedFile
            if (file != null) {
                STEDGUI.busy()
                if (isInput) {
                    stedWindow.desktop
                        .fontMapperDesktopFrame.setInputFile(file)
                } else {
                    stedWindow.desktop
                        .fontMapperDesktopFrame.setOutputFile(file)
                }
                stedWindow.desktop
                    .fontMapperDesktopFrame
                    .enableConverterIfFilesLoaded()
                STEDGUI.relax()
            }
        }
    }
}