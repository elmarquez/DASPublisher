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

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Customer formatter
 * @author dmarques
 */
public class JTextAreaOutputFormatter extends Formatter {

    /**
     * JTextAreaOutputFormatter constructor
     */
    public JTextAreaOutputFormatter() {
        super();
    }

    //--------------------------------------------------------------------------
    
    /**
     * Format the record
     * @param record
     * @return 
     */
    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        // Get the date from the LogRecord and add it to the buffer
        Date date = new Date(record.getMillis());
        sb.append(date.toString());
        sb.append(" ");
        // Get the level name and add it to the buffer
        sb.append(record.getLevel().getName());
        sb.append(" ");
        // Get the formatted message (includes localization 
        // and substitution of paramters) and add it to the buffer
        sb.append(formatMessage(record));
        sb.append("\n");
        // return result
        return sb.toString();
    }
    
} // end class
