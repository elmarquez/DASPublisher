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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.model.Archive;
import ryerson.daspub.Config;
import ryerson.daspub.model.Submission;

/**
 * Writes HTML status report for a content archive.
 * @author dmarques
 */
public class PublishReportTask implements Runnable {

    private final String htmlFileName = "index.html";
    private final String[] supportFiles = {"animatedcollapse.js",
                                           "styles.css"};

    private Config config;
    private File output;
    
    private static final Logger logger = Logger.getLogger(PublishReportTask.class.getName());

    //--------------------------------------------------------------------------

    /**
     * Report publisher 
     * @param Configuration Configuration
     */
    public PublishReportTask(Config Configuration) {
        config = Configuration;
        output = new File(Config.OUTPUT_REPORT_PATH);
    }

    //--------------------------------------------------------------------------

    /**
     * Run task.
     */
    @Override
    public void run() {
        logger.log(Level.INFO,"STARTING publish report task");
        // delete existing report file
        if (output.exists()) {
            output.delete();
        }
        output.mkdirs();
        // get report content
        StringBuilder content = new StringBuilder();
        List<Archive> archives = Archive.getArchives(Config.ARCHIVE_PATHS);
        Iterator<Archive> it = archives.iterator();
        while (it.hasNext()) {
            Archive archive = it.next();
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
        // get list of duplicate submissions
        List<Submission> duplicates = sc.getDuplicates();
        // place report data into html template, write output file
        String data = "";
        File file = null;
        try {
            // write html
            file = new File(output,htmlFileName);
            InputStream is = PublishReportTask.class.getResourceAsStream(htmlFileName);
            data = IOUtils.toString(is);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            String title = "Status Report - Generated " + dateFormat.format(date);
            
            data = data.replace("${title}", title);
            data = data.replace("${timestamp}", dateFormat.format(date));

            data = data.replace("${total.complete}", String.valueOf(complete));
            data = data.replace("${total.partial}", String.valueOf(partial));
            data = data.replace("${total.error}", String.valueOf(error));
            data = data.replace("${total.incomplete}", String.valueOf(incomplete));
            data = data.replace("${percent.complete}", String.valueOf(percent));
            data = data.replace("${total}", String.valueOf(total));
            
            data = data.replace("${content}", content.toString());

            if (duplicates.size() > 0) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("There are ");
                sb2.append(String.valueOf(duplicates.size()));
                sb2.append(" duplicate submission ids.");
                sb2.append("<table width='100%'>");
                Iterator<Submission> its = duplicates.iterator();
                while (its.hasNext()) {
                    Submission s = its.next();
                    sb2.append("<tr><td>");
                    sb2.append(s.getId());
                    sb2.append("</td><td>in</td><td>");
                    sb2.append(s.getSourceFile().getParent());
                    sb2.append("</td></tr>");
                }
                sb2.append("</table>");
                data = data.replace("${duplicates}", sb2.toString());
            } else {
                // data = data.replace("${duplicates}", "There are 0 duplicated submission IDs.");
                data = data.replace("${duplicates}", " ");
            }
            
            logger.log(Level.INFO,"Writing report file \"{0}\"",file.getAbsolutePath());
            FileUtils.write(file, data);
            
            // write support files
            // if the file is a binary file, we need to modify this code
            for (int i=0;i<supportFiles.length;i++) {
                file = new File(output,supportFiles[i]);
                is = PublishReportTask.class.getResourceAsStream(supportFiles[i]);
                data = IOUtils.toString(is);
                logger.log(Level.INFO,"Writing support file \"{0}\"",file.getAbsolutePath());
                FileUtils.write(file,data);                
            }
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE,"Could not create output file \"{0}\"\n\n{1}",new Object[]{file.getAbsolutePath(),stack});
            System.exit(-1);
        }                
        logger.log(Level.INFO,"DONE publish report task");
    }

} // end class
