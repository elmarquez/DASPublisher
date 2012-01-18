/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ryerson.daspub;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import ryerson.daspub.utility.FolderFileFilter;

/**
 * Academic program. Contains course folders.
 * @author dmarques
 */
public class Program {
    
    private String path;
    
    //--------------------------------------------------------------------------

    /**
     * Program constructor
     * @param Path 
     */
    public Program(String Path) {
        path = Path;
    }
    
    //--------------------------------------------------------------------------

    /**
     * 
     * @return True if the program folder exists, false otherwise.
     */
    public boolean exists() {
        File f = new File(path);
        return f.exists();
    }

    /**
     * Get the list of courses belonging to the program.
     * @return 
     */
    public Iterator<Course> getCourses() {
        ArrayList<Course> result = new ArrayList<>();
        File program = new File(path);
        File[] files = program.listFiles(new FolderFileFilter());
        for (int i=0;i<files.length;i++) {
            File f = files[i];
            Course c = new Course(f.getAbsolutePath());
            result.add(c);
        }
        return result.iterator();
    }
    
    public File getFile() {
        return new File(path);
    }
    
    public String getName() {
        File f = new File(path);
        return f.getName();
    }
    
    public String getPath() {
        return path;
    }
    
} // end class
