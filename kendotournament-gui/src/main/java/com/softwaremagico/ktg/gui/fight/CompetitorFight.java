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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import com.softwaremagico.ktg.core.Duel;
import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.KendoLog;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Score;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.persistence.AutoSaveByAction;
import com.softwaremagico.ktg.persistence.DuelPool;

import java.sql.SQLException;

public class CompetitorFight extends JPanel {
	private static final long serialVersionUID = -7099479717436351865L;
	private static final int SCORE_DIMENSION = 50;
	private static final int FAULTS_DIMENSION = 15;
	private static final int MARGIN = 5;
	private static final int NAME_CHARACTERS = 11;
	private PointPanel round1;
	private PointPanel round2;
	private PointPanel faults;
	private JLabel nameLabel;
	private RegisteredPerson competitor;
	private Fight fight;
	private Translator trans = null;
	private TeamFight teamFight;
	private GridBagConstraints gridBagConstraints;

	protected CompetitorFight(TeamFight teamFight, RegisteredPerson competitor, Fight fight, boolean left,
			boolean selected, boolean menu) {
		this.competitor = competitor;
		this.fight = fight;
		this.teamFight = teamFight;
		setLanguage();
		decoration();
		try {
			if (left) {
				if (competitor != null) {
					fillLeftToRight(competitor.getSurnameNameIni(NAME_CHARACTERS));
				} else {
					fillLeftToRight(" --- --- ");
				}
			} else {
				if (competitor != null) {
					fillRightToLeft(competitor.getSurnameNameIni(NAME_CHARACTERS));
				} else {
					fillRightToLeft(" --- --- ");
				}
			}
		} catch (NullPointerException npe) {
			AlertManager.showErrorInformation(this.getClass().getName(), npe);
		}
		if (menu) {
			addAllMenus();
		}
	}

	protected CompetitorFight(boolean left) {
		competitor = null;
		fight = null;
		setLanguage();
		decoration();
		if (left) {
			fillLeftToRight(" --- --- ");
		} else {
			fillRightToLeft(" --- --- ");
		}
	}

	protected void updateCompetitorNameLength(int width) {
		int characters = ((width - (SCORE_DIMENSION + MARGIN * 2) * 2) - (FAULTS_DIMENSION + MARGIN * 2)) / 28;
		if (characters > 3 && competitor != null) {
			nameLabel.setText(competitor.getSurnameNameIni(characters));
		}
	}

	/**
	 * Translate the GUI to the selected language.
	 */
	public final void setLanguage() {
		trans = LanguagePool.getTranslator("gui.xml");
	}

	private void fill(String name, int point1X, int point2X, int faultX, int nameX, boolean left) {
		round1 = new PointPanel();
		round1.setMinimumSize(new Dimension(SCORE_DIMENSION, SCORE_DIMENSION));
		round1.setPreferredSize(new Dimension(SCORE_DIMENSION, SCORE_DIMENSION));
		round1.setMaximumSize(new Dimension(SCORE_DIMENSION, SCORE_DIMENSION));
		round1.setBorder(BorderFactory.createLineBorder(Color.green));
		round1.setBackground(new Color(255, 255, 255));
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.gridx = point1X;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.insets = new Insets(MARGIN, MARGIN, MARGIN, MARGIN);
		add(round1, gridBagConstraints);

		round2 = new PointPanel();
		round2.setMinimumSize(new Dimension(SCORE_DIMENSION, SCORE_DIMENSION));
		round2.setPreferredSize(new Dimension(SCORE_DIMENSION, SCORE_DIMENSION));
		round2.setMaximumSize(new Dimension(SCORE_DIMENSION, SCORE_DIMENSION));
		round2.setBackground(new Color(255, 255, 255));
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.gridx = point2X;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.insets = new Insets(MARGIN, MARGIN, MARGIN, MARGIN);
		add(round2, gridBagConstraints);

		faults = new PointPanel();
		faults.setMinimumSize(new Dimension(FAULTS_DIMENSION, SCORE_DIMENSION));
		faults.setPreferredSize(new Dimension(FAULTS_DIMENSION, SCORE_DIMENSION));
		faults.setMaximumSize(new Dimension(FAULTS_DIMENSION, SCORE_DIMENSION));
		faults.setBackground(new Color(255, 255, 255));
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.gridx = faultX;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.insets = new Insets(MARGIN, MARGIN, MARGIN, MARGIN);
		add(faults, gridBagConstraints);

		JPanel namePanel;
		if (left) {
			namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		} else {
			namePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		}
		nameLabel = new JLabel(name);
		nameLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
		namePanel.add(nameLabel, BorderLayout.EAST);
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		if (left) {
			gridBagConstraints.anchor = GridBagConstraints.LINE_END;
		} else {
			gridBagConstraints.anchor = GridBagConstraints.LINE_START;
		}
		gridBagConstraints.gridx = nameX;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.insets = new Insets(MARGIN, MARGIN, MARGIN, MARGIN);
		add(namePanel, gridBagConstraints);
	}

	private void fillRightToLeft(String name) {
		fill(name, 0, 1, 2, 3, false);
	}

	private void fillLeftToRight(String name) {
		fill(name, 3, 2, 1, 0, true);
	}

	private void decoration() {
		setLayout(new GridBagLayout());
		gridBagConstraints = new GridBagConstraints();
	}

	private void updateScorePanel(PointPanel panel, Score hit) {
		panel.setBackground(Score.getImage(hit.getImageName()));
		panel.revalidate();
		panel.repaint();
	}

	private void updateFaultPanel(PointPanel panel, boolean fault) {
		if (fault) {
			panel.setBackground(Score.getImage(Score.FAULT.getImageName()));
		} else {
			panel.setBackground(Score.getImage(Score.EMPTY.getImageName()));
		}
		panel.revalidate();
		panel.repaint();
	}

	protected void updateScorePanels() {
		Integer player;
		try {
			if (fight != null) {
				if ((player = fight.getTeam1().getMemberOrder(competitor, fight.getIndex())) != null) {
					Duel d = fight.getDuels().get(player);
					updateScorePanel(round1, d.getHits(true).get(0));
					updateScorePanel(round2, d.getHits(true).get(1));
					updateFaultPanel(faults, d.getFaults(true));
				}
				if ((player = fight.getTeam2().getMemberOrder(competitor, fight.getIndex())) != null) {
					Duel d = fight.getDuels().get(player);
					updateScorePanel(round1, d.getHits(false).get(0));
					updateScorePanel(round2, d.getHits(false).get(1));
					updateFaultPanel(faults, d.getFaults(false));
				}
			}
		} catch (NullPointerException npe) {
			AlertManager.showErrorInformation(this.getClass().getName(), npe);
		}
	}

	private void addAllMenus() {
		if (fight.getTeam1().getNumberOfMembers(fight.getIndex()) > 0
				|| fight.getTeam2().getNumberOfMembers(fight.getIndex()) > 0) {
			addPopUpMenu();
		}
	}

	private void addPopUpMenu() {
		round1.setComponentPopupMenu(createContextMenu(0));
		round2.setComponentPopupMenu(createContextMenu(1));
		faults.setComponentPopupMenu(createFaultMenu());
	}

	private JPopupMenu createContextMenu(int round) {
		JPopupMenu contextMenu = new JPopupMenu();
		JMenuItem menMenu;
		try {
			for (Score s : Score.getValidPoints()) {
				menMenu = new JMenuItem();
				menMenu.setText(s.getName());
				menMenu.addActionListener(new MenuListener(round));
				contextMenu.add(menMenu);
			}

			menMenu = new JMenuItem();
			menMenu.setText(trans.getTranslatedText(Score.EMPTY.getName()));
			menMenu.addActionListener(new MenuListener(round));
			contextMenu.add(menMenu);
		} catch (IndexOutOfBoundsException iofb) {
		}

		return contextMenu;
	}

	private class MenuListener implements ActionListener {

		private int round;

		MenuListener(int tmp_round) {
			round = tmp_round;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JMenuItem sourceItem = (JMenuItem) e.getSource();

			if (sourceItem.getText().equals(trans.getTranslatedText(Score.EMPTY.getName()))) {
				updateDuel(Score.EMPTY, round);
			} else {
				updateDuel(Score.getScore(sourceItem.getText()), round);
			}
		}
	}

	private void updateDuel(Score point, int round) {
		Integer index;
		Duel duel = null;
		try {
			if ((index = fight.getTeam1().getMemberOrder(competitor, fight.getIndex())) != null) {
				duel = fight.getDuels().get(index);
				if (!point.equals(Score.EMPTY)) {
					duel.setResultInRound(round, point, true);
				} else {
					duel.clearResultInRound(round, true);
				}
			}
			if ((index = fight.getTeam2().getMemberOrder(competitor, fight.getIndex())) != null) {
				duel = fight.getDuels().get(index);
				if (!point.equals(Score.EMPTY)) {
					duel.setResultInRound(round, point, false);
				} else {
					duel.clearResultInRound(round, false);
				}
			}
		} catch (NullPointerException npe) {
			AlertManager.showErrorInformation(this.getClass().getName(), npe);
		}
		teamFight.getRoundFight().updateScorePanels();
		if (duel != null) {
			try {
				DuelPool.getInstance().update(fight.getTournament(), duel);
				AutoSaveByAction.getInstance().save();
			} catch (SQLException ex) {
				KendoLog.errorMessage(this.getClass().getName(), ex);
			}
		}
	}

	private void increaseFault() {
		Integer index;
		Duel duel = null;
		try {
			if ((index = fight.getTeam1().getMemberOrder(competitor, fight.getIndex())) != null) {
				duel = fight.getDuels().get(index);
				duel.setFaults(true);
				// KendoTournamentGenerator.getInstance().fightManager.storeDuel(d,
				// fight, index);
				updateScorePanels();
			}
			if ((index = fight.getTeam2().getMemberOrder(competitor, fight.getIndex())) != null) {
				duel = fight.getDuels().get(index);
				duel.setFaults(false);
				// KendoTournamentGenerator.getInstance().fightManager.storeDuel(d,
				// fight, index);
				updateScorePanels();
			}
		} catch (NullPointerException npe) {
		}
		teamFight.getRoundFight().updateScorePanels();
		if (duel != null) {
			try {
				DuelPool.getInstance().update(fight.getTournament(), duel);
				AutoSaveByAction.getInstance().save();
			} catch (SQLException ex) {
				KendoLog.errorMessage(this.getClass().getName(), ex);
			}
		}
	}

	private void resetFault() {
		Integer index;
		Duel duel = null;
		try {
			if ((index = fight.getTeam1().getMemberOrder(competitor, fight.getIndex())) != null) {
				duel = fight.getDuels().get(index);
				duel.resetFaults(true);
				// KendoTournamentGenerator.getInstance().fightManager.storeDuel(d,
				// fight, index);
				updateScorePanels();
			}
			if ((index = fight.getTeam2().getMemberOrder(competitor, fight.getIndex())) != null) {
				duel = fight.getDuels().get(index);
				duel.resetFaults(false);
				// KendoTournamentGenerator.getInstance().fightManager.storeDuel(d,
				// fight, index);
				updateScorePanels();
			}
		} catch (NullPointerException npe) {
		}
		if (duel != null) {
			try {
				DuelPool.getInstance().update(fight.getTournament(), duel);
				AutoSaveByAction.getInstance().save();
			} catch (SQLException ex) {
				KendoLog.errorMessage(this.getClass().getName(), ex);
			}
		}
	}

	private class FaultMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			JMenuItem sourceItem = (JMenuItem) e.getSource();
			if (sourceItem.getText().equals(trans.getTranslatedText(Score.EMPTY.getName()))) {
				resetFault();
			} else if (sourceItem.getText().equals(trans.getTranslatedText(Score.FAULT.getName()))) {
				increaseFault();
			}
		}
	}

	private JPopupMenu createFaultMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		JMenuItem menMenu = new JMenuItem();
		try {

			menMenu.setText(trans.getTranslatedText(Score.FAULT.getName()));
			menMenu.addActionListener(new FaultMenuListener());
			contextMenu.add(menMenu);

			menMenu = new JMenuItem();
			menMenu.setText(trans.getTranslatedText(Score.EMPTY.getName()));
			menMenu.addActionListener(new FaultMenuListener());
			contextMenu.add(menMenu);

		} catch (IndexOutOfBoundsException iofb) {
		}

		return contextMenu;
	}
}
