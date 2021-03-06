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
package ryerson.daspub.mobile;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ryerson.daspub.model.Archive;
import ryerson.daspub.Config;

/**
 * Mobile publication generator.
 * @author dmarques
 */
public class PublishMobilePresentationTask implements Runnable {

    private Config config;                      // configuration
    private File output;                        // output publication directory
    
    private String index_template = "";         // course index page
    private String course_template = "";        // course page
    private String assignment_template = "";    // assignment page
    private String exam_template = "";          // exam page
    
    private static final Logger logger = Logger.getLogger(PublishMobilePresentationTask.class.getName());

    //--------------------------------------------------------------------------
    
    /**
     * Publisher constructor
     * @param Configuration Configuration
     */
    public PublishMobilePresentationTask(Config Configuration) {
        config = Configuration;
        output = new File(Config.OUTPUT_MOBILE_WORK_PATH);
    }

    //--------------------------------------------------------------------------
    
    /**
     * Run task.
     */
    @Override
    public void run() {
        logger.log(Level.INFO,"STARTING publish mobile presentation task");
        // make the output directory if it does not exist
        if (!output.exists()) {
            output.mkdirs();
        }
        // process the archives
        List<Archive> archives = Archive.getArchives(Config.ARCHIVE_PATHS);
        Iterator<Archive> it = archives.iterator();
        while (it.hasNext()) {
            Archive a = it.next();
            ArchivePage.Write(a,output);
        }        
        logger.log(Level.INFO,"DONE publish mobile presentation task");
    }

} // end class
