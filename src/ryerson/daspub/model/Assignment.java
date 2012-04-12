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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.Config;
import ryerson.daspub.Config.STATUS;
import ryerson.daspub.utility.AssignmentMetadataFileFilter;
import ryerson.daspub.utility.AssignmentPDFFileFilter;
import ryerson.daspub.utility.MarkupParser;
import ryerson.daspub.utility.SubmissionMetadataFileFilter;
import ryerson.daspub.utility.MetadataFileFilter;

/**
 * Assignment entity. An assignment has four types of subfiles:
 * 
 * - a text file containing metadata describing the assignment
 * - a PDF file representing the handout that students received for the assignment
 * - student submissions (images, PDFs, videos)
 * - an Excel spreadsheet with metadata for all student submissions
 * 
 * @author dmarques
 */
public class Assignment {

    private File source;
    private String description = "";

    private static final Logger logger = Logger.getLogger(Assignment.class.getName());
    
    //--------------------------------------------------------------------------

    /**
     * Assignment constructor
     * @param F Source folder
     */
    public Assignment(File F) {
        source = F;
        parseMetadataFile();
    }
   
    //--------------------------------------------------------------------------

    /**
     * Get assignment description. If no description is available, an empty
     * string is returned.
     * @return Assignment description
     * @throws Exception
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get path
     * @return 
     */
    public File getFolder() {
       return source; 
    }

    /**
     * Get submission metadata file.
     * @return Metadata file. Null if no file is found.
     */
    private File getMetadataFile() {
        File[] files = source.listFiles(new MetadataFileFilter());
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
        return source.getName();
    }

    /**
     * Get publication status
     * @return Assignment STATUS flag value.
     */
    public STATUS getStatus() {
        if (!this.hasSyllabusFile()) {
            return STATUS.INCOMPLETE;
        }
        if (!this.hasMetadataFile()) {
            return STATUS.INCOMPLETE;
        }
        if (!this.hasSubmissionMetadataFile()) {
            return STATUS.INCOMPLETE;
        }
        if (!this.hasConformingSubmissionMetadataFile()) {
            return STATUS.INCOMPLETE;
        }
        if (!this.hasSubmissions()) {
            return STATUS.PARTIAL;
        }
        if (!this.hasCompleteSubmissionMetadata()) {
            return STATUS.PARTIAL;
        }
        return STATUS.COMPLETE;
    }

    /**
     * 
     * @return 
     */
    public List<Submission> getSubmissions() {
        ArrayList<Submission> items = new ArrayList<>();
        // load the metadata file
        File file = new File(this.source,Config.SUBMISSION_METADATA_FILE);
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
                    Submission s = Submission.getSubmission(cells,source);
                    if (s != null) {
                        items.add(s);
                    }
                }            
            } catch (IOException | BiffException | IndexOutOfBoundsException ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE,"Submission metadata for assignment {0} could not be loaded.\n\n{1}",
                        new Object[]{source.getAbsolutePath(),stack});
            }
        }
        return items;        
    }

    /**
     * Get link to assignment syllabus file.
     * @return Returns null if file does not exist.
     */
    public File getSyllabusFile() {
        File file = new File(source,Config.ASSIGNMENT_SYLLABUS_FILE);
        if (file.exists()) return file;
        return null;
     }
    
    /**
     * Get path safe name.
     * @TODO put this into a utility class
     */
    public String getURLSafeName() {
        String name = getName();
        name = name.replace(" ", "_");
        name = name.replace(":", "_");
        name = name.replace("/", "_");
        name = name.replace(",", "_");
        name = name.replace(".", "_");
        name = name.replace("-", "_");
        return name;
    }

    /**
     * Determine if the assignment has complete submission metadata.
     */
    public boolean hasCompleteSubmissionMetadata() {
       logger.log(Level.WARNING,"Assignment.hasCompleteSubmissionMetadata is not implemented and does not return correct results.");
       return true; 
    }
    
    /**
     * Determine if the submission metadata file conforms to the requirements
     * for parsing.
     * TODO this is duplicate code!! factor it out!
     */
    public boolean hasConformingSubmissionMetadataFile() {
        ArrayList<Submission> items = new ArrayList<>();
        // load the metadata file
        File file = new File(this.source,Config.SUBMISSION_METADATA_FILE);
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
                    Submission s = Submission.getSubmission(cells,source);
                    if (s != null) {
                        items.add(s);
                    }
                }            
            } catch (IOException | BiffException | IndexOutOfBoundsException ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE,"Submission metadata for assignment {0} could not be loaded.\n\n{1}",
                        new Object[]{source.getAbsolutePath(),stack});
                return false;
            }
        }
       return true;
    }

    /**
     * Determine if the assignment has any submissions with image files.
     * @return True if the assignment includes on or more submissions with images, false otherwise.
     */
    public boolean hasDocumentSubmission() {
       return true;
    }
    
    /**
     * Determine if the assignment contains a text file with assignment metadata.
     * @return True if file exists, false otherwise.
     */
    public boolean hasMetadataFile() {
        File[] files = source.listFiles(new AssignmentMetadataFileFilter());
        if (files.length > 0) return true;
        return false;
    }

    /**
     * Determine if the assignment has a submission metadata file
     * @return 
     */
    public boolean hasSubmissionMetadataFile() {
        File[] files = source.listFiles(new SubmissionMetadataFileFilter());
        if (files.length > 0) return true;
        return false;
    }
    
    /**
     * Determine if the assignment has student submissions
     * @return 
     */
    public boolean hasSubmissions() {
        List<Submission> items = getSubmissions();
        if (items.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Determine if the assignment has a PDF file assignment handout.
     * @return True if file exists, false otherwise.
     */
    public boolean hasSyllabusFile() {
        File[] files = source.listFiles(new AssignmentPDFFileFilter());
        if (files.length > 0) return true;
        return false;
    }

    /**
     * Determine if the assignment has any animations or video files.
     * @return True if the assignment includes on or more submissions with video, false otherwise.
     */
    public boolean hasVideoSubmission() {
        // @TODO complete this
        return false;
    }
    
    /**
     * Parse the metadata file.
     */
    private void parseMetadataFile() {
        File file = new File(source,Config.ASSIGNMENT_METADATA_FILE);
        if (file.exists()) {
            Map<String,String> vals = MarkupParser.parse(file);
            if (vals.containsKey("Description")) {
                description = vals.get("Description");
            }
        }
    }
    
} // end class
