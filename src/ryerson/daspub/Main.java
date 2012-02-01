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
import ryerson.daspub.mobile.MobilePublisher;
import ryerson.daspub.report.ReportPublisher;
import ryerson.daspub.slideshow.SlideshowPublisher;

/**
 * Command line interface to application components.
 * @author dmarques
 */
public class Main {

    private static final String CMD_CLEAN = "clean";
    private static final String CMD_CONFIG = "config";
    private static final String CMD_OUTPUT = "output";
    private static final String CMD_REPORT = "report";
    private static final String CMD_HELP = "help";
    private static final String CMD_LOG = "log";
    private static final String CMD_PUBLISH = "publish";
    private static final String OPTION_ARTIFACT = "artifact";
    private static final String OPTION_MOBILE = "mobile";
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
        executeCommands();
    }

    //--------------------------------------------------------------------------

    /**
     * Define the command line options.
     */
    private void defineCommandOptions() {
        options.addOption(CMD_CLEAN,true,"Delete all files and subfolders from specified directory");
        options.addOption(CMD_CONFIG,true,"Path to configuration file");
        options.addOption(CMD_HELP,false,"Show help message");
        options.addOption(CMD_LOG,true,"Path to output log file");
        options.addOption(CMD_OUTPUT,true,"Output path for results.");
        options.addOption(CMD_PUBLISH,true,"Publish archive content. Available options are mobile, artifact, slideshow.");
        options.addOption(CMD_REPORT,false,"Write archive status report.");
    }

    /**
     * Execute commands.
     */
    private void executeCommands() {
        // show help message
        if (cmd.hasOption(CMD_HELP)) {
            showHelpMessage();
            System.exit(0);
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
                System.exit(-1);
            }
        }
        // set configuration file
        if (cmd.hasOption(CMD_CLEAN)) {
            try {
                File path = new File(cmd.getOptionValue(CMD_CLEAN));
                if (path.exists()) {
                    FileUtils.deleteDirectory(path);
                }
                System.exit(-1);
            } catch (Exception ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE,"Could not clean output directories.\n\n{0}", stack);
                System.exit(-1);
            }
        }
        // generate status report
        if (cmd.hasOption(CMD_REPORT)) {
            // preconditions
            if (config == null) {
                logger.log(Level.SEVERE,"Configuration file must be specified.");
                System.exit(-1);
            }
            if (!cmd.hasOption(CMD_OUTPUT)) {
                logger.log(Level.SEVERE,"Output option must be specified.");
                System.exit(-1);
            }
            // process
            String output_path = cmd.getOptionValue(CMD_REPORT);
            String output = cmd.getOptionValue(CMD_OUTPUT);
            File outputPath = new File(output);
            try {
                ReportPublisher r = new ReportPublisher(config,outputPath);
                r.run();
            } catch (Exception ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE,"Could not generate report.\n\n{0}",stack);
                System.exit(-1);
            }
            System.exit(0);
        }
        // publish content to output directory
        if (cmd.hasOption(CMD_PUBLISH) && config != null) {
            // preconditions
            if (config == null) {
                logger.log(Level.SEVERE,"Configuration file must be specified.");
                System.exit(-1);
            }
            if (!cmd.hasOption(CMD_OUTPUT)) {
                logger.log(Level.SEVERE,"Output option must be specified.");
                System.exit(-1);
            }
            // process
            String option = cmd.getOptionValue(CMD_PUBLISH);
            String output = cmd.getOptionValue(CMD_OUTPUT);
            File outputPath = new File(output);
            try {
                if (option.equals(OPTION_MOBILE)) {
                    // publish mobile content application
                    MobilePublisher p = new MobilePublisher(config,outputPath);
                    p.run();
                } else if (option.equals(OPTION_ARTIFACT)) {
                    // publish artifact gallery and tags
                    ArtifactPublisher p = new ArtifactPublisher(config,outputPath);
                    p.run();
                    System.exit(0);
                } else if (option.equals(OPTION_SLIDESHOW)) {
                    // publish slideshow
                    SlideshowPublisher p = new SlideshowPublisher(config,outputPath);
                    p.run();
                    System.exit(0);                    
                }
            } catch (Exception ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE,"Could not publish gallery.\n\n{0}",stack);
                System.exit(-1);
            }                
            System.exit(0);
        }
    }

    /**
     * Program entry point
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Main m = new Main(args);
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
            System.out.println("Could not parse command line arguments.");
            System.out.println(ex.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Print the help message to standard output
     */
    private void showHelpMessage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java daspub.jar ",options,true);
    }

} // end class
