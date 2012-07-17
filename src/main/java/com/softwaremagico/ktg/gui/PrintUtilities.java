package com.softwaremagico.ktg.gui;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2012 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.swing.RepaintManager;

/**
 * A simple utility class that lets you very simply print an arbitrary
 * component. Just pass the component to the PrintUtilities.printComponent. The
 * component you want to print doesn't need a print method and doesn't have to
 * implement any interface or do anything special at all. <P> If you are going
 * to be printing many times, it is marginally more efficient to first do the
 * following:
 * <PRE>
 *    PrintUtilities printHelper = new PrintUtilities(theComponent);
 * </PRE> then later do printHelper.print(). But this is a very tiny difference,
 * so in most cases just do the simpler
 * PrintUtilities.printComponent(componentToBePrinted).
 *
 * 7/99 Marty Hall, http://www.apl.jhu.edu/~hall/java/ May be freely used or
 * adapted.
 */
public class PrintUtilities implements Printable {

    PageFormat format;
    private Component componentToBePrinted;

    public static void printComponent(Component c) {
        new PrintUtilities(c).print();
    }

    public PrintUtilities(Component componentToBePrinted) {
        this.componentToBePrinted = componentToBePrinted;
    }

    public void print() {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setJobName("Kendo Tournament");
        format = printJob.defaultPage();
        format.setOrientation(PageFormat.LANDSCAPE);
        printJob.setPrintable(this);
        if (printJob.printDialog()) {
            try {
                printJob.print();
            } catch (PrinterException pe) {
                System.out.println("Error printing: " + pe);
            }
        }
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        pageFormat.setOrientation(PageFormat.LANDSCAPE);
        if (pageIndex > 0) {
            return (NO_SUCH_PAGE);
        } else {
            Graphics2D g2d = (Graphics2D) g;
            //Set us to the upper left corner
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            //  for faster printing, turn off double buffering
            //disableDoubleBuffering(componentToBePrinted);

            Dimension d = componentToBePrinted.getSize(); //get size of document
            double panelWidth = d.width; //width in pixels 
            double panelHeight = d.height; //height in pixels

            double pageHeight = pageFormat.getImageableHeight(); //height of printer page
            double pageWidth = pageFormat.getImageableWidth(); //width of printer page

            double scaleW = pageWidth / panelWidth;
            double scaleH = pageHeight / panelHeight;

            double scale = scaleW > scaleH ? scaleH : scaleW;

            g2d.scale(scale, scale);

            //  shift Graphic to line up with beginning of next page to print
            //g2d.translate(0f, pageIndex * pageHeight);


            //disableDoubleBuffering(componentToBePrinted);
            componentToBePrinted.paint(g2d);
            //enableDoubleBuffering(componentToBePrinted);
            return (PAGE_EXISTS);
        }
    }

    /**
     * The speed and quality of printing suffers dramatically if any of the
     * containers have double buffering turned on. So this turns if off
     * globally.
     *
     * @see enableDoubleBuffering
     */
    public static void disableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }

    /**
     * Re-enables double buffering globally.
     */
    public static void enableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }
}
