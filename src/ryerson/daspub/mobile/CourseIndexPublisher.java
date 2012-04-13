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
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.Config;
import ryerson.daspub.model.Archive;
import ryerson.daspub.model.Course;
import ryerson.daspub.model.Program;

/**
 * Builds a course index file.
 * @author dmarques
 */
public class CourseIndexPublisher implements Runnable {

    private static String FILE_NAME = "index.html";
    
    private Config config;
    private File output;
    
    private static final Logger logger = Logger.getLogger(CourseIndexPublisher.class.getName());

    //--------------------------------------------------------------------------
    
    /**
     * CourseIndexPublisher constructor
     * @param C Configuration
     * @param Output Output folder
     */
    public CourseIndexPublisher(Config C, File Input, File Output) {
        config = C;
        output = new File(Output,FILE_NAME);
    }
    
    //--------------------------------------------------------------------------

    /**
     * Build course index.
     */
    public void run() {
        // build index
        StringBuilder sb = new StringBuilder();
        List<Archive> archives = Archive.getArchives(Config.ARCHIVE_PATHS);
        Iterator<Archive> ita = archives.iterator();
        while (ita.hasNext()) {
            Archive a = ita.next();
            Iterator<Program> itp = a.getPrograms();
            while (itp.hasNext()) {
                Program p = itp.next();
                Iterator<Course> itc = p.getCourses();
                while (itc.hasNext()) {
                    Course c = itc.next();
                    sb.append("\n\t<li data-filtertext=\"");
                    // add course name to search index
                    sb.append(c.getCourseCode());
                    sb.append(" ");
                    sb.append(c.getName());
                    // add SPC codes to search index
                    List<String> spc = c.getSPCFulfilledCodes();
                    Iterator<String> its = spc.iterator();
                    while (its.hasNext()) {
                        String code = its.next();
                        sb.append(" ");
                        sb.append(code);
                    }
                    sb.append("\"><a href=\"");
                    sb.append(a.getURLSafeName());
                    sb.append("/");
                    sb.append(p.getURLSafeName());
                    sb.append("/");
                    sb.append(c.getURLSafeName());
                    sb.append("/index.html");
                    sb.append("\" data-transition=\"fade\">");
                    sb.append(c.getCourseCode());
                    sb.append(" - ");
                    sb.append(c.getName());
                    sb.append("</a></li>");
                }
            }
        }
        // write file
        try {
            FileUtils.write(output,sb.toString());
        } catch (IOException ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE,"Could not write course index {0}\n\n{1}",
                    new Object[]{output.getAbsolutePath(),
                                 stack});
            System.exit(-1);
        }
    }
    
} // end class
