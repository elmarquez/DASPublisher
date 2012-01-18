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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import ryerson.daspub.artifact.ArtifactPublisher;
import ryerson.daspub.mobile.MobilePublisher;
import ryerson.daspub.report.ReportPublisher;
import ryerson.daspub.slideshow.SlideshowPublisher;

/**
 * Command line interface to application components.
 * @author dmarques
 */
public class Main {

    private static final String CMD_CONFIG = "config";
    private static final String CMD_REPORT = "report";
    private static final String CMD_HELP = "help";
    private static final String CMD_PUBLISH_ARTIFACT = "artifact";
    private static final String CMD_PUBLISH_MOBILE = "mobile";
    private static final String CMD_PUBLISH_SLIDESHOW = "slideshow";
    private static final String CMD_WINDOW = "window";

    private static Options options = new Options();
    private static Config config = null;
    private static CommandLine cmd = null;

    private static File input = null;
    private static List<File> inputs = null;
    private static File output = null;
    private static File report = null;

    private static final Logger _logger = Logger.getLogger(Main.class.getName());

    //--------------------------------------------------------------------------

    /**
     * Main constructors
     * @param args Arguments
     */
    public Main(String[] args) {
        // define command line options
        defineCommandOptions();
        // parse command line options
        parseArguments(args);
        // execute commands
        executeCommands();
    }

    //--------------------------------------------------------------------------

    /**
     * Define the command line options.
     */
    private void defineCommandOptions() {
        options.addOption(CMD_CONFIG,true,"Set configuration file");
        options.addOption(CMD_HELP,false,"Show help message");
        options.addOption(CMD_PUBLISH_MOBILE,true,"Publish mobile gallery to specified folder");
        options.addOption(CMD_PUBLISH_ARTIFACT,true,"Publish artifact gallery and tag sheets to specified folder");
        options.addOption(CMD_PUBLISH_SLIDESHOW,true,"Publish slideshow to specified folder");
        options.addOption(CMD_REPORT,true,"Write archive status report to specified folder");
        options.addOption(CMD_WINDOW,false,"Show the application user interface");
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
                _logger.log(Level.SEVERE,"Could not parse configuration file.", ex);
                System.exit(-1);
            }
        }
        // generate status report
        if (cmd.hasOption(CMD_REPORT) && config != null) {
            String output_path = cmd.getOptionValue(CMD_REPORT);
            output = new File(output_path);
            try {
                ReportPublisher r = new ReportPublisher(config,output);
                r.run();
            } catch (Exception ex) {
                _logger.log(Level.SEVERE,"Could not generate report.",ex);
                System.exit(-1);
            }
            System.exit(0);
        }
        // publish mobile content application
        if (cmd.hasOption(CMD_PUBLISH_MOBILE) && config != null) {
            String output_path = cmd.getOptionValue(CMD_PUBLISH_MOBILE);
            output = new File(output_path);
            try {
                MobilePublisher p = new MobilePublisher(config,output);
                p.run();
            } catch (Exception ex) {
                _logger.log(Level.SEVERE,"Could not publish mobile gallery.",ex);
                System.exit(-1);
            }
            System.exit(0);
        }
        // publish artifact gallery and tags
        if (cmd.hasOption(CMD_PUBLISH_MOBILE) && config != null) {
            String output_path = cmd.getOptionValue(CMD_PUBLISH_MOBILE);
            output = new File(output_path);
            try {
                ArtifactPublisher p = new ArtifactPublisher(config,output);
                p.run();
            } catch (Exception ex) {
                _logger.log(Level.SEVERE,"Could not publish artifact gallery.",ex);
                System.exit(-1);
            }
            System.exit(0);
        }
        // publish slideshow
        if (cmd.hasOption(CMD_PUBLISH_MOBILE) && config != null) {
            String output_path = cmd.getOptionValue(CMD_PUBLISH_MOBILE);
            output = new File(output_path);
            try {
                SlideshowPublisher p = new SlideshowPublisher(config,output);
                p.run();
            } catch (Exception ex) {
                _logger.log(Level.SEVERE,"Could not publish slideshow.",ex);
                System.exit(-1);
            }
            System.exit(0);
        }
        // show the application user interface 
        if (cmd.hasOption(CMD_WINDOW)) {
            AppJFrame f = new AppJFrame();
            f.pack();
            f.setVisible(true);
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
