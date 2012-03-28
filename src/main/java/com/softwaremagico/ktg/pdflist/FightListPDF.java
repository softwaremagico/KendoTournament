/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.pdflist;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.MessageManager;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.leaguedesigner.DesignedGroup;
import com.softwaremagico.ktg.leaguedesigner.DesignedGroups;
import java.awt.Color;
import java.io.FileOutputStream;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author jorge
 */
public class FightListPDF extends ParentList {

    private Tournament championship;
    private final int border = 0;
    private final int teamsByPage = 40;
    Translator trans = null;

    public FightListPDF(Tournament tmp_championship) {
        championship = tmp_championship;
        trans = new Translator("gui.xml");
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
                MessageManager.customMessage("fightsListOK", "PDF", KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
            } catch (NullPointerException npe) {
                MessageManager.errorMessage("fightsListBad", "PDF", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                error = true;
                KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            } catch (Exception ex) {
                MessageManager.errorMessage("fightsListBad", "PDF", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                error = true;
            }
        }
        return !error;
    }

    public PdfPTable fightTable(Fight f, String font, int fontSize) {
        Paragraph p;
        PdfPCell cell;
        float[] widths = {0.010f, 0.40f, 0.05f, 0.40f, 0.05f};
        PdfPTable fightTable = new PdfPTable(widths);
        fightTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);


        p = new Paragraph(f.team1.returnName() + " Vs " + f.team2.returnName(), FontFactory.getFont(font, fontSize + 2, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setBorderWidth(border);
        cell.setColspan(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        fightTable.addCell(cell);

        for (int i = 0; i < f.team1.getNumberOfMembers(f.level); i++) {
            p = new Paragraph(" ");
            cell = new PdfPCell(p);
            cell.setBorderWidth(border);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            fightTable.addCell(cell);

            p = new Paragraph(f.team1.getMember(i, f.level).returnSurnameNameIni(), FontFactory.getFont(font, fontSize));
            cell = new PdfPCell(p);
            cell.setBorderWidth(border);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            fightTable.addCell(cell);

            p = new Paragraph(" ");
            cell = new PdfPCell(p);
            cell.setBorderWidth(border);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            fightTable.addCell(cell);

            p = new Paragraph(f.team2.getMember(i, f.level).returnSurnameNameIni(), FontFactory.getFont(font, fontSize));
            cell = new PdfPCell(p);
            cell.setBorderWidth(border);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            fightTable.addCell(cell);

            p = new Paragraph(" ");
            cell = new PdfPCell(p);
            cell.setBorderWidth(border);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            fightTable.addCell(cell);
        }

        return fightTable;
    }

    private PdfPTable simpleTable(Document document, PdfWriter writer, PdfPTable mainTable, float[] widths, String font, int fontSize) {
        PdfPCell cell;
        Paragraph p;
        mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        int cellNumber = 0;

        for (int i = 0; i < championship.fightingAreas; i++) {
            List<Fight> fights = KendoTournamentGenerator.getInstance().database.searchFightsByTournamentNameAndFightArea(championship.name, i);

            p = new Paragraph(" ");
            cell = new PdfPCell(p);
            cell.setBorderWidth(border);
            cell.setColspan(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.addCell(cell);
            cellNumber++;

            p = new Paragraph("Shiaijo: " + KendoTournamentGenerator.getInstance().shiaijosName[i], FontFactory.getFont(font, fontSize + 6, Font.BOLD));
            cell = new PdfPCell(p);
            cell.setBorderWidth(border);
            cell.setColspan(5);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.addCell(cell);
            cellNumber += 1;

            for (int j = 0; j < fights.size(); j++) {
                p = new Paragraph(" ");
                cell = new PdfPCell(p);
                cell.setBorderWidth(border);
                cell.setColspan(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);

                cell = new PdfPCell(fightTable(fights.get(j), font, fontSize));
                cell.setBorderWidth(1);
                cell.setColspan(3);
                if (fights.get(j).isOver() < 2) {
                    cell.setBackgroundColor(new Color(200, 200, 200));
                } else {
                    cell.setBackgroundColor(new Color(255, 255, 255));
                }
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                mainTable.addCell(cell);

                p = new Paragraph(" ");
                cell = new PdfPCell(p);
                cell.setBorderWidth(border);
                cell.setColspan(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);

                cellNumber += 5;


                //Add a new page if is necessary...
               /*
                 * if ((cellNumber >= teamsByPage - 1) && (j < fights.size() -
                 * 1)) { // if ((cellNumber % fightsByPage == fightsByPage -
                 * championship.teamSize - 1) && (j < fights.size() - 1)) {
                 * mainTable.writeSelectedRows(0, -1, 0,
                 * document.getPageSize().getHeight() - 40,
                 * writer.getDirectContent()); mainTable.flushContent();
                 * document.newPage(); AddBackGroundImage(document,
                 * Path.returnBackgroundPath()); cellNumber = 0;
                 */

                /*
                 * Repeat header
                 */
                /*
                 * p = new Paragraph("Shiaijo: " +
                 * KendoTournamentGenerator.getInstance().shiaijosName[i],
                 * FontFactory.getFont(font, fontSize + 6, Font.BOLD)); cell =
                 * new PdfPCell(p); cell.setBorderWidth(border);
                 * cell.setColspan(5);
                 * cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                 * mainTable.addCell(cell); cellNumber += 1; }
                 */
            }

            //Add a new page if is necessary...
            /*
             * if ((cellNumber % teamsByPage >= teamsByPage - 1) && (i <
             * championship.fightingAreas - 1)) { mainTable.writeSelectedRows(0,
             * -1, 0, document.getPageSize().getHeight() - 40,
             * writer.getDirectContent()); mainTable.flushContent();
             * document.newPage(); AddBackGroundImage(document,
             * Path.returnBackgroundPath()); cellNumber = 0; }
             */
        }

        return mainTable;
    }

    private PdfPTable championshipTable(Document document, PdfWriter writer, PdfPTable mainTable, float[] widths, String font, int fontSize) {
        PdfPCell cell;
        Paragraph p;
        int cellNumber = 0;

        KendoTournamentGenerator.getInstance().fights.getFightsFromDatabase(championship.name);
        KendoTournamentGenerator.getInstance().designedGroups = new DesignedGroups(championship, KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        KendoTournamentGenerator.getInstance().designedGroups.refillDesigner(KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(championship.name));

        /*
         * Championship
         */
        p = new Paragraph(championship.name, FontFactory.getFont(font, fontSize + 15, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setColspan(5);
        cell.setBorderWidth(border);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        //cell.setBackgroundColor(new Color(255, 255, 255));
        mainTable.addCell(cell);
        cellNumber += 4;

        p = new Paragraph(" ");
        cell = new PdfPCell(p);
        cell.setBorderWidth(border);
        cell.setColspan(5);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        mainTable.addCell(cell);
        cellNumber++;

        for (int l = 0; l < KendoTournamentGenerator.getInstance().designedGroups.returnNumberOfLevels(); l++) {
            /*
             * Header of the phase
             */
            p = new Paragraph(trans.returnTag("Round", KendoTournamentGenerator.getInstance().language) + " " + (l + 1) + ":", FontFactory.getFont(font, fontSize + 10, Font.BOLD));
            cell = new PdfPCell(p);
            cell.setColspan(5);
            cell.setBorderWidth(border);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            //cell.setBackgroundColor(new Color(255, 255, 255));
            mainTable.addCell(cell);
            cellNumber += 2;

            List<DesignedGroup> groups = KendoTournamentGenerator.getInstance().designedGroups.returnGroupsOfLevel(l);

            for (int i = 0; i < groups.size(); i++) {
                /*
                 * Header of the group
                 */
                p = new Paragraph(" ");
                cell = new PdfPCell(p);
                cell.setBorderWidth(border);
                cell.setColspan(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);
                cellNumber++;

                /*
                 * left margin
                 */
                p = new Paragraph(" ");
                cell = new PdfPCell(p);
                cell.setBorderWidth(border);
                cell.setColspan(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);

                p = new Paragraph(trans.returnTag("GroupString", KendoTournamentGenerator.getInstance().language) + " " + (i + 1) + " (" + trans.returnTag("FightArea", KendoTournamentGenerator.getInstance().language) + " " + KendoTournamentGenerator.getInstance().shiaijosName[groups.get(i).getShiaijo(KendoTournamentGenerator.getInstance().fights.getFights())] + ")", FontFactory.getFont(font, fontSize + 2, Font.BOLD));
                cell = new PdfPCell(p);
                cell.setColspan(3);
                cell.setBorderWidth(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(new Color(255, 255, 255));
                mainTable.addCell(cell);

                /*
                 * right margin
                 */
                p = new Paragraph(" ");
                cell = new PdfPCell(p);
                cell.setBorderWidth(border);
                cell.setColspan(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);

                cellNumber += 2;

                for (int j = 0; j < KendoTournamentGenerator.getInstance().fights.size(); j++) {
                    if (groups.get(i).isFightOfGroup(KendoTournamentGenerator.getInstance().fights.get(j))) {

                        p = new Paragraph(" ");
                        cell = new PdfPCell(p);
                        cell.setBorderWidth(border);
                        cell.setColspan(1);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        mainTable.addCell(cell);

                        cell = new PdfPCell(fightTable(KendoTournamentGenerator.getInstance().fights.get(j), font, fontSize));
                        cell.setBorderWidth(1);
                        cell.setColspan(3);
                        if (KendoTournamentGenerator.getInstance().fights.get(j).isOver() < 2) {
                            cell.setBackgroundColor(new Color(200, 200, 200));
                        } else {
                            cell.setBackgroundColor(new Color(255, 255, 255));
                        }
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        //cell.setBackgroundColor(new Color(255, 255, 255));
                        mainTable.addCell(cell);
                        cellNumber += 5;

                        p = new Paragraph(" ");
                        cell = new PdfPCell(p);
                        cell.setBorderWidth(border);
                        cell.setColspan(1);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        mainTable.addCell(cell);

                        //Add a new page if is necessary...
                        /*
                         * if ((cellNumber >= teamsByPage - 1) && (j <
                         * KendoTournamentGenerator.getInstance().fights.size()
                         * - 1)) { mainTable.writeSelectedRows(0, -1, 0,
                         * document.getPageSize().getHeight() - 40,
                         * writer.getDirectContent()); mainTable.flushContent();
                         * document.newPage(); AddBackGroundImage(document,
                         * Path.returnBackgroundPath()); cellNumber = 0; }
                         */
                    }
                }

                //Add a new page if is necessary...
                /*
                 * if ((cellNumber >= teamsByPage - 1) && (i < groups.size() -
                 * 1)) { mainTable.writeSelectedRows(0, -1, 0,
                 * document.getPageSize().getHeight() - 40,
                 * writer.getDirectContent()); mainTable.flushContent();
                 * document.newPage(); AddBackGroundImage(document,
                 * Path.returnBackgroundPath()); cellNumber = 0; }
                 */
            }
        }
        return mainTable;
    }

    @Override
    public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        if (championship.mode.equals("simple")) {
            simpleTable(document, writer, mainTable, getTableWidths(), font, fontSize - 2);
        } else {
            championshipTable(document, writer, mainTable, getTableWidths(), font, fontSize - 2);
        }
    }

    @Override
    public float[] getTableWidths() {
        float[] widths = {0.10f, 0.40f, 0.05f, 0.40f, 0.05f};
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
        p = new Paragraph(championship.name, FontFactory.getFont(font, fontSize + 16, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setBorderWidth(headerBorder);
        cell.setColspan(getTableWidths().length);
        cell.setMinimumHeight(50);
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
