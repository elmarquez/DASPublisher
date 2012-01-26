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
package ryerson.daspub.mobile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.Archive;
import ryerson.daspub.Assignment;
import ryerson.daspub.Config;
import ryerson.daspub.Course;
import ryerson.daspub.Program;
import ryerson.daspub.utility.ImageFileFilter;
import ryerson.daspub.utility.ImageUtils;
import ryerson.daspub.utility.NonThumbnailIndexFileFilter;
import ryerson.daspub.utility.ProcessableImageFileFilter;

/**
 * Mobile publication generator.
 * @author dmarques
 */
public class MobilePublisher implements Runnable {

    private Config config;                      // configuration
    private File output;                        // output publication directory
    
    private String index_template = "";         // course index page
    private String course_template = "";        // course page
    private String assignment_template = "";    // assignment page
    private String exam_template = "";          // exam page
    
    private static final Logger logger = Logger.getLogger(MobilePublisher.class.getName());

    //--------------------------------------------------------------------------
    
    /**
     * Publisher constructor
     * @param Config Configuration
     * @param Output Output directory
     * @throws Exception
     */
    public MobilePublisher(Config Config, File Output) throws Exception {
        config = Config;
        output = Output;
        loadTemplates();
    }

    //--------------------------------------------------------------------------
    
    /**
     * Clean the output directory.
     */
    private void cleanOutputDirectory() {
        try {
            if (output.exists()) {
                Path dir = output.toPath();
                Files.deleteIfExists(dir);
            }
            output.mkdirs();
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE, "Could not delete {0}. Will continue processing. Caught exception:\n\n{1}", 
                    new Object[]{output.getAbsolutePath(),stack});
        }
    }
    
    /**
     * Create a formatted HTML list from a list of String items.
     * @param Items
     * @return 
     */
    private String getFormattedList(List<String> Items) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = Items.iterator();
        while (it.hasNext()) {
            sb.append("<li>");
            sb.append(it.next());
            sb.append("</li>");
        }
        return sb.toString();
    }
    
    /**
     * Load template files into memory. I know its a stupidly redundant method.
     */
    private void loadTemplates() throws Exception {
        course_template = FileUtils.readFileToString(new File(Config.COURSE_TEMPLATE_PATH));
        assignment_template = FileUtils.readFileToString(new File(Config.ASSIGNMENT_TEMPLATE_PATH));
        exam_template = FileUtils.readFileToString(new File(Config.EXAM_TEMPLATE_PATH));
    }

    /**
     * Process an assignment folder
     * @param A
     * @param CourseFolder
     * @returns HTML HTML index of assignments
     * @throws Exception
     * @throws IOException
     */
    private String processAssignment(Assignment A, File CourseFolder) throws Exception, IOException {
        File assignmentFolder = new File(CourseFolder.getAbsolutePath(), A.getName());
        // create assignment index page
        String html = new String(assignment_template);
        html = html.replace("${assignment.title}", A.getName());
        html = html.replace("${assignment.description}", A.getDescription());
        html = html.replace("${assignment.description.pdf}", A.getAssignmentDescriptionPDF());
        html = html.replace("${assignment.worksamples}", A.getSubmissionIndex());
        File index = new File(assignmentFolder.getAbsolutePath(), "index.html");
        FileUtils.write(index, html);
        // copy media files
        File[] files = A.getPath().listFiles(new ImageFileFilter());
        for (int i=0;i<files.length;i++) {
            FileUtils.copyFile(files[i], new File(assignmentFolder,files[i].getName()));
        }        
        // generate image thumbnails
        File thumbs = new File(assignmentFolder,"thumb");
        if (!thumbs.exists()) {
            thumbs.mkdirs();
        }
        files = assignmentFolder.listFiles(new ProcessableImageFileFilter());
        for (int i=0;i<files.length;i++) {
            File thumb = new File(thumbs.getAbsolutePath(),files[i].getName());
            ImageUtils.writeThumbnail(files[i], thumb);
        }
        // return html
        return html;
    }

    /**
     * Generate HTML presentation for a course.
     * @param C Course
     * @param Output Output folder to write data to
     */
    private void processCourse(Course C, File Output) {
        logger.log(Level.INFO, "Processing course folder {0}", C.getFile().getAbsolutePath());
        try {
            // create course index page
            String html = new String(course_template);
            html = html.replace("${course.title}", C.getName());
            html = html.replace("${course.description}", C.getDescription());
            html = html.replace("${course.description.pdf}", C.getCourseDescriptionPDF());
            html = html.replace("${course.instructors}", getFormattedList(C.getInstructors()));
            html = html.replace("${course.cacb.criteria}", getFormattedList(C.getCACBCriteria()));
            // process assignment folders and add assignment list to course index page
            Iterator<Assignment> assignments = C.getAssignments();
            StringBuilder sb = new StringBuilder();
            while (assignments.hasNext()) {
                Assignment a = assignments.next();
                sb.append("<div class='assignment'>");
                String s = processAssignment(a,Output);
                sb.append(s);
                sb.append("</div>");
            }
            html = html.replace("${assignments}",sb.toString());
            // html = html.replace("${course.assignments}", getFormattedList(C.getAssignments()));
            // TODO revise formatted lists method for more flexibility
            html = html.replace("${course.exams}", getFormattedList(C.getExams()));
            File index = new File(Output.getAbsolutePath(), "index.html");
            FileUtils.write(index, html);
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE, "Could not copy course {0} to {1}. Caught exception:\n\n{2}", 
                    new Object[]{C.getFile().getAbsolutePath(), Output.getAbsolutePath(), stack});
        }
    }

    /**
     * Run the publisher.
     */
    public void run() {
        // clean the output directory
        cleanOutputDirectory();
        // copy static files to output directory
        try {
            File staticFiles = new File(Config.STATIC_FILES_PATH);
            FileUtils.copyDirectory(staticFiles,output,new NonThumbnailIndexFileFilter());
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE, "Could not copy static files from {0} to {1}. Will continue processing. Caught exception:\n\n{2}", 
                    new Object[]{Config.STATIC_FILES_PATH, output.getAbsolutePath(), stack});
            System.exit(-1);
        }
        // process the archives
        Iterator<Archive> archives = Archive.getArchives(Config.ARCHIVE_PATHS);
        Iterator<Program> programs = null;
        Iterator<Course> courses = null;
        Archive archive = null;
        Program program = null;
        Course course = null;
        File courseOutPath = null;
        File archiveOutPath = new File(output.getAbsolutePath(),"program");
        while (archives.hasNext()) {
            try {
                archive = archives.next();
                archiveOutPath = new File(archiveOutPath,archive.getPathSafeName());
                // process programs
                programs = archive.getPrograms();
                while (programs.hasNext()) {
                    // process courses
                    program = programs.next();
                    File programOutPath = new File(archiveOutPath,program.getPathSafeName());
                    // get course
                    courses = program.getCourses();
                    while (courses.hasNext()) {
                        course = courses.next();
                        // determine output path
                        courseOutPath = new File(programOutPath, course.getPathSafeName());
                        // process the course folder
                        processCourse(course,courseOutPath);
                    }
                }
                // write program summary
                // write archive summary
            } catch (Exception ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE, "Could not copy archive content from {0} to {1}. Caught exception:\n\n{2}", 
                        new Object[]{archive.getPath(), output.getAbsolutePath(), stack});
                System.exit(-1);
            }
        }
    }

} // end class
