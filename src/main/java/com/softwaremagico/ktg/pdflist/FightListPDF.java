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
import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.TournamentTypes;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.tournament.TournamentGroup;
import com.softwaremagico.ktg.tournament.TournamentGroupPool;
import java.util.List;

/**
 *
 * @author jorge
 */
public class FightListPDF extends ParentList {

    private Tournament championship;

    public FightListPDF(Tournament tmp_championship) {
        championship = tmp_championship;
        trans = LanguagePool.getTranslator("gui.xml");
    }

    public PdfPTable fightTable(Fight f, String font, int fontSize) {
        float[] widths = getTableWidths();
        PdfPTable fightTable = new PdfPTable(widths);
        fightTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        fightTable.addCell(getHeader3(f.team1.returnName() + " Vs " + f.team2.returnName(), 0));

        for (int i = 0; i < f.team1.getNumberOfMembers(f.level); i++) {
            fightTable.addCell(getCell(f.team1.getMember(i, f.level).getSurnameNameIni(), 1, Element.ALIGN_LEFT));
            fightTable.addCell(getEmptyCell());
            fightTable.addCell(getCell(f.team2.getMember(i, f.level).getSurnameNameIni(), 1, Element.ALIGN_RIGHT));
        }

        return fightTable;
    }

    private PdfPTable simpleTable(PdfPTable mainTable) {
        PdfPCell cell;
        Paragraph p;
        mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        for (int i = 0; i < championship.fightingAreas; i++) {
            List<Fight> fights = KendoTournamentGenerator.getInstance().database.searchFightsByTournamentAndFightArea(championship, i);
            mainTable.addCell(getEmptyRow());
            mainTable.addCell(getEmptyRow());
            mainTable.addCell(getHeader2("Shiaijo: " + KendoTournamentGenerator.getInstance().returnShiaijo(i), 0));

            for (int j = 0; j < fights.size(); j++) {
                cell = new PdfPCell(fightTable(fights.get(j), font, fontSize));
                cell.setBorderWidth(0);
                cell.setColspan(3);
                //if (fights.get(j).isOver()) {
                //    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
                //} else {
                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(255, 255, 255));
                //}
                //cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                mainTable.addCell(cell);
            }
        }

        return mainTable;
    }

    private PdfPTable championshipTable(PdfPTable mainTable) {
        PdfPCell cell;

        KendoTournamentGenerator.getInstance().fightManager.getFightsFromDatabase(championship);
        
        for (int l = 0; l < TournamentGroupPool.getManager(championship).getLevels().size(); l++) {
            /*
             * Header of the phase
             */
            mainTable.addCell(getEmptyRow());
            mainTable.addCell(getEmptyRow());
            mainTable.addCell(getHeader1(trans.returnTag("Round") + " " + (l + 1) + ":", 0, Element.ALIGN_LEFT));

            List<TournamentGroup> groups = TournamentGroupPool.getManager(championship).returnGroupsOfLevel(l);

            for (int i = 0; i < groups.size(); i++) {
                mainTable.addCell(getEmptyRow());
                mainTable.addCell(getHeader2(trans.returnTag("GroupString") + " " + (i + 1) + " (" + trans.returnTag("FightArea") + " " + KendoTournamentGenerator.getInstance().returnShiaijo(groups.get(i).getShiaijo(KendoTournamentGenerator.getInstance().fightManager.getFights())) + ")", 0));

                for (int j = 0; j < KendoTournamentGenerator.getInstance().fightManager.size(); j++) {
                    if (groups.get(i).isFightOfGroup(KendoTournamentGenerator.getInstance().fightManager.get(j))) {

                        cell = new PdfPCell(fightTable(KendoTournamentGenerator.getInstance().fightManager.get(j), font, fontSize));
                        cell.setBorderWidth(1);
                        cell.setColspan(3);
                        //if (KendoTournamentGenerator.getInstance().fightManager.get(j).isOver()) {
                        //    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(200, 200, 200));
                        // } else {
                        cell.setBackgroundColor(new com.itextpdf.text.BaseColor(255, 255, 255));
                        //}
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        mainTable.addCell(cell);
                    }
                }
            }
        }
        return mainTable;
    }

    @Override
    public void createBodyRows(Document document, PdfPTable mainTable, float width, float height, PdfWriter writer, String font, int fontSize) {
        if (championship.mode.equals(TournamentTypes.SIMPLE)) {
            simpleTable(mainTable);
        } else {
            championshipTable(mainTable);
        }
    }

    @Override
    public float[] getTableWidths() {
        float[] widths = {0.40f, 0.10f, 0.40f};
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
        p = new Paragraph(championship.getName(), FontFactory.getFont(font, fontSize + 16, Font.BOLD));
        cell = new PdfPCell(p);
        cell.setBorderWidth(headerBorder);
        cell.setColspan(getTableWidths().length);
        cell.setMinimumHeight(50);
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
        return "fightsListOK";
    }

    @Override
    protected String fileCreatedBadTag() {
        return "fightsListBad";
    }
}
