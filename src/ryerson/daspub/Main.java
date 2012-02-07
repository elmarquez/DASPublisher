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

package ryerson.daspub;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.artifact.ArtifactPublisher;
import ryerson.daspub.artifact.QRTagSheetPublisher;
import ryerson.daspub.mobile.MobilePublisher;
import ryerson.daspub.report.ReportPublisher;
import ryerson.daspub.slideshow.SlideshowPublisher;

/**
 * Command line interface to application components.
 * 
 * Main commands:
 * -config c:/path/to/config/file.txt  Set configuration from file
 * -help show help message
 * -publish artifact,gallery,mobile,report -output c:/path/to/output/folder
 *
 * Options:
 * -clean clean output directory before publishing 
 * -log c:/path/to/optional/output/log/file.txt write output messages to log file
 * TODO what is the result of -clean -publish report ??? kills the output directory ... should kill report file instead
 * @author dmarques
 */
public class Main implements Runnable {

    private static final int FAIL = -1;
    private static final int SUCCESS = 0;
    
    private static final String CMD_CLEAN = "clean";
    private static final String CMD_CONFIG = "config";
    private static final String CMD_OUTPUT = "output";
    private static final String CMD_REPORT = "report";
    private static final String CMD_HELP = "help";
    private static final String CMD_LOG = "log";
    private static final String CMD_PUBLISH = "publish";

    private static final String OPTION_ARTIFACT = "artifact";
    private static final String OPTION_MOBILE = "mobile";
    private static final String OPTION_REPORT = "report";
    private static final String OPTION_SLIDESHOW = "slideshow";

    private static Options options = new Options();
    private static Config config = null;
    private static CommandLine cmd = null;

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    //--------------------------------------------------------------------------

    /**
     * Main constructor
     * @param args Arguments
     */
    public Main(String[] args) {
        defineCommandOptions();
        parseArguments(args);
    }

    //--------------------------------------------------------------------------

    /**
     * Define the command line options.
     */
    private void defineCommandOptions() {
        options.addOption(CMD_CLEAN,false,"Delete all files and subfolders from the output path before publishing.");
        options.addOption(CMD_CONFIG,true,"Path to configuration file.");
        options.addOption(CMD_HELP,false,"Show help message.");
        options.addOption(CMD_LOG,true,"Path for log file.");
        options.addOption(CMD_OUTPUT,true,"Output path.");
        options.addOption(CMD_PUBLISH,true,"Publish content. Available options are artifact, mobile, report, slideshow. Requires specification of output path.");
    }

    /**
     * Program entry point
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Main m = new Main(args);
        m.run();
    }

    /**
     * Parse command line arguments
     * @param args
     */
    private void parseArguments(String[] args) {
        CommandLineParser parser = new PosixParser();
        try {
            cmd = parser.parse(options,args);
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE,"Could not parse command line arguments.\n\n{0}",stack);
            System.exit(FAIL);
        }
    }

    /**
     * Execute commands.
     */
    public void run() {
        // show help message
        if (cmd.hasOption(CMD_HELP)) {
            showHelpMessage();
            System.exit(SUCCESS);
        }
        // set configuration file
        if (cmd.hasOption(CMD_CONFIG)) {
            try {
                String path = cmd.getOptionValue(CMD_CONFIG);
                File configfile = new File(path);
                config = new Config(configfile);
            } catch (Exception ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE,"Could not parse configuration file.\n\n{0}", stack);
                System.exit(FAIL);
            }
        }
        // clean output directory
        if (cmd.hasOption(CMD_CLEAN)) {
            if (!cmd.hasOption(CMD_OUTPUT)) {
                logger.log(Level.SEVERE,"Output path must be specified.");
                System.exit(FAIL);
            }
            try {
                File path = new File(cmd.getOptionValue(CMD_OUTPUT));
                if (path.exists()) {
                    logger.log(Level.INFO,"Cleaning output directory {0}.", path.getAbsolutePath());
                    FileUtils.deleteDirectory(path);
                }
            } catch (Exception ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE,"Could not clean output directory.\n\n{0}", stack);
                System.exit(FAIL);
            }
        }
        // publish content to output directory
        if (cmd.hasOption(CMD_PUBLISH) && config != null) {
            // preconditions
            if (config == null) {
                logger.log(Level.SEVERE,"Configuration file must be specified.");
                System.exit(FAIL);
            }
            if (!cmd.hasOption(CMD_OUTPUT)) {
                logger.log(Level.SEVERE,"Output path must be specified.");
                System.exit(FAIL);
            }
            // process
            String option = cmd.getOptionValue(CMD_PUBLISH);
            String output = cmd.getOptionValue(CMD_OUTPUT);
            File outputPath = new File(output);
            try {
                switch (option) {
                    case OPTION_ARTIFACT: {
                            ArtifactPublisher ap = new ArtifactPublisher(config,outputPath);
                            QRTagSheetPublisher qp = new QRTagSheetPublisher(outputPath,new File(output,"artifact-tagsheet.pdf"));
                            ap.run();
                            qp.run();
                            break;
                        }
                    case OPTION_MOBILE: {
                            MobilePublisher mp = new MobilePublisher(config,outputPath);
                            mp.run();
                            break;
                        }
                    case OPTION_REPORT: {
                            ReportPublisher rp = new ReportPublisher(config,outputPath);
                            rp.run();
                            break;
                        }
                    case OPTION_SLIDESHOW: {
                            SlideshowPublisher sp = new SlideshowPublisher(config,outputPath);
                            sp.run();
                            break;
                        }
                }
            } catch (Exception ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE,"Could not complete publication.\n\n{0}",stack);
                System.exit(FAIL);
            }
        }
    }

    /**
     * Print the help message to standard output
     */
    private void showHelpMessage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java DASPub.jar ",options,true);
    }

} // end class
