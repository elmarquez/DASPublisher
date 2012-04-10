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
 * Course description file filter.
 * @author dmarques
 */
public class CourseDescriptionTextFileFilter implements FilenameFilter {

    /**
     * Determine if a file is a course description.
     * @param dir Parent directory
     * @param name File name
     * @return True if the file is a course description, false otherwise.
     */
    public boolean accept(File dir, String name) {
        if (name.toLowerCase().equals(Config.COURSE_METADATA_FILE)) return true;
        return false;
    }

} // end class
