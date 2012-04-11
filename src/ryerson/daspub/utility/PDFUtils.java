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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;

/**
 * PDF utilities
 * @author dmarques
 */
public class PDFUtils {

    private static PdfDecoder pdf = new PdfDecoder(true);
    private static final Logger logger = Logger.getLogger(PDFUtils.class.getName());
    
    //--------------------------------------------------------------------------

    /**
     * Get incremental file name
     * @param F File
     * @param I Index
     * @return 
     */
    private static File getIncrementedFileName(File F, int I) {
        String filename = FilenameUtils.getBaseName(F.getName());
        String extension = FilenameUtils.getExtension(F.getName());
        filename = filename + "-" + String.valueOf(I) + "." + extension;
        File file = new File(F.getParentFile(),filename);
        return file;
    }
    
    /**
     * Extract one or more thumbnail images from a PDF document.
     * @param Input Input file
     * @param Output Output file
     * @param Width Maximum thumbnail width
     * @param Height Maximum thumbnail height
     * @param Multipage If document has multiple pages, write out a file for each page. Pages are numbered sequentially, in the form filename-#.jpg
     * @return List of files written
     * @throws IOException
     * @throws PDFException
     */
    public static List<File> writeJPGImage(File Input, File Output, int Width, int Height, Boolean Multipage) throws PdfException, IOException {
        ArrayList<File> result = new ArrayList<>();
        if (FilenameUtils.isExtension(Input.getName(),"pdf")) {
            // open the file
            pdf.openPdfFile(Input.getAbsolutePath());
            // write images
            if (Multipage) {
                int pages = pdf.getPageCount();
                if (pages > 1) {
                    for (int i=0;i<pages;i++) {
                        BufferedImage img = pdf.getPageAsImage(i+1);
                        File output = getIncrementedFileName(Output,i+1);
                        logger.log(Level.INFO,"Writing JPEG image for {0}",output.getName());
                        Thumbnails.of(img).size(Width,Height).outputFormat("jpg").toFile(output);
                        result.add(output);
                    }
                } else if (pages == 1) {
                    BufferedImage img = pdf.getPageAsImage(1);
                    logger.log(Level.INFO,"Writing JPEG for {0}",Output.getName());
                    Thumbnails.of(img).size(Width,Height).outputFormat("jpg").toFile(Output);
                    result.add(Output);
                }
            } else {
                // get first page of PDF as an image
                BufferedImage img = pdf.getPageAsImage(1);
                // create and write a thumbnail image
                logger.log(Level.INFO,"Writing JPEG for {0}",Output.getName());
                Thumbnails.of(img).size(Width,Height).outputFormat("jpg").toFile(Output);
                result.add(Output);
            }
            // close the file
            pdf.closePdfFile();
        } else {
            logger.log(Level.WARNING,"Could not write PDF thumbnail for {0}. File is not a PDF document.",Input.getAbsolutePath());
        }
        // return file list
        return result;
    }

} // end class
