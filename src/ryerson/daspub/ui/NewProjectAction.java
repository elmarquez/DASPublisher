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
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

/**
 * New project action.
 * @author dmarques
 */
public class NewProjectAction extends AbstractAction {

    private static final String LABEL = "New Project";
    private static final String DESCRIPTION = "New Project";
    private static final Integer MNEMONIC = new Integer(KeyEvent.VK_N);

    private static final Logger logger = Logger.getLogger(NewProjectAction.class.getName());

    //--------------------------------------------------------------------------
    /**
     * NewProjectAction constructor
     */
    public NewProjectAction() {
        super(LABEL, null);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY, MNEMONIC);
    }

    //--------------------------------------------------------------------------
    /**
     * Handle new project action.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // open a new dialog box 
        ApplicationJFrame frame = ApplicationJFrame.getInstance();
        JFileChooser fc = new JFileChooser();
        //In response to a button click:
        int returnVal = fc.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            // set the current configuration file path
            File file = fc.getSelectedFile();
            // open the file
            String msg = "Opening: " + file.getName() + ".";
            logger.log(Level.INFO,msg);
        } else {
            logger.log(Level.INFO,"Open command cancelled by user.");
        }
        // load configuration into application frame
        frame.openProject(null);
        
    }
    
} // end class
