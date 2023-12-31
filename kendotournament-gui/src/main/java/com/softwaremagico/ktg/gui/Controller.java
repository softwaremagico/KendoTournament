package com.softwaremagico.ktg.gui;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.Configuration;
import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Role;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.gui.base.RolesMenu;
import com.softwaremagico.ktg.gui.fight.FightPanel;
import com.softwaremagico.ktg.gui.fight.NewPersonalizedFight;
import com.softwaremagico.ktg.gui.league.NewLoopLeague;
import com.softwaremagico.ktg.gui.league.NewSimpleLeague;
import com.softwaremagico.ktg.gui.tournament.TournamentDesigner;
import com.softwaremagico.ktg.gui.tournament.TournamentEvolution;
import com.softwaremagico.ktg.gui.tournament.king.NewKingOfTheMountainTournament;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.lists.AccreditionCards;
import com.softwaremagico.ktg.lists.BlogList;
import com.softwaremagico.ktg.lists.ClubList;
import com.softwaremagico.ktg.lists.DiplomaEditor;
import com.softwaremagico.ktg.lists.EmptyFightsList;
import com.softwaremagico.ktg.lists.FightList;
import com.softwaremagico.ktg.lists.ListFromTournamentTree;
import com.softwaremagico.ktg.lists.PointList;
import com.softwaremagico.ktg.lists.RefereeList;
import com.softwaremagico.ktg.lists.SelectTournamentForCompetitorPointList;
import com.softwaremagico.ktg.lists.SummaryList;
import com.softwaremagico.ktg.lists.TeamList;
import com.softwaremagico.ktg.log.KendoLog;
import com.softwaremagico.ktg.persistence.ClubPool;
import com.softwaremagico.ktg.persistence.CustomLinkPool;
import com.softwaremagico.ktg.persistence.DatabaseConnection;
import com.softwaremagico.ktg.persistence.DatabaseEngine;
import com.softwaremagico.ktg.persistence.DuelPool;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.persistence.PhotoPool;
import com.softwaremagico.ktg.persistence.RegisteredPersonPool;
import com.softwaremagico.ktg.persistence.RolePool;
import com.softwaremagico.ktg.persistence.TeamPool;
import com.softwaremagico.ktg.persistence.TournamentPool;
import com.softwaremagico.ktg.persistence.UndrawPool;
import com.softwaremagico.ktg.statistics.SelectCompetitorForPerformedHits;
import com.softwaremagico.ktg.statistics.SelectCompetitorForReceivedHits;
import com.softwaremagico.ktg.statistics.SelectCompetitorForWonFights;
import com.softwaremagico.ktg.statistics.StatisticsGeneralHits;
import com.softwaremagico.ktg.statistics.StatisticsHitsPerformed;
import com.softwaremagico.ktg.statistics.StatisticsHitsReceived;
import com.softwaremagico.ktg.statistics.StatisticsTeamTopTen;
import com.softwaremagico.ktg.statistics.StatisticsTopTen;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;
import com.softwaremagico.ktg.tournament.TournamentType;

public class Controller {
	private MainGUI main;
	private AboutBox aboutGui;
	private HelpWindow helpWindow;
	private NewCompetitor newCompetitor;
	private NewTournament newTournament;
	private NewClub newClub;
	private NewTeam newTeam;
	private DatabaseConnectionWindow databaseConnection;
	private SearchCompetitor searchCompetitor = null;
	private SearchClub searchClub = null;
	private SearchTeam searchTeam = null;
	private SearchTournament searchTournament = null;
	private FightPanel fightPanel = null;
	private NewRole participantRole = null;
	private TeamList teamsList = null;
	private RefereeList refereeList = null;
	private FightList fightsList = null;
	private PointList pointList = null;
	private SummaryList summaryList = null;
	private EmptyFightsList fightsCard = null;
	private ClubList clubList = null;
	private DiplomaEditor diplomaGui = null;
	private AccreditionCards accreditionCards = null;
	private StatisticsGeneralHits statisticsHits = null;
	private StatisticsTopTen statisticsTopTen = null;
	private StatisticsTeamTopTen statisticsTeamTopTen = null;
	private StatisticsHitsPerformed statisticsPerformedHits = null;
	private StatisticsHitsReceived statisticsReceivedHits = null;
	private SelectCompetitorForPerformedHits selectPerformedHitsOfCompetitor = null;
	private SelectCompetitorForReceivedHits selectReceivedHitsOfCompetitor = null;
	private SelectCompetitorForWonFights selectWonFightsOfCompetitor = null;
	private ChooseScore chooseScore = null;
	private ShortNewFight shortFight = null;
	private TournamentDesigner designer = null;
	private NewSimpleLeague newSimpleTournament;
	private NewKingOfTheMountainTournament newKingTournament;
	private NewLoopLeague newRing;
	private TournamentEvolution leagueEvolution = null;
	private SelectTournament selectTournament = null;
	private ChangeOrderTeam changeTeam = null;
	private DatabaseConversor databaseConversor = null;
	private ListFromTournamentTree listFromTournamentTree = null;
	private SelectTournamentForCompetitorPointList competitorsScoreList = null;
	private BlogList blogList = null;

	public Controller(MainGUI tmp_gui) {
		main = tmp_gui;
		addMainMenuListeners();
		main.setVisible(true);
		performConnection();
	}

	private void performConnection() {
		// SQLite connects automatically.
		if (DatabaseConnection.getInstance().getDatabaseEngine().equals(DatabaseEngine.SQLite)) {
			try {
				if (DatabaseConnection.getInstance().testDatabaseConnection(DatabaseConnection.getInstance().getUser(),
						DatabaseConnection.getInstance().getPassword(), DatabaseConnection.getInstance().getDatabaseName(),
						DatabaseConnection.getInstance().getServer(), true)) {
					main.enableMenuItems();
				}
			} catch (SQLException ex) {
			}
		} else { // Show connection window.
			if (!DatabaseConnection.getInstance().isDatabaseConnectionTested()) {
				openConnectionDatabase();
			}
		}
	}

	private void openConnectionDatabase() {
		try {
			databaseConnection.dispose();
		} catch (NullPointerException npe) {
		}
		databaseConnection = new DatabaseConnectionWindow();
		databaseConnection.setVisible(true);
		addNewConnectionListeners();
	}

	/**
	 * *******************************************************************
	 *
	 * MAIN MENU
	 *
	 ********************************************************************
	 */
	/**
	 * Add all listeners to main GUI.
	 */
	private void addMainMenuListeners() {
		main.addListenerToLanguages(new AddLanguagesListener());
		main.addAboutMenuItemListener(new AboutBoxListener());
		main.addCompetitorMenuItemListener(new NewCompetitorListener());
		main.addRoleMenuItemListener(new NewRoleListener());
		main.addTournamentMenuItemListener(new NewTournamentListener());
		main.addConnectDatabaseMenuItemListener(new NewConnectDatabaseListener());
		main.addClubMenuItemListener(new NewClubListener());
		main.addTeamMenuItemListener(new NewTeamListener());
		main.addTournamentPanelMenuItemListener(new NewTournamentPanelListener());
		main.addTeamListMenuItemListener(new NewTeamListListener());
		main.addFightListMenuItemListener(new NewFightListListener());
		main.addFightsCardMenuItemListener(new NewFightsCardListener());
		main.addRefereeListMenuItemListener(new NewRefereeListListener());
		main.addPointListMenuItemListener(new NewPointListListener());
		main.addSummaryMenuItemListener(new NewSummaryListener());
		main.addClubListMenuItemListener(new NewClubListListener());
		main.addDiplomaListMenuItemListener(new NewDiplomaListener());
		main.addHitsStatisticsMenuItemListener(new NewHitsStatisticsListener());
		main.addPerformedHitsStatisticsMenuItemListener(new NewPerformedHitsStatisticsListener());
		main.addReceivedHitsStatisticsMenuItemListener(new NewReceivedHitsStatisticsListener());
		main.addWonFightsStatisticsMenuItemListener(new NewWonFightsStatisticsListener());
		main.addTopTenStatisticsMenuItemListener(new NewTopTenStatisticsListener());
		main.addFightMenuItemListener(new NewFightListener());
		main.addRingMenuItemListener(new NewRingListener());
		main.addKingMenuItemListener(new NewKingListener());
		main.addDesignerMenuItemListener(new DesignerListener());
		main.addTeamTopTenListener(new NewTeamTopTenStatisticsListener());
		main.addAccreditionCardMenuItemListener(new AccreditionCardsListener());
		main.addHelpMenuItemListener(new HelpWindowListener());
		main.addScoreMenuItemListener(new ChooseScoreListener());
		main.addConvertDatabaseMenuItemListener(new DatabaseConversorListener());
		main.addSaveMenuItemListener(new SaveListener());
		main.addTreeOptionMenuItemListener(new TournamentTreeListener());
		main.addCompetitorsGlobalScoreMenuItemListener(new NewCompetitorsScoreListListener());
		main.addManualMenuItemListener(new NewPersonalizedFightListener());
		main.addLoadMenuItemListener(new LoadListener());
		main.addClearCacheMenuItemListener(new ClearCacheListener());
		main.addBlockMenuItemListener(new BlogExportListener());
	}

	class BlogExportListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				blogList.dispose();
			} catch (NullPointerException npe) {
			}
			blogList = new BlogList();
			blogList.setVisible(true);
		}
	}

	class ClearCacheListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!DatabaseConnection.getInstance().needsToBeStoredInDatabase()) {
				clearCache();
			} else {
				// Data not stored. Ask the user.
				int confirmed = JOptionPane.showConfirmDialog(null, LanguagePool.getTranslator("messages.xml").getTranslatedText("saveBeforeExit"), "Exit",
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (confirmed == JOptionPane.YES_OPTION) {
					if (DatabaseConnection.getInstance().isDatabaseConnectionTested()) {
						try {
							DatabaseConnection.getInstance().updateDatabase();
						} catch (SQLException ex) {
							AlertManager.showSqlErrorMessage(ex);
						}
					}
					clearCache();
				} else if (confirmed == JOptionPane.NO_OPTION) {
					clearCache();
				} else {
					// Do nothing.
				}
			}
		}
	}

	private void clearCache() {
		ClubPool.getInstance().clearCache();
		CustomLinkPool.getInstance().clearCache();
		DuelPool.getInstance().clearCache();
		FightPool.getInstance().clearCache();
		PhotoPool.getInstance().clearCache();
		RegisteredPersonPool.getInstance().clearCache();
		RolePool.getInstance().clearCache();
		TeamPool.getInstance().clearCache();
		TournamentPool.getInstance().clearCache();
		UndrawPool.getInstance().clearCache();
		TournamentManagerFactory.clearCache();
	}

	class TournamentTreeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				listFromTournamentTree.dispose();
			} catch (NullPointerException npe) {
			}
			listFromTournamentTree = new ListFromTournamentTree();
			listFromTournamentTree.setVisible(true);
		}
	}

	class SaveListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (AlertManager.questionMessage("questionUpdateDatabase", "SQL")) {
				try {
					DatabaseConnection.getInstance().updateDatabase();
					AlertManager.informationMessage(this.getClass().getName(), "updatedDatabase", "SQL");
				} catch (SQLException ex) {
					AlertManager.showSqlErrorMessage(ex);
				}
			}
		}
	}

	class LoadListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (DatabaseConnection.getInstance().needsToBeStoredInDatabase()) {
				int confirmed = JOptionPane.showConfirmDialog(null, LanguagePool.getTranslator("messages.xml").getTranslatedText("saveBeforeExit"), "Load",
						JOptionPane.YES_NO_CANCEL_OPTION);
				if (confirmed == JOptionPane.CANCEL_OPTION) {
					return;
				}
				if (confirmed == JOptionPane.YES_OPTION) {
					try {
						DatabaseConnection.getInstance().updateDatabase();
					} catch (SQLException ex) {
						AlertManager.showSqlErrorMessage(ex);
					}
				}
			}
			if (DatabaseConnection.getInstance().isDatabaseConnectionTested()) {
				DatabaseConnection.getInstance().resetDatabase();
				AlertManager.informationMessage(this.getClass().getName(), "RefresehdData", "Load");
			}
		}
	}

	class AddLanguagesListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			KendoTournamentGenerator.getInstance().setLanguage(main.getSelectedLanguage());
			RolePool.resetRoleTags();
			Locale.setDefault(new Locale(KendoTournamentGenerator.getInstance().getLanguage()));
			Configuration.storeLanguageConfiguration(KendoTournamentGenerator.getInstance().getLanguage());
			main.setLanguage();
		}
	}

	class AboutBoxListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				try {
					aboutGui.dispose();
				} catch (NullPointerException npe) {
				}
				aboutGui = new AboutBox();
				try {
					aboutGui.UpdateText(MyFile.inString(Path.getRootPath() + "Readme.txt"));
				} catch (FileNotFoundException fnf) {
					try {
						aboutGui.UpdateText(MyFile.inString(Path.getRootPath() + ".." + File.separator + "Readme.txt"));
					} catch (FileNotFoundException fnf2) {
						aboutGui.UpdateText("Not available until the project is installed!");
					}
				}
				aboutGui.setVisible(true);
			} catch (IOException ex) {
				Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	class HelpWindowListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				helpWindow.dispose();
			} catch (NullPointerException npe) {
			}
			helpWindow = new HelpWindow();
			String filename = Path.getManualPath() + "Guia_" + KendoTournamentGenerator.getInstance().getLanguage().toUpperCase() + ".txt";
			String text = "";
			try {
				text = MyFile.inString(filename);
			} catch (IOException ex) {
			}
			if (text.length() > 0) {
				helpWindow.UpdateText(text);
			} else {
				try {
					helpWindow.UpdateText(MyFile.inString(Path.getManualPath() + "Guia_EN.txt"));
				} catch (IOException ex) {
					Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			helpWindow.setVisible(true);
		}
	}

	class NewCompetitorListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				newCompetitor.dispose();
			} catch (NullPointerException npe) {
			}
			newCompetitor = new NewCompetitor();
			newCompetitor.setVisible(true);
			addNewCompetitorListeners();
		}
	}

	class NewRoleListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				participantRole.dispose();
			} catch (NullPointerException npe) {
			}
			participantRole = new NewRole(false);
			participantRole.setVisible(true);
		}
	}

	class NewTeamListListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				teamsList.dispose();
			} catch (NullPointerException npe) {
			}
			teamsList = new TeamList();
			teamsList.setVisible(true);
		}
	}

	class NewRefereeListListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				refereeList.dispose();
			} catch (NullPointerException npe) {
			}
			refereeList = new RefereeList();
			refereeList.setVisible(true);
		}
	}

	class NewDiplomaListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				diplomaGui.dispose();
			} catch (NullPointerException npe) {
			}
			diplomaGui = new DiplomaEditor();
			diplomaGui.setVisible(true);
		}
	}

	class NewSummaryListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				summaryList.dispose();
			} catch (NullPointerException npe) {
			}
			summaryList = new SummaryList();
			summaryList.setVisible(true);
		}
	}

	class NewClubListListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				clubList.dispose();
			} catch (NullPointerException npe) {
			}
			clubList = new ClubList();
			clubList.setVisible(true);
		}
	}

	class AccreditionCardsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				accreditionCards.dispose();
			} catch (NullPointerException npe) {
			}
			accreditionCards = new AccreditionCards();
			accreditionCards.setVisible(true);
		}
	}

	class NewPointListListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				pointList.dispose();
			} catch (NullPointerException npe) {
			}
			pointList = new PointList();
			pointList.setVisible(true);
		}
	}

	class NewTournamentListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				newTournament.dispose();
			} catch (NullPointerException npe) {
			}
			newTournament = new NewTournament();
			newTournament.setVisible(true);
			addNewTournamentListeners();
		}
	}

	class NewClubListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				newClub.dispose();
			} catch (NullPointerException npe) {
			}
			newClub = new NewClub();
			newClub.setVisible(true);
			addNewClubListeners();
		}
	}

	class NewTeamListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				newTeam.dispose();
			} catch (NullPointerException npe) {
			}
			newTeam = new NewTeam();
			newTeam.start();
			newTeam.fill();
			newTeam.setVisible(true);
			addNewTeamListeners();

		}
	}

	class NewFightListListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				fightsList.dispose();
			} catch (NullPointerException npe) {
			}
			fightsList = new FightList();
			fightsList.setVisible(true);
		}
	}

	class NewFightsCardListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				fightsCard.dispose();
			} catch (NullPointerException npe) {
			}
			fightsCard = new EmptyFightsList();
			fightsCard.setVisible(true);
		}
	}

	class NewCompetitorsScoreListListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				competitorsScoreList.dispose();
			} catch (NullPointerException npe) {
			}
			competitorsScoreList = new SelectTournamentForCompetitorPointList();
			competitorsScoreList.setVisible(true);
		}
	}

	class NewPersonalizedFightListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			SelectTournamentPersonalized selectTournament = new SelectTournamentPersonalized("AcceptButton");
			selectTournament.setVisible(true);
		}
	}

	class NewTournamentPanelListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				fightPanel.dispose();
			} catch (NullPointerException npe) {
			}
			fightPanel = new FightPanel();
			fightPanel.setVisible(true);
			addFightPanelListeners();
		}
	}

	class NewConnectDatabaseListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			openConnectionDatabase();
		}
	}

	class NewHitsStatisticsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				selectTournament.dispose();
			} catch (NullPointerException npe) {
			}
			selectTournament = new SelectTournament("titleHitStatistics");
			selectTournament.setVisible(true);
			addSelectTournamentListeners();
		}
	}

	class NewPerformedHitsStatisticsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				selectPerformedHitsOfCompetitor.dispose();
			} catch (NullPointerException npe) {
			}
			selectPerformedHitsOfCompetitor = new SelectCompetitorForPerformedHits();
			selectPerformedHitsOfCompetitor.setVisible(true);
			addSelectPerformedHitsCompetitorListeners();
		}
	}

	class NewReceivedHitsStatisticsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				selectReceivedHitsOfCompetitor.dispose();
			} catch (NullPointerException npe) {
			}
			selectReceivedHitsOfCompetitor = new SelectCompetitorForReceivedHits();
			selectReceivedHitsOfCompetitor.setVisible(true);
			addSelectReceivedHitsCompetitorListeners();
		}
	}

	class NewWonFightsStatisticsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				selectWonFightsOfCompetitor.dispose();
			} catch (NullPointerException npe) {
			}
			selectWonFightsOfCompetitor = new SelectCompetitorForWonFights();
			selectWonFightsOfCompetitor.setVisible(true);
			addSelectWonFightsCompetitorListeners();
		}
	}

	class NewTopTenStatisticsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				selectTournament.dispose();
			} catch (NullPointerException npe) {
			}
			selectTournament = new SelectTournament("titleHitStatistics");
			selectTournament.setVisible(true);
			addSelectTournamentTopTenListeners();
		}
	}

	class NewTeamTopTenStatisticsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				selectTournament.dispose();
			} catch (NullPointerException npe) {
			}
			selectTournament = new SelectTournament("titleTeamTopTen");
			selectTournament.setVisible(true);
			addSelectTournamentForTeamTopTenListeners();
		}
	}

	class NewFightListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				newSimpleTournament.dispose();
			} catch (NullPointerException npe) {
			}
			newSimpleTournament = new NewSimpleLeague();
			newSimpleTournament.setVisible(true);
			// AddNewTeamListeners();

		}
	}

	class NewKingListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				newKingTournament.dispose();
			} catch (NullPointerException npe) {
			}
			newKingTournament = new NewKingOfTheMountainTournament();
			newKingTournament.setVisible(true);

		}
	}

	class NewRingListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				newRing.dispose();
			} catch (NullPointerException npe) {
			}
			newRing = new NewLoopLeague();
			newRing.setVisible(true);

		}
	}

	class DesignerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				designer.dispose();
			} catch (NullPointerException npe) {
			}
			designer = new TournamentDesigner();
			designer.setVisible(true);
		}
	}

	class ChooseScoreListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				chooseScore.dispose();
			} catch (NullPointerException npe) {
			}
			// Tournament championship =
			// DatabaseConnection.getInstance().getDatabase().getTournamentByName(KendoTournamentGenerator.getInstance().getLastSelectedTournament(),
			// false);
			chooseScore = new ChooseScore();
			chooseScore.setVisible(true);
		}
	}

	class DatabaseConversorListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				databaseConversor.dispose();
			} catch (NullPointerException npe) {
			}
			databaseConversor = new DatabaseConversor();
			databaseConversor.setVisible(true);
		}
	}

	/**
	 * *******************************************************************
	 *
	 * DATABASE CONNECTION
	 *
	 ********************************************************************
	 */
	/**
	 * Add all listeners to main GUI.
	 */
	private void addNewConnectionListeners() {
		databaseConnection.addConnectButtonListener(new ConnectDatabaseListener());
		databaseConnection.addPasswordFieldKeyReleased(new PasswordTyped());
	}

	class ConnectDatabaseListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			connect();
		}
	}

	private void connect() {
		databaseConnection.performConnection();
		main.enableMenuItems();
	}

	class PasswordTyped implements KeyListener {

		boolean key = false;

		@Override
		public void keyReleased(java.awt.event.KeyEvent evt) {
			// Allow to connect presing ENTER.
			int ke = evt.getKeyCode();
			if (ke == 10 && key) {
				connect();
				key = false;
			}
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
		}

		@Override
		public void keyPressed(KeyEvent arg0) {
			key = true;
		}
	}

	/**
	 * *******************************************************************
	 *
	 * NEW COMPETITOR
	 *
	 ********************************************************************
	 */
	/**
	 * Add all listeners to main GUI.
	 */
	private void addNewCompetitorListeners() {
		newCompetitor.addSearchListener(new SearchListener());
		newCompetitor.addAcceptListener(new AcceptNewCompetitorListener());
	}

	class SearchListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				searchCompetitor.dispose();
			} catch (NullPointerException npe) {
			}
			searchCompetitor = new SearchCompetitor();
			searchCompetitor.setVisible(true);
			newCompetitor.setVisible(false);
			addSearchCompetitorListeners();
		}
	}

	class AcceptNewCompetitorListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			RegisteredPerson competitor;
			newCompetitor.correctNif();
			if ((competitor = newCompetitor.acceptCompetitor()) != null) {
				main.enableMenuItems();
				try {
					participantRole.dispose();
				} catch (NullPointerException npe) {
				}
				try {
					if (TournamentPool.getInstance().getSorted().size() > 0) {
						participantRole = new NewRole(true, competitor);
						participantRole.setVisible(true);
						// participantFunction.defaultSelect(c);
					}
				} catch (SQLException ex) {
					AlertManager.showSqlErrorMessage(ex);
				}
			}
		}
	}

	/**
	 * *******************************************************************
	 *
	 * SEARCH COMPETITOR
	 *
	 ********************************************************************
	 */
	/**
	 * Add all listeners to GUI.
	 */
	private void addSearchCompetitorListeners() {
		searchCompetitor.addSelectButtonListener(new SearchSelectButtonListener());
	}

	class SearchSelectButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			RegisteredPerson competitor = searchCompetitor.returnSelectedItem();
			if (competitor != null) {
				newCompetitor.updateWindow(competitor);
				newCompetitor.setVisible(true);
				searchCompetitor.dispose();
			}
		}
	}

	/**
	 * *******************************************************************
	 *
	 * NEW CLUB
	 *
	 ********************************************************************
	 */
	/**
	 * Add all listeners to GUI.
	 */
	private void addNewClubListeners() {
		newClub.addSearchListener(new SearchClubListener());
		newClub.addAcceptListener(new AcceptClubListener());
	}

	class AcceptClubListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			newClub.acceptClub();
			newClub.updateClubsInCompetitor(newCompetitor);
			main.enableMenuItems();
		}
	}

	class SearchClubListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				searchClub.dispose();
			} catch (NullPointerException npe) {
			}
			searchClub = new SearchClub();
			searchClub.setVisible(true);
			newClub.setVisible(false);
			addSearchClubListeners();
		}
	}

	/**
	 * *******************************************************************
	 *
	 * SEARCH CLUB
	 *
	 ********************************************************************
	 */
	/**
	 * Add all listeners to GUI.
	 */
	private void addSearchClubListeners() {
		searchClub.addSelectButtonListener(new SearchClubSelectButtonListener());
	}

	class SearchClubSelectButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Club club = searchClub.returnSelectedItem();
			if (club != null) {
				newClub.updateWindow(club);
				newClub.setVisible(true);
				searchClub.dispose();
			}
		}
	}

	/**
	 * *******************************************************************
	 *
	 * NEW TEAM
	 *
	 ********************************************************************
	 */
	/**
	 * Add all listeners to GUI.
	 */
	private void addNewTeamListeners() {
		newTeam.addSearchListener(new SearchTeamListener());
	}

	class SearchTeamListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				searchTeam.dispose();
			} catch (NullPointerException npe) {
			}
			searchTeam = new SearchTeam();
			searchTeam.setVisible(true);
			newTeam.setVisible(false);
			addSearchTeamListeners();
		}
	}

	/**
	 * *******************************************************************
	 *
	 * SEARCH TEAM
	 *
	 ********************************************************************
	 */
	/**
	 * Add all listeners to GUI.
	 */
	private void addSearchTeamListeners() {
		searchTeam.addSelectButtonListener(new SearchTeamSelectButtonListener());
	}

	class SearchTeamSelectButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Team team = searchTeam.returnSelectedItem();
			if (team != null) {
				newTeam.updateWindow(team);
				newTeam.setVisible(true);
				searchTeam.dispose();
			}
		}
	}

	/**
	 * *******************************************************************
	 *
	 * NEW TOURNAMENT
	 *
	 ********************************************************************
	 */
	/**
	 * Add all listeners to GUI.
	 */
	private void addNewTournamentListeners() {
		newTournament.addSearchListener(new SearchTournamentListener());
		newTournament.addAcceptListener(new AddNewTournamentListener());
	}

	class SearchTournamentListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				searchTournament.dispose();
			} catch (NullPointerException npe) {
			}
			searchTournament = new SearchTournament();
			searchTournament.setVisible(true);
			newTournament.setVisible(false);
			addSearchTournamentListeners();
		}
	}

	class AddNewTournamentListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (newTournament.acceptTournament()) {
				newTournament.dispose();
				try {
					participantRole.dispose();
				} catch (NullPointerException npe) {
				}
				participantRole = new NewRole(false);
				participantRole.setVisible(true);
				main.enableMenuItems();
			}
		}
	}

	/**
	 * *******************************************************************
	 *
	 * SEARCH TOURNAMENT
	 *
	 ********************************************************************
	 */
	/**
	 * Add all listeners to GUI.
	 */
	private void addSearchTournamentListeners() {
		searchTournament.addSelectButtonListener(new SearchTournamentSelectButtonListener());
		searchTournament.getCloneButton().addActionListener(new CloneButtonListener());
	}

	class SearchTournamentSelectButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Tournament tournament = searchTournament.returnSelectedItem();
			if (tournament != null) {
				newTournament.setVisible(true);
				newTournament.updateWindow(tournament);
				searchTournament.dispose();
			}
		}
	}

	class CloneButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			final Tournament tournament = searchTournament.returnSelectedItem();
			if (tournament != null) {
				searchTournament.dispose();
				// Open name window.
				final CloneTournamentNameWindow cloneTournamentNameWindow = new CloneTournamentNameWindow();
				cloneTournamentNameWindow.setTournamentName(tournament.getName());
				cloneTournamentNameWindow.setVisible(true);
				cloneTournamentNameWindow.addAcceptButtonListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							// Check not repeated name
							if (TournamentPool.getInstance().getByName(cloneTournamentNameWindow.getTournamentName()) != null) {
								AlertManager.errorMessage(Controller.class.getName(), "nameTournament", "");
							} else {
								final Tournament clonedTournament = tournament.clone(tournament, cloneTournamentNameWindow.getTournamentName());
								TournamentPool.getInstance().add(clonedTournament);
								// Finishing cloning.

								for (Team team : TeamPool.getInstance().get(tournament)) {
									Team clonedTeam = team.clone(clonedTournament);
									TeamPool.getInstance().add(clonedTournament, clonedTeam);
								}
								for (Role role : RolePool.getInstance().get(tournament)) {
									Role clonedRole = role.clone(clonedTournament);
									RolePool.getInstance().add(clonedTournament, clonedRole);
								}

								newTournament.setVisible(true);
								newTournament.updateWindow(clonedTournament);
								cloneTournamentNameWindow.dispose();
							}
						} catch (SQLException ex) {
							AlertManager.showSqlErrorMessage(ex);
						}
					}
				});
			}
		}
	}

	/**
	 * *******************************************************************
	 *
	 * FIGHT PANEL
	 *
	 ********************************************************************
	 */
	/**
	 * Add all listeners to GUI.
	 */
	private void addFightPanelListeners() {
		// tournamentPanel.addTreeListener(new TreeButtonListener());
		// tournamentPanel.addFightListener(new AddFightToPanelListener());
		fightPanel.addChangeTeamMenuItemListener(new NewChangeTeamOrderListener());
	}

	class TreeButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				leagueEvolution.dispose();
			} catch (NullPointerException npe) {
			}
			leagueEvolution = new TournamentEvolution(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
			leagueEvolution.setVisible(true);
			leagueEvolution.setExtendedState(leagueEvolution.getExtendedState() | JFrame.MAXIMIZED_BOTH);
			leagueEvolution.updateBlackBoard(fightPanel.getSelectedTournament(), false);
		}
	}

	class AddFightToPanelListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				shortFight.dispose();
			} catch (NullPointerException npe) {
			}
			shortFight = new ShortNewFight(fightPanel.getSelectedTournament(), fightPanel.getSelectedFightArea());
			addShortNewFightListeners();
			if (shortFight.filled()) {
				shortFight.setVisible(true);
			}
		}
	}

	class NewChangeTeamOrderListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				changeTeam.dispose();
			} catch (NullPointerException npe) {
			}
			changeTeam = new ChangeOrderTeam(fightPanel.getSelectedFightArea());
			addChangeOrderListeners();
			changeTeam.setVisible(true);
		}
	}

	/**
	 * *******************************************************************
	 *
	 * CHANGE ORDER OF TEAM
	 *
	 ********************************************************************
	 */
	/**
	 * Add listeners
	 */
	private void addChangeOrderListeners() {
		changeTeam.addCloseListener(new ChangeTeamOrderListener());
	}

	class ChangeTeamOrderListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			changeTeam.dispose();
			try {
				// Update GUI.
				fightPanel.update();
			} catch (NullPointerException npe) {
			}
		}
	}

	/**
	 * *******************************************************************
	 *
	 * SELECT TOURNAMENT
	 *
	 ********************************************************************
	 */
	/**
	 * Add all listeners to GUI.
	 */
	private void addSelectTournamentListeners() {
		selectTournament.addGenerateButtonListener(new SelectTournamentListener());
	}

	class SelectTournamentListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				statisticsHits.dispose();
			} catch (NullPointerException npe) {
			}
			statisticsHits = new StatisticsGeneralHits(selectTournament.getSelectedTournament());
			statisticsHits.setVisible(true);
			selectTournament.dispose();
		}
	}

	/**
	 * *******************************************************************
	 *
	 * SELECT TOURNAMENT FOR TOP TEN
	 *
	 ********************************************************************
	 */
	/**
	 * Add all listeners to GUI.
	 */
	private void addSelectTournamentTopTenListeners() {
		selectTournament.addGenerateButtonListener(new SelectTournamentTopTenListener());
	}

	class SelectTournamentTopTenListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				statisticsTopTen.dispose();
			} catch (NullPointerException npe) {
			}
			statisticsTopTen = new StatisticsTopTen(selectTournament.getSelectedTournament());
			statisticsTopTen.setVisible(true);
			selectTournament.dispose();
		}
	}

	/**
	 * *******************************************************************
	 *
	 * SELECT TEAM FOR TOP TEN
	 *
	 ********************************************************************
	 */
	/**
	 * Add all listeners to GUI.
	 */
	private void addSelectTournamentForTeamTopTenListeners() {
		selectTournament.addGenerateButtonListener(new selectTournamentForTeamTopTenListener());
	}

	class selectTournamentForTeamTopTenListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				statisticsTeamTopTen.dispose();
			} catch (NullPointerException npe) {
			}
			statisticsTeamTopTen = new StatisticsTeamTopTen(selectTournament.getSelectedTournament());
			statisticsTeamTopTen.setVisible(true);
			selectTournament.dispose();
		}
	}

	/**
	 * *******************************************************************
	 *
	 * SELECT COMPETITOR
	 *
	 ********************************************************************
	 */
	/**
	 * Add all listeners to GUI.
	 */
	private void addSelectPerformedHitsCompetitorListeners() {
		selectPerformedHitsOfCompetitor.addGenerateButtonListener(new SelectPerformedHitsCompetitorListener());
	}

	class SelectPerformedHitsCompetitorListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				statisticsPerformedHits.dispose();
			} catch (NullPointerException npe) {
			}
			statisticsPerformedHits = new StatisticsHitsPerformed(selectPerformedHitsOfCompetitor.returnSelectedCompetitor());
			statisticsPerformedHits.setVisible(true);
			selectPerformedHitsOfCompetitor.dispose();
		}
	}

	/**
	 * Add all listeners to GUI.
	 */
	private void addSelectReceivedHitsCompetitorListeners() {
		selectReceivedHitsOfCompetitor.addGenerateButtonListener(new SelectReceivedHitsCompetitorListener());
	}

	class SelectReceivedHitsCompetitorListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				statisticsReceivedHits.dispose();
			} catch (NullPointerException npe) {
			}
			statisticsReceivedHits = new StatisticsHitsReceived(selectReceivedHitsOfCompetitor.returnSelectedCompetitor());
			statisticsReceivedHits.setVisible(true);
			selectReceivedHitsOfCompetitor.dispose();
		}
	}

	/**
	 * Add all listeners to GUI.
	 */
	private void addSelectWonFightsCompetitorListeners() {
		selectWonFightsOfCompetitor.addGenerateButtonListener(new SelectWonFightsCompetitorListener());
	}

	class SelectWonFightsCompetitorListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				statisticsTopTen.dispose();
			} catch (NullPointerException npe) {
			}
			statisticsTopTen = new StatisticsTopTen(null);
			statisticsTopTen.setVisible(true);
			statisticsTopTen.updateComboBox(selectWonFightsOfCompetitor.returnSelectedCompetitor());
			selectWonFightsOfCompetitor.dispose();
		}
	}

	/**
	 * *******************************************************************
	 *
	 * SHORT NEW FIGHT
	 *
	 ********************************************************************
	 */
	/**
	 * Add all listeners to GUI.
	 */
	private void addShortNewFightListeners() {
		shortFight.addFightListener(new AddShortFightListener());
	}

	class AddShortFightListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Fight f;
			try {
				f = new Fight(shortFight.getTournament(), shortFight.getTeam1(), shortFight.getTeam2(), shortFight.getArena(), FightPool.getInstance()
						.getLastLevelUsed(shortFight.getTournament()), 0, FightPool.getInstance()
						.get(KendoTournamentGenerator.getInstance().getLastSelectedTournament()).size());
				try {
					FightPool.getInstance().add(KendoTournamentGenerator.getInstance().getLastSelectedTournament(), f);
					AlertManager.translatedMessage(this.getClass().getName(), "addFight", "SQL", KendoTournamentGenerator.getInstance().getLanguage(),
							JOptionPane.INFORMATION_MESSAGE);
					fightPanel.updateScorePanel();
				} catch (NullPointerException npe) {
					AlertManager.showErrorInformation(this.getClass().getName(), npe);
				}
			} catch (SQLException ex) {
				AlertManager.showSqlErrorMessage(ex);
			}
		}
	}

	class SelectTournamentPersonalized extends ListFromTournamentCreateFile {

		private static final long serialVersionUID = -2551958099358143398L;

		public SelectTournamentPersonalized(String buttonTag) {
			createGui(false);
			trans = LanguagePool.getTranslator("gui.xml");
			// Not modified last selected tournament.
			this.setTranslatedTitle("ManualFightsMenuItem");
			GenerateButton.setText(trans.getTranslatedText(buttonTag));
			addGenerateButtonListener(new PersonalizedTournamentListener());
		}

		@Override
		public String defaultFileName() {
			return "";
		}

		class PersonalizedTournamentListener implements ActionListener {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {

					if (AlertManager.questionMessage("convertToPersonalized", getTitle())) {
						// Remove old information.
						getSelectedTournament().setType(TournamentType.PERSONALIZED);
						TournamentPool.getInstance().update(getSelectedTournament());
						FightPool.getInstance().remove(getSelectedTournament());

						// Open fight panel.
						try {
							fightPanel.dispose();
						} catch (NullPointerException npe) {
						}
						fightPanel = new FightPanel();
						fightPanel.setVisible(true);
						addFightPanelListeners();

						NewPersonalizedFight personalizedFight = new NewPersonalizedFight(getSelectedTournament(), fightPanel);
						personalizedFight.setVisible(true);

						dispose();
					} else {
						AlertManager.warningMessage(Controller.class.getName(), "canceledAction", getTitle());
					}
				} catch (Exception ex) {
					AlertManager.errorMessage(RolesMenu.class.getName(), "importFail", "");
					KendoLog.errorMessage(RolesMenu.class.getName(), ex);
				}
			}
		}
	}
}
