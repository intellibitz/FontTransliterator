package sted.widgets

import java.awt.Dimension
import java.awt.Insets
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.Icon
import javax.swing.JButton

class GCButton(icon: Icon, rollOverIcon: Icon?) : JButton(), ActionListener {
    private val runtime: Runtime
    override fun actionPerformed(e: ActionEvent) {
        runtime.runFinalization()
        runtime.gc()
    }

    init {
        setIcon(icon)
        rolloverIcon = rollOverIcon
        isRolloverEnabled = true
        margin = Insets(0, 0, 0, 0)
        size = Dimension(icon.iconHeight, icon.iconWidth)
        toolTipText = "Runs Garbage Collector"
        runtime = Runtime.getRuntime()
        // add the button as the self action listener
        // the button knows what to do anyways
        addActionListener(this)
    }
}