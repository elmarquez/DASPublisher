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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ryerson.daspub.model.Submission;

/**
 * Utilities for extracting submission data from an Excel spreadsheet file.
 * @author dmarques
 */
public class SubmissionSpreadsheetAdapter {

    private static final int MAX_BLANK_CELLS_PER_ROW = 5;
    private static final int MIN_CELLS_PER_ROW = 14;

    private File parentFolder;
    private File spreadsheet;

    private static final Logger logger = Logger.getLogger(SubmissionSpreadsheetAdapter.class.getName());

    //--------------------------------------------------------------------------

    /**
     * SubmissionSpreadsheetAdapter constructor.
     * @param F Spreadsheet file
     */
    public SubmissionSpreadsheetAdapter(File F) {
        spreadsheet = F;
        parentFolder = F.getParentFile();
    }

    //--------------------------------------------------------------------------

    /**
     * Get the cell value. Compensates for empty cells and cells that contain
     * errors.
     * @param C Cell
     * @return
     */
    private static String getCellValue(Cell C) {
        CellType type = C.getType();
        if (type == CellType.EMPTY || type == CellType.ERROR
                || type == CellType.FORMULA_ERROR) {
            return "";
        } else {
            return C.getContents();
        }
    }

    /**
     * Get data rows.
     * @return
     */
    private List<Cell[]> getDataRows() {
        ArrayList<Cell[]> datarows = new ArrayList<Cell[]>();
        List<Cell[]> rows;
        try {
            rows = getRows();
            // find the end of the header row
            int dataStart = 0;
            int dataEnd = rows.size();
            int row = 0;
            boolean found = false;
            while(!found && row < rows.size() && row < 5) {
                Cell[] cells = rows.get(row);
                if (isHeaderRow(cells)) {
                    dataStart = row + 1;
                    found = true;
                }
                row++;
            }
            // return remainder as data rows
            if (found) {
                for (int i=dataStart;i<dataEnd;i++) {
                    Cell[] cells = rows.get(i);
                    datarows.add(cells);
                }
            }
        } catch (BiffException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return datarows;
    }

    /**
     * Gets the header row.
     * @return
     */
    private Cell[] getHeader() {
        Cell[] header = new Cell[0];
        try {
            List<Cell[]> rows = getRows();
            int row = 0;
            boolean found = false;
            while(!found && row < 5) {
                Cell[] cells = rows.get(row);
                if (isHeaderRow(cells)) {
                    header = cells;
                    found = true;
                }
                row++;
            }
        } catch (BiffException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return header;
    }

    /**
     * Gets header to column number map.
     * @return
     */
    private HashMap<String,Integer> getHeaderMap() {
       HashMap<String,Integer> map = new HashMap<String,Integer>();
       Cell[] header = getHeader();
       for (int col=0;col<header.length;col++) {
           Cell cell = header[col];
           String key = getCellValue(cell);
           map.put(key,col);
       }
       return map;
    }

    /**
     * Get spreadsheet rows.
     * @return
     * @throws BiffException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private List<Cell[]> getRows() throws BiffException, FileNotFoundException, IOException {
        ArrayList<Cell[]> rows = new ArrayList<Cell[]>();
        if (spreadsheet.exists()) {
            WorkbookSettings ws = new WorkbookSettings();
            ws.setLocale(new Locale("en", "EN"));
            FileInputStream fis = new FileInputStream(spreadsheet);
            Workbook workbook = Workbook.getWorkbook(fis, ws);
            Sheet sheet = workbook.getSheet(0);
            int endRow = sheet.getRows();
            for (int row=0; row < endRow; row++) {
                Cell[] cells = sheet.getRow(row);
                if (!isBlankRow(cells)) {
                    rows.add(cells);
                }
            }
        }
        return rows;
    }

    /**
     * Parse spreadsheet row to create a Submission object.  If the row is
     * missing required data fields, then a null object will be returned.
     * @param Cells Cells from one spreadsheet row
     * @param Path Folder where the submission data object is located
     * @return Submission
     * @todo Consider a flexible cell to parameter mapping, using column header names instead of fixed mapping
     */
    private Submission getSubmission(Cell[] Cells) {
        Submission s = null;
        // map cell values to new submission object
        String c_year = getCellValue(Cells[0]);
        String c_semester = getCellValue(Cells[1]);
        String c_coursenumber = getCellValue(Cells[2]);
        String c_coursename = getCellValue(Cells[3]);
        String c_studiomaster = getCellValue(Cells[4]);
        String c_instructor = getCellValue(Cells[5]);
        String c_assignmentname = getCellValue(Cells[6]);
        String c_assignmentduration = getCellValue(Cells[7]);
        String c_studentname = getCellValue(Cells[8]);
        String c_evaluation = getCellValue(Cells[9]);
        // action
        String c_numberofitems = getCellValue(Cells[11]);
        String c_filename = getCellValue(Cells[12]);
        // notes
        String c_id = getCellValue(Cells[14]);
        // if the required cells are not empty, create a new submission object
        if (c_id != null
                && c_year != null
                && c_coursenumber != null
                && c_instructor != null
                && c_studentname != null
                && c_filename != null) {
            File f = new File(parentFolder, c_filename);
            s = new Submission(c_year,
                    c_semester,
                    c_coursenumber,
                    c_coursename,
                    c_studiomaster,
                    c_instructor,
                    c_assignmentname,
                    c_assignmentduration,
                    c_studentname,
                    c_numberofitems,
                    c_id,
                    f.getAbsolutePath(),
                    c_evaluation);
        } else {
            logger.log(Level.WARNING, "Spreadsheet item is missing one of the required values: id, year, course number, instructor, student name, file name.");
        }
        return s;
    }

    /**
     * Gets list of submissions.
     * @return List of submissions.
     */
    public List<Submission> getSubmissions() throws Exception {
        ArrayList<Submission> items = new ArrayList<Submission>();
        List<Cell[]> datarows = getDataRows();
        Iterator<Cell[]> it = datarows.iterator();
        while (it.hasNext()) {
            Cell[] row = it.next();
            Submission s = this.getSubmission(row);
            if (s != null) {
                items.add(s);
            }
        }
        return items;
    }

    /**
     * Determines if the submission metadata file conforms to the requirements
     * for parsing.
     * @param F Spreadsheet file
     */
    public boolean hasConformingSubmissionMetadataFile() {
        try {
            List<Submission> items = getSubmissions();
            return true;
        } catch (Exception ex) {
            String stack = ExceptionUtils.getStackTrace(ex);
            logger.log(Level.SEVERE, "Could not load submission metadata from \"{0}\".\n\n{1}",
                    new Object[]{spreadsheet.getAbsolutePath(), stack});
        }
        return false;
    }

    /**
     * Determine if a cell row is blank.
     * @param Cells Row of cells
     * @return
     */
    private static boolean isBlankRow(Cell[] Cells) {
        if (Cells == null || Cells.length == 0) {
            return true;
        }
        // if first cell is blank, row is considered blank
        Cell c = Cells[0];
        CellType type = c.getType();
        if (type == CellType.EMPTY) {
            return true;
        }
        return false;
    }

    /**
     * Determines if cell array is a spreadsheet header row.
     * @param Cells
     * @return True if header row, false otherwise.
     */
    private static boolean isHeaderRow(Cell[] Cells) {
        if (Cells == null || Cells.length == 0) {
            return false;
        }
        Cell cell = Cells[0];
        if (getCellValue(cell).toUpperCase().equals("YEAR")) {
            return true;
        }
        return false;
    }

} // end class
