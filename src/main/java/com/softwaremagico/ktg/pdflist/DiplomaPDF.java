package com.softwaremagico.ktg.pdflist;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.softwaremagico.ktg.*;
import com.softwaremagico.ktg.database.DatabaseConnection;
import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.statistics.CompetitorRanking;
import com.softwaremagico.ktg.statistics.TeamRanking;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

/**
 *
 * @author Jorge
 */
public class DiplomaPDF {

    protected int fontSize = 17;
    private final int border = 0;
    private boolean statistics;
    Tournament tournament;
    float nameposition = 100;
    List<Duel> duels = new ArrayList<>();
    static int TOTAL_RANGES = 9;
    List<TeamRanking> teamTopTen;
    List<CompetitorRanking> competitorTopTen;
    RoleTags rolesWithDiploma = null;
    private boolean allDiplomas;

    public DiplomaPDF(Tournament tmp_championship, boolean tmp_statistics, boolean printAllDiplomas, RoleTags roles) {
        tournament = tmp_championship;
        statistics = tmp_statistics;
        allDiplomas = printAllDiplomas;
        rolesWithDiploma = roles;
    }

    public boolean generateDiplomaPDF(String path, float nposition) {
        nameposition = nposition;
        //DIN A6 105 x 148 mm
        Document document = new Document(PageSize.A4.rotate());

        if (!path.endsWith(".pdf")) {
            path += ".pdf";
        }

        if (!MyFile.fileExist(path) || MessageManager.questionMessage("existFile", "Warning!")) {
            TimerPanel tp = new TimerPanel();
            //tp.dispose();
            ThreadDiploma td = new ThreadDiploma(tp, document, path);
            td.start();
        }
        return true;
    }

    public class ThreadDiploma extends Thread {

        TimerPanel timerPanel;
        Document document;
        String path;
        Translator transl;
        private com.itextpdf.text.Image background;

        public ThreadDiploma(TimerPanel tp, Document d, String p) {
            transl = LanguagePool.getTranslator("gui.xml");
            timerPanel = tp;
            tp.updateTitle(transl.returnTag("DiplomaProgressBarTitle"));
            tp.updateLabel(transl.returnTag("DiplomaProgressBarLabel"));
            document = d;
            path = p;
        }

        @Override
        public void run() {
            convertPDF();
        }

        public boolean convertPDF() {
            boolean error;
            try {
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
                generatePDF(document, writer);
                MessageManager.translatedMessage(this.getClass().getName(), "diplomaOK", "PDF", JOptionPane.INFORMATION_MESSAGE);
                DatabaseConnection.getInstance().getDatabase().setAllParticipantsInTournamentAsDiplomaPrinted(rolesWithDiploma, tournament);
                error = false;
            } catch (NullPointerException npe) {
                MessageManager.errorMessage(this.getClass().getName(), "noTournamentFieldsFilled", "MySQL");
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
                error = true;
            } catch (Exception ex) {
                MessageManager.errorMessage(this.getClass().getName(), "diplomaBad", "PDF");
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
                error = true;
            }
            timerPanel.dispose();
            return error;
        }

        void addBackGroundImage(Document document, String imagen) throws BadElementException,
                DocumentException, MalformedURLException, IOException {
            //com.itextpdf.text.Image png;

            if (background == null) {
                background = com.itextpdf.text.Image.getInstance(imagen);
            }
            background.setAlignment(com.itextpdf.text.Image.UNDERLYING);
            background.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());
            background.setAbsolutePosition(0, 0);
            document.add(background);
        }

        private Document documentData(Document document) {
            document.addTitle("Kendo Tournament Diploma List");
            document.addAuthor("Jorge Hortelano");
            document.addCreator("Kendo Tournament Tool");
            document.addSubject("List of Diplomas for all participant");
            document.addKeywords("Kendo, Tournament, Diploma");
            document.addCreationDate();
            return document;
        }

        void generatePDF(Document document, PdfWriter writer) throws Exception {
            String font = FontFactory.HELVETICA;
            documentData(document);
            document.open();
            pagePDF(document, writer, font);
            document.close();
        }

        private void pagePDF(Document document, PdfWriter writer, String font) throws Exception {
            pageTable(document, document.getPageSize().getWidth(), document.getPageSize().getHeight(), writer, font, fontSize);
        }

        public void pageTable(Document document, float width, float height, PdfWriter writer, String font, int fontSize) throws IOException, BadElementException, Exception {
            List<RegisteredPerson> competitors;

            competitors = DatabaseConnection.getInstance().getDatabase().selectAllCompetitorWithDiplomaInTournament(rolesWithDiploma, tournament, allDiplomas);

            if (statistics) {
                duels = DatabaseConnection.getInstance().getDatabase().getDuelsOfTournament(tournament);
                //teamTopTen = DatabaseConnection.getInstance().getDatabase().getTeamsOrderByScore(tournament.name, false);
                Ranking ranking = new Ranking();
                teamTopTen = ranking.getRanking(DatabaseConnection.getInstance().getDatabase().searchFightsByTournament(tournament));
                competitorTopTen = DatabaseConnection.getInstance().getDatabase().getCompetitorsOrderByScore(true, tournament);
            }

            for (int i = 0; i < competitors.size(); i++) {
                timerPanel.updateText(transl.returnTag("DiplomaProgressBarLabel") + ": " + (i + 1) + "/" + competitors.size(), i, competitors.size());
                diplomaTable(document, writer, competitors.get(i), font, fontSize);
                if (statistics) {
                    statisticsTable(document, writer, competitors.get(i), font, fontSize);
                }
            }
        }

        private void diplomaTable(Document document, PdfWriter writer, RegisteredPerson competitor, String font, int fontSize) throws BadElementException, DocumentException, MalformedURLException, IOException {
            PdfPCell cell;
            Paragraph p;
            PdfPTable mainTable = new PdfPTable(1);
            mainTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.setTotalWidth(document.getPageSize().getWidth());

            document.newPage();
            addBackGroundImage(document, Path.getDiplomaPath());

            p = new Paragraph(competitor.getSurname() + ", " + competitor.getName(), FontFactory.getFont(font, fontSize + 20));
            cell = new PdfPCell(p);
            cell.setBorderWidth(border);
            cell.setColspan(1);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

            mainTable.addCell(cell);
            mainTable.writeSelectedRows(0, -1, 0, document.getPageSize().getHeight() * nameposition + 27, writer.getDirectContent());
            //mainTable.flushContent();
            mainTable.setWidthPercentage(100);
            //document.add(mainTable);
        }

        private void statisticsTable(Document document, PdfWriter writer, RegisteredPerson competitor, String font, int fontSize) {
            try {
                PdfPCell cell;
                float[] width = {(float) 0.3, (float) 0.3, (float) 0.3};
                PdfPTable mainTable = new PdfPTable(width);

                document.newPage();

                //General hits.
                Image image = com.itextpdf.text.Image.getInstance(createPieChart(createGeneralHitsDataset(), transl.returnTag("TitleHits")), null, false);
                cell = new PdfPCell(image, true);
                cell.setBorderWidth(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);


                List<Duel> duelsTeamRight;
                List<Duel> duelsTeamLeft;

                duelsTeamRight = DatabaseConnection.getInstance().getDatabase().getDuelsOfcompetitor(competitor.getId(), true);
                duelsTeamLeft = DatabaseConnection.getInstance().getDatabase().getDuelsOfcompetitor(competitor.getId(), false);

                //Performed hits.
                image = com.itextpdf.text.Image.getInstance(createPieChart(createPerformedHitsDataset(duelsTeamRight, duelsTeamLeft), transl.returnTag("PerformedHitsStatisticsMenuItem")), null, false);
                cell = new PdfPCell(image, true);
                cell.setBorderWidth(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);


                //Received hits.
                image = com.itextpdf.text.Image.getInstance(createPieChart(createReceivedHitsDataset(duelsTeamRight, duelsTeamLeft), transl.returnTag("ReceivedHitsStatisticsMenuItem")), null, false);
                cell = new PdfPCell(image, true);
                cell.setBorderWidth(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);


                Paragraph p = new Paragraph(" ");
                cell = new PdfPCell(p);
                cell.setBorderWidth(0);
                cell.setColspan(3);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);

                //Team ranking.
                image = com.itextpdf.text.Image.getInstance(createBarChart(createTeamRankingDataset(competitor), transl.returnTag("TopTenTeamTitle"), transl.returnTag("NumberOfWinnedTopTen")), null, false);
                cell = new PdfPCell(image, true);
                cell.setBorderWidth(1);
                cell.setColspan(3);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);


                p = new Paragraph(" ");
                cell = new PdfPCell(p);
                cell.setBorderWidth(0);
                cell.setColspan(3);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);

                //All score.
                cell = new PdfPCell(RankingTable(competitor, font, fontSize));
                cell.setBorderWidth(0);
                cell.setColspan(3);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                mainTable.addCell(cell);

                document.add(mainTable);
            } catch (DocumentException | IOException ex) {
                Logger.getLogger(DiplomaPDF.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private DefaultPieDataset createGeneralHitsDataset() {
            DefaultPieDataset dataset = new DefaultPieDataset();
            int mems = 0, kotes = 0, tsukis = 0, hansokus = 0, does = 0, ippones = 0;
            for (int i = 0; i < duels.size(); i++) {
                Duel d = duels.get(i);
                mems += d.getMems();
                kotes += d.getKotes();
                tsukis += d.getTsukis();
                hansokus += d.getHansokus();
                does += d.getDoes();
                ippones += d.getIppones();
            }

            float total = mems + kotes + tsukis + hansokus + does + ippones;

            dataset.setValue("Men (" + mems * 100 / total + "%)", (float) mems);
            dataset.setValue("Kote (" + kotes * 100 / total + "%)", (float) kotes);
            dataset.setValue("Tsuki (" + tsukis * 100 / total + "%)", (float) tsukis);
            dataset.setValue("Do (" + does * 100 / total + "%)", (float) does);
            dataset.setValue("Ippon (" + ippones * 100 / total + "%)", (float) ippones);
            dataset.setValue("Hansoku (" + hansokus * 100 / total + "%)", (float) hansokus);
            return dataset;
        }

        private DefaultPieDataset createPerformedHitsDataset(List<Duel> duelsTeamRight, List<Duel> duelsTeamLeft) {
            DefaultPieDataset dataset = new DefaultPieDataset();
            int mems = 0, kotes = 0, tsukis = 0, hansokus = 0, does = 0, ippones = 0;
            for (int i = 0; i < duelsTeamRight.size(); i++) {
                Duel d = duelsTeamRight.get(i);
                mems += d.getMems(false);
                kotes += d.getKotes(false);
                tsukis += d.getTsukis(false);
                hansokus += d.getHansokus(false);
                does += d.getDoes(false);
                ippones += d.getIppones(false);
            }

            for (int i = 0; i < duelsTeamLeft.size(); i++) {
                Duel d = duelsTeamLeft.get(i);
                mems += d.getMems(true);
                kotes += d.getKotes(true);
                tsukis += d.getTsukis(true);
                hansokus += d.getHansokus(true);
                does += d.getDoes(true);
                ippones += d.getIppones(true);
            }

            float total = mems + kotes + tsukis + hansokus + does + ippones;

            dataset.setValue("Men (" + mems * 100 / total + "%)", (float) mems);
            dataset.setValue("Kote (" + kotes * 100 / total + "%)", (float) kotes);
            dataset.setValue("Tsuki (" + tsukis * 100 / total + "%)", (float) tsukis);
            dataset.setValue("Do (" + does * 100 / total + "%)", (float) does);
            dataset.setValue("Ippon (" + ippones * 100 / total + "%)", (float) ippones);
            dataset.setValue("Hansoku (" + hansokus * 100 / total + "%)", (float) hansokus);
            return dataset;
        }

        private DefaultPieDataset createReceivedHitsDataset(List<Duel> duelsTeamRight, List<Duel> duelsTeamLeft) {
            DefaultPieDataset dataset = new DefaultPieDataset();
            int mems = 0, kotes = 0, tsukis = 0, hansokus = 0, does = 0, ippones = 0;
            for (int i = 0; i < duelsTeamRight.size(); i++) {
                Duel d = duelsTeamRight.get(i);
                mems += d.getMems(true);
                kotes += d.getKotes(true);
                tsukis += d.getTsukis(true);
                hansokus += d.getHansokus(true);
                does += d.getDoes(true);
                ippones += d.getIppones(true);
            }

            for (int i = 0; i < duelsTeamLeft.size(); i++) {
                Duel d = duelsTeamLeft.get(i);
                mems += d.getMems(false);
                kotes += d.getKotes(false);
                tsukis += d.getTsukis(false);
                hansokus += d.getHansokus(false);
                does += d.getDoes(false);
                ippones += d.getIppones(false);

            }

            float total = mems + kotes + tsukis + hansokus + does + ippones;

            dataset.setValue("Men (" + mems * 100 / total + "%)", (float) mems);
            dataset.setValue("Kote (" + kotes * 100 / total + "%)", (float) kotes);
            dataset.setValue("Tsuki (" + tsukis * 100 / total + "%)", (float) tsukis);
            dataset.setValue("Do (" + does * 100 / total + "%)", (float) does);
            dataset.setValue("Ippon (" + ippones * 100 / total + "%)", (float) ippones);
            dataset.setValue("Hansoku (" + hansokus * 100 / total + "%)", (float) hansokus);
            return dataset;
        }

        private int searchForTeamPosition(Team team) {
            if (team == null) {
                return -1;
            }
            for (int i = 0; i < teamTopTen.size(); i++) {
                if (teamTopTen.get(i).name.equals(team.getName()) && teamTopTen.get(i).tournament.equals(team.tournament)) {
                    return i;
                }
            }
            return -1;
        }

        private CategoryDataset createTeamRankingDataset(RegisteredPerson competitor) {
            // row keys...
            final String series1 = transl.returnTag("WonMatchs");
            final String series2 = transl.returnTag("DrawMatchs");
            final String series3 = transl.returnTag("WonFights");
            final String series4 = transl.returnTag("DrawFights");
            final String series5 = transl.returnTag("PerformedHitStatistics");

            // create the dataset...
            final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            int centerValue;
            try {
                centerValue = searchForTeamPosition(DatabaseConnection.getInstance().getDatabase().getTeamOfCompetitor(competitor.getId(), tournament, false));
            } catch (NullPointerException npe) {
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
                centerValue = 0;
            }
            int startValue = centerValue - TOTAL_RANGES / 2;
            if (startValue < 0) {
                startValue = 0;
            }

            int padding = 0; // If the result can not be centered because is in the last one, move the graphic.        
            int endValue = startValue + TOTAL_RANGES;
            if (endValue >= teamTopTen.size()) {
                padding = endValue - (teamTopTen.size());
                endValue = teamTopTen.size();
            }

            startValue = startValue - padding;
            if (startValue < 0) {
                startValue = 0;
            }
            for (int i = startValue; i < endValue; i++) {
                String c;
                if (tournament == null) {
                    c = (i + 1) + " - " + teamTopTen.get(i).name + " (" + teamTopTen.get(i).tournament + ")";
                } else {
                    c = (i + 1) + " - " + teamTopTen.get(i).name;
                }
                dataset.addValue(teamTopTen.get(i).wonMatchs, series1, c);
                dataset.addValue(teamTopTen.get(i).drawMatchs, series2, c);
                dataset.addValue(teamTopTen.get(i).wonFights, series3, c);
                dataset.addValue(teamTopTen.get(i).drawFights, series4, c);
                dataset.addValue(teamTopTen.get(i).score, series5, c);
            }
            return dataset;
        }

        private BufferedImage createPieChart(DefaultPieDataset dataset, String title) {

            // create the chart…
            JFreeChart chart = ChartFactory.createPieChart(
                    title, // Titulo de grafico
                    dataset, // data
                    true, // incluye leyenda
                    true, // visualiza tooltips
                    false // urls
                    );
            BufferedImage image = chart.createBufferedImage(600, 400);
            return image;
        }

        private BufferedImage createBarChart(CategoryDataset dataset, String title, String axis) {
            // create the chart...
            final JFreeChart chart = ChartFactory.createBarChart(
                    title, // chart title
                    "",
                    axis, // domain axis label
                    dataset, // data
                    PlotOrientation.VERTICAL, // orientation
                    true, // include legend
                    true, // tooltips?
                    false // URLs?
                    );

            // get a reference to the plot for further customisation...
            final CategoryPlot plot = chart.getCategoryPlot();

            // set the range axis to display integers only...
            final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
            rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

            //Change orientation of labels. 
            final CategoryAxis domainAxis = plot.getDomainAxis();
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

            BufferedImage image = chart.createBufferedImage(1200, 300);
            return image;
        }

        private PdfPTable CompetitorTable(RegisteredPerson c, CompetitorRanking cr, int index, String font, int fontSize) {
            PdfPCell cell;
            Paragraph p;
            float[] width = {(float) 0.3, (float) 0.3, (float) 0.3};
            PdfPTable table = new PdfPTable(width);
            int start, end;

            if (!cr.id.equals(c.getId())) {
                p = new Paragraph((index + 1) + "º ", FontFactory.getFont(font, fontSize - 8));
            } else {
                p = new Paragraph((index + 1) + "º " + c.getAcronim(), FontFactory.getFont(font, fontSize - 8, Font.BOLD));
            }
            cell = new PdfPCell(p);
            cell.setBorderWidth(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);

            p = new Paragraph(cr.victorias + "", FontFactory.getFont(font, fontSize - 8));
            cell = new PdfPCell(p);
            cell.setBorderWidth(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            p = new Paragraph(cr.puntos + "", FontFactory.getFont(font, fontSize - 8));
            cell = new PdfPCell(p);
            cell.setBorderWidth(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);


            return table;
        }

        private PdfPTable RankingTable(RegisteredPerson comp, String font, int fontSize) {
            PdfPCell cell;
            float[] width = {(float) 0.2, (float) 0.2, (float) 0.2, (float) 0.2, (float) 0.2};
            PdfPTable table = new PdfPTable(width);
            int columns = 5;
            int files = 7;


            Paragraph p = new Paragraph(transl.returnTag("TopTenCompetitorTitle"));
            cell = new PdfPCell(p);
            cell.setBorderWidth(1);
            cell.setColspan(columns);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            for (int i = 0; i < columns; i++) {
                float[] width2 = {(float) 0.3, (float) 0.3, (float) 0.3};
                PdfPTable table2 = new PdfPTable(width2);

                p = new Paragraph(transl.returnTag("TopTenCompetitorNumber"), FontFactory.getFont(font, fontSize - 12));
                cell = new PdfPCell(p);
                cell.setBorderWidth(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.addCell(cell);

                p = new Paragraph(transl.returnTag("Fights"), FontFactory.getFont(font, fontSize - 12));
                cell = new PdfPCell(p);
                cell.setBorderWidth(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.addCell(cell);

                p = new Paragraph(transl.returnTag("Hits"), FontFactory.getFont(font, fontSize - 12));
                cell = new PdfPCell(p);
                cell.setBorderWidth(1);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table2.addCell(cell);

                table.addCell(table2);
            }

            //Only if are room left.
            int centerValue;
            try {
                centerValue = searchForCompetitorPosition(comp);
            } catch (NullPointerException npe) {
                centerValue = 0;
            }
            int startValue = centerValue - files * columns / 2;
            if (startValue < 0) {
                startValue = 0;
            }

            int padding = 0; // If the result can not be centered because is in the last one, move the graphic.
            int endValue = startValue + files * columns;
            if (endValue >= competitorTopTen.size()) {
                padding = endValue - (competitorTopTen.size());
                endValue = competitorTopTen.size();
            }

            startValue = startValue - padding;
            if (startValue < 0) {
                startValue = 0;
            }

            for (int i = startValue; i < endValue; i++) {
                cell = new PdfPCell(CompetitorTable(comp, competitorTopTen.get(i), i, font, fontSize));
                cell.setBorderWidth(1);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);
            }

            //Empty cells for finishing table.
            if ((endValue - startValue) % columns > 0) {
                for (int i = 0; i < (columns - ((endValue - startValue) % columns)); i++) {
                    p = new Paragraph(" ");
                    cell = new PdfPCell(p);
                    cell.setBorderWidth(1);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.addCell(cell);
                }
            }

            return table;
        }

        private int searchForCompetitorPosition(RegisteredPerson c) {
            for (int i = 0; i < competitorTopTen.size(); i++) {
                if (competitorTopTen.get(i).id.equals(c.getId())) {
                    return i;
                }
            }
            return -1;
        }
    }
}
