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

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ryerson.daspub.Config;
import ryerson.daspub.model.Assignment;
import ryerson.daspub.model.Course;

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
        sb.append(C.getPublicationStatus().toString().toLowerCase());
        sb.append("'>");
        sb.append("\n\t\t<h1><a href=\"javascript:animatedcollapse.toggle('");
        sb.append(C.getPathSafeName());
        sb.append("\')\">");
        sb.append(C.getCourseCode());
        sb.append(" - ");
        sb.append(C.getCourseName());
        sb.append("</a></h1>");
        sb.append("\n\t</div>");
        // toggle wrapper
        sb.append("\n\t<div id='");
        sb.append(C.getPathSafeName());
        sb.append("' style='display:none;'>");
        // description and metadata files
        sb.append("\n\t\t<div class='metadata'>");
        sb.append("\n\t\t\t<ul class='marked'>");
        if (C.hasCourseMetadataFile()) {
            sb.append("\n\t\t\t\t<li class='checked'>Has course description file (");
        } else {
            sb.append("\n\t\t\t\t<li class='crossed'>Does not have course description file (");
        }
        sb.append(Config.COURSE_METADATA_FILE);
        sb.append(")</li>");
        if (C.hasCourseHandoutFile()) {
            sb.append("\n\t\t\t\t<li class='checked'>Has course handout PDF (");
        } else {
            sb.append("\n\t\t\t\t<li class='crossed'>Does not have course handout PDF (");
        }
        sb.append(Config.COURSE_HANDOUT_FILE);
        sb.append(")</li>");
        if (!C.hasAssignments()) {
            sb.append("\n\t\t\t\t<li class='crossed'>Does not have assignment folders.</li>");
        }
        sb.append("\n\t\t\t</ul>");
        sb.append("\n\t\t</div>");
        // assignment reports
        List<Assignment> assignments = C.getAssignments();
        Iterator<Assignment> ita = assignments.iterator();
        while (ita.hasNext()) {
            Assignment a = ita.next();
            sb.append(AssignmentReport.GetHTML(a));
        } 
        // end
        sb.append("\n\t</div>");
        sb.append("\n</div><!-- /course -->");
        return sb.toString();
    }
    
} // end class
