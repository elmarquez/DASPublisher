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
 * Open project action.
 * @author dmarques
 */
public class OpenProjectAction extends AbstractAction {

    private static final String LABEL = "Open Project";
    private static final String DESCRIPTION = "Open Project";
    private static final Integer MNEMONIC = new Integer(KeyEvent.VK_O);

    private static final Logger logger = Logger.getLogger(OpenProjectAction.class.getName());

    //--------------------------------------------------------------------------

    /**
     * OpenProjectAction constructor
     */
    public OpenProjectAction() {
        super(LABEL, null);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY, MNEMONIC);
    }
    
    //--------------------------------------------------------------------------
    
    /**
     * Handle save project action.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // open a file chooser dialog
        ApplicationJFrame frame = ApplicationJFrame.getInstance();
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // check if user has selected a file
        int returnVal = fc.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            // set the current configuration file path
            File file = fc.getSelectedFile();
            if (file.exists()) {
                try {
                    frame.openProject(file);
                } catch (Exception ex) {
                    logger.log(Level.SEVERE,"Could not open project file {0}\n\n{1}", 
                            new Object[]{file.getAbsolutePath(),ex});
                }
            }
        } else {
            logger.log(Level.INFO,"Open project file command cancelled by user.");
        }
    }
    
} // end class
