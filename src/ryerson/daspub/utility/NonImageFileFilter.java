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
import java.io.FilenameFilter;
import ryerson.daspub.Config;

/**
 * Non-image file filter. Returns true if a file is not processable using the
 * included image processing utilities.
 * @author dmarques
 */
public class NonImageFileFilter implements FilenameFilter {

    /**
     * Determine if the file is not an image file that can be processed and not
     * a metadata file.
     * @param dir Parent directory
     * @param name File name
     * @return True if the file is an image, false otherwise.
     */
    public boolean accept(File dir, String name) {
        int i = name.indexOf(".");
        if (i == -1) return true;
        String ext = name.toLowerCase().substring(i+1);
        boolean result = true;
        // check to see if its a visual file
        int j = 0;
        String type = "";
        while (result && j < Config.PROCESSABLE_IMAGE_TYPES.length) {
            type = Config.PROCESSABLE_IMAGE_TYPES[j];
            if (ext.equals(type)) {
                return false;
            }
            j++;
        }
        // check to see if its a metadata file
        j = 0;
        String[] types = Config.getMetadataFileTypes();
        while (result && j < types.length) {
            type = types[j];
            if (ext.equals(type)) {
                return false;
            }
            j++;
        }
        // return result
        return result;
    }

} // end class
