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

/**
 * Directory file filter.
 * @author dmarques
 */
public class FolderFileFilter implements FileFilter {

    /**
     * Determine if a file is a directory.
     * @param F File
     * @return True if the file is a directory, false otherwise.
     */
    @Override
    public boolean accept(File F) {
        if (F.isDirectory()) return true;
        return false;
    }

} // end class
