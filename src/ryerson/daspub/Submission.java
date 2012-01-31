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
import jxl.Cell;

/**
 * Student work submission
 * @author dmarques
 */
public class Submission {
    
    private File file;
    
    private String year;
    private String semester;
    private String coursenumber;
    private String coursename;
    private String instructor;
    private String assignmentname;
    private String assignmentduration;
    private String studentname;
    private String path;
    private String evaluation;
    private String tags;
    
    //--------------------------------------------------------------------------

    /**
     * Submission constructor
     * @param Year
     * @param Semester
     * @param CourseNumber
     * @param CourseName
     * @param Instructor
     * @param AssignmentName
     * @param AssignmentDuration
     * @param StudentName
     * @param Path Absolute path to file
     * @param Evaluation 
     * @param Tags 
     */
    public Submission(String Year, 
                      String Semester, 
                      String CourseNumber, 
                      String CourseName, 
                      String Instructor, 
                      String AssignmentName,
                      String AssignmentDuration, 
                      String StudentName, 
                      String Path, 
                      String Evaluation, 
                      String Tags)
    {
        year = Year;
        semester = Semester;
        coursenumber = CourseNumber;
        coursename = CourseName;
        instructor = Instructor;
        assignmentname = AssignmentName;
        assignmentduration = AssignmentDuration;
        studentname = StudentName;
        path = Path;
        evaluation = Evaluation;
        tags = Tags;
        
        file = new File(path);
    }
    
    //--------------------------------------------------------------------------

    /**
     * 
     * @return 
     */
    public String getAssignmentDuration() {
        return assignmentduration;
    }
    
    /**
     * 
     * @return 
     */
    public String getAssignmentName() {
        return assignmentname;
    }
    
    /**
     * Get submission author name
     * @return 
     */
    public String getAuthor() {
        return studentname;
    }
    
    /**
     * Get course name
     * @return 
     */
    public String getCourseName() {
        return coursename;
    }
    
    /**
     * Get course number
     * @return 
     */
    public String getCourseNumber() {
        return coursenumber;
    }

    /**
     * Get instructor evaluation
     * @return 
     */
    public String getEvaluation() {
        return evaluation;
    }

    /**
     * 
     */
    public File getFile() {
        return file;
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
     * Get instructor
     * @return 
     */
    public String getInstructor() {
        return instructor;
    }

    /**
     * Get submission semester
     */
    public String getSemester() {
        return semester;
    }
    
    /**
     * year, semester, course number, course name, instructor, assignment name, 
     * assignment duration, student name, file name, evaluation
     * @param Cells 
     * @param Path Parent folder of the submission 
     * @return 
     */
    public static Submission getSubmission(Cell[] Cells, File Path) {
        Submission s = null;
        // map cell values to new object
        String c_year = Cells[0].getContents();
        String c_semester = Cells[1].getContents();
        String c_coursenumber = Cells[2].getContents();
        String c_coursename = Cells[3].getContents();
        String c_instructor = Cells[4].getContents();
        String c_assignmentname = Cells[5].getContents();
        String c_assignmentduration = Cells[6].getContents();
        String c_studentname = Cells[7].getContents();
        String c_filename = Cells[8].getContents();
        String c_evaluation = Cells[9].getContents();
        String c_tags = "";
        if (Cells.length>10) {
            c_tags = Cells[10].getContents();
        }
        // if the required cells are not empty, create a new submission object
        if (c_year != null && 
            c_coursenumber != null && 
            c_instructor != null && 
            c_studentname != null && 
            c_filename != null) 
        {
            File f = new File(Path,c_filename);
            s = new Submission(c_year, c_semester, c_coursenumber, c_coursename, 
                               c_instructor, c_assignmentname, 
                               c_assignmentduration, c_studentname, 
                               f.getAbsolutePath(), c_evaluation, c_tags);
        }
        return s;
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
     * Get submission year.
     */
    public String getYear() {
        return year;
    }
    
    /**
     * Write thumbnail to folder
     * @param Folder Folder to write thumbnail to
     */
    public void writeThumbnail(File Folder) {
        // consider whether the document is a multi-page PDF or video
        // File thumb = new File(thumbs.getAbsolutePath(),files[i].getName());
        // ImageUtils.writeThumbnail(files[i], thumb);            
    }
    
} // end class
