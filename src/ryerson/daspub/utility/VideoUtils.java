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
import java.io.IOException;

/**
 * Video file utilities.
 * @author dmarques
 * @see http://krishnabhargav.blogspot.com/2008/02/processing-videos-in-java.html
 * @see http://www.xuggle.com/xuggler/
 * @see http://build.xuggle.com/view/Stable/job/xuggler_jdk5_stable/ws/workingcopy/src/com/xuggle/mediatool/demos/DecodeAndCaptureFrames.java
 */
public class VideoUtils {

    /**
     * Write a JPG snapshot 
     * @param Input
     * @param Output
     * @param Width
     * @param Height
     * @throws IOException
     * @throws PdfException
     * @throws ImageReadException 
     */
    public static void writeJPGImage(File Input, File Output, int Width, int Height) throws IOException {
        // open file
        // get duration
        // if multiple snapshots
        //     subdivide duration by number of snapshots
        //     fetch frames
        // else
        //     get frame from 1/3 into video
        // write frames
    }
    
} // end class
