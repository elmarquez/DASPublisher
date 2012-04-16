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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import ryerson.daspub.Config.STATUS;
import ryerson.daspub.model.Archive;
import ryerson.daspub.model.Assignment;
import ryerson.daspub.model.Course;
import ryerson.daspub.model.Program;
import ryerson.daspub.model.Submission;

/**
 * Creates a cumulative count of publication states for all courses in the 
 * archive.
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
    
    private HashMap<String,Submission> unique = new HashMap<>();
    private ArrayList<Submission> duplicates = new ArrayList<>();
    
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
        List<Archive> archives = Archive.getArchives(paths);
        Iterator<Archive> ita = archives.iterator();
        while (ita.hasNext()) {
            Archive a = ita.next();
            Iterator<Program> ip = a.getPrograms();
            while (ip.hasNext()) {
                Program p = ip.next();
                Iterator<Course> ic = p.getCourses();
                while (ic.hasNext()) {
                    Course c = ic.next();
                    STATUS status = c.getStatus();
                    if (hasDuplicateSubmissionID(c) || status == STATUS.ERROR) {
                        status = STATUS.ERROR;
                    } else if (status == STATUS.COMPLETE) {
                        complete++;                        
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
     * Get submission items with duplicate IDs
     * @return 
     */
    public List<Submission> getDuplicates() {
        return duplicates;
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
    
    /**
     * Determine if the course has a submission ID used elsewhere.
     * @return True if the course has submission IDs that are duplicated elsewhere, false otherwise.
     */
    private boolean hasDuplicateSubmissionID(Course C) {
        boolean hasduplicates = false;
        List<Assignment> assignments = C.getAssignments();
        Iterator<Assignment> ita = assignments.iterator();
        while (ita.hasNext()) {
            Assignment a = ita.next();
            List<Submission> ls = a.getSubmissions();
            Iterator<Submission> its = ls.iterator();
            while (its.hasNext()) {
                Submission s = its.next();
                if (!unique.containsKey(s.getSubmissionId())) {
                    unique.put(s.getSubmissionId(), s);
                } else {
                    duplicates.add(s);
                    hasduplicates = true;
                }
            }
        }
        return hasduplicates;
    }
    
} // end class
