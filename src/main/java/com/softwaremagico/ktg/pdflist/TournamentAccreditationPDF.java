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
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.language.Translator;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author jorge
 */
public class TournamentAccreditationPDF {

    Tournament championship;
    private final int border = 1;
    private boolean all;

    public TournamentAccreditationPDF(Tournament tmp_championship) throws Exception {
        championship = tmp_championship;
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
            transl = new Translator("gui.xml");
            timerPanel = tp;
            tp.updateTitle(transl.returnTag("AccreditationProgressBarTitle", KendoTournamentGenerator.getInstance().language));
            tp.updateLabel(transl.returnTag("AccreditationProgressBarLabel", KendoTournamentGenerator.getInstance().language));
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
            }

            private PdfPTable pageTable(Document document, PdfWriter writer) throws IOException, BadElementException, Exception {
                PdfPCell cell;
                float[] widths = {0.50f, 0.50f};
                PdfPTable mainTable = new PdfPTable(widths);
                mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.setTotalWidth(document.getPageSize().getWidth());

                List<CompetitorWithPhoto> competitors = KendoTournamentGenerator.getInstance().database.selectAllParticipantsInTournamentWithoutAccreditation(championship.name, all);

                for (int i = 0; i < competitors.size(); i++) {
                    timerPanel.updateText(transl.returnTag("AccreditationProgressBarLabel", KendoTournamentGenerator.getInstance().language), i, competitors.size());
                    CompetitorAccreditationCardPDF competitorPDF = new CompetitorAccreditationCardPDF(competitors.get(i), championship);
                    PdfPTable competitorTable = competitorPDF.pageTable(document.getPageSize().getWidth() / 2, document.getPageSize().getHeight() / 2, writer, font, fontSize);
                     competitorTable.setTableEvent(new PdfDocument.TableBgEvent());
                    cell = new PdfPCell(competitorTable);
                    cell.setBorderWidth(border);
                    cell.setColspan(1);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.addElement(competitorTable);
                    mainTable.addCell(cell);
                }

                mainTable.completeRow();
                timerPanel.dispose();
                return mainTable;
            }
        }
    }
}
