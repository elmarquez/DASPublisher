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
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Cell;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;
import ryerson.daspub.Config;
import ryerson.daspub.utility.ImageUtils;
import ryerson.daspub.utility.URLUtils;

/**
 * Student work submission.
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
    private String evaluation;

    private File source;

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
        source = new File(Path);
        evaluation = Evaluation;
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
     * Get evaluation code.
     * @return
     */
    public Config.SUBMISSION_EVALUATION getEvaluation() {
        switch (evaluation.toLowerCase()) {
            case "low pass":
                return Config.SUBMISSION_EVALUATION.LOW_PASS;
            case "high pass":
                return Config.SUBMISSION_EVALUATION.HIGH_PASS;
        }
        return Config.SUBMISSION_EVALUATION.NONE;
    }

    /**
     * Get evaluation string.
     * @return 
     */
    public String getEvaluationString() {
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
        if (source.exists()) {
            name = ImageUtils.getJPGFileName(source.getName(),"jpg");
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
        return source;
    }

    /**
     * Get the submission file name
     * @return
     */
    public String getSourceFileName() {
        return source.getName();
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
     * Get submission type designation.
     * @return
     */
    public Config.SUBMISSION_TYPE getType() {
        if (isImage()) {
            return Config.SUBMISSION_TYPE.IMAGE;
        } else if (isPDF()) {
            return Config.SUBMISSION_TYPE.PDF;
        } else if (isVideo()) {
            return Config.SUBMISSION_TYPE.VIDEO;
        }
        return Config.SUBMISSION_TYPE.OTHER;
    }

    /**
     * Get path safe name.
     * @return Empty string if the source file does not exist.
     */
    public String getURLSafeName() {
        if (source.exists()) {
            String name = source.getName();
            return URLUtils.getURLSafeName(name);
        }
        return "";
    }

    /**
     * Get submission year.
     */
    public String getYear() {
        return year;
    }

    /**
     * Determine if the source file has been defined and the source file exists.
     */
    public boolean hasSourceFile() {
        if (source != null && source.exists()) {
            return true;
        }
        return false;
    }

    /**
     * Determine if submission is an image file.
     * @return True if submission is an image, false otherwise.
     */
    public boolean isImage() {
        String ext = FilenameUtils.getExtension(source.getName());
        for (int i=0;i<Config.IMAGE_TYPE.length;i++) {
            if (ext.equals(Config.IMAGE_TYPE[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if submission is a multi-page PDF file.
     * @return True if submission is a multi-page PDF, false otherwise.
     */
    public boolean isMultiPagePDF() {
        if (isPDF()) {
            PdfDecoder pdf = new PdfDecoder(true);
            try {
                pdf.openPdfFile(source.getAbsolutePath());
                int count = pdf.getPageCount();
                if (count > 1) {
                    return true;
                }
            } catch (PdfException ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE,"Could not read PDF file {0}.\n\n{1}",
                        new Object[]{source.getAbsolutePath(),stack});
            }
        }
        return false;
    }

    /**
     * Determine if submission is a PDF file.
     * @return True if submission is a PDF document, false otherwise.
     */
    public boolean isPDF() {
        SuffixFileFilter sff = new SuffixFileFilter("pdf");
        if (sff.accept(source)) {
            return true;
        }
        return false;
    }

    /**
     * Determine if submission is a single page PDF.
     * @return True if submission is a single page PDF, false otherwise.
     */
    public boolean isSinglePagePDF() {
        if (isPDF()) {
            PdfDecoder pdf = new PdfDecoder(true);
            try {
                pdf.openPdfFile(source.getAbsolutePath());
                int count = pdf.getPageCount();
                if (count == 1) {
                    return true;
                }
            } catch (PdfException ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE,"Could not read PDF file {0}.\n\n{1}",
                        new Object[]{source.getAbsolutePath(),stack});
            }
        }
        return false;
    }

    /**
     * Determine if submission is a video file.
     * @return True if submission is a video, false otherwise.
     */
    public boolean isVideo() {
        String ext = FilenameUtils.getExtension(source.getName());
        for (int i=0;i<Config.VIDEO_TYPE.length;i++) {
            if (ext.equals(Config.VIDEO_TYPE[i])) {
                return true;
            }
        }
        return false;
    }

} // end class
