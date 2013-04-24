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
import com.softwaremagico.ktg.tournament.ManualChampionship;
import com.softwaremagico.ktg.tournament.TournamentGroup;
import com.softwaremagico.ktg.tournament.TournamentManagerPool;
import com.softwaremagico.ktg.tournament.TournamentType;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

public class LeagueDesigner extends javax.swing.JFrame {
    
    private DefaultListModel<String> teamModel = new DefaultListModel<>();
    private Translator trans = null;
    private List<Team> teams;
    private Tournament tournament;
    private List<Tournament> listTournaments = new ArrayList<>();
    private boolean refreshTournament = true;
    private BlackBoardPanel bbp;
    private boolean refreshSpinner = true;

    /**
     * Creates new form LeagueDesigner
     */
    public LeagueDesigner() {
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage();
        bbp = new BlackBoardPanel(this, true);
        BlackBoardScrollPane.setViewportView(bbp);
        fillTournaments();
        updateInterface();
        updateLevel();
    }
    
    private void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        this.setTitle(trans.getTranslatedText("titleLeagueDesigner"));
        TournamentLabel.setText(trans.getTranslatedText("TournamentLabel"));
        PassLabel.setText(trans.getTranslatedText("PassLabel"));
        addGroupButton.setText(trans.getTranslatedText("AddGroupButton"));
        DeleteButton.setText(trans.getTranslatedText("DeleteGroupButton"));
        DeleteTeamsButton.setText(trans.getTranslatedText("CleanButton"));
        DeleteAllButton.setText(trans.getTranslatedText("CleanAllButton"));
        AddTeamButton.setText(trans.getTranslatedText("AddTeamButton"));
        AcceptButton.setText(trans.getTranslatedText("GenerateMatchButton"));
        CloseButton.setText(trans.getTranslatedText("CloseButton"));
        manualCheckBox.setText(trans.getTranslatedText("ManualFightsMenuItem"));
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
        if (manualCheckBox.isSelected()) {
            return TournamentType.MANUAL;
        }
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
        uptadeTournament();
        refreshTournament = true;
    }
    
    private void uptadeTournament() {
        tournament = (Tournament) (TournamentComboBox.getSelectedItem());
        KendoTournamentGenerator.getInstance().setLastSelectedTournament(tournament.getName());
        manualCheckBox.setSelected(tournament.getType().equals(TournamentType.MANUAL));
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
            if (TournamentManagerPool.getManager(tournament).getGroups(0).size() < getNumberOfGroupsOfLeague()) {
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
    
    private void removeTeamsOfSelectedPanel() {
        TournamentGroupBox groupBox = bbp.getSelectedBox();
        groupBox.removeTeams();
        updateInfo();
    }
    
    private Team getTeamByName(String name) {
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
    
    private Team getSelectedTeam() {
        String name = (String) TeamList.getSelectedValue();
        return getTeamByName(name);
    }
    
    private void addTeam() {
        Team t = getSelectedTeam();
        if (t != null) {
            addTeamToSelectedPanel(t);
        }
    }
    
    public int getNumberOfGroupsOfLeague() {
        return teams.size() / 2;
    }
    
    private void updateInterface() {
        try {
            teams = TeamPool.getInstance().get(tournament);
            
            fillTeams();
            updateBlackBoard();
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
        addGroupButton = new javax.swing.JButton();
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
        manualCheckBox = new javax.swing.JCheckBox();

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

        addGroupButton.setText("Add");
        addGroupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addGroupButtonActionPerformed(evt);
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

        PassSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 2, 1));
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
                            .addComponent(addGroupButton, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
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

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {DeleteAllButton, DeleteButton, DeleteLevelButton, LevelComboBox, PassSpinner, addGroupButton});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(TreeEditionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(addGroupButton)
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

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {DeleteAllButton, DeleteButton, DeleteLevelButton, LevelComboBox, addGroupButton});

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 248, Short.MAX_VALUE)
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

        manualCheckBox.setText("Manual");
        manualCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualCheckBoxActionPerformed(evt);
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(manualCheckBox))
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
                    .addComponent(manualCheckBox))
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
            uptadeTournament();            
            updateInterface();
        }
    }//GEN-LAST:event_TournamentComboBoxActionPerformed
    
    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        removeSelectedPanel();
    }//GEN-LAST:event_DeleteButtonActionPerformed
    
    private void addGroupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addGroupButtonActionPerformed
        setTournamentType();
        addDesignedPanelToLevelZero();
    }//GEN-LAST:event_addGroupButtonActionPerformed
    
    private void AddTeamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddTeamButtonActionPerformed
        addTeam();
        updateBlackBoard();
    }//GEN-LAST:event_AddTeamButtonActionPerformed
    
    private void DeleteTeamsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteTeamsButtonActionPerformed
        removeTeamsOfSelectedPanel();
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
            if (tournament.getType().equals(TournamentType.MANUAL)) {
                ManualChampionship championship = (ManualChampionship) TournamentManagerPool.getManager(tournament);
                if (!championship.allGroupsHaveNextLink()) {
                    MessageManager.errorMessage(this.getClass().getName(), "noLinkFinished", "Error");
                    return;
                }
            }
            if (MessageManager.questionMessage("questionCreateFight", "Warning!")) {
                FightPool.getInstance().remove(tournament);
                FightPool.getInstance().add(tournament, TournamentManagerPool.getManager(tournament).getFights(0));
                this.dispose();
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
            // updateMode();
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
    
    private void CleanLinksButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CleanLinksButtonActionPerformed
        try {
            if (tournament.getType().equals(TournamentType.MANUAL)) {
                ManualChampionship championship = (ManualChampionship) TournamentManagerPool.getManager(tournament);
                championship.removeLinks();
            }
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
        }
    }//GEN-LAST:event_LoadButtonActionPerformed
    
    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.toFront();
    }//GEN-LAST:event_formWindowOpened
    
    private void manualCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manualCheckBoxActionPerformed
        setTournamentType();
    }//GEN-LAST:event_manualCheckBoxActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AcceptButton;
    private javax.swing.JButton AddTeamButton;
    private javax.swing.JScrollPane BlackBoardScrollPane;
    private javax.swing.JPanel ButtonPanel;
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
    private javax.swing.JLabel PassLabel;
    private javax.swing.JSpinner PassSpinner;
    private javax.swing.JList TeamList;
    private javax.swing.JScrollPane TeamScrollPane;
    private javax.swing.JComboBox TournamentComboBox;
    private javax.swing.JLabel TournamentLabel;
    private javax.swing.ButtonGroup TreeChampionshipButtonGroup;
    private javax.swing.JLabel TreeEditionLabel;
    private javax.swing.JButton addGroupButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JCheckBox manualCheckBox;
    // End of variables declaration//GEN-END:variables
}
