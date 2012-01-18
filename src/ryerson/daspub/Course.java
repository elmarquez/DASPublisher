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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import ryerson.daspub.utility.FolderFileFilter;

/**
 * Course entity. Lazy loads data from the file system.
 * @author dmarques
 */
public class Course {

    private String path;
    private static final Logger _logger = Logger.getLogger(Course.class.getName());

    //--------------------------------------------------------------------------
    
    /**
     * Course constructor
     * @param D Directory
     */
    public Course(String Path) {
        path = Path;
    }

    //--------------------------------------------------------------------------
    
    /**
     * Get list of assignments
     */
    public List<Assignment> getAssignments() {
        File dir = new File(path);
        ArrayList<Assignment> items = new ArrayList<>();
        File[] files = dir.listFiles(new FolderFileFilter());
        for (int i = 0; i < files.length; i++) {
            Assignment a = new Assignment(files[i]);
            items.add(a);
        }
        return items;
    }

    /**
     * Get list of CACB criteria addressed by this course.
     * @return List of criteria
     */
    public List<String> getCACBCriteria() throws Exception {
        String text[] = parseDescriptionFile();
        ArrayList<String> items = new ArrayList<>();
        String[] subitems = text[2].split("-");
        for (int i = 0; i < subitems.length; i++) {
            items.add(subitems[i].trim());
        }
        return items;
    }

    /**
     * Get link to course description PDF 
     * @return 
     */
    public String getCourseDescriptionPDF() {
        File dir = new File(path);
        File file = new File(dir.getAbsolutePath(), Config.COURSE_DESCRIPTION_PDF_FILE);
        if (file.exists()) {
            return Config.COURSE_DESCRIPTION_PDF_FILE;
        } else {
            return "";
        }
    }

    /**
     * Get course description
     * @return
     */
    public String getDescription() throws Exception {
        String text[] = parseDescriptionFile();
        return text[0];
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
     * Get course folder file
     * @return 
     */
    public File getFile() {
        return new File(path);
    }

    /**
     * Get list of instructors
     * @return 
     */
    public List<String> getInstructors() throws Exception {
        String text[] = parseDescriptionFile();
        ArrayList<String> items = new ArrayList<>();
        String[] subitems = text[1].split("-");
        for (int i = 0; i < subitems.length; i++) {
            items.add(subitems[i].trim());
        }
        return items;
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
     * Get status report HTML
     */
    public String getStatusReportHTML() {
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
                sb.append(a.getStatusReportHTML());
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
     * The content is raw and unprocessed.
     * 0 - Description
     * 1 - Hours
     * 2 - Instructors
     * 3 - CACB Criteria
     * @return Array of content values
     */
    private String[] parseDescriptionFile() throws Exception {
        String[] vals = {"", "", "", ""};
        File dir = new File(path);
        File file = new File(dir.getAbsolutePath(), Config.COURSE_DESCRIPTION_TEXT_FILE);
        if (file.exists()) {
            String text = FileUtils.readFileToString(file);
            text = text.replace("\"", "&quot;");
            String[] blocks = text.split("==");
            String line = "";
            int count = 0;
            for (int i = 0; i < blocks.length && count < vals.length; i++) {
                line = blocks[i];
                if (!line.equals("")) {
                    if (line.startsWith("\r\n")) {
                        line = line.replace("\r\n", " ");
                        line = line.trim();
                        vals[count] = line;
                        count++;
                    }
                }
            }
        }
        return vals;
    }
    
} // end class
