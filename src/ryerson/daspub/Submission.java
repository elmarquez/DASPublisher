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
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Cell;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.sanselan.ImageReadException;
import org.jpedal.exception.PdfException;
import ryerson.daspub.utility.ImageUtils;
import ryerson.daspub.utility.PDFUtils;

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

    private boolean isMultiPagePDF = false;
    
    private static final Logger logger = Logger.getLogger(Submission.class.getName());

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
     * Get the assignment duration
     * @return 
     */
    public String getAssignmentDuration() {
        return assignmentduration;
    }
    
    /**
     * Get the assignment name
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
     * Get instructor name
     * @return 
     */
    public String getInstructor() {
        return instructor;
    }

    /**
     * Get the submission output file name
     * @return 
     */
    public String getOutputFileName() {
        String name = "";
        if (file.exists()) {
            name = ImageUtils.getJPGFileName(file.getName(),"jpg");
        }
        return name;
    }
    
    /**
     * Get submission semester
     */
    public String getSemester() {
        return semester;
    }
    
    /**
     * 
     */
    public File getSourceFile() {
        return file;
    }
    
    /**
     * Get the submission file name
     * @return 
     */
    public String getSourceFileName() {
        File f = new File(path);
        return f.getName();
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
     * Get thumbnail file name.
     * @return 
     */
    public String getThumbnailFileName() {
        String name = "";
        if (file.exists()) {
            name = ImageUtils.getJPGFileName(file.getName(),"jpg");
        }
        return name;
    }
    
    /**
     * Get submission year.
     */
    public String getYear() {
        return year;
    }

    /**
     * Write a JPG image of the file.
     * @param Output Output file
     */
    public void writeImage(File Output) {
        logger.log(Level.INFO,"Writing full size image for {0}",file.getName());
        if (!Output.exists()) {
            Output.mkdirs();
        }
        try {
            File image = new File(path);
            File imageOutput = new File(Output,getThumbnailFileName());
            if (FilenameUtils.isExtension(image.getName(),"pdf")) {
                PDFUtils.writeJPGImage(image,imageOutput,Config.IMAGE_MAX_WIDTH,Config.IMAGE_MAX_HEIGHT,true);
            } else {
                ImageUtils.writeJPGImage(image,imageOutput,Config.IMAGE_MAX_WIDTH,Config.IMAGE_MAX_HEIGHT);
            }
        } catch (IOException | PdfException | ImageReadException ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE,"Could not write thumbnail {0}\n\n{1}",
                    new Object[]{Output.getAbsolutePath(),stack});
        }
    }

    /**
     * Write thumbnail to file.
     * @param Output Output file
     */
    public void writeThumbnail(File Output) {
        logger.log(Level.INFO,"Writing thumbnail image for {0}",file.getName());
        if (!Output.exists()) {
            Output.mkdirs();
        }
        File image = new File(path);
        try {
            String filename = ImageUtils.getJPGFileName(image.getName(),"jpg");
            File imageOutput = new File(Output,filename);
            if (FilenameUtils.isExtension(image.getName(),"pdf")) {
                PDFUtils.writeJPGImage(image,imageOutput,Config.THUMB_MAX_WIDTH,Config.THUMB_MAX_HEIGHT,false);
            } else {
                ImageUtils.writeJPGImage(image,imageOutput,Config.THUMB_MAX_WIDTH,Config.THUMB_MAX_HEIGHT);
            }
        } catch (IOException | PdfException | ImageReadException ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE,"Could not write thumbnail {0}\n\n{1}",
                    new Object[]{Output.getAbsolutePath(),stack});
        }
    }
    
} // end class
