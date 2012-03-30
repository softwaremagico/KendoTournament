/*
 *  This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2012 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *  Created on 25-feb-2008.
 */
package com.softwaremagico.ktg.statistics;

import com.softwaremagico.ktg.*;
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
    private List<TeamRanking> teamTopTen;
    private int startRange = 0;
    private List<Team> teams;
    private boolean changesAllowed = false;
    private Tournament championship;

    public StatisticsTeamTopTen(Tournament tmp_championship) {
        championship = tmp_championship;
        //teamTopTen = KendoTournamentGenerator.getInstance().database.getTeamsOrderByScore(tmp_championship, false);
        Ranking ranking = new Ranking();
        if (championship != null) {
            teamTopTen = ranking.getRanking(KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(championship.name));
        } else {
            teamTopTen = ranking.getRanking(KendoTournamentGenerator.getInstance().database.getAllFights());
        }
        transl = new Translator("gui.xml");
        start();
        NumberSpinner.setVisible(true);
        NumberLabel.setVisible(true);
        this.setExtendedState(this.getExtendedState() | StatisticsTeamTopTen.MAXIMIZED_BOTH);
        try {
        if (tmp_championship.name.equals("All")) {
            teams = KendoTournamentGenerator.getInstance().database.getAllTeams();
        } else {
            teams = KendoTournamentGenerator.getInstance().database.searchTeamsByTournament(championship.name, false);
        }
        fillSelectComboBox();
        changesAllowed = true;
        NumberLabel.setText(trans.returnTag("NumberTeamsLabel", KendoTournamentGenerator.getInstance().language));
        } catch (NullPointerException npe) {
    }
    }

    @Override
    public void generateStatistics() {
        Statistics stats = new Statistics();
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String defaultFileName() {
        return "TeamsTopTen.png";
    }

    /**
     * Generate the set of data
     * @return 
     */
    private CategoryDataset createDataset() {

        // row keys...
        final String series1 = transl.returnTag("WonMatchs", KendoTournamentGenerator.getInstance().language);
        final String series2 = transl.returnTag("DrawMatchs", KendoTournamentGenerator.getInstance().language);
        final String series3 = transl.returnTag("WonFights", KendoTournamentGenerator.getInstance().language);
        final String series4 = transl.returnTag("DrawFights", KendoTournamentGenerator.getInstance().language);
        final String series5 = transl.returnTag("PerformedHitStatistics", KendoTournamentGenerator.getInstance().language);

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
            String c = "";
            if (championship == null) {
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

    private JFreeChart createChart(CategoryDataset dataset) {
        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart(
                transl.returnTag("TopTenTitle", KendoTournamentGenerator.getInstance().language), // chart title
                "",
                transl.returnTag("NumberOfWinnedTopTen", KendoTournamentGenerator.getInstance().language), // domain axis label
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

    public void updateComboBox(Team t) {
        if (championship.name.equals("All")) {
            SelectComboBox.setSelectedItem(t.returnName() + " (" + t.competition.name + ")");
        } else {
            SelectComboBox.setSelectedItem(t.returnName());
        }
    }

    private void fillSelectComboBox() {
        SelectComboBox.setEnabled(true);
        SelectComboBox.removeAllItems();
        SelectComboBox.setVisible(true);

        for (int i = 0; i < teams.size(); i++) {
            if (championship.name.equals("All")) {
                SelectComboBox.addItem(teams.get(i).returnName() + " (" + teams.get(i).competition.name + ")");
            } else {
                SelectComboBox.addItem(teams.get(i).returnName());
            }
        }

        try {
            if (championship.name.equals("All")) {
                SelectComboBox.setSelectedItem(teamTopTen.get(0).name + " (" + teamTopTen.get(0).tournament + ")");
            } else {
                SelectComboBox.setSelectedItem(teamTopTen.get(0).name);
            }
        } catch (NullPointerException npe) {
        } catch (IndexOutOfBoundsException iob) {
        }
    }

    private int searchForTeamPosition(Team t) {
        for (int i = 0; i < teamTopTen.size(); i++) {
            if (teamTopTen.get(i).name.equals(t.returnName()) && teamTopTen.get(i).tournament.equals(t.competition.name)) {
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
