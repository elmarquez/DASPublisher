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
     * Get incremented file name.
     * @param Filename File name
     * @param Increment File name increment
     * @return
     */
    public static String getJPGFileName(String Filename, int Increment) {
        String name = FilenameUtils.removeExtension(Filename);
        return name + "-" + String.valueOf(Increment) + ".jpg";
    }

    /**
     * Resize image
     * @param Input Input file
     * @param Width Output width
     * @param Height Output height
     * @return Resized image as a byte array
     * @throws IOException
     */
    public static byte[] resizeImageToByteArray(File Input, int Width, int Height)
           throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Thumbnails.of(Input).size(Width,Height).toOutputStream(baos);
        return baos.toByteArray();
    }

    /**
     * Convert input file, resize and write JPG output image.
     * @param Input Input file
     * @param Output Output file or folder
     * @param Width Maximum width
     * @param Height Maximum height
     * @throws IOException
     * @throws PdfException
     * @TODO there is a problem here when resizing
     * @TODO check the original image size and don't go any higher than the original
     */
    public static void writeJPGImage(File Input, File Output, int Width, int Height)
           throws IOException, ImageReadException, org.jpedal.exception.PdfException {
        // if output is folder, create a new file object
        File output = Output;
        if (output.isDirectory()) {
            output = new File(Output,Input.getName());
        }
        // write image
        if (FilenameUtils.isExtension(Input.getName(),"jpg")) {
            Thumbnails.of(Input).size(Width,Height).toFile(output);
            logger.log(Level.FINE,"Wrote image {0}", output.getAbsolutePath());
        } else if (FilenameUtils.isExtension(Input.getName(),"gif") ||
                   FilenameUtils.isExtension(Input.getName(),"png")) {
            Thumbnails.of(Input).size(Width,Height).outputFormat("jpg").toFile(output);
            logger.log(Level.FINE,"Wrote image {0}", output.getAbsolutePath());
        } else if (FilenameUtils.isExtension(Input.getName(),"tif")) {
            BufferedImage image = Sanselan.getBufferedImage(Input);
            Thumbnails.of(image).size(Width,Height).toFile(output);
        } else {
            logger.log(Level.WARNING,
                       "Could not write JPG for {0}. File is not a processable image.",
                       output.getAbsolutePath());
        }
    }

} // end class
