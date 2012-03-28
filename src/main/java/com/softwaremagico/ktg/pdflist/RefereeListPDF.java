/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import com.softwaremagico.ktg.language.Translator;
import java.awt.Color;
import java.io.FileOutputStream;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author jorge
 */
public class RefereeListPDF extends ParentList {

    private Tournament championship;
    private final int border = 0;
    Translator trans = new Translator("gui.xml");

    public RefereeListPDF(Tournament tmp_championship) {
        championship = tmp_championship;
    }

    public boolean GenerateTeamListPDF(String path) {
        boolean error = false;
        //DIN A6 105 x 148 mm
        Document document = new Document(PageSize.A4);
        if (!path.endsWith(".pdf")) {
            path += ".pdf";
        }
        if (!MyFile.fileExist(path) || MessageManager.question("existFile", "Warning!", KendoTournamentGenerator.getInstance().language)) {
            try {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
                generatePDF(document, writer);
                MessageManager.customMessage("refereeListOK", "PDF", KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            } catch (NullPointerException npe) {
                MessageManager.errorMessage("noTournamentFieldsFilled", "MySQL", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                error = true;
                KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            } catch (Exception ex) {
                MessageManager.errorMessage("refereeListBad", "PDF", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                error = true;
            }
        }
        return !error;
    }

    @Override
    public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        PdfPCell cell;
        Paragraph p;
        int cellNumber = 0;

        p = new Paragraph(championship.name, FontFactory.getFont(font, fontSize + 6, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setBorderWidth(border);
        cell.setColspan(3);
        cell.setMinimumHeight(50);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        mainTable.addCell(cell);

        List<CompetitorWithPhoto> listReferee = KendoTournamentGenerator.getInstance().database.searchRefereeByTournament(championship.name, false, false);
        for (int i = 0; i < listReferee.size(); i++) {
            p = new Paragraph(" ");
            cell = new PdfPCell(p);
            cell.setBorderWidth(border);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.addCell(cell);

            p = new Paragraph(listReferee.get(i).returnSurnameName() + " (" + listReferee.get(i).club + ")");
            cell = new PdfPCell(p);
            cell.setBorderWidth(1);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBackgroundColor(new Color(255, 255, 255));
            mainTable.addCell(cell);
            cellNumber++;

            p = new Paragraph(" ");
            cell = new PdfPCell(p);
            cell.setBorderWidth(border);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.addCell(cell);

            cellNumber++;

            //Add a new page if is necessary...
            /*
             * if ((cellNumber > refereeByPage - (championship.teamSize)) && (i
             * < listReferee.size() - 1)) { mainTable.writeSelectedRows(0, -1,
             * 0, document.getPageSize().getHeight() - 40,
             * writer.getDirectContent()); mainTable.flushContent();
             * document.newPage(); AddBackGroundImage(document,
             * Path.returnBackgroundPath()); cellNumber = 0; }
             */
        }
    }

    @Override
    public float[] getTableWidths() {
        float[] widths = {0.05f, 0.90f, 0.05f};
        return widths;
    }

    @Override
    public void setTablePropierties(PdfPTable mainTable) {
        mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
    }

    @Override
    public void createHeaderRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        PdfPCell cell;
        Paragraph p;
        
        p = new Paragraph(trans.returnTag("RefereeTitle", KendoTournamentGenerator.getInstance().language), FontFactory.getFont(font, fontSize + 26, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setBorderWidth(headerBorder);
        cell.setColspan(3);
        //cell.setMinimumHeight(50);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        mainTable.addCell(cell);
    }

    @Override
    public void createFooterRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        PdfPCell cell;
        cell = new PdfPCell();
        cell.setColspan(getTableWidths().length);
        cell.setBorderWidth(footerBorder);
        mainTable.addCell(cell);
    }
}
