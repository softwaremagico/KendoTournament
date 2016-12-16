package com.softwaremagico.ktg.gui.tournament.king;

/*
 * #%L
 * Kendo Tournament Manager GUI
 * %%
 * Copyright (C) 2008 - 2016 Softwaremagico
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.gui.base.KFrame;
import com.softwaremagico.ktg.gui.base.KLabel;
import com.softwaremagico.ktg.gui.base.KPanel;
import com.softwaremagico.ktg.gui.base.TeamComboBox;
import com.softwaremagico.ktg.gui.base.TournamentComboBox;
import com.softwaremagico.ktg.gui.base.buttons.CloseButton;
import com.softwaremagico.ktg.gui.base.buttons.KButton;
import com.softwaremagico.ktg.language.ITranslator;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.log.KendoLog;
import com.softwaremagico.ktg.persistence.AutoSaveByAction;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.persistence.TournamentPool;
import com.softwaremagico.ktg.tournament.PersonalizedFightsException;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;
import com.softwaremagico.ktg.tournament.TournamentType;
import com.softwaremagico.ktg.tournament.king.DrawResolution;
import com.softwaremagico.ktg.tournament.king.KingOfTheMountainTournament;

public class NewKingOfTheMountainTournament extends KFrame {
	private static final long serialVersionUID = -9216293186060151629L;
	private static final int BUTTON_WIDTH = 150;
	private ITranslator trans = null;
	private Tournament tournament;
	private TeamComboBox teamComboBox;
	private TournamentComboBox tournamentComboBox;
	private DefaultListModel<String> redTeamModel = new DefaultListModel<>();
	private DefaultListModel<String> whiteTeamModel = new DefaultListModel<>();
	private KingOfTheMountainTournament tournamentManager = null;
	private JRadioButton oldestEliminated, newestEliminated, bothEliminated;

	public NewKingOfTheMountainTournament() {
		defineWindow(450, 700);
		setResizable(false);
		setElements();
		setLanguage();
		fillTeams();
	}

	private void setElements() {
		getContentPane().removeAll();
		setLayout(new GridBagLayout());
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.ipadx = xPadding;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 0;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);

		KPanel tournamentPanel = createTournamentPanel();
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 5;
		getContentPane().add(tournamentPanel, gridBagConstraints);

		KPanel teamSelector = createTeamSelector();
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridheight = 2;
		getContentPane().add(teamSelector, gridBagConstraints);

		KPanel listTeams = createTeamList();

		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.gridheight = 4;
		getContentPane().add(listTeams, gridBagConstraints);

		final KFrame thisWindow = this;

		KPanel buttonPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setMinimumSize(new Dimension(200, 50));
		KButton acceptButton = new KButton();
		acceptButton.setTranslatedText("AcceptButton");
		acceptButton.setPreferredSize(new Dimension(80, 40));
		acceptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (AlertManager.questionMessage("questionCreateFight", "Warning!")) {
					tournament.setDrawResolution(getDrawResolution());
					createNewFights();
					thisWindow.dispose();
				}
			}
		});
		buttonPanel.add(acceptButton);

		CloseButton closeButton = new CloseButton(this);
		buttonPanel.add(closeButton);

		gridBagConstraints.anchor = GridBagConstraints.LINE_END;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.ipadx = xPadding;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 11;
		gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 0;
		gridBagConstraints.weighty = 0;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		getContentPane().add(buttonPanel, gridBagConstraints);
	}

	private KPanel createTournamentPanel() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.ipadx = xPadding;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 0;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);

		KPanel tournamentPanel = new KPanel(new GridBagLayout());

		KLabel tournamentLabel = new KLabel("championship");
		gridBagConstraints.gridx = 0;
		tournamentPanel.add(tournamentLabel, gridBagConstraints);

		// Select score system.
		oldestEliminated = new JRadioButton();
		newestEliminated = new JRadioButton();
		bothEliminated = new JRadioButton();

		tournamentComboBox = new TournamentComboBox(this);
		tournamentComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				KendoTournamentGenerator.getInstance().setLastSelectedTournament(tournamentComboBox.getSelectedItem().toString());
				tournament = (Tournament) tournamentComboBox.getSelectedItem();
				tournamentManager = null;
				try {
					getTournamentManager().setTeams(FightPool.getInstance().get(tournament));
				} catch (SQLException error) {
					KendoLog.errorMessage(this.getClass().getName(), error);
				}
				fillTeams();
				if (getTournamentManager() != null) {
					getRadioButton(tournament.getDrawResolution()).setSelected(true);
				}
			}
		});
		tournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
		tournamentComboBox.setWidth(280);
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.gridx = 1;
		tournamentPanel.add(tournamentComboBox, gridBagConstraints);

		ButtonGroup scoreGroup = new ButtonGroup();
		scoreGroup.add(oldestEliminated);
		scoreGroup.add(newestEliminated);
		scoreGroup.add(bothEliminated);

		gridBagConstraints.gridwidth = 4;
		gridBagConstraints.anchor = GridBagConstraints.LINE_START;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		tournamentPanel.add(oldestEliminated, gridBagConstraints);
		gridBagConstraints.gridy = 2;
		tournamentPanel.add(newestEliminated, gridBagConstraints);
		gridBagConstraints.gridy = 3;
		tournamentPanel.add(bothEliminated, gridBagConstraints);

		return tournamentPanel;
	}

	private KPanel createTeamSelector() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.ipadx = xPadding;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 0;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		KPanel rootPanel = new KPanel(new GridBagLayout());
		rootPanel.setBorder(BorderFactory.createEtchedBorder());

		KPanel teamPanel = new KPanel(new FlowLayout(FlowLayout.RIGHT));
		teamPanel.setMinimumSize(new Dimension(200, 50));

		KLabel teamLabel = new KLabel("Team");
		teamPanel.add(teamLabel);

		gridBagConstraints.gridy = 1;
		teamComboBox = new TeamComboBox(tournament, this);
		teamComboBox.setWidth(280);
		teamPanel.add(teamComboBox);
		rootPanel.add(teamPanel, gridBagConstraints);

		KPanel addButtonPanel = new KPanel(new FlowLayout(FlowLayout.CENTER));
		addButtonPanel.setMinimumSize(new Dimension(200, 50));

		KButton addToRedTeam = new KButton();
		addToRedTeam.setTranslatedText("redTeamButton");
		addToRedTeam.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (teamComboBox.getSelectedTeam() != null) {
					if (getTournamentManager() != null) {
						getTournamentManager().getRedTeams().add(teamComboBox.getSelectedTeam());
					}
					fillTeams();
				}
			}
		});
		addToRedTeam.setWidth(BUTTON_WIDTH);
		addButtonPanel.add(addToRedTeam);

		KButton addToWhiteTeam = new KButton();
		addToWhiteTeam.setTranslatedText("whiteTeamButton");
		addToWhiteTeam.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (teamComboBox.getSelectedTeam() != null) {
					if (getTournamentManager() != null) {
						getTournamentManager().getWhiteTeams().add(teamComboBox.getSelectedTeam());
					}
					fillTeams();
				}
			}
		});
		addToWhiteTeam.setWidth(BUTTON_WIDTH);
		addButtonPanel.add(addToWhiteTeam);

		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.CENTER;
		rootPanel.add(addButtonPanel, gridBagConstraints);

		return rootPanel;
	}

	private KPanel createTeamList() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.ipadx = xPadding;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 0;
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);

		KPanel listPanel = new KPanel(new GridBagLayout());

		gridBagConstraints.gridx = 0;
		KLabel redTeamLabel = new KLabel("RedTeam");
		listPanel.add(redTeamLabel);

		gridBagConstraints.gridx = 1;
		KLabel whiteTeamLabel = new KLabel("WhiteTeam");
		listPanel.add(whiteTeamLabel);

		final JList<String> redList = new JList<>();
		redList.setModel(redTeamModel);
		redList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		JScrollPane redTeamScrollPane = new JScrollPane();
		redTeamScrollPane.setViewportView(redList);

		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridheight = 4;
		gridBagConstraints.weighty = 1;
		listPanel.add(redTeamScrollPane, gridBagConstraints);

		final JList<String> whiteList = new JList<>();
		whiteList.setModel(whiteTeamModel);
		whiteList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		JScrollPane whiteTeamScrollPane = new JScrollPane();
		whiteTeamScrollPane.setViewportView(whiteList);
		gridBagConstraints.gridx = 1;
		listPanel.add(whiteTeamScrollPane, gridBagConstraints);

		KButton removeFromRedTeam = new KButton();
		removeFromRedTeam.setTranslatedText("DeleteButton");
		removeFromRedTeam.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeTeamSelection(redList, redTeamModel, getTournamentManager().getRedTeams());
				fillTeams();
			}
		});

		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.weighty = 0;
		listPanel.add(removeFromRedTeam, gridBagConstraints);

		KButton removeFromWhiteTeam = new KButton();
		removeFromWhiteTeam.setTranslatedText("DeleteButton");
		removeFromWhiteTeam.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeTeamSelection(whiteList, whiteTeamModel, getTournamentManager().getWhiteTeams());
				fillTeams();
			}
		});

		gridBagConstraints.gridx = 1;
		listPanel.add(removeFromWhiteTeam, gridBagConstraints);

		return listPanel;
	}

	private JRadioButton getRadioButton(DrawResolution drawResolution) {
		switch (drawResolution) {
		case OLDEST_ELIMINATED:
			return oldestEliminated;
		case NEWEST_ELIMINATED:
			return newestEliminated;
		case BOTH_ELIMINATED:
			return bothEliminated;
		}
		return oldestEliminated;
	}

	private DrawResolution getDrawResolution() {
		if (oldestEliminated.isSelected()) {
			return DrawResolution.OLDEST_ELIMINATED;
		}
		if (newestEliminated.isSelected()) {
			return DrawResolution.NEWEST_ELIMINATED;
		}
		if (bothEliminated.isSelected()) {
			return DrawResolution.BOTH_ELIMINATED;
		}
		return DrawResolution.OLDEST_ELIMINATED;
	}

	private void removeTeamSelection(JList<String> teamOptionList, DefaultListModel<String> teamModel, List<Team> teams) {
		int selectedTeam = teamOptionList.getSelectedIndex();
		if (selectedTeam >= 0) {
			teamModel.remove(selectedTeam);
			teams.remove(selectedTeam);
			if (selectedTeam < teamModel.getSize()) {
				teamOptionList.setSelectedIndex(selectedTeam);
			} else {
				teamOptionList.setSelectedIndex(teamModel.getSize() - 1);
			}
		}
	}

	private void setLanguage() {
		trans = LanguagePool.getTranslator("gui.xml");
		this.setTitle(trans.getTranslatedText("kingOfTheMountainTournament"));
		oldestEliminated.setText(trans.getTranslatedText("kingEliminateOldest"));
		newestEliminated.setText(trans.getTranslatedText("kingEliminateNewest"));
		bothEliminated.setText(trans.getTranslatedText("kingEliminateBoth"));
	}

	private void fillTeams() {
		if (teamComboBox != null) {
			Set<Team> hiddenTeams = new HashSet<>();
			if (getTournamentManager() != null) {
				hiddenTeams.addAll(getTournamentManager().getRedTeams());
				hiddenTeams.addAll(getTournamentManager().getWhiteTeams());
			}
			teamComboBox.setHiddenTeams((Tournament) tournamentComboBox.getSelectedItem(), hiddenTeams);
			teamComboBox.fillTeams((Tournament) tournamentComboBox.getSelectedItem());
		}
		fillSelectedTeams();
	}

	private void fillSelectedTeams() {
		redTeamModel.removeAllElements();
		whiteTeamModel.removeAllElements();

		if (getTournamentManager() != null) {
			for (Team team : getTournamentManager().getRedTeams()) {
				redTeamModel.addElement(team.getName());
			}

			for (Team team : getTournamentManager().getWhiteTeams()) {
				whiteTeamModel.addElement(team.getName());
			}
		}
	}

	private KingOfTheMountainTournament getTournamentManager() {
		if (tournamentManager == null) {
			// Gets existing tournamentManager or creates a new one.
			tournamentManager = (KingOfTheMountainTournament) TournamentManagerFactory.getManager(tournamentComboBox.getSelectedTournament(), getDefinedType());
		}
		return tournamentManager;
	}

	private void setTournamentType() {
		((Tournament) tournamentComboBox.getSelectedItem()).setType(getDefinedType());
	}

	private TournamentType getDefinedType() {
		return TournamentType.KING_OF_THE_MOUNTAIN;
	}

	private void createNewFights() {
		setTournamentType();
		for (int i = 0; i < redTeamModel.getSize(); i++) {
			tournamentManager.getRedTeams().add(getTeamByName(redTeamModel.getElementAt(i)));
		}

		for (int i = 0; i < whiteTeamModel.getSize(); i++) {
			tournamentManager.getWhiteTeams().add(getTeamByName(whiteTeamModel.getElementAt(i)));
		}
		tournamentManager.initializeLevelZero();
		try {
			FightPool.getInstance().remove(tournament);
			// Delete old group fights if any.
			getTournamentManager().resetFights();
			List<Fight> newFights = tournamentManager.createSortedFights(false, 0);
			FightPool.getInstance().add(tournament, newFights);
			TournamentPool.getInstance().update(tournament);
			AlertManager.informationMessage(this.getClass().getName(), "fightStored", "New Fight");
			AutoSaveByAction.getInstance().save();
		} catch (PersonalizedFightsException e) {
			KendoLog.errorMessage(this.getClass().getName(), e);
		} catch (SQLException e) {
			KendoLog.errorMessage(this.getClass().getName(), e);
		}
	}

	private Team getTeamByName(String name) {
		try {
			for (Team team : teamComboBox.getTeams()) {
				if (team.getName().equals(name)) {
					return team;
				}
			}
		} catch (NullPointerException npe) {
		}
		return null;
	}

	@Override
	public void update() {

	}

	@Override
	public void elementChanged() {

	}

}
