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
import java.util.logging.Logger;
import ryerson.daspub.model.Assignment;
import ryerson.daspub.utility.NonImageFileFilter;

/**
 * Assignment object status report.
 * @author dmarques
 */
public class AssignmentReport {
    
    private static final Logger logger = Logger.getLogger(AssignmentReport.class.getName());

    //--------------------------------------------------------------------------

    /**
     * Get status report.
     * @param A Assignment
     * @return HTML status report
     */
    public static String GetHTML(Assignment A) {
        StringBuilder sb = new StringBuilder();
        // if the folder is 100%, show green
        // if it has the required description/metadata/student work files, show yellow
        // if it is missing all files, show red
        sb.append("\n\t<div class='assignment ");
        sb.append(A.getPublicationStatus());
        sb.append("'>");
        // item title
        sb.append("\n\t\t<h1>");
        sb.append(A.getName());
        sb.append("</h1>");
        // description and metadata files
        sb.append("\n\t\t<ul class='marked'>");
        if (A.hasDescriptionFile()) {
            sb.append("\n\t\t\t<li class='checked'>Has assignment_description.txt file</li>");
        } else {
            sb.append("\n\t\t\t<li class='crossed'>Does not have assignment_description.txt file</li>");
        }
        if (A.hasMetadataFile()) {
            sb.append("\n\t\t\t<li class='checked'>Has files.xls file</li>");
        } else {
            sb.append("\n\t\t\t<li class='crossed'>Does not have files.xls file</li>");
        }
        sb.append("\n\t\t</ul>");
        // student work files
        sb.append("\n\t\t<div class='files'>");
        File[] files = A.getPath().listFiles();
        int count = files.length;
        sb.append("\n\t\t\t<p>There are ");
        sb.append(count);
        sb.append(" files in the assignment folder.");
        sb.append("</p>");
        sb.append("\n\t\t\t<ul>");
        files = A.getPath().listFiles(new NonImageFileFilter());
        for (int i=0;i<files.length;i++) {
            // if description or metadata file then pass
            sb.append("\n\t\t\t\t<li>");
            sb.append(files[i].getName());
            sb.append("</li>");
        }
        sb.append("\n\t\t\t</ul>");
        sb.append("\n\t\t</div>");
        // return results
        sb.append("\n\t</div><!-- /assignment -->");
        return sb.toString();
    }
    
} // end class
