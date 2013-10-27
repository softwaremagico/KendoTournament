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
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.gui.base.KendoFrame;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.persistence.ClubPool;
import com.softwaremagico.ktg.persistence.DatabaseConnection;
import com.softwaremagico.ktg.persistence.RegisteredPersonPool;
import com.softwaremagico.ktg.persistence.TournamentPool;
import com.softwaremagico.ktg.tools.Media;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

public final class MainGUI extends KendoFrame {

    Translator trans = null;
    private List<JRadioButtonMenuItem> languageList = new ArrayList<>();
    private boolean refresh = true;

    /**
     * Creates new form MainGUI
     */
    public MainGUI() {
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage();
        setIconImage(new ImageIcon(this.getClass().getResource("/kendo.png")).getImage());
        completeLanguageMenu();
        enableMenuItems();
        setPhoto();
        updateConfig();
        addCloseAction();
    }

    /**
     * Translate the GUI to the selected language.
     */
    public final void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        SaveMenuItem.setText(trans.getTranslatedText("SaveMenuItem"));
        TournamentMenu.setText(trans.getTranslatedText("FileMenu"));
        LanguageMenu.setText(trans.getTranslatedText("LanguageMenu"));
        OptionsMenu.setText(trans.getTranslatedText("OptionsMenu"));
        StatisticsMenu.setText(trans.getTranslatedText("StatisticsMenu"));
        InsertMenu.setText(trans.getTranslatedText("InsertMenu"));
        TournamentMenu.setText(trans.getTranslatedText("TournamentMenu"));
        TournamentPanelMenuItem.setText(trans.getTranslatedText("TournamentPanelMenuItem"));
        HelpMenu.setText(trans.getTranslatedText("HelpMenu"));
        AboutMenuItem.setText(trans.getTranslatedText("AboutMenu"));
        CompetitorMenuItem.setText(trans.getTranslatedText("CompetitorMenuItem"));
        RoleMenuItem.setText(trans.getTranslatedText("RoleMenuItem"));
        TournamentMenuItem.setText(trans.getTranslatedText("TournamentMenuItem"));
        ClubMenuItem.setText(trans.getTranslatedText("ClubMenuItem"));
        TeamMenuItem.setText(trans.getTranslatedText("TeamMenuItem"));
        FightMenuItem.setText(trans.getTranslatedText("FightMenuItem"));
        DatabaseMenu.setText(trans.getTranslatedText("DatabaseMenu"));
        DatabaseConnectMenuItem.setText(trans.getTranslatedText("DatabaseConnectMenuItem"));
        DatabaseDisconnectMenuItem.setText(trans.getTranslatedText("DatabaseDisconnectMenuItem"));
        ListMenu.setText(trans.getTranslatedText("ListMenu"));
        TeamListMenuItem.setText(trans.getTranslatedText("TeamListMenuItem"));
        FightListMenuItem.setText(trans.getTranslatedText("FightListMenuItem"));
        PointListMenuItem.setText(trans.getTranslatedText("PointListMenuItem"));
        DiplomaMenuItem.setText(trans.getTranslatedText("DiplomaMenuItem"));
        CompetitorStatisticsMenu.setText(trans.getTranslatedText("CompetitorStatisticsMenu"));
        PerformedHitsMenuItem.setText(trans.getTranslatedText("PerformedHitsStatisticsMenuItem"));
        ReceivedHitsMenuItem.setText(trans.getTranslatedText("ReceivedHitsStatisticsMenuItem"));
        WonLostMenuItem.setText(trans.getTranslatedText("WonLostMenuItem"));
        TournamentStatisticsMenu.setText(trans.getTranslatedText("TournamentStatisticsMenu"));
        TeamStatisticsMenu.setText(trans.getTranslatedText("TeamStatisticsMenu"));
        TournamentTopTenMenuItem.setText(trans.getTranslatedText("TournamentTopTenMenuItem"));
        TournamentHitsStatisticsMenuItem.setText(trans.getTranslatedText("TournamentHitsStatisticsMenuItem"));
        DefineFightsMenu.setText(trans.getTranslatedText("DefineFightsMenu"));
        ProgramMenu.setText(trans.getTranslatedText("ProgramMenu"));
        ExitMenuItem.setText(trans.getTranslatedText("ExitMenuItem"));
        RingMenuItem.setText(trans.getTranslatedText("RingMenuItem"));
        DesignerMenuItem.setText(trans.getTranslatedText("ChampionshipLabel"));
        TeamTopTenMenuItem.setText(trans.getTranslatedText("TeamTopTenMenuItem"));
        AccreditationMenuItem.setText(trans.getTranslatedText("AccreditationMenuItem"));
        HelpMenuItem.setText(trans.getTranslatedText("HelpMenuItem"));
        SummaryMenuItem.setText(trans.getTranslatedText("SummaryMenuItem"));
        ClubListMenuItem.setText(trans.getTranslatedText("ClubListMenuItem"));
        RefereeListMenuItem.setText(trans.getTranslatedText("RefereeListMenuItem"));
        ScoreMenuItem.setText(trans.getTranslatedText("ScoreMenuItem"));
        LogMenuCheckBox.setText(trans.getTranslatedText("LogOption"));
        DebugMenuCheckBox.setText(trans.getTranslatedText("DebugOption"));
        ConvertDatabaseMenuItem.setText(trans.getTranslatedText("ConvertDatabase"));
        FightsCardMenuItem.setText(trans.getTranslatedText("FightsCard"));
        AutosaveCheckBox.setText(trans.getTranslatedText("AutosaveMenuItem"));
        TournamentTreeMenuItem.setText(trans.getTranslatedText("TreeButton"));
        CompetitorsGlobalScoreMenuItem.setText(trans.getTranslatedText("GeneralClassification"));
    }

    private void setPhoto() {
        setPhoto(Path.getMainPhoto());
    }

    private void setPhoto(String path) {
        setPhoto(Media.getImageFitted(path, MainPhotoPanel));
    }

    private void setPhoto(BufferedImage image) {
        if (image != null) {
            JLabel picLabel = new JLabel(new ImageIcon(image));
            MainPhotoPanel.removeAll();
            MainPhotoPanel.add(picLabel, 0);
            MainPhotoPanel.revalidate();
            MainPhotoPanel.repaint();
        }
    }

    /**
     * Generates the Menu items depending on the lenguages available on the XML
     * files
     */
    private void completeLanguageMenu() {
        for (int i = 0; i < Translator.getAvailableLanguages().size(); i++) {
            javax.swing.JRadioButtonMenuItem MenuItem;
            MenuItem = new javax.swing.JRadioButtonMenuItem(Translator.getAvailableLanguages().get(i).getName(),
                    new ImageIcon(Path.getImagePath() + Translator.getAvailableLanguages().get(i).getFlag()));
            if (KendoTournamentGenerator.getInstance().getLanguage()
                    .equals(Translator.getAvailableLanguages().get(i).getAbbreviature())) {
                MenuItem.setSelected(true);
            }
            LanguageMenu.add(MenuItem);
            LanguageButtonGroup.add(MenuItem);
            languageList.add(MenuItem);
        }
    }

    public String getSelectedLanguage() {
        for (int i = 0; i < languageList.size(); i++) {
            if (languageList.get(i).isSelected()) {
                return Translator.getAvailableLanguages().get(i).getAbbreviature();
            }
        }
        return KendoTournamentGenerator.getInstance().getLanguage();
    }

    /**
     * It is called from Controller performConnection();
     */
    public void enableMenuItems() {
        boolean connected;
        connected = DatabaseConnection.getInstance().isDatabaseConnectionTested();
        DatabaseConnectMenuItem.setEnabled(!connected);
        DatabaseDisconnectMenuItem.setEnabled(connected);
        InsertMenu.setEnabled(connected);

        boolean competitorCheck = connected && !ClubPool.getInstance().isEmpty();
        CompetitorMenuItem.setEnabled(competitorCheck);

        boolean tournamentCheck = competitorCheck && !RegisteredPersonPool.getInstance().isEmpty();
        TournamentMenuItem.setEnabled(tournamentCheck);

        boolean existTournaments = tournamentCheck && !TournamentPool.getInstance().isEmpty();
        TournamentMenu.setEnabled(existTournaments);
        ListMenu.setEnabled(existTournaments);
        StatisticsMenu.setEnabled(existTournaments);
    }

    @Override
    public String defaultFileName() {
        return "";
    }

    private void updateConfig() {
        refresh = false;
        LogMenuCheckBox.setState(KendoTournamentGenerator.getInstance().getLogOption());
        DebugMenuCheckBox.setState(KendoTournamentGenerator.isDebugOptionSelected());
        refresh = true;
        AutosaveCheckBox.setState(KendoTournamentGenerator.isAutosaveOptionSelected());
    }

    private void close() {
        // No data to store. Exit.
        if (!DatabaseConnection.getInstance().needsToBeStoredInDatabase()) {
            System.exit(0);
        }
        // Data not stored. Ask the user.
        int confirmed = JOptionPane.showConfirmDialog(null, LanguagePool.getTranslator("messages.xml")
                .getTranslatedText("saveBeforeExit"), "Exit", JOptionPane.YES_NO_CANCEL_OPTION);
        if (confirmed == JOptionPane.YES_OPTION) {
            if (DatabaseConnection.getInstance().isDatabaseConnectionTested()) {
                try {
                    DatabaseConnection.getInstance().updateDatabase();
                } catch (SQLException ex) {
                    AlertManager.showSqlErrorMessage(ex);
                }
            }
            System.exit(0);
        } else if (confirmed == JOptionPane.NO_OPTION) {
            System.exit(0);
        } else {
            // Do nothing.
        }
    }

    private void addCloseAction() {

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
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

    public void addAccreditionCardMenuItemListener(ActionListener al) {
        AccreditationMenuItem.addActionListener(al);
    }

    public void addHelpMenuItemListener(ActionListener al) {
        HelpMenuItem.addActionListener(al);
    }

    public void addScoreMenuItemListener(ActionListener al) {
        ScoreMenuItem.addActionListener(al);
    }

    public void addConvertDatabaseMenuItemListener(ActionListener al) {
        ConvertDatabaseMenuItem.addActionListener(al);
    }

    public void addFightsCardMenuItemListener(ActionListener al) {
        FightsCardMenuItem.addActionListener(al);
    }

    public void addSaveMenuItemListener(ActionListener al) {
        SaveMenuItem.addActionListener(al);
    }

    public void addTreeOptionMenuItemListener(ActionListener al) {
        TournamentTreeMenuItem.addActionListener(al);
    }

    public void addCompetitorsGlobalScoreMenuItemListener(ActionListener al) {
        CompetitorsGlobalScoreMenuItem.addActionListener(al);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed"
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
        ConvertDatabaseMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"convertDatabase.png"));
        SaveMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"save.png"));
        ExitMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"exit.png"));
        InsertMenu = new javax.swing.JMenu();
        ClubMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"club.png"));
        CompetitorMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"user.png"));
        TournamentMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"highscores.png"));
        TournamentMenu = new javax.swing.JMenu();
        ScoreMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"score.png"));
        RoleMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"photo.png"));
        TeamMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"team.png"));
        DefineFightsMenu = new javax.swing.JMenu();
        DefineFightsMenu.setIcon(new ImageIcon(Path.returnIconFolder()+"designer.png"));
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
        TournamentTreeMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"tree.png"));
        CompetitorsGlobalScoreMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"xpdf.png"));
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
        AutosaveCheckBox = new javax.swing.JCheckBoxMenuItem();
        AutosaveCheckBox.setIcon(new ImageIcon(Path.returnIconFolder()+"session-save.png"));
        LogMenuCheckBox = new javax.swing.JCheckBoxMenuItem();
        LogMenuCheckBox.setIcon(new ImageIcon(Path.returnIconFolder()+"log.png"));
        DebugMenuCheckBox = new javax.swing.JCheckBoxMenuItem();
        DebugMenuCheckBox.setIcon(new ImageIcon(Path.returnIconFolder()+"debug.png"));
        HelpMenu = new javax.swing.JMenu();
        HelpMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"help.png"));
        AboutMenuItem = new javax.swing.JMenuItem(new ImageIcon(Path.returnIconFolder()+"info.png"));

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Kendo Tournament Manager");
        setName("mainGui"); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                WindowClose(evt);
            }
        });

        MainPhotoPanel.setBorder(null);
        MainPhotoPanel.setForeground(new java.awt.Color(5, 0, 0));
        MainPhotoPanel.setOpaque(false);
        MainPhotoPanel.setLayout(new java.awt.BorderLayout());

        MainMenuBar.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N

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

        ConvertDatabaseMenuItem.setText("Convert database");
        DatabaseMenu.add(ConvertDatabaseMenuItem);

        ProgramMenu.add(DatabaseMenu);

        SaveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        SaveMenuItem.setText("Save");
        ProgramMenu.add(SaveMenuItem);

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

        DefineFightsMenu.setText("Define Fights");

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

        TournamentTreeMenuItem.setText("TournamentTree");
        ListMenu.add(TournamentTreeMenuItem);

        CompetitorsGlobalScoreMenuItem.setText("CompetitorsGlobalScore");
        ListMenu.add(CompetitorsGlobalScoreMenuItem);

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

        AutosaveCheckBox.setSelected(true);
        AutosaveCheckBox.setText("Autosave");
        AutosaveCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                AutosaveCheckBoxStateChanged(evt);
            }
        });
        OptionsMenu.add(AutosaveCheckBox);

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
                .addComponent(MainPhotoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 524, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MainPhotoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void DatabaseDisconnectMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_DatabaseDisconnectMenuItemActionPerformed
        DatabaseConnection.getInstance().disconnect();
        //Delete memory information.
        DatabaseConnection.getInstance().resetDatabase();
        DatabaseConnection.getInstance().setPassword("");
        enableMenuItems();
        AlertManager.translatedMessage(this.getClass().getName(), "databaseDisconnected", "MySQL",
                JOptionPane.INFORMATION_MESSAGE);
    }// GEN-LAST:event_DatabaseDisconnectMenuItemActionPerformed

    private void ExitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_ExitMenuItemActionPerformed
        close();
    }// GEN-LAST:event_ExitMenuItemActionPerformed

    private void LogMenuCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_LogMenuCheckBoxItemStateChanged
        if (refresh) {
            KendoTournamentGenerator.getInstance().setLogOption(LogMenuCheckBox.getState());
        }
    }// GEN-LAST:event_LogMenuCheckBoxItemStateChanged

    private void DebugMenuCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {// GEN-FIRST:event_DebugMenuCheckBoxItemStateChanged
        if (refresh) {
            KendoTournamentGenerator.getInstance().setDebugOption(DebugMenuCheckBox.getState());
        }
    }// GEN-LAST:event_DebugMenuCheckBoxItemStateChanged

    private void AutosaveCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_AutosaveCheckBoxStateChanged
        KendoTournamentGenerator.getInstance().setAutosaveOption(AutosaveCheckBox.getState());
    }// GEN-LAST:event_AutosaveCheckBoxStateChanged

    private void WindowClose(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_WindowClose
        close();
    }// GEN-LAST:event_WindowClose
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AboutMenuItem;
    private javax.swing.JMenuItem AccreditationMenuItem;
    private javax.swing.JCheckBoxMenuItem AutosaveCheckBox;
    private javax.swing.JMenuItem ClubListMenuItem;
    private javax.swing.JMenuItem ClubMenuItem;
    private javax.swing.JMenuItem CompetitorMenuItem;
    private javax.swing.JMenu CompetitorStatisticsMenu;
    private javax.swing.JMenuItem CompetitorsGlobalScoreMenuItem;
    private javax.swing.JMenuItem ConvertDatabaseMenuItem;
    private javax.swing.JMenuItem DatabaseConnectMenuItem;
    private javax.swing.JMenuItem DatabaseDisconnectMenuItem;
    private javax.swing.JMenu DatabaseMenu;
    private javax.swing.JCheckBoxMenuItem DebugMenuCheckBox;
    private javax.swing.JMenu DefineFightsMenu;
    private javax.swing.JMenuItem DesignerMenuItem;
    private javax.swing.JMenuItem DiplomaMenuItem;
    private javax.swing.JMenuItem ExitMenuItem;
    private javax.swing.JMenuItem FightListMenuItem;
    private javax.swing.JMenuItem FightMenuItem;
    private javax.swing.JMenuItem FightsCardMenuItem;
    private javax.swing.JMenu HelpMenu;
    private javax.swing.JMenuItem HelpMenuItem;
    private javax.swing.JMenu InsertMenu;
    private javax.swing.ButtonGroup LanguageButtonGroup;
    private javax.swing.JMenu LanguageMenu;
    private javax.swing.JMenu ListMenu;
    private javax.swing.JCheckBoxMenuItem LogMenuCheckBox;
    private javax.swing.JMenuBar MainMenuBar;
    private javax.swing.JPanel MainPhotoPanel;
    private javax.swing.JMenu OptionsMenu;
    private javax.swing.JMenuItem PerformedHitsMenuItem;
    private javax.swing.JMenuItem PointListMenuItem;
    private javax.swing.JMenu ProgramMenu;
    private javax.swing.JMenuItem ReceivedHitsMenuItem;
    private javax.swing.JMenuItem RefereeListMenuItem;
    private javax.swing.JMenuItem RingMenuItem;
    private javax.swing.JMenuItem RoleMenuItem;
    private javax.swing.JMenuItem SaveMenuItem;
    private javax.swing.JMenuItem ScoreMenuItem;
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
    private javax.swing.JMenuItem TournamentTreeMenuItem;
    private javax.swing.JMenuItem WonLostMenuItem;
    // End of variables declaration//GEN-END:variables
}
