package intellibitz.sted.actions;

import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;

public class SelectAllAction
        extends TableModelListenerAction {
    public SelectAllAction() {
        super();
    }


    public void valueChanged(ListSelectionEvent e) {
    }

    public void actionPerformed(ActionEvent e) {
        super.selectAll();
        fireStatusPosted("Selected All");
    }

}
