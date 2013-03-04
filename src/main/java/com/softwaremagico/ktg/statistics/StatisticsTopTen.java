package com.softwaremagico.ktg.statistics;
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

import com.softwaremagico.ktg.Competitor;
import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.database.DatabaseConnection;
import com.softwaremagico.ktg.gui.fight.FightPanel;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class StatisticsTopTen extends StatisticsGUI {

    Translator transl;
    Tournament tournament;
    List<CompetitorRanking> competitorTopTen;
    int startRange = 0;
    private List<Competitor> competitors;
    boolean changesAllowed = false;

    public StatisticsTopTen(Tournament tournament) {
        this.tournament = tournament;
        if (tournament == null) {
            competitorTopTen = getCompetitorsOrderByScore();
        } else {
            competitorTopTen = getCompetitorsOrderByScoreInChampionship(tournament);
        }
        transl = LanguagePool.getTranslator("gui.xml");
        start();
        NumberSpinner.setVisible(true);
        NumberLabel.setVisible(true);
        this.setExtendedState(this.getExtendedState() | FightPanel.MAXIMIZED_BOTH);
        if (tournament != null) {
            competitors = DatabaseConnection.getInstance().getDatabase().selectAllCompetitorsInTournament(tournament);
        } else {
            competitors = DatabaseConnection.getInstance().getDatabase().getAllCompetitors();
        }
        fillSelectComboBox();
        changesAllowed = true;
        NumberLabel.setText(trans.returnTag("NumberCompetitorsLabel"));
    }

    public StatisticsTopTen() {
        //usa la formula vieja!! 
        competitorTopTen = getCompetitorsOrderByScore();
        transl = LanguagePool.getTranslator("gui.xml");
        start();
        NumberSpinner.setVisible(true);
        NumberLabel.setVisible(true);
        this.setExtendedState(this.getExtendedState() | FightPanel.MAXIMIZED_BOTH);
        competitors = DatabaseConnection.getInstance().getDatabase().getAllCompetitors();
        fillSelectComboBox();
        changesAllowed = true;
        NumberLabel.setText(trans.returnTag("NumberCompetitorsLabel"));
    }

    private List<CompetitorRanking> getCompetitorsOrderByScore() {
        List<Competitor> competitorsList = DatabaseConnection.getInstance().getDatabase().getAllCompetitors();
        List<CompetitorRanking> ranking = new ArrayList<>();
        List<Fight> fights = DatabaseConnection.getInstance().getDatabase().getAllFights();
        for (int i = 0; i < competitorsList.size(); i++) {
            if (competitorsList.get(i) != null) {
                int victories = obtainWinnedDuels(competitorsList.get(i), fights);
                int score = obtainTotalHits(competitorsList.get(i), fights);
                ranking.add(new CompetitorRanking(competitorsList.get(i).getName(), competitorsList.get(i).getSurname(), competitorsList.get(i).getId(), victories, score));
            }
        }
        return OrderCompetitorRanking(ranking);

    }

    private List<CompetitorRanking> getCompetitorsOrderByScoreInChampionship(Tournament tournament) {
        List<Competitor> competitorsList = DatabaseConnection.getInstance().getDatabase().selectAllCompetitorsInTournament(tournament);
        List<CompetitorRanking> ranking = new ArrayList<>();
        List<Fight> fights = DatabaseConnection.getInstance().getDatabase().searchFightsByTournament(tournament);
        for (int i = 0; i < competitorsList.size(); i++) {
            if (competitorsList.get(i) != null) {
                int victories = obtainWinnedDuels(competitorsList.get(i), fights);
                int score = obtainTotalHits(competitorsList.get(i), fights);
                ranking.add(new CompetitorRanking(competitorsList.get(i).getName(), competitorsList.get(i).getSurname(), competitorsList.get(i).getId(), victories, score));
            }
        }
        return OrderCompetitorRanking(ranking);

    }

    private List<CompetitorRanking> OrderCompetitorRanking(List<CompetitorRanking> competitorsScore) {
        List<CompetitorRanking> ordered = new ArrayList<>();
        while (!competitorsScore.isEmpty()) {
            int max = 0;
            int maxVictories = 0;
            int maxScore = 0;
            for (int i = 0; i < competitorsScore.size(); i++) {
                if (competitorsScore.get(i).victorias > maxVictories) {
                    max = i;
                    maxVictories = competitorsScore.get(i).victorias;
                    maxScore = competitorsScore.get(i).puntos;
                } else {
                    if (competitorsScore.get(i).victorias == maxVictories) {
                        //Draw victories but has more score.
                        if (competitorsScore.get(i).puntos > maxScore) {
                            max = i;
                            maxScore = competitorsScore.get(i).puntos;
                        }
                    }
                }
            }
            //Add competitior as the next with more score.
            ordered.add(competitorsScore.get(max));
            competitorsScore.remove(max);
        }
        return ordered;
    }

    public int obtainWinnedDuels(Competitor c, List<Fight> fights) {
        int won = 0;
        for (int i = 0; i < fights.size(); i++) {
            Integer index = fights.get(i).team1.getMemberOrder(fights.get(i).level, c.getId());
            if (index != null && index >= 0) {
                if (fights.get(i).duels.get(index).winner() < 0) {
                    won++;
                }
            }
            index = fights.get(i).team2.getMemberOrder(fights.get(i).level, c.getId());
            if (index != null && index >= 0) {
                if (fights.get(i).duels.get(index).winner() > 0) {
                    won++;
                }
            }
        }
        return won;
    }

    public int obtainTotalHits(Competitor c, List<Fight> fights) {
        int hits = 0;
        for (int i = 0; i < fights.size(); i++) {
            Integer index = fights.get(i).team1.getMemberOrder(fights.get(i).level, c.getId());
            if (index != null && index >= 0) {
                hits += fights.get(i).duels.get(index).howManyPoints(true);
            }
            index = fights.get(i).team2.getMemberOrder(fights.get(i).level, c.getId());
            if (index != null && index >= 0) {
                hits += fights.get(i).duels.get(index).howManyPoints(false);
            }
        }
        return hits;
    }

    @Override
    public void generateStatistics() {
        //Statistics stats = new Statistics();
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String defaultFileName() {
        return "TopTen.png";
    }

    /**
     * Generate the set of data
     *
     * @return
     */
    private CategoryDataset createDataset() {

        // row keys...
        final String series1 = transl.returnTag("WonFights");
        final String series2 = transl.returnTag("PerformedHitStatistics");

        // create the dataset...
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        int centerValue;
        try {
            centerValue = searchForCompetitorPosition(competitors.get(SelectComboBox.getSelectedIndex()));
        } catch (NullPointerException | ArrayIndexOutOfBoundsException npe) {
            centerValue = 0;
        }
        int startValue = centerValue - returnNumberOfSpinner() / 2;
        if (startValue < 0) {
            startValue = 0;
        }

        int padding = 0; // If the result can not be centered because is in the last one, move the graphic.
        int endValue = startValue + returnNumberOfSpinner();
        if (endValue >= competitorTopTen.size()) {
            padding = endValue - (competitorTopTen.size());
            endValue = competitorTopTen.size();
        }

        startValue = startValue - padding;
        if (startValue < 0) {
            startValue = 0;
        }

        for (int i = startValue; i < endValue; i++) {
            String c = (i + 1) + " - " + competitorTopTen.get(i).name + " " + competitorTopTen.get(i).surname;

            dataset.addValue(competitorTopTen.get(i).victorias, series1, c);
            dataset.addValue(competitorTopTen.get(i).puntos, series2, c);
        }
        return dataset;
    }

    private JFreeChart createChart(CategoryDataset dataset) {
        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart(
                transl.returnTag("TopTenTitle"), // chart title
                "",
                transl.returnTag("NumberOfWinnedTopTen"), // domain axis label
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


        return chart;
    }

    @Override
    public JPanel createPanel() {
        JFreeChart chart = createChart(createDataset());
        return new ChartPanel(chart);
    }

    @Override
    public void changeSelectComboBox() {
        if (changesAllowed) {
            JFreeChart chart = createChart(createDataset());
            ChartPanel cp = new ChartPanel(chart);

            GraphicPanel.removeAll();
            GraphicPanel.add(cp, 0);
            GraphicPanel.revalidate();
            cp.repaint();
        }
    }

    public void updateComboBox(Competitor c) {
        if (c != null) {
            SelectComboBox.setSelectedItem(c.getSurname() + ", " + c.getName() + " (" + c.getId() + ")");
        }
    }

    private void fillSelectComboBox() {
        SelectComboBox.setEnabled(true);
        SelectComboBox.removeAllItems();
        SelectComboBox.setVisible(true);

        for (int i = 0; i < competitors.size(); i++) {
            SelectComboBox.addItem(competitors.get(i).getSurname() + ", " + competitors.get(i).getName() + " (" + competitors.get(i).getId() + ")");
        }

        try {
            SelectComboBox.setSelectedItem(competitorTopTen.get(0).surname + ", " + competitorTopTen.get(0).name + " (" + competitorTopTen.get(0).id + ")");
        } catch (NullPointerException | IndexOutOfBoundsException npe) {
        }
    }

    private int searchForCompetitorPosition(Competitor c) {
        for (int i = 0; i < competitorTopTen.size(); i++) {
            if (competitorTopTen.get(i).id.equals(c.getId())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    void numberSpinnedChanged() {
        if ((Integer) NumberSpinner.getValue() < 3) {
            NumberSpinner.setValue(3);
        }

        JFreeChart chart = createChart(createDataset());
        ChartPanel cp = new ChartPanel(chart);

        GraphicPanel.removeAll();
        GraphicPanel.add(cp, 0);
        GraphicPanel.revalidate();
        cp.repaint();
    }
}
