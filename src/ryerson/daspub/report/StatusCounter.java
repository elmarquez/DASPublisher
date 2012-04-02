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
import ryerson.daspub.Config.STATUS;
import ryerson.daspub.model.Archive;
import ryerson.daspub.model.Course;
import ryerson.daspub.model.Program;

/**
 *
 * @author dmarques
 */
public class StatusCounter {
    
    private List<String> paths;
    
    private int complete;
    private int partial;
    private int incomplete;
    private int error;
    private int total;
    
    private int percentage;
    
    //--------------------------------------------------------------------------

    /**
     * StatusCounter constructor
     * @param Paths
     */
    public StatusCounter(List<String> Paths) {
        paths = Paths;
    }
    
    //--------------------------------------------------------------------------

    /**
     * Tabulate values
     */
    public void count() {
        Iterator<Archive> archives = Archive.getArchives(paths);
        while (archives.hasNext()) {
            Archive a = archives.next();
            Iterator<Program> ip = a.getPrograms();
            while (ip.hasNext()) {
                Program p = ip.next();
                Iterator<Course> ic = p.getCourses();
                while (ic.hasNext()) {
                    Course c = ic.next();
                    STATUS status = c.getPublicationStatus();
                    if (status == STATUS.COMPLETE) {
                        complete++;                        
                    } else if (status == STATUS.ERROR) {
                        error++;
                    } else if (status == STATUS.INCOMPLETE) {
                        incomplete++;
                    } else if (status == STATUS.PARTIAL) {
                        partial++;
                    }
                    total++;
                }
            }
        }

    }
    
    /**
     * Get the total number of complete courses in the archive.
     * @return 
     */
    public int getCompleteCourseCount() {
        return complete;
    }
    
    /**
     * Get the total number of courses with errors in the archive.
     * @return 
     */
    public int getErrorCourseCount() {
        return error;
    }
    
    /**
     * Get the total number of incomplete courses in the archive.
     * @return 
     */
    public int getIncompleteCourseCount() {
        return incomplete;
    }
    
    /**
     * Get the total number of partially complete courses in the archive.
     * @return 
     */
    public int getPartialCourseCount() {
        return partial;
    }

    /**
     * Get total percentage representation of complete courses. Partially 
     * complete courses count for half a percentage point.
     * @return 
     */
    public int getPercentageComplete() {
        if (total > 0) {
            return complete + (partial / 2) / getTotalCourseCount();
        }
        return 0;
    }
    
    /**
     * Get the total number of courses in the archive.
     * @return 
     */
    public int getTotalCourseCount() {
        return total;
    }
    
} // end class
