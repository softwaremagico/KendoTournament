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
import com.softwaremagico.ktg.Score;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.language.Translator;
import java.util.List;

/**
 *
 * @author jorge
 */
public class SummaryPDF extends ParentList {

    private Tournament championship;
    private final int border = 0;
    Translator trans = null;
    private int useOnlyShiaijo = -1;

    public SummaryPDF(Tournament tmp_championship, int shiaijo) {
        championship = tmp_championship;
        trans = new Translator("gui.xml");
        useOnlyShiaijo = shiaijo;
    }

    private PdfPTable FightTable(Fight f, boolean first) throws DocumentException {

        PdfPTable Table;
        Table = new PdfPTable(getTableWidths());

        if (!first) {
            Table.addCell(getEmptyRow());
        }

        //Team1
        Table.addCell(getHeader3(f.team1.returnName(), 0, 4));


        //Separation Draw Fights
        Table.addCell(getEmptyCell());

        //Team2
        Table.addCell(getHeader3(f.team2.returnName(), 0, 4));

        for (int i = 0; i < f.team1.getNumberOfMembers(f.level); i++) {
            //Team 1
            Table.addCell(getCell(f.team1.getMember(i, f.level).returnSurnameNameIni(), 1, 1, Element.ALIGN_LEFT));

            //Faults
            String fault;
            if (f.duels.get(i).faultsCompetitorA > 0) {
                fault = "" + Score.FAULT.getAbbreviature();
            } else {
                fault = "" + Score.EMPTY.getAbbreviature();
            }

            Table.addCell(getCell(fault, 1, 1, Element.ALIGN_CENTER));

            //Points
            Table.addCell(getCell(f.duels.get(i).hitsFromCompetitorA.get(1).getAbbreviature() + "", 1, 1, Element.ALIGN_CENTER));
            Table.addCell(getCell(f.duels.get(i).hitsFromCompetitorA.get(0).getAbbreviature() + "", 1, 1, Element.ALIGN_CENTER));


            //Draw Fights
            String draw;
            if (f.duels.get(i).winner() == 0 && f.isOver() != 2) {
                draw = "" + Score.DRAW.getAbbreviature();
            } else {
                draw = "" + Score.EMPTY.getAbbreviature();
            }
            Table.addCell(getCell(draw, 1, Element.ALIGN_CENTER));

            //Points Team 2
            Table.addCell(getCell(f.duels.get(i).hitsFromCompetitorB.get(0).getAbbreviature() + "", 1, 1, Element.ALIGN_CENTER));
            Table.addCell(getCell(f.duels.get(i).hitsFromCompetitorB.get(1).getAbbreviature() + "", 1, 1, Element.ALIGN_CENTER));

            //Faults
            if (f.duels.get(i).faultsCompetitorB > 0) {
                fault = "" + Score.FAULT.getAbbreviature();
            } else {
                fault = "" + Score.EMPTY.getAbbreviature();
            }

            Table.addCell(getCell(fault, 1, 1, Element.ALIGN_CENTER));

            //Team 1
            Table.addCell(getCell(f.team2.getMember(i, f.level).returnSurnameNameIni(), 1, 1, Element.ALIGN_LEFT));
        }

        Table.addCell(getEmptyRow());

        return Table;
    }

    @Override
    public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        PdfPCell cell;

        boolean first = true;
        int lastLevel = -1;

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
                mainTable.addCell(getEmptyRow());
                mainTable.addCell(getHeader1(trans.returnTag("Round", KendoTournamentGenerator.getInstance().language) + " " + (fights.get(i).level + 1) + ":", 0, Element.ALIGN_LEFT));
                lastLevel = fights.get(i).level;
            }

            try {
                cell = new PdfPCell(FightTable(fights.get(i), first));
            } catch (DocumentException ex) {
                cell = new PdfPCell();
                KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            }
            cell.setBorderWidth(border);
            cell.setColspan(getTableWidths().length);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.addCell(cell);

            first = false;
        }
    }

    @Override
    public float[] getTableWidths() {
        float[] widths = {0.29f, 0.03f, 0.08f, 0.08f, 0.04f, 0.08f, 0.08f, 0.03f, 0.29f};
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
        cell.setColspan(getTableWidths().length);
        cell.setBorderWidth(headerBorder);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        mainTable.addCell(cell);
    }

    @Override
    public void createFooterRow(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        mainTable.addCell(getEmptyRow());
    }

    @Override
    protected Rectangle getPageSize() {
        return PageSize.A4;
    }

    @Override
    protected String fileCreatedOkTag() {
        return "SummaryListOK";
    }

    @Override
    protected String fileCreatedBadTag() {
        return "SummaryListBad";
    }
}
