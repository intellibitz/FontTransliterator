package sted.widgets

import java.awt.*
import java.awt.event.MouseListener
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.plaf.basic.BasicButtonUI

internal class TabButton : JButton() {
    fun load(mouseListener: MouseListener?) {
        val size = 17
        preferredSize = Dimension(size, size)
        toolTipText = "close this tab"
        //Make the button looks the same for all Laf's
        setUI(BasicButtonUI())
        //Make it transparent
        isContentAreaFilled = false
        //No need to be focusable
        isFocusable = false
        border = BorderFactory.createEtchedBorder()
        isBorderPainted = false
        //Making nice rollover effect
        //we use the same listener for all buttons
        addMouseListener(mouseListener)
        isRolloverEnabled = true
    }

    //we don't want to update UI for this button
    override fun updateUI() {}

    //paint the cross
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g.create() as Graphics2D
        //shift the image for pressed buttons
        if (getModel().isPressed) {
            g2.translate(1, 1)
        }
        g2.stroke = BasicStroke(2f)
        g2.color = Color.BLACK
        if (getModel().isRollover) {
            g2.color = Color.MAGENTA
        }
        val delta = 6
        g2.drawLine(
            delta, delta, width - delta - 1,
            height - delta - 1
        )
        g2.drawLine(
            width - delta - 1, delta, delta,
            height - delta - 1
        )
        g2.dispose()
    }
}