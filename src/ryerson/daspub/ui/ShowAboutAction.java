/*
 * Copyright (c) 2011 Davis Marques
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */
package ryerson.daspub.ui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

/**
 * Show about application jframe.
 * @author dmarques
 */
public class ShowAboutAction extends AbstractAction {

    private static final String LABEL = "About this Application";
    private static final String DESCRIPTION = "About this Application";
    private static final Integer MNEMONIC = new Integer(KeyEvent.VK_A);
    
    private static final String COPYRIGHT = "\u00a9";    
    private static final String DIALOG_TITLE = "About";
    private static final String DIALOG_MESSAGE = "DAS Publisher\n"
            + "Copyright " 
            + COPYRIGHT 
            + " 2012 Department of Architectural Science\n"
            + "Ryerson University\n"
            + "http://www.arch.ryerson.ca";

    private static final Logger logger = Logger.getLogger(NewProjectAction.class.getName());
    
    //--------------------------------------------------------------------------
    
    /**
     * ShowAboutAction constructor
     */
    public ShowAboutAction() {
        super(LABEL, null);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY, MNEMONIC);
    }
    
    //--------------------------------------------------------------------------

    /**
     * Action performed handler. Show window.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        ApplicationJFrame parent = ApplicationJFrame.getInstance();
        logger.log(Level.INFO,"Opening 'About this Application' dialog.");
        JOptionPane.showMessageDialog(parent, DIALOG_MESSAGE, DIALOG_TITLE, JOptionPane.NO_OPTION);
    }
    
} // end class
