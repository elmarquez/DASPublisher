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

import com.itextpdf.text.pdf.PdfException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;

/**
 * Image processing utility methods
 * @author dmarques
 * @see http://im4java.sourceforge.net/
 */
public class ImageUtils {
    
    private static final Logger logger = Logger.getLogger(ImageUtils.class.getName());

    //--------------------------------------------------------------------------

    /**
     * Change the file name so that it has the new extension
     * @param Filename Current file name
     * @param Extension New extension to replace current filename extension
     * @return 
     */
    public static String getJPGFileName(String Filename, String Extension) {
        String name = FilenameUtils.removeExtension(Filename);
        return name + "." + Extension;
    }
    
    /**
     * Generate a new file name
     * When converting between image encoding types, the filename extension
     * @param File
     * @param NewExtension
     * @return 
     */
    public static File getJPGFileName(File Input, String NewExtension) {
        String filename = FilenameUtils.removeExtension(Input.getName());
        filename = filename + "." + NewExtension;
        return new File(Input.getParentFile(),filename);
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
     * Convert input file, resize and write JPG output image.  If the input file
     * has multiple pages, write enumerated output files.
     * @param Input Input file
     * @param Output Output file
     * @param Width Maximum width
     * @param Height Maximum height
     * @throws IOException
     * @throws PdfException 
     * TODO there is a problem here when resizing
     * TODO check the original image size and don't go any higher than the original
     */
    public static void writeJPGImage(File Input, File Output, int Width, int Height) throws IOException, ImageReadException, org.jpedal.exception.PdfException {
        if (FilenameUtils.isExtension(Input.getName(),"jpg")) {
            Thumbnails.of(Input).size(Width,Height).toFile(Output);
            logger.log(Level.FINE,"Wrote image {0}", Output.getAbsolutePath());
        } else if (FilenameUtils.isExtension(Input.getName(),"gif") ||
                   FilenameUtils.isExtension(Input.getName(),"png")) {
            Thumbnails.of(Input).size(Width,Height).outputFormat("jpg").toFile(Output);
            logger.log(Level.FINE,"Wrote image {0}", Output.getAbsolutePath());
        } else if (FilenameUtils.isExtension(Input.getName(),"tif")) {
            BufferedImage image = Sanselan.getBufferedImage(Input);
            Thumbnails.of(image).size(Width,Height).toFile(Output);
        } else if (FilenameUtils.isExtension(Input.getName(),"pdf")) {
            // TODO not sure if this should be removed
            PDFUtils.writeJPGImage(Input,Output,Width,Height,false);
            logger.log(Level.FINE,"Wrote image {0}", Output.getAbsolutePath());
        } else if (FilenameUtils.isExtension(Input.getName(),"mp4")) {
            // TODO not sure if this should be removed, create a VideoUtils class
            logger.log(Level.WARNING,"Image writing for MP4 type not implemented yet");
        } else {
            logger.log(Level.WARNING,"Could not write image for {0}. File is not processable.", Output.getAbsolutePath());                
        }
    }

} // end class
