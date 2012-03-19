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
import java.util.logging.Level;
import java.util.logging.Logger;
import ryerson.daspub.model.Archive;
import ryerson.daspub.model.Program;

/**
 * Archive object status report.
 * @author dmarques
 */
public class ArchiveReport {
    
    private static final Logger logger = Logger.getLogger(ArchiveReport.class.getName());

    //--------------------------------------------------------------------------

    /**
     * Get status report.
     * @param A Archive
     * @return 
     */
    public static String GetHTML(Archive A) {
        logger.log(Level.INFO,"Building report for archive {0}",A.getPath());
        // start report block
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n<div class='archive'>");
        sb.append("\n<h1>");
        sb.append(A.getPath());
        sb.append("</h1>");
        // get program report
        Iterator<Program> programs = A.getPrograms();
        while (programs.hasNext()) {
            Program program = programs.next();
            sb.append(ProgramReport.GetHTML(program));
        }
        // close report block
        sb.append("</div>");
        // return result
        return sb.toString();
    }
    
} // end class
