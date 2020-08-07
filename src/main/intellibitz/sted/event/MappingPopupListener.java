package sted.event;

import sted.actions.TableRowsSelectAction;
import sted.ui.MenuHandler;
import sted.io.Resources;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

public class MappingPopupListener
        extends MouseAdapter {
    private JPopupMenu popupMenu;

    public MappingPopupListener() {
        super();
    }

    public void load() {
        popupMenu = MenuHandler.getInstance()
                .getPopupMenu(Resources.MENU_POPUP_MAPPING);
    }

    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            setTableOnAction((JTable) e.getSource());
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private void setTableOnAction(JTable table) {
        final Map actions = MenuHandler.getInstance().getActions();
        for (Object o : actions.values()) {
            final Object action = o;
            if (TableRowsSelectAction.class.isInstance(action)) {
                ((TableRowsSelectAction) action).setTable(table);
            }
        }
    }

}
