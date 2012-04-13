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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import ryerson.daspub.Config;
import ryerson.daspub.Config.STATUS;
import ryerson.daspub.utility.FolderFileFilter;
import ryerson.daspub.utility.MarkupParser;
import ryerson.daspub.utility.URLUtils;

/**
 * Academic course.
 * @author dmarques
 */
public class Course {

    private File source;                                    // source folder
    private String description = "";                        // course description
    private String format = "";                             // course format
    private List<String> instructors = new ArrayList<>();   // instructors
    private List<String> spc = new ArrayList<>();           // student performance criteria
    
    private static final Logger logger = Logger.getLogger(Course.class.getName());

    //--------------------------------------------------------------------------
    
    /**
     * Course constructor
     * @param Path Source folder path
     */
    public Course(String Path) {
        source = new File(Path);
        parseDescriptionFile();
    }

    //--------------------------------------------------------------------------
    
    /**
     * Get assignment iterator.
     */
    public List<Assignment> getAssignments() {
        ArrayList<Assignment> items = new ArrayList<>();
        File[] files = source.listFiles(new FolderFileFilter());
        for (int i = 0; i < files.length; i++) {
            Assignment a = new Assignment(files[i]);
            items.add(a);
        }
        return items;
    }

    /**
     * Get course code
     */
    public String getCourseCode() {
        String result = "";
        String[] s = this.getFolder().getName().split("-");
        if (s.length > 1) {
            result = s[0];
        }
        return result;
    }
    
    /**
     * Get course description.
     */
    public String getDescription(){ 
        return description;
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
    public File getFolder() {
        return source;
    }

    /**
     * Get course format
     * @return Course format description
     */
    public String getFormat() {
        return format;
    }
    
    /**
     * Get full course folder name.
     * @return 
     */
    public String getFullname() {
        return source.getName();
    }

    /**
     * Get list of instructors
     */
    public List<String> getInstructors() {
        return instructors;
    }

    /**
     * Get course name
     * @return 
     */
    public String getName() {
        String result = "";
        String[] s = this.getFolder().getName().split("-");
        if (s.length > 1) {
            result = s[1];
        }
        return result;
    }
    
    /**
     * Get list of SPC criteria that this course fulfills.
     * @return 
     */
    public List<String> getSPCFulfilled() {
        return spc;
    }
    
    /**
     * Get list of SPC criteria codes fulfilled by this course. Assumes that
     * SPC codes start with the characters "SPC" and are provided in an 
     * enumerated list within the metadata file.
     * @return List of SPC codes
     */
    public List<String> getSPCFulfilledCodes() {
        ArrayList<String> codes = new ArrayList<>();
        Iterator<String> it = spc.iterator();
        while (it.hasNext()) {
            String criteria = it.next();
            String[] items = criteria.split("-");
            if (items.length>1) {
                String code = items[0].trim();
                if (code.toUpperCase().startsWith("SPC")) {
                    codes.add(code);                    
                }
            }
        }
        return codes;
    }
    
    /**
     * Get publication status.
     * @return
     */
    public STATUS getStatus() {
        if (!this.hasMetadataFile()) {
            return STATUS.INCOMPLETE;
        }
        if (!this.hasSyllabusFile()) {
            return STATUS.INCOMPLETE;
        }
        if (!this.hasAssignments()) {
            return STATUS.INCOMPLETE;
        }
        List<Assignment> assignments = this.getAssignments();
        Iterator<Assignment> ita = assignments.iterator();
        while (ita.hasNext()) {
            Assignment a = ita.next();
            STATUS status = a.getStatus();
            if (status == STATUS.PARTIAL) {
                return STATUS.PARTIAL;
            } else if (status == STATUS.INCOMPLETE ||
                       status == STATUS.ERROR) {
                return STATUS.INCOMPLETE;
            }
        }        
        return STATUS.COMPLETE;
    }

    /**
     * Get handout file
     * @return Returns null if file does not exist.
     */
    public File getSyllabusFile() {
        File file = new File(source,Config.COURSE_SYLLABUS_FILE);
        if (file.exists()) return file;
        return null;
    }
    
    /**
     * Get path safe name.
     */
    public String getURLSafeName() {
        String name = getFullname();
        return URLUtils.getURLSafeName(name);
    }

    /**
     * Determine if Course has assignment folders
     */
    public boolean hasAssignments() {
        List<Assignment> assignments = this.getAssignments();
        if (assignments.size() > 0) {
            return true;
        }
        return false;
    }
    
    /**
     * Determine if course has handout file
     */
    public boolean hasSyllabusFile() {
        File file = new File(source,Config.COURSE_SYLLABUS_FILE);
        return file.exists();
    }
    
    /**
     * Determine if the course has metadata file
     * @return 
     */
    public boolean hasMetadataFile() {
        File file = new File(source,Config.COURSE_METADATA_FILE);
        return file.exists();
    }
    
    /**
     * Parse the course metadata file and assign values to local variables.
     */
    private void parseDescriptionFile() {
        File file = new File(source,Config.COURSE_METADATA_FILE);
        if (file.exists()) {
            Map<String,String> vals = MarkupParser.parse(file);
            if (vals.containsKey("Description")) {
                description = vals.get("Description");
            }
            if (vals.containsKey("Format")) {
                format = vals.get("Format");
            }
            if (vals.containsKey("Instructors")) {
                String text = vals.get("Instructors");
                instructors = MarkupParser.getList(text, "\\*");
            }
            if (vals.containsKey("CACB Criteria")) {
                String text = vals.get("CACB Criteria");
                spc = MarkupParser.getList(text, "\\*");
            }
        }
    }
        
} // end class
