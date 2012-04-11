/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ryerson.daspub.mobile;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jpedal.exception.PdfException;
import ryerson.daspub.Config;
import ryerson.daspub.model.Assignment;
import ryerson.daspub.model.Submission;
import ryerson.daspub.utility.PDFUtils;

/**
 * Assignment presentation.
 * @author dmarques
 */
public class AssignmentPresentation {
    
    private static String THUMBS = "thumbs/";
    private static String HANDOUT = "handout.jpg";
    private static String HANDOUT_THUMB = "handout-thumb.jpg";
    
    private static final Logger logger = Logger.getLogger(AssignmentPresentation.class.getName());
    
    //--------------------------------------------------------------------------

    /**
     * HTML thumbnail index of course handout
     * @param A Assignment
     * @param Output Output folder
     * @return PhotoSwipe gallery index
     */
    public static String buildHandoutIndex(Assignment A, File Output) {
        StringBuilder sb = new StringBuilder();
        if (A.hasSyllabusFile()) {
            // javascript for gallery
            sb.append("\n<script type=\"text/javascript\">");
            sb.append("\n\t$(document).ready(function(){");
            sb.append("\n\t\tvar myPhotoSwipe = $(\"#syllabus a\").photoSwipe({ enableMouseWheel: false , enableKeyboard: false });");
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
                sb.append("\n<ul id=\"syllabus\" class=\"gallery\">");
                for (int i=0;i<images.size();i++) {
                    File f = images.get(i);
                    File t = thumbs.get(i);
                    sb.append("\n\t<li>");
                    sb.append("<a href=\"");
                    sb.append(f.getName());
                    sb.append("\" rel=\"external\"><img src=\"");
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
     * HTML thumbnail index of document Submissions matching the evaluation 
     * value E.
     * @param A Assignment
     * @param E Evaluation value
     * @return PhotoSwipe gallery index
     */
    public static String buildDocumentSubmissionIndex(Assignment A, String E) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<ul id=\"Gallery\" class=\"gallery\">");
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
                sb.append(s.getThumbnailFileName());
                sb.append("\" alt=\"");
                sb.append(s.getAuthor());
                sb.append("\" /></a></li>");
            }
        }
        sb.append("\n</ul>");
        // return result
        return sb.toString();
    }

    /**
     * HTML thumbnail index of video submissions matching the evaluation value 
     * E.
     * @param A Assignment
     * @param E Evaluation value
     * @return PhotoSwipe gallery index
     */
    public static String buildVideoSubmissionIndex(Assignment A, String E) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n<ul id=\"Gallery\" class=\"gallery\">");
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
                sb.append(s.getThumbnailFileName());
                sb.append("\" alt=\"");
                sb.append(s.getAuthor());
                sb.append("\" /></a></li>");
            }
        }
        sb.append("\n</ul>");
        // return result
        return sb.toString();
    }

    /**
     * Write HTML output
     * @param A Assignment
     * @param Output Output folder
     */
    public static void Write(Assignment A, File Output) {
        logger.log(Level.INFO, "Writing assignment folder {0}", A.getFolder().getAbsolutePath());
        // create the output folder        
        Output.mkdirs();
        try {
            // load index page template file
            String template = FileUtils.readFileToString(new File(Config.ASSIGNMENT_TEMPLATE_PATH));
            // create assignment index page
            template = template.replace("${title}", A.getName());
            template = template.replace("${description}", A.getDescription());
            if (A.hasSyllabusFile()) {
                template = template.replace("${syllabus}", buildHandoutIndex(A,Output));
            } else {
                template = template.replace("${syllabus}", "\n<p>Assignment handout not available.</p>");
            }
            // create thumbnails and scaled full size images
            // @TODO move this to buildSubmissionIndex above
            File thumbnailOutputPath = new File(Output,THUMBS);
            List<Submission> ls = A.getSubmissions();
            Iterator<Submission> its = ls.iterator();
            while (its.hasNext()) {
                Submission s = its.next();
                if (s.getSourceFile().exists()) {
                    s.writeImage(Output);
                    s.writeThumbnail(thumbnailOutputPath);
                } else {
                    logger.log(Level.WARNING,"Submission file {0} does not exist. Could not write images.",
                            s.getSourceFile().getAbsolutePath());
                }
            }
            // build indicies for document submissions
            String index = buildDocumentSubmissionIndex(A,"high pass");
            template = template.replace("${document.highpass}",index);
            index = buildDocumentSubmissionIndex(A,"low pass");
            template = template.replace("${document.lowpass}",index);
            // build indicies for video submissions
            index = buildVideoSubmissionIndex(A,"high pass");
            template = template.replace("${video.highpass}",index);
            index = buildVideoSubmissionIndex(A,"low pass");
            template = template.replace("${video.lowpass}",index);
            // write html file
            File html = new File(Output,"index.html");
            FileUtils.write(html,template);
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE,"Could not write assignment {0} to {1}\n\n{2}",
                    new Object[]{A.getName(),Output,stack});
        }
    }

} // end class
