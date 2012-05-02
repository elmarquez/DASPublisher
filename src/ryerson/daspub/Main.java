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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.artifact.PublishArtifactPagesTask;
import ryerson.daspub.artifact.PublishQRTagSheetTask;
import ryerson.daspub.init.Initializer;
import ryerson.daspub.mobile.PublishMobilePresentationTask;
import ryerson.daspub.report.PublishReportTask;
import ryerson.daspub.slideshow.PublishSlideshowTask;
import ryerson.daspub.ui.ApplicationJFrame;
import ryerson.daspub.ui.JTextAreaOutputFormatter;
import ryerson.daspub.ui.JTextAreaOutputHandler;

/**
 * Command line interface to application components.
 * @author dmarques
 */
public class Main implements Runnable {

    public static final int FAIL = -1;
    public static final int SUCCESS = 0;

    private static final String CMD_CONFIG = "config";
    private static final String CMD_HELP = "help";
    private static final String CMD_GUI = "gui";
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
    private static File configFile = null;
    private static CommandLine cmd = null;

    private static ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    //--------------------------------------------------------------------------

    /**
     * Main constructor.
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
        options.addOption(CMD_CONFIG, true, "Path to project configuration file.");
        options.addOption(CMD_GUI, false, "Show the application user interface. This option halts execution of additional publishing options.");
        options.addOption(CMD_HELP, false, "Show command line help message.");
        options.addOption(CMD_INIT, true, "Create a new archive with sample course folders and metadata files or, update an existing archive with required files. Requires specification of an archive path.");
        options.addOption(CMD_PUBLISH, true, "Publish content. Available options are artifact, mobile, report, slideshow, tagsheet. Requires specification of output path.");
    }

    /**
     * Initialize the archive
     */
    private void executeInit() {
        try {
            Initializer init = new Initializer(config);
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
        String option = cmd.getOptionValue(CMD_PUBLISH);
        try {
            if (option.equals(OPTION_ARTIFACT)) {
                PublishArtifactPagesTask p = new PublishArtifactPagesTask(config);
                pool.execute(p);
            } else if (option.equals(OPTION_MOBILE)) {
                PublishMobilePresentationTask p = new PublishMobilePresentationTask(config);
                pool.execute(p);
            } else if (option.equals(OPTION_REPORT)) {
                PublishReportTask p = new PublishReportTask(config);
                pool.execute(p);
            } else if (option.equals(OPTION_SLIDESHOW)) {
                PublishSlideshowTask p = new PublishSlideshowTask(config);
                pool.execute(p);
            } else if (option.equals(OPTION_TAGSHEET)) {
                PublishQRTagSheetTask p = new PublishQRTagSheetTask(config);
                pool.execute(p);
            }
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE, "Could not complete publication\n\n{0}", stack);
            System.exit(FAIL);
        }
        // shutdown the thread pool
        pool.shutdown();
        // wait for pool to stop
        try {
            while (!pool.isTerminated()) {
                pool.awaitTermination(10,TimeUnit.SECONDS);
            }
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Exception while waiting for thread pool termination.\n\n{0}", ex);
        }
        // halt
        System.exit(Main.SUCCESS);
    }

    /**
     * Get thread pool.
     * @return 
     */
    public static ExecutorService getThreadPool() {
        return pool;
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
    @Override
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
                configFile = new File(path);
                config = Config.load(configFile);
            } catch (Exception ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE, "Could not parse configuration file\n\n{0}", stack);
                System.exit(FAIL);
            }
        }
        // show gui or process command line options
        if (cmd.hasOption(CMD_GUI)) {
            ApplicationJFrame frame = ApplicationJFrame.getInstance();
            // send logging output to jtextarea
            JTextArea textArea = frame.getLogOutputTextArea();
            JTextAreaOutputHandler handler = new JTextAreaOutputHandler(textArea);
            handler.setFormatter(new JTextAreaOutputFormatter());
            handler.setLevel(Level.ALL);
            Logger rootlogger = LogManager.getLogManager().getLogger("");
            rootlogger.addHandler(handler);
            // load project if specified at command line
            if (configFile!=null) {
                try {
                    frame.openProject(configFile);                    
                } catch (Exception ex) {
                    String stack = ExceptionUtils.getStackTrace(ex);
                    logger.log(Level.SEVERE, "Could not load configuration file {0}\n\n{1}", 
                            new Object[] {configFile.getAbsolutePath(),stack});
                }
            }
            // display the application frame
            frame.setVisible(true);
            logger.log(Level.INFO,"Ready");
        } else if (cmd.hasOption(CMD_INIT)) {
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
