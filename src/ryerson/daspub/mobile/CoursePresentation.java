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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.Config;
import ryerson.daspub.model.Assignment;
import ryerson.daspub.model.Course;
import ryerson.daspub.utility.PDFFileFilter;

/**
 *
 * @author dmarques
 */
public class CoursePresentation {
 
    private static final Logger logger = Logger.getLogger(CoursePresentation.class.getName());

    //--------------------------------------------------------------------------
    
    /**
     * Generate a comma separated list of items.
     * @param L List of items
     * @return 
     */
    private static String getHTMLFormattedList(List<String> L) {
        StringBuilder sb = new StringBuilder();
        int count = L.size();
        for (int i=0;i<count-1;i++) {
            sb.append(L.get(i));
            sb.append(", ");
        }
        return sb.toString();
    }
    
    /**
     * Write course data and HTML index file to specified output folder
     * @param Output Output folder
     */
    public static void Write(Course C, File F) {
        logger.log(Level.INFO, "Writing course folder {0}", C.getPath());
        try {
            // create the output folder
            F.mkdirs();
            // copy course metadata, etc. files to output
            File sourcePath = C.getFile();
            File[] files = sourcePath.listFiles(new PDFFileFilter());
            for (int i=0;i<files.length;i++) {
                FileUtils.copyFile(files[i], new File(F,files[i].getName()));
            }
            // load index page template file
            String template = FileUtils.readFileToString(new File(Config.COURSE_TEMPLATE_PATH));
            // build index page
            template = template.replace("${course.title}", C.getName());
            template = template.replace("${course.description}", C.getDescription());
            template = template.replace("${course.format}", C.getFormat());
            template = template.replace("${course.syllabus}", C.getSyllabusLink());
            template = template.replace("${course.instructors}", getHTMLFormattedList(C.getInstructors()));
            template = template.replace("${course.cacb.criteria}", getHTMLFormattedList(C.getCACBCriteria()));
            // build assignment index
            Iterator<Assignment> assignments = C.getAssignments();
            StringBuilder sb = new StringBuilder();
            sb.append("<ul data-role=\"listview\" data-inset=\"true\" data-theme=\"c\">");
            File assignmentOutputPath = null;
            while (assignments.hasNext()) {
                Assignment a = assignments.next();
                // add assignment to index
                sb.append("\n\t<li><a href=\"");
                sb.append(a.getPathSafeName());
                sb.append("\">");
                sb.append(a.getName());
                sb.append("</a></li>");
                // process assignment output
                assignmentOutputPath = new File(F,a.getPathSafeName());
                AssignmentPresentation.Write(a,assignmentOutputPath);
            }
            sb.append("\n</ul>\n");
            template = template.replace("${course.assignments}",sb.toString());
            // build exam list
            // TODO revise formatted lists method for more flexibility
            template = template.replace("${course.exams}", getHTMLFormattedList(C.getExams()));
            // write index page
            File index = new File(F.getAbsolutePath(), "index.html");
            FileUtils.write(index, template);
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE, "Could not copy course {0} to {1}\n\n{2}", 
                    new Object[]{C.getPath(),F.getAbsolutePath(),stack});
        }
    }
    
} // end class
