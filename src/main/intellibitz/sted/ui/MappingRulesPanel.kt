package sted.ui

import sted.event.FontMapChangeEvent
import sted.event.FontMapChangeListener
import sted.fontmap.FontMap
import sted.fontmap.FontMapEntry
import sted.io.Resources
import sted.io.Resources.getResource
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*
import javax.swing.border.TitledBorder
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import javax.swing.event.TableModelEvent
import javax.swing.event.TableModelListener
import javax.swing.table.TableModel

class MappingRulesPanel : JPanel(), TableModelListener, FontMapChangeListener, ListSelectionListener {
    private val word2 = JTextField()
    private val word1 = JTextField()
    private val beginsWithCheck = JCheckBox()
    private val endsWithCheck = JCheckBox()
    private val ruleTitle = JLabel()
    private val followedByTitle = JLabel()
    private val precededByTitle = JLabel()
    private val followedText = JTextField()
    private val precededText = JTextField()
    private lateinit var fontMap: FontMap
    private var tableModel: TableModel? = null
    fun init() {
        val titledBorder = BorderFactory.createTitledBorder(
            getResource(Resources.TITLE_MAPPING_RULE)
        )
        titledBorder.titleJustification = TitledBorder.CENTER
        border = titledBorder
        val gridBagLayout = GridBagLayout()
        layout = gridBagLayout
        val gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.weighty = 0.0
        gridBagConstraints.weightx = 1.0
        gridBagConstraints.gridheight = 1
        gridBagConstraints.gridwidth = 1
        //
        word1.isEditable = false
        word1.isEnabled = false
        word1.horizontalAlignment = JLabel.RIGHT
        gridBagLayout.setConstraints(word1, gridBagConstraints)
        add(word1)
        gridBagConstraints.gridx = 1
        gridBagConstraints.gridwidth = 1
        gridBagConstraints.weightx = 0.0
        val eqLabel = JLabel(" = ")
        gridBagLayout.setConstraints(eqLabel, gridBagConstraints)
        add(eqLabel)
        gridBagConstraints.gridx = 2
        gridBagConstraints.gridwidth = 1
        gridBagConstraints.weightx = 1.0
        word2.isEditable = false
        word2.isEnabled = false
        gridBagLayout.setConstraints(word2, gridBagConstraints)
        add(word2)
        gridBagConstraints.gridy = 1
        gridBagConstraints.gridx = 0
        gridBagConstraints.gridwidth = 1
        ruleTitle.text = " If <> is: "
        gridBagLayout.setConstraints(ruleTitle, gridBagConstraints)
        add(ruleTitle)

        //
        //
        gridBagConstraints.gridy = 2
        gridBagConstraints.gridx = 0
        //        gridBagConstraints.gridwidth = 2;
        beginsWithCheck.text = getResource(
            Resources.TITLE_TABLE_COLUMN_FIRST_LETTER
        )
        beginsWithCheck.isEnabled = false
        gridBagLayout.setConstraints(beginsWithCheck, gridBagConstraints)
        add(beginsWithCheck)
        gridBagConstraints.gridx = 2
        //        gridBagConstraints.gridwidth = 2;
        endsWithCheck.text = getResource(
            Resources.TITLE_TABLE_COLUMN_LAST_LETTER
        )
        endsWithCheck.isEnabled = false
        gridBagLayout.setConstraints(endsWithCheck, gridBagConstraints)
        add(endsWithCheck)
        //
        //
        gridBagConstraints.gridy = 3
        gridBagConstraints.gridx = 0
        //        gridBagConstraints.gridwidth = 2;
        followedByTitle.text = "Followed By: "
        followedByTitle.isEnabled = false
        gridBagLayout.setConstraints(followedByTitle, gridBagConstraints)
        add(followedByTitle)
        gridBagConstraints.gridx = 2
        //        gridBagConstraints.gridwidth = 2;
        followedText.isEditable = false
        followedText.isEnabled = false
        gridBagLayout.setConstraints(followedText, gridBagConstraints)
        add(followedText)
        gridBagConstraints.gridy = 4
        gridBagConstraints.gridx = 0
        //        gridBagConstraints.gridwidth = 2;
        precededByTitle.text = "Preceded By: "
        precededByTitle.isEnabled = false
        gridBagLayout.setConstraints(precededByTitle, gridBagConstraints)
        add(precededByTitle)
        gridBagConstraints.gridx = 2
        //        gridBagConstraints.gridwidth = 2;
        precededText.isEditable = false
        precededText.isEnabled = false
        //
        gridBagLayout.setConstraints(precededText, gridBagConstraints)
        add(precededText)
        //
        isVisible = true
    }

    fun load() {}

    private fun reset() {
        word1.font = fontMap.font1
        word2.font = fontMap.font2
        followedText.font = fontMap.font2
        precededText.font = fontMap.font2
        ruleTitle.font = fontMap.font2
    }

    private fun clear() {
        word1.text = Resources.EMPTY_STRING
        word2.text = Resources.EMPTY_STRING
        followedText.text = Resources.EMPTY_STRING
        precededText.text = Resources.EMPTY_STRING
        beginsWithCheck.isSelected = false
        endsWithCheck.isSelected = false
        ruleTitle.text = Resources.RULE_TITLE
    }

    private fun load(entry: FontMapEntry?) {
        clear()
        if (entry != null) {
            word1.text = entry.from
            word2.text = entry.to
            if (entry.isRulesSet) {
                ruleTitle.text = "If <" + word1.text + "> is: "
                beginsWithCheck.isSelected = entry.isBeginsWith
                endsWithCheck.isSelected = entry.isEndsWith
                var s = entry.followedBy
                if (s != null) {
                    followedText.text = s
                }
                s = entry.precededBy
                if (s != null) {
                    precededText.text = s
                }
            }
            updateUI()
        }
    }

    override fun stateChanged(fontMapChangeEvent: FontMapChangeEvent) {
        this.fontMap = fontMapChangeEvent.fontMap
        clear()
        reset()
    }

    override fun tableChanged(e: TableModelEvent) {
        val tableModel = e.source as TableModel
        isEnabled = if (tableModel.getValueAt(0, 0) == null) {
            false
        } else {
            load((tableModel as MappingTableModel).getValueAt(e.firstRow))
            true
        }
        this.tableModel = tableModel
    }

    override fun valueChanged(e: ListSelectionEvent) {
        if (tableModel != null) {
            val listSelectionModel = e.source as ListSelectionModel
            val row = listSelectionModel.minSelectionIndex
            if (row > -1) {
                load((tableModel as MappingTableModel).getValueAt(row))
            }
        }
    }
}