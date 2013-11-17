package com.softwaremagico.ktg.gui.fight;

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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.ScoreOfTeam;
import com.softwaremagico.ktg.gui.base.KFrame;
import com.softwaremagico.ktg.gui.base.KLabel;
import com.softwaremagico.ktg.gui.base.KPanel;
import com.softwaremagico.ktg.gui.base.buttons.CloseButton;

public class RankingWindow extends KFrame {

	private final static String FONT = "Arial";
	private final static int AUTO_CLOSE_SECONDS = 30;
	private final static int MIN_ROWS = 6;
	private Timer timer = null;
	private Ranking ranking;
	private JScrollPane rankingScrollPane;

	public RankingWindow(Ranking ranking, boolean autoclose) {
		this.ranking = ranking;
		defineWindow(800, 400);
		setResizable(true);
		setElements();
		addResizedEvent();

		if (autoclose) {
			if (timer == null) {
				startTimer();
			}

			// Ensure that timer is closed.
			this.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					close();
				}
			});
		}

	}

	private void setElements() {
		getContentPane().removeAll();
		setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		rankingScrollPane = createRankingPanel();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = xPadding;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		getContentPane().add(rankingScrollPane, gridBagConstraints);

		KPanel buttonPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setMinimumSize(new Dimension(200, 50));
		CloseButton closeButton = new CloseButton(this);
		buttonPanel.add(closeButton);

		gridBagConstraints.anchor = GridBagConstraints.LINE_END;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.ipadx = xPadding;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.weighty = 0;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		getContentPane().add(buttonPanel, gridBagConstraints);
	}

	private JScrollPane createRankingPanel() {
		KPanel rankingPanel = new KPanel();
		GridLayout experimentLayout = new GridLayout(0, 4);
		rankingPanel.setLayout(experimentLayout);
		setTitle(rankingPanel, getFontSize() + 4);
		setTeams(rankingPanel, getFontSize());
		JScrollPane rankingScrollPane = new JScrollPane();
		rankingScrollPane.setViewportView(rankingPanel);
		rankingScrollPane.setBorder(BorderFactory.createEmptyBorder());
		return rankingScrollPane;
	}

	private void setTitle(KPanel rankingPanel, int titleFontSize) {
		KLabel teamLabel = new KLabel("TeamTopTenMenuItem");
		teamLabel.setBoldFont(true);
		teamLabel.setFont(new Font(FONT, Font.BOLD, titleFontSize));
		// teamLabel.setFontSize(titleFontSize);
		teamLabel.setHorizontalAlignment(JLabel.CENTER);
		rankingPanel.add(teamLabel);

		KLabel wonMatch = new KLabel("WonMatchs");
		wonMatch.setBoldFont(true);
		wonMatch.setFont(new Font(FONT, Font.BOLD, titleFontSize));
		// wonMatch.setFontSize(titleFontSize);
		wonMatch.setHorizontalAlignment(JLabel.CENTER);
		rankingPanel.add(wonMatch);

		KLabel wonFights = new KLabel("WonFights");
		wonFights.setBoldFont(true);
		wonFights.setFont(new Font(FONT, Font.BOLD, titleFontSize));
		// wonFights.setFontSize(titleFontSize);
		wonFights.setHorizontalAlignment(JLabel.CENTER);
		rankingPanel.add(wonFights);

		KLabel hits = new KLabel("PerformedHitStatistics");
		hits.setBoldFont(true);
		hits.setFont(new Font(FONT, Font.BOLD, titleFontSize));
		// hits.setFontSize(titleFontSize);
		hits.setHorizontalAlignment(JLabel.CENTER);
		rankingPanel.add(hits);
	}

	private void setTeams(KPanel rankingPanel, int teamsFontSize) {
		List<ScoreOfTeam> scores = ranking.getTeamsScoreRanking();
		int rows = 0;
		for (ScoreOfTeam score : scores) {
			addTeamLine(rankingPanel, teamsFontSize, score.getTeam().getShortName(15), score.getWonFights() + "/"
					+ score.getDrawFights(), score.getWonDuels() + "/" + score.getDrawDuels(), score.getHits()
					.toString());
			rows++;
		}
		while (rows < MIN_ROWS) {
			addTeamLine(rankingPanel, teamsFontSize, "", "", "", "");
			rows++;
		}
	}

	private void addTeamLine(KPanel rankingPanel, int teamsFontSize, String teamName, String fightsScore,
			String duelsScore, String hits) {
		KLabel teamLabel = new KLabel();
		teamLabel.setText(teamName);
		teamLabel.setFont(new Font(FONT, Font.PLAIN, teamsFontSize));
		// teamLabel.setFontSize(teamsFontSize);
		teamLabel.setHorizontalAlignment(JLabel.LEFT);
		rankingPanel.add(teamLabel);

		KLabel fightLabel = new KLabel();
		fightLabel.setText(fightsScore);
		fightLabel.setFont(new Font(FONT, Font.PLAIN, teamsFontSize));
		// fightLabel.setFontSize(teamsFontSize);
		fightLabel.setHorizontalAlignment(JLabel.CENTER);
		rankingPanel.add(fightLabel);

		KLabel duelLabel = new KLabel();
		duelLabel.setText(duelsScore);
		duelLabel.setFont(new Font(FONT, Font.PLAIN, teamsFontSize));
		// duelLabel.setFontSize(teamsFontSize);
		duelLabel.setHorizontalAlignment(JLabel.CENTER);
		rankingPanel.add(duelLabel);

		KLabel hitLabel = new KLabel();
		hitLabel.setText(hits);
		hitLabel.setFont(new Font(FONT, Font.PLAIN, teamsFontSize));
		// hitLabel.setFontSize(teamsFontSize);
		hitLabel.setHorizontalAlignment(JLabel.CENTER);
		rankingPanel.add(hitLabel);
	}

	private int getFontSize() {
		return this.getWidth() / 50;
	}

	@Override
	public void update() {
	}

	@Override
	public void elementChanged() {
	}

	private void addResizedEvent() {
		addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(java.awt.event.ComponentEvent evt) {
				setElements();
				revalidate();
			}
		});
	}

	private void startTimer() {
		timer = new Timer(AUTO_CLOSE_SECONDS * 1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				dispose();
			}
		});
		timer.setRepeats(false);
		timer.start();
	}

	private void close() {
		try {
			timer.stop();
			timer = null;
		} catch (NullPointerException npe) {
		}
		this.dispose();
	}
}
