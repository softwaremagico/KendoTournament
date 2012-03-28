/*
 *  This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2009 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *  Created on 27-mar-2012.
 */
package com.softwaremagico.ktg.pdflist;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 *
 * @author LOCAL\jhortelano
 */
public abstract class PdfDocument {

    protected int fontSize = 17;

    protected Document documentData(Document document) {
        document.addTitle("Kendo Tournament's File");
        document.addAuthor("Jorge Hortelano");
        document.addCreator("Kendo Tournament Tool");
        document.addSubject("Kendo List");
        document.addKeywords("Kendo, Tournament");
        document.addCreationDate();
        return document;
    }

    void addBackGroundImage(Document document, String imagen) throws BadElementException,
            DocumentException, MalformedURLException, IOException {
        com.lowagie.text.Image png;

        png = com.lowagie.text.Image.getInstance(imagen);
        png.setAlignment(com.lowagie.text.Image.MIDDLE);
        png.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());
        png.setAbsolutePosition(0, 0);
        document.add(png);
    }

    void generatePDF(Document document, PdfWriter writer) throws Exception {
        String font = FontFactory.HELVETICA;
        documentData(document);
        document.open();
        document.setMargins(180, 108, 72, 36);
        createPagePDF(document, writer, font);
        document.close();
    }

    protected abstract void createPagePDF(Document document, PdfWriter writer, String font) throws Exception;
}
