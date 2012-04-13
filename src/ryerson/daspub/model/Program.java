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
import java.util.ArrayList;
import java.util.Iterator;
import ryerson.daspub.utility.FolderFileFilter;
import ryerson.daspub.utility.URLUtils;

/**
 * Academic program. Contains course folders.
 * @author dmarques
 */
public class Program {
    
    private File source;
    
    //--------------------------------------------------------------------------

    /**
     * Program constructor
     * @param Source Source folder
     */
    public Program(File Source) {
        source = Source;
    }
    
    //--------------------------------------------------------------------------

    /**
     * Determine if program folder exists.
     * @return True if the program folder exists, false otherwise.
     */
    public boolean exists() {
        return source.exists();
    }

    /**
     * Get the list of courses belonging to the program.
     * @return 
     */
    public Iterator<Course> getCourses() {
        ArrayList<Course> result = new ArrayList<>();
        File[] files = source.listFiles(new FolderFileFilter());
        for (int i=0;i<files.length;i++) {
            File f = files[i];
            Course c = new Course(f.getAbsolutePath());
            result.add(c);
        }
        return result.iterator();
    }
    
    /**
     * Get source folder file.
     */
    public File getFile() {
        return source;
    }
    
    /**
     * Get program name.
     */
    public String getName() {
        return source.getName();
    }

    /**
     * Get path safe name.
     */
    public String getURLSafeName() {
        String name = getName();
        return URLUtils.getURLSafeName(name);
    }

} // end class
