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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.utility.FolderFileFilter;
import ryerson.daspub.utility.PDFFileFilter;

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
     * Create a formatted HTML list from a list of String items.
     * @param Items
     * @return 
     */
    private static String getHTMLFormattedList(List<String> Items) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<ul>");
        Iterator<String> it = Items.iterator();
        while (it.hasNext()) {
            sb.append("\n\t<li>");
            sb.append(it.next());
            sb.append("</li>");
        }
        sb.append("\n</ul>\n");
        return sb.toString();
    }
    
    /**
     *
     * @return
     */
    private String getHTMLPublicationStatus() {
        return "incomplete";
    }

    /**
     * Get HTML anchor safe identifier
     * @return
     */
    private String getHTMLSafeID() {
        File dir = new File(path);
        String name = dir.getName();
        return name.replace(" ", "_");
    }

    /**
     * Get status report HTML
     * TODO make static
     */
    public String getHTMLStatusReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<script type='text/javascript'>");
        sb.append("animatedcollapse.addDiv('");
        sb.append(getHTMLSafeID());
        sb.append("','fade=1')");
        sb.append("</script>");
        sb.append("\n<div class='course'>");
        // item title
        sb.append("\n\t<div class='title ");
        sb.append(getHTMLPublicationStatus());
        sb.append("'>");
        sb.append("\n\t\t<h1>");
        sb.append(getName());
        sb.append("</h1>");
        sb.append("\n\t\t<a href=\"javascript:animatedcollapse.toggle('");
        sb.append(getHTMLSafeID());
        sb.append("\')\">+</a>");
        sb.append("\n\t</div>");
        // toggle wrapper
        sb.append("\n\t<div id='");
        sb.append(getHTMLSafeID());
        sb.append("' style='display:none;'>");
        // description and metadata files
        sb.append("\n\t\t<div class='metadata'>");
        sb.append("\n\t\t\t<ul class='marked'>");
        sb.append("\n\t\t\t\t<li class='checkmark'>Course description file (course.txt)</li>");
        sb.append("\n\t\t\t\t<li class='cross'>Course description PDF file (course.pdf)</li>");
        sb.append("\n\t\t\t</ul>");
        sb.append("\n\t\t</div>");
        // file reports
        File dir = new File(path);
        File[] dirs = dir.listFiles(new FolderFileFilter());
        if (dirs.length > 0) {
            Arrays.sort(dirs, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return f1.getName().compareTo(f2.getName());
                }
            });
            for (int i = 0; i < dirs.length; i++) {
                Assignment a = new Assignment(dirs[i]);
                sb.append(a.getHTMLStatusReport());
            }
        } else {
            sb.append("\n\t\t<div class='assignment'>");
            sb.append("\n\t\t\t<ul class='marked'>");
            sb.append("\n\t\t\t\t<li class='cross'>No assignments have been provided for this course.</li>");
            sb.append("\n\t\t\t</ul>");
            sb.append("\n\t\t</div>");
        }
        // end
        sb.append("\n\t</div>");
        sb.append("\n</div><!-- /course -->");
        return sb.toString();
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
    
    /**
     * Write course data and HTML index file to specified output folder
     * @param Output Output folder
     */
    public void writeHTML(File Output) {
        logger.log(Level.INFO, "Writing course folder {0}", path);
        try {
            // create the output folder
            Output.mkdirs();
            // copy course metadata, etc. files to output
            File sourcePath = this.getFile();
            File[] files = sourcePath.listFiles(new PDFFileFilter());
            for (int i=0;i<files.length;i++) {
                FileUtils.copyFile(files[i], new File(Output,files[i].getName()));
            }
            // load index page template file
            String template = FileUtils.readFileToString(new File(Config.COURSE_TEMPLATE_PATH));
            // build index page
            template = template.replace("${course.title}", this.getName());
            template = template.replace("${course.description}", description);
            template = template.replace("${course.format}", format);
            template = template.replace("${course.syllabus}", this.getSyllabusLink());
            template = template.replace("${course.instructors}", getHTMLFormattedList(instructors));
            template = template.replace("${course.cacb.criteria}", getHTMLFormattedList(cacbcriteria));
            // build assignment index
            Iterator<Assignment> assignments = this.getAssignments();
            StringBuilder sb = new StringBuilder();
            sb.append("<ul data-role=\"listview\" data-inset=\"true\" data-theme=\"c\">");
            File assignmentOutputPath = null;
            while (assignments.hasNext()) {
                Assignment a = assignments.next();
                // add assignment to index
                sb.append("\n\t<li><a href=\"");
                sb.append(a.getPathSafeName());
                sb.append("\">");
                sb.append(a.getName());
                sb.append("</a></li>");
                // process assignment output
                assignmentOutputPath = new File(Output,a.getPathSafeName());
                Assignment.WriteHTML(a,assignmentOutputPath);
            }
            sb.append("\n</ul>\n");
            template = template.replace("${course.assignments}",sb.toString());
            // build exam list
            // TODO revise formatted lists method for more flexibility
            template = template.replace("${course.exams}", getHTMLFormattedList(this.getExams()));
            // write index page
            File index = new File(Output.getAbsolutePath(), "index.html");
            FileUtils.write(index, template);
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE, "Could not copy course {0} to {1}\n\n{2}", 
                    new Object[]{path,Output.getAbsolutePath(),stack});
        }
    }
    
} // end class
