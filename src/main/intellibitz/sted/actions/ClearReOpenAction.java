package intellibitz.sted.actions;

import intellibitz.sted.util.MenuHandler;

import java.awt.event.ActionEvent;

public class ClearReOpenAction
        extends STEDWindowAction {
    public ClearReOpenAction() {
        super();
    }

    public void actionPerformed(ActionEvent e) {
        MenuHandler.clearReOpenItems(MenuHandler.getInstance());
    }

}
