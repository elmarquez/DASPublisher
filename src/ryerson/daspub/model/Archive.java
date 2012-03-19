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

/**
 * Archive of academic design work. Contains program folders.
 * @author dmarques
 */
public class Archive {
    
    private String path;
    
    //--------------------------------------------------------------------------

    /**
     * Archive constructor
     * @param Path Path
     */
    public Archive(String Path) {
        path = Path;
    }
    
    //--------------------------------------------------------------------------

    /**
     * Get the state 
     * @return True if the archive folder exists, false otherwise
     */
    public boolean exists() {
        File f = new File(path);
        return f.exists();
    }

    /**
     * 
     * @param Paths
     * @return 
     */
    public static Iterator<Archive> getArchives(List<String> Paths) {
       ArrayList<Archive> result = new ArrayList<>();
       Iterator<String> itp = Paths.iterator();
       while (itp.hasNext()) {
           result.add(new Archive(itp.next()));
       }
       return result.iterator();
    }
    
    /**
     * Get the archive name
     * @return Name
     */
    public String getName() {
        File f = new File(path);
        return f.getName();
    }
    
    /**
     * Get the archive path
     * @return Path
     */
    public String getPath() {
        return path;
    }
    
    /**
     * Get path safe name.
     */
    public String getPathSafeName() {
        String name = getName();
        return name.replace(" ", "_");
    }

    /**
     * Get an iterator to the program list
     * @return 
     */
    public Iterator<Program> getPrograms() {
        File archive = new File(path);
        ArrayList<Program> result = new ArrayList<>();
        if (archive.exists()) {
            File[] files = archive.listFiles(new FolderFileFilter());
            for (int i=0;i<files.length;i++) {
                File f = files[i];
                Program p = new Program(f.getAbsolutePath());
                result.add(p);
            }
        }
        return result.iterator();
    }
    
} // end class
