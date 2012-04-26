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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.Config;
import ryerson.daspub.model.Assignment;
import ryerson.daspub.model.Course;
import ryerson.daspub.utility.MarkupUtils;
import ryerson.daspub.utility.PDFUtils;

/**
 * Course page.
 * @author dmarques
 */
public class CoursePage {
 
    private static final Logger logger = Logger.getLogger(CoursePage.class.getName());

    //--------------------------------------------------------------------------
    
    /**
     * HTML thumbnail index of course handout
     * @param C Course
     * @param Output Output folder
     * @return PhotoSwipe gallery index
     */
    public static String buildHandoutIndex(Course C, File Output) {
        StringBuilder sb = new StringBuilder();
        if (C.hasSyllabusFile()) {
            // javascript for gallery
            sb.append("\n<script type=\"text/javascript\">");
            sb.append("\n\t$(document).ready(function(){");
            sb.append("\n\t\tvar myPhotoSwipe = $(\"#syllabus a\").photoSwipe({ enableMouseWheel: false , enableKeyboard: false });");
            sb.append("\n\t\t});");
            sb.append("\n</script>");
            // gallery
            File thumb = new File(Output,"syllabus-thumb.jpg");
            File full = new File(Output,"syllabus.jpg");
            try {
                List<File> thumbs;
                List<File> images;
                thumbs = PDFUtils.writeJPGImageAllPDFPages(C.getSyllabusFile(),
                                                thumb,
                                                Config.THUMB_MAX_WIDTH,
                                                Config.THUMB_MAX_HEIGHT);
                images = PDFUtils.writeJPGImageAllPDFPages(C.getSyllabusFile(),
                                                full,
                                                Config.IMAGE_MAX_WIDTH,
                                                Config.IMAGE_MAX_HEIGHT);
                // for each page of the syllabus, write out an index thumbnail
                sb.append("\n<ul id=\"syllabus\" class=\"gallery\">");
                for (int i=0;i<images.size();i++) {
                    File f = images.get(i);
                    File t = thumbs.get(i);
                    sb.append("\n\t<li>");
                    sb.append("<a href=\"");
                    sb.append(f.getName());
                    sb.append("\" rel=\"external\"><img src=\"");
                    sb.append(t.getName());
                    sb.append("\" alt=\"");
                    sb.append("Page ");
                    sb.append(i + 1);
                    sb.append(" of ");
                    sb.append(images.size());
                    sb.append("\" /></a></li>");
                }
                sb.append("\n</ul>");            
            } catch (Exception ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE, "Could not write handout gallery for {0}\n\n{1}", 
                        new Object[]{C.getFolder().getAbsolutePath(),stack});
            }
        }
        // return result
        return sb.toString();
    }    
    
    /**
     * Write course data and HTML index file to specified output folder
     * @param Output Output folder
     */
    public static void Write(Course C, File Output) {
        logger.log(Level.INFO, "Writing course folder {0}", C.getFolder().getAbsolutePath());
        try {
            // create the output folder
            Output.mkdirs();
            // load index page template file
            String page = FileUtils.readFileToString(new File(Config.COURSE_TEMPLATE_PATH));
            // build index page
            String title = C.getCourseCode() + " - " + C.getName();
            page = page.replace("${title}", title);
            page = page.replace("${description}", C.getDescription());
            page = page.replace("${format}", C.getFormat());
            if (C.hasSyllabusFile()) {
                page = page.replace("${syllabus}", buildHandoutIndex(C,Output));
            } else {
                page = page.replace("${syllabus}", "\n<p>Course syllabus not available.</p>");
            }
            page = page.replace("${instructors}", MarkupUtils.getHTMLUnorderedList(C.getInstructors()));
            page = page.replace("${spc}", MarkupUtils.getHTMLUnorderedList(C.getSPCFulfilled()));
            // build assignment index
            List<Assignment> la = C.getAssignments();
            Iterator<Assignment> assignments = la.iterator();
            StringBuilder sb = new StringBuilder();
            sb.append("\n<ul data-role=\"listview\" data-inset=\"true\" data-theme=\"c\">");
            File assignmentOutputPath = null;
            while (assignments.hasNext()) {
                Assignment a = assignments.next();
                // add assignment to index
                sb.append("\n\t<li><a href=\"");
                sb.append(a.getURLSafeName());
                sb.append("\" data-transition=\"fade\">");
                sb.append(a.getName());
                sb.append("</a></li>");
                // process assignment output
                assignmentOutputPath = new File(Output,a.getURLSafeName());
                AssignmentPage.Write(a,assignmentOutputPath);
            }
            sb.append("\n</ul>\n");
            page = page.replace("${assignments}",sb.toString());
            // write index page
            File index = new File(Output.getAbsolutePath(), "index.html");
            FileUtils.write(index, page);
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE, "Could not copy course {0} to {1}\n\n{2}", 
                    new Object[]{C.getFolder().getAbsolutePath(),
                                 Output.getAbsolutePath(),
                                 stack});
        }
    }
    
} // end class
