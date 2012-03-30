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

import ryerson.daspub.model.Program;
import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.model.Course;

/**
 * Utility class to publish HTML data for a program
 * @author dmarques
 */
public class ProgramPresentation {
    
    private static final Logger logger = Logger.getLogger(ProgramPresentation.class.getName());

    //--------------------------------------------------------------------------
    
    /**
     * Write the program HTML to the specified path.  Create the path if it
     * does not exist.
     * @param P Program
     * @param F Output folder
     */
    public static void Write(Program P, File F) {
        try {
            Iterator<Course> courses = null;
            Course course = null;
            File courseOutputPath = null;
            // process courses
            courses = P.getCourses();
            while (courses.hasNext()) {
                course = courses.next();
                courseOutputPath = new File(F, course.getPathSafeName());
                CoursePresentation.Write(course,courseOutputPath);
            }
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE,"Could not copy archive content from {0} to {1}\n\n{2}", 
                    new Object[]{P.getPath(), F.getAbsolutePath(), stack});
            System.exit(-1);
        }
    }
    
} // end class
