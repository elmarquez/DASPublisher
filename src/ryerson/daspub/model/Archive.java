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
import java.util.List;
import ryerson.daspub.utility.FolderFileFilter;
import ryerson.daspub.utility.URLUtils;

/**
 * Archive of academic design work. Contains program folders.
 * @author dmarques
 */
public class Archive {
    
    private File source;
    
    //--------------------------------------------------------------------------

    /**
     * Archive constructor
     * @param Source Source folder
     */
    public Archive(File Source) {
        source = Source;
    }
    
    //--------------------------------------------------------------------------

    /**
     * Get the state 
     * @return True if the archive folder exists, false otherwise
     */
    public boolean exists() {
        return source.exists();
    }

    /**
     * Get a list of all archives in a given path.
     * @param Archive paths
     * @return 
     */
    public static List<Archive> getArchives(List<String> Paths) {
       ArrayList<Archive> result = new ArrayList<>();
       Iterator<String> itp = Paths.iterator();
       while (itp.hasNext()) {
           File folder = new File(itp.next());
           Archive archive = new Archive(folder);
           result.add(archive);
       }
       return result;
    }
    
    /**
     * Get source folder.
     * @return 
     */
    public File getFile() {
        return source;
    }
    
    /**
     * Get the archive name
     * @return Name
     */
    public String getName() {
        return source.getName();
    }
    
    /**
     * Get an iterator to the program list
     * @return 
     */
    public Iterator<Program> getPrograms() {
        ArrayList<Program> result = new ArrayList<>();
        if (source.exists()) {
            File[] files = source.listFiles(new FolderFileFilter());
            for (int i=0;i<files.length;i++) {
                File folder = files[i];
                Program p = new Program(folder);
                result.add(p);
            }
        }
        return result.iterator();
    }
    
    /**
     * Get path safe name.
     */
    public String getURLSafeName() {
        String name = getName();
        return URLUtils.getURLSafeName(name);
    }

} // end class
