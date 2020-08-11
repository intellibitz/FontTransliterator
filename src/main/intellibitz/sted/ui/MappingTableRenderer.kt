package sted.ui;

import sted.fontmap.FontMap;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

class MappingTableRenderer
        extends DefaultTableCellRenderer {
    MappingTableRenderer() {
        super();
    }

    public void setFontMap(FontMap fontMap) {
        this.fontMap = fontMap;
    }

    public Component getTableCellRendererComponent
            (JTable table, Object value, boolean isSelected, boolean hasFocus,
             int row,
             int column) {
        final DefaultTableCellRenderer renderer = (DefaultTableCellRenderer)
                super.getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, column);
        if (column == 0) {
            renderer.setFont(fontMap.getFont1());
            renderer.setHorizontalAlignment(JLabel.RIGHT);
        } else if (column == 1) {
            renderer.setHorizontalAlignment(JLabel.CENTER);
            renderer.setFocusable(false);
        } else if (column == 2) {
            renderer.setFont(fontMap.getFont2());
            renderer.setHorizontalAlignment(JLabel.LEFT);
        } else if (column == 5 || column == 6) {
            renderer.setFont(fontMap.getFont1());
        } else {
            renderer.setHorizontalAlignment(JLabel.CENTER);
        }
        return renderer;
    }

    private FontMap fontMap;
}

