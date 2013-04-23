package com.softwaremagico.ktg.gui.tournament;
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

import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.MessageManager;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.FightPool;
import com.softwaremagico.ktg.database.TeamPool;
import com.softwaremagico.ktg.database.TournamentPool;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.tournament.ITournamentManager;
import com.softwaremagico.ktg.tournament.TournamentGroup;
import com.softwaremagico.ktg.tournament.TournamentManagerPool;
import com.softwaremagico.ktg.tournament.TournamentType;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JViewport;

public class LeagueDesigner extends javax.swing.JFrame {

    private DefaultListModel<String> teamModel = new DefaultListModel<>();
    private Translator trans = null;
    private List<Team> teams;
    private Tournament tournament;
    private List<Tournament> listTournaments = new ArrayList<>();
    private boolean refreshTournament = true;
    private boolean refreshMode = true;
    private BlackBoardPanel bbp;
    private Point p;
    private JViewport viewport;
    private boolean refreshSpinner = true;
    private ITournamentManager tournamentManager;
    private Integer selectedGroup;

    /**
     * Creates new form LeagueDesigner
     */
    public LeagueDesigner() {
        refreshMode = false;
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage();
        bbp = new BlackBoardPanel(this, true);
        BlackBoardScrollPane.setViewportView(bbp);
        fillTournaments();
        updateInterface();
        updateLevel();
        updateMode();
        refreshMode = true;
    }

    private void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        this.setTitle(trans.getTranslatedText("titleLeagueDesigner"));
        TournamentLabel.setText(trans.getTranslatedText("TournamentLabel"));
        PassLabel.setText(trans.getTranslatedText("PassLabel"));
        AddButton.setText(trans.getTranslatedText("AddGroupButton"));
        DeleteButton.setText(trans.getTranslatedText("DeleteGroupButton"));
        DeleteTeamsButton.setText(trans.getTranslatedText("CleanButton"));
        DeleteAllButton.setText(trans.getTranslatedText("CleanAllButton"));
        AddTeamButton.setText(trans.getTranslatedText("AddTeamButton"));
        AcceptButton.setText(trans.getTranslatedText("GenerateMatchButton"));
        CloseButton.setText(trans.getTranslatedText("CloseButton"));
        ChampionshipRadioButton.setText(trans.getTranslatedText("ChampionshipRadioButton"));
        TreeRadioButton.setText(trans.getTranslatedText("TreeRadioButton"));
        CleanLinksButton.setText(trans.getTranslatedText("CleanLinks"));
        DeleteLevelLabel.setText(trans.getTranslatedText("DeleteLevelLabel"));
        DeleteLevelButton.setText(trans.getTranslatedText("DeleteButton"));
        LoadButton.setText(trans.getTranslatedText("ButtonLoadTournament"));
        TreeEditionLabel.setText(trans.getTranslatedText("TournamentLabel"));
        GroupEditionLabel.setText(trans.getTranslatedText("GroupLabel"));
    }

    private void setTournamentType() {
        ((Tournament) TournamentComboBox.getSelectedItem()).setType(getDefinedType());
    }

    protected TournamentType getDefinedType() {
        return TournamentType.CHAMPIONSHIP;
    }

    private void fillTournaments() {
        refreshTournament = false;
        TournamentComboBox.removeAllItems();
        try {
            listTournaments = TournamentPool.getInstance().getSorted();
            for (int i = 0; i < listTournaments.size(); i++) {
                TournamentComboBox.addItem(listTournaments.get(i));
            }
        } catch (NullPointerException npe) {
        }
        TournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
        tournament = (Tournament) (TournamentComboBox.getSelectedItem());
        refreshTournament = true;
    }

    /**
     * Fill the list with the results obtained
     */
    public void fillTeams() {
        try {
            teamModel.removeAllElements();
            for (int i = 0; i < teams.size(); i++) {
                if (!TournamentManagerPool.getManager(tournament).exist(teams.get(i))) {
                    teamModel.addElement(teams.get(i).getName());
                }
            }
            if (teamModel.size() > 0) {
                TeamList.setSelectedIndex(0);
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
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

    private void addDesignedPanelToLevelZero() {
        try {
            if (TournamentManagerPool.getManager(tournament).getGroups(0).size() < obtainNumberOfGroupsOfLeague()) {
                //int defaultArena = (TournamentManagerPool.getManager(tournament).returnGroupsOfLevel(0).size()) / tournament.fightingAreas;
                int defaultArena = 0;
                TournamentGroup group = new TournamentGroup(tournament, 0, defaultArena);
                TournamentManagerPool.getManager(tournament).addGroup(group);
                TournamentManagerPool.getManager(tournament).setDefaultFightAreas();
                updateInfo();
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
    }

    private void addTeamToSelectedPanel(Team t) {
        bbp.getSelectedBox().getTournamentGroup().addTeam(t);
        updateInfo();
    }

    private void removeSelectedPanel() {
        try {
            TournamentGroupBox groupBox = bbp.getSelectedBox();
            TournamentManagerPool.getManager(tournament).removeGroup(groupBox.getTournamentGroup());
            updateInfo();
        } catch (NullPointerException npe) {
        }
    }

    private void deleteTeamsOfSelectedPanel() {
        TournamentGroupBox groupBox = bbp.getSelectedBox();
        groupBox.getTournamentGroup().removeTeams();
        updateInfo();
    }

    private Team returnTeamByName(String name) {
        try {
            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).getName().equals(name)) {
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

    public int obtainNumberOfGroupsOfLeague() {
        return teams.size() / 2;
    }

    private void updateInterface() {
        try {
            //TournamentManagerPool.getManager(tournament) = new TournamentGroupManager(tournament);
            teams = TeamPool.getInstance().get(tournament);

            //if (!tournament.mode.equals(TournamentType.SIMPLE)) {
            //If it is not stored in a file, generate it. 
            //TournamentManagerPool.getManager(tournament).refillDesigner(DatabaseConnection.getInstance().getDatabase().searchFightsByTournament(tournament));
            //FightPool.getManager(tournament).getFightsFromDatabase(tournament);
            //}

            fillTeams();
            updateBlackBoard();
            updateTournamentType();
        } catch (NullPointerException npe) {
        }
    }

    protected void updateInfo() {
        updateTeams();
        updateBlackBoard();
    }

    private void updateBlackBoard() {
        try {
            bbp.update(tournament);
            BlackBoardScrollPane.revalidate();
            BlackBoardScrollPane.repaint();
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
    }

    private void updateTournamentType() {
        refreshSpinner = false;
        switch (tournament.getType()) {
            case CHAMPIONSHIP:
                ChampionshipRadioButton.setSelected(true);
                PassSpinner.setValue(2);
                PassSpinner.setEnabled(false);
                break;
            case MANUAL:
                ManualRadioButton.setSelected(true);
                PassSpinner.setEnabled(true);
                break;
            case LEAGUE_TREE:
                TreeRadioButton.setSelected(true);
                PassSpinner.setValue(1);
                PassSpinner.setEnabled(false);
                break;
        }
        refreshSpinner = true;
    }

    private void updateMode() {
        try {
            TournamentType oldMode = tournament.getType();
            if (ManualRadioButton.isSelected()) {
                tournament.setType(TournamentType.MANUAL);
                CleanLinksButton.setVisible(true);
            } else if (ChampionshipRadioButton.isSelected()) {
                tournament.setType(TournamentType.CHAMPIONSHIP);
                CleanLinksButton.setVisible(false);
            } else if (TreeRadioButton.isSelected()) {
                tournament.setType(TournamentType.LEAGUE_TREE);
                CleanLinksButton.setVisible(false);
            }

            //Mode has changed. Update Levels
            if (!oldMode.equals(tournament.getType()) && TournamentManagerPool.getManager(tournament).getNumberOfLevels() > 0) {
                TournamentManagerPool.getManager(tournament).removeGroups(0);
            }

            updateTournamentType();

            if (tournament.getType().equals(TournamentType.SIMPLE)) {
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
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }

        updateInfo();
    }

    private void updateLevel() {
        LevelComboBox.removeAllItems();
        for (int i = 0; i < TournamentManagerPool.getManager(tournament).getNumberOfLevels(); i++) {
            if (i < TournamentManagerPool.getManager(tournament).getNumberOfLevels() - 2) {
                LevelComboBox.addItem(trans.getTranslatedText("Round") + " " + (TournamentManagerPool.getManager(tournament).getNumberOfLevels() - i));
            } else if (i == TournamentManagerPool.getManager(tournament).getNumberOfLevels() - 2) {
                LevelComboBox.addItem(trans.getTranslatedText("SemiFinalLabel"));
            } else {
                LevelComboBox.addItem(trans.getTranslatedText("FinalLabel"));
            }
        }
        LevelComboBox.addItem(trans.getTranslatedText("All"));
    }

    private void focus(int x, int y) {
        p = new Point();
        Dimension dv = viewport.getViewSize();
        Dimension de = viewport.getExtentSize();


        int columnsWide = this.getWidth() / TournamentManagerPool.getManager(tournament).getNumberOfLevels();
        int rowsWide;
        if (TournamentManagerPool.getManager(tournament).getGroups(0).size() > 0) {
            rowsWide = this.getWidth() / TournamentManagerPool.getManager(tournament).getGroups(0).size();
        } else {
            rowsWide = 1;
        }

        p.x = columnsWide * x;
        p.y = rowsWide * y;

        viewport.setViewPosition(p);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TreeChampionshipButtonGroup = new javax.swing.ButtonGroup();
        TournamentLabel = new javax.swing.JLabel();
        TournamentComboBox = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        BlackBoardScrollPane = new javax.swing.JScrollPane();
        DeleteLevelLabel = new javax.swing.JLabel();
        LevelComboBox = new javax.swing.JComboBox();
        DeleteLevelButton = new javax.swing.JButton();
        AddButton = new javax.swing.JButton();
        DeleteButton = new javax.swing.JButton();
        DeleteAllButton = new javax.swing.JButton();
        TreeEditionLabel = new javax.swing.JLabel();
        PassLabel = new javax.swing.JLabel();
        PassSpinner = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        TeamScrollPane = new javax.swing.JScrollPane();
        TeamList = new javax.swing.JList();
        AddTeamButton = new javax.swing.JButton();
        DeleteTeamsButton = new javax.swing.JButton();
        CleanLinksButton = new javax.swing.JButton();
        GroupEditionLabel = new javax.swing.JLabel();
        ButtonPanel = new javax.swing.JPanel();
        AcceptButton = new javax.swing.JButton();
        CloseButton = new javax.swing.JButton();
        LoadButton = new javax.swing.JButton();
        ManualRadioButton = new javax.swing.JRadioButton();
        TreeRadioButton = new javax.swing.JRadioButton();
        ChampionshipRadioButton = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 319, Short.MAX_VALUE)
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

        TreeChampionshipButtonGroup.add(ManualRadioButton);
        ManualRadioButton.setText("Manual");
        ManualRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ManualRadioButtonItemStateChanged(evt);
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

        TreeChampionshipButtonGroup.add(ChampionshipRadioButton);
        ChampionshipRadioButton.setText("Championship");
        ChampionshipRadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ChampionshipRadioButtonItemStateChanged(evt);
            }
        });

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
                        .addComponent(TournamentComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ManualRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ChampionshipRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(TreeRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TournamentLabel)
                    .addComponent(TournamentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ManualRadioButton)
                    .addComponent(ChampionshipRadioButton)
                    .addComponent(TreeRadioButton))
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
            tournament = (Tournament) (TournamentComboBox.getSelectedItem());
            KendoTournamentGenerator.getInstance().setLastSelectedTournament(TournamentComboBox.getSelectedItem().toString());
            updateInterface();
        }
    }//GEN-LAST:event_TournamentComboBoxActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        removeSelectedPanel();
    }//GEN-LAST:event_DeleteButtonActionPerformed

    private void AddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddButtonActionPerformed
        setTournamentType();
        addDesignedPanelToLevelZero();
    }//GEN-LAST:event_AddButtonActionPerformed

    private void AddTeamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddTeamButtonActionPerformed
        addTeam();
        updateBlackBoard();
    }//GEN-LAST:event_AddTeamButtonActionPerformed

    private void DeleteTeamsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteTeamsButtonActionPerformed
        deleteTeamsOfSelectedPanel();
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
                tournament.setHowManyTeamsOfGroupPassToTheTree((Integer) PassSpinner.getValue());
            }
        } catch (NullPointerException npe) {
        }
    }//GEN-LAST:event_PassSpinnerStateChanged

    private void TeamListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TeamListMouseClicked
        if (!tournament.getType().equals(TournamentType.SIMPLE)) {
            if (evt.getClickCount() == 2) {
                addTeam();
            }
        }
    }//GEN-LAST:event_TeamListMouseClicked

    private void CloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseButtonActionPerformed
        this.dispose();
}//GEN-LAST:event_CloseButtonActionPerformed

    private void AcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AcceptButtonActionPerformed
        try {
            if (!TournamentManagerPool.getManager(tournament).allGroupsHaveNextLink()) {
                if (MessageManager.questionMessage("questionCreateFight", "Warning!")) {
                    FightPool.getInstance().remove(tournament);
                    FightPool.getInstance().add(tournament, TournamentManagerPool.getManager(tournament).getFights(0));
                    this.dispose();
                }
            } else {
                MessageManager.errorMessage(this.getClass().getName(), "noLinkFinished", "Error");
            }
        } catch (NullPointerException npe) {
        }
    }//GEN-LAST:event_AcceptButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        /*
         * if (TournamentComboBox.getItemCount() > 0 &&
         * FightPool.getManager(tournament).size() == 0) {
         * TournamentManagerPool.getManager(tournament).storeDesigner(); }
         */
    }//GEN-LAST:event_formWindowClosing

    private void DeleteAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteAllButtonActionPerformed
        try {
            FightPool.getInstance().reset();
            TournamentManagerPool.getManager(tournament).removeGroups(0);
            updateMode();
            updateBlackBoard();
            fillTeams();
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
    }//GEN-LAST:event_DeleteAllButtonActionPerformed

    private void TournamentComboBoxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_TournamentComboBoxFocusGained
        /*
         * if (TournamentComboBox.getItemCount() > 0 &&
         * FightPool.getManager(tournament).size() == 0) {
         * TournamentManagerPool.getManager(tournament).storeDesigner(); }
         */
    }//GEN-LAST:event_TournamentComboBoxFocusGained

    private void ManualRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ManualRadioButtonItemStateChanged
        if (refreshMode && ManualRadioButton.isSelected()) {
            updateMode();
            updateBlackBoard();
        }
    }//GEN-LAST:event_ManualRadioButtonItemStateChanged

    private void ChampionshipRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ChampionshipRadioButtonItemStateChanged
        if (refreshMode && ChampionshipRadioButton.isSelected()) {
            PassSpinner.setValue(2);
            updateMode();
            updateBlackBoard();
        }
    }//GEN-LAST:event_ChampionshipRadioButtonItemStateChanged

    private void TreeRadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_TreeRadioButtonItemStateChanged
        try {
            if (refreshMode && TreeRadioButton.isSelected()) {
                PassSpinner.setValue(1);
                updateMode();
                updateBlackBoard();
            }
        } catch (NullPointerException npe) {
        }
    }//GEN-LAST:event_TreeRadioButtonItemStateChanged

    private void CleanLinksButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CleanLinksButtonActionPerformed
        try {
            TournamentManagerPool.getManager(tournament).removeLinks();
            updateBlackBoard();
        } catch (NullPointerException npe) {
        }
    }//GEN-LAST:event_CleanLinksButtonActionPerformed

    private void DeleteLevelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteLevelButtonActionPerformed
        //Select All levels
        if (LevelComboBox.getSelectedIndex() == LevelComboBox.getItemCount() - 1) {
            FightPool.getInstance().remove(tournament);
            TournamentManagerPool.getManager(tournament).removeTeams(0);
            MessageManager.translatedMessage(this.getClass().getName(), "fightsDeleted", "MySQL", JOptionPane.INFORMATION_MESSAGE);
        } else {
            FightPool.getInstance().remove(tournament, LevelComboBox.getSelectedIndex());
            TournamentManagerPool.getManager(tournament).removeTeams(LevelComboBox.getSelectedIndex() > 0 ? LevelComboBox.getSelectedIndex() : 1);
            MessageManager.translatedMessage(this.getClass().getName(), "fightsDeleted", "MySQL", JOptionPane.INFORMATION_MESSAGE);
        }
        updateBlackBoard();
        fillTeams();
    }//GEN-LAST:event_DeleteLevelButtonActionPerformed

    private void LoadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LoadButtonActionPerformed
        if (MessageManager.questionMessage("questionLoadDesign", "Warning!")) {
            FightPool.getInstance().reset();
            tournament.setType(tournament.getType());
            fillTeams();
            updateBlackBoard();
            refreshMode = false;
            updateTournamentType();
            refreshMode = true;
            //TournamentManagerPool.getManager(tournament).storeDesigner();
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
    private javax.swing.JRadioButton ChampionshipRadioButton;
    private javax.swing.JButton CleanLinksButton;
    private javax.swing.JButton CloseButton;
    private javax.swing.JButton DeleteAllButton;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JButton DeleteLevelButton;
    private javax.swing.JLabel DeleteLevelLabel;
    private javax.swing.JButton DeleteTeamsButton;
    private javax.swing.JLabel GroupEditionLabel;
    private javax.swing.JComboBox LevelComboBox;
    private javax.swing.JButton LoadButton;
    private javax.swing.JRadioButton ManualRadioButton;
    private javax.swing.JLabel PassLabel;
    private javax.swing.JSpinner PassSpinner;
    private javax.swing.JList TeamList;
    private javax.swing.JScrollPane TeamScrollPane;
    private javax.swing.JComboBox TournamentComboBox;
    private javax.swing.JLabel TournamentLabel;
    private javax.swing.ButtonGroup TreeChampionshipButtonGroup;
    private javax.swing.JLabel TreeEditionLabel;
    private javax.swing.JRadioButton TreeRadioButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
