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
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.artifact.ArtifactPublisher;
import ryerson.daspub.artifact.QRCodeTagSheetPublisher;
import ryerson.daspub.init.Initializer;
import ryerson.daspub.mobile.MobilePublisher;
import ryerson.daspub.report.ReportPublisher;
import ryerson.daspub.slideshow.SlideshowPublisher;

/**
 * Command line interface to application components.
 * @author dmarques
 */
public class Main implements Runnable {

    public static final int FAIL = -1;
    public static final int SUCCESS = 0;
    
    private static final String CMD_CONFIG = "config";
    private static final String CMD_HELP = "help";
    private static final String CMD_INIT = "init";
    private static final String CMD_OUTPUT = "output";
    private static final String CMD_PUBLISH = "publish";

    private static final String OPTION_ARTIFACT = "artifact";
    private static final String OPTION_MOBILE = "mobile";
    private static final String OPTION_REPORT = "report";
    private static final String OPTION_SLIDESHOW = "slideshow";
    private static final String OPTION_TAGSHEET = "tagsheet";
    
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
        options.addOption(CMD_CONFIG, true, "Path to configuration file.");
        options.addOption(CMD_HELP, false, "Show help message.");
        options.addOption(CMD_INIT, true, "Initialize or update the archive folder(s) with requried metadata files.");
        options.addOption(CMD_OUTPUT, true, "Output path for published files.");
        options.addOption(CMD_PUBLISH, true, "Publish content. Available options are artifact, mobile, report, slideshow, tagsheet. Requires specification of output path.");
    }

    /**
     * Initialize the archive
     */
    private void executeInit() {
        // preconditions
        if (!cmd.hasOption(CMD_OUTPUT)) {
            logger.log(Level.SEVERE, "Output path must be specified");
            System.exit(FAIL);
        }
        // initialize archive
        try {
            File path = new File(cmd.getOptionValue(CMD_OUTPUT));
            Initializer init = new Initializer();
            init.run();
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE, "Could not complete initialization\n\n{0}", stack);
            System.exit(FAIL);
        }
    }

    /**
     * Publish content
     */
    private void executePublish() {
        // preconditions
        if (config == null) {
            logger.log(Level.SEVERE, "Configuration file must be specified");
            System.exit(FAIL);
        }
        if (!cmd.hasOption(CMD_OUTPUT)) {
            logger.log(Level.SEVERE, "Output path must be specified");
            System.exit(FAIL);
        }
        // publish
        String option = cmd.getOptionValue(CMD_PUBLISH);
        String output = cmd.getOptionValue(CMD_OUTPUT);
        File outputPath = new File(output);
        try {
            switch (option) {
                case OPTION_ARTIFACT: {
                    ArtifactPublisher p = new ArtifactPublisher(config, outputPath);
                    p.run();
                    break;
                }
                case OPTION_MOBILE: {
                    MobilePublisher p = new MobilePublisher(config, outputPath);
                    p.run();
                    break;
                }
                case OPTION_REPORT: {
                    ReportPublisher p = new ReportPublisher(config, outputPath);
                    p.run();
                    break;
                }
                case OPTION_SLIDESHOW: {
                    SlideshowPublisher p = new SlideshowPublisher(config, outputPath);
                    p.run();
                    break;
                }
                case OPTION_TAGSHEET: {
                    QRCodeTagSheetPublisher p = new QRCodeTagSheetPublisher(config, outputPath, outputPath);
                    p.run();
                    break;
                }
            }
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE, "Could not complete publication\n\n{0}", stack);
            System.exit(FAIL);
        }
        // exit
        System.exit(SUCCESS);
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
            cmd = parser.parse(options, args);
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE, "Could not parse command line arguments\n\n{0}", stack);
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
        // load configuration
        if (cmd.hasOption(CMD_CONFIG)) {
            try {
                String path = cmd.getOptionValue(CMD_CONFIG);
                File configfile = new File(path);
                config = new Config();
                config.load(configfile);
            } catch (Exception ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE, "Could not parse configuration file\n\n{0}", stack);
                System.exit(FAIL);
            }
        }
        // execute commands
        if (cmd.hasOption(CMD_INIT)) {
            executeInit();
        } else if (cmd.hasOption(CMD_PUBLISH)) {
            executePublish();
        }
    }

    /**
     * Print the help message to standard output
     */
    private void showHelpMessage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java DASPub.jar ", options, true);
    }
    
} // end class
