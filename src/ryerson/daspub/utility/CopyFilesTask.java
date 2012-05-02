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
package ryerson.daspub.utility;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Utility class to copy files between directories. Can be executed on a thread
 * pool.
 * @author dmarques
 */
public class CopyFilesTask implements Runnable {

    private File input;
    private File output;

    private static final Logger logger = Logger.getLogger(CopyFilesTask.class.getName());

    //--------------------------------------------------------------------------

    /**
     * CopyFilesTask constructor
     * @param Input Input folder
     * @param Output Output folder
     */
    public CopyFilesTask(File Input, File Output) {
        input = Input;
        output = Output;
    }

    //--------------------------------------------------------------------------

    /**
     * Copy files from input to
     */
    @Override
    public void run() {
        logger.log(Level.INFO,"STARTING copy files task");
        if (input.exists()) {
            // create the output directory if it does not exist
            if (!output.exists()) {
                output.mkdirs();
            }
            // copy files
            try {
                logger.log(Level.INFO,"Copying files from \"{0}\" to \"{1}\"",
                        new Object[]{input.getAbsolutePath(),output.getAbsolutePath()});
                FileUtils.copyDirectory(input, output);
            } catch (IOException ex) {
                String stack = ExceptionUtils.getStackTrace(ex);
                logger.log(Level.SEVERE,"Could not complete copying files from {0} to {1}\n\n{2}",
                        new Object[]{input.getAbsolutePath(),output.getAbsolutePath(),stack});
            }
        } else {
            logger.log(Level.SEVERE,"No files were copied because the input directory \"{0}\" does not exist.",
                    input.getAbsolutePath());
        }
        logger.log(Level.INFO,"DONE copying files");
    }

} // end class
