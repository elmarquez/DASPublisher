/*******************************************************************************
 * Copyright (c) 2008, 2010 Xuggle Inc.  All rights reserved.
 *
 * This file is part of Xuggle-Xuggler-Main.
 *
 * Xuggle-Xuggler-Main is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Xuggle-Xuggler-Main is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Xuggle-Xuggler-Main.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ryerson.daspub.utility;

import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Using {@link IMediaReader}, takes a media container, finds the first video 
 * stream, decodes that stream, and then writes video frames out to a PNG image 
 * file, based on the video presentation timestamps.
 * @author aclarke
 * @author trebor
 */
public class FrameGrabber extends MediaListenerAdapter {

    private int CAPTURED_FRAMES = 0;        // count of the number of frames captures
    private int MAX_CAPTURED_FRAMES = 1;    // maximum number of frames to capture
    
    // The number of seconds between frames.
    public static final double SECONDS_BETWEEN_FRAMES = 5;

    // The number of micro-seconds between frames.
    public static final long MICRO_SECONDS_BETWEEN_FRAMES =
            (long) (Global.DEFAULT_PTS_PER_SECOND * SECONDS_BETWEEN_FRAMES);

    // Time of last frame write.
    private static long mLastPtsWrite = Global.NO_PTS;
    
    // The video stream index, used to ensure we display frames from one
    // and only one video stream from the media container.
    private int mVideoStreamIndex = -1;

    private File input;
    private File output;
    
    private static final Logger logger = Logger.getLogger(FrameGrabber.class.getName());

    //--------------------------------------------------------------------------
    
    /** 
     * FrameGrabber constructor.
     * @param Input Absolute path to input file
     * @param Output Absolute path to output file
     */
    public FrameGrabber(File Input, File Output) {
        input = Input;
        output = Output;
        
        // create a media reader for processing video
        IMediaReader reader = ToolFactory.makeReader(Input.getAbsolutePath());

        // stipulate that we want BufferedImages created in BGR 24bit color space
        reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);

        // note that DecodeAndCaptureFrames is derived from
        // MediaReader.ListenerAdapter and thus may be added as a listener
        // to the MediaReader. DecodeAndCaptureFrames implements
        // onVideoPicture().
        reader.addListener(this);

        // read out the contents of the media file, note that nothing else
        // happens here.  action happens in the onVideoPicture() method
        // which is called when complete video pictures are extracted from
        // the media source
        while (reader.readPacket() == null && CAPTURED_FRAMES < MAX_CAPTURED_FRAMES) {
            do {
            } while (false);
        }
    }
    
    //--------------------------------------------------------------------------

    /**
     * Called after a video frame has been decoded from a media stream.
     * Optionally a BufferedImage version of the frame may be passed
     * if the calling {@link IMediaReader} instance was configured to
     * create BufferedImages. This method blocks, so return quickly.
     */
    public void onVideoPicture(IVideoPictureEvent event) {
        try {
            // if the stream index does not match the selected stream index,
            // then have a closer look
            if (event.getStreamIndex() != mVideoStreamIndex) {
                // if the selected video stream id is not yet set, go ahead an
                // select this lucky video stream
                if (-1 == mVideoStreamIndex) {
                    mVideoStreamIndex = event.getStreamIndex();
                } // otherwise return, no need to show frames from this video stream
                else {
                    return;
                }
            }
            // if uninitialized, backdate mLastPtsWrite so we get the very
            // first frame
            if (mLastPtsWrite == Global.NO_PTS) {
                mLastPtsWrite = event.getTimeStamp() - MICRO_SECONDS_BETWEEN_FRAMES;
            }
            // if it's time to write the next frame
            if (event.getTimeStamp() - mLastPtsWrite >= MICRO_SECONDS_BETWEEN_FRAMES) {
                // write out PNG
                ImageIO.write(event.getImage(), "png", output);
                // indicate file written
                double seconds = ((double) event.getTimeStamp()) / Global.DEFAULT_PTS_PER_SECOND;
                // update last write time
                mLastPtsWrite += MICRO_SECONDS_BETWEEN_FRAMES;
                CAPTURED_FRAMES++;
            }
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE,
                       "Could not write poster image for video file {0}\n\n{1}",
                        new Object[]{input.getAbsolutePath(),stack});
        }
    }
    
} // end class