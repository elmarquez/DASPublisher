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
package ryerson.daspub.report;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import ryerson.daspub.model.Assignment;
import ryerson.daspub.model.Course;
import ryerson.daspub.utility.FolderFileFilter;

/**
 * Course object status report.
 * @author dmarques
 */
public class CourseReport {

    private static final Logger logger = Logger.getLogger(CourseReport.class.getName());

    //--------------------------------------------------------------------------

    /**
     * Get status report.
     * @param C Course
     * @return 
     */
    public static String GetHTML(Course C) {
        logger.log(Level.INFO,"Building report for course {0}",C.getPath());
        // toggle block
        StringBuilder sb = new StringBuilder();
        sb.append("\n<script type='text/javascript'>");
        sb.append("animatedcollapse.addDiv('");
        sb.append(C.getPathSafeName());
        sb.append("','fade=1')");
        sb.append("</script>");
        sb.append("\n<div class='course'>");
        // item title
        sb.append("\n\t<div class='title ");
        sb.append(C.getPublicationStatus());
        sb.append("'>");
        sb.append("\n\t\t<h1>");
        sb.append(C.getName());
        sb.append("</h1>");
        sb.append("\n\t\t<a href=\"javascript:animatedcollapse.toggle('");
        sb.append(C.getPathSafeName());
        sb.append("\')\">+</a>");
        sb.append("\n\t</div>");
        // toggle wrapper
        sb.append("\n\t<div id='");
        sb.append(C.getPathSafeName());
        sb.append("' style='display:none;'>");
        // description and metadata files
        sb.append("\n\t\t<div class='metadata'>");
        sb.append("\n\t\t\t<ul class='marked'>");
        sb.append("\n\t\t\t\t<li class='checkmark'>Course description file (course.txt)</li>");
        sb.append("\n\t\t\t\t<li class='cross'>Course description PDF file (course.pdf)</li>");
        sb.append("\n\t\t\t</ul>");
        sb.append("\n\t\t</div>");
        // file reports
        File dir = new File(C.getPath());
        File[] dirs = dir.listFiles(new FolderFileFilter());
        if (dirs.length > 0) {
            Arrays.sort(dirs, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return f1.getName().compareTo(f2.getName());
                }
            });
            for (int i = 0; i < dirs.length; i++) {
                Assignment a = new Assignment(dirs[i]);
                sb.append(AssignmentReport.GetHTML(a));
            }
        } else {
            sb.append("\n\t\t<div class='assignment'>");
            sb.append("\n\t\t\t<ul class='marked'>");
            sb.append("\n\t\t\t\t<li class='cross'>No assignments have been provided for this course.</li>");
            sb.append("\n\t\t\t</ul>");
            sb.append("\n\t\t</div>");
        }
        // end
        sb.append("\n\t</div>");
        sb.append("\n</div><!-- /course -->");
        return sb.toString();
    }
    
} // end class