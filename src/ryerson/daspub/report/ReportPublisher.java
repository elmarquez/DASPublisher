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

package ryerson.daspub.report;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import ryerson.daspub.Archive;
import ryerson.daspub.Config;
import ryerson.daspub.Course;
import ryerson.daspub.Program;

/**
 * Report generator.
 * @author dmarques
 */
public class ReportPublisher implements Runnable {

    private Config config;                          // configuration
    private String path;                            // output file path

    private final String htmlFileName = "report.html";
    private final String[] supportFiles = {"animatedcollapse.js","configuration.html","help.html","styles.css"};
    
    private static final Logger _logger = Logger.getLogger(ReportPublisher.class.getName());

    //--------------------------------------------------------------------------

    /**
     * Reporter constructor
     * @param Config Configuration
     */
    public ReportPublisher(Config Configuration, String Path) {
        config = Configuration;
        path = Path;
    }

    /**
     * 
     * @param Configuration
     * @param Dir 
     */
    public ReportPublisher(Config Configuration, File Dir) {
        config = Configuration;
        path = Dir.getAbsolutePath();
    }

    //--------------------------------------------------------------------------

    /**
     * Generate archive status report
     */
    public void run() {
        StringBuilder content = new StringBuilder();
        // process archives
        Iterator<Archive> archives = Archive.getArchives(Config.ARCHIVE_PATHS);
        while (archives.hasNext()) {
            Archive archive = archives.next();
            // archive block header
            content.append("\n\n<div class='archive'>");
            content.append("\n<h1>");
            content.append(path);
            content.append("</h1>");
            // process programs
            Iterator<Program> programs = archive.getPrograms();
            while (programs.hasNext()) {
                Program program = programs.next();
                // program block header
                content.append("\n\n<div class='program'>");
                content.append("\n<h1>");
                content.append(program.getName());
                content.append("</h1>");
                //  process courses
                Iterator<Course> courses = program.getCourses();
                while (courses.hasNext()) {
                    Course course = courses.next();
                    _logger.log(Level.INFO,"Adding report for course {0}",course.getPath());
                    content.append(course.getStatusReportHTML());
                }
                content.append("</div>");
            }
            content.append("</div>");
        }
        // delete existing report file
        File output = new File(path);
        if (output.exists()) {
            output.delete();
        }
        output.mkdirs();
        // place report data into html template, write output file
        String data = "";
        File file = null;
        try {
            // write html
            file = new File(output,htmlFileName);
            InputStream is = ReportPublisher.class.getResourceAsStream(htmlFileName);
            data = IOUtils.toString(is);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String title = "Status Report - Generated " + dateFormat.format(date);
            data = data.replace("${title}", title);
            data = data.replace("${content}", content.toString());
            _logger.log(Level.INFO,"Writing report file {0}",file.getAbsolutePath());
            FileUtils.write(file, data);
            // write support file
            // if the file is a binary file, we need to modify this code
            for (int i=0;i<supportFiles.length;i++) {
                file = new File(output,supportFiles[i]);
                is = ReportPublisher.class.getResourceAsStream(supportFiles[i]);
                data = IOUtils.toString(is);
                _logger.log(Level.INFO,"Writing support file {0}",file.getAbsolutePath());
                FileUtils.write(file, data);                
            }
        } catch (Exception ex) {
            _logger.log(Level.SEVERE,"Could not create output file {0}.\n\n{1}",new Object[]{file.getAbsolutePath(),ex});
            System.exit(-1);
        }
    }

} // end class
