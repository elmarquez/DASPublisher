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
import org.apache.commons.lang3.exception.ExceptionUtils;
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
     * Get incremental file name
     * @param F File
     * @param I Index
     * @param E Extension
     * @return 
     */
    private static File getIncrementedFileName(File F, int I, String E) {
        String filename = FilenameUtils.getBaseName(F.getName());
        String extension = FilenameUtils.getExtension(F.getName());
        filename = filename + "-" + String.valueOf(I) + "." + E;
        File file = new File(F.getParentFile(),filename);
        return file;
    }

    /**
     * Get page count for PDF document.
     * @param Input
     * @return 
     */
    public static int getPageCount(File Input) {
        int count = 0;
        try {
            pdf.openPdfFile(Input.getAbsolutePath());
            count = pdf.getPageCount();
            pdf.closePdfFile();
        } catch (PdfException ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE,
                       "Could not get page count for {0}\n\n{1}", 
                       new Object[] {Input.getAbsolutePath(),stack});
        }
        return count;
    }
    
    /**
     * Write JPG image of page in a PDF document.  If the document has multiple
     * pages, only the first page image will be written.
     * @param Input PDF input file
     * @param Output Output file
     * @param Width Maximum thumbnail width
     * @param Height Maximum thumbnail height
     * @throws IOException
     * @throws PDFException
     */
    public static List<File> writeJPGImage(File Input, File Output, int Width, int Height) throws PdfException, IOException {
        ArrayList<File> files = new ArrayList<>();
        if (FilenameUtils.isExtension(Input.getName(),"pdf")) {
            // if output is a directory, change it 
            File output = Output;
            if (output.isDirectory()) {
                output = new File(output,Input.getName());
            }
            // if output extension is not jpg, change it
            if (!FilenameUtils.isExtension(output.getName(),"jpg")) {
                String basename = FilenameUtils.getBaseName(output.getName()) + ".jpg";
                output = new File(output.getParentFile(),basename);
            }
            // write jpg image
            pdf.openPdfFile(Input.getAbsolutePath());
            BufferedImage img = pdf.getPageAsImage(1);
            logger.log(Level.INFO,"Writing {0}",output.getName());
            Thumbnails.of(img).size(Width,Height).outputFormat("jpg").toFile(output);
            files.add(output);
            pdf.closePdfFile();
        } else {
            logger.log(Level.WARNING,"Could not write PDF thumbnail for {0}. File is not a PDF document.",Input.getAbsolutePath());
        }
        // return result
        return files;
    }
    
    /**
     * Write a JPG image of a specific page in a PDF document. If the page 
     * number does not exist in the document, nothing will be written.
     * @param Input PDF input file
     * @param Output Output file
     * @param Width Maximum thumbnail width
     * @param Height Maximum thumbnail height
     * @param Page Page number. Zero based page index.
     */
    public static void writeJPGImage(File Input, File Output, int Width, int Height, int Page) throws PdfException, IOException {
        if (FilenameUtils.isExtension(Input.getName(),"pdf")) {
            pdf.openPdfFile(Input.getAbsolutePath());
            if (Page < pdf.getPageCount()) {
                BufferedImage img = pdf.getPageAsImage(Page+1); // PDF page index starts at 1
                logger.log(Level.INFO,"Writing {0}", Output.getName());
                Thumbnails.of(img).size(Width,Height).outputFormat("jpg").toFile(Output);
            } else {
                logger.log(Level.WARNING,
                           "Could not write PDF thumbnail for {0}. Requested page number does not exist.",
                           Input.getAbsolutePath());
            }
            pdf.closePdfFile();
        } else {
            logger.log(Level.WARNING,"Could not write PDF thumbnail for {0}.",Input.getAbsolutePath());
        }
    }

    /**
     * Write JPG image of each document page.
     * @param Input PDF input file
     * @param Output Output folder or output file name.
     * @param Width Maximum thumbnail width
     * @param Height Maximum thumbnail height
     * @throws PdfException
     * @throws IOException 
     */
    public static List<File> writeJPGImageAllPDFPages(File Input, File Output, int Width, int Height) throws PdfException, IOException {
        ArrayList<File> files = new ArrayList<>();
        if (FilenameUtils.isExtension(Input.getName(),"pdf")) {
            pdf.openPdfFile(Input.getAbsolutePath());
            int count = pdf.getPageCount();
            for (int i=0;i<count;i++) {
                BufferedImage img = pdf.getPageAsImage(i+1); // PDF page index starts at 1
                File output = getIncrementedFileName(Output,i,"jpg");
                logger.log(Level.INFO,"Writing {0}",output.getName());
                Thumbnails.of(img).size(Width,Height).outputFormat("jpg").toFile(output);
                files.add(output);
            } 
            pdf.closePdfFile();
        } else {
            logger.log(Level.WARNING,"Could not write JPG for PDF {0}.",Input.getAbsolutePath());
        }
        // return result
        return files;
    }

} // end class
