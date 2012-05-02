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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * A utility class for maintaining configuration data. Defaults are provided in
 * the class. Overriding values can be loaded from a file. When specifying an
 * overriding value, it should match the name of the corresponding variable
 * in the Config class.
 * @author dmarques
 */
public class Config {

    public static final String APPLICATION_TITLE = "DAS Publisher";
    public static final String APPLICATION_SHORT_TITLE = "DASPub";

    // publication status
    public static enum STATUS {COMPLETE, INCOMPLETE, PARTIAL, ERROR};
    public static enum SUBMISSION_EVALUATION {HIGH_PASS, LOW_PASS, NONE};
    public static enum SUBMISSION_TYPE {IMAGE, PDF, VIDEO, NONE};
    public static enum VIDEO { MP4, OGG, WEBM, NONE };
    
    // file types
    public static final String PDF_TYPE = "pdf";
    public static final String[] IMAGE_TYPE = {"jpg","pdf","png","tif","gif","jpeg","tiff"}; // pdf needs to be listed to simplify some search functions ... 
    public static final String[] VIDEO_TYPE = {"mp4","ogg","webm"};

    // output paths
    public static String OUTPUT_ARTIFACT_PAGES_PATH = "c:\\daspublisher\\output\\artifact\\";
    public static String OUTPUT_MOBILE_PATH = "c:\\daspublisher\\output\\mobile\\";
    public static String OUTPUT_QR_LABELSHEET_PATH = "c:\\daspublisher\\output\\artifact\\qr\\";
    public static String OUTPUT_REPORT_PATH = "c:\\daspublisher\\output\\report\\";
    public static String OUTPUT_SLIDESHOW_PATH = "c:\\daspublisher\\output\\slideshow\\";

    // templates
    public static String ARTIFACT_TEMPLATE_PATH = "c:\\daspublisher\\template\\artifact.php";
    public static String ASSIGNMENT_TEMPLATE_PATH = "c:\\daspublisher\\static\\assignment.txt";
    public static String COURSE_TEMPLATE_PATH = "c:\\daspublisher\\static\\course.txt";

    // static content
    public static String STATIC_ARTIFACT_CONTENT = "c:\\daspublisher\\static\\artifact\\";
    public static String STATIC_MOBILE_CONTENT = "c:\\daspublisher\\static\\mobile\\";
    public static String STATIC_REPORT_CONTENT = "c:\\daspublisher\\static\\report\\";
    public static String STATIC_SLIDESHOW_CONTENT = "c:\\daspublisher\\static\\slideshow\\";

    // archive
    public static ArrayList<String> ARCHIVE_PATHS = new ArrayList<String>(); 
    public static String ARCHIVE_PATH;
    
    // output properties
    public static String COURSE_METADATA_FILE = "course.txt";
    public static String COURSE_SYLLABUS_FILE = "course.pdf";
    public static String ASSIGNMENT_METADATA_FILE = "assignment.txt";
    public static String ASSIGNMENT_SYLLABUS_FILE = "assignment.pdf";
    public static String SUBMISSION_METADATA_FILE = "assignment.xls";

    public static int IMAGE_MAX_HEIGHT = 2000;    // full size image maximum height
    public static int IMAGE_MAX_WIDTH = 2000;     // full size image maximum width
    public static int THUMB_MAX_HEIGHT = 300;     // thumbnail image maximum height
    public static int THUMB_MAX_WIDTH = 300;      // thumbnail image maximum width
    public static int VIDEO_MAX_WIDTH = 640;      // maximum video width
    public static int VIDEO_MAX_HEIGHT = 480;     // maximum video height
    
    public static String ARTIFACT_BASE_URL = "http://www.myserver.org/";
    public static int ARTIFACT_PREVIEW_MAX_HEIGHT = 640;
    public static int ARTIFACT_PREVIEW_MAX_WIDTH = 640;
    public static int ARTIFACT_TAG_WIDTH = 200;
    public static int ARTIFACT_TAG_HEIGHT = 200;

    private static File input;
    private static String data;

    private static final Logger logger = Logger.getLogger(Config.class.getName());

    //--------------------------------------------------------------------------

    /**
     * Config constructor. Uses default values.
     */
    public Config() {}
    
    /**
     * Config constructor. Set values using map.
     * @param Args 
     */
    public Config(HashMap<String,String> Args) {
        setValues(Args);
    }

    //--------------------------------------------------------------------------
    
    /**
     * Get the artifact template.
     * @return 
     */
    public String getArtifactTemplate() {
        File template = new File(Config.ARTIFACT_TEMPLATE_PATH);
        try {
            return FileUtils.readFileToString(template);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Could not load artifact template {0}", 
                    new Object[] {template.getAbsolutePath(),ex});
            return "";
        }
    }
    
    /**
     * Load configuration data from a file.
     * @param F Configuration file
     */
    public static Config load(File F) throws Exception {
        logger.log(Level.INFO,"Loading configuration file \"{0}\"",F.getAbsolutePath());
        HashMap<String,String> vals = parseConfigurationFile(F);
        return new Config(vals);
    }
    
    /**
     * Parse the configuration file
     * @param Path
     * @return Map of arguments
     */
    private static HashMap<String,String> parseConfigurationFile(File F) throws Exception {
        HashMap<String,String> args = new HashMap<String,String>();
        if (F.exists()) {
            StringBuilder text = new StringBuilder();
            try {
                Scanner scanner = new Scanner(new FileInputStream(F), "UTF-8");
                String line = "";
                while (scanner.hasNextLine()) {
                    line = scanner.nextLine();
                    if (!line.startsWith("//") && !line.startsWith(" ") && !line.equals("")) {
                        line = line.trim();
                        String[] items = line.split("=");
                        String key = "";
                        String val = "";
                        if (items.length > 0) {
                            key = items[0].trim();
                            if (items.length > 1) {
                                val = items[1].trim();
                            }
                            args.put(key, val);
                        }
                    }
                }
            } catch (Exception ex) {

            }
        } else {
            logger.log(Level.WARNING,"Configuration file does not exist {0}",F.getAbsolutePath());            
        }
        // return result
        return args;            
    }

    /**
     * Save the configuration.
     * @return True if file saved, false otherwise.
     */
    public boolean save() {
        throw new UnsupportedOperationException("Save has not been implemented for the Config class");
    }
    
    /**
     * Save the configuration to a file.
     * @param C Configuration
     * @param F File
     */
    public static void save(Config C, File F) {
        throw new UnsupportedOperationException("Save has not been implemented for the Config class");
    }
    
    /**
     * Parse arguments and assign variables
     * @param Args
     */
    private static void setValues(HashMap<String,String> Args) {
        Set<String> keys = Args.keySet();
        Iterator<String> it = keys.iterator();
        String name = "";
        String val = "";
        Field field = null;
        while (it.hasNext()) {
            name = it.next(); // all local fields have upper case names
            Class aClass = Config.class;
            try {
                field = aClass.getField(name.toUpperCase());
                val = Args.get(name);
                if (field.getType()==int.class) {
                    field.setInt(Config.class,Integer.valueOf(val));
                } else {
                    field.set(Config.class,val);
                }
                logger.log(Level.INFO,"Set \"{0}\" as \"{1}\"",new Object[]{field.getName(),val.toString()});
            } catch (Exception ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE,"Could not find or set field \"{0}\"\n\n{1}",
                        new Object[]{name,stack});
            }
        }
        // create derived objects
        if (ARCHIVE_PATH != null && ARCHIVE_PATH.contains(";")) {
            String[] items = ARCHIVE_PATH.split(";");
            for (int i=0;i<items.length;i++) {
                String path = items[i].trim();
                ARCHIVE_PATHS.add(path);
            }
        } else {
            ARCHIVE_PATHS.add(ARCHIVE_PATH);
        }
    }

} // end class
