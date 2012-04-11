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
package ryerson.daspub.init;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.Config;
import ryerson.daspub.model.Archive;
import ryerson.daspub.model.Assignment;
import ryerson.daspub.model.Course;
import ryerson.daspub.model.Program;

/**
 * Create a new configuration file and empty sample repository, or update an
 * existing repository to fill in any missing sample files.
 * @author dmarques
 * @TODO finish the archive initializer
 */
public class Initializer implements Runnable {

    private String configurationFile = "";
    private String archiveMetadataFile = "";
    private String programMetadataFile = "";
    private String courseMetadataFile = "";
    private String assignmentMetadataFile = "";
    private byte[] submissionMetadataFile;
    private String buildAllScript = "";
    private String buildArtifactScript = "";
    private String buildMobileScript = "";
    private String buildSlideshowScript = "";
    private static final Logger logger = Logger.getLogger(Initializer.class.getName());

    //--------------------------------------------------------------------------
    /** 
     * Load default metadata files
     */
    private void loadDefaultFiles() {
        InputStream is = Initializer.class.getResourceAsStream(configurationFile);
        File file = null;
        try {
            String data = IOUtils.toString(is);
        } catch (IOException ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE, "Could not write file {0}.\n\n{1}", new Object[]{file.getAbsolutePath(), stack});
            System.exit(-1);
        }
    }

    /**
     * Run
     */
    public void run() {
        // create a model
        Iterator<Archive> archives = Archive.getArchives(Config.ARCHIVE_PATHS);
        while (archives.hasNext()) {
            Archive a = archives.next();
            Iterator<Program> itp = a.getPrograms();
            while (itp.hasNext()) {
                Program p = itp.next();
                Iterator<Course> courses = p.getCourses();
                while (courses.hasNext()) {
                    Course c = courses.next();
                    tryInitCourse(c);
                }
            }
        }
    }

    /**
     * 
     * @param A Archive
     */
    private static void tryInitArchive(Archive A) {
        
        Iterator<Program> programs = A.getPrograms();
        if (!programs.hasNext()) {
            // create the program folder
            // get an update iterator
        }
        while (programs.hasNext()) {
        }
    }

    /**
     * Initialize assignment if it does not have metadata files.
     * @param A Assignment
     */
    private static void tryInitAssignment(Assignment A) {
    }

    /**
     * Initialize course if it does not have metadata files.
     * @param C Course
     */
    private static void tryInitCourse(Course C) {
        // if the course does not have a metadata file
        if (!C.hasMetadataFile()) {
            // write the default metadata file
        }
        // try to initialize assignments
        List<Assignment> la = C.getAssignments();
        Iterator<Assignment> assignments = la.iterator();
        while (assignments.hasNext()) {
            Assignment a = assignments.next();
            tryInitAssignment(a);
        }
    }

    /**
     * 
     * @param P 
     */
    private static void tryInitProgram(Program P) {
    }
} // end class
