package sted.actions;

import sted.ui.STEDWindow;
import sted.util.MenuHandler;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ItemListenerAction
        extends STEDWindowAction
        implements ItemListener {
    public ItemListenerAction() {
        super();
    }

    public void actionPerformed(ActionEvent e) {
    }

    /**
     * Invoked when an item has been selected or deselected by the user. The
     * code written for this method performs the operations that need to occur
     * when an item is selected (or deselected).
     */
    public void itemStateChanged(ItemEvent e) {
        boolean state = true;
        if (ItemEvent.DESELECTED == e.getStateChange()) {
            state = false;
        }
        final Object object = e.getSource();
        final Action action;
        if (AbstractButton.class.isInstance(object)) {
            final STEDWindow stedWindow = getSTEDWindow();
            action = ((AbstractButton) object).getAction();
            AbstractButton button = MenuHandler.getInstance()
                    .getMenuItem((String) action.getValue(Action.NAME));
            if (button != null) {
                button.setSelected(state);
            }
            button = MenuHandler.getInstance()
                    .getToolButton((String) action.getValue(Action.NAME));
            if (button != null) {
                button.setSelected(state);
            }
//            getSTEDWindow().convertSampleText();
        }
    }

}
