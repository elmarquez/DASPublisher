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

/**
 * Non-directory file filter. Returns true if a file is not a file folder.
 * @author dmarques
 */
public class NonDirectoryFileFilter implements FilenameFilter {

    /**
     * Determine if the file is not a directory.
     * @param dir Parent directory
     * @param name File name
     * @return True if the file is a directory, false otherwise.
     */
    public boolean accept(File dir, String name) {
        File file = new File(dir,name);
        if (file.isDirectory()) {
            return false;
        }
        return true;
    }

} // end class
