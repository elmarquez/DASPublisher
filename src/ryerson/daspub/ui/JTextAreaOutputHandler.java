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
package ryerson.daspub.ui;

import java.io.PrintWriter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javax.swing.JTextArea;

/**
 * 
 * @author dmarques
 */
public class JTextAreaOutputHandler extends Handler {

    private JTextArea output;
    private PrintWriter printWriter;

    //--------------------------------------------------------------------------
    
    /**
     * ScreenOutputLogHandler constructor
     * @param TextArea JTextArea where log output is to be displayed
     */
    public JTextAreaOutputHandler(JTextArea TextArea) {
        super();
        output = TextArea;
        setFormatter(new SimpleFormatter());
    }

    //--------------------------------------------------------------------------

    /**
     * Close the output stream.
     */
    @Override
    public void close() throws SecurityException {
        if (output != null)
            output.append("Logging closed.");
    }    

    /**
     * Send current buffer to the output.
     */
    @Override
    public void flush() {
        if (output != null)
            output.append("Log buffer flushed.");
    }

    /**
     * Send record to the output.
     */
    @Override
    public void publish(LogRecord record) {
        if (!isLoggable(record)) return;
        String log = getFormatter().format(record);
        output.append(log);
    }
    
} // end class
