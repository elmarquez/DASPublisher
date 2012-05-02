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
package ryerson.daspub.slideshow;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import ryerson.daspub.Config;

/**
 * Slideshow publisher.
 * @author dmarques
 * @see http://www.pixedelic.com/plugins/camera/
 */
public class PublishSlideshowTask implements Runnable {

    private Config config;                      // configuration
    private File output;                        // publication directory
    private String slideshow_template = "";     // slideshow template
    
    private static final Logger logger = Logger.getLogger(PublishSlideshowTask.class.getName());

    //--------------------------------------------------------------------------
    
    /**
     * Publisher constructor
     * @param Configuration Configuration
     * @param Output Output directory
     */
    public PublishSlideshowTask(Config Configuration) {
        config = Configuration;
        output = new File(Config.OUTPUT_SLIDESHOW_PATH);
    }

    //--------------------------------------------------------------------------
    
    /**
     * Delete directory
     * @param Dir
     * @throws Exception 
     */
    private void deleteDirectory(File Dir) {
    }
    
    /**
     * Load template files into memory. I know its a stupidly redundant method.
     */
    private void loadTemplates() throws Exception {
        slideshow_template = FileUtils.readFileToString(new File(Config.COURSE_TEMPLATE_PATH));
    }

    /**
     * Run task.
     */
    @Override
    public void run() {
        logger.log(Level.INFO,"DONE publish slideshow presentation task");
    }

} // end class
