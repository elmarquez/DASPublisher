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
package ryerson.daspub.artifact;

import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.sanselan.ImageReadException;
import org.jpedal.exception.PdfException;
import ryerson.daspub.Archive;
import ryerson.daspub.Assignment;
import ryerson.daspub.Config;
import ryerson.daspub.Course;
import ryerson.daspub.Program;
import ryerson.daspub.Submission;
import ryerson.daspub.utility.ImageUtils;
import ryerson.daspub.utility.PDFUtils;

/**
 * Artifact web gallery generator.
 */
public class ArtifactPublisher implements Runnable {

    private Config config = null;
    private File output;
    private String template = "";
    private static final Logger logger = Logger.getLogger(ArtifactPublisher.class.getName());

    //--------------------------------------------------------------------------
    
    /**
     * ArtifactTagGenerator constructor
     * @param Config Configuration
     * @param Output Output directory
     */
    public ArtifactPublisher(Config Config, File Output) throws Exception {
        config = Config;
        output = Output;
        template = FileUtils.readFileToString(new File(Config.ARTIFACT_TEMPLATE_PATH));
    }

    //--------------------------------------------------------------------------

    /**
     * Run 
     */
    @Override
    public void run() {
        // make the output directory if it does not exist
        if (!output.exists()) {
            output.mkdirs();
        }
        // generate static pages for each submission
        Iterator<Archive> ita = Archive.getArchives(Config.ARCHIVE_PATHS);
        while (ita.hasNext()) {
            Archive archive = ita.next();
            Iterator<Program> itp = archive.getPrograms();
            while (itp.hasNext()) {
                Program program = itp.next();
                Iterator<Course> itc = program.getCourses();
                while (itc.hasNext()) {
                    Course course = itc.next();
                    Iterator<Assignment> itas = course.getAssignments();
                    while (itas.hasNext()) {
                        Assignment assignment = itas.next();
                        Iterator<Submission> its = assignment.getSubmissions();
                        while (its.hasNext()) {
                            Submission submission = its.next();
                            processSubmission(submission,output);
                        }
                    }
                }
            }
        }
    }

    /**
     * Process the submission
     * @param S 
     * @param Output
     */
    private void processSubmission(Submission S,File Output) {
        File file = S.getFile();
        logger.log(Level.FINE, "Processing submission {0}", file.getAbsolutePath());
        try {
            // generate id for artifact
            long checksum = FileUtils.checksumCRC32(file); // TODO not sure if this is the right way to do this
            String id = String.valueOf(checksum);
            // set output file names
            String artifact_html = id + ".html";
            String artifact_large_jpg = id + ".jpg";
            String artifact_qrcode_png = id + "_qr.png";
            String artifact_screen_jpg = id + "_screen.jpg";
            String artifact_thumbnail_jpg = id + "_tn.jpg";
            // resize and write image to output folder
            if (FilenameUtils.isExtension(file.getName(),"pdf")) {
                PDFUtils.writeJPGImage(file,new File(Output,artifact_large_jpg),Config.IMAGE_MAX_WIDTH,Config.IMAGE_MAX_HEIGHT,false);
                PDFUtils.writeJPGImage(file,new File(Output,artifact_thumbnail_jpg),Config.THUMB_MAX_WIDTH,Config.THUMB_MAX_HEIGHT,false);
            } else {
                ImageUtils.writeJPGImage(file,new File(Output,artifact_large_jpg),Config.IMAGE_MAX_WIDTH,Config.IMAGE_MAX_HEIGHT);
                ImageUtils.writeJPGImage(file,new File(Output,artifact_thumbnail_jpg),Config.THUMB_MAX_WIDTH,Config.THUMB_MAX_HEIGHT);
            }
            // write artifact page
            String artifact_page = new String(template);
            FileUtils.write(new File(Output, artifact_html), artifact_page);
            // generate qr code and write to output folder
            String url = Config.ARTIFACT_BASE_URL + "/" + artifact_html;
            writeQRTag(url,output,artifact_qrcode_png);
        } catch (ImageReadException | IOException | PdfException | WriterException ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.WARNING, "Could not generate artifact record for {0}. Caught exception:\n\n{1}",
                    new Object[]{file.getAbsolutePath(), stack});
        }
    }

    /**
     * Write PNG image with QR code tag.
     * @param Url Embedded URL
     * @param Dir Output directory
     * @param Filename Filename
     * @throws IOException
     * @throws WriterException
     * @see http://www.copperykeenclaws.com/how-to-create-qr-codes-in-java/
     */
    private void writeQRTag(String Url, File Dir, String Filename) throws IOException, WriterException {
        File file = new File(Dir, Filename);
        // get a byte matrix for the data
        BitMatrix matrix = null;
        int h = Config.ARTIFACT_TAG_HEIGHT;
        int w = Config.ARTIFACT_TAG_WIDTH;
        com.google.zxing.Writer writer = new QRCodeWriter();
        // write code to image
        matrix = writer.encode(Url,com.google.zxing.BarcodeFormat.QR_CODE,w,h);
        MatrixToImageWriter.writeToFile(matrix, "PNG", file);
        logger.log(Level.INFO, "Wrote QR code tag {0}.", file.getAbsolutePath());
    }

} // end class
