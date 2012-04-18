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

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.InputFormatException;
import it.sauronsoftware.jave.MultimediaInfo;
import it.sauronsoftware.jave.VideoInfo;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import ryerson.daspub.Config;

/**
 * Video file utilities.
 * @author dmarques
 * @see http://krishnabhargav.blogspot.com/2008/02/processing-videos-in-java.html
 * @see http://www.xuggle.com/xuggler/
 * @see http://build.xuggle.com/view/Stable/job/xuggler_jdk5_stable/ws/workingcopy/src/com/xuggle/mediatool/demos/DecodeAndCaptureFrames.java
 */
public class VideoUtils {

    /**
     * Get video format identifier.
     * @param Input
     * @return 
     */
    public static Config.VIDEO getFormat(File Input) {
        Config.VIDEO result = Config.VIDEO.NONE;
        if (Input != null) {
            String ext = FilenameUtils.getExtension(Input.getName());
            switch (ext) {
                case "mp4":
                    result = Config.VIDEO.MP4;
                    break;
                case "ogg":
                    result = Config.VIDEO.MP4;
                    break;
                case "webm":
                    result = Config.VIDEO.MP4;
                    break;
            }
        }
        return result;
    }
    
    /**
     * Change the file name so that it has the new extension
     * @param Filename Current file name
     * @return
     */
    public static String getJPGFileName(String Filename) {
        String name = FilenameUtils.removeExtension(Filename);
        return name + ".jpg";
    }

    /**
     * Generate a new file name
     * When converting between image encoding types, the filename extension
     * @param File
     * @return
     */
    public static String getJPGFileName(File Input) {
        String filename = FilenameUtils.removeExtension(Input.getName());
        return filename + ".jpg";
    }
    
    /**
     * Get video dimensions.
     */
    public static VideoInfo getMetadata(File Input) throws InputFormatException, EncoderException {
        Encoder encoder = new Encoder();
        MultimediaInfo info = encoder.getInfo(Input);
        return info.getVideo();
    }

    /**
     * Get mimetype string for video file.
     * @param Input
     * @return 
     */
    public static String getMimeType(File Input) {
        String ext = FilenameUtils.getExtension(Input.getName());
        String mimetype = "unknown"; // default
        switch (ext.toLowerCase()) {
            case "mp4":
                mimetype = "video/mp4";
                break;
            case "ogg":
                mimetype = "video/ogg";
                break;
            case "webm":
                mimetype = "video/webm";
                break;
        }
        return mimetype;
    }
    
    /**
     * Write a JPG snapshot 
     * @param Input
     * @param Output
     * @param Width
     * @param Height
     * @throws IOException
     */
    public static File writeJPGPosterImage(File Input, File Output, int Width, int Height) throws IOException, URISyntaxException, InputFormatException, EncoderException {
        File output = Output;
        // make sure the folder exists and file name is set correctly
        String filename = getJPGFileName(Input);
        if (Output.isDirectory()) {
            Output.mkdirs();
            output = new File(Output,filename);
        } else {
            Output.getParentFile().mkdirs();
        }
        // get a frame from the video
        Encoder encoder = new Encoder();
        MultimediaInfo info = encoder.getInfo(Input);

        // write the video frame
        
        // return the written file
        return output;
    }
    
} // end class
