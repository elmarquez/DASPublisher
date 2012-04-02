/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ryerson.daspub.report;

import java.util.Iterator;
import java.util.List;
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
                    Course.STATUS status = c.getPublicationStatus();
                    if (status == Course.STATUS.COMPLETE) {
                        complete++;                        
                    } else if (status == Course.STATUS.ERROR) {
                        error++;
                    } else if (status == Course.STATUS.INCOMPLETE) {
                        incomplete++;
                    } else if (status == Course.STATUS.PARTIAL) {
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
     * Get total percentage representation of complete courses.
     * @return 
     */
    public int getPercentageComplete() {
        if (total > 0) {
            return Math.round(complete / getTotalCourseCount());        
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
