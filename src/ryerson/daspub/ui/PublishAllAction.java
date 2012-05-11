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
import ryerson.daspub.artifact.PublishQRTagSheetTask;
import ryerson.daspub.mobile.PublishMobilePresentationTask;
import ryerson.daspub.report.PublishReportTask;
import ryerson.daspub.slideshow.PublishSlideshowTask;
import ryerson.daspub.utility.CopyFilesTask;

/**
 * Publish all content action.
 * @author dmarques
 */
public class PublishAllAction extends AbstractAction {

    private static final String LABEL = "All";
    private static final String DESCRIPTION = "Publish All Content";
    private static final Integer MNEMONIC = new Integer(KeyEvent.VK_A);

    private static final Logger logger = Logger.getLogger(PublishAllAction.class.getName());

    //--------------------------------------------------------------------------

    /**
     * PublishAllAction constructor
     */
    public PublishAllAction() {
        super(LABEL,null);
        putValue(SHORT_DESCRIPTION, DESCRIPTION);
        putValue(MNEMONIC_KEY,MNEMONIC);
    }

    //--------------------------------------------------------------------------

    /**
     * Handle publish all action.
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // get the configuration info
        ApplicationJFrame frame = ApplicationJFrame.getInstance();
        Config config = frame.getConfiguration();

        // create copy static files tasks
        File artifactinput = new File(Config.STATIC_ARTIFACT_CONTENT);
        File artifactoutput = new File(Config.OUTPUT_ARTIFACT_PAGES_PATH);
        File mobileinput = new File(Config.STATIC_MOBILE_CONTENT);
        File mobileoutput = new File(Config.OUTPUT_MOBILE_WORK_PATH);
        File reportinput = new File(Config.STATIC_REPORT_CONTENT);
        File reportoutput = new File(Config.OUTPUT_REPORT_PATH);
        File slideinput = new File(Config.STATIC_SLIDESHOW_CONTENT);
        File slideoutput = new File(Config.OUTPUT_SLIDESHOW_PATH);
        
        CopyFilesTask copyArtifactFilesTask = new CopyFilesTask(artifactinput,artifactoutput);
        CopyFilesTask copyMobileFilesTask = new CopyFilesTask(mobileinput,mobileoutput);
        CopyFilesTask copyReportFilesTask = new CopyFilesTask(reportinput,reportoutput);
        CopyFilesTask copySlideshowFilesTask = new CopyFilesTask(slideinput,slideoutput);

        // create content generation tasks
        PublishArtifactPagesTask makeArtifactPagesTask = new PublishArtifactPagesTask(config);
        PublishMobilePresentationTask makeMobileTask = new PublishMobilePresentationTask(config);
        PublishQRTagSheetTask makeQRTagSheetTask = new PublishQRTagSheetTask(config);
        PublishReportTask makeReportTask = new PublishReportTask(config);
        PublishSlideshowTask makeSlideshowTask = new PublishSlideshowTask(config);

        // execute tasks in parallel
        ExecutorService pool = Main.getThreadPool();
        
        pool.execute(copyArtifactFilesTask);
        pool.execute(copyMobileFilesTask);
        pool.execute(copyReportFilesTask);
        pool.execute(copySlideshowFilesTask);
        
        pool.execute(makeArtifactPagesTask);
        pool.execute(makeMobileTask);
        pool.execute(makeQRTagSheetTask);
        pool.execute(makeReportTask);
        pool.execute(makeSlideshowTask);
    }

} // end class
