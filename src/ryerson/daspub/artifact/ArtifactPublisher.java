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
import com.itextpdf.text.DocumentException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.sanselan.ImageReadException;
import org.jpedal.exception.PdfException;
import ryerson.daspub.Assignment;
import ryerson.daspub.Config;
import ryerson.daspub.Course;
import ryerson.daspub.Submission;
import ryerson.daspub.utility.FolderFileFilter;
import ryerson.daspub.utility.ImageUtils;

/**
 * Artifact tag and web gallery generator.
 */
public class ArtifactPublisher implements Runnable {

    private static final String TAG_SHEET_NAME = "artifact-tag-sheet.pdf";
    private Config _config = null;
    private String _inputPath = "";
    private String _outputPath = "";
    private File _input;
    private File _output;
    private String _artifact_template = "";
    
    private static final Logger _logger = Logger.getLogger(ArtifactPublisher.class.getName());

    //--------------------------------------------------------------------------
    /**
     * ArtifactTagGenerator constructor
     * @param Config Configuration
     * @param Output Output directory
     */
    public ArtifactPublisher(Config Config, File Output) throws Exception {
        _config = Config;
        _output = Output;
        loadTemplates();
    }

    //--------------------------------------------------------------------------
    
    /**
     * Clean the output directory.
     */
    private void cleanOutputDirectory() {
        try {
            if (_output.exists()) {
                Path dir = _output.toPath();
                Files.deleteIfExists(dir);
            }
            _output.mkdirs();
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            _logger.log(Level.SEVERE, "Could not delete {0}.\n\n{1}", 
                    new Object[]{_output.getAbsolutePath(),stack});
        }
    }

        /**
     * Generate QR code that encodes a URL
     * @param Url 
     * @param Dir
     * @param Filename
     * @throws IOException
     * @throws WriterException
     * @see http://www.copperykeenclaws.com/how-to-create-qr-codes-in-java/
     */
    private void writeQRCode(String Url, File Dir, String Filename) throws IOException, WriterException {
        File file = new File(Dir, Filename);
        // get a byte matrix for the data
        BitMatrix matrix = null;
        int h = Config.ARTIFACT_TAG_HEIGHT;
        int w = Config.ARTIFACT_TAG_WIDTH;
        com.google.zxing.Writer writer = new QRCodeWriter();
        // write code to image
        matrix = writer.encode(Url, com.google.zxing.BarcodeFormat.QR_CODE, w, h);
        MatrixToImageWriter.writeToFile(matrix, "PNG", file);
        _logger.log(Level.INFO,"Wrote QR code tag {0}.",file.getAbsolutePath());
    }

    /**
     * Load template files
     */
    private void loadTemplates() throws Exception {
        _artifact_template = FileUtils.readFileToString(new File(Config.ARTIFACT_TEMPLATE_PATH));
    }

    /**
     * Run 
     */
    @Override
    public void run() {
        // artifact gallery output path 
        File gallery = new File(_output, "artifact");
        // process each archive
        List<String> archives = Config.ARCHIVE_PATHS;
        Iterator<String> it = archives.iterator();
        File archive = null;
        while (it.hasNext()) {
            // input path
            String archivePath = it.next();
            archive = new File(archivePath);
            // process each assignment folder
            File[] dirs = archive.listFiles(new FolderFileFilter());
            for (int i = 0; i < dirs.length; i++) {
                Course c = new Course(dirs[i].getAbsolutePath());
                Iterator<Assignment> assignments = c.getAssignments();
                while (assignments.hasNext()) {
                    Assignment a = assignments.next();
                    Iterator<Submission> submissions = a.getSubmissions();
                    while (submissions.hasNext()) {
                        Submission sub = submissions.next();
                        File file = sub.getFile();
                        _logger.log(Level.FINE, "Generating artifact record for {0}", file.getAbsolutePath());
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
                            ImageUtils.writeJPGImage(file, new File(gallery, artifact_large_jpg), Config.IMAGE_MAX_WIDTH, Config.IMAGE_MAX_HEIGHT);
                            ImageUtils.writeJPGImage(file, new File(gallery, artifact_thumbnail_jpg), Config.THUMB_MAX_WIDTH, Config.THUMB_MAX_HEIGHT);
                            // write artifact page
                            String artifact_page = new String(_artifact_template);
                            FileUtils.write(new File(gallery, artifact_html), artifact_page);
                            // generate qr code and write to output folder
                            String url = Config.ARTIFACT_BASE_URL + "/" + artifact_html;
                            writeQRCode(url, gallery, artifact_qrcode_png);
                        } catch (ImageReadException | IOException | PdfException | WriterException ex) {
                            String stack = ExceptionUtils.getStackTrace(ex);
                            _logger.log(Level.WARNING, "Could not generate artifact record for {0}. Caught exception:\n\n{1}",
                                    new Object[]{file.getAbsolutePath(), stack});
                        }
                    }
                }
            }
        }
        // generate tag sheet
        try {
            QRCodeSheetPublisher ts = new QRCodeSheetPublisher(new File(_output,"artifact"));
            ts.writeTagSheet(_output);
        } catch (IOException | DocumentException ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            _logger.log(Level.SEVERE,"Could not complete generation of artifact tag sheet.\n\n{0}",stack);
        }
    }
    
} // end class
