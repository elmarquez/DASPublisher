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
import java.io.FileFilter;
import org.apache.commons.io.FilenameUtils;

/**
 * JSON file filter.
 * @author dmarques
 */
public class JSONFileFilter implements FileFilter {

    private final String EXT = "jsn";
    
    /**
     * Determine if file is JSON file.
     * @param F File
     */
    @Override
    public boolean accept(File F) {
        String ext = FilenameUtils.getExtension(F.getName());
        if (EXT.equals(ext)) {
            return true;
        }
        return false;
    }
    
} // end class
