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

    private static PdfDecoder _pdf = new PdfDecoder(true);

    private static final Logger _logger = Logger.getLogger(PDFUtils.class.getName());
    
    //--------------------------------------------------------------------------

    public static void WriteImage() {
        
    }
    
    /**
     * Extract a thumbnail image of the first page in the PDF
     * @param Input Input file
     * @param Output Output directory
     * @param Width Maximum thumbnail width
     * @param Height Maximum thumbnail height
     * @param Multipage If document has multiple pages, write out a file for each page
     * @throws IOException
     * @throws PDFException
     */
    public static void WriteJPGImage(File Input, File Output, int Width, int Height, Boolean Multipage) throws PdfException, IOException {
        if (FilenameUtils.isExtension(Input.getName(),"pdf")) {
            String filename = FilenameUtils.removeExtension(Input.getName()) + ".jpg";
            File output = new File(Output,filename);
            // get first page of PDF as an image
            _pdf.openPdfFile(Input.getAbsolutePath());
            BufferedImage img = _pdf.getPageAsImage(1);
            _pdf.closePdfFile();
            // create and write a thumbnail image
            Thumbnails.of(img).size(Width,Height).outputFormat("jpg").toFile(output);
            _logger.log(Level.FINE,"Wrote PDF thumbnail {0}",Output.getAbsolutePath());
        } else {
            _logger.log(Level.WARNING,"Could not write PDF thumbnail for {0}. File is not a PDF document.",Input.getAbsolutePath());
        }
    }

} // end class
