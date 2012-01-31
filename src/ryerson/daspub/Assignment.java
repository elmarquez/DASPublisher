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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import org.apache.commons.io.FileUtils;
import ryerson.daspub.utility.AssignmentDescriptionTextFileFilter;
import ryerson.daspub.utility.AssignmentMetadataFileFilter;
import ryerson.daspub.utility.ImageFileFilter;
import ryerson.daspub.utility.MetadataFileFilter;
import ryerson.daspub.utility.NonImageFileFilter;

/**
 * Assignment entity. Lazy loads data from the file system.
 * @author dmarques
 */
public class Assignment {

    public static enum STATUS {COMPLETE, INCOMPLETE, ERROR};

    private File path;

    private static final Logger logger = Logger.getLogger(Assignment.class.getName());
    
    //--------------------------------------------------------------------------

    /**
     * Assignment constructor
     * @param D Directory
     */
    public Assignment(File D) {
        path = D;
    }

    /**
     * Assignment constructor
     * @param Path 
     */
    public Assignment(String Path) {
        path = new File(Path);
    }

    //--------------------------------------------------------------------------

    /**
     * Get link to assignment description PDF
     * @return 
     */
    public String getAssignmentDescriptionPDF() {
        File file = new File(path.getAbsolutePath(),Config.ASSIGNMENT_DESCRIPTION_PDF_FILE);
        if (file.exists()) {
            return Config.ASSIGNMENT_DESCRIPTION_PDF_FILE;
        } else {
            return "";
        }
     }
    
    /**
     * Get assignment description. If no description is available, an empty
     * string is returned.
     * @return Assignment description
     * @throws Exception
     */
    public String getDescription() {
        try {
            String text[] = parseDescriptionFile();
            return text[0];
        } catch (Exception ex) {
            logger.log(Level.SEVERE,"Could not parse description. Will continue processing. Caught exception:\n\n{0}",ex);
            return "";
        }
    }

    /**
     * Get HTML anchor safe identifier
     * @return
     */
    private String getHTMLSafeID() {
        String name = path.getName();
        return name.replace(" ", "_");
    }
    
    /**
     * Get submission metadata file.
     * @return Metadata file. Null if no file is found.
     */
    private File getMetadataFile() {
        File[] files = path.listFiles(new MetadataFileFilter());
        File file = null;
        if (files.length>0) {
            file = files[0];
        }
        return file;
    }

    /**
     * Get assignment name from folder name.
     * @return Assignment name
     */
    public String getName() {
        return path.getName();
    }

    /**
     * Get list of non-processable files.
     * @return List of files
     */
    private File[] getNonProcessableFileList() {
        return path.listFiles(new NonImageFileFilter());
    }

    /**
     * Get path
     * @return 
     */
    public File getPath() {
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
     * Get list of processable files.
     * @return List of files
     */
    private File[] getProcessableFileList() {
        return path.listFiles(new ImageFileFilter());
    }

    /**
     * Get publication status
     * @return
     */
    public String getPublicationStatus() {
        // if description
        // if files.xls
        // if files.xls is complete or partially complete
        return "";
    }

    /**
     * Get status report HTML
     * @return
     */
    public String getStatusReportHTML() {
        StringBuilder sb = new StringBuilder();
        // if the folder is 100%, show green
        // if it has the required description/metadata/student work files, show yellow
        // if it is missing all files, show red
        sb.append("\n\t<div class='assignment ");
        sb.append(getPublicationStatus());
        sb.append("'>");
        // item title
        sb.append("\n\t\t<h1>");
        sb.append(getName());
        sb.append("</h1>");
        // description and metadata files
        sb.append("\n\t\t<ul class='marked'>");
        if (hasDescriptionFile()) {
            sb.append("\n\t\t\t<li class='checked'>Has assignment_description.txt file</li>");
        } else {
            sb.append("\n\t\t\t<li class='crossed'>Does not have assignment_description.txt file</li>");
        }
        if (hasMetadataFile()) {
            sb.append("\n\t\t\t<li class='checked'>Has files.xls file</li>");
        } else {
            sb.append("\n\t\t\t<li class='crossed'>Does not have files.xls file</li>");
        }
        sb.append("\n\t\t</ul>");
        // student work files
        sb.append("\n\t\t<div class='files'>");
        File[] files = path.listFiles();
        int count = files.length;
        sb.append("\n\t\t\t<p>There are ");
        sb.append(count);
        sb.append(" files in the assignment folder.");
        sb.append("</p>");
        sb.append("\n\t\t\t<ul>");
        files = path.listFiles(new NonImageFileFilter());
        for (int i=0;i<files.length;i++) {
            // if description or metadata file then pass
            sb.append("\n\t\t\t\t<li>");
            sb.append(files[i].getName());
            sb.append("</li>");
        }
        sb.append("\n\t\t\t</ul>");
        sb.append("\n\t\t</div>");
        // return results
        sb.append("\n\t</div><!-- /assignment -->");
        return sb.toString();
    }

    /**
     * Get list of files.
     * @return List of image files
     */
    public Iterator<Submission> getSubmissions() {
        ArrayList<Submission> items = new ArrayList<>();
        // load the metadata file
        File file = new File(this.path,Config.ASSIGNMENT_FILE_METADATA);
        if (file.exists()) {            
            try {
                WorkbookSettings ws = new WorkbookSettings();
                ws.setLocale(new Locale("en","EN"));
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = Workbook.getWorkbook(fis,ws);
                Sheet sheet = workbook.getSheet(0);
                int rows = sheet.getRows();
                // Cell[] headCells = sheet.getRow(0);
                for (int row=1;row<rows-1;row++) {
                    Cell[] cells = sheet.getRow(row);
                    Submission s = Submission.getSubmission(cells,path);
                    if (s != null) {
                        items.add(s);
                    }
                }            
            } catch (IOException | BiffException | IndexOutOfBoundsException ex) {
                logger.log(Level.SEVERE,"Metadata for assignment {0} could not be loaded. Caught exception:\n\n{1}",
                        new Object[]{path.getAbsolutePath(),ex});
            }
        }
        return items.iterator();
    }

    /**
     * A map of fullsize, thumbnail file paths?
     * @return 
     */
    public String getSubmissionIndex() {
        StringBuilder sb = new StringBuilder();
        Iterator<Submission> submissions = getSubmissions();
        sb.append("\n<ul id='Gallery' class='gallery'>");
        while (submissions.hasNext()) {
            Submission sub = submissions.next();
            sb.append("\n\t<li>");
            sb.append("<a href='");
            sb.append(sub.getFileName());
            sb.append("' rel='external'>");
            sb.append("<img src='");
            sb.append(sub.getFileName());
            sb.append("' alt='");
            sb.append("' />");
            String author = sub.getAuthor();
            sb.append(sub);// TODO get the description, etc. from the spreadsheet file
            sb.append("Description of the work, authors, date, instructors");
            sb.append("</a>");
            sb.append("</li>");
        }
        sb.append("\n</ul>");
        return sb.toString();
    }

    /**
     * Determine if assignment has a description file.
     * @return True if file exists, false otherwise.
     */
    private boolean hasDescriptionFile() {
        File[] files = path.listFiles(new AssignmentDescriptionTextFileFilter());
        if (files.length > 0) return true;
        return false;
    }

    /**
     * Determine if assignment has a metadata file.
     * @return True if file exists, false otherwise.
     */
    private boolean hasMetadataFile() {
        File[] files = path.listFiles(new AssignmentMetadataFileFilter());
        if (files.length > 0) return true;
        return false;
    }

    /**
     * Determine if the file metadata is complete
     * @return
     */
    private boolean isCompletedMetadata() {
        return false;
    }

    /**
     * The content is raw and unprocessed.
     * 0 - Description
     * 1 - 
     * @return Array of content values
     */
    private String[] parseDescriptionFile() throws Exception {
       String[] vals = {"", "", "", ""};
       File file = new File(path.getAbsolutePath(),Config.ASSIGNMENT_DESCRIPTION_TEXT_FILE);
        if (file.exists()) {
           String text = FileUtils.readFileToString(file);
           text = text.replace("\"", "&quot;");
           String[] blocks = text.split("==");
           String line = "";
           int count = 0;
           for (int i=0;i<blocks.length && count<vals.length;i++) {
               line = blocks[i];
               if (!line.equals("")) {
                   if (line.startsWith("\r\n")) {
                       line = line.replace("\r\n"," ");
                       line = line.trim();
                       vals[count] = line;
                       count++;
                   }
               }
           }
        } 
        return vals;
    }

    /**
     * 
     * @param A Assignment
     * @param Output Output folder
     * @return
     */
    public static String WriteHTML(Assignment A, File Output) {
        logger.log(Level.INFO, "Writing assignment folder {0}", A.getPath().getAbsolutePath());
        // create the output folder        
        Output.mkdirs();
        try {
            // load index page template file
            String template = FileUtils.readFileToString(new File(Config.ASSIGNMENT_TEMPLATE_PATH));
            // create assignment index page
            template = template.replace("${assignment.title}", A.getName());
            template = template.replace("${assignment.description}", A.getDescription());
            template = template.replace("${assignment.description.pdf}", A.getAssignmentDescriptionPDF());
            // build submission index - high pass
            template = template.replace("${assignment.worksamples}", A.getSubmissionIndex());        
            // build submission index - low pass

            // write index file
            File index = new File(Output,"index.html");
            FileUtils.write(index, template);
            // write JPGs of all source files
            File[] files = A.getPath().listFiles(new ImageFileFilter());
            for (int i=0;i<files.length;i++) {
                FileUtils.copyFile(files[i], new File(Output,files[i].getName()));
            }
            // write thumbnails
            File thumbs = new File(Output,"thumb");
            if (!thumbs.exists()) {
                thumbs.mkdirs();
            }
            Iterator<Submission> its = A.getSubmissions();
            while (its.hasNext()) {
                Submission s = its.next();
                // File thumb = new File(thumbs.getAbsolutePath(),files[i].getName());
                // ImageUtils.writeThumbnail(files[i], thumb);            
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE,"Could not write assignment {0}. Will continue processing. Caught exception:\n\n{1}",
                    new Object[]{A.getPath(),ex});
        }
        // create index data
        // should probably get rid of the return statement
        return A.getPathSafeName();
    }
    
} // end class

