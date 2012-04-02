/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ryerson.daspub.mobile;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.Config;
import ryerson.daspub.model.Assignment;
import ryerson.daspub.model.Submission;

/**
 *
 * @author dmarques
 */
public class AssignmentPresentation {
    
    private static String THUMBS = "thumbs/";
    
    private static final Logger logger = Logger.getLogger(AssignmentPresentation.class.getName());
    
    //--------------------------------------------------------------------------

    /**
     * HTML thumbnail index of Submissions matching the evaluation value E.
     * @param A Assignment
     * @param E Evaluation value
     * @return PhotoSwipe gallery index
     */
    public static String getHTMLSubmissionIndex(Assignment A, String E) {
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
                sb.append(A.getPathSafeName());
                sb.append("/");
                sb.append(s.getOutputFileName());
                sb.append("\" rel=\"external\"><img src=\"");
                sb.append(A.getPathSafeName());
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
        logger.log(Level.INFO, "Writing assignment folder {0}", A.getPath().getAbsolutePath());
        // create the output folder        
        Output.mkdirs();
        try {
            // load index page template file
            String template = FileUtils.readFileToString(new File(Config.ASSIGNMENT_TEMPLATE_PATH));
            // create assignment index page
            template = template.replace("${assignment.title}", A.getName());
            template = template.replace("${assignment.description}", A.getDescription());
            template = template.replace("${assignment.syllabus}", A.getSyllabusLink());
            // create thumbnails and scaled full size images
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
            // build index for high pass submissions
            String index = getHTMLSubmissionIndex(A,"high pass");
            template = template.replace("${assignment.highpass}",index);
            // build index for low pass submissions
            index = getHTMLSubmissionIndex(A,"low pass");
            template = template.replace("${assignment.lowpass}",index);
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
