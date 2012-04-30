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
import ryerson.daspub.Main;

/**
 * Exit application action.
 * @author dmarques
 */
public class ExitAction extends AbstractAction {

    private static final String LABEL = "Exit";
    private static final String DESCRIPTION = "Exit application";
    private static final Integer MNEMONIC = new Integer(KeyEvent.VK_X);

    private static final Logger logger = Logger.getLogger(ExitAction.class.getName());

    //--------------------------------------------------------------------------

    /**
     * ExitAction constructor
     */
    public ExitAction() {
        super(LABEL, null);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY,MNEMONIC);
    }
    
    //--------------------------------------------------------------------------
    
    /**
     * Handle application exit action.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        logger.log(Level.INFO,"Exiting application");
        System.exit(Main.SUCCESS);
    }
    
} // end class
