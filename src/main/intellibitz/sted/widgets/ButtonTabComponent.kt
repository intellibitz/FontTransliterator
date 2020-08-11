package sted.widgets

import java.awt.FlowLayout
import java.awt.event.*
import javax.swing.*

/**
 * Component to be used as tabComponent; Contains a JLabel to show the text and
 * a JButton to close the tab it belongs to
 */
class ButtonTabComponent : JPanel(), ActionListener {
    val tabTitle = JLabel()
    private var actionListener: ActionListener? = null
    fun load(title: String?, pane: JTabbedPane?) {
        //unset default FlowLayout' gaps
        layout = FlowLayout(FlowLayout.LEFT, 0, 0)
        if (pane == null) {
            throw NullPointerException("TabbedPane is null")
        }
        isOpaque = false

        //make JLabel read titles from JTabbedPane
//        tabTitle = new JLabel(title);
        tabTitle.text = title
        add(tabTitle)
        //add more space between the tabTitle and the button
        tabTitle.border = BorderFactory.createEmptyBorder(0, 0, 0, 5)
        //tab button
        val tabButton = TabButton()
        tabButton.load(buttonMouseListener)
        //Close the proper tab by clicking the button
        tabButton.addActionListener(this)
        add(tabButton)
        //add more space to the top of the component
        border = BorderFactory.createEmptyBorder(2, 0, 0, 0)
    }

    override fun actionPerformed(e: ActionEvent) {
        //TODO: HACKETY HACK! DOUBLE CHECK THIS IS SAFE..
        e.source = this
        actionListener!!.actionPerformed(e)
    }

    fun addActionListener(actionListener: ActionListener?) {
        this.actionListener = actionListener
    }

    companion object {
        private val buttonMouseListener: MouseListener = object : MouseAdapter() {
            override fun mouseEntered(e: MouseEvent) {
                val component = e.component
                if (component is AbstractButton) {
                    component.isBorderPainted = true
                }
            }

            override fun mouseExited(e: MouseEvent) {
                val component = e.component
                if (component is AbstractButton) {
                    component.isBorderPainted = false
                }
            }
        }
    }
}