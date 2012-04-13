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
package ryerson.daspub.mobile;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.sanselan.ImageReadException;
import org.jpedal.exception.PdfException;
import ryerson.daspub.Config;
import ryerson.daspub.model.Assignment;
import ryerson.daspub.model.Submission;
import ryerson.daspub.utility.ImageUtils;
import ryerson.daspub.utility.PDFUtils;

/**
 * Assignment presentation.
 * @author dmarques
 */
public class AssignmentPage {
    
    private static String THUMBS = "thumbs";
    private static String HANDOUT = "handout.jpg";
    private static String HANDOUT_THUMB = "handout-thumb.jpg";
    
    private static final Logger logger = Logger.getLogger(AssignmentPage.class.getName());
    
    //--------------------------------------------------------------------------

    /**
     * HTML thumbnail index of document Submissions matching the evaluation 
     * value E.
     * @param A Assignment
     * @param E Evaluation value
     * @param Id Block identifier
     * @param Title Block title
     * @return PhotoSwipe gallery index
     */
    public static String buildDocumentSubmissionIndex(Assignment A, String E, String Id, String Title) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<script type=\"text/javascript\">");
        sb.append("\n\t$(document).ready(function(){");
        sb.append("\n\t\tvar myPhotoSwipe = $(\"#");
        sb.append(Id);
        sb.append(" a\").photoSwipe({ enableMouseWheel: false , enableKeyboard: false });");
        sb.append("\n\t\t});");
        sb.append("\n</script>");

        sb.append("<div data-role=\"collapsible\" data-collapsed=\"true\">");
        sb.append("\n\t<h3>");
        sb.append(Title);
        sb.append("</h3>");
        sb.append("\n<ul id=\"");
        sb.append(Id);
        sb.append("\" class=\"gallery\">");
        List<Submission> ls = A.getSubmissions();
        Iterator<Submission> its = ls.iterator();
        while (its.hasNext()) {
            Submission s = its.next();
            if (s.getEvaluation().toLowerCase().equals(E) &&
                s.getSourceFile().exists()) {
                sb.append("\n\t<li>");
                sb.append("<a href=\"");
                sb.append(A.getURLSafeName());
                sb.append("/");
                sb.append(s.getOutputFileName());
                sb.append("\" rel=\"external\"><img src=\"");
                sb.append(A.getURLSafeName());
                sb.append("/");
                sb.append(THUMBS);
                sb.append("/");
                sb.append(getThumbnailFileName(s));
                sb.append("\" alt=\"");
                sb.append(s.getAuthor());
                sb.append(" - ");
                sb.append(s.getEvaluation());
                sb.append("\" /></a></li>");
            }
        }
        sb.append("\n</ul>");
        // return result
        return sb.toString();
    }

    /**
     * HTML thumbnail index of course handout
     * @param A Assignment
     * @param Output Output folder
     * @return PhotoSwipe gallery index
     */
    public static String buildSyllabusIndex(Assignment A, File Output) {
        StringBuilder sb = new StringBuilder();
        if (A.hasSyllabusFile()) {
            // javascript for gallery
            sb.append("\n<script type=\"text/javascript\">");
            sb.append("\n\t$(document).ready(function(){");
            sb.append("\n\t\tvar myPhotoSwipe = $(\"#syllabus a\").photoSwipe({enableMouseWheel:false,enableKeyboard:false});");
            sb.append("\n\t\t});");
            sb.append("\n</script>");
            // gallery
            File fullname = new File(Output,HANDOUT);
            File thumbname = new File(Output,HANDOUT_THUMB);
            try {
                List<File> thumbs;
                List<File> images;
                thumbs = PDFUtils.writeJPGImage(A.getSyllabusFile(),
                                                thumbname,
                                                Config.THUMB_MAX_WIDTH,
                                                Config.THUMB_MAX_HEIGHT,
                                                true);
                images = PDFUtils.writeJPGImage(A.getSyllabusFile(),
                                                fullname,
                                                Config.IMAGE_MAX_WIDTH,
                                                Config.IMAGE_MAX_HEIGHT,
                                                true);
                // for each page of the syllabus, write out an index thumbnail
                String url = A.getURLSafeName();
                sb.append("\n<ul id=\"syllabus\" class=\"gallery\">");
                for (int i=0;i<images.size();i++) {
                    File f = images.get(i);
                    File t = thumbs.get(i);
                    sb.append("\n\t<li>");
                    sb.append("<a href=\"");
                    sb.append(url);
                    sb.append("/");
                    sb.append(f.getName());
                    sb.append("\" rel=\"external\"><img src=\"");
                    sb.append(url);
                    sb.append("/");
                    sb.append(t.getName());
                    sb.append("\" alt=\"");
                    sb.append("Page ");
                    sb.append(i + 1);
                    sb.append(" of ");
                    sb.append(images.size());
                    sb.append("\" /></a></li>");
                }
                sb.append("\n</ul>");            
            } catch (PdfException | IOException ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE, "Could not write handout gallery for {0}\n\n{1}", 
                        new Object[]{A.getFolder().getAbsolutePath(),stack});
            }
        }
        // return result
        return sb.toString();
    }    

    /**
     * HTML thumbnail index of video submissions matching the evaluation value 
     * E.
     * @param A Assignment
     * @param E Evaluation value
     * @param Id Block identifier
     * @param Title Block title
     * @return PhotoSwipe gallery index
     */
    public static String buildVideoSubmissionIndex(Assignment A, String E, String Id, String Title) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div data-role=\"collapsible\" data-collapsed=\"true\">");
        sb.append("\n\t<h3>");
        sb.append(Title);
        sb.append("</h3>");
        
        sb.append("\n<ul id=\"");
        sb.append(Id);
        sb.append("\" class=\"gallery\">");
        List<Submission> ls = A.getSubmissions();
        Iterator<Submission> its = ls.iterator();
        while (its.hasNext()) {
            Submission s = its.next();
            if (s.getEvaluation().toLowerCase().equals(E) &&
                s.getSourceFile().exists()) {
                sb.append("\n\t\t<li>");
                sb.append("<a href=\"");
                sb.append(A.getURLSafeName());
                sb.append("/");
                sb.append(s.getOutputFileName());
                sb.append("\" rel=\"external\"><img src=\"");
                sb.append(A.getURLSafeName());
                sb.append("/");
                sb.append(THUMBS);
                sb.append("/");
                sb.append(getThumbnailFileName(s));
                sb.append("\" alt=\"");
                sb.append(s.getAuthor());
                sb.append("\" /></a></li>");
            }
        }
        sb.append("\n\t</ul>");
        sb.append("\n</div>");
        // return result
        return sb.toString();
    }

    /**
     * Get thumbnail file name.
     * @param S Submission
     * @return 
     */
    public static String getThumbnailFileName(Submission S) {
        if (S.getSourceFile().exists()) {
            return ImageUtils.getJPGFileName(S.getSourceFile().getName(),"jpg");
        }
        return "";
    }

    /**
     * Write assignment page.
     * @param A Assignment
     * @param Output Output folder
     */
    public static void Write(Assignment A, File Output) {
        logger.log(Level.INFO, "Writing assignment folder {0}", A.getFolder().getAbsolutePath());
        // create the output folder        
        Output.mkdirs();
        try {
            // load template
            String data = FileUtils.readFileToString(new File(Config.ASSIGNMENT_TEMPLATE_PATH));
            // replace template variable fields
            data = data.replace("${title}", A.getName());
            data = data.replace("${description}", A.getDescription());
            if (A.hasSyllabusFile()) {
                data = data.replace("${syllabus}", buildSyllabusIndex(A,Output));
            } else {
                data = data.replace("${syllabus}", "\n<p>Assignment handout not available.</p>");
            }
            if (A.hasImageSubmissions()) {
                // build indicies for document submissions
                String index = buildDocumentSubmissionIndex(A,"high pass","imagesHighPass","Documents - High Pass");
                index += buildDocumentSubmissionIndex(A,"low pass","imagesLowPass","Documents - Low Pass");
                data = data.replace("${images}",index);
            }
            if (A.hasVideoSubmission()) {
                // build indicies for video submissions
                String index = buildVideoSubmissionIndex(A,"high pass","videoHighPass","Animations and Videos - High Pass");
                index += buildVideoSubmissionIndex(A,"low pass","videoLowPass","Animations and Videos - Low Pass");
                data = data.replace("${video}",index);
            }
            // write thumbnails and full size images to output directory
            writeImages(A,Output);
            // write html page
            File html = new File(Output,"index.html");
            FileUtils.write(html,data);
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE,"Could not write assignment {0} to {1}\n\n{2}",
                    new Object[]{A.getName(),Output,stack});
        }
    }
    
    /**
     * Write a JPG image of submission source file.
     * @param S Submission
     * @param Output Output file
     */
    private static void writeImage(Submission S, File Output) {
        logger.log(Level.INFO,"Writing full size image for {0}",S.getSourceFile().getName());
        if (!Output.exists()) {
            Output.mkdirs();
        }
        try {
            File imageOutput = new File(Output,getThumbnailFileName(S));
            if (FilenameUtils.isExtension(S.getSourceFile().getName(),"pdf")) {
                PDFUtils.writeJPGImage(S.getSourceFile(),imageOutput,Config.IMAGE_MAX_WIDTH,Config.IMAGE_MAX_HEIGHT,true);
            } else {
                ImageUtils.writeJPGImage(S.getSourceFile(),imageOutput,Config.IMAGE_MAX_WIDTH,Config.IMAGE_MAX_HEIGHT);
            }
        } catch (IOException | PdfException | ImageReadException ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE,"Could not write thumbnail {0}\n\n{1}",
                    new Object[]{Output.getAbsolutePath(),stack});
        }
    }

    /**
     * Create thumbnails and scaled full size images.
     * @param A Assignment 
     * @param Output Output folder
     */
    private static void writeImages(Assignment A, File Output) {
        File thumbnailOutput = new File(Output,THUMBS);
        List<Submission> ls = A.getSubmissions();
        Iterator<Submission> its = ls.iterator();
        while (its.hasNext()) {
            Submission s = its.next();
            if (s.getSourceFile().exists()) {
                writeImage(s,Output);
                writeThumbnail(s,thumbnailOutput);
            } else {
                logger.log(Level.WARNING,"Submission file {0} does not exist. Could not write images.",
                        s.getSourceFile().getAbsolutePath());
            }
        }          
    }

    /**
     * Write JPG thumbnail image of submission source file.
     * @param S Submission
     * @param Output Output file
     */
    private static void writeThumbnail(Submission S, File Output) {
        logger.log(Level.INFO,"Writing thumbnail image for {0}",S.getSourceFile().getName());
        if (!Output.exists()) {
            Output.mkdirs();
        }
        try {
            String filename = ImageUtils.getJPGFileName(S.getSourceFile().getName(),"jpg");
            File imageOutput = new File(Output,filename);
            if (FilenameUtils.isExtension(S.getSourceFile().getName(),"pdf")) {
                PDFUtils.writeJPGImage(S.getSourceFile(),imageOutput,Config.THUMB_MAX_WIDTH,Config.THUMB_MAX_HEIGHT,false);
            } else {
                ImageUtils.writeJPGImage(S.getSourceFile(),imageOutput,Config.THUMB_MAX_WIDTH,Config.THUMB_MAX_HEIGHT);
            }
        } catch (IOException | PdfException | ImageReadException ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE,"Could not write thumbnail {0}\n\n{1}",
                    new Object[]{Output.getAbsolutePath(),stack});
        }
    }

} // end class
