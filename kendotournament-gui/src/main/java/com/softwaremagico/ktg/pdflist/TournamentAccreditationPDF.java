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
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.RolePool;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author jorge
 */
public class TournamentAccreditationPDF {

    Tournament tournament;
    private final int border = 1;
    private boolean all;

    public TournamentAccreditationPDF(Tournament tmp_championship) throws Exception {
        tournament = tmp_championship;
    }

    public void setPrintAll(boolean value) {
        all = value;
    }

    public void createFile(String file) {
        TimerPanel tp = new TimerPanel();
        ThreadAccreditation ta = new ThreadAccreditation(tp, file, all);
        ta.start();
    }

    public class ThreadAccreditation extends Thread {

        TimerPanel timerPanel;
        String path;
        Translator transl;
        boolean all;
        TournamentAccreditation ta;

        public ThreadAccreditation(TimerPanel tp, String p, boolean printAll) {
            transl = LanguagePool.getTranslator("gui.xml");
            timerPanel = tp;
            tp.updateTitle(transl.returnTag("AccreditationProgressBarTitle"));
            tp.updateLabel(transl.returnTag("AccreditationProgressBarLabel"));
            path = p;
            all = printAll;
        }

        @Override
        public void run() {
            ta = new TournamentAccreditation(path);
        }

        public class TournamentAccreditation extends PdfDocument {

            TournamentAccreditation(String file) {
                createFile(file);
            }

            @Override
            protected Rectangle getPageSize() {
                return PageSize.A4;
            }

            @Override
            protected String fileCreatedOkTag() {
                return "tournamentOK";
            }

            @Override
            protected String fileCreatedBadTag() {
                return "tournamentBad";
            }

            @Override
            protected void createPagePDF(Document document, PdfWriter writer, String font) throws Exception {
                PdfPTable table = pageTable(document, writer);
                table.setWidthPercentage(100);
                document.add(table);
                timerPanel.dispose();
            }

            private PdfPTable pageTable(Document document, PdfWriter writer) throws IOException, BadElementException, Exception {
                PdfPCell cell;
                float[] widths = {0.50f, 0.50f};
                PdfPTable mainTable = new PdfPTable(widths);
                mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.setTotalWidth(document.getPageSize().getWidth());
                com.itextpdf.text.Image banner = com.itextpdf.text.Image.getInstance(Path.getBannerPath());


                List<RegisteredPerson> competitors;
                if (all) {
                    competitors = RolePool.getInstance().getPeople(tournament);
                } else {
                    competitors = RolePool.getInstance().getRegisteredPeopleInTournamenteWithoutAccreditation(tournament);
                }

                for (int i = 0; i < competitors.size(); i++) {
                    timerPanel.updateText(transl.returnTag("AccreditationProgressBarLabel"), i, competitors.size());
                    CompetitorAccreditationCardPDF competitorPDF = new CompetitorAccreditationCardPDF(competitors.get(i), tournament, banner);
                    PdfPTable competitorTable = competitorPDF.pageTable(document.getPageSize().getWidth() / 2, document.getPageSize().getHeight() / 2, writer, font, fontSize);
                    competitorTable.setTableEvent(new PdfDocument.TableBgEvent());
                    cell = new PdfPCell(competitorTable);
                    cell.setBorderWidth(border);
                    cell.setColspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.addElement(competitorTable);
                    mainTable.addCell(cell);
                }
                timerPanel.updateLabel(transl.returnTag("WrittingToDisk"));
                mainTable.completeRow();
                return mainTable;
            }
        }
    }
}
