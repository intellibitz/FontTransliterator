package sted.ui

import sted.fontmap.FontMap
import java.awt.Component
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer

internal class MappingTableRenderer : DefaultTableCellRenderer() {
    lateinit var fontMap: FontMap

    override fun getTableCellRendererComponent(
        table: JTable,
        value: Any,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        val renderer = super.getTableCellRendererComponent(
            table, value, isSelected,
            hasFocus, row, column
        ) as DefaultTableCellRenderer
        if (column == 0) {
            renderer.font = fontMap.font1
            renderer.horizontalAlignment = RIGHT
        } else if (column == 1) {
            renderer.horizontalAlignment = CENTER
            renderer.isFocusable = false
        } else if (column == 2) {
            renderer.font = fontMap.font2
            renderer.horizontalAlignment = LEFT
        } else if (column == 5 || column == 6) {
            renderer.font = fontMap.font1
        } else {
            renderer.horizontalAlignment = CENTER
        }
        return renderer
    }

}