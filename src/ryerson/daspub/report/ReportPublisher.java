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
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.model.Archive;
import ryerson.daspub.Config;

/**
 * Writes HTML status report for a content archive.
 * @author dmarques
 */
public class ReportPublisher implements Runnable {

    private final String htmlFileName = "index.html";
    private final String[] supportFiles = {"animatedcollapse.js",
                                           "styles.css"};

    private Config config;
    private String path;
    
    private static final Logger logger = Logger.getLogger(ReportPublisher.class.getName());

    //--------------------------------------------------------------------------

    /**
     * Report publisher 
     * @param Configuration Configuration
     * @param Output Output directory
     */
    public ReportPublisher(Config Configuration, File Output) {
        config = Configuration;
        path = Output.getAbsolutePath();
    }

    //--------------------------------------------------------------------------

    /**
     * Generate report.
     */
    public void run() {
        StringBuilder content = new StringBuilder();
        // get report content
        Iterator<Archive> archives = Archive.getArchives(Config.ARCHIVE_PATHS);
        while (archives.hasNext()) {
            Archive archive = archives.next();
            content.append(ArchiveReport.GetHTML(archive));
        }
        // get total number of complete, partial and incomplete items
        StatusCounter sc = new StatusCounter(Config.ARCHIVE_PATHS);
        sc.count();
        int complete = sc.getCompleteCourseCount();
        int partial = sc.getPartialCourseCount();
        int error = sc.getErrorCourseCount();
        int incomplete = sc.getIncompleteCourseCount();
        int total = sc.getTotalCourseCount();
        int percent = sc.getPercentageComplete();
        
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

            data = data.replace("${total.complete}", String.valueOf(complete));
            data = data.replace("${total.partial}", String.valueOf(partial));
            data = data.replace("${total.error}", String.valueOf(error));
            data = data.replace("${total.incomplete}", String.valueOf(incomplete));
            data = data.replace("${percent.complete}", String.valueOf(percent));
            
            logger.log(Level.INFO,"Writing report file {0}",file.getAbsolutePath());
            FileUtils.write(file, data);
            // write support files
            // if the file is a binary file, we need to modify this code
            for (int i=0;i<supportFiles.length;i++) {
                file = new File(output,supportFiles[i]);
                is = ReportPublisher.class.getResourceAsStream(supportFiles[i]);
                data = IOUtils.toString(is);
                logger.log(Level.INFO,"Writing support file {0}",file.getAbsolutePath());
                FileUtils.write(file,data);                
            }
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE,"Could not create output file {0}.\n\n{1}",new Object[]{file.getAbsolutePath(),stack});
            System.exit(-1);
        }
    }

} // end class
