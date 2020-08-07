package sted.widgets

import java.awt.BorderLayout
import java.awt.Component
import java.awt.event.*
import javax.swing.AbstractAction
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JPanel

/**
 * Displays the About message.
 */
open class AboutDialog(title: String?, aboutDescriptor: Component?) : JDialog(), KeyListener, MouseListener {
    private val ok: JButton
    fun setOKText(okTitle: String?) {
        ok.text = okTitle
    }

    /**
     * Invoked when a key has been pressed. See the class description for [ ] for a definition of a key pressed event.
     */
    override fun keyPressed(e: KeyEvent) {
        isVisible = false
    }

    /**
     * Invoked when a key has been released. See the class description for
     * [KeyEvent] for a definition of a key released event.
     */
    override fun keyReleased(e: KeyEvent) {
        isVisible = false
    }

    /**
     * Invoked when a key has been typed. See the class description for [ ] for a definition of a key typed event.
     */
    override fun keyTyped(e: KeyEvent) {
        isVisible = false
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on
     * a component.
     */
    override fun mouseClicked(e: MouseEvent) {
        isVisible = false
    }

    /**
     * Invoked when the mouse enters a component.
     */
    override fun mouseEntered(e: MouseEvent) {
//        setVisible(false);
    }

    /**
     * Invoked when the mouse exits a component.
     */
    override fun mouseExited(e: MouseEvent) {
//        setVisible(false);
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    override fun mousePressed(e: MouseEvent) {
        isVisible = false
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    override fun mouseReleased(e: MouseEvent) {
        isVisible = false
    }

    init {
        setTitle(title)
        contentPane.add(aboutDescriptor)
        ok = JButton("ok")
        ok.addActionListener(object : AbstractAction() {
            override fun actionPerformed(evt: ActionEvent) {
                this@AboutDialog.isVisible = false
            }
        })
        ok.addKeyListener(this)
        ok.isFocusable = true
        val _pane = JPanel()
        _pane.add(ok)
        contentPane.add(BorderLayout.SOUTH, _pane)
        getRootPane().defaultButton = ok
        isResizable = false
        defaultCloseOperation = HIDE_ON_CLOSE
        pack()
        ok.requestFocus()
        isVisible = false
    }
}