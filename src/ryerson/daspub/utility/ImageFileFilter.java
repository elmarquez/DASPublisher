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
 * Image file filter. Returns true when an image file that we are able to
 * process is encountered.
 * @author dmarques
 */
public class ImageFileFilter implements FilenameFilter {

    /**
     * Determine if the file is an image file that can be processed.
     * @param dir
     * @param name
     * @return True if the file is an image, false otherwise.
     */
    public boolean accept(File dir, String name) {
        int i = name.indexOf(".");
        if (i == -1) return false;
        String ext = name.toLowerCase().substring(i+1);
        boolean match = false;
        int j = 0;
        while (!match && j < Config.IMAGE_TYPES.length) {
            String type = Config.IMAGE_TYPES[j];
            if (ext.equals(type)) {
                match = true;
            }
            j++;
        }
        return match;
    }

}
