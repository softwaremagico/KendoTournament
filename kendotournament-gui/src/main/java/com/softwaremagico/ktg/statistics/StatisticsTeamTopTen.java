package com.softwaremagico.ktg.statistics;
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

import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.ScoreOfTeam;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.FightPool;
import com.softwaremagico.ktg.database.TeamPool;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
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

public class StatisticsTeamTopTen extends StatisticsGUI {

    private Translator transl;
    private List<ScoreOfTeam> teamTopTen;
    private int startRange = 0;
    private List<Team> teams;
    private boolean changesAllowed = false;
    private Tournament tournament;

    public StatisticsTeamTopTen(Tournament tournament) {
        this.tournament = tournament;
        //teamTopTen = DatabaseConnection.getInstance().getDatabase().getTeamsOrderByScore(tmp_championship, false);
        Ranking ranking;
        if (tournament != null) {
            ranking = new Ranking(FightPool.getInstance().get(tournament));
            teams = TeamPool.getInstance().get(tournament);
        } else {
            ranking = new Ranking(FightPool.getInstance().getAll());
            teams = TeamPool.getInstance().getAll();
        }
        teamTopTen = ranking.getTeamsScoreRanking();
        transl = LanguagePool.getTranslator("gui.xml");
        start();
        NumberSpinner.setVisible(true);
        NumberLabel.setVisible(true);
        this.setExtendedState(this.getExtendedState() | StatisticsTeamTopTen.MAXIMIZED_BOTH);

        try {
            fillSelectComboBox();
            changesAllowed = true;
            NumberLabel.setText(trans.getTranslatedText("NumberTeamsLabel"));
        } catch (NullPointerException npe) {
        }
    }

    @Override
    public void generateStatistics() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String defaultFileName() {
        return "TeamsTopTen.png";
    }

    /**
     * Generate the set of data
     *
     * @return
     */
    private CategoryDataset createDataset() {

        // row keys...
        final String series1 = transl.getTranslatedText("WonMatchs");
        final String series2 = transl.getTranslatedText("DrawMatchs");
        final String series3 = transl.getTranslatedText("WonFights");
        final String series4 = transl.getTranslatedText("DrawFights");
        final String series5 = transl.getTranslatedText("PerformedHitStatistics");

        // create the dataset...
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        int centerValue;
        try {
            centerValue = searchForTeamPosition(teams.get(SelectComboBox.getSelectedIndex()));
        } catch (NullPointerException npe) {
            centerValue = 0;
        }
        int startValue = centerValue - returnNumberOfSpinner() / 2;
        if (startValue < 0) {
            startValue = 0;
        }

        int padding = 0; // If the result can not be centered because is in the last one, move the graphic.        
        int endValue = startValue + returnNumberOfSpinner();
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
                c = (i + 1) + " - " + teamTopTen.get(i).getTeam().getName() + " (" + teamTopTen.get(i).getTournament().getName() + ")";
            } else {
                c = (i + 1) + " - " + teamTopTen.get(i).getTeam().getName();
            }
            dataset.addValue(teamTopTen.get(i).getWonFights(), series1, c);
            dataset.addValue(teamTopTen.get(i).getDrawFights(), series2, c);
            dataset.addValue(teamTopTen.get(i).getWonDuels(), series3, c);
            dataset.addValue(teamTopTen.get(i).getDrawDuels(), series4, c);
            dataset.addValue(teamTopTen.get(i).getHits(), series5, c);
        }
        return dataset;
    }

    private JFreeChart createChart(CategoryDataset dataset) {
        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart(
                transl.getTranslatedText("TopTenTitle"), // chart title
                "",
                transl.getTranslatedText("NumberOfWinnedTopTen"), // domain axis label
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

    public void updateComboBox(Team team) {
        if (tournament == null) {
            SelectComboBox.setSelectedItem(team.getName() + " (" + team.getTournament().getName() + ")");
        } else {
            SelectComboBox.setSelectedItem(team.getName());
        }
    }

    private void fillSelectComboBox() {
        SelectComboBox.setEnabled(true);
        SelectComboBox.removeAllItems();
        SelectComboBox.setVisible(true);

        for (int i = 0; i < teams.size(); i++) {
            if (tournament == null) {
                SelectComboBox.addItem(teams.get(i).getName() + " (" + teams.get(i).getTournament().getName() + ")");
            } else {
                SelectComboBox.addItem(teams.get(i).getName());
            }
        }

        try {
            if (tournament == null) {
                SelectComboBox.setSelectedItem(teamTopTen.get(0).getTeam().getName() + " (" + teamTopTen.get(0).getTournament().getName() + ")");
            } else {
                SelectComboBox.setSelectedItem(teamTopTen.get(0).getTeam().getName());
            }
        } catch (NullPointerException | IndexOutOfBoundsException npe) {
        }
    }

    private int searchForTeamPosition(Team team) {
        for (int i = 0; i < teamTopTen.size(); i++) {
            if (teamTopTen.get(i).getTeam().equals(team)) {
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
