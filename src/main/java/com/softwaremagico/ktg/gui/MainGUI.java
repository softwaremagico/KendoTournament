package com.softwaremagico.ktg.gui;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 * Jorge Hortelano Otero <softwaremagico@gmail.com>
 * C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.MessageManager;
import com.softwaremagico.ktg.database.StoreDatabase;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
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

/**
 *
 * @author jorge
 */
public class MainGUI extends KendoFrame {

    Translator trans = null;
    private List<JRadioButtonMenuItem> languageList = new ArrayList<>();
    private PhotoFrame banner;
    private boolean refresh = true;

    /**
     * Creates new form MainGUI
     */
    public MainGUI() {
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage();
        completeLanguageMenu();
        setPhoto();
        isConnectedToDatabase();
        updateConfig();
    }

    /**
     * Translate the GUI to the selected language.
     */
    public final void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        TournamentMenu.setText(trans.returnTag("FileMenu"));
        LanguageMenu.setText(trans.returnTag("LanguageMenu"));
        OptionsMenu.setText(trans.returnTag("OptionsMenu"));
        StatisticsMenu.setText(trans.returnTag("StatisticsMenu"));
        InsertMenu.setText(trans.returnTag("InsertMenu"));
        TournamentMenu.setText(trans.returnTag("TournamentMenu"));
        TournamentPanelMenuItem.setText(trans.returnTag("TournamentPanelMenuItem"));
        HelpMenu.setText(trans.returnTag("HelpMenu"));
        AboutMenuItem.setText(trans.returnTag("AboutMenu"));
        CompetitorMenuItem.setText(trans.returnTag("CompetitorMenuItem"));
        RoleMenuItem.setText(trans.returnTag("RoleMenuItem"));
        TournamentMenuItem.setText(trans.returnTag("TournamentMenuItem"));
        ClubMenuItem.setText(trans.returnTag("ClubMenuItem"));
        TeamMenuItem.setText(trans.returnTag("TeamMenuItem"));
        FightMenuItem.setText(trans.returnTag("FightMenuItem"));
        DatabaseMenu.setText(trans.returnTag("DatabaseMenu"));
        DatabaseConnectMenuItem.setText(trans.returnTag("DatabaseConnectMenuItem"));
        DatabaseDisconnectMenuItem.setText(trans.returnTag("DatabaseDisconnectMenuItem"));
        DatabaseUpdateMenuItem.setText(trans.returnTag("DatabaseUpdateMenuItem"));
        ListMenu.setText(trans.returnTag("ListMenu"));
        TeamListMenuItem.setText(trans.returnTag("TeamListMenuItem"));
        FightListMenuItem.setText(trans.returnTag("FightListMenuItem"));
        PointListMenuItem.setText(trans.returnTag("PointListMenuItem"));
        DiplomaMenuItem.setText(trans.returnTag("DiplomaMenuItem"));
        CompetitorStatisticsMenu.setText(trans.returnTag("CompetitorStatisticsMenu"));
        PerformedHitsMenuItem.setText(trans.returnTag("PerformedHitsStatisticsMenuItem"));
        ReceivedHitsMenuItem.setText(trans.returnTag("ReceivedHitsStatisticsMenuItem"));
        WonLostMenuItem.setText(trans.returnTag("WonLostMenuItem"));
        TournamentStatisticsMenu.setText(trans.returnTag("TournamentStatisticsMenu"));
        TeamStatisticsMenu.setText(trans.returnTag("TeamStatisticsMenu"));
        TournamentTopTenMenuItem.setText(trans.returnTag("TournamentTopTenMenuItem"));
        TournamentHitsStatisticsMenuItem.setText(trans.returnTag("TournamentHitsStatisticsMenuItem"));
        DefineFightsMenu.setText(trans.returnTag("DefineFightsMenu"));
        ProgramMenu.setText(trans.returnTag("ProgramMenu"));
        ExitMenuItem.setText(trans.returnTag("ExitMenuItem"));
        ManualFightsMenuItem.setText(trans.returnTag("ManualFightsMenuItem"));
        RingMenuItem.setText(trans.returnTag("RingMenuItem"));
        DesignerMenuItem.setText(trans.returnTag("DesignerMenuItem"));
        TeamTopTenMenuItem.setText(trans.returnTag("TeamTopTenMenuItem"));
        MonitorMenu.setText(trans.returnTag("MonitorMenuItem"));
        ScoreMonitorMenuItem.setText(trans.returnTag("ScoreMonitorMenuItem"));
        TreeMonitorMenuItem.setText(trans.returnTag("TreeMonitorMenuItem"));
        AccreditationMenuItem.setText(trans.returnTag("AccreditationMenuItem"));
        HelpMenuItem.setText(trans.returnTag("HelpMenuItem"));
        SummaryMenuItem.setText(trans.returnTag("SummaryMenuItem"));
        ClubListMenuItem.setText(trans.returnTag("ClubListMenuItem"));
        RefereeListMenuItem.setText(trans.returnTag("RefereeListMenuItem"));
        ScoreMenuItem.setText(trans.returnTag("ScoreMenuItem"));
        ImportMenu.setText(trans.returnTag("ImportMenu"));
        CsvMenuItem.setText(trans.returnTag("CvsMenuItem"));
        ExportMenuItem.setText(trans.returnTag("ExportDatabase"));
        ImportMenuItem.setText(trans.returnTag("ImportDatabase"));
        ChangeTeamMenuItem.setText(trans.returnTag("ChangeTeamOrder"));
        LogMenuCheckBox.setText(trans.returnTag("LogOption"));
        DebugMenuCheckBox.setText(trans.returnTag("DebugOption"));
        ConvertDatabaseMenuItem.setText(trans.returnTag("ConvertDatabase"));
        FightsCardMenuItem.setText(trans.returnTag("FightsCard"));
    }

    private void setPhoto() {
        banner = new PhotoFrame(MainPhotoPanel, Path.returnMainPhoto());
        MainPhotoPanel.add(banner, 0);
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
        ConvertDatabaseMenuItem.setEnabled(true);
    }

    @Override
    public String defaultFileName() {
        return "";
    }

    private void updateConfig() {
        refresh = false;
        LogMenuCheckBox.setState(KendoTournamentGenerator.getInstance().getLogOption());
        DebugMenuCheckBox.setState(KendoTournamentGenerator.getInstance().getDebugOptionSelected());
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

    public void addConvertDatabaseMenuItemListener(ActionListener al) {
        ConvertDatabaseMenuItem.addActionListener(al);
    }

    public void addFightsCardMenuItemListener(ActionListener al) {
        FightsCardMenuItem.addActionListener(al);
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
        DatabaseMenu.setIcon(new ImageIcon(Path.returnIconFolder()+"book.png"));
        DatabaseConnectMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"connect.png"));
        DatabaseDisconnectMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"disconnect.png"));
        ExportMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"export.png"));
        ImportMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"import.png"));
        DatabaseUpdateMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"execute.png"));
        ConvertDatabaseMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"convertDatabase.png"));
        MonitorMenu = new javax.swing.JMenu();
        MonitorMenu.setIcon(new ImageIcon(Path.returnIconFolder()+"monitor.png"));
        ScoreMonitorMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"score.png"));
        TreeMonitorMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"project.png"));
        ImportMenu = new javax.swing.JMenu();
        CsvMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"text.png"));
        ExitMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"exit.png"));
        InsertMenu = new javax.swing.JMenu();
        ClubMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"club.png"));
        CompetitorMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"user.png"));
        TournamentMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"highscores.png"));
        TournamentMenu = new javax.swing.JMenu();
        ScoreMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"score.png"));
        RoleMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"photo.png"));
        TeamMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"team.png"));
        ChangeTeamMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"changeTeam.png"));
        DefineFightsMenu = new javax.swing.JMenu();
        DefineFightsMenu.setIcon(new ImageIcon(Path.returnIconFolder()+"designer.png"));
        ManualFightsMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"manual.png"));
        FightMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"keyboard.png"));
        RingMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"ring.png"));
        DesignerMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"project.png"));
        TournamentPanelMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"panel.png"));
        ListMenu = new javax.swing.JMenu();
        AccreditationMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"acreditation.png"));
        ClubListMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"xpdf.png"));
        TeamListMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"xpdf.png"));
        RefereeListMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"xpdf.png"));
        FightListMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"xpdf.png"));
        PointListMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"xpdf.png"));
        FightsCardMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"xpdf.png"));
        SummaryMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"xpdf.png"));
        DiplomaMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"diploma.png"));
        StatisticsMenu = new javax.swing.JMenu();
        TournamentStatisticsMenu = new javax.swing.JMenu();
        TournamentStatisticsMenu.setIcon(new ImageIcon(Path.returnIconFolder()+"statistics.png"));
        TournamentHitsStatisticsMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"chart.png"));
        TournamentTopTenMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"bars.png"));
        TeamStatisticsMenu = new javax.swing.JMenu();
        TeamStatisticsMenu.setIcon(new ImageIcon(Path.returnIconFolder()+"statistics.png"));
        TeamTopTenMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"bars.png"));
        CompetitorStatisticsMenu = new javax.swing.JMenu();
        CompetitorStatisticsMenu.setIcon(new ImageIcon(Path.returnIconFolder()+"statistics.png"));
        WonLostMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"bars.png"));
        PerformedHitsMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"chart.png"));
        ReceivedHitsMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"chart.png"));
        OptionsMenu = new javax.swing.JMenu();
        LanguageMenu = new javax.swing.JMenu();
        LanguageMenu.setIcon(new ImageIcon(Path.returnIconFolder()+"language.png"));
        LogMenuCheckBox = new javax.swing.JCheckBoxMenuItem();
        LogMenuCheckBox.setIcon(new ImageIcon(Path.returnIconFolder()+"log.png"));
        DebugMenuCheckBox = new javax.swing.JCheckBoxMenuItem();
        DebugMenuCheckBox.setIcon(new ImageIcon(Path.returnIconFolder()+"debug.png"));
        HelpMenu = new javax.swing.JMenu();
        HelpMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"help.png"));
        AboutMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"info.png"));

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

        ConvertDatabaseMenuItem.setText("Convert database");
        DatabaseMenu.add(ConvertDatabaseMenuItem);

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
        ImportMenu.setIcon(new ImageIcon(Path.returnIconFolder()+"import2.png"));

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

        FightsCardMenuItem.setText("FightsCard");
        ListMenu.add(FightsCardMenuItem);

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

        LogMenuCheckBox.setSelected(true);
        LogMenuCheckBox.setText("Log");
        LogMenuCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                LogMenuCheckBoxItemStateChanged(evt);
            }
        });
        OptionsMenu.add(LogMenuCheckBox);

        DebugMenuCheckBox.setSelected(true);
        DebugMenuCheckBox.setText("Debug");
        DebugMenuCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                DebugMenuCheckBoxItemStateChanged(evt);
            }
        });
        OptionsMenu.add(DebugMenuCheckBox);

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
            MessageManager.translatedMessage("databaseDisconnected", "MySQL", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            MessageManager.errorMessage("disconnectDatabaseFail", "MySQL");
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
}//GEN-LAST:event_DatabaseDisconnectMenuItemActionPerformed

    private void ExitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitMenuItemActionPerformed
        this.dispose();
    }//GEN-LAST:event_ExitMenuItemActionPerformed

    private void ImportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportMenuItemActionPerformed
        String file;
        if (!(file = exploreWindowsForKtg(trans.returnTag("ImportSQL"),
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
        if (!(file = exploreWindowsForKtg(trans.returnTag("ExportSQL"),
                JFileChooser.FILES_AND_DIRECTORIES, "KendoTournament_" + date + ".ktg")).equals("")) {
            //KendoTournament.getInstance().database.exportDatabase(file);
            new StoreDatabase().save(file);
        }
}//GEN-LAST:event_ExportMenuItemActionPerformed

private void LogMenuCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_LogMenuCheckBoxItemStateChanged
    if (refresh) {
        KendoTournamentGenerator.getInstance().changeLogOption(LogMenuCheckBox.getState());
    }
}//GEN-LAST:event_LogMenuCheckBoxItemStateChanged

    private void DebugMenuCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DebugMenuCheckBoxItemStateChanged
        if (refresh) {
            KendoTournamentGenerator.getInstance().changeDebugOption(DebugMenuCheckBox.getState());
        }
    }//GEN-LAST:event_DebugMenuCheckBoxItemStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AboutMenuItem;
    private javax.swing.JMenuItem AccreditationMenuItem;
    private javax.swing.JMenuItem ChangeTeamMenuItem;
    private javax.swing.JMenuItem ClubListMenuItem;
    private javax.swing.JMenuItem ClubMenuItem;
    private javax.swing.JMenuItem CompetitorMenuItem;
    private javax.swing.JMenu CompetitorStatisticsMenu;
    private javax.swing.JMenuItem ConvertDatabaseMenuItem;
    private javax.swing.JMenuItem CsvMenuItem;
    private javax.swing.JMenuItem DatabaseConnectMenuItem;
    private javax.swing.JMenuItem DatabaseDisconnectMenuItem;
    private javax.swing.JMenu DatabaseMenu;
    private javax.swing.JMenuItem DatabaseUpdateMenuItem;
    private javax.swing.JCheckBoxMenuItem DebugMenuCheckBox;
    private javax.swing.JMenu DefineFightsMenu;
    private javax.swing.JMenuItem DesignerMenuItem;
    private javax.swing.JMenuItem DiplomaMenuItem;
    private javax.swing.JMenuItem ExitMenuItem;
    private javax.swing.JMenuItem ExportMenuItem;
    private javax.swing.JMenuItem FightListMenuItem;
    private javax.swing.JMenuItem FightMenuItem;
    private javax.swing.JMenuItem FightsCardMenuItem;
    private javax.swing.JMenu HelpMenu;
    private javax.swing.JMenuItem HelpMenuItem;
    private javax.swing.JMenu ImportMenu;
    private javax.swing.JMenuItem ImportMenuItem;
    private javax.swing.JMenu InsertMenu;
    private javax.swing.ButtonGroup LanguageButtonGroup;
    private javax.swing.JMenu LanguageMenu;
    private javax.swing.JMenu ListMenu;
    private javax.swing.JCheckBoxMenuItem LogMenuCheckBox;
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
