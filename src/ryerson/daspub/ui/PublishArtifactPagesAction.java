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
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import ryerson.daspub.Config;
import ryerson.daspub.Main;
import ryerson.daspub.artifact.PublishArtifactPagesTask;
import ryerson.daspub.utility.CopyFilesTask;

/**
 * Publish artifact pages content action.
 * @author dmarques
 */
public class PublishArtifactPagesAction extends AbstractAction {

    private static final String LABEL = "Artifact Pages";
    private static final String DESCRIPTION = "Publish Artifact Pages";
    private static final Integer MNEMONIC = new Integer(KeyEvent.VK_A);

    private static final Logger logger = Logger.getLogger(PublishArtifactPagesAction.class.getName());

    //--------------------------------------------------------------------------

    /**
     * PublishArtifactPagesAction constructor
     */
    public PublishArtifactPagesAction() {
        super(LABEL,null);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY,MNEMONIC);
    }
    
    //--------------------------------------------------------------------------
    
    /**
     * Handle publish action.
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // get the configuration info
        ApplicationJFrame frame = ApplicationJFrame.getInstance();
        Config config = frame.getConfiguration();
        // create tasks
        File input = new File(Config.STATIC_ARTIFACT_CONTENT);
        File output = new File(Config.OUTPUT_ARTIFACT_PAGES_PATH);
        CopyFilesTask copyArtifactFilesTask = new CopyFilesTask(input,output);
        PublishArtifactPagesTask makeArtifactPagesTask = new PublishArtifactPagesTask(config);
        // execute tasks in parallel
        ExecutorService pool = Main.getThreadPool();
        pool.execute(copyArtifactFilesTask);
        pool.execute(makeArtifactPagesTask);
    }
    
} // end class
