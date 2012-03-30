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
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Cell;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.sanselan.ImageReadException;
import org.jpedal.exception.PdfException;
import ryerson.daspub.Config;
import ryerson.daspub.utility.ImageUtils;
import ryerson.daspub.utility.PDFUtils;

/**
 * Student work submission
 * @author dmarques
 */
public class Submission {
    
    private String year;
    private String semester;
    private String courseNumber;
    private String courseName;
    private String studioMaster;
    private String instructor;
    private String assignmentName;
    private String assignmentDuration;
    private String studentName;
    private String numberOfItems;
    private String submissionId;
    private String path;
    private String evaluation;
    
    private File file;

    private static final Logger logger = Logger.getLogger(Submission.class.getName());

    //--------------------------------------------------------------------------

    /**
     * Submission constructor
     * @param Year
     * @param Semester
     * @param CourseNumber
     * @param CourseName
     * @param StudioMaster
     * @param Instructor
     * @param AssignmentName
     * @param AssignmentDuration
     * @param StudentName
     * @param NumberOfItems
     * @param SubmissionId
     * @param Path Absolute path to file
     * @param Evaluation 
     */
    public Submission(String Year, 
                      String Semester, 
                      String CourseNumber, 
                      String CourseName, 
                      String StudioMaster,
                      String Instructor, 
                      String AssignmentName,
                      String AssignmentDuration,
                      String StudentName,
                      String NumberOfItems, 
                      String SubmissionId,
                      String Path,
                      String Evaluation)
    {
        year = Year;
        semester = Semester;
        courseNumber = CourseNumber;
        courseName = CourseName;
        studioMaster = StudioMaster;
        instructor = Instructor;
        assignmentName = AssignmentName;
        assignmentDuration = AssignmentDuration;
        studentName = StudentName;
        numberOfItems = NumberOfItems;
        submissionId = SubmissionId;
        path = Path;
        evaluation = Evaluation;
        
        file = new File(path);
    }
    
    //--------------------------------------------------------------------------

    /**
     * Get the assignment duration
     * @return 
     */
    public String getAssignmentDuration() {
        return assignmentDuration;
    }
    
    /**
     * Get the assignment name
     * @return 
     */
    public String getAssignmentName() {
        return assignmentName;
    }
    
    /**
     * Get submission author name
     * @return 
     */
    public String getAuthor() {
        return studentName;
    }
    
    /**
     * Get course name
     * @return 
     */
    public String getCourseName() {
        return courseName;
    }
    
    /**
     * Get course number
     * @return 
     */
    public String getCourseNumber() {
        return courseNumber;
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
     * Get number of items in submission
     * @return 
     */
    public String getNumberOfItems() {
        return numberOfItems;
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
     * Get student name
     * @return 
     */
    public String getStudentName() {
        return studentName;
    }
    
    /**
     * Get studio master
     * @return 
     */
    public String getStudioMaster() {
        return studioMaster;
    }
    
    /**
     * Parse spreadsheet row to create a Submission object.  If the row is 
     * missing required data fields, then a null object will be returned.
     * @param Cells 
     * @param Path Folder where the submission data object is located
     * @return Submission
     * @todo Consider a flexible cell to parameter mapping, using column names instead
     */
    public static Submission getSubmission(Cell[] Cells, File Path) {
        Submission s = null;
        // map cell values to new submission object
        String c_year = Cells[0].getContents();
        String c_semester = Cells[1].getContents();
        String c_coursenumber = Cells[2].getContents();
        String c_coursename = Cells[3].getContents();
        String c_studiomaster = Cells[4].getContents();
        String c_instructor = Cells[5].getContents();
        String c_assignmentname = Cells[6].getContents();
        String c_assignmentduration = Cells[7].getContents();
        String c_studentname = Cells[8].getContents();
        String c_numberofitems = Cells[9].getContents();
        String c_id = Cells[10].getContents();        
        String c_filename = Cells[11].getContents();
        String c_evaluation = Cells[12].getContents();
        // if the required cells are not empty, create a new submission object
        if (c_id != null &&
            c_year != null && 
            c_coursenumber != null && 
            c_instructor != null && 
            c_studentname != null && 
            c_filename != null) 
        {
            File f = new File(Path,c_filename);
            s = new Submission(c_year, 
                                c_semester, 
                                c_coursenumber, 
                                c_coursename, 
                                c_studiomaster, 
                                c_instructor, 
                                c_assignmentname, 
                                c_assignmentduration, 
                                c_studentname, 
                                c_numberofitems, 
                                c_id, 
                                f.getAbsolutePath(), 
                                c_evaluation);
        } else {
            logger.log(Level.WARNING,"Spreadsheet item is missing one of the required values: id, year, course number, instructor, student name, file name.");
        }
        return s;
    }
    
    /**
     * Get submission ID.
     * @return 
     */
    public String getSubmissionId() {
        return submissionId;
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

    public boolean isImage() {
        return false;
    }
    
    public boolean isMultiPagePDF() {
        return false;
    }
    
    public boolean isSinglePagePDF() {
        return false;
    }
    
    public boolean isVideo() {
        return false;
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
