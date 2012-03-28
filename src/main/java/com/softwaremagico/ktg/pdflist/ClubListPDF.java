/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.pdflist;

import com.softwaremagico.ktg.CompetitorWithPhoto;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Club;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.MessageManager;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
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
public class ClubListPDF extends ParentList {

    private Tournament championship;
    private final int border = 0;
    private final int linesByPage = 55;
    Translator trans = null;

    public ClubListPDF(Tournament tmp_championship) {
        championship = tmp_championship;
        trans = new Translator("gui.xml");
    }

    public boolean generateClubListPDF(String path) {
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
                MessageManager.customMessage("ClubListOK", "PDF", KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            } catch (NullPointerException npe) {
                MessageManager.errorMessage("ClubListBad", "PDF", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                error = true;
                KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            } catch (Exception ex) {
                MessageManager.errorMessage("ClubListBad", "PDF", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                error = true;
            }
        }
        return !error;
    }

    public float[] getTableWidths() {
        float[] widths = {0.15f, 0.50f, 0.20f, 0.15f};
        return widths;
    }

    public void setTablePropierties(PdfPTable mainTable) {
        mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        mainTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
    }

    public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        PdfPCell cell;
        Paragraph p;
        int cellNumber = 0;

        List<Club> clubs = KendoTournamentGenerator.getInstance().database.returnClubs(false);

        for (int i = 0; i < clubs.size(); i++) {

            List<CompetitorWithPhoto> competitors = KendoTournamentGenerator.getInstance().database.searchCompetitorsByClubAndTournament(clubs.get(i).returnName(), championship.name, false, false);

            if (competitors.size() > 0) {

                p = new Paragraph(" ", FontFactory.getFont(font, fontSize, Font.BOLD));
                cell = new PdfPCell(p);
                cell.setColspan(4);
                cell.setBorderWidth(border);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);

                p = new Paragraph(" ", FontFactory.getFont(font, fontSize, Font.BOLD));
                cell = new PdfPCell(p);
                cell.setColspan(1);
                cell.setBorderWidth(border);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);

                /*
                 * Club
                 */
                String text = clubs.get(i).returnName();
                if (clubs.get(i).returnCountry().length() > 1) {
                    text += " (" + clubs.get(i).returnCountry() + ")";
                }

                /*
                 * try { if (clubs.get(i).email.length() > 1) { text += ": " +
                 * clubs.get(i).email; } } catch (NullPointerException npe) { }
                 */
                p = new Paragraph(text, FontFactory.getFont(font, fontSize, Font.BOLD));
                cell = new PdfPCell(p);
                cell.setColspan(2);
                cell.setBorderWidth(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(new Color(255, 255, 255));
                mainTable.addCell(cell);

                p = new Paragraph(" ", FontFactory.getFont(font, fontSize, Font.BOLD));
                cell = new PdfPCell(p);
                cell.setColspan(1);
                cell.setBorderWidth(border);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);

                cellNumber += 3;

            }

            for (int j = 0; j < competitors.size(); j++) {

                /*
                 * Border Line
                 */
                p = new Paragraph(" ", FontFactory.getFont(font, fontSize - 8, Font.BOLD));
                cell = new PdfPCell(p);
                cell.setColspan(1);
                cell.setBorderWidth(border);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);

                p = new Paragraph(competitors.get(j).returnSurnameName() + " (" + competitors.get(j).getId() + ")", FontFactory.getFont(font, fontSize - 8, Font.BOLD));
                cell = new PdfPCell(p);
                cell.setBorderWidth(1);
                cell.setColspan(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                if (cellNumber % 2 == 0) {
                    cell.setBackgroundColor(new Color(255, 255, 255));
                } else {
                    cell.setBackgroundColor(new Color(230, 230, 230));
                }
                mainTable.addCell(cell);

                p = new Paragraph(KendoTournamentGenerator.getInstance().getAvailableRoles().getTraduction(KendoTournamentGenerator.getInstance().database.getTagRole(championship, competitors.get(j))), FontFactory.getFont(font, fontSize - 8, Font.BOLD));
                cell = new PdfPCell(p);
                cell.setBorderWidth(1);
                cell.setColspan(1);
                if (cellNumber % 2 == 0) {
                    cell.setBackgroundColor(new Color(255, 255, 255));
                } else {
                    cell.setBackgroundColor(new Color(230, 230, 230));
                }
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);

                /*
                 * Border Line
                 */
                p = new Paragraph(" ", FontFactory.getFont(font, fontSize - 8, Font.BOLD));
                cell = new PdfPCell(p);
                cell.setColspan(1);
                cell.setBorderWidth(border);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);
                cellNumber++;

                //Add a new page if it is necessary...
               /*
                 * if ((cellNumber >= linesByPage - 1) && (j <
                 * competitors.size() - 1)) { mainTable.writeSelectedRows(0, -1,
                 * 0, document.getPageSize().getHeight() - 40,
                 * writer.getDirectContent()); mainTable.flushContent();
                 * document.newPage(); AddBackGroundImage(document,
                 * Path.returnBackgroundPath()); cellNumber = 0; }
                 */
            }

            //Add a new page if is necessary...
            /*
             * if ((cellNumber >= linesByPage - 1) && (i < clubs.size() - 1)) {
             * mainTable.writeSelectedRows(0, -1, 0,
             * document.getPageSize().getHeight() - 40,
             * writer.getDirectContent()); mainTable.flushContent();
             * document.newPage(); AddBackGroundImage(document,
             * Path.returnBackgroundPath()); cellNumber = 0; }
             */
        }
    }

    @Override
    public void createHeaderRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        PdfPCell cell;
        Paragraph p;
        p = new Paragraph(championship.name, FontFactory.getFont(font, fontSize + 15, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setColspan(getTableWidths().length);
        cell.setBorderWidth(headerBorder);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //cell.setBackgroundColor(new Color(255, 255, 255));
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
