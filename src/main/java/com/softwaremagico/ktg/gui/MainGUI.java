/*
 *   This software is designed by Jorge Hortelano Otero.
 *   softwaremagico@gmail.com
 *   Copyright (C) 2012 Jorge Hortelano Otero.
 *   C/Quart 89, 3. Valencia CP:46008 (Spain).
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version 2
 *   of the License, or (at your option) any later version.
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *   Created on 09-dic-2008.
 */
package com.softwaremagico.ktg.gui;

import com.softwaremagico.ktg.files.Path;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.MessageManager;
import com.softwaremagico.ktg.StoreDatabase;
import com.softwaremagico.ktg.language.Translator;

/**
 *
 * @author jorge
 */
public class MainGUI extends KendoFrame {

    Translator trans = null;
    private List<JRadioButtonMenuItem> languageList = new ArrayList<JRadioButtonMenuItem>();
    private PhotoFrame banner;
    private boolean refresh = true;

    /**
     * Creates new form MainGUI
     */
    public MainGUI() {
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage(KendoTournamentGenerator.getInstance().language);
        completeLanguageMenu();
        setPhoto();
        isConnectedToDatabase();
        updateConfig();
    }

    /**
     * Translate the GUI to the selected language.
     */
    public final void setLanguage(String language) {
        trans = new Translator("gui.xml");
        TournamentMenu.setText(trans.returnTag("FileMenu", language));
        LanguageMenu.setText(trans.returnTag("LanguageMenu", language));
        OptionsMenu.setText(trans.returnTag("OptionsMenu", language));
        StatisticsMenu.setText(trans.returnTag("StatisticsMenu", language));
        InsertMenu.setText(trans.returnTag("InsertMenu", language));
        TournamentMenu.setText(trans.returnTag("TournamentMenu", language));
        TournamentPanelMenuItem.setText(trans.returnTag("TournamentPanelMenuItem", language));
        HelpMenu.setText(trans.returnTag("HelpMenu", language));
        AboutMenuItem.setText(trans.returnTag("AboutMenu", language));
        CompetitorMenuItem.setText(trans.returnTag("CompetitorMenuItem", language));
        RoleMenuItem.setText(trans.returnTag("RoleMenuItem", language));
        TournamentMenuItem.setText(trans.returnTag("TournamentMenuItem", language));
        ClubMenuItem.setText(trans.returnTag("ClubMenuItem", language));
        TeamMenuItem.setText(trans.returnTag("TeamMenuItem", language));
        FightMenuItem.setText(trans.returnTag("FightMenuItem", language));
        DatabaseMenu.setText(trans.returnTag("DatabaseMenu", language));
        DatabaseConnectMenuItem.setText(trans.returnTag("DatabaseConnectMenuItem", language));
        DatabaseDisconnectMenuItem.setText(trans.returnTag("DatabaseDisconnectMenuItem", language));
        DatabaseUpdateMenuItem.setText(trans.returnTag("DatabaseUpdateMenuItem", language));
        ListMenu.setText(trans.returnTag("ListMenu", language));
        TeamListMenuItem.setText(trans.returnTag("TeamListMenuItem", language));
        FightListMenuItem.setText(trans.returnTag("FightListMenuItem", language));
        PointListMenuItem.setText(trans.returnTag("PointListMenuItem", language));
        DiplomaMenuItem.setText(trans.returnTag("DiplomaMenuItem", language));
        CompetitorStatisticsMenu.setText(trans.returnTag("CompetitorStatisticsMenu", language));
        PerformedHitsMenuItem.setText(trans.returnTag("PerformedHitsStatisticsMenuItem", language));
        ReceivedHitsMenuItem.setText(trans.returnTag("ReceivedHitsStatisticsMenuItem", language));
        WonLostMenuItem.setText(trans.returnTag("WonLostMenuItem", language));
        TournamentStatisticsMenu.setText(trans.returnTag("TournamentStatisticsMenu", language));
        TeamStatisticsMenu.setText(trans.returnTag("TeamStatisticsMenu", language));
        TournamentTopTenMenuItem.setText(trans.returnTag("TournamentTopTenMenuItem", language));
        TournamentHitsStatisticsMenuItem.setText(trans.returnTag("TournamentHitsStatisticsMenuItem", language));
        DefineFightsMenu.setText(trans.returnTag("DefineFightsMenu", language));
        ProgramMenu.setText(trans.returnTag("ProgramMenu", language));
        ExitMenuItem.setText(trans.returnTag("ExitMenuItem", language));
        ManualFightsMenuItem.setText(trans.returnTag("ManualFightsMenuItem", language));
        RingMenuItem.setText(trans.returnTag("RingMenuItem", language));
        DesignerMenuItem.setText(trans.returnTag("DesignerMenuItem", language));
        TeamTopTenMenuItem.setText(trans.returnTag("TeamTopTenMenuItem", language));
        MonitorMenu.setText(trans.returnTag("MonitorMenuItem", language));
        ScoreMonitorMenuItem.setText(trans.returnTag("ScoreMonitorMenuItem", language));
        TreeMonitorMenuItem.setText(trans.returnTag("TreeMonitorMenuItem", language));
        AccreditationMenuItem.setText(trans.returnTag("AccreditationMenuItem", language));
        HelpMenuItem.setText(trans.returnTag("HelpMenuItem", language));
        SummaryMenuItem.setText(trans.returnTag("SummaryMenuItem", language));
        ClubListMenuItem.setText(trans.returnTag("ClubListMenuItem", language));
        RefereeListMenuItem.setText(trans.returnTag("RefereeListMenuItem", language));
        ScoreMenuItem.setText(trans.returnTag("ScoreMenuItem", language));
        ImportMenu.setText(trans.returnTag("ImportMenu", language));
        CsvMenuItem.setText(trans.returnTag("CvsMenuItem", language));
        ExportMenuItem.setText(trans.returnTag("ExportDatabase", language));
        ImportMenuItem.setText(trans.returnTag("ImportDatabase", language));
        ChangeTeamMenuItem.setText(trans.returnTag("ChangeTeamOrder", language));
        LogMenuItem.setText(trans.returnTag("LogOption", language));
        DebugMenuItem.setText(trans.returnTag("DebugOption", language));
    }

    private void setPhoto() {
        banner = new PhotoFrame(MainPhotoPanel, Path.returnMainPhoto());
        // MainPhotoPanel.add(banner, 0);
        banner.repaint();
    }

    /**
     * Generates the Menu items depending on the lenguages available on the XML
     * files
     */
    private void completeLanguageMenu() {
        for (int i = 0; i < KendoTournamentGenerator.getInstance().languages.size(); i++) {
            javax.swing.JRadioButtonMenuItem MenuItem;
            MenuItem = new javax.swing.JRadioButtonMenuItem(KendoTournamentGenerator.getInstance().languages.getName(i), new ImageIcon(Path.returnImagePath() + KendoTournamentGenerator.getInstance().languages.getPathToFlag(i)));
            if (KendoTournamentGenerator.getInstance().language.equals(KendoTournamentGenerator.getInstance().languages.getAbbreviature(i))) {
                MenuItem.setSelected(true);
            }
            LanguageMenu.add(MenuItem);
            LanguageButtonGroup.add(MenuItem);
            languageList.add(MenuItem);
        }
    }

    public String ReturnSelectedLanguage() {
        for (int i = 0; i < languageList.size(); i++) {
            if (languageList.get(i).isSelected()) {
                return KendoTournamentGenerator.getInstance().languages.getAbbreviature(i);
            }
        }
        return KendoTournamentGenerator.getInstance().language;
    }

    public final void isConnectedToDatabase() {
        boolean connected = KendoTournamentGenerator.getInstance().databaseConnected;
        DiplomaMenuItem.setEnabled(connected);
        TournamentPanelMenuItem.setEnabled(connected);
        TournamentTopTenMenuItem.setEnabled(connected);
        PointListMenuItem.setEnabled(connected);
        FightListMenuItem.setEnabled(connected);
        TeamListMenuItem.setEnabled(connected);
        DatabaseConnectMenuItem.setEnabled(!connected);
        DatabaseDisconnectMenuItem.setEnabled(connected);
        FightMenuItem.setEnabled(connected);
        TeamMenuItem.setEnabled(connected);
        ClubMenuItem.setEnabled(connected);
        TournamentMenuItem.setEnabled(connected);
        RoleMenuItem.setEnabled(connected);
        CompetitorMenuItem.setEnabled(connected);
        TournamentHitsStatisticsMenuItem.setEnabled(connected);
        CompetitorStatisticsMenu.setEnabled(connected);
        TournamentStatisticsMenu.setEnabled(connected);
        DesignerMenuItem.setEnabled(connected);
        ManualFightsMenuItem.setEnabled(connected);
        TeamStatisticsMenu.setEnabled(connected);
        DefineFightsMenu.setEnabled(connected);
        ScoreMonitorMenuItem.setEnabled(connected);
        TreeMonitorMenuItem.setEnabled(connected);
        MonitorMenu.setEnabled(connected);
        AccreditationMenuItem.setEnabled(connected);
        DatabaseUpdateMenuItem.setEnabled(connected);
        ScoreMenuItem.setEnabled(connected);
        SummaryMenuItem.setEnabled(connected);
        RefereeListMenuItem.setEnabled(connected);
        ImportMenu.setEnabled(connected);
        CsvMenuItem.setEnabled(connected);
        ClubListMenuItem.setEnabled(connected);
        ExportMenuItem.setEnabled(connected);
        ImportMenuItem.setEnabled(connected);
        ChangeTeamMenuItem.setEnabled(connected);
    }

    @Override
    public String defaultFileName() {
        return "";
    }

    private void updateConfig() {
        refresh = false;
        LogMenuItem.setState(KendoTournamentGenerator.getInstance().getLogOption());
        DebugMenuItem.setState(KendoTournamentGenerator.getInstance().getDebugOptionSelected());
        refresh = true;
    }

    /**
     * **********************************************
     *
     * LISTENERS
     *
     ***********************************************
     */
    /**
     * Add the same action listener to all langugaes of the menu.
     *
     * @param al
     */
    public void addListenerToLanguages(ActionListener al) {
        for (int i = 0; i < languageList.size(); i++) {
            languageList.get(i).addActionListener(al);
        }
    }

    public void addAboutMenuItemListener(ActionListener al) {
        AboutMenuItem.addActionListener(al);
    }

    public void addCompetitorMenuItemListener(ActionListener al) {
        CompetitorMenuItem.addActionListener(al);
    }

    public void addRoleMenuItemListener(ActionListener al) {
        RoleMenuItem.addActionListener(al);
    }

    public void addTournamentMenuItemListener(ActionListener al) {
        TournamentMenuItem.addActionListener(al);
    }

    public void addTournamentPanelMenuItemListener(ActionListener al) {
        TournamentPanelMenuItem.addActionListener(al);
    }

    public void addClubMenuItemListener(ActionListener al) {
        ClubMenuItem.addActionListener(al);
    }

    public void addTeamMenuItemListener(ActionListener al) {
        TeamMenuItem.addActionListener(al);
    }

    public void addConnectDatabaseMenuItemListener(ActionListener al) {
        DatabaseConnectMenuItem.addActionListener(al);
    }

    public void addUpdateDatabaseMenuItemListener(ActionListener al) {
        DatabaseUpdateMenuItem.addActionListener(al);
    }

    public void addTeamListMenuItemListener(ActionListener al) {
        TeamListMenuItem.addActionListener(al);
    }

    public void addRefereeListMenuItemListener(ActionListener al) {
        RefereeListMenuItem.addActionListener(al);
    }

    public void addFightListMenuItemListener(ActionListener al) {
        FightListMenuItem.addActionListener(al);
    }

    public void addPointListMenuItemListener(ActionListener al) {
        PointListMenuItem.addActionListener(al);
    }

    public void addDiplomaListMenuItemListener(ActionListener al) {
        DiplomaMenuItem.addActionListener(al);
    }

    public void addSummaryMenuItemListener(ActionListener al) {
        SummaryMenuItem.addActionListener(al);
    }

    public void addClubListMenuItemListener(ActionListener al) {
        ClubListMenuItem.addActionListener(al);
    }

    public void addHitsStatisticsMenuItemListener(ActionListener al) {
        TournamentHitsStatisticsMenuItem.addActionListener(al);
    }

    public void addPerformedHitsStatisticsMenuItemListener(ActionListener al) {
        PerformedHitsMenuItem.addActionListener(al);
    }

    public void addReceivedHitsStatisticsMenuItemListener(ActionListener al) {
        ReceivedHitsMenuItem.addActionListener(al);
    }

    public void addWonFightsStatisticsMenuItemListener(ActionListener al) {
        WonLostMenuItem.addActionListener(al);
    }

    public void addTopTenStatisticsMenuItemListener(ActionListener al) {
        TournamentTopTenMenuItem.addActionListener(al);
    }

    public void addManualMenuItemListener(ActionListener al) {
        ManualFightsMenuItem.addActionListener(al);
    }

    public void addFightMenuItemListener(ActionListener al) {
        FightMenuItem.addActionListener(al);
    }

    public void addRingMenuItemListener(ActionListener al) {
        RingMenuItem.addActionListener(al);
    }

    public void addDesignerMenuItemListener(ActionListener al) {
        DesignerMenuItem.addActionListener(al);
    }

    public void addTeamTopTenListener(ActionListener al) {
        TeamTopTenMenuItem.addActionListener(al);
    }

    public void addScoreMonitorListener(ActionListener al) {
        ScoreMonitorMenuItem.addActionListener(al);
    }

    public void addTreeMonitorListener(ActionListener al) {
        TreeMonitorMenuItem.addActionListener(al);
    }

    public void addAccreditionCardMenuItemListener(ActionListener al) {
        AccreditationMenuItem.addActionListener(al);
    }

    public void addHelpMenuItemListener(ActionListener al) {
        HelpMenuItem.addActionListener(al);
    }

    public void addScoreMenuItemListener(ActionListener al) {
        ScoreMenuItem.addActionListener(al);
    }

    public void addCsvMenuItemListener(ActionListener al) {
        CsvMenuItem.addActionListener(al);
    }

    public void addChangeTeamMenuItemListener(ActionListener al) {
        ChangeTeamMenuItem.addActionListener(al);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LanguageButtonGroup = new javax.swing.ButtonGroup();
        MainPhotoPanel = new javax.swing.JPanel();
        MainMenuBar = new javax.swing.JMenuBar();
        ProgramMenu = new javax.swing.JMenu();
        DatabaseMenu = new javax.swing.JMenu();
        DatabaseConnectMenuItem = new javax.swing.JMenuItem();
        DatabaseDisconnectMenuItem = new javax.swing.JMenuItem();
        ExportMenuItem = new javax.swing.JMenuItem();
        ImportMenuItem = new javax.swing.JMenuItem();
        DatabaseUpdateMenuItem = new javax.swing.JMenuItem();
        MonitorMenu = new javax.swing.JMenu();
        ScoreMonitorMenuItem = new javax.swing.JMenuItem();
        TreeMonitorMenuItem = new javax.swing.JMenuItem();
        ImportMenu = new javax.swing.JMenu();
        CsvMenuItem = new javax.swing.JMenuItem();
        ExitMenuItem = new javax.swing.JMenuItem();
        InsertMenu = new javax.swing.JMenu();
        ClubMenuItem = new javax.swing.JMenuItem();
        CompetitorMenuItem = new javax.swing.JMenuItem();
        TournamentMenuItem = new javax.swing.JMenuItem();
        TournamentMenu = new javax.swing.JMenu();
        ScoreMenuItem = new javax.swing.JMenuItem();
        RoleMenuItem = new javax.swing.JMenuItem();
        TeamMenuItem = new javax.swing.JMenuItem();
        ChangeTeamMenuItem = new javax.swing.JMenuItem();
        DefineFightsMenu = new javax.swing.JMenu();
        ManualFightsMenuItem = new javax.swing.JMenuItem();
        FightMenuItem = new javax.swing.JMenuItem();
        RingMenuItem = new javax.swing.JMenuItem();
        DesignerMenuItem = new javax.swing.JMenuItem();
        TournamentPanelMenuItem = new javax.swing.JMenuItem();
        ListMenu = new javax.swing.JMenu();
        AccreditationMenuItem = new javax.swing.JMenuItem();
        ClubListMenuItem = new javax.swing.JMenuItem();
        TeamListMenuItem = new javax.swing.JMenuItem();
        RefereeListMenuItem = new javax.swing.JMenuItem();
        FightListMenuItem = new javax.swing.JMenuItem();
        PointListMenuItem = new javax.swing.JMenuItem();
        SummaryMenuItem = new javax.swing.JMenuItem();
        DiplomaMenuItem = new javax.swing.JMenuItem();
        StatisticsMenu = new javax.swing.JMenu();
        TournamentStatisticsMenu = new javax.swing.JMenu();
        TournamentHitsStatisticsMenuItem = new javax.swing.JMenuItem();
        TournamentTopTenMenuItem = new javax.swing.JMenuItem();
        TeamStatisticsMenu = new javax.swing.JMenu();
        TeamTopTenMenuItem = new javax.swing.JMenuItem();
        CompetitorStatisticsMenu = new javax.swing.JMenu();
        WonLostMenuItem = new javax.swing.JMenuItem();
        PerformedHitsMenuItem = new javax.swing.JMenuItem();
        ReceivedHitsMenuItem = new javax.swing.JMenuItem();
        OptionsMenu = new javax.swing.JMenu();
        LanguageMenu = new javax.swing.JMenu();
        LogMenuItem = new javax.swing.JCheckBoxMenuItem();
        DebugMenuItem = new javax.swing.JCheckBoxMenuItem();
        HelpMenu = new javax.swing.JMenu();
        HelpMenuItem = new javax.swing.JMenuItem();
        AboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Kendo Tournament Administration Tool");
        setResizable(false);

        MainPhotoPanel.setOpaque(false);
        MainPhotoPanel.setLayout(new java.awt.BorderLayout());

        ProgramMenu.setText("Program");

        DatabaseMenu.setText("Database");

        DatabaseConnectMenuItem.setText("Connect");
        DatabaseMenu.add(DatabaseConnectMenuItem);

        DatabaseDisconnectMenuItem.setText("Disconnect");
        DatabaseDisconnectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DatabaseDisconnectMenuItemActionPerformed(evt);
            }
        });
        DatabaseMenu.add(DatabaseDisconnectMenuItem);

        ExportMenuItem.setText("Export...");
        ExportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExportMenuItemActionPerformed(evt);
            }
        });
        DatabaseMenu.add(ExportMenuItem);

        ImportMenuItem.setText("Import...");
        ImportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportMenuItemActionPerformed(evt);
            }
        });
        DatabaseMenu.add(ImportMenuItem);

        DatabaseUpdateMenuItem.setText("Update");
        DatabaseMenu.add(DatabaseUpdateMenuItem);

        ProgramMenu.add(DatabaseMenu);

        MonitorMenu.setText("Monitor");

        ScoreMonitorMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        ScoreMonitorMenuItem.setText("Score");
        MonitorMenu.add(ScoreMonitorMenuItem);

        TreeMonitorMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        TreeMonitorMenuItem.setText("Tree");
        MonitorMenu.add(TreeMonitorMenuItem);

        ProgramMenu.add(MonitorMenu);

        ImportMenu.setText("Import");

        CsvMenuItem.setText("CSV");
        ImportMenu.add(CsvMenuItem);

        ProgramMenu.add(ImportMenu);

        ExitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        ExitMenuItem.setText("Exit");
        ExitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitMenuItemActionPerformed(evt);
            }
        });
        ProgramMenu.add(ExitMenuItem);

        MainMenuBar.add(ProgramMenu);

        InsertMenu.setText("Insert");

        ClubMenuItem.setText("Add Club");
        InsertMenu.add(ClubMenuItem);

        CompetitorMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        CompetitorMenuItem.setText("Add Competitor");
        InsertMenu.add(CompetitorMenuItem);

        TournamentMenuItem.setText("Add Tournament");
        InsertMenu.add(TournamentMenuItem);

        MainMenuBar.add(InsertMenu);

        TournamentMenu.setText("Tournament");

        ScoreMenuItem.setText("Score");
        TournamentMenu.add(ScoreMenuItem);

        RoleMenuItem.setText("Define competitor's role");
        TournamentMenu.add(RoleMenuItem);

        TeamMenuItem.setText("Add Team");
        TournamentMenu.add(TeamMenuItem);

        ChangeTeamMenuItem.setText("Change Team Order");
        TournamentMenu.add(ChangeTeamMenuItem);

        DefineFightsMenu.setText("Define Fights");

        ManualFightsMenuItem.setText("Manual");
        DefineFightsMenu.add(ManualFightsMenuItem);

        FightMenuItem.setText("Add Fight");
        DefineFightsMenu.add(FightMenuItem);

        RingMenuItem.setText("Ring Tournament");
        DefineFightsMenu.add(RingMenuItem);

        DesignerMenuItem.setText("Designer");
        DefineFightsMenu.add(DesignerMenuItem);

        TournamentMenu.add(DefineFightsMenu);

        TournamentPanelMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        TournamentPanelMenuItem.setText("Tournament Panel");
        TournamentMenu.add(TournamentPanelMenuItem);

        MainMenuBar.add(TournamentMenu);

        ListMenu.setText("Lists");

        AccreditationMenuItem.setText("Accreditation Cards");
        ListMenu.add(AccreditationMenuItem);

        ClubListMenuItem.setText("List of Clubs");
        ListMenu.add(ClubListMenuItem);

        TeamListMenuItem.setText("List of Teams");
        ListMenu.add(TeamListMenuItem);

        RefereeListMenuItem.setText("List of Referees");
        ListMenu.add(RefereeListMenuItem);

        FightListMenuItem.setText("List of Fights");
        ListMenu.add(FightListMenuItem);

        PointListMenuItem.setText("List of Points");
        ListMenu.add(PointListMenuItem);

        SummaryMenuItem.setText("Fights Summary");
        ListMenu.add(SummaryMenuItem);

        DiplomaMenuItem.setText("Diploma");
        ListMenu.add(DiplomaMenuItem);

        MainMenuBar.add(ListMenu);

        StatisticsMenu.setText("Statistics");

        TournamentStatisticsMenu.setText("By tournament");

        TournamentHitsStatisticsMenuItem.setText("Hits");
        TournamentStatisticsMenu.add(TournamentHitsStatisticsMenuItem);

        TournamentTopTenMenuItem.setText("Top Ten");
        TournamentStatisticsMenu.add(TournamentTopTenMenuItem);

        StatisticsMenu.add(TournamentStatisticsMenu);

        TeamStatisticsMenu.setText("By team");

        TeamTopTenMenuItem.setText("Top Ten");
        TeamStatisticsMenu.add(TeamTopTenMenuItem);

        StatisticsMenu.add(TeamStatisticsMenu);

        CompetitorStatisticsMenu.setText("By competitor");

        WonLostMenuItem.setText("Fights Won");
        CompetitorStatisticsMenu.add(WonLostMenuItem);

        PerformedHitsMenuItem.setText("Performed Hits");
        CompetitorStatisticsMenu.add(PerformedHitsMenuItem);

        ReceivedHitsMenuItem.setText("Received Hits");
        CompetitorStatisticsMenu.add(ReceivedHitsMenuItem);

        StatisticsMenu.add(CompetitorStatisticsMenu);

        MainMenuBar.add(StatisticsMenu);

        OptionsMenu.setText("Options");

        LanguageMenu.setText("Language");
        OptionsMenu.add(LanguageMenu);

        LogMenuItem.setSelected(true);
        LogMenuItem.setText("Log");
        LogMenuItem.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LogMenuItemItemStateChanged(evt);
            }
        });
        OptionsMenu.add(LogMenuItem);

        DebugMenuItem.setSelected(true);
        DebugMenuItem.setText("Debug");
        DebugMenuItem.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                DebugMenuItemItemStateChanged(evt);
            }
        });
        OptionsMenu.add(DebugMenuItem);

        MainMenuBar.add(OptionsMenu);

        HelpMenu.setText("Help");

        HelpMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        HelpMenuItem.setText("Help");
        HelpMenu.add(HelpMenuItem);

        AboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        AboutMenuItem.setText("About...");
        HelpMenu.add(AboutMenuItem);

        MainMenuBar.add(HelpMenu);

        setJMenuBar(MainMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MainPhotoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MainPhotoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void DatabaseDisconnectMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DatabaseDisconnectMenuItemActionPerformed
        try {
            KendoTournamentGenerator.getInstance().database.disconnect();
            KendoTournamentGenerator.getInstance().databaseConnected = false;
            isConnectedToDatabase();
            MessageManager.customMessage("databaseDisconnected", "MySQL", KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
        } catch (SQLException ex) {
            MessageManager.errorMessage("disconnectDatabaseFail", "MySQL", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
}//GEN-LAST:event_DatabaseDisconnectMenuItemActionPerformed

    private void ExitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitMenuItemActionPerformed
        this.dispose();
    }//GEN-LAST:event_ExitMenuItemActionPerformed

    private void ImportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportMenuItemActionPerformed
        String file;
        if (!(file = exploreWindowsForKtg(trans.returnTag("ImportSQL", KendoTournamentGenerator.getInstance().language),
                JFileChooser.FILES_AND_DIRECTORIES, "")).equals("")) {
            //KendoTournament.getInstance().database.importDatabase(file);
            new StoreDatabase().load(file);
        }
    }//GEN-LAST:event_ImportMenuItemActionPerformed

    private void ExportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportMenuItemActionPerformed
        String file;
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        String date = year + "-" + month + "-" + day;
        if (!(file = exploreWindowsForKtg(trans.returnTag("ExportSQL", KendoTournamentGenerator.getInstance().language),
                JFileChooser.FILES_AND_DIRECTORIES, "KendoTournament_" + date + ".ktg")).equals("")) {
            //KendoTournament.getInstance().database.exportDatabase(file);
            new StoreDatabase().save(file);
        }
}//GEN-LAST:event_ExportMenuItemActionPerformed

private void LogMenuItemItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LogMenuItemItemStateChanged
    if (refresh) {
        KendoTournamentGenerator.getInstance().changeLogOption(LogMenuItem.getState());
    }
}//GEN-LAST:event_LogMenuItemItemStateChanged

    private void DebugMenuItemItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DebugMenuItemItemStateChanged
        if (refresh) {
            KendoTournamentGenerator.getInstance().changeDebugOption(DebugMenuItem.getState());
        }
    }//GEN-LAST:event_DebugMenuItemItemStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AboutMenuItem;
    private javax.swing.JMenuItem AccreditationMenuItem;
    private javax.swing.JMenuItem ChangeTeamMenuItem;
    private javax.swing.JMenuItem ClubListMenuItem;
    private javax.swing.JMenuItem ClubMenuItem;
    private javax.swing.JMenuItem CompetitorMenuItem;
    private javax.swing.JMenu CompetitorStatisticsMenu;
    private javax.swing.JMenuItem CsvMenuItem;
    private javax.swing.JMenuItem DatabaseConnectMenuItem;
    private javax.swing.JMenuItem DatabaseDisconnectMenuItem;
    private javax.swing.JMenu DatabaseMenu;
    private javax.swing.JMenuItem DatabaseUpdateMenuItem;
    private javax.swing.JCheckBoxMenuItem DebugMenuItem;
    private javax.swing.JMenu DefineFightsMenu;
    private javax.swing.JMenuItem DesignerMenuItem;
    private javax.swing.JMenuItem DiplomaMenuItem;
    private javax.swing.JMenuItem ExitMenuItem;
    private javax.swing.JMenuItem ExportMenuItem;
    private javax.swing.JMenuItem FightListMenuItem;
    private javax.swing.JMenuItem FightMenuItem;
    private javax.swing.JMenu HelpMenu;
    private javax.swing.JMenuItem HelpMenuItem;
    private javax.swing.JMenu ImportMenu;
    private javax.swing.JMenuItem ImportMenuItem;
    private javax.swing.JMenu InsertMenu;
    private javax.swing.ButtonGroup LanguageButtonGroup;
    private javax.swing.JMenu LanguageMenu;
    private javax.swing.JMenu ListMenu;
    private javax.swing.JCheckBoxMenuItem LogMenuItem;
    private javax.swing.JMenuBar MainMenuBar;
    private javax.swing.JPanel MainPhotoPanel;
    private javax.swing.JMenuItem ManualFightsMenuItem;
    private javax.swing.JMenu MonitorMenu;
    private javax.swing.JMenu OptionsMenu;
    private javax.swing.JMenuItem PerformedHitsMenuItem;
    private javax.swing.JMenuItem PointListMenuItem;
    private javax.swing.JMenu ProgramMenu;
    private javax.swing.JMenuItem ReceivedHitsMenuItem;
    private javax.swing.JMenuItem RefereeListMenuItem;
    private javax.swing.JMenuItem RingMenuItem;
    private javax.swing.JMenuItem RoleMenuItem;
    private javax.swing.JMenuItem ScoreMenuItem;
    private javax.swing.JMenuItem ScoreMonitorMenuItem;
    private javax.swing.JMenu StatisticsMenu;
    private javax.swing.JMenuItem SummaryMenuItem;
    private javax.swing.JMenuItem TeamListMenuItem;
    private javax.swing.JMenuItem TeamMenuItem;
    private javax.swing.JMenu TeamStatisticsMenu;
    private javax.swing.JMenuItem TeamTopTenMenuItem;
    private javax.swing.JMenuItem TournamentHitsStatisticsMenuItem;
    private javax.swing.JMenu TournamentMenu;
    private javax.swing.JMenuItem TournamentMenuItem;
    private javax.swing.JMenuItem TournamentPanelMenuItem;
    private javax.swing.JMenu TournamentStatisticsMenu;
    private javax.swing.JMenuItem TournamentTopTenMenuItem;
    private javax.swing.JMenuItem TreeMonitorMenuItem;
    private javax.swing.JMenuItem WonLostMenuItem;
    // End of variables declaration//GEN-END:variables
}
