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
import ryerson.daspub.model.Archive;

/**
 * Utility class to publish HTML data for an archive.
 * @TODO should distinguish between writing this class's page, and writing pages for children
 * @author dmarques
 */
public class ArchivePage {
    
    private static final Logger logger = Logger.getLogger(ArchivePage.class.getName());

    //--------------------------------------------------------------------------
    
    /**
     * Write the archive HTML to the specified path. Create the path if it does 
     * not exist.
     * @param A
     * @param F 
     */
    public static void Write(Archive A, File F) {
        try {
            Program program = null;
            File archiveOutDir = new File(F,A.getURLSafeName());
            // process programs
            Iterator<Program> programs = A.getPrograms();
            while (programs.hasNext()) {
                program = programs.next();
                File programOutDir = new File(archiveOutDir,program.getURLSafeName());
                ProgramPage.Write(program, programOutDir);
            }
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE,"Could not copy archive content from {0} to {1}\n\n{2}", 
                    new Object[]{A.getFile().getAbsolutePath(), 
                                 F.getAbsolutePath(), 
                                 stack});
            System.exit(-1);
        }
    }
    
} // end class
