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
import javax.swing.AbstractAction;

/**
 * Open project action.
 * @author dmarques
 */
public class OpenProjectAction extends AbstractAction {

    private static final String LABEL = "Open Project";
    private static final String DESCRIPTION = "Open Project";
    private static final Integer MNEMONIC = new Integer(KeyEvent.VK_O);

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
        ApplicationJFrame frame = ApplicationJFrame.getInstance();
        // open the file chooser
    }
    
} // end class
