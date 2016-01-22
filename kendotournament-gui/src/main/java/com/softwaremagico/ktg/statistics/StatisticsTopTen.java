package com.softwaremagico.ktg.statistics;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> Valencia (Spain).
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

import java.sql.SQLException;
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

import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.RoleTag;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.gui.fight.FightPanel;
import com.softwaremagico.ktg.language.ITranslator;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.persistence.RolePool;
import com.softwaremagico.ktg.tournament.ScoreOfCompetitor;

public class StatisticsTopTen extends StatisticsGUI {
	private static final long serialVersionUID = 8825408636741174219L;
	private ITranslator transl;
	private Tournament tournament;
	private List<ScoreOfCompetitor> competitorTopTen;
	private int startRange = 0;
	private List<RegisteredPerson> competitors;
	private boolean changesAllowed = false;

	public StatisticsTopTen(Tournament tournament) {
		this.tournament = tournament;
		try {
			Ranking ranking;
			if (tournament == null) {
				ranking = new Ranking(FightPool.getInstance().getAll());
				competitorTopTen = ranking.getCompetitorsScoreRanking();
			} else {
				ranking = new Ranking(FightPool.getInstance().get(tournament));
				competitorTopTen = ranking.getCompetitorsScoreRanking();
			}
			transl = LanguagePool.getTranslator("gui.xml");
			start();
			NumberSpinner.setVisible(true);
			NumberLabel.setVisible(true);
			this.setExtendedState(this.getExtendedState() | FightPanel.MAXIMIZED_BOTH);
			if (tournament != null) {
				competitors = RolePool.getInstance().getPeople(tournament, RoleTag.competitorsRoles);
			} else {
				competitors = RolePool.getInstance().getPeople(RoleTag.competitorsRoles);
			}
			fillSelectComboBox();
			changesAllowed = true;
			NumberLabel.setText(trans.getTranslatedText("NumberCompetitorsLabel"));
		} catch (SQLException ex) {
			AlertManager.showSqlErrorMessage(ex);
		}
	}

	@Override
	public void generateStatistics() {
		// Statistics stats = new Statistics();
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
		final String series1 = transl.getTranslatedText("WonFights");
		final String series2 = transl.getTranslatedText("PerformedHitStatistics");

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

		int padding = 0; // If the result can not be centered because is in the
							// last one, move the graphic.
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
			String c = (i + 1) + " - " + competitorTopTen.get(i).getCompetitor().getName() + " "
					+ competitorTopTen.get(i).getCompetitor().getSurname();

			dataset.addValue(competitorTopTen.get(i).getDuelsWon(), series1, c);
			dataset.addValue(competitorTopTen.get(i).getHits(), series2, c);
		}
		return dataset;
	}

	private JFreeChart createChart(CategoryDataset dataset) {
		// create the chart...
		final JFreeChart chart = ChartFactory.createBarChart(transl.getTranslatedText("TopTenTitle"), // chart
																										// title
				"", transl.getTranslatedText("NumberOfWinnedTopTen"), // domain
																		// axis
																		// label
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

		// Change orientation of labels.
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

	public void updateComboBox(RegisteredPerson competitor) {
		if (competitor != null) {
			SelectComboBox.setSelectedItem(
					competitor.getSurname() + ", " + competitor.getName() + " (" + competitor.getId() + ")");
		}
	}

	private void fillSelectComboBox() {
		SelectComboBox.setEnabled(true);
		SelectComboBox.removeAllItems();
		SelectComboBox.setVisible(true);

		for (int i = 0; i < competitors.size(); i++) {
			SelectComboBox.addItem(competitors.get(i).getSurname() + ", " + competitors.get(i).getName() + " ("
					+ competitors.get(i).getId() + ")");
		}

		// Select the winner.
		try {
			SelectComboBox.setSelectedItem(competitorTopTen.get(0).getCompetitor().getSurname() + ", "
					+ competitorTopTen.get(0).getCompetitor().getName() + " ("
					+ competitorTopTen.get(0).getCompetitor().getId() + ")");
		} catch (NullPointerException | IndexOutOfBoundsException npe) {
		}
	}

	private int searchForCompetitorPosition(RegisteredPerson c) {
		for (int i = 0; i < competitorTopTen.size(); i++) {
			if (competitorTopTen.get(i).getCompetitor().getId().equals(c.getId())) {
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
