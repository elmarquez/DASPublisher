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
import jxl.read.biff.WorkbookParser;
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
    public String getDescription() throws Exception {
        String text[] = parseDescriptionFile();
        return text[0];
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
        File metadata = getMetadataFile();
        // 
        try {
            WorkbookSettings ws = new WorkbookSettings();
            ws.setLocale(new Locale("en","EN"));
            FileInputStream fis = new FileInputStream(metadata);
            Workbook workbook = Workbook.getWorkbook(fis,ws);
            Sheet sheet = workbook.getSheet(0);
            // Cell[] headCells = sheet.getRow(0);
            for (int row=1;row<sheet.getRows();row++) {
                String errorStr = "";
                Cell[] cells = sheet.getRow(row);
                // get the cell values
                String course = cells[0].getContents();
                String filename = cells[1].getContents();
                String author = cells[2].getContents();
                String instructor = cells[3].getContents();
                String grade = cells[4].getContents();
                // if the cells are not empty, add the submission
                if (course != null && filename != null && author != null && 
                    instructor != null && grade != null) 
                {
                    // create a new submission from cell data
                    File f = new File(path,filename);
                    Submission s = new Submission(course,f.getAbsolutePath(),author,instructor,grade);
                    // add the submission to the list
                    items.add(s);
                }
            }            
        } catch (IOException | BiffException | IndexOutOfBoundsException ex) {
            logger.log(Level.SEVERE,"Metadata for assignment {0} could not be loaded. Caught exception:\n\n{1}",
                    new Object[]{path.getAbsolutePath(),ex.getStackTrace()});
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
    
} // end class

