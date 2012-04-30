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

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;

/**
 * Launches a native browser to open remote
 * @author dmarques
 */
public class ShowHelpAction extends AbstractAction {

    private static final String LABEL = "Help Documentation";
    private static final String DESCRIPTION = "Show Help Documentation";
    private static final Integer MNEMONIC = new Integer(KeyEvent.VK_H);

    private static final String DOCUMENTATION_URL = "http://www.arch.ryerson.ca/";
    
    private static final Logger logger = Logger.getLogger(ShowHelpAction.class.getName());

    //--------------------------------------------------------------------------

    /**
     * ShowHelpAction constructor
     */
    public ShowHelpAction() {
        super(LABEL, null);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY,MNEMONIC);
    }
    
    //--------------------------------------------------------------------------

    /**
     * Launch native desktop browser and open documentation web site URL.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Desktop desktop = Desktop.getDesktop();
        if (!desktop.isDesktopSupported()) {
            logger.log(Level.SEVERE,"Desktop is not supported. (fatal)");
        } else if(!desktop.isSupported(Desktop.Action.BROWSE)) {
            logger.log(Level.SEVERE,"Desktop does not support the browse action. (fatal)");
        } else {
            try {
                URI uri = new java.net.URI(DOCUMENTATION_URL);
                desktop.browse(uri);
            } catch (URISyntaxException ex) {
                logger.log(Level.SEVERE, "Could not open URL {0}", ex);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "Could not open URL {0}", ex);
            }
        }
    }
    
} // end class
