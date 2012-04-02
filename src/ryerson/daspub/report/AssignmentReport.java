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

import java.util.List;
import java.util.logging.Logger;
import ryerson.daspub.Config;
import ryerson.daspub.model.Assignment;
import ryerson.daspub.model.Submission;

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
        // get the assignment status
        sb.append("\n\t<div class='assignment ");
        sb.append(A.getPublicationStatus().toString().toLowerCase());
        sb.append("'>");
        // item title
        sb.append("\n\t\t<h1>");
        sb.append(A.getName());
        sb.append("</h1>");
        // assignment metadata file
        sb.append("\n\t\t<ul class='marked'>");
        if (A.hasAssignmentMetadataFile()) {
            sb.append("\n\t\t\t<li class='checked'>Has ");
        } else {
            sb.append("\n\t\t\t<li class='crossed'>Does not have ");
        }
        sb.append(Config.ASSIGNMENT_DESCRIPTION_TEXT_FILE);
        sb.append(" file</li>");
        // assignment handout file
        if (A.hasAssignmentHandout()) {
            sb.append("\n\t\t\t<li class='checked'>Has ");
        } else {
            sb.append("\n\t\t\t<li class='crossed'>Does not have ");
        }
        sb.append(Config.ASSIGNMENT_PDF_FILE);
        sb.append(" file</li>");
        // submission metadata files
        if (A.hasSubmissionMetadataFile()) {
            sb.append("\n\t\t\t<li class='checked'>Has ");
        } else {
            sb.append("\n\t\t\t<li class='crossed'>Does not have ");
        }
        sb.append(Config.SUBMISSION_METADATA_FILE);
        sb.append(" file</li>");
        // submission files
        if (A.hasSubmissions()) {
            List<Submission> ls = A.getSubmissions();
            int count = ls.size();
            sb.append("\n\t\t\t<li class='checked'>Has ");
            sb.append(String.valueOf(count));
            sb.append(" student work files.</li>");
        } else {
            sb.append("\n\t\t\t<li class='crossed'>Does not have student work files.</li>");
        }        
        // completed submission metadata
        if (A.hasSubmissions()) {
            sb.append("\n\t\t\t<li class='checked'>Has completed submission metadata in ");
        } else {
            sb.append("\n\t\t\t<li class='crossed'>Does not have completed submission metadata in ");
        }        
        sb.append(Config.SUBMISSION_METADATA_FILE);
        sb.append(" file</li>");

        sb.append("\n\t\t</ul>");
        // return results
        sb.append("\n\t</div><!-- /assignment -->");
        return sb.toString();
    }
    
} // end class
