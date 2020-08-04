/**
 * Copyright (C) IntelliBitz Technologies.,  Muthu Ramadoss
 * 168, Medavakkam Main Road, Madipakkam, Chennai 600091, Tamilnadu, India.
 * http://www.intellibitz.com
 * training@intellibitz.com
 * +91 44 2247 5106
 * http://groups.google.com/group/etoe
 * http://sted.sourceforge.net
 * <p>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * <p>
 * STED, Copyright (C) 2007 IntelliBitz Technologies
 * STED comes with ABSOLUTELY NO WARRANTY;
 * This is free software, and you are welcome
 * to redistribute it under the GNU GPL conditions;
 * <p>
 * Visit http://www.gnu.org/ for GPL License terms.
 * <p>
 * $Id: MappingPopupListener.java 56 2007-05-19 06:47:59Z sushmu $
 * $HeadURL: svn+ssh://sushmu@svn.code.sf.net/p/sted/code/FontTransliterator/trunk/src/intellibitz/sted/event/MappingPopupListener.java $
 * <p>
 * sted event package
 * defines the listeners and the event types
 */

/**
 * $Id: MappingPopupListener.java 56 2007-05-19 06:47:59Z sushmu $
 * $HeadURL: svn+ssh://sushmu@svn.code.sf.net/p/sted/code/FontTransliterator/trunk/src/intellibitz/sted/event/MappingPopupListener.java $
 */

/**
 * sted event package
 * defines the listeners and the event types
 */
package intellibitz.sted.event;

import intellibitz.sted.actions.TableRowsSelectAction;
import intellibitz.sted.util.MenuHandler;
import intellibitz.sted.util.Resources;

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
