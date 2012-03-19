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
import java.util.logging.Logger;
import ryerson.daspub.model.Course;
import ryerson.daspub.model.Program;

/**
 * Program object status report.
 * @author dmarques
 */
public class ProgramReport {
    
    private static final Logger logger = Logger.getLogger(ProgramReport.class.getName());

    //--------------------------------------------------------------------------
    
    /**
     * Get status report.
     * @param P Program
     * @return 
     */
    public static String GetHTML(Program P) {
        StringBuilder sb = new StringBuilder();
        // program block header
        sb.append("\n\n<div class='program'>");
        sb.append("\n<h1>");
        sb.append(P.getName());
        sb.append("</h1>");
        //  process courses
        Iterator<Course> courses = P.getCourses();
        while (courses.hasNext()) {
            Course course = courses.next();
            sb.append(CourseReport.GetHTML(course));
        }
        sb.append("</div>");
        // return result
        return sb.toString();
    }
    
} // end class
