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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.model.Archive;
import ryerson.daspub.model.Assignment;
import ryerson.daspub.Config;
import ryerson.daspub.model.Course;
import ryerson.daspub.model.Program;
import ryerson.daspub.model.Submission;
import ryerson.daspub.utility.ImageUtils;
import ryerson.daspub.utility.PDFUtils;

/**
 * Artifact web gallery generator.
 */
public class PublishArtifactPagesTask implements Runnable {

    private Config config = null;
    private File output;
    private String template = "";

    private File largeDir;
    private File mediumDir;
    private File smallDir;
    private File qrDir;

    private static final Logger logger = Logger.getLogger(PublishArtifactPagesTask.class.getName());

    //--------------------------------------------------------------------------

    /**
     * ArtifactTagGenerator constructor
     * @param Configuration Configuration
     */
    public PublishArtifactPagesTask(Config Configuration) {
        config = Configuration;
        output = new File(Config.OUTPUT_ARTIFACT_PAGES_PATH);
        largeDir = new File(output,"large");
        mediumDir = new File(output,"medium");
        smallDir = new File(output,"small");
        qrDir = new File(output,"qr");
        template = config.getArtifactTemplate();
    }

    //--------------------------------------------------------------------------

    /**
     * Run
     */
    @Override
    public void run() {
        logger.log(Level.INFO,"STARTING publish artifact pages task");
        // make the output directory if it does not exist
        if (!output.exists()) {
            output.mkdirs();
        }
        // make all subdirectories
        largeDir.mkdirs();
        mediumDir.mkdirs();
        smallDir.mkdirs();
        qrDir.mkdirs();
        // generate static pages for each submission
        List<Archive> archives = Archive.getArchives(Config.ARCHIVE_PATHS);
        Iterator<Archive> ita = archives.iterator();
        while (ita.hasNext()) {
            Archive archive = ita.next();
            Iterator<Program> itp = archive.getPrograms();
            while (itp.hasNext()) {
                Program program = itp.next();
                Iterator<Course> itc = program.getCourses();
                while (itc.hasNext()) {
                    Course course = itc.next();
                    List<Assignment> la = course.getAssignments();
                    Iterator<Assignment> itas = la.iterator();
                    while (itas.hasNext()) {
                        Assignment assignment = itas.next();
                        List<Submission> ls = assignment.getSubmissions();
                        Iterator<Submission> its = ls.iterator();
                        while (its.hasNext()) {
                            Submission submission = its.next();
                            if (submission.hasSourceFile()) {
                                processSubmission(submission, output);
                            }
                        }
                    }
                }
            }
        }
        logger.log(Level.INFO,"DONE publish artifact pages task");
    }

    /**
     * Process the submission
     * @param S
     * @param Output
     */
    private void processSubmission(Submission S, File Output) {
        File input = S.getSourceFile();
        String id = S.getId();
        if (id != null) {
            logger.log(Level.FINE, "Processing submission {0}", input.getAbsolutePath());
            try {
                // set output file names
                String artifact_html = id + ".php";
                String medium_jpg = id + ".jpg";
                String large_jpg = id + ".jpg";
                String qrcode_png = id + ".png";
                String thumbnail_jpg = id + ".jpg";
                // resize and write image to output folder
                if (S.isPDF()) {
                    PDFUtils.writeJPGImage(input, new File(smallDir, thumbnail_jpg), Config.THUMB_MAX_WIDTH, Config.THUMB_MAX_HEIGHT);
                    PDFUtils.writeJPGImage(input, new File(mediumDir, medium_jpg), Config.ARTIFACT_PREVIEW_MAX_WIDTH, Config.ARTIFACT_PREVIEW_MAX_HEIGHT);
                    PDFUtils.writeJPGImage(input, new File(largeDir, large_jpg), Config.IMAGE_MAX_WIDTH, Config.IMAGE_MAX_HEIGHT);
                } else if (S.isImage()) {
                    ImageUtils.writeJPGImage(input, new File(smallDir, thumbnail_jpg), Config.THUMB_MAX_WIDTH, Config.THUMB_MAX_HEIGHT);
                    ImageUtils.writeJPGImage(input, new File(mediumDir, medium_jpg), Config.ARTIFACT_PREVIEW_MAX_WIDTH, Config.ARTIFACT_PREVIEW_MAX_HEIGHT);
                    ImageUtils.writeJPGImage(input, new File(largeDir, large_jpg), Config.IMAGE_MAX_WIDTH, Config.IMAGE_MAX_HEIGHT);
                } else if (S.isVideo()) {
                    // not implemented yet
                }
                // substitute artifact page template values
                String page = new String(template);
                page = page.replace("${imageMedium}", medium_jpg);
                page = page.replace("${imageLarge}", large_jpg);
                page = page.replace("${year}", S.getYear());
                page = page.replace("${semester}", S.getSemester());
                page = page.replace("${courseNumber}", S.getCourseNumber());
                page = page.replace("${courseName}", S.getCourseName());
                page = page.replace("${studioMaster}", S.getStudioMaster());
                page = page.replace("${instructor}", S.getInstructor());
                page = page.replace("${assignmentName}", S.getAssignmentName());
                page = page.replace("${assignmentDuration}", S.getAssignmentDuration());
                page = page.replace("${studentName}", S.getStudentName());
                page = page.replace("${submissionId}", S.getId());
                String evaluation = "None";
                if (S.getEvaluation() == Config.SUBMISSION_EVALUATION.HIGH_PASS) {
                    evaluation = "High Pass";
                } else if (S.getEvaluation() == Config.SUBMISSION_EVALUATION.LOW_PASS) {
                    evaluation = "Low Pass";
                }
                page = page.replace("${evaluation}", evaluation);
                String caption = S.getAssignmentName() + " - " +
                                 S.getStudentName() + ", " +
                                 S.getEvaluation();
                page = page.replace("${caption}", caption);
                // write page
                File artifactPageFile = new File(Output, artifact_html);
                logger.log(Level.INFO,"Writing artifact page \"{0}\"",artifactPageFile.getAbsolutePath());
                FileUtils.write(artifactPageFile, page);
                // generate qr code and write to output folder
                String url = Config.ARTIFACT_BASE_URL + "/" + artifact_html;
                writeQRTag(url, qrDir, qrcode_png);
            } catch (Exception ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.WARNING, "Could not generate artifact record for {0}. Caught exception:\n\n{1}",
                        new Object[]{input.getAbsolutePath(), stack});
            }
        } else {
            logger.log(Level.WARNING, "Could not create artifact page for {0}. Submission ID not available.", input.getAbsolutePath());
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
        logger.log(Level.INFO, "Writing QR tag image \"{0}\"", file.getAbsolutePath());
        matrix = writer.encode(Url, com.google.zxing.BarcodeFormat.QR_CODE, w, h);
        MatrixToImageWriter.writeToFile(matrix, "PNG", file);
    }

} // end class
