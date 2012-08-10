package com.softwaremagico.ktg.championship;
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

import com.softwaremagico.ktg.*;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JViewport;
import javax.swing.Timer;

/**
 *
 * @author jorge
 */
public class LeagueDesigner extends javax.swing.JFrame {

    private DefaultListModel<String> teamModel = new DefaultListModel<>();
    private Translator trans = null;
    List<Team> teams;
    Tournament championship;
    private List<Tournament> listTournaments = new ArrayList<>();
    private boolean refreshTournament = true;
    private boolean refreshMode = true;
    Integer numberMaxOfWinners = 1;
    Timer timer;
    private boolean wasDoubleClick = true;
    private BlackBoardPanel bbp;
    //private final String FOLDER = "designer";
    private Point p;
    private JViewport viewport;
    private boolean refreshSpinner = true;

    /**
     * Creates new form LeagueDesigner
     */
    public LeagueDesigner() {
        try {
            refreshMode = false;
            initComponents();
            setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                    (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
            setLanguage();
            bbp = new BlackBoardPanel();
            BlackBoardScrollPane.setViewportView(bbp);
            PassSpinner.setValue(numberMaxOfWinners);
            fillTournaments();
            updateInterface();
            updateLevel();

            if (KendoTournamentGenerator.getInstance().tournamentManager.size() == 0) {
                addDesignedPanelFirstLevel();
            }
            updateMode();
            refreshMode = true;
        } catch (NullPointerException npe) {
        }
    }

    private void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        this.setTitle(trans.returnTag("titleLeagueDesigner"));
        TournamentLabel.setText(trans.returnTag("TournamentLabel"));
        PassLabel.setText(trans.returnTag("PassLabel"));
        AddButton.setText(trans.returnTag("AddGroupButton"));
        DeleteButton.setText(trans.returnTag("DeleteGroupButton"));
        DeleteTeamsButton.setText(trans.returnTag("CleanButton"));
        DeleteAllButton.setText(trans.returnTag("CleanAllButton"));
        AddTeamButton.setText(trans.returnTag("AddTeamButton"));
        AcceptButton.setText(trans.returnTag("GenerateMatchButton"));
        CloseButton.setText(trans.returnTag("CloseButton"));
        ChampionshipRadioButton.setText(trans.returnTag("ChampionshipRadioButton"));
        TreeRadioButton.setText(trans.returnTag("TreeRadioButton"));
        SimpleRadioButton.setText(trans.returnTag("SimpleRadioButton"));
        CleanLinksButton.setText(trans.returnTag("CleanLinks"));
        DeleteLevelLabel.setText(trans.returnTag("DeleteLevelLabel"));
        DeleteLevelButton.setText(trans.returnTag("DeleteButton"));
        LoadButton.setText(trans.returnTag("ButtonLoadTournament"));
        TreeEditionLabel.setText(trans.returnTag("TournamentLabel"));
        GroupEditionLabel.setText(trans.returnTag("GroupLabel"));
    }

    private void fillTournaments() {
        refreshTournament = false;
        try {
            listTournaments = KendoTournamentGenerator.getInstance().database.getAllTournaments();
            for (int i = 0; i < listTournaments.size(); i++) {
                TournamentComboBox.addItem(listTournaments.get(i).name);
            }
        } catch (NullPointerException npe) {
        }
        TournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
        refreshTournament = true;
    }

    /**
     * Fill the list with the results obtained
     */
    public void fillTeams() {
        try {
            teamModel.removeAllElements();
            for (int i = 0; i < teams.size(); i++) {
                if (!KendoTournamentGenerator.getInstance().tournamentManager.isTeamContainedInTournament(teams.get(i), TournamentComboBox.getSelectedItem().toString())) {
                    teamModel.addElement(teams.get(i).returnName());
                }
            }
            if (teamModel.size() > 0) {
                TeamList.setSelectedIndex(0);
            }
        } catch (NullPointerException npe) {
        }
    }

    private void updateTeams() {
        int index = TeamList.getSelectedIndex();
        fillTeams();
        if (index < teamModel.size() && index >= 0) {
            TeamList.setSelectedIndex(index);
        } else {
            index--;
            if (index >= 0) {
                TeamList.setSelectedIndex(index);
            }
        }
    }

    private void addDesignedPanelFirstLevel() {
        try {
            if (KendoTournamentGenerator.getInstance().tournamentManager.sizeOfTournamentLevelZero(TournamentComboBox.getSelectedItem().toString()) < obtainNumberOfGroupsOfLeague()) {
                //int defaultArena = (KendoTournamentGenerator.getInstance().tournamentManager.returnGroupsOfLevel(0).size()) / tournament.fightingAreas;
                int defaultArena = 0;
                TournamentGroup designedFight = new TournamentGroup(numberMaxOfWinners, championship, 0, defaultArena);
                designedFight.addMouseClickListener(new MouseAdapters(designedFight));
                KendoTournamentGenerator.getInstance().tournamentManager.add(designedFight, true);
                designedFight.setSelected(KendoTournamentGenerator.getInstance().tournamentManager);
                KendoTournamentGenerator.getInstance().tournamentManager.updateArenas(0);
                updateBlackBoard();
                updateDesignerGroups();
                updateListeners();
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
    }

    private void addTeamToSelectedPanel(Team t) {
        for (int i = 0; i < KendoTournamentGenerator.getInstance().tournamentManager.size(); i++) {
            if (KendoTournamentGenerator.getInstance().tournamentManager.get(i).isSelected()) {
                KendoTournamentGenerator.getInstance().tournamentManager.get(i).addTeam(t);
                break;
            }
        }
        updateTeams();
        updateDesignerGroups();
    }

    private void removeSelectedPanel() {
        try {
            TournamentGroup group = KendoTournamentGenerator.getInstance().tournamentManager.getLastGroupSelected();
            Integer select = KendoTournamentGenerator.getInstance().tournamentManager.getIndexLastSelected();
            KendoTournamentGenerator.getInstance().tournamentManager.removeGroupOfLevelZero(group);
            if ((select == null) || (select < 0) || (select > KendoTournamentGenerator.getInstance().tournamentManager.getSizeOfLevel(0) - 1)) {
                //Select the last group.
                KendoTournamentGenerator.getInstance().tournamentManager.selectLastGroup();
            } else {
                //Select the previous one. 
                KendoTournamentGenerator.getInstance().tournamentManager.selectGroup(select);
            }
            updateTeams();
            updateBlackBoard();
            updateDesignerGroups();
        } catch (NullPointerException npe) {
        }
    }

    private void DeleteTeamsOfSelectedPanel() {
        try {
            for (int i = 0; i < KendoTournamentGenerator.getInstance().tournamentManager.size(); i++) {
                if (KendoTournamentGenerator.getInstance().tournamentManager.get(i).isSelected()) {
                    KendoTournamentGenerator.getInstance().tournamentManager.get(i).deleteTeams();
                }
            }
            updateTeams();
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        updateBlackBoard();
    }

    private Team returnTeamByName(String name) {
        try {
            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).returnName().equals(name)) {
                    return teams.get(i);
                }

            }
        } catch (NullPointerException npe) {
        }
        return null;
    }

    private Team returnSelectedTeam() {
        String name = (String) TeamList.getSelectedValue();
        //int index = TeamList.getSelectedIndex();
        return returnTeamByName(name);
    }

    private void addTeam() {
        Team t = returnSelectedTeam();
        if (t != null) {
            addTeamToSelectedPanel(returnSelectedTeam());
        }
    }

    private void updateDesignerGroups() {
        KendoTournamentGenerator.getInstance().tournamentManager.update();
    }

    public int obtainNumberOfGroupsOfLeague() {
        return teams.size() / 2;
    }

    public int obtainMaxNumberOfTeamsByGroup() {
        try {
            return (int) Math.min(Math.floor((float) teams.size() / KendoTournamentGenerator.getInstance().tournamentManager.getSizeOfLevel(0)), 4);
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    private void updateInterface() {
        try {
            championship = KendoTournamentGenerator.getInstance().database.getTournamentByName(TournamentComboBox.getSelectedItem().toString(), false);
            KendoTournamentGenerator.getInstance().tournamentManager = new TournamentGroupManager(championship);
            teams = KendoTournamentGenerator.getInstance().database.searchTeamsByTournamentExactName(TournamentComboBox.getSelectedItem().toString(), false);

            if (!championship.mode.equals(TournamentTypes.SIMPLE)) {
                //If it is not stored in a file, generate it. 
                KendoTournamentGenerator.getInstance().tournamentManager.refillDesigner(KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(championship.name));
            }

            KendoTournamentGenerator.getInstance().tournamentManager.setMode(championship.mode);

            fillTeams();
            updateListeners();
            updateBlackBoard();
            updateRadioButton();
        } catch (NullPointerException npe) {
        }
    }

    void updateBlackBoard() {
        try {
            Integer select = KendoTournamentGenerator.getInstance().tournamentManager.getIndexLastSelected();
            if (!KendoTournamentGenerator.getInstance().tournamentManager.getMode().equals(TournamentTypes.SIMPLE)) {
                bbp.updateBlackBoard(TournamentComboBox.getSelectedItem().toString(), false);
                KendoTournamentGenerator.getInstance().tournamentManager.enhance(false);
            } else {
                bbp.clearBlackBoard();
            }
            if (select != null && select >= 0) {
                KendoTournamentGenerator.getInstance().tournamentManager.selectGroup(select);
            }

            BlackBoardScrollPane.revalidate();
            BlackBoardScrollPane.repaint();
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
    }

    private void consistentTree() {
        try {
            //It is impossible in a tree league that are more than one winner. If it is, change to a tournament.
            if (KendoTournamentGenerator.getInstance().tournamentManager.default_max_winners > 1 && KendoTournamentGenerator.getInstance().tournamentManager.getMode().equals(TournamentTypes.LEAGUE_TREE)) {
                ChampionshipRadioButton.setSelected(true);
                KendoTournamentGenerator.getInstance().tournamentManager.setMode(TournamentTypes.CHAMPIONSHIP);
                championship.mode = KendoTournamentGenerator.getInstance().tournamentManager.getMode();
                updateBlackBoard();
            }
        } catch (NullPointerException npe) {
        }
    }

    private void updateRadioButton() {
        switch (KendoTournamentGenerator.getInstance().tournamentManager.getMode()) {
            case CHAMPIONSHIP:
                ChampionshipRadioButton.setSelected(true);
                break;
            case MANUAL:
                ManualRadioButton.setSelected(true);
                break;
            case LEAGUE_TREE:
                TreeRadioButton.setSelected(true);
                break;
            case SIMPLE:
                SimpleRadioButton.setSelected(true);
                break;
        }
    }

    private void enableSpinner() {
        refreshSpinner = false;
        switch (KendoTournamentGenerator.getInstance().tournamentManager.getMode()) {
            case LEAGUE_TREE:
            case SIMPLE:
                PassSpinner.setValue(1);
                PassSpinner.setEnabled(false);
                break;
            case CHAMPIONSHIP:
                PassSpinner.setValue(2);
                PassSpinner.setEnabled(false);
                break;
            case MANUAL:
                PassSpinner.setEnabled(true);
                break;
        }
        refreshSpinner = true;
    }

    private void updateMode() {
        try {
            TournamentTypes oldMode = KendoTournamentGenerator.getInstance().tournamentManager.getMode();
            if (ManualRadioButton.isSelected()) {
                KendoTournamentGenerator.getInstance().tournamentManager.setMode(TournamentTypes.MANUAL);
                championship.mode = TournamentTypes.MANUAL;
                CleanLinksButton.setVisible(true);
            } else if (ChampionshipRadioButton.isSelected()) {
                KendoTournamentGenerator.getInstance().tournamentManager.setMode(TournamentTypes.CHAMPIONSHIP);
                championship.mode = TournamentTypes.CHAMPIONSHIP;
                CleanLinksButton.setVisible(false);
            } else if (TreeRadioButton.isSelected()) {
                KendoTournamentGenerator.getInstance().tournamentManager.setMode(TournamentTypes.LEAGUE_TREE);
                championship.mode = TournamentTypes.LEAGUE_TREE;
                CleanLinksButton.setVisible(false);
            } else if (SimpleRadioButton.isSelected()) {
                KendoTournamentGenerator.getInstance().tournamentManager.setMode(TournamentTypes.SIMPLE);
                championship.mode = TournamentTypes.SIMPLE;
                CleanLinksButton.setVisible(false);
            }

            //Mode has changed. Update Levels
            if (!oldMode.equals(KendoTournamentGenerator.getInstance().tournamentManager.getMode()) && KendoTournamentGenerator.getInstance().tournamentManager.getLevels().size() > 0) {
                LeagueLevel levelZero = KendoTournamentGenerator.getInstance().tournamentManager.getLevels().get(0);
                KendoTournamentGenerator.getInstance().tournamentManager.convertFirstLevelsToCurrentChampionship(levelZero);
            }

            enableSpinner();

            if (KendoTournamentGenerator.getInstance().tournamentManager.getMode().equals(TournamentTypes.SIMPLE)) {
                AddButton.setVisible(false);
                DeleteButton.setVisible(false);
                DeleteAllButton.setVisible(false);
                DeleteTeamsButton.setVisible(false);
                AddTeamButton.setVisible(false);
                AcceptButton.setVisible(false);
                bbp.clearBlackBoard();
            } else {
                AddButton.setVisible(true);
                DeleteButton.setVisible(true);
                DeleteAllButton.setVisible(true);
                DeleteTeamsButton.setVisible(true);
                AddTeamButton.setVisible(true);
                AcceptButton.setVisible(true);
            }
            if (refreshMode) {
                //Update tournament.
                KendoTournamentGenerator.getInstance().database.updateTournament(championship, false);
            }
            //KendoTournamentGenerator.getInstance().tournamentManager.updateInnerLevels();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }

        updateBlackBoard();
    }

    private void updateLevel() {
        LevelComboBox.removeAllItems();
        for (int i = 0; i < KendoTournamentGenerator.getInstance().tournamentManager.getLevels().size(); i++) {
            if (i < KendoTournamentGenerator.getInstance().tournamentManager.getLevels().size() - 2) {
                LevelComboBox.addItem(trans.returnTag("Round") + " " + (KendoTournamentGenerator.getInstance().tournamentManager.getLevels().size() - i));
            } else if (i == KendoTournamentGenerator.getInstance().tournamentManager.getLevels().size() - 2) {
                LevelComboBox.addItem(trans.returnTag("SemiFinalLabel"));
            } else {
                LevelComboBox.addItem(trans.returnTag("FinalLabel"));
            }
        }
        LevelComboBox.addItem(trans.returnTag("All"));
    }

    private void focus(int x, int y) {
        p = new Point();
        Dimension dv = viewport.getViewSize();
        Dimension de = viewport.getExtentSize();


        int columnsWide = this.getWidth() / KendoTournamentGenerator.getInstance().tournamentManager.getLevels().size();
        int rowsWide;
        if (KendoTournamentGenerator.getInstance().tournamentManager.returnGroupsOfLevel(0).size() > 0) {
            rowsWide = this.getWidth() / KendoTournamentGenerator.getInstance().tournamentManager.returnGroupsOfLevel(0).size();
        } else {
            rowsWide = 1;
        }

        p.x = columnsWide * x;
        p.y = rowsWide * y;

        viewport.setViewPosition(p);
    }

    /**
     * **********************************************
     *
     * LISTENERS
     *
     ***********************************************
     */
    /**
     * When clicking to a box.
     */
    class MouseAdapters extends MouseAdapter {

        TournamentGroup designedFight;

        MouseAdapters(TournamentGroup d) {
            designedFight = d;
        }

        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            clicked(evt, designedFight);
        }
    }

    void clicked(java.awt.event.MouseEvent e, TournamentGroup group) {
        final boolean selected = group.isSelected();

        if (group.getLevel() == 0) {
            if (e.getClickCount() == 2) {
                group.openDesignGroupWindow(this);
                wasDoubleClick = true;
            } else {
                //Avoid to run the one-click functions when performing a doubleclick.
                Integer timerinterval = (Integer) Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
                timer = new Timer(timerinterval.intValue(), new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        if (wasDoubleClick) {
                            wasDoubleClick = false; // reset flag
                        } else {
                            //If is a group already selected, add the selected team.
                            if (selected) {
                                //addTeam();
                            }

                        }
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }

            group.setSelected(KendoTournamentGenerator.getInstance().tournamentManager);
        } else if (group.getLevel() == 1 && group.teams.isEmpty()) {
            //Clicking in the second level is only useful for defining links and the championship has not started. 
            if (KendoTournamentGenerator.getInstance().tournamentManager.getMode().equals(TournamentTypes.MANUAL)) {
                KendoTournamentGenerator.getInstance().tournamentManager.addLink(KendoTournamentGenerator.getInstance().tournamentManager.getLastGroupSelected(), group);
                updateBlackBoard();
            }
        }
        updateListeners();
    }

    private void updateListeners() {
        for (int i = 0; i < KendoTournamentGenerator.getInstance().tournamentManager.size(); i++) {
            if (!KendoTournamentGenerator.getInstance().tournamentManager.get(i).listenerAdded) {
                KendoTournamentGenerator.getInstance().tournamentManager.get(i).addMouseClickListener(new MouseAdapters(KendoTournamentGenerator.getInstance().tournamentManager.get(i)));
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TreeChampionshipButtonGroup = new javax.swing.ButtonGroup();
        ChampionshipPanel = new javax.swing.JPanel();
        ChampionshipRadioButton = new javax.swing.JRadioButton();
        TreeRadioButton = new javax.swing.JRadioButton();
        ManualRadioButton = new javax.swing.JRadioButton();
        SimpleRadioButton = new javax.swing.JRadioButton();
        TournamentLabel = new javax.swing.JLabel();
        TournamentComboBox = new javax.swing.JComboBox<String>();
        jPanel1 = new javax.swing.JPanel();
        BlackBoardScrollPane = new javax.swing.JScrollPane();
        DeleteLevelLabel = new javax.swing.JLabel();
        LevelComboBox = new javax.swing.JComboBox<String>();
        DeleteLevelButton = new javax.swing.JButton();
        AddButton = new javax.swing.JButton();
        DeleteButton = new javax.swing.JButton();
        DeleteAllButton = new javax.swing.JButton();
        TreeEditionLabel = new javax.swing.JLabel();
        PassLabel = new javax.swing.JLabel();
        PassSpinner = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        TeamScrollPane = new javax.swing.JScrollPane();
        TeamList = new javax.swing.JList<String>();
        AddTeamButton = new javax.swing.JButton();
        DeleteTeamsButton = new javax.swing.JButton();
        CleanLinksButton = new javax.swing.JButton();
        GroupEditionLabel = new javax.swing.JLabel();
        ButtonPanel = new javax.swing.JPanel();
        AcceptButton = new javax.swing.JButton();
        CloseButton = new javax.swing.JButton();
        LoadButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 600));
        setPreferredSize(new java.awt.Dimension(800, 600));
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        TreeChampionshipButtonGroup.add(ChampionshipRadioButton);
        ChampionshipRadioButton.setText("Championship");
        ChampionshipRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ChampionshipRadioButtonItemStateChanged(evt);
            }
        });

        TreeChampionshipButtonGroup.add(TreeRadioButton);
        TreeRadioButton.setSelected(true);
        TreeRadioButton.setText("Tree");
        TreeRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                TreeRadioButtonItemStateChanged(evt);
            }
        });

        TreeChampionshipButtonGroup.add(ManualRadioButton);
        ManualRadioButton.setText("Manual");
        ManualRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ManualRadioButtonItemStateChanged(evt);
            }
        });

        TreeChampionshipButtonGroup.add(SimpleRadioButton);
        SimpleRadioButton.setText("Simple");
        SimpleRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                SimpleRadioButtonItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout ChampionshipPanelLayout = new javax.swing.GroupLayout(ChampionshipPanel);
        ChampionshipPanel.setLayout(ChampionshipPanelLayout);
        ChampionshipPanelLayout.setHorizontalGroup(
            ChampionshipPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ChampionshipPanelLayout.createSequentialGroup()
                .addComponent(SimpleRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ManualRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ChampionshipRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TreeRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                .addContainerGap())
        );
        ChampionshipPanelLayout.setVerticalGroup(
            ChampionshipPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ChampionshipPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(SimpleRadioButton)
                .addComponent(ManualRadioButton)
                .addComponent(ChampionshipRadioButton)
                .addComponent(TreeRadioButton))
        );

        TournamentLabel.setText("Tournament:");

        TournamentComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TournamentComboBoxActionPerformed(evt);
            }
        });
        TournamentComboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                TournamentComboBoxFocusGained(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        BlackBoardScrollPane.setBackground(new java.awt.Color(255, 255, 255));

        DeleteLevelLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        DeleteLevelLabel.setText("Delete Level:");

        DeleteLevelButton.setText("Delete");
        DeleteLevelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteLevelButtonActionPerformed(evt);
            }
        });

        AddButton.setText("Add");
        AddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddButtonActionPerformed(evt);
            }
        });

        DeleteButton.setText("Delete");
        DeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteButtonActionPerformed(evt);
            }
        });

        DeleteAllButton.setText("Delete All");
        DeleteAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteAllButtonActionPerformed(evt);
            }
        });

        TreeEditionLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        TreeEditionLabel.setText("Tree Edition:");

        PassLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        PassLabel.setText("Pass:");

        PassSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 3, 1));
        PassSpinner.setEnabled(false);
        PassSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                PassSpinnerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(TreeEditionLabel)
                            .addComponent(AddButton, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                            .addComponent(DeleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                            .addComponent(DeleteAllButton, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(DeleteLevelLabel)
                            .addComponent(LevelComboBox, 0, 149, Short.MAX_VALUE)
                            .addComponent(DeleteLevelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(PassLabel)
                        .addComponent(PassSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BlackBoardScrollPane)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AddButton, DeleteAllButton, DeleteButton, DeleteLevelButton, LevelComboBox, PassSpinner});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(TreeEditionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(AddButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DeleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DeleteAllButton)
                        .addGap(18, 18, 18)
                        .addComponent(DeleteLevelLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LevelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DeleteLevelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(PassLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PassSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(BlackBoardScrollPane))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {AddButton, DeleteAllButton, DeleteButton, DeleteLevelButton, LevelComboBox});

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        TeamList.setModel(teamModel);
        TeamList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TeamListMouseClicked(evt);
            }
        });
        TeamScrollPane.setViewportView(TeamList);

        AddTeamButton.setText("Add Team");
        AddTeamButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddTeamButtonActionPerformed(evt);
            }
        });

        DeleteTeamsButton.setText("Clear");
        DeleteTeamsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteTeamsButtonActionPerformed(evt);
            }
        });

        CleanLinksButton.setText("Clear Links");
        CleanLinksButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CleanLinksButtonActionPerformed(evt);
            }
        });

        GroupEditionLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        GroupEditionLabel.setText("Group Edition:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(GroupEditionLabel)
                    .addComponent(AddTeamButton, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DeleteTeamsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CleanLinksButton, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TeamScrollPane)
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AddTeamButton, CleanLinksButton, DeleteTeamsButton});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(GroupEditionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AddTeamButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DeleteTeamsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CleanLinksButton))
                    .addComponent(TeamScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {AddTeamButton, CleanLinksButton, DeleteTeamsButton});

        AcceptButton.setText("Accept");
        AcceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcceptButtonActionPerformed(evt);
            }
        });

        CloseButton.setText("Close");
        CloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseButtonActionPerformed(evt);
            }
        });

        LoadButton.setText("Load");
        LoadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LoadButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ButtonPanelLayout = new javax.swing.GroupLayout(ButtonPanel);
        ButtonPanel.setLayout(ButtonPanelLayout);
        ButtonPanelLayout.setHorizontalGroup(
            ButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ButtonPanelLayout.createSequentialGroup()
                .addComponent(LoadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(AcceptButton, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CloseButton))
        );

        ButtonPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AcceptButton, CloseButton});

        ButtonPanelLayout.setVerticalGroup(
            ButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LoadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(ButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(AcceptButton)
                .addComponent(CloseButton))
        );

        ButtonPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {AcceptButton, CloseButton, LoadButton});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ButtonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(TournamentLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TournamentComboBox, 0, 331, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ChampionshipPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(TournamentLabel)
                        .addComponent(TournamentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ChampionshipPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void TournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TournamentComboBoxActionPerformed
        if (refreshTournament) {
            KendoTournamentGenerator.getInstance().changeLastSelectedTournament(TournamentComboBox.getSelectedItem().toString());
            updateInterface();
        }
    }//GEN-LAST:event_TournamentComboBoxActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        removeSelectedPanel();
    }//GEN-LAST:event_DeleteButtonActionPerformed

    private void AddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddButtonActionPerformed
        addDesignedPanelFirstLevel();
    }//GEN-LAST:event_AddButtonActionPerformed

    private void AddTeamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddTeamButtonActionPerformed
        addTeam();
        updateBlackBoard();
    }//GEN-LAST:event_AddTeamButtonActionPerformed

    private void DeleteTeamsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteTeamsButtonActionPerformed
        DeleteTeamsOfSelectedPanel();
    }//GEN-LAST:event_DeleteTeamsButtonActionPerformed

    private void PassSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_PassSpinnerStateChanged
        try {
            if ((Integer) PassSpinner.getValue() < 1) {
                PassSpinner.setValue(1);
            }

            if ((Integer) PassSpinner.getValue() > 2) {
                PassSpinner.setValue(2);
            }

            if (refreshSpinner) {
                numberMaxOfWinners = (Integer) PassSpinner.getValue();
                KendoTournamentGenerator.getInstance().tournamentManager.setNumberOfTeamsPassNextRound(numberMaxOfWinners);

                /*
                 * consistentTree();
                 * KendoTournamentGenerator.getInstance().tournamentManager.updateInnerLevel(0);
                 * updateBlackBoard();
                 */
            }
        } catch (NullPointerException npe) {
        }
    }//GEN-LAST:event_PassSpinnerStateChanged

    private void TeamListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TeamListMouseClicked
        if (!KendoTournamentGenerator.getInstance().tournamentManager.getMode().equals(TournamentTypes.SIMPLE)) {
            if (evt.getClickCount() == 2) {
                addTeam();
            }
        }
    }//GEN-LAST:event_TeamListMouseClicked

    private void CloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseButtonActionPerformed
        //try{
/*
         * if (TournamentComboBox.getItemCount() > 0 &&
         * KendoTournamentGenerator.getInstance().fightManager.size() == 0) {
         * KendoTournamentGenerator.getInstance().tournamentManager.storeDesigner();
         * }
         */
        //}catch(NullPointerException npe){}
        this.dispose();
}//GEN-LAST:event_CloseButtonActionPerformed

    private void AcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AcceptButtonActionPerformed
        try {
            if (!(KendoTournamentGenerator.getInstance().tournamentManager.getMode().equals(TournamentTypes.MANUAL)) || KendoTournamentGenerator.getInstance().tournamentManager.allGroupsHaveManualLink()) {
                if (MessageManager.questionMessage("questionCreateFight", "Warning!")) {
                    Log.finer("Deleting old fights");
                    KendoTournamentGenerator.getInstance().fightManager.deleteAllFights(championship.name, false);
                    if (KendoTournamentGenerator.getInstance().fightManager.setAll(KendoTournamentGenerator.getInstance().tournamentManager.generateLevelFights(0), true)) {
                        //Delete inner levels when delete old fightManager.
                        Log.finest("Deleting inner levels of old tournament");
                        KendoTournamentGenerator.getInstance().tournamentManager.emptyInnerLevels();
                        this.dispose();
                    }
                }
            } else {
                MessageManager.errorMessage("noLinkFinished", "Error");
            }
        } catch (NullPointerException npe) {
        }
        /*
         * if (TournamentComboBox.getItemCount() > 0) {
         * KendoTournamentGenerator.getInstance().tournamentManager.storeDesigner(FOLDER
         * + File.separator + TournamentComboBox.getSelectedItem().toString() +
         * ".dsg"); }
         */
        KendoTournamentGenerator.getInstance().tournamentManager.deleteUsedDesigner();
    }//GEN-LAST:event_AcceptButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        /*
         * if (TournamentComboBox.getItemCount() > 0 &&
         * KendoTournamentGenerator.getInstance().fightManager.size() == 0) {
         * KendoTournamentGenerator.getInstance().tournamentManager.storeDesigner();
         * }
         */
    }//GEN-LAST:event_formWindowClosing

    private void DeleteAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteAllButtonActionPerformed
        try {
            KendoTournamentGenerator.getInstance().fightManager.deleteAllFightsButNotFromDatabase(championship.name, false);
            KendoTournamentGenerator.getInstance().tournamentManager = new TournamentGroupManager(championship);
            KendoTournamentGenerator.getInstance().tournamentManager.createLevelZero();
            KendoTournamentGenerator.getInstance().tournamentManager.setMode(championship.mode);
            KendoTournamentGenerator.getInstance().tournamentManager.setNumberOfTeamsPassNextRound(numberMaxOfWinners);
            updateMode();
            updateBlackBoard();
            fillTeams();
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
    }//GEN-LAST:event_DeleteAllButtonActionPerformed

    private void TournamentComboBoxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TournamentComboBoxFocusGained
        /*
         * if (TournamentComboBox.getItemCount() > 0 &&
         * KendoTournamentGenerator.getInstance().fightManager.size() == 0) {
         * KendoTournamentGenerator.getInstance().tournamentManager.storeDesigner();
         * }
         */
    }//GEN-LAST:event_TournamentComboBoxFocusGained

    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        consistentTree();
    }//GEN-LAST:event_formWindowGainedFocus

    private void ManualRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ManualRadioButtonItemStateChanged
        if (refreshMode && ManualRadioButton.isSelected()) {
            updateMode();
            updateBlackBoard();
        }
    }//GEN-LAST:event_ManualRadioButtonItemStateChanged

    private void ChampionshipRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ChampionshipRadioButtonItemStateChanged
        if (refreshMode && ChampionshipRadioButton.isSelected()) {
            numberMaxOfWinners = 2;
            PassSpinner.setValue(numberMaxOfWinners);
            KendoTournamentGenerator.getInstance().tournamentManager.setNumberOfTeamsPassNextRound(numberMaxOfWinners);
            updateMode();
            updateBlackBoard();
        }
    }//GEN-LAST:event_ChampionshipRadioButtonItemStateChanged

    private void TreeRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_TreeRadioButtonItemStateChanged
        try {
            if (refreshMode && TreeRadioButton.isSelected()) {
                numberMaxOfWinners = 1;
                PassSpinner.setValue(numberMaxOfWinners);
                KendoTournamentGenerator.getInstance().tournamentManager.setNumberOfTeamsPassNextRound(numberMaxOfWinners);
                updateMode();
                updateBlackBoard();
            }
        } catch (NullPointerException npe) {
        }
    }//GEN-LAST:event_TreeRadioButtonItemStateChanged

    private void CleanLinksButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CleanLinksButtonActionPerformed
        try {
            KendoTournamentGenerator.getInstance().tournamentManager.cleanLinksSelectedGroup();
            updateBlackBoard();
        } catch (NullPointerException npe) {
        }
    }//GEN-LAST:event_CleanLinksButtonActionPerformed

    private void SimpleRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_SimpleRadioButtonItemStateChanged
        if (refreshMode && SimpleRadioButton.isSelected()) {
            updateMode();
            updateBlackBoard();
        }
    }//GEN-LAST:event_SimpleRadioButtonItemStateChanged

    private void DeleteLevelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteLevelButtonActionPerformed
        //Select All levels
        if (LevelComboBox.getSelectedIndex() == LevelComboBox.getItemCount() - 1) {
            if (KendoTournamentGenerator.getInstance().fightManager.deleteAllFights(championship.name, true)) {
                KendoTournamentGenerator.getInstance().tournamentManager.deleteTeamsOfLevel(0);
                MessageManager.translatedMessage("fightDeleted", "MySQL", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            if (KendoTournamentGenerator.getInstance().fightManager.deleteFightsOfLevel(championship.name, LevelComboBox.getSelectedIndex(), true)) {
                KendoTournamentGenerator.getInstance().tournamentManager.deleteTeamsOfLevel(LevelComboBox.getSelectedIndex());
                MessageManager.translatedMessage("fightDeleted", "MySQL", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        updateBlackBoard();
        fillTeams();
    }//GEN-LAST:event_DeleteLevelButtonActionPerformed

    private void LoadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadButtonActionPerformed
        if (MessageManager.questionMessage("questionLoadDesign", "Warning!")) {
            KendoTournamentGenerator.getInstance().fightManager.setAll(KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(championship.name), false);
            KendoTournamentGenerator.getInstance().tournamentManager.refillDesigner(KendoTournamentGenerator.getInstance().fightManager.getFights());
            KendoTournamentGenerator.getInstance().tournamentManager.setMode(championship.mode);
            fillTeams();
            updateListeners();
            updateBlackBoard();
            refreshMode = false;
            updateRadioButton();
            refreshMode = true;
            //KendoTournamentGenerator.getInstance().tournamentManager.storeDesigner();
        }
    }//GEN-LAST:event_LoadButtonActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.toFront();
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AcceptButton;
    private javax.swing.JButton AddButton;
    private javax.swing.JButton AddTeamButton;
    private javax.swing.JScrollPane BlackBoardScrollPane;
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JPanel ChampionshipPanel;
    private javax.swing.JRadioButton ChampionshipRadioButton;
    private javax.swing.JButton CleanLinksButton;
    private javax.swing.JButton CloseButton;
    private javax.swing.JButton DeleteAllButton;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JButton DeleteLevelButton;
    private javax.swing.JLabel DeleteLevelLabel;
    private javax.swing.JButton DeleteTeamsButton;
    private javax.swing.JLabel GroupEditionLabel;
    private javax.swing.JComboBox<String> LevelComboBox;
    private javax.swing.JButton LoadButton;
    private javax.swing.JRadioButton ManualRadioButton;
    private javax.swing.JLabel PassLabel;
    private javax.swing.JSpinner PassSpinner;
    private javax.swing.JRadioButton SimpleRadioButton;
    private javax.swing.JList<String> TeamList;
    private javax.swing.JScrollPane TeamScrollPane;
    private javax.swing.JComboBox<String> TournamentComboBox;
    private javax.swing.JLabel TournamentLabel;
    private javax.swing.ButtonGroup TreeChampionshipButtonGroup;
    private javax.swing.JLabel TreeEditionLabel;
    private javax.swing.JRadioButton TreeRadioButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
