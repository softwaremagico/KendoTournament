package com.softwaremagico.ktg.gui;
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

import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.Configuration;
import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.MessageManager;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.DatabaseConnection;
import com.softwaremagico.ktg.database.FightPool;
import com.softwaremagico.ktg.database.RolePool;
import com.softwaremagico.ktg.database.TournamentPool;
import com.softwaremagico.ktg.files.MyFile;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.gui.fight.*;
import com.softwaremagico.ktg.gui.tournament.LeagueDesigner;
import com.softwaremagico.ktg.gui.tournament.LeagueEvolution;
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

public class Controller {

    MainGUI main;
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
    private SelectCompetitorForPerformedHits selectPerformedHitsOfCompetitor = null;
    private SelectCompetitorForReceivedHits selectReceivedHitsOfCompetitor = null;
    private SelectCompetitorForWonFights selectWonFightsOfCompetitor = null;
    private ChooseScore chooseScore = null;
    private ShortNewFight shortFight = null;
    private LeagueDesigner designer = null;
    private NewSimpleTournament newFight;
    private NewLoopTournament newRing;
    private LeagueEvolution leagueEvolution = null;
    private SelectTournament selectTournament = null;
    private SelectTournamentExportFightsToCsv selectTournamentExportFightsToCsv = null;
    private SelectTournamentImportFightsFromCsv selectTournamentImportFightsFromCsv = null;
    private SelectTournamentExportParticipantsToCsv selectTournamentExportParticipantsToCsv = null;
    private SelectTournamentImportParticipantsFromCsv selectTournamentImportParticipantsFromCsv = null;
    private ChangeOrderTeam changeTeam = null;
    private DatabaseConversor databaseConversor = null;

    public Controller(MainGUI tmp_gui) {
        main = tmp_gui;
        AddMainMenuListeners();
        main.setVisible(true);
        connectDatabase(!DatabaseConnection.getInstance().isDatabaseConnectionTested());
    }

    private void connectDatabase(boolean connect) {
        if (connect) {
            try {
                databaseConnection.dispose();
            } catch (NullPointerException npe) {
            }
            databaseConnection = new DatabaseConnectionWindow();
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
        //main.addUpdateDatabaseMenuItemListener(new UpdateDatabaseListener());
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
        main.addAccreditionCardMenuItemListener(new AccreditionCardsListener());
        main.addHelpMenuItemListener(new HelpWindowListener());
        main.addScoreMenuItemListener(new ChooseScoreListener());
        main.addExportFightCsvMenuItemListener(new ExportFightCsvListener("CvsMenuItem", "ExportMenu"));
        main.addImportFightCsvMenuItemListener(new ImportFightCsvListener("CvsMenuItem", "ImportMenu"));
        main.addExportParticipantCsvMenuItemListener(new ExportParticipantCsvListener("CvsMenuItem", "ExportMenu"));
        main.addImportParticipantCsvMenuItemListener(new ImportParticipantCsvListener("CvsMenuItem", "ImportMenu"));
        main.addChangeTeamMenuItemListener(new ChangeTeamListener());
        main.addConvertDatabaseMenuItemListener(new DatabaseConversorListener());
        main.addSaveMenuItemListener(new SaveListener());
    }

    class SaveListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (MessageManager.questionMessage("questionUpdateDatabase", "SQL")) {
                DatabaseConnection.getInstance().updateDatabase();
                MessageManager.informationMessage(this.getClass().getName(), "updatedDatabase", "SQL");
            }
        }
    }

    class AddLanguagesListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            KendoTournamentGenerator.getInstance().setLanguage(main.getSelectedLanguage());
            RolePool.getInstance().resetRoleTags();
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
                aboutGui.UpdateText(MyFile.inString(Path.getRootPath() + "Readme.txt", true));
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
                text = MyFile.inString(filename, true);
            } catch (IOException ex) {
            }
            if (text.length() > 0) {
                helpWindow.UpdateText(text);
            } else {
                try {
                    helpWindow.UpdateText(MyFile.inString(Path.getManualPath() + "Guia_EN.txt", true));
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
            DatabaseConnection.getInstance().getDatabase().updateDatabase(Path.returnDatabaseSchemaPath() + File.separator + "updates" + File.separator, true);
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
                selectTournament.dispose();
            } catch (NullPointerException npe) {
            }
            selectTournament = new SelectTournament("titleHitStatistics");
            selectTournament.setVisible(true);
            AddSelectTournamentTopTenListeners();
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
            newRing = new NewLoopTournament();
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

    class ChooseScoreListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                chooseScore.dispose();
            } catch (NullPointerException npe) {
            }
            //Tournament championship = DatabaseConnection.getInstance().getDatabase().getTournamentByName(KendoTournamentGenerator.getInstance().getLastSelectedTournament(), false);
            chooseScore = new ChooseScore();
            chooseScore.setVisible(true);
        }
    }

    class ExportFightCsvListener implements ActionListener {

        private String tag = "";
        private String tagImportExport = "";

        public ExportFightCsvListener(String tag, String tagImportExport) {
            this.tag = tag;
            this.tagImportExport = tagImportExport;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                selectTournamentExportFightsToCsv.dispose();
            } catch (NullPointerException npe) {
            }
            selectTournamentExportFightsToCsv = new SelectTournamentExportFightsToCsv(tag, tagImportExport);
            selectTournamentExportFightsToCsv.setVisible(true);
            selectTournamentExportFightsToCsv.addGenerateButtonListener(new SelectTournamentExportFightCSVListener());
        }
    }

    class ImportFightCsvListener implements ActionListener {

        private String tag = "";
        private String tagImportExport = "";

        public ImportFightCsvListener(String tag, String tagImportExport) {
            this.tag = tag;
            this.tagImportExport = tagImportExport;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                selectTournamentImportFightsFromCsv.dispose();
            } catch (NullPointerException npe) {
            }
            selectTournamentImportFightsFromCsv = new SelectTournamentImportFightsFromCsv(tag, tagImportExport);
            selectTournamentImportFightsFromCsv.setVisible(true);
            selectTournamentImportFightsFromCsv.addGenerateButtonListener(new SelectTournamentImportFightCSVListener());
        }
    }

    class ExportParticipantCsvListener implements ActionListener {

        private String tag = "";
        private String tagImportExport = "";

        public ExportParticipantCsvListener(String tag, String tagImportExport) {
            this.tag = tag;
            this.tagImportExport = tagImportExport;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                selectTournamentExportParticipantsToCsv.dispose();
            } catch (NullPointerException npe) {
            }
            selectTournamentExportParticipantsToCsv = new SelectTournamentExportParticipantsToCsv(tag, tagImportExport);
            selectTournamentExportParticipantsToCsv.setVisible(true);
            selectTournamentExportParticipantsToCsv.addGenerateButtonListener(new SelectTournamentExportParticipantsCSVListener());
        }
    }

    class ImportParticipantCsvListener implements ActionListener {

        private String tag = "";
        private String tagImportExport = "";

        public ImportParticipantCsvListener(String tag, String tagImportExport) {
            this.tag = tag;
            this.tagImportExport = tagImportExport;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                selectTournamentImportParticipantsFromCsv.dispose();
            } catch (NullPointerException npe) {
            }
            selectTournamentImportParticipantsFromCsv = new SelectTournamentImportParticipantsFromCsv(tag, tagImportExport);
            selectTournamentImportParticipantsFromCsv.setVisible(true);
            selectTournamentImportParticipantsFromCsv.addGenerateButtonListener(new SelectTournamentImportParticipantsCSVListener());
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
            main.changeMenuIsConnectedToDatabase();
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
                main.changeMenuIsConnectedToDatabase();
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
            AddSearchCompetitorListeners();
        }
    }

    class AcceptNewCompetitorListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            RegisteredPerson competitor;
            newCompetitor.correctNif();
            if ((competitor = newCompetitor.acceptCompetitor()) != null) {
                try {
                    participantFunction.dispose();
                } catch (NullPointerException npe) {
                }
                if (TournamentPool.getInstance().getSorted().size() > 0) {
                    participantFunction = new NewRole(true, competitor);
                    participantFunction.setVisible(true);
                    //participantFunction.defaultSelect(c);
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
            Club club = searchClub.returnSelectedItem();
            if (club != null) {
                newClub.UpdateWindow(club);
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
            if (newTournament.acceptTournament()) {
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
                chooseScore = new ChooseScore();
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
            Tournament tournament = searchTournament.returnSelectedItem();
            if (tournament != null) {
                newTournament.setVisible(true);
                newTournament.updateWindow(tournament);
                searchTournament.dispose();
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
    private void AddFightPanelListeners() {
        // tournamentPanel.addTreeListener(new TreeButtonListener());
        // tournamentPanel.addFightListener(new AddFightToPanelListener());
    }

    class TreeButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                leagueEvolution.dispose();
            } catch (NullPointerException npe) {
            }
            leagueEvolution = new LeagueEvolution(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
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
            shortFight = new ShortNewFight(tournamentPanel.getSelectedTournament(), tournamentPanel.getSelectedFightArea());
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
            statisticsHits = new StatisticsGeneralHits(selectTournament.returnSelectedTournament());
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
        selectTournament.addGenerateButtonListener(new SelectTournamentTopTenListener());
    }

    class SelectTournamentTopTenListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                statisticsTopTen.dispose();
            } catch (NullPointerException npe) {
            }
            statisticsTopTen = new StatisticsTopTen(selectTournament.returnSelectedTournament());
            statisticsTopTen.setVisible(true);
            selectTournament.dispose();
        }
    }

    /**
     * *******************************************************************
     *
     * SELECT TOURNAMENT FOR EXPORTING FIGHTS TO CSV
     *
     ********************************************************************
     */
    /**
     *
     */
    class SelectTournamentExportFightCSVListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            selectTournamentExportFightsToCsv.generate();
            selectTournamentExportFightsToCsv.dispose();
        }
    }

    /**
     * *******************************************************************
     *
     * SELECT TOURNAMENT FOR IMPORTING FIGHTS TO CSV
     *
     ********************************************************************
     */
    class SelectTournamentImportFightCSVListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            selectTournamentImportFightsFromCsv.generate();
            selectTournamentImportFightsFromCsv.dispose();
        }
    }

    /**
     * *******************************************************************
     *
     * SELECT TOURNAMENT FOR EXPORTING PARTICIPANTS TO CSV
     *
     ********************************************************************
     */
    class SelectTournamentExportParticipantsCSVListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            selectTournamentExportParticipantsToCsv.generate();
            selectTournamentExportParticipantsToCsv.dispose();
        }
    }

    /**
     * *******************************************************************
     *
     * SELECT TOURNAMENT FOR IMPORTING PARTICIPANTS TO CSV
     *
     ********************************************************************
     */
    class SelectTournamentImportParticipantsCSVListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            selectTournamentImportParticipantsFromCsv.generate();
            selectTournamentImportParticipantsFromCsv.dispose();
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
        selectTournament.addGenerateButtonListener(new selectTournamentForTeamTopTenListener());
    }

    class selectTournamentForTeamTopTenListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                statisticsTeamTopTen.dispose();
            } catch (NullPointerException npe) {
            }
            statisticsTeamTopTen = new StatisticsTeamTopTen(selectTournament.returnSelectedTournament());
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
    private void AddShortNewFightListeners() {
        shortFight.addFightListener(new AddShortFightListener());
    }

    class AddShortFightListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Fight f;
            f = new Fight(shortFight.getTournament(),
                    shortFight.getTeam1(), shortFight.getTeam2(),
                    shortFight.getArena(), FightPool.getInstance().getLastLevelUsed(shortFight.getTournament()), 0);
            try {
                FightPool.getInstance().add(KendoTournamentGenerator.getInstance().getLastSelectedTournament(), f);
                MessageManager.translatedMessage(this.getClass().getName(), "addFight", "MySQL", KendoTournamentGenerator.getInstance().getLanguage(), JOptionPane.INFORMATION_MESSAGE);
                tournamentPanel.updateScorePanel();
            } catch (NullPointerException npe) {
                KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
            }

        }
    }
}
