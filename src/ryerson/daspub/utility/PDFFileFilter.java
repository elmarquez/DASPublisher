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
 * PDF file filter.
 * @author dmarques
 */
public class PDFFileFilter implements FilenameFilter {

    /**
     * Determine if the file is a PDF file.
     * @param dir
     * @param name
     * @return True if the file is a PDF, false otherwise.
     */
    public boolean accept(File dir, String name) {
        int i = name.indexOf(".");
        if (i == -1) return false;
        String ext = name.toLowerCase().substring(i+1);
        if (ext.toLowerCase().equals("pdf")) return true;
        return false;
    }

} // end class
