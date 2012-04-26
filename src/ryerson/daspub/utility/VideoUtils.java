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

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.commons.io.FilenameUtils;
import ryerson.daspub.Config;

/**
 * Video file utilities.
 * @author dmarques
 * @see http://build.xuggle.com/view/Stable/job/xuggler_jdk5_stable/ws/workingcopy/src/com/xuggle/mediatool/demos/DecodeAndCaptureFrames.java
 */
public class VideoUtils {

    private static final String IMAGE_EXT = ".png";

    private static final String MIME_MP4 = "video/mp4";
    private static final String MIME_OGG = "video/ogg";
    private static final String MIME_WEBM = "video/webm";

    //--------------------------------------------------------------------------
    /**
     * Get video format identifier.
     * @param Input
     * @return
     */
    public static Config.VIDEO getFormat(File Input) {
        Config.VIDEO result = Config.VIDEO.NONE;
        if (Input != null) {
            String ext = FilenameUtils.getExtension(Input.getName());
            if (ext.equals("mp4")) {
                result = Config.VIDEO.MP4;
            } else if (ext.equals("ogg")) {
                result = Config.VIDEO.MP4;
            } else if (ext.equals("webm")) {
                result = Config.VIDEO.MP4;
            }
        }
        return result;
    }

    /**
     * Change the file name so that it has the new extension
     * @param Filename Current file name
     * @return
     */
    public static String getPNGFileName(String Filename) {
        String name = FilenameUtils.removeExtension(Filename);
        return name + IMAGE_EXT;
    }

    /**
     * Generate a new file name
     * When converting between image encoding types, the filename extension
     * @param File
     * @return
     */
    public static String getPNGFileName(File Input) {
        String filename = FilenameUtils.removeExtension(Input.getName());
        return filename + IMAGE_EXT;
    }

    /**
     * Get mimetype string for video file.
     * @param Input
     * @return Mimetype string
     * @TODO perhaps this should rely on type flag from submission instead of
     * parsing the extension
     */
    public static String getMimeType(File Input) {
        String ext = FilenameUtils.getExtension(Input.getName());
        String mimetype = "unknown"; // default
        if (ext.toLowerCase().equals("mp4")) {
            mimetype = MIME_MP4;
        } else if (ext.toLowerCase().equals("ogg")) {
            mimetype = MIME_OGG;
        } else if (ext.toLowerCase().equals("webm")) {
            mimetype = MIME_WEBM;
        }
        return mimetype;
    }

    /**
     * Get video stream screen dimensions.
     * @param Input Input video file
     * @returns Video stream screen dimensions
     * @throws IllegalArgumentException
     */
    public static Dimension getSize(File Input) throws IllegalArgumentException {
        // Create a Xuggler container object
        IContainer container = IContainer.make();
        // Open up the container
        if (container.open(Input.getAbsolutePath(), IContainer.Type.READ, null) < 0)
          throw new IllegalArgumentException("Could not open file: " + Input.getAbsolutePath());
        // get streams
        int numStreams = container.getNumStreams();
        // iterate through the streams to find video dimensions
        Dimension dim = new Dimension();
        for(int i = 0; i < numStreams; i++) {
            // find the stream object
            IStream stream = container.getStream(i);
            // get the pre-configured decoder that can decode this stream;
            IStreamCoder coder = stream.getStreamCoder();
            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                dim.setSize(coder.getWidth(),coder.getHeight());
            }
        }
        // return result
        return dim;
    }

    /**
     * Write first video frame as a PNG image file.
     * @param Input
     * @param Output
     * @throws IOException
     */
    public static File writePosterImage(File Input, File Output) throws IOException, URISyntaxException {
        File output = Output;
        // make sure the folder exists and file name is set correctly
        String filename = getPNGFileName(Input);
        if (output.isDirectory()) {
            output.mkdirs();
            output = new File(output, filename);
        } else {
            output.getParentFile().mkdirs();
        }
        // write video frame
        FrameGrabber d = new FrameGrabber(Input,output);
        // return the written file
        return output;
    }

} // end class
