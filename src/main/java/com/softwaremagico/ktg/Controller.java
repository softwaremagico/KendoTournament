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
 *  Created on 09-dic-2008.
 */
package com.softwaremagico.ktg;

import com.softwaremagico.ktg.championship.LeagueDesigner;
import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.gui.*;
import com.softwaremagico.ktg.gui.fight.*;
import com.softwaremagico.ktg.language.Configuration;
import com.softwaremagico.ktg.pdflist.*;
import com.softwaremagico.ktg.statistics.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author jorge
 */
public class Controller {

    MainGUI main;
    private AboutBox aboutGui;
    private HelpWindow helpWindow;
    private NewCompetitor newCompetitor;
    private NewTournament newTournament;
    private NewClub newClub;
    private NewTeam newTeam;
    private DatabaseConnection databaseConnection;
    private SearchCompetitor searchCompetitor = null;
    private SearchClub searchClub = null;
    private SearchTeam searchTeam = null;
    private SearchTournament searchTournament = null;
    private FightPanel tournamentPanel = null;
    private NewRole participantFunction = null;
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
    private SelectTournamentForHitsStatistics selectTournament = null;
    private SelectTournamentForTopTen selectTournamentTopTen = null;
    private SelectTournamentForTeamTopTen selectTournamentForTeamTopTen = null;
    private SelectCompetitorForPerformedHits selectPerformedHitsOfCompetitor = null;
    private SelectCompetitorForReceivedHits selectReceivedHitsOfCompetitor = null;
    private SelectCompetitorForWonFights selectWonFightsOfCompetitor = null;
    private ChooseScoreGUI chooseScore = null;
    private ShortNewFight shortFight = null;
    private LeagueDesigner designer = null;
    private NewSimpleTournament newFight;
    private NewRingTournament newRing;
    private LeagueEvolution leagueEvolution = null;
    private MonitorTree monitorTree = null;
    private MonitorPosition monitorPosition = null;
    private Monitor monitor = null;
    private SelectTournamentForMonitor selectTournamentForMonitor = null;
    private SelectTournamentForTreeMonitor selectTournamentForTreeMonitor = null;
    private SelectTournamentForCSV selectTournamentForCSV = null;
    private ChangeOrderTeam changeTeam = null;
    private DatabaseConversor databaseConversor = null;

    public Controller(MainGUI tmp_gui) {
        main = tmp_gui;
        AddMainMenuListeners();
        main.setVisible(true);
        connectDatabase(!KendoTournamentGenerator.getInstance().databaseConnected);
    }

    private void connectDatabase(boolean connect) {
        if (connect) {
            try {
                databaseConnection.dispose();
            } catch (NullPointerException npe) {
            }
            databaseConnection = new DatabaseConnection();
            databaseConnection.setVisible(true);
            AddNewConnectionListeners();
        }
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
    private void AddMainMenuListeners() {
        main.addListenerToLanguages(new AddLanguagesListener());
        main.addAboutMenuItemListener(new AboutBoxListener());
        main.addCompetitorMenuItemListener(new NewCompetitorListener());
        main.addRoleMenuItemListener(new NewRoleListener());
        main.addTournamentMenuItemListener(new NewTournamentListener());
        main.addConnectDatabaseMenuItemListener(new NewConnectDatabaseListener());
        main.addUpdateDatabaseMenuItemListener(new UpdateDatabaseListener());
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
        main.addManualMenuItemListener(new NewManualFightListener());
        main.addFightMenuItemListener(new NewFightListener());
        main.addRingMenuItemListener(new NewRingListener());
        main.addDesignerMenuItemListener(new DesignerListener());
        main.addTeamTopTenListener(new NewTeamTopTenStatisticsListener());
        main.addScoreMonitorListener(new ScoreMonitorListener());
        main.addTreeMonitorListener(new TreeMonitorListener());
        main.addAccreditionCardMenuItemListener(new AccreditionCardsListener());
        main.addHelpMenuItemListener(new HelpWindowListener());
        main.addScoreMenuItemListener(new ChooseScoreListener());
        main.addCsvMenuItemListener(new CsvListener());
        main.addChangeTeamMenuItemListener(new ChangeTeamListener());
        main.addConvertDatabaseMenuItemListener(new DatabaseConversorListener());
    }

    class AddLanguagesListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            KendoTournamentGenerator.getInstance().language = main.ReturnSelectedLanguage();
            Locale.setDefault(new Locale(KendoTournamentGenerator.getInstance().language));
            Configuration langConf = new Configuration();
            langConf.storeLanguageConfiguration(KendoTournamentGenerator.getInstance().language);
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
                aboutGui.UpdateText(MyFile.InString(Path.returnRootPath() + "Readme.txt", true));
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
            String filename = Path.returnManualPath() + "Guia_" + KendoTournamentGenerator.getInstance().language.toUpperCase() + ".txt";
            String text = "";
            try {
                text = MyFile.InString(filename, true);
            } catch (IOException ex) {
            }
            if (text.length() > 0) {
                helpWindow.UpdateText(text);
            } else {
                try {
                    helpWindow.UpdateText(MyFile.InString(Path.returnManualPath() + "Guia_EN.txt", true));
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
            AddNewCompetitorListeners();
        }
    }

    class NewRoleListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                participantFunction.dispose();
            } catch (NullPointerException npe) {
            }
            participantFunction = new NewRole(false);
            participantFunction.setVisible(true);
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
            AddNewTournamentListeners();
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
            AddNewClubListeners();
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
            AddNewTeamListeners();

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

    class NewManualFightListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                tournamentPanel.dispose();
            } catch (NullPointerException npe) {
            }
            tournamentPanel = new FightPanel();
            tournamentPanel.setVisible(true);
            AddFightPanelListeners();
        }
    }

    class NewTournamentPanelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                tournamentPanel.dispose();
            } catch (NullPointerException npe) {
            }
            tournamentPanel = new FightPanel();
            tournamentPanel.setVisible(true);
            AddFightPanelListeners();
        }
    }

    class NewConnectDatabaseListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            connectDatabase(true);
        }
    }

    class UpdateDatabaseListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            KendoTournamentGenerator.getInstance().database.updateDatabase(Path.returnDatabasePath() + File.separator + "updates" + File.separator, true);
        }
    }

    class NewHitsStatisticsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                selectTournament.dispose();
            } catch (NullPointerException npe) {
            }
            selectTournament = new SelectTournamentForHitsStatistics();
            selectTournament.setVisible(true);
            AddSelectTournamentListeners();
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
            AddSelectPerformedHitsCompetitorListeners();
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
            AddSelectReceivedHitsCompetitorListeners();
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
            AddSelectWonFightsCompetitorListeners();
        }
    }

    class NewTopTenStatisticsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                selectTournamentTopTen.dispose();
            } catch (NullPointerException npe) {
            }
            selectTournamentTopTen = new SelectTournamentForTopTen();
            selectTournamentTopTen.setVisible(true);
            AddSelectTournamentTopTenListeners();
        }
    }

    class NewTeamTopTenStatisticsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                selectTournamentForTeamTopTen.dispose();
            } catch (NullPointerException npe) {
            }
            selectTournamentForTeamTopTen = new SelectTournamentForTeamTopTen();
            selectTournamentForTeamTopTen.setVisible(true);
            AddSelectTournamentForTeamTopTenListeners();
        }
    }

    class NewFightListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                newFight.dispose();
            } catch (NullPointerException npe) {
            }
            newFight = new NewSimpleTournament();
            newFight.setVisible(true);
            //AddNewTeamListeners();

        }
    }

    class NewRingListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                newRing.dispose();
            } catch (NullPointerException npe) {
            }
            newRing = new NewRingTournament();
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
            designer = new LeagueDesigner();
            designer.setVisible(true);
        }
    }

    class ScoreMonitorListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                selectTournamentForMonitor.dispose();
            } catch (NullPointerException npe) {
            }
            selectTournamentForMonitor = new SelectTournamentForMonitor();
            selectTournamentForMonitor.setVisible(true);
            AddSelectTournamentMonitorListeners();
        }
    }

    class TreeMonitorListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                selectTournamentForTreeMonitor.dispose();
            } catch (NullPointerException npe) {
            }
            selectTournamentForTreeMonitor = new SelectTournamentForTreeMonitor();
            selectTournamentForTreeMonitor.setVisible(true);
            AddSelectTournamentTreeMonitorListeners();
        }
    }

    class ChooseScoreListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                chooseScore.dispose();
            } catch (NullPointerException npe) {
            }
            //Tournament championship = KendoTournamentGenerator.getInstance().database.getTournamentByName(KendoTournamentGenerator.getInstance().getLastSelectedTournament(), false);
            chooseScore = new ChooseScoreGUI();
            chooseScore.setVisible(true);
        }
    }

    class CsvListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                selectTournamentForCSV.dispose();
            } catch (NullPointerException npe) {
            }
            selectTournamentForCSV = new SelectTournamentForCSV();
            selectTournamentForCSV.setVisible(true);
            AddSelectTournamentCSVListeners();

        }
    }

    class ChangeTeamListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                changeTeam.dispose();
            } catch (NullPointerException npe) {
            }
            changeTeam = new ChangeOrderTeam();
            changeTeam.setVisible(true);
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
    private void AddNewConnectionListeners() {
        databaseConnection.addConnectButtonListener(new ConnectDatabaseListener());
        databaseConnection.addPasswordFieldKeyReleased(new PasswordTyped());
    }

    class ConnectDatabaseListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            databaseConnection.performConnection();
            main.isConnectedToDatabase();
        }
    }

    class PasswordTyped implements KeyListener {

        boolean key = false;

        @Override
        public void keyReleased(java.awt.event.KeyEvent evt) {
            String p = "";
            for (int i = 0; i < databaseConnection.password().length; i++) {
                p += String.valueOf(databaseConnection.password()[i]);
            }

            //Allow to connect presing ENTER.
            int ke = evt.getKeyCode();
            if (ke == 10 && key) {
                databaseConnection.performConnection();
                main.isConnectedToDatabase();
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
    private void AddNewCompetitorListeners() {
        newCompetitor.addSearchListener(new SearchListener());
        newCompetitor.addAcceptListener(new AcceptCompetitorListener());
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
            AddSearchCompetitorListeners();
        }
    }

    class AcceptCompetitorListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Competitor c;
            newCompetitor.testNIF();
            if ((c = newCompetitor.acceptCompetitor()) != null) {
                try {
                    participantFunction.dispose();
                } catch (NullPointerException npe) {
                }
                if ((KendoTournamentGenerator.getInstance().database.getAllTournaments()).size() > 0) {
                    participantFunction = new NewRole(true);
                    participantFunction.setVisible(true);
                    participantFunction.defaultSelect(c);
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
    private void AddSearchCompetitorListeners() {
        searchCompetitor.addSelectButtonListener(new SearchSelectButtonListener());
    }

    class SearchSelectButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            CompetitorWithPhoto c = searchCompetitor.returnSelectedItem();
            newCompetitor.updateWindow(c);
            newCompetitor.setVisible(true);
            searchCompetitor.dispose();
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
    private void AddNewClubListeners() {
        newClub.addSearchListener(new SearchClubListener());
        newClub.addAcceptListener(new AcceptClubListener());
    }

    class AcceptClubListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //newClub.acceptClub(); is defined in the window!
            newClub.updateClubsInCompetitor(newCompetitor);
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
            AddSearchClubListeners();
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
    private void AddSearchClubListeners() {
        searchClub.addSelectButtonListener(new SearchClubSelectButtonListener());
    }

    class SearchClubSelectButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Club c = searchClub.returnSelectedItem();
            newClub.UpdateWindow(c);
            newClub.setVisible(true);
            searchClub.dispose();
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
    private void AddNewTeamListeners() {
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
            AddSearchTeamListeners();
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
    private void AddSearchTeamListeners() {
        searchTeam.addSelectButtonListener(new SearchTeamSelectButtonListener());
    }

    class SearchTeamSelectButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Team t = searchTeam.returnSelectedItem();
            newTeam.updateWindow(t);
            newTeam.setVisible(true);
            searchTeam.dispose();
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
    private void AddNewTournamentListeners() {
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
            AddSearchTournamentListeners();
        }
    }

    class AddNewTournamentListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (newTournament.storeTournament()) {
                newTournament.dispose();
                try {
                    participantFunction.dispose();
                } catch (NullPointerException npe) {
                }
                participantFunction = new NewRole(false);
                participantFunction.setVisible(true);
                try {
                    chooseScore.dispose();
                } catch (NullPointerException npe) {
                }
                chooseScore = new ChooseScoreGUI();
                chooseScore.setVisible(true);
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
    private void AddSearchTournamentListeners() {
        searchTournament.addSelectButtonListener(new SearchTournamentSelectButtonListener());
    }

    class SearchTournamentSelectButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Tournament t = searchTournament.returnSelectedItem();
            newTournament.setVisible(true);
            newTournament.UpdateWindow(t);
            searchTournament.dispose();
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
    private void AddFightPanelListeners() {
        tournamentPanel.addTreeListener(new TreeButtonListener());
        tournamentPanel.addFightListener(new AddFightToPanelListener());
    }

    class TreeButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                leagueEvolution.dispose();
            } catch (NullPointerException npe) {
            }
            leagueEvolution = new LeagueEvolution();
            leagueEvolution.setVisible(true);
            leagueEvolution.setExtendedState(leagueEvolution.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            leagueEvolution.updateBlackBoard(tournamentPanel.getSelectedTournament(), false);
        }
    }

    class AddFightToPanelListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                shortFight.dispose();
            } catch (NullPointerException npe) {
            }
            shortFight = new ShortNewFight(tournamentPanel.getSelectedTournament(), tournamentPanel.getSelectedArena());
            AddShortNewFightListeners();
            if (shortFight.filled()) {
                shortFight.setVisible(true);
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
    private void AddSelectTournamentListeners() {
        selectTournament.addGenerateButtonListener(new SelectTournamentListener());
    }

    class SelectTournamentListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                statisticsHits.dispose();
            } catch (NullPointerException npe) {
            }
            statisticsHits = new StatisticsGeneralHits(selectTournament.returnSelectedTournamentName());
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
    private void AddSelectTournamentTopTenListeners() {
        selectTournamentTopTen.addGenerateButtonListener(new SelectTournamentTopTenListener());
    }

    class SelectTournamentTopTenListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                statisticsTopTen.dispose();
            } catch (NullPointerException npe) {
            }
            statisticsTopTen = new StatisticsTopTen(selectTournamentTopTen.returnSelectedTournamentName());
            statisticsTopTen.setVisible(true);
            selectTournamentTopTen.dispose();
        }
    }

    /**
     * *******************************************************************
     *
     * SELECT TOURNAMENT FOR MONITOR
     *
     ********************************************************************
     */
    /**
     * Add all listeners to GUI.
     */
    private void AddSelectTournamentMonitorListeners() {
        selectTournamentForMonitor.addGenerateButtonListener(new SelectTournamentMonitorListener());
    }

    class SelectTournamentMonitorListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                monitor.dispose();
            } catch (NullPointerException npe) {
            }
            monitor = new Monitor(KendoTournamentGenerator.getInstance().database.getTournamentByName(selectTournamentForMonitor.returnSelectedTournamentName(), false),
                    selectTournamentForMonitor.returnSelectedArena());
            monitor.setVisible(true);
            monitor.setExtendedState(monitor.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            selectTournamentForMonitor.dispose();
        }
    }

    /**
     * *******************************************************************
     *
     * SELECT TOURNAMENT FOR TREE MONITOR
     *
     ********************************************************************
     */
    /**
     * Add all listeners to GUI.
     */
    private void AddSelectTournamentTreeMonitorListeners() {
        selectTournamentForTreeMonitor.addGenerateButtonListener(new SelectTournamentTreeMonitorListener());
    }

    class SelectTournamentTreeMonitorListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                monitorTree.dispose();
            } catch (NullPointerException npe) {
            }
            try {
                monitorPosition.dispose();
            } catch (NullPointerException npe) {
            }

            try {
                Tournament championship = KendoTournamentGenerator.getInstance().database.getTournamentByName(selectTournamentForTreeMonitor.returnSelectedTournamentName(), true);
                if (championship.mode.equals("championship") || championship.mode.equals("tree")) {
                    monitorTree = new MonitorTree(championship);
                    monitorTree.setVisible(true);
                    monitorTree.setExtendedState(monitorTree.getExtendedState() | JFrame.MAXIMIZED_BOTH);
                    monitorTree.updateBlackBoard(selectTournamentForTreeMonitor.returnSelectedTournamentName(), false);
                } else {
                    monitorPosition = new MonitorPosition(championship);
                    monitorPosition.setVisible(true);
                }
                selectTournamentForTreeMonitor.dispose();
            } catch (NullPointerException npe) {
            }
        }
    }

    /**
     * *******************************************************************
     *
     * SELECT TOURNAMENT FOR CSV
     *
     ********************************************************************
     */
    /**
     * Add all listeners to GUI.
     */
    private void AddSelectTournamentCSVListeners() {
        selectTournamentForCSV.addGenerateButtonListener(new SelectTournamentCSVListener());
    }

    class SelectTournamentCSVListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ImportCSV csv = new ImportCSV(selectTournamentForCSV.returnSelectedTournamentName());
            selectTournamentForCSV.dispose();
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
    private void AddSelectTournamentForTeamTopTenListeners() {
        selectTournamentForTeamTopTen.addGenerateButtonListener(new selectTournamentForTeamTopTenListener());
    }

    class selectTournamentForTeamTopTenListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                statisticsTeamTopTen.dispose();
            } catch (NullPointerException npe) {
            }
            statisticsTeamTopTen = new StatisticsTeamTopTen(selectTournamentForTeamTopTen.returnSelectedTournament());
            statisticsTeamTopTen.setVisible(true);
            selectTournamentForTeamTopTen.dispose();
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
    private void AddSelectPerformedHitsCompetitorListeners() {
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
    private void AddSelectReceivedHitsCompetitorListeners() {
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
    private void AddSelectWonFightsCompetitorListeners() {
        selectWonFightsOfCompetitor.addGenerateButtonListener(new SelectWonFightsCompetitorListener());
    }

    class SelectWonFightsCompetitorListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                statisticsTopTen.dispose();
            } catch (NullPointerException npe) {
            }
            statisticsTopTen = new StatisticsTopTen();
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
    private void AddShortNewFightListeners() {
        shortFight.addFightListener(new AddShortFightListener());
    }

    class AddShortFightListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Fight f;
            try {
                f = new Fight(shortFight.getTeam1(), shortFight.getTeam2(),
                        shortFight.getTournament(), shortFight.getArena(),
                        KendoTournamentGenerator.getInstance().fightManager.get(KendoTournamentGenerator.getInstance().fightManager.size() - 1).level + 1);
            } catch (IndexOutOfBoundsException aion) {
                f = new Fight(shortFight.getTeam1(), shortFight.getTeam2(),
                        shortFight.getTournament(), shortFight.getArena(), 0);
            }
            try {
                KendoTournamentGenerator.getInstance().fightManager.add(f);
                MessageManager.translatedMessage("addFight", "MySQL", KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE);
                tournamentPanel.fillFightsPanel();
                KendoTournamentGenerator.getInstance().designedGroups.refillDesigner(KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(shortFight.getTournament().name));
            } catch (NullPointerException npe) {
                KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            }

        }
    }
}
