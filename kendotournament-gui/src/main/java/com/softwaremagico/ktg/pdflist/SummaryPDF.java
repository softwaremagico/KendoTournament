package com.softwaremagico.ktg.pdflist;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 * Jorge Hortelano Otero <softwaremagico@gmail.com>
 * C/Quart 89, 3. Valencia CP:46008 (Spain).
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
 * along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.Score;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.core.TournamentType;
import com.softwaremagico.ktg.database.FightPool;
import java.util.List;

public class SummaryPDF extends ParentList {

    private Tournament tournament;
    private final int border = 0;
    private int useOnlyShiaijo = -1;
    protected boolean showNotFinishedFights = true; //If true, only show not finished fights, if false only show finished fights.
    protected boolean showAll = true; //If true, show finished and not finished fights;

    public SummaryPDF(Tournament tmp_championship, int shiaijo) {
        tournament = tmp_championship;
        useOnlyShiaijo = shiaijo;
    }

    protected String getDrawFight(Fight f, int duel) {
        //Draw Fights
        String draw;
        if (f.getDuels().get(duel).winner() == 0 && f.isOver()) {
            draw = "" + Score.DRAW.getAbbreviature();
        } else {
            draw = "" + Score.EMPTY.getAbbreviature();
        }
        return draw;
    }

    protected String getFaults(Fight f, int duel, boolean leftTeam) {
        String faultSimbol;
        boolean faults;
        if (leftTeam) {
            faults = f.getDuels().get(duel).getFaults(true);
        } else {
            faults = f.getDuels().get(duel).getFaults(false);
        }
        if (faults) {
            faultSimbol = "" + Score.FAULT.getAbbreviature();
        } else {
            faultSimbol = "" + Score.EMPTY.getAbbreviature();
        }
        return faultSimbol;
    }

    protected String getScore(Fight f, int duel, int score, boolean leftTeam) {
        if (leftTeam) {
            return f.getDuels().get(duel).getHits(true).get(score).getAbbreviature() + "";
        } else {
            return f.getDuels().get(duel).getHits(false).get(score).getAbbreviature() + "";
        }
    }

    private PdfPTable fightTable(Fight f, boolean first) throws DocumentException {
        PdfPTable Table;
        Table = new PdfPTable(getTableWidths());

        if (!first) {
            Table.addCell(getEmptyRow());
        }

        //Team1
        Table.addCell(getHeader3(f.getTeam1().getName(), 0, 4));

        //Separation Draw Fights
        Table.addCell(getEmptyCell());

        //Team2
        Table.addCell(getHeader3(f.getTeam2().getName(), 0, 4));

        for (int i = 0; i < f.getTeam1().getNumberOfMembers(f.getLevel()); i++) {
            //Team 1
            Table.addCell(getCell(f.getTeam1().getMember(i, f.getLevel()).getSurnameNameIni(), 1, 1, Element.ALIGN_LEFT));

            //Faults
            Table.addCell(getCell(getFaults(f, i, true), 1, 1, Element.ALIGN_CENTER));

            //Points
            Table.addCell(getCell(getScore(f, i, 1, true), 1, 1, Element.ALIGN_CENTER));
            Table.addCell(getCell(getScore(f, i, 0, true), 1, 1, Element.ALIGN_CENTER));

            Table.addCell(getCell(getDrawFight(f, i), 1, Element.ALIGN_CENTER));

            //Points Team 2
            Table.addCell(getCell(getScore(f, i, 0, false), 1, 1, Element.ALIGN_CENTER));
            Table.addCell(getCell(getScore(f, i, 1, false), 1, 1, Element.ALIGN_CENTER));

            //Faults
            Table.addCell(getCell(getFaults(f, i, false), 1, 1, Element.ALIGN_CENTER));

            //Team 2
            Table.addCell(getCell(f.getTeam2().getMember(i, f.getLevel()).getSurnameNameIni(), 1, 1, Element.ALIGN_RIGHT));
        }
        Table.addCell(getEmptyRow());

        return Table;
    }

    @Override
    public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        PdfPCell cell;

        boolean first = true;
        int lastLevel = -1;

        List<Fight> fights;
        if (useOnlyShiaijo < 0) {
            fights = FightPool.getInstance().get(tournament);
        } else {
            fights = FightPool.getInstance().get(tournament, useOnlyShiaijo);
        }


        for (int i = 0; i < fights.size(); i++) {
            if ((showAll) || (fights.get(i).isOver() == !showNotFinishedFights)) {
                /*
                 * Header of the phase
                 */
                if (lastLevel != fights.get(i).getLevel() && !tournament.getType().equals(TournamentType.SIMPLE)) {
                    mainTable.addCell(getEmptyRow());
                    mainTable.addCell(getHeader1(trans.getTranslatedText("Round") + " " + (fights.get(i).getLevel() + 1) + ":", 0, Element.ALIGN_LEFT));
                    lastLevel = fights.get(i).getLevel();
                }

                try {
                    cell = new PdfPCell(fightTable(fights.get(i), first));
                } catch (DocumentException ex) {
                    cell = new PdfPCell();
                    KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
                }
                cell.setBorderWidth(border);
                cell.setColspan(getTableWidths().length);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);

                first = false;
            }
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

        p = new Paragraph(tournament.getName(), FontFactory.getFont(font, fontSize + 15, Font.BOLD));
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
