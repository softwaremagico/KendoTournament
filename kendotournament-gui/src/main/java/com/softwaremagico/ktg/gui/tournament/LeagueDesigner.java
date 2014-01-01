package com.softwaremagico.ktg.gui.tournament;
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
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.persistence.CustomLinkPool;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.persistence.TeamPool;
import com.softwaremagico.ktg.persistence.TournamentPool;
import com.softwaremagico.ktg.tournament.CustomChampionship;
import com.softwaremagico.ktg.tournament.LeagueLevelCustom;
import com.softwaremagico.ktg.tournament.PersonalizedFightsException;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.TournamentManagerFactory;
import com.softwaremagico.ktg.tournament.TournamentType;
import com.softwaremagico.ktg.tournament.TreeTournamentGroup;
import java.awt.Toolkit;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

public final class LeagueDesigner extends javax.swing.JFrame {

    private DefaultListModel<String> teamModel = new DefaultListModel<>();
    private Translator trans = null;
    private List<Team> teams;
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
        //Update groups if it already is a championship.
        /*if (getSelectedTournament().isChampionship()) {
         TournamentManagerFactory.getManager(getSelectedTournament()).fillGroups();
         }*/
        updateInfo();
        updateLevel();
    }

    private void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        this.setTitle(trans.getTranslatedText("titleLeagueDesigner"));
        TournamentLabel.setText(trans.getTranslatedText("TournamentLabel"));
        PassLabel.setText(trans.getTranslatedText("PassLabel"));
        addGroupButton.setText(trans.getTranslatedText("AddGroupButton"));
        deleteButton.setText(trans.getTranslatedText("DeleteGroupButton"));
        deleteTeamsButton.setText(trans.getTranslatedText("CleanButton"));
        deleteAllButton.setText(trans.getTranslatedText("CleanAllButton"));
        addTeamButton.setText(trans.getTranslatedText("AddTeamButton"));
        acceptButton.setText(trans.getTranslatedText("AcceptButton"));
        closeButton.setText(trans.getTranslatedText("CloseButton"));
        customCheckBox.setText(trans.getTranslatedText("ManualFightsMenuItem"));
        cleanLinksButton.setText(trans.getTranslatedText("CleanLinks"));
        DeleteLevelLabel.setText(trans.getTranslatedText("DeleteLevelLabel"));
        deleteLevelButton.setText(trans.getTranslatedText("DeleteButton"));
        TreeEditionLabel.setText(trans.getTranslatedText("TournamentLabel"));
        GroupEditionLabel.setText(trans.getTranslatedText("GroupLabel"));
    }

    private Tournament getSelectedTournament() {
        return (Tournament) (tournamentComboBox.getSelectedItem());
    }

    private void setTournamentType() {
        getSelectedTournament().setType(getDefinedType());
    }

    protected TournamentType getDefinedType() {
        if (customCheckBox.isSelected()) {
            return TournamentType.CUSTOM_CHAMPIONSHIP;
        }
        return TournamentType.CHAMPIONSHIP;
    }

    private void fillTournaments() {
        refreshTournament = false;
        tournamentComboBox.removeAllItems();
        try {
            listTournaments = TournamentPool.getInstance().getSorted();
            for (int i = 0; i < listTournaments.size(); i++) {
                tournamentComboBox.addItem(listTournaments.get(i));
            }
        } catch (NullPointerException npe) {
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
        tournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
        uptadeTournament();
        refreshTournament = true;
    }

    private void uptadeTournament() {
        try {
            KendoTournamentGenerator.getInstance().setLastSelectedTournament(getSelectedTournament().getName());
        } catch (NullPointerException npe) {
        }
        customCheckBox.setSelected(getSelectedTournament().getType().equals(TournamentType.CUSTOM_CHAMPIONSHIP));
        cleanLinksButton.setEnabled(customCheckBox.isSelected());
        refreshSpinner = false;
        winnerPassSpinner.setValue(getSelectedTournament().getHowManyTeamsOfGroupPassToTheTree());
        refreshSpinner = true;
        try {
            teams = TeamPool.getInstance().get(getSelectedTournament());
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
    }

    /**
     * Fill the list with the results obtained
     */
    public void fillTeams() {
        try {
            teamModel.removeAllElements();
            for (int i = 0; i < teams.size(); i++) {
                if (!TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType()).exist(teams.get(i))) {
                    teamModel.addElement(teams.get(i).getName());
                }
            }
            if (teamModel.size() > 0) {
                teamList.setSelectedIndex(0);
            }
        } catch (NullPointerException npe) {
            AlertManager.showErrorInformation(this.getClass().getName(), npe);
        }
    }

    private void updateTeams() {
        int index = teamList.getSelectedIndex();
        fillTeams();
        if (index < teamModel.size() && index >= 0) {
            teamList.setSelectedIndex(index);
        } else {
            index--;
            if (index >= 0) {
                teamList.setSelectedIndex(index);
            }
        }
    }

    private void addDesignedPanelToLevelZero() {
        try {
            if (TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType()).getGroups(0).size() < getNumberOfGroupsOfLeague()) {
                int defaultArena = 0;
                TGroup group = new TreeTournamentGroup(getSelectedTournament(), 0, defaultArena, TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType()).getGroups(0).size());
                TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType()).addGroup(group);
                TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType()).setDefaultFightAreas();
                updateInfo();
            }
        } catch (NullPointerException npe) {
            AlertManager.showErrorInformation(this.getClass().getName(), npe);
        }
    }

    private void addTeamToSelectedPanel(Team t) {
        if (TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType()).getGroups().size() > 0) {
            bbp.getSelectedBox().getTournamentGroup().addTeam(t);
            updateInfo();
        }
    }

    private void removeSelectedPanel() {
        try {
            TournamentGroupBox groupBox = bbp.getSelectedBox();
            if (groupBox != null) {
                if (getSelectedTournament().getType().equals(TournamentType.CUSTOM_CHAMPIONSHIP)) {
                    CustomChampionship championship = (CustomChampionship) TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType());
                    championship.removeLinks();
                }
                TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType()).removeGroup(groupBox.getTournamentGroup());
                updateInfo();
            }
        } catch (NullPointerException npe) {
        }
    }

    private void removeTeamsOfSelectedPanel() {
        TournamentGroupBox groupBox = bbp.getSelectedBox();
        if (groupBox != null) {
            groupBox.removeTeams();
            updateInfo();
        }
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
        String name = (String) teamList.getSelectedValue();
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

    protected void updateInfo() {
        updateTeams();
        updateBlackBoard();
    }

    private void updateBlackBoard() {
        try {
            bbp.update(getSelectedTournament());
            BlackBoardScrollPane.revalidate();
            BlackBoardScrollPane.repaint();
        } catch (NullPointerException npe) {
            AlertManager.showErrorInformation(this.getClass().getName(), npe);
        }
    }

    private void updateLevel() {
        levelComboBox.removeAllItems();
        for (int i = 0; i < TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType()).getNumberOfLevels(); i++) {
            if (i < TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType()).getNumberOfLevels() - 2) {
                levelComboBox.addItem(trans.getTranslatedText("Round") + " " + (TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType()).getNumberOfLevels() - i));
            } else if (i == TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType()).getNumberOfLevels() - 2) {
                levelComboBox.addItem(trans.getTranslatedText("SemiFinalLabel"));
            } else {
                levelComboBox.addItem(trans.getTranslatedText("FinalLabel"));
            }
        }
        levelComboBox.addItem(trans.getTranslatedText("All"));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TournamentLabel = new javax.swing.JLabel();
        tournamentComboBox = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        BlackBoardScrollPane = new javax.swing.JScrollPane();
        DeleteLevelLabel = new javax.swing.JLabel();
        levelComboBox = new javax.swing.JComboBox();
        deleteLevelButton = new javax.swing.JButton();
        addGroupButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        deleteAllButton = new javax.swing.JButton();
        TreeEditionLabel = new javax.swing.JLabel();
        PassLabel = new javax.swing.JLabel();
        winnerPassSpinner = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        TeamScrollPane = new javax.swing.JScrollPane();
        teamList = new javax.swing.JList();
        addTeamButton = new javax.swing.JButton();
        deleteTeamsButton = new javax.swing.JButton();
        cleanLinksButton = new javax.swing.JButton();
        GroupEditionLabel = new javax.swing.JLabel();
        ButtonPanel = new javax.swing.JPanel();
        acceptButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        customCheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        TournamentLabel.setText("Tournament:");

        tournamentComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tournamentComboBoxActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        BlackBoardScrollPane.setBackground(new java.awt.Color(255, 255, 255));

        DeleteLevelLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        DeleteLevelLabel.setText("Delete Level:");

        deleteLevelButton.setText("Delete");
        deleteLevelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteLevelButtonActionPerformed(evt);
            }
        });

        addGroupButton.setText("Add");
        addGroupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addGroupButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        deleteAllButton.setText("Delete All");
        deleteAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAllButtonActionPerformed(evt);
            }
        });

        TreeEditionLabel.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        TreeEditionLabel.setText("Tree Edition:");

        PassLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        PassLabel.setText("Pass:");

        winnerPassSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 2, 1));
        winnerPassSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                winnerPassSpinnerStateChanged(evt);
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
                            .addComponent(deleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                            .addComponent(deleteAllButton, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(DeleteLevelLabel)
                            .addComponent(levelComboBox, 0, 149, Short.MAX_VALUE)
                            .addComponent(deleteLevelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(PassLabel)
                        .addComponent(winnerPassSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BlackBoardScrollPane)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addGroupButton, deleteAllButton, deleteButton, deleteLevelButton, levelComboBox, winnerPassSpinner});

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
                        .addComponent(deleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteAllButton)
                        .addGap(18, 18, 18)
                        .addComponent(DeleteLevelLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(levelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteLevelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(PassLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(winnerPassSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(BlackBoardScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {addGroupButton, deleteAllButton, deleteButton, deleteLevelButton, levelComboBox});

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        teamList.setModel(teamModel);
        teamList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                teamListMouseClicked(evt);
            }
        });
        TeamScrollPane.setViewportView(teamList);

        addTeamButton.setText("Add Team");
        addTeamButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTeamButtonActionPerformed(evt);
            }
        });

        deleteTeamsButton.setText("Clear");
        deleteTeamsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteTeamsButtonActionPerformed(evt);
            }
        });

        cleanLinksButton.setText("Clear Links");
        cleanLinksButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanLinksButtonActionPerformed(evt);
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
                    .addComponent(addTeamButton, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteTeamsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cleanLinksButton, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TeamScrollPane)
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addTeamButton, cleanLinksButton, deleteTeamsButton});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(GroupEditionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addTeamButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteTeamsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cleanLinksButton))
                    .addComponent(TeamScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {addTeamButton, cleanLinksButton, deleteTeamsButton});

        acceptButton.setText("Accept");
        acceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptButtonActionPerformed(evt);
            }
        });

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ButtonPanelLayout = new javax.swing.GroupLayout(ButtonPanel);
        ButtonPanel.setLayout(ButtonPanelLayout);
        ButtonPanelLayout.setHorizontalGroup(
            ButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ButtonPanelLayout.createSequentialGroup()
                .addContainerGap(494, Short.MAX_VALUE)
                .addComponent(acceptButton, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton))
        );

        ButtonPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {acceptButton, closeButton});

        ButtonPanelLayout.setVerticalGroup(
            ButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(acceptButton)
                .addComponent(closeButton))
        );

        ButtonPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {acceptButton, closeButton});

        customCheckBox.setText("Manual");
        customCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customCheckBoxActionPerformed(evt);
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
                        .addComponent(tournamentComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(customCheckBox))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TournamentLabel)
                    .addComponent(tournamentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(customCheckBox))
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
    private void tournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tournamentComboBoxActionPerformed
        if (refreshTournament) {
            uptadeTournament();
            updateInfo();
        }
    }//GEN-LAST:event_tournamentComboBoxActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        removeSelectedPanel();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void addGroupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addGroupButtonActionPerformed
        setTournamentType();
        addDesignedPanelToLevelZero();
    }//GEN-LAST:event_addGroupButtonActionPerformed

    private void addTeamButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTeamButtonActionPerformed
        addTeam();
    }//GEN-LAST:event_addTeamButtonActionPerformed

    private void deleteTeamsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteTeamsButtonActionPerformed
        removeTeamsOfSelectedPanel();
    }//GEN-LAST:event_deleteTeamsButtonActionPerformed

    private void winnerPassSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_winnerPassSpinnerStateChanged
        if (refreshSpinner) {
            try {
                TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType()).setHowManyTeamsOfGroupPassToTheTree((Integer) winnerPassSpinner.getValue());
                getSelectedTournament().setHowManyTeamsOfGroupPassToTheTree((Integer) winnerPassSpinner.getValue());
            } catch (NullPointerException npe) {
            }
            updateBlackBoard();
        }
    }//GEN-LAST:event_winnerPassSpinnerStateChanged

    private void teamListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teamListMouseClicked
        if (!getSelectedTournament().getType().equals(TournamentType.SIMPLE)) {
            if (evt.getClickCount() == 2) {
                addTeam();
            }
        }
    }//GEN-LAST:event_teamListMouseClicked

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.dispose();
}//GEN-LAST:event_closeButtonActionPerformed

    private void acceptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptButtonActionPerformed
        try {
            if (getSelectedTournament().getType().equals(TournamentType.CUSTOM_CHAMPIONSHIP)) {
                CustomChampionship championship = (CustomChampionship) TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType());
                if (!championship.allGroupsHaveNextLink()) {
                    AlertManager.errorMessage(this.getClass().getName(), "noLinkFinished", "Error");
                    return;
                }
            }
            if (AlertManager.questionMessage("questionCreateFight", "Warning!")) {
                //Delete fights.
                FightPool.getInstance().remove(getSelectedTournament());
                //Delete fights and teams in groups
                TournamentManagerFactory.getManager(getSelectedTournament()).resetFights();
                //Remove teams of level greater than zero. 
                TournamentManagerFactory.getManager(getSelectedTournament()).removeTeams(1);
                try {
                    if (FightPool.getInstance().add(getSelectedTournament(), TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType()).createSortedFights(0))) {
                        AlertManager.informationMessage(this.getClass().getName(), "fightStored", "New Fight");
                        //Update tournament type to database.
                        TournamentPool.getInstance().update(getSelectedTournament());
                        //Delete all previous links if exists.
                        CustomLinkPool.getInstance().remove(getSelectedTournament());
                        //Update manual links if necesary.
                        if (getSelectedTournament().getType().equals(TournamentType.CUSTOM_CHAMPIONSHIP)) {
                            //Add new links.
                            CustomLinkPool.getInstance().add(getSelectedTournament(), ((LeagueLevelCustom) TournamentManagerFactory.getManager(getSelectedTournament()).getLevel(0)).getLinks());
                        }
                        this.dispose();
                    }
                } catch (PersonalizedFightsException e) {
                    // Not possible here.
                }
            }
        } catch (NullPointerException npe) {
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
    }//GEN-LAST:event_acceptButtonActionPerformed

    private void deleteAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAllButtonActionPerformed
        try {
            FightPool.getInstance().remove(getSelectedTournament());
            TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType()).removeGroups(0);
            updateBlackBoard();
            fillTeams();
            if (getSelectedTournament().getType().equals(TournamentType.CUSTOM_CHAMPIONSHIP)) {
                CustomChampionship championship = (CustomChampionship) TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType());
                championship.removeLinks();
            }
        } catch (NullPointerException npe) {
            AlertManager.showErrorInformation(this.getClass().getName(), npe);
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
    }//GEN-LAST:event_deleteAllButtonActionPerformed

    private void cleanLinksButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanLinksButtonActionPerformed
        try {
            if (getSelectedTournament().getType().equals(TournamentType.CUSTOM_CHAMPIONSHIP)) {
                CustomChampionship championship = (CustomChampionship) TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType());
                if (bbp.getSelectedBox() != null) {
                    championship.removeLinks(bbp.getSelectedBox().getTournamentGroup());
                }
            }
            updateBlackBoard();
        } catch (NullPointerException npe) {
        }
    }//GEN-LAST:event_cleanLinksButtonActionPerformed

    private void deleteLevelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteLevelButtonActionPerformed
        //Select All levels
        try {
            if (levelComboBox.getSelectedIndex() == levelComboBox.getItemCount() - 1) {
                FightPool.getInstance().remove(getSelectedTournament());
                TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType()).removeTeams(0);
                AlertManager.translatedMessage(this.getClass().getName(), "fightsDeleted", "MySQL", JOptionPane.INFORMATION_MESSAGE);
            } else {
                FightPool.getInstance().remove(getSelectedTournament(), levelComboBox.getSelectedIndex());
                TournamentManagerFactory.getManager(getSelectedTournament(), getDefinedType()).removeTeams(levelComboBox.getSelectedIndex() > 0 ? levelComboBox.getSelectedIndex() : 1);
                AlertManager.translatedMessage(this.getClass().getName(), "fightsDeleted", "MySQL", JOptionPane.INFORMATION_MESSAGE);
            }
            updateBlackBoard();
            fillTeams();
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
    }//GEN-LAST:event_deleteLevelButtonActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.toFront();
    }//GEN-LAST:event_formWindowOpened

    private void customCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customCheckBoxActionPerformed
        setTournamentType();
        if (customCheckBox.isSelected()) {
            AlertManager.informationMessage(this.getClass().getName(), "manualChampionshipHelp", "Designer");
        } else {
        }
        updateInfo();
        cleanLinksButton.setEnabled(customCheckBox.isSelected());
    }//GEN-LAST:event_customCheckBoxActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane BlackBoardScrollPane;
    private javax.swing.JPanel ButtonPanel;
    private javax.swing.JLabel DeleteLevelLabel;
    private javax.swing.JLabel GroupEditionLabel;
    private javax.swing.JLabel PassLabel;
    private javax.swing.JScrollPane TeamScrollPane;
    private javax.swing.JLabel TournamentLabel;
    private javax.swing.JLabel TreeEditionLabel;
    private javax.swing.JButton acceptButton;
    private javax.swing.JButton addGroupButton;
    private javax.swing.JButton addTeamButton;
    private javax.swing.JButton cleanLinksButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JCheckBox customCheckBox;
    private javax.swing.JButton deleteAllButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton deleteLevelButton;
    private javax.swing.JButton deleteTeamsButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JComboBox levelComboBox;
    private javax.swing.JList teamList;
    private javax.swing.JComboBox tournamentComboBox;
    private javax.swing.JSpinner winnerPassSpinner;
    // End of variables declaration//GEN-END:variables
}
