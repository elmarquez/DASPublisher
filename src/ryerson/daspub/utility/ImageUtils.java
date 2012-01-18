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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import org.jpedal.exception.PdfException;
import ryerson.daspub.Config;

/**
 *
 * @author dmarques
 * @see http://im4java.sourceforge.net/
 */
public class ImageUtils {

    private static final Logger _logger = Logger.getLogger(ImageUtils.class.getName());
    
    /**
     * Determine if a file is an image.
     * @param F File
     * @return True if an image, false otherwise
     */
    public static boolean isImage(File F) {
        boolean match = false;
        int i = F.getName().lastIndexOf(".");
        if (i != -1) {
            String ext = F.getName().substring(i+1);
            int count = 0;
            while (!match && count < Config.IMAGE_TYPES.length) {
                if (Config.IMAGE_TYPES[count].equals(ext)) {
                    match = true;
                }
                count++;
            }            
        }
        return match;
    }
    
    /**
     * Determine if a file is a visual but not processable.
     * @param F File
     * @return True if a near image, false otherwise
     */
    public static boolean isNearImage(File F) {
        boolean match = false;
        int i = F.getName().lastIndexOf(".");
        if (i != -1) {
            String ext = F.getName().substring(i+1);
            int count = 0;
            while (!match && count < Config.NEAR_IMAGE_TYPES.length) {
                if (Config.NEAR_IMAGE_TYPES[count].equals(ext)) {
                    match = true;
                }
                count++;
            }            
        }
        return match;
    }

    /**
     * Determine if a file is a PDF document.
     * @param F File
     * @return True if a PDF document, false otherwise
     */
    public static boolean isPDF(File F) {
        boolean match = false;
        int i = F.getName().lastIndexOf(".");
        if (i != -1) {
            String ext = F.getName().substring(i+1);
            if (Config.PDF_TYPE.equals(ext)) {
                match = true;
            }
        }
        return match;
    }
    
    /**
     * Determine if a file is a processable image.
     * @param F File
     * @return True if a processable image, false otherwise
     */
    public static boolean isProcessableImage(File F) {
        boolean match = false;
        int i = F.getName().lastIndexOf(".");
        if (i != -1) {
            String ext = F.getName().substring(i+1);
            int count = 0;
            while (!match && count < Config.PROCESSABLE_IMAGE_TYPES.length) {
                if (Config.PROCESSABLE_IMAGE_TYPES[count].equals(ext)) {
                    match = true;
                }
                count++;
            }            
        }
        return match;
    }
    
    /**
     * 
     * @param Input
     * @param Width
     * @param Height
     * @return
     * @throws IOException 
     */
    public static byte[] resizeImageToByteArray(File Input, int Width, int Height) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Thumbnails.of(Input).size(Width,Height).toOutputStream(baos);
        return baos.toByteArray();
    }
    
    /**
     * 
     * @param Input
     * @param Output
     * @param Width
     * @param Height
     * @throws IOException
     * @throws PdfException 
     */
    public static void writeImage(File Input, File Output, int Width, int Height) throws IOException, PdfException {
        // TODO there is a problem here when resizing
        // TODO check the original image size and don't go any higher than the original
        if (isProcessableImage(Input)) {
            Thumbnails.of(Input).size(Width,Height).toFile(Output);               
            _logger.log(Level.FINE,"Wrote image {0}", Output.getAbsolutePath());
        } else if (isPDF(Input)) {
            PDFUtils.WriteThumbnail(Input,Output,Width,Height);
            _logger.log(Level.FINE,"Wrote image {0}", Output.getAbsolutePath());
        } else {
            _logger.log(Level.WARNING,"Could not write image for {0}. File is not a processable image or PDF.", Output.getAbsolutePath());                
        }
    }

    /**
     * Resize image and write thumbnail file.
     * @param Input Source image file
     * @param Output Thumbnail file
     * @throws IOException Could not write image thumbnail file
     * @throws PdfException Could not open PDF document or extract thumbnail
     */
    public static void writeThumbnail(File Input, File Output) throws IOException, PdfException {
        if (isProcessableImage(Input)) {
            Thumbnails.of(Input).size(Config.THUMB_MAX_WIDTH, Config.THUMB_MAX_HEIGHT).toFile(Output);               
            _logger.log(Level.FINE,"Wrote thumbnail {0}", Output.getAbsolutePath());
        } else if (isPDF(Input)) {
            PDFUtils.WriteThumbnail(Input, Output, Config.THUMB_MAX_WIDTH, Config.THUMB_MAX_HEIGHT);
            _logger.log(Level.FINE,"Wrote thumbnail {0}", Output.getAbsolutePath());
        } else {
            _logger.log(Level.WARNING,"Could not write thumbnail {0}. Input file is not a processable image or PDF.", Output.getAbsolutePath());
        }
    }
    
} // end class
