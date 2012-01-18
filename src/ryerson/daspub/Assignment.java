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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.WorkbookSettings;
import jxl.read.biff.WorkbookParser;
import org.apache.commons.io.FileUtils;
import ryerson.daspub.utility.AssignmentDescriptionTextFileFilter;
import ryerson.daspub.utility.AssignmentFileMetadataFileFilter;
import ryerson.daspub.utility.ImageFileFilter;
import ryerson.daspub.utility.MetadataFileFilter;
import ryerson.daspub.utility.NonImageFileFilter;
import ryerson.daspub.utility.ProcessableImageFileFilter;

/**
 * Assignment entity. Lazy loads data from the file system.
 * @author dmarques
 */
public class Assignment {

    public static enum STATUS {COMPLETE, INCOMPLETE, ERROR};

    private File dir;

    private static final Logger _logger = Logger.getLogger(Assignment.class.getName());
    
    //--------------------------------------------------------------------------

    /**
     * Assignment constructor
     * @param D Directory
     */
    public Assignment(File D) {
        dir = D;
    }

    //--------------------------------------------------------------------------

    /**
     * Get link to assignment description PDF
     * @return 
     */
    public String getAssignmentDescriptionPDF() {
        File file = new File(dir.getAbsolutePath(),Config.ASSIGNMENT_DESCRIPTION_PDF_FILE);
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
        String name = dir.getName();
        return name.replace(" ", "_");
    }

    /**
     * Get list of files
     * @return List of image files
     */
    public List<File> getMedia() {
        ArrayList<File> items = new ArrayList<>();
        File[] files = dir.listFiles(new ProcessableImageFileFilter());
        items.addAll(Arrays.asList(files));
        return items;
    }

    /**
     * Get assignment metadata
     */
    private void getMetadata() {
        File metadata = getMetadataFile();
        if (metadata != null) {
            try {
                WorkbookSettings wbs = new WorkbookSettings();
                FileInputStream fis = new FileInputStream(metadata);
                jxl.read.biff.File f = new jxl.read.biff.File(null, wbs);
                WorkbookParser wbp = new WorkbookParser(f,wbs);
            } catch (Exception ex) {
                _logger.log(Level.SEVERE,"Could not load metadata file {0}",metadata.getAbsolutePath());
            }
        } else {
            _logger.log(Level.SEVERE,"Assignment {0} metadata file {1} could not be loaded.",
                    new Object[]{dir.getAbsolutePath(),metadata.getAbsolutePath()});
        }
    }
    
    /**
     * Get metadata file
     * @return 
     */
    private File getMetadataFile() {
        File[] files = dir.listFiles(new MetadataFileFilter());
        File file = null;
        return file;
    }
    
    /**
     * Get list of metadata files.
     * TODO: do we need this method? seems useless
     * @return List of files.
     */
    private File[] getMetadataFileList() {
        return dir.listFiles(new MetadataFileFilter());
    }

    /**
     * Get list of non-processable files.
     * @return List of files
     */
    private File[] getNonProcessableFileList() {
        return dir.listFiles(new NonImageFileFilter());
    }

    /**
     * Get path
     * @return 
     */
    public File getPath() {
       return dir; 
    }

    /**
     * Get list of processable files.
     * @return List of files
     */
    private File[] getProcessableFileList() {
        return dir.listFiles(new ImageFileFilter());
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
        sb.append(getTitle());
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
        File[] files = dir.listFiles();
        int count = files.length;
        sb.append("\n\t\t\t<p>There are ");
        sb.append(count);
        sb.append(" files in the assignment folder.");
        sb.append("</p>");
        sb.append("\n\t\t\t<ul>");
        files = dir.listFiles(new NonImageFileFilter());
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
     * Get assignment title
     * @return
     */
    public String getTitle() {
        return dir.getName();
    }

    /**
     * A map of fullsize, thumbnail file paths?
     * @return 
     */
    public String getWorkIndex() {
        StringBuilder sb = new StringBuilder();
        List<File> work = getMedia();
        Iterator<File> it = work.iterator();
        sb.append("\n<ul id='Gallery' class='gallery'>");
        while (it.hasNext()) {
            String filename = it.next().getName();
            sb.append("\n\t<li>");
            sb.append("<a href='");
            sb.append(filename);
            sb.append("' rel='external'>");
            sb.append("<img src='");
            sb.append(filename);
            // TODO get the description, etc. from the spreadsheet file
            sb.append("' alt='");
            sb.append("Description of the work, authors, date, instructors");
            sb.append("' />");
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
        File[] files = dir.listFiles(new AssignmentDescriptionTextFileFilter());
        if (files.length > 0) return true;
        return false;
    }

    /**
     * Determine if assignment has a metadata file.
     * @return True if file exists, false otherwise.
     */
    private boolean hasMetadataFile() {
        File[] files = dir.listFiles(new AssignmentFileMetadataFileFilter());
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
       File file = new File(dir.getAbsolutePath(),Config.ASSIGNMENT_DESCRIPTION_TEXT_FILE);
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

