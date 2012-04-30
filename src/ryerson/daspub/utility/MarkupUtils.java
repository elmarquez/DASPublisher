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
package ryerson.daspub.utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Utility functions to parse a structured text file into sections and formatted
 * HTML blocks.
 * @author dmarques
 */
public class MarkupUtils {

    private static final String TITLE_DELIMITER = "==";
    
    private static final Logger logger = Logger.getLogger(MarkupUtils.class.getName());
    
    //--------------------------------------------------------------------------

    /**
     * Build HTML list from list items.
     * @param Items
     * @param Tag
     * @return 
     */
    private static String getHTMLList(List<String> Items, String Tag) {
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(Tag);
        sb.append(">");
        Iterator<String> its = Items.iterator();
        while (its.hasNext()) {
            String item = its.next();
            if (!item.equals("") || !item.equals(" ")) {
                sb.append("\n\t<li>");
                sb.append(item.trim());
                sb.append("</li>");
            }
        }
        sb.append("\n</");
        sb.append(Tag);
        sb.append(">");
        return sb.toString();
    }
    
    /**
     * Get ordered bullet list from structured text markup.
     * @param Markup
     * @return 
     */
    public static String getHTMLOrderedList(List<String> Items) {
        return getHTMLList(Items,"ol");
    }
    
    /**
     * Get unordered bullet list from structured text markup.
     * @param Markup
     * @return 
     */
    public static String getHTMLUnorderedList(List<String> Items) {
        return getHTMLList(Items,"ul");
    }
    
    /**
     * Get list from markup items.
     */
    public static List<String> getList(String Markup, String ItemMarker) {
        ArrayList<String> result = new ArrayList<String>();
        String[] item = Markup.split(ItemMarker);    
        for (int i=0;i<item.length;i++) {
            if (!item[i].equals("") && !item[i].equals(" ")) {
                result.add(item[i].trim());
            }
        }
        return result;
    }
    
    /**
     * Assumes that each section in the document is delimited with a title. The
     * title is on a line that starts with ==
     * @param F
     * @return 
     */
    public static Map<String, String> parse(File F) {
        HashMap<String,String> result = new HashMap<String,String>();
        List<String> lines = null;
        try {
            lines = FileUtils.readLines(F);
            // line processing method
            String key = "";
            StringBuilder value = new StringBuilder();
            if (lines != null) {
                Iterator<String> it = lines.iterator();
                while (it.hasNext()) {
                    String line = it.next();
                    line = line.trim();
                    if (!line.equals("")) {
                        if (line.startsWith(TITLE_DELIMITER)) {
                            String newkey = line.replace(TITLE_DELIMITER, "");
                            if (!newkey.equals(key)) {
                                if (!key.equals("")) {
                                    result.put(key,value.toString().trim());                                
                                }
                                key = newkey;
                                value = new StringBuilder();
                            }
                        } else {
                            line = line.replace("\r", "");
                            line = line.replace("\n", "");
                            line = line.trim();
                            value.append(line);
                            value.append(" ");
                        }
                    }
                }
                // make sure that the last item gets inserted into the map
                result.put(key, value.toString());
            }
        } catch (IOException ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE,"Could not parse markup file {0}\n\n{1}",
                        new Object[]{F.getAbsolutePath(),stack});
        }
        return result;
    }
    
} // end class
