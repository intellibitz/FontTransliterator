package sted.widgets

import java.awt.Dimension
import java.awt.Insets
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.Icon
import javax.swing.JButton

class GCButton : JButton(), ActionListener {
    override fun actionPerformed(e: ActionEvent) {
        val runtime = Runtime.getRuntime()
        runtime.runFinalization()
        runtime.gc()
    }

    fun load(icon: Icon?, rollOverIcon: Icon?) {
        isRolloverEnabled = true
        margin = Insets(0, 0, 0, 0)
        toolTipText = "Runs Garbage Collector"
        // add the button as the self action listener
        // the button knows what to do anyways
        addActionListener(this)
        if (icon != null) {
            setIcon(icon)
            size = Dimension(icon.iconHeight, icon.iconWidth)
        }
        rolloverIcon = rollOverIcon
    }
}