/*
 *   This software is designed by Jorge Hortelano Otero.
 *   softwaremagico@gmail.com
 *   Copyright (C) 2012 Jorge Hortelano Otero.
 *   C/Quart 89, 3. Valencia CP:46008 (Spain).
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version 2
 *   of the License, or (at your option) any later version.
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *   Created on 5-feb-2009.
 */
package com.softwaremagico.ktg.pdflist;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.softwaremagico.ktg.CompetitorWithPhoto;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.MessageManager;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.language.Translator;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author jorge
 */
public class TournamentAccreditationPDF {

    Tournament championship;
    private final int border = 1;
    private int fontSize = 17;
    //private final int accreditationPerPage = 4;
    private com.lowagie.text.Image png = null;

    public TournamentAccreditationPDF(Tournament tmp_championship) throws Exception {
        championship = tmp_championship;
    }

    public void GenerateTournamentPDF(String path, boolean printAll) {
        //DIN A6 105 x 148 mm
        Document document = new Document(PageSize.A4);
        if (!path.endsWith(".pdf")) {
            path += ".pdf";
        }
        if (!MyFile.fileExist(path) || MessageManager.question("existFile", "Warning!", KendoTournamentGenerator.getInstance().language)) {
            TimerPanel tp = new TimerPanel();

            ThreadAccreditation ta = new ThreadAccreditation(tp, document, path, printAll);
            ta.start();
        }
    }

    public class ThreadAccreditation extends Thread {

        TimerPanel timerPanel;
        Document document;
        String path;
        Translator transl;
        boolean all;

        public ThreadAccreditation(TimerPanel tp, Document d, String p, boolean printAll) {
            transl = new Translator("gui.xml");
            timerPanel = tp;
            tp.updateTitle(transl.returnTag("AccreditationProgressBarTitle", KendoTournamentGenerator.getInstance().language));
            tp.updateLabel(transl.returnTag("AccreditationProgressBarLabel", KendoTournamentGenerator.getInstance().language));
            document = d;
            path = p;
            all = printAll;
        }

        @Override
        public void run() {
            convertPDF();
        }

        public boolean convertPDF() {
            boolean error = false;
            try {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
                GeneratePDF(document, writer);
                MessageManager.customMessage("tournamentOK", "PDF", KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
                KendoTournamentGenerator.getInstance().database.setAllParticipantsInTournamentAsAccreditationPrinted(championship.name);
            } catch (NullPointerException npe) {
                MessageManager.errorMessage("noTournamentFieldsFilled", "MySQL", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageManager.errorMessage("tournamentBad", "PDF", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            }
            timerPanel.dispose();
            return error;
        }

        private void GeneratePDF(Document document, PdfWriter writer) throws Exception {
            String font = FontFactory.HELVETICA;
            DocumentData(document);
            document.open();
            AccreditationGroupPagePDF(document, writer, font);
            document.close();
        }

        private Document DocumentData(Document document) {
            document.addTitle("Kendo Tournament");
            document.addAuthor("Jorge Hortelano");
            document.addCreator("Kendo Tournament Tool");
            document.addSubject("Tournament");
            document.addKeywords("Kendo, Tournament");
            document.addCreationDate();
            return document;
        }

        private void AddBackGroundImage(Document document, String imagen) throws BadElementException,
                DocumentException, MalformedURLException, IOException {
            //Obtain and scale the background. 
            if (png == null) {
                png = com.lowagie.text.Image.getInstance(imagen);
                png.setAlignment(com.lowagie.text.Image.UNDERLYING);
                png.scaleToFit(document.getPageSize().getWidth() / 2, document.getPageSize().getHeight() / 2);
            }

            //Copy the background to each competitor accreditation. 
            png.setAbsolutePosition(0, 0);
            document.add(png);
            png.setAbsolutePosition(document.getPageSize().getWidth() / 2, 0);
            document.add(png);
            png.setAbsolutePosition(0, document.getPageSize().getHeight() / 2);
            document.add(png);
            png.setAbsolutePosition(document.getPageSize().getWidth() / 2, document.getPageSize().getHeight() / 2);
            document.add(png);
        }

        private void AccreditationGroupPagePDF(Document document, PdfWriter writer, String font) throws Exception {
            AddBackGroundImage(document, Path.returnBackgroundPath());
            PdfPTable table = PageTable(document, writer, font, fontSize);
            table.setWidthPercentage(100);
            document.add(table);
        }

        private PdfPTable PageTable(Document document, PdfWriter writer, String font, int fontSize) throws IOException, BadElementException, Exception {
            PdfPCell cell;
            Paragraph p;
            float[] widths = {0.50f, 0.50f};
            PdfPTable mainTable = new PdfPTable(widths);
            mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.setTotalWidth(document.getPageSize().getWidth());

            List<CompetitorWithPhoto> competitors = KendoTournamentGenerator.getInstance().database.selectAllParticipantsInTournamentWithoutAccreditation(championship.name, all);


            for (int i = 0; i < competitors.size(); i++) {
                timerPanel.updateText(transl.returnTag("AccreditationProgressBarLabel", KendoTournamentGenerator.getInstance().language), i, competitors.size());
                CompetitorAccreditationCardPDF competitorPDF = new CompetitorAccreditationCardPDF(competitors.get(i), championship);
                PdfPTable competitorTable = competitorPDF.pageTable(document.getPageSize().getWidth() / 2, document.getPageSize().getHeight() / 2, writer, font, fontSize);
                cell = new PdfPCell(competitorTable);
                cell.setBorderWidth(border);
                cell.setColspan(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.addElement(competitorTable);
                mainTable.addCell(cell);
                /*
                 * if ((i > 0) && (i % accreditationPerPage == 0)) { //Add a new
                 * page if this one is filled up and remains more than one
                 * competitor's accreditation. mainTable.writeSelectedRows(0,
                 * -1, 0, document.getPageSize().getHeight(),
                 * writer.getDirectContent()); mainTable.flushContent();
                 * document.newPage(); AddBackGroundImage(document,
                 * Path.returnBackgroundPath()); }
                 */
            }

            if (competitors.size() % 2 == 1) {  // if we have even competitors, the last one needs an empty cell.
                cell = new PdfPCell();
                mainTable.addCell(cell);
            }

            /*
             * mainTable.writeSelectedRows(0, -1, 0,
             * document.getPageSize().getHeight(), writer.getDirectContent());
             * mainTable.flushContent();
             */

            return mainTable;
        }
    }
}
