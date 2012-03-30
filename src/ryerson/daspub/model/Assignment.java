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
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.Config;
import ryerson.daspub.utility.AssignmentDescriptionTextFileFilter;
import ryerson.daspub.utility.AssignmentMetadataFileFilter;
import ryerson.daspub.utility.MetadataFileFilter;

/**
 * Assignment entity.
 * @author dmarques
 */
public class Assignment {

    public static enum STATUS {COMPLETE, INCOMPLETE, ERROR};
    private static String THUMBS = "thumbs/";
    
    private File path;
    private String description = "";

    private static final Logger logger = Logger.getLogger(Assignment.class.getName());
    
    //--------------------------------------------------------------------------

    /**
     * Assignment constructor
     * @param D Directory
     */
    public Assignment(File D) {
        path = D;
    }
   
    //--------------------------------------------------------------------------

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
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE,"Could not parse description.\n\n{0}",stack);
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
     * Get path
     * @return 
     */
    public File getPath() {
       return path; 
    }

    /**
     * Get path safe name.
     * TODO consider replacing _ with %20
     */
    public String getPathSafeName() {
        String name = getName();
        return name.replace(" ", "_");
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
                for (int row=1;row<rows;row++) {
                    Cell[] cells = sheet.getRow(row);
                    Submission s = Submission.getSubmission(cells,path);
                    if (s != null) {
                        items.add(s);
                    }
                }            
            } catch (IOException | BiffException | IndexOutOfBoundsException ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE,"Metadata for assignment {0} could not be loaded.\n\n{1}",
                        new Object[]{path.getAbsolutePath(),stack});
            }
        }
        return items.iterator();
    }

    /**
     * Get link to assignment syllabus file.
     * @return 
     */
    public String getSyllabusLink() {
        File file = new File(path.getAbsolutePath(),Config.ASSIGNMENT_DESCRIPTION_PDF_FILE);
        if (file.exists()) {
            return "<a href='" + Config.ASSIGNMENT_DESCRIPTION_PDF_FILE + "'>Assignment handout</a>";
        } else {
            return "Assignment handout not available.";
        }
     }
    
    /**
     * Determine if assignment has a description file.
     * @return True if file exists, false otherwise.
     */
    public boolean hasDescriptionFile() {
        File[] files = path.listFiles(new AssignmentDescriptionTextFileFilter());
        if (files.length > 0) return true;
        return false;
    }

    /**
     * Determine if assignment has a metadata file.
     * @return True if file exists, false otherwise.
     */
    public boolean hasMetadataFile() {
        File[] files = path.listFiles(new AssignmentMetadataFileFilter());
        if (files.length > 0) return true;
        return false;
    }

    /**
     * Determine if the file metadata is complete
     * @return
     */
    public boolean isCompletedMetadata() {
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
