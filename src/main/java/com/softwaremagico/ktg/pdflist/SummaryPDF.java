/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.pdflist;

import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Score;
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
public class SummaryPDF extends ParentList {

    private Tournament championship;
    private final int border = 0;
    private final int linesByPage = 40;
    Translator trans = null;
    private int useOnlyShiaijo = -1;

    public SummaryPDF(Tournament tmp_championship, int shiaijo) {
        championship = tmp_championship;
        trans = new Translator("gui.xml");
        useOnlyShiaijo = shiaijo;
    }

    public boolean GenerateFightListPDF(String path) {
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
                MessageManager.customMessage("SummaryListOK", "PDF", KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            } catch (NullPointerException npe) {
                MessageManager.errorMessage("SummaryListBad", "PDF", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                error = true;
                KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            } catch (Exception ex) {
                MessageManager.errorMessage("SummaryListBad", "PDF", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                error = true;
            }
        }
        return !error;
    }

    private PdfPTable FightTable(String font, int fontSize, Fight f, boolean first) throws DocumentException {
        PdfPCell cell;
        Paragraph p;
        float[] widths = {0.29f, 0.03f, 0.08f, 0.08f, 0.04f, 0.08f, 0.08f, 0.03f, 0.29f};

        PdfPTable Table;
        Table = new PdfPTable(widths);
        Table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        Table.setTotalWidth(widths);

        if (!first) {
            /*
             * Space
             */
            p = new Paragraph(" ");
            cell = new PdfPCell(p);
            cell.setColspan(9);
            cell.setBorderWidth(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            //cell.setBackgroundColor(new Color(255, 255, 255));
            Table.addCell(cell);
        }

        /*
         * Team 1
         */
        p = new Paragraph(f.team1.returnName(), FontFactory.getFont(font, fontSize + 2, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setColspan(4);
        cell.setBorderWidth(1);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new Color(225, 225, 225));
        Table.addCell(cell);

        /*
         * Separation Draw Fights
         */
        p = new Paragraph(" ", FontFactory.getFont(font, fontSize + 2, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setColspan(1);
        cell.setBorderWidth(border);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new Color(255, 255, 255));
        Table.addCell(cell);

        /*
         * Team 2
         */
        p = new Paragraph(f.team2.returnName(), FontFactory.getFont(font, fontSize + 2, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setColspan(4);
        cell.setBorderWidth(1);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new Color(225, 225, 225));
        Table.addCell(cell);

        for (int i = 0; i < f.team1.getNumberOfMembers(f.level); i++) {
            /*
             * Competitor Team1
             */
            p = new Paragraph(f.team1.getMember(i, f.level).returnSurnameNameIni(), FontFactory.getFont(font, fontSize + 2, Font.BOLD));
            cell = new PdfPCell(p);
            cell.setColspan(1);
            cell.setBorderWidth(1);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBackgroundColor(new Color(255, 255, 255));
            Table.addCell(cell);

            /*
             * Faults
             */
            if (f.duels.get(i).faultsCompetitorA > 0) {
                p = new Paragraph("" + Score.FAULT.getAbbreviature(), FontFactory.getFont(font, fontSize + 2, Font.BOLD));
            } else {
                p = new Paragraph("" + Score.EMPTY.getAbbreviature(), FontFactory.getFont(font, fontSize + 2, Font.BOLD));
            }
            cell = new PdfPCell(p);
            cell.setColspan(1);
            cell.setBorderWidth(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setBackgroundColor(new Color(255, 255, 255));
            Table.addCell(cell);

            /*
             * Point 2
             */
            p = new Paragraph(f.duels.get(i).hitsFromCompetitorA.get(1).getAbbreviature() + "", FontFactory.getFont(font, fontSize + 2, Font.BOLD));
            cell = new PdfPCell(p);
            cell.setColspan(1);
            cell.setBorderWidth(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new Color(255, 255, 255));
            Table.addCell(cell);

            /*
             * Point 1
             */
            p = new Paragraph(f.duels.get(i).hitsFromCompetitorA.get(0).getAbbreviature() + "", FontFactory.getFont(font, fontSize + 2, Font.BOLD));
            cell = new PdfPCell(p);
            cell.setColspan(1);
            cell.setBorderWidth(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new Color(255, 255, 255));
            Table.addCell(cell);

            /*
             * Separation Draw Fights
             */
            if (f.duels.get(i).winner() == 0 && f.isOver() != 2) {
                p = new Paragraph("" + Score.DRAW.getAbbreviature(), FontFactory.getFont(font, fontSize + 2, Font.BOLD));
            } else {
                p = new Paragraph("" + Score.EMPTY.getAbbreviature(), FontFactory.getFont(font, fontSize + 2, Font.BOLD));
            }
            cell = new PdfPCell(p);
            cell.setColspan(1);
            cell.setBorderWidth(border);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new Color(255, 255, 255));
            Table.addCell(cell);

            /*
             * Point 1
             */
            p = new Paragraph(f.duels.get(i).hitsFromCompetitorB.get(0).getAbbreviature() + "", FontFactory.getFont(font, fontSize + 2, Font.BOLD));
            cell = new PdfPCell(p);
            cell.setColspan(1);
            cell.setBorderWidth(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new Color(255, 255, 255));
            Table.addCell(cell);

            /*
             * Point 2
             */
            p = new Paragraph(f.duels.get(i).hitsFromCompetitorB.get(1).getAbbreviature() + "", FontFactory.getFont(font, fontSize + 2, Font.BOLD));
            cell = new PdfPCell(p);
            cell.setColspan(1);
            cell.setBorderWidth(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new Color(255, 255, 255));
            Table.addCell(cell);

            /*
             * Faults
             */
            if (f.duels.get(i).faultsCompetitorB > 0) {
                p = new Paragraph("" + Score.FAULT.getAbbreviature(), FontFactory.getFont(font, fontSize + 2, Font.BOLD));
            } else {
                p = new Paragraph("" + Score.EMPTY.getAbbreviature(), FontFactory.getFont(font, fontSize + 2, Font.BOLD));
            }
            cell = new PdfPCell(p);
            cell.setColspan(1);
            cell.setBorderWidth(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell.setBackgroundColor(new Color(255, 255, 255));
            Table.addCell(cell);

            /*
             * Competitor Team2
             */
            try {
                p = new Paragraph(f.team2.getMember(i, f.level).returnSurnameNameIni(), FontFactory.getFont(font, fontSize + 2, Font.BOLD));
            } catch (NullPointerException npe) {
                p = new Paragraph("", FontFactory.getFont(font, fontSize + 2, Font.BOLD));
            }
            cell = new PdfPCell(p);
            cell.setColspan(1);
            cell.setBorderWidth(1);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setBackgroundColor(new Color(255, 255, 255));
            Table.addCell(cell);
        }

        /*
         * Separation Line
         */
        p = new Paragraph(" ", FontFactory.getFont(font, fontSize + 2, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setColspan(7);
        cell.setBorderWidth(border);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        Table.addCell(cell);

        return Table;
    }

    @Override
    public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        PdfPCell cell;
        Paragraph p;

        float cellNumber = 0;
        boolean first = true;
        int lastLevel = 0;

        List<Fight> fights = null;
        if (useOnlyShiaijo < 0) {
            fights = KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(championship.name);
        } else {
            fights = KendoTournamentGenerator.getInstance().database.searchFightsByTournamentNameAndFightArea(championship.name, useOnlyShiaijo);
        }


        for (int i = 0; i < fights.size(); i++) {

            /*
             * Header of the phase
             */
            if (lastLevel != fights.get(i).level) {
                p = new Paragraph(trans.returnTag("Round", KendoTournamentGenerator.getInstance().language) + " " + (fights.get(i).level + 1) + ":", FontFactory.getFont(font, fontSize + 10, Font.BOLD));
                cell = new PdfPCell(p);
                cell.setColspan(6);
                cell.setBorderWidth(border);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                //cell.setBackgroundColor(new Color(255, 255, 255));
                mainTable.addCell(cell);
                cellNumber += 3;
                lastLevel = fights.get(i).level;
            }

            /*
             * Border Line
             */
            p = new Paragraph(" ", FontFactory.getFont(font, fontSize + 2, Font.BOLD));
            cell = new PdfPCell(p);
            cell.setColspan(1);
            cell.setBorderWidth(border);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.addCell(cell);
            try {
                cell = new PdfPCell(FightTable(font, fontSize - 5, fights.get(i), first));
            } catch (DocumentException ex) {
                cell = new PdfPCell();
                KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            }
            cell.setBorderWidth(border);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.addCell(cell);
            cellNumber += fights.get(i).team1.getNumberOfMembers(fights.get(i).level) + 2;

            /*
             * Border Line
             */
            p = new Paragraph(" ", FontFactory.getFont(font, fontSize + 2, Font.BOLD));
            cell = new PdfPCell(p);
            cell.setColspan(1);
            cell.setBorderWidth(border);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.addCell(cell);

            first = false;

            //Add a new page if is necessary...
           /*
             * if ((cellNumber > linesByPage) && (i < fights.size() - 1)) {
             * mainTable.writeSelectedRows(0, -1, 0,
             * document.getPageSize().getHeight() - 40,
             * writer.getDirectContent()); mainTable.flushContent();
             * document.newPage(); AddBackGroundImage(document,
             * Path.returnBackgroundPath()); cellNumber = 0; first = true; }
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

        p = new Paragraph(championship.name, FontFactory.getFont(font, fontSize + 15, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setColspan(3);
        cell.setBorderWidth(headerBorder);
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
