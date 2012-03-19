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
package ryerson.daspub.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.Config;
import ryerson.daspub.utility.FolderFileFilter;

/**
 * Course entity. Lazy loads data from the file system.
 * @author dmarques
 */
public class Course {

    private String path;
    
    private String description = "";
    private String format = "";
    private ArrayList<String> instructors = new ArrayList<>();
    private ArrayList<String> cacbcriteria = new ArrayList<>();
    
    private static final Logger logger = Logger.getLogger(Course.class.getName());

    //--------------------------------------------------------------------------
    
    /**
     * Course constructor
     * @param D Directory
     */
    public Course(String Path) {
        path = Path;
        parseDescriptionFile();
    }

    //--------------------------------------------------------------------------
    
    /**
     * Get assignment iterator.
     */
    public Iterator<Assignment> getAssignments() {
        File dir = new File(path);
        ArrayList<Assignment> items = new ArrayList<>();
        File[] files = dir.listFiles(new FolderFileFilter());
        for (int i = 0; i < files.length; i++) {
            Assignment a = new Assignment(files[i]);
            items.add(a);
        }
        return items.iterator();
    }

    /**
     * Get SPC items
     * @return 
     */
    public List<String> getCACBCriteria() {
        return new ArrayList<String>();
    }
    
    /**
     * Get course description.
     */
    public String getDescription(){ 
        return "course description";
    }
    
    /**
     * 
     * @return 
     */
    public List<String> getExams() {
        ArrayList<String> items = new ArrayList<>();
        items.add("Not implemented yet");
        return items;
    }

    /**
     * Get course folder file
     * @return 
     */
    public File getFile() {
        return new File(path);
    }

    /**
     * Get course format
     * @return 
     */
    public String getFormat() {
        return "course format";
    }

    /**
     * Get list of instructors
     */
    public List<String> getInstructors() {
        return new ArrayList<String>();
    }
    
    /**
     * Get course title. The course title is the folder name.
     * @return
     */
    public String getName() {
        File dir = new File(path);
        return dir.getName();
    }

    /**
     * Get course folder path
     * @return 
     */
    public String getPath() {
        return path;
    }

    /**
     * Get path safe name.
     */
    public String getPathSafeName() {
        String name = getName();
        return name.replace(" ", "_");
    }

    /**
     * Get publication status flag
     * @return
     */
    public String getPublicationStatus() {
        return "incomplete";
    }

    /**
     * Get link to course description PDF 
     * @return 
     */
    public String getSyllabusLink() {
        File file = new File(path, Config.COURSE_DESCRIPTION_PDF_FILE);
        if (file.exists()) {
            return "<a href='" + Config.COURSE_DESCRIPTION_PDF_FILE + "'>Course syllabus</a>";
        } else {
            return "Course syllabus file not available.";
        }
    }

    /**
     * Parse the course description file and assign values to local variables.
     * 0 - Description
     * 1 - Course format, hours
     * 2 - Instructors
     * 3 - CACB Criteria
     * TODO this should be implemented for lazy loading instead
     */
    private void parseDescriptionFile() {
        File file = new File(path,Config.COURSE_DESCRIPTION_TEXT_FILE);
        if (file.exists()) {
            try {
                String text = FileUtils.readFileToString(file);
                String[] block = text.split("==");
                // get rid of empty elements
                int n = 0;
                if (block[0].equals("")) {
                    n++;
                }
                // process elements
                if (block.length>n+1) {
                    description = block[n+1].replaceAll("\r\n","").trim();
                }
                if (block.length>n+3) {
                    format = block[n+3].replaceAll("\r\n","").trim();
                }
                if (block.length>n+5) {
                    String[] items = block[n+5].split("-");
                    for (int i=0;i<items.length;i++) {
                        if (!items[i].equals("") && !items[i].equals("\r\n") && !items[i].contains("\r\n\r\n")) {
                            instructors.add(items[i].replaceAll("\r\n",""));
                        }
                    }
                }
                if (block.length>=n+7) {
                    String[] items = block[n+7].split("-");
                    for (int i=0;i<items.length;i++) {
                        if (!items[i].equals("") && !items[i].equals("\r\n") && !items[i].contains("\r\n\r\n")) {
                            cacbcriteria.add(items[i].replaceAll("\r\n",""));
                        }
                    }
                }
            } catch (IOException ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE,"Could not parse course description file {0}\n\n{1}",
                        new Object[]{file.getAbsolutePath(),stack});
            }
        }
    }
        
} // end class
