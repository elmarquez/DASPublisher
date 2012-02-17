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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.Archive;
import ryerson.daspub.Config;
import ryerson.daspub.Course;
import ryerson.daspub.Program;
import ryerson.daspub.utility.NonThumbnailIndexFileFilter;

/**
 * Mobile publication generator.
 * @author dmarques
 */
public class MobilePublisher implements Runnable {

    private Config config;                      // configuration
    private File output;                        // output publication directory
    
    private String index_template = "";         // course index page
    private String course_template = "";        // course page
    private String assignment_template = "";    // assignment page
    private String exam_template = "";          // exam page
    
    private static final Logger logger = Logger.getLogger(MobilePublisher.class.getName());

    //--------------------------------------------------------------------------
    
    /**
     * Publisher constructor
     * @param Config Configuration
     * @param Output Output directory
     * @throws Exception
     */
    public MobilePublisher(Config Config, File Output) throws Exception {
        config = Config;
        output = Output;
    }

    //--------------------------------------------------------------------------
    
    /**
     * Run the publisher.
     */
    public void run() {
        // make the output directory if it does not exist
        if (!output.exists()) {
            output.mkdirs();
        }
        // copy static files to output directory
        try {
            File staticFiles = new File(Config.STATIC_FILES_PATH);
            logger.log(Level.INFO,"Copying static files from {0} to {1}", 
                    new Object[]{staticFiles.getAbsolutePath(),output.getAbsolutePath()});
            FileUtils.copyDirectory(staticFiles,output,new NonThumbnailIndexFileFilter());
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE,"Could not copy static files from {0} to {1}\n\n{2}", 
                    new Object[]{Config.STATIC_FILES_PATH, output.getAbsolutePath(), stack});
            System.exit(-1);
        }
        // process the archives
        Iterator<Archive> archives = Archive.getArchives(Config.ARCHIVE_PATHS);
        Iterator<Program> programs = null;
        Iterator<Course> courses = null;
        Archive archive = null;
        Program program = null;
        Course course = null;
        File courseOutputPath = null;
        File archiveOutPath = new File(output.getAbsolutePath(),"program");
        while (archives.hasNext()) {
            try {
                archive = archives.next();
                archiveOutPath = new File(archiveOutPath,archive.getPathSafeName());
                // process programs
                programs = archive.getPrograms();
                while (programs.hasNext()) {
                    // process courses
                    program = programs.next();
                    File programOutPath = new File(archiveOutPath,program.getPathSafeName());
                    courses = program.getCourses();
                    while (courses.hasNext()) {
                        course = courses.next();
                        courseOutputPath = new File(programOutPath, course.getPathSafeName());
                        course.writeHTML(courseOutputPath);
                    }
                }
                // write program summary
                // write archive summary
            } catch (Exception ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE,"Could not copy archive content from {0} to {1}\n\n{2}", 
                        new Object[]{archive.getPath(), output.getAbsolutePath(), stack});
                System.exit(-1);
            }
        }
    }

} // end class
