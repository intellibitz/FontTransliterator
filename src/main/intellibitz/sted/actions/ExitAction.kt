package sted.actions

import sted.io.SettingsXMLWriter
import java.awt.event.ActionEvent
import java.awt.event.WindowEvent
import javax.swing.JOptionPane
import javax.xml.transform.TransformerException
import kotlin.system.exitProcess

class ExitAction : STEDWindowAction() {
    private fun exit() {
        if (JOptionPane.CANCEL_OPTION !=
            stedWindow.desktop.saveDirty()
        ) {
            try {
                SettingsXMLWriter.writeUserSettings(stedWindow)
            } catch (ex: TransformerException) {
                logger.severe(
                    "Unable to write User Settings - TransformerException " +
                            ex.message
                )
                logger.throwing("ExitAction", "exit", ex)
            }
            Runtime.getRuntime().gc()
            System.runFinalization()
            exitProcess(0)
        }
    }

    override fun actionPerformed(e: ActionEvent) {
        exit()
    }

    override fun windowClosing(e: WindowEvent) {
        exit()
    }
}