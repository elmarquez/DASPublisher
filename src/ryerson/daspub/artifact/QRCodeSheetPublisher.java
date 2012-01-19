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
package ryerson.daspub.artifact;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;
import ryerson.daspub.utility.ImageUtils;
import ryerson.daspub.utility.QRCodeImageFileFilter;

/**
 * Takes a file pointer to a directory that contains PNG images of QR barcodes.
 * Lays out the images on a letter sized sheet that conforms to an Avery 22806
 * sheet template.
 */
public class QRCodeSheetPublisher {

    private static final int ITEMS_PER_PAGE = 12;
    private static final float LINE_THICKNESS = 0.25f;
    private static final String PDF_FILENAME = "artifact-tagsheet.pdf";

    private static final Boolean PRINT_FRAME = false;
    private static final Boolean PRINT_TRIM_MARKS = true;
    private static final Boolean PRINT_BARCODE = true;
    private static final Boolean PRINT_ID_STRING = true;

    private static final Boolean PRINT_PAGE_NUMBER = true;
    private static final Boolean PRINT_DATE_STAMP = true;
    private static final Boolean PRINT_HEADER_STRING = true;

    private static final int TRIM_MARK_OFFSET = 3;
    private static final int TRIM_MARK_LENGTH = 12;

    private static final String HEADER_STRING = "Print on Avery 22806 compatible label sheet.";
    
    private File input;
    private ArrayList<Point> layout = new ArrayList<>();
    
    private static final Logger _logger = Logger.getLogger(QRCodeSheetPublisher.class.getName());

    //--------------------------------------------------------------------------

    /**
     * TagSheet constructor.
     * @param Input Input directory with QR barcode files
     * @param Output Output file
     */
    public QRCodeSheetPublisher(File Input) {
        input = Input;
        
        // define the standard sticker layout points
        layout.add(new Point(45,601));
        layout.add(new Point(234,601));
        layout.add(new Point(423,601));

        layout.add(new Point(45,416));
        layout.add(new Point(234,416));
        layout.add(new Point(423,416));

        layout.add(new Point(45,230));
        layout.add(new Point(234,230));
        layout.add(new Point(423,230));

        layout.add(new Point(45,45));
        layout.add(new Point(234,45));
        layout.add(new Point(423,45));
    }

    //--------------------------------------------------------------------------

    /**
     * Draw trimming marks around each tag.
     * @param Writer PDF writer
     * @param x X coordinate of bottom left corner of tag frame
     * @param y Y coordinate of bottom left corner of tag frame
     * @param width Width of tag frame
     * @param height Height of tag frame
     * @throws DocumentException
     * @throws IOException 
     */
    private void drawTrimMarks(PdfWriter Writer, int x, int y, int width, int height) throws DocumentException, IOException {
        // redefine tag frame coordinates in absolute coordinates
        width = width + x;
        height = height + y;
        // bottom left
        drawLine(Writer,x,y-TRIM_MARK_OFFSET,x,y-TRIM_MARK_LENGTH);
        drawLine(Writer,x-TRIM_MARK_OFFSET,y,x-TRIM_MARK_LENGTH,y);
        // top left
        drawLine(Writer,x,height+TRIM_MARK_OFFSET,x,height+TRIM_MARK_LENGTH);
        drawLine(Writer,x-TRIM_MARK_OFFSET,height,x-TRIM_MARK_LENGTH,height);
        // top right
        drawLine(Writer,width,height+TRIM_MARK_OFFSET,width,height+TRIM_MARK_LENGTH);
        drawLine(Writer,width+TRIM_MARK_OFFSET,height,width+TRIM_MARK_LENGTH,height);
        // bottom right
        drawLine(Writer,width,y-TRIM_MARK_OFFSET,width,y-TRIM_MARK_LENGTH);
        drawLine(Writer,width+TRIM_MARK_OFFSET,y,width+TRIM_MARK_LENGTH,y);
    }
    
    /**
     * Draw an image on the current page.
     * @param Writer PDF writer
     * @param Img Image data
     * @param x X coordinate of bottom left corner of image
     * @param y Y coordinate of bottom left corner of image
     * @param Width Image width
     * @param Height Image height
     * @throws BadElementException
     * @throws DocumentException 
     * @throws IOException
     * @throws MalformedURLException
     */
    private void drawImage(PdfWriter Writer, File Img, int x, int y, int Width, int Height) throws BadElementException, MalformedURLException, IOException, DocumentException {
        // resize the image
        byte[] data = ImageUtils.resizeImageToByteArray(Img, Width, Height);
        // place the image
        Image img = Image.getInstance(data);
        img.setAbsolutePosition(x,y);
        Writer.getDirectContent().addImage(img, true);
    }
    
    /**
     * Draw a text label on the current page.
     * @param Writer PDF writer
     * @param Text
     * @param x
     * @param y
     * @param alignment
     * @throws DocumentException
     * @throws IOException 
     */
    private void drawLabel(PdfWriter Writer, String Text, int x, int y, int alignment) throws DocumentException, IOException {
        PdfContentByte cb = Writer.getDirectContent();
        BaseFont bf = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        cb.saveState();
        cb.beginText();
        cb.setFontAndSize(bf,9);
        cb.showTextAligned(alignment, Text, x, y, 0);
        cb.endText();
        cb.restoreState();
    }
    
    /**
     * 
     * @param Writer
     * @param Text
     * @param x
     * @param y
     */
    private void drawLabel(PdfWriter Writer, String Text, int x, int y) throws DocumentException, IOException {
        drawLabel(Writer,Text,x,y,PdfContentByte.ALIGN_CENTER);
    }
    
    /**
     * Draw a line on the page
     * @param Writer
     * @param x
     * @param y
     * @param w
     * @param h 
     */
    private void drawLine(PdfWriter Writer, int x1, int y1, int x2, int y2) throws DocumentException, IOException {
        PdfContentByte cb = Writer.getDirectContent();
        cb.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, false), 24);
        cb.moveTo(x1,y1);
        cb.lineTo(x2,y2);
        cb.setLineWidth(LINE_THICKNESS);
        cb.stroke();    
    }

    /**
     * 
     * @param Writer
     * @param Page
     * @throws DocumentException
     * @throws IOException 
     */
    private void drawPageLabels(PdfWriter Writer, int Page) throws DocumentException, IOException {
        // page header
        if (PRINT_HEADER_STRING) drawLabel(Writer,HEADER_STRING,306,770);
        // date stamp
        if (PRINT_DATE_STAMP) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            String time = dateFormat.format(cal.getTime());
            drawLabel(Writer,time,45,22,PdfContentByte.ALIGN_LEFT);
        }
        // page number
        if (PRINT_PAGE_NUMBER) drawLabel(Writer,String.valueOf(Page),568,22,PdfContentByte.ALIGN_RIGHT);
    }
    
    /**
     * 
     * @param Writer
     * @param x
     * @param y
     * @param w
     * @param h 
     */
    private void addRectangle(PdfWriter Writer, int x, int y, int w, int h) {
        PdfContentByte cb = Writer.getDirectContent();
        cb.rectangle(x,y,w,h);
        cb.setLineWidth(LINE_THICKNESS);
        cb.stroke();
    }

    /**
     * 
     * @param Writer
     * @param P 
     */
    private void drawTag(PdfWriter Writer, File Img, Point P) throws BadElementException, MalformedURLException, IOException, DocumentException {
        // tag frame
        if (PRINT_FRAME) addRectangle(Writer,P.x,P.y,145,145);
        // tag cut marks
        if (PRINT_TRIM_MARKS) drawTrimMarks(Writer,P.x,P.y,145,145);
        // bar code
        if (PRINT_BARCODE) drawImage(Writer,Img,P.x+23,P.y+23,100,100);
        // place label @72,16
        if (PRINT_ID_STRING) {
            String name = Img.getName();
            name = name.replace("_qr.png", "");
            drawLabel(Writer,name,P.x+72,P.y+16);
        }
    }
     
    /**
     * 
     * @param Output
     * @throws DocumentException
     * @throws FileNotFoundException
     * @throws BadElementException
     * @throws MalformedURLException
     * @throws IOException 
     */
    public void writeTagSheet(File Output) throws DocumentException, FileNotFoundException, BadElementException, MalformedURLException, IOException {
        // get list of input files
        File[] files = input.listFiles(new QRCodeImageFileFilter());
        if (files != null && files.length > 0) {
            // create a new PDF document
            Document document = new Document(PageSize.LETTER);
            PdfWriter writer = PdfWriter.getInstance(document,new FileOutputStream(Output));
            document.addTitle("Artifact QR Code Labels");
            document.open();
            // generate page layouts with barcodes
            if (files != null) {
                int itemcount = 0;
                int pagecount = 1;
                for (int i=0;i<files.length;i++) {
                    if (itemcount == 0) {
                        drawPageLabels(writer,pagecount);
                    }
                    Point p = layout.get(itemcount);
                    drawTag(writer,files[i],p);
                    itemcount++;
                    if (itemcount>ITEMS_PER_PAGE-1) {
                        itemcount = 0;
                        pagecount++;
                        document.newPage();
                    }            
                }
            }
            document.close();
        }
    }
    
} // end class
