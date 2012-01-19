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
package ryerson.daspub;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Student work submission
 * @author dmarques
 */
public class Submission {
    
    private String path;
    private String course;
    private String author;
    private String instructor;
    private String grade;
    private String tags;
    
    //--------------------------------------------------------------------------

    /**
     * Submission constructor
     * @param Course
     * @param Path
     * @param Author
     * @param Instructor
     * @param Grade
     */
    public Submission(String Course, String Path, String Author, String Instructor, String Grade) {
        course = Course;
        path = Path;
        author = Author;
        instructor = Instructor;
        grade = Grade;
    }
    
    //--------------------------------------------------------------------------

    /**
     * 
     * @return 
     */
    public boolean exists() {
        File f = new File(path);
        return f.exists();
    }

    /**
     * Get author name
     * @return 
     */
    public String getAuthor() {
        return author;
    }

    public List<String> getAuthors() {
        ArrayList<String> authors = new ArrayList<>();
        return authors;
    }
    
    /**
     * Get 
     * @return 
     */
    public String getCourse() {
        return course;
    }

    public File getFile() {
        return new File(path);
    }
    
    /**
     * Get the submission file name
     * @return 
     */
    public String getFileName() {
        File f = new File(path);
        return f.getName();
    }

    /**
     * Get submission grade
     * @return 
     */
    public String getGrade() {
        return grade;
    }
    
    /**
     * Get instructor
     * @return 
     */
    public String getInstructor() {
        return instructor;
    }

    /**
     * Get tags
     * @return 
     */
    public Set<String> getTags() {
        HashSet<String> tagset = new HashSet<>();
        String[] ts = tags.split(",");
        for (int i=0;i<ts.length;i++) {
            String tag = ts[i];
            tag = tag.replace(" ","");
            tagset.add(tag);
        }
        return tagset;
    }
    
    /**
     * Get thumbnail image or icon for submission.
     */
    public void getThumbnail() throws Exception {
        throw new Exception("Not implemented yet.");
    }

    /**
     * 
     * @param Height
     * @param Width
     * @throws Exception 
     */
    public void getThumbnail(int Height, int Width) throws Exception {
        throw new Exception("Not implemented yet.");
    }
    
} // end class
