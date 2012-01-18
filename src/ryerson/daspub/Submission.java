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
import java.util.List;

/**
 * Student work submission
 * @author dmarques
 */
public class Submission {
    
    private String path;
    private String filename;
    private String course;
    private String author;
    private String instructor;
    private String grade;
    private String tag;
    
    //--------------------------------------------------------------------------

    /**
     * Submission constructor
     * @param Path 
     */
    public Submission(String Path) {
        path = Path;
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
    public List<String> getTags() {
        ArrayList<String> tags = new ArrayList<>();
        String[] sp = tag.split(",");
        for (int i=0;i<sp.length;i++) {
            String tag = sp[i];
            tag = tag.replace(" ","");
            tags.add(tag);
        }
        return tags;
    }
    
} // end class
