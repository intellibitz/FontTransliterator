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
 * $Id:ExitAction.java 55 2007-05-19 05:55:34Z sushmu $
 * $HeadURL: svn+ssh://sushmu@svn.code.sf.net/p/sted/code/FontTransliterator/trunk/src/intellibitz/sted/actions/ExitAction.java $
 */

/**
 * $Id:ExitAction.java 55 2007-05-19 05:55:34Z sushmu $
 * $HeadURL: svn+ssh://sushmu@svn.code.sf.net/p/sted/code/FontTransliterator/trunk/src/intellibitz/sted/actions/ExitAction.java $
 */

package intellibitz.sted.actions;

import intellibitz.sted.io.SettingsXMLWriter;
import intellibitz.sted.ui.STEDWindow;

import javax.swing.*;
import javax.xml.transform.TransformerException;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

public class ExitAction
        extends STEDWindowAction {
    public ExitAction() {
        super();
    }

    public void actionPerformed(ActionEvent e) {
        exit();
    }

    private void exit() {
        STEDWindow stedWindow = getSTEDWindow();
        if (JOptionPane.CANCEL_OPTION !=
                stedWindow.getDesktop().saveDirty()) {
            try {
                SettingsXMLWriter.writeUserSettings(stedWindow);
            } catch (TransformerException ex) {
                logger.severe(
                        "Unable to write User Settings - TransformerException " +
                                ex.getMessage());
                logger.throwing("ExitAction", "exit", ex);
            }
            Runtime.getRuntime().gc();
            System.runFinalization();
            System.exit(0);
        }
    }

    public void windowClosing(WindowEvent e) {
        exit();
    }
}
