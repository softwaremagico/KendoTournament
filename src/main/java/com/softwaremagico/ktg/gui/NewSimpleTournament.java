/*
 * 
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
 *   Created on 01-ene-2009.
 */
package com.softwaremagico.ktg.gui;

import com.softwaremagico.ktg.*;
import com.softwaremagico.ktg.language.Translator;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Jorge
 */
public class NewSimpleTournament extends javax.swing.JFrame {

    Translator trans = null;
    DefaultListModel<String> fightsModel = new DefaultListModel<String>();
    List<Tournament> listTournaments = new ArrayList<Tournament>();
    List<Team> listTeams = new ArrayList<Team>();
    ArrayList<Fight> fights = new ArrayList<Fight>();
    Tournament competition = null;
    private boolean refreshTournament = true;
    private boolean refreshTeam1 = true;

    /** Creates new form NewSimpleTournament */
    public NewSimpleTournament() {
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage(KendoTournamentGenerator.getInstance().language);
        fillTournaments();
        RefreshTournament();
        fillTeam1ComboBox();
        fillTeam2ComboBox();
        fillFightingAreas();
    }

    /**
     * Translate the GUI to the selected language.
     */
    private void setLanguage(String language) {
        trans = new Translator("gui.xml");
        this.setTitle(trans.returnTag("titleNewFight", language));
        TournamentLabel.setText(trans.returnTag("TournamentLabel", language));
        Team1Label.setText(trans.returnTag("Team1Label", language));
        Team2Label.setText(trans.returnTag("Team2Label", language));
        AddButton.setText(trans.returnTag("AddButton", language));
        AcceptButton.setText(trans.returnTag("AcceptButton", language));
        DeleteButton.setText(trans.returnTag("DeleteButton", language));
        RandomButton.setText(trans.returnTag("RandomButton", language));
        FightAreaLabel.setText(trans.returnTag("FightArea", language));
        UpButton.setText(trans.returnTag("UpButton", language));
        DownButton.setText(trans.returnTag("DownButton", language));
        DeleteAllButton.setText(trans.returnTag("CleanAllButton", language));
        SortedButton.setText(trans.returnTag("SortedButton", language));
    }

    private void fillTournaments() {
        refreshTournament = false;
        try {
            TournamentComboBox.removeAllItems();
            listTournaments = KendoTournamentGenerator.getInstance().database.getAllTournaments();
            for (int i = 0; i < listTournaments.size(); i++) {
                TournamentComboBox.addItem(listTournaments.get(i).name);
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        TournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
        refreshTournament = true;
    }

    private void fillTeam1ComboBox() {
        refreshTeam1 = false;
        try {
            Team1ComboBox.removeAllItems();
            listTeams = KendoTournamentGenerator.getInstance().database.searchTeamsByTournament(TournamentComboBox.getSelectedItem().toString(), false);
            for (int i = 0; i < listTeams.size(); i++) {
                Team1ComboBox.addItem(listTeams.get(i).returnName());
            }
        } catch (NullPointerException npe) {
            //npe.printStackTrace();
        }
        refreshTeam1 = true;
    }

    private void fillTeam2ComboBox() {
        String selected = "";
        try {
            selected = Team2ComboBox.getSelectedItem().toString();
        } catch (NullPointerException npe) {
        }
        Team2ComboBox.removeAllItems();
        try {
            for (int i = 0; i < listTeams.size(); i++) {
                if (!listTeams.get(i).returnName().equals(Team1ComboBox.getSelectedItem().toString())) {
                    Team2ComboBox.addItem(listTeams.get(i).returnName());
                }
            }
            /* Mantain the last selected index */
            for (int i = 0; i < Team2ComboBox.getItemCount(); i++) {
                if (Team2ComboBox.getItemAt(i).toString().equals(selected)) {
                    Team2ComboBox.setSelectedIndex(i);
                    break;
                }
            }
        } catch (NullPointerException npe) {
            //npe.printStackTrace();
        }
    }

    private void fillFightingAreas() {
        try {
            FightAreaComboBox.removeAllItems();
            competition = KendoTournamentGenerator.getInstance().database.getTournamentByName(TournamentComboBox.getSelectedItem().toString(), true);
            for (int i = 0; i < competition.fightingAreas; i++) {
                FightAreaComboBox.addItem(KendoTournamentGenerator.getInstance().shiaijosName[i]+"");
            }
        } catch (NullPointerException npe) {
        }
    }

    private void UpFight(int index) {
        if (index >= 0 && index < fights.size()) {
            Fight f = fights.get(index);
            fights.remove(index);
            if (index > 0) {
                index--;
            }
            fights.add(index, f);
        }
    }

    private void DownFight(int index) {
        if (index >= 0 && index < fights.size()) {
            Fight f = fights.get(index);
            fights.remove(index);
            if (index < fights.size()) {
                index++;
            }
            fights.add(index, f);
        }
    }

    private void RefreshTournament() {
        try {
            if (TournamentComboBox.getItemCount() > 0) {
                competition = KendoTournamentGenerator.getInstance().database.getTournamentByName(TournamentComboBox.getSelectedItem().toString(), false);
                fights = KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(TournamentComboBox.getSelectedItem().toString());
                fillTeam1ComboBox();
                fillTeam2ComboBox();
                fillFightingAreas();
                fillFights();
            } else {
                Team1ComboBox.removeAllItems();
                Team2ComboBox.removeAllItems();
                FightAreaComboBox.removeAllItems();
                fightsModel.removeAllElements();
            }
        } catch (NullPointerException npe) {
        }
    }

    /**
     * Fill the list with the results obtained
     */
    public void fillFights() {
        fightsModel.removeAllElements();
        if (competition.mode.equals("simple")) {
            for (int i = 0; i < fights.size(); i++) {
                String text = fights.get(i).team1.returnName() + " - " + fights.get(i).team2.returnName();
                if (competition.fightingAreas > 1) {
                    text += "  (" + trans.returnTag("FightArea", KendoTournamentGenerator.getInstance().language)
                            + " " + KendoTournamentGenerator.getInstance().shiaijosName[fights.get(i).asignedFightArea] + ")";

                }
                fightsModel.addElement(text);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        NewFightPanel = new javax.swing.JPanel();
        AddButton = new javax.swing.JButton();
        Team1ComboBox = new javax.swing.JComboBox<String>();
        Team2ComboBox = new javax.swing.JComboBox<String>();
        Team1Label = new javax.swing.JLabel();
        Team2Label = new javax.swing.JLabel();
        FightAreaLabel = new javax.swing.JLabel();
        FightAreaComboBox = new javax.swing.JComboBox<String>();
        RandomButton = new javax.swing.JButton();
        SortedButton = new javax.swing.JButton();
        FightsPanel = new javax.swing.JPanel();
        FightScrollPane = new javax.swing.JScrollPane();
        FightsList = new javax.swing.JList();
        DeleteButton = new javax.swing.JButton();
        UpButton = new javax.swing.JButton();
        DownButton = new javax.swing.JButton();
        DeleteAllButton = new javax.swing.JButton();
        AcceptButton = new javax.swing.JButton();
        TournamentLabel = new javax.swing.JLabel();
        TournamentComboBox = new javax.swing.JComboBox<String>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        NewFightPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        AddButton.setText("Add");
        AddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddButtonActionPerformed(evt);
            }
        });

        Team1ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Team1ComboBoxActionPerformed(evt);
            }
        });

        Team1Label.setText("Team 1:");

        Team2Label.setText("Team 2:");

        FightAreaLabel.setText("Fight Area:");

        RandomButton.setText("Random");
        RandomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RandomButtonActionPerformed(evt);
            }
        });

        SortedButton.setText("Sorted");
        SortedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SortedButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout NewFightPanelLayout = new javax.swing.GroupLayout(NewFightPanel);
        NewFightPanel.setLayout(NewFightPanelLayout);
        NewFightPanelLayout.setHorizontalGroup(
            NewFightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NewFightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(NewFightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(NewFightPanelLayout.createSequentialGroup()
                        .addGroup(NewFightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Team2Label)
                            .addComponent(Team1Label)
                            .addComponent(FightAreaLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 107, Short.MAX_VALUE)
                        .addGroup(NewFightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(FightAreaComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Team2ComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Team1ComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(NewFightPanelLayout.createSequentialGroup()
                        .addComponent(RandomButton, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SortedButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 171, Short.MAX_VALUE)
                        .addComponent(AddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        NewFightPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AddButton, RandomButton, SortedButton});

        NewFightPanelLayout.setVerticalGroup(
            NewFightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NewFightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(NewFightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Team1ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Team1Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(NewFightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Team2ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Team2Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(NewFightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(FightAreaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FightAreaLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(NewFightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddButton)
                    .addComponent(RandomButton)
                    .addComponent(SortedButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        FightsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        FightsList.setModel(fightsModel);
        FightsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        FightScrollPane.setViewportView(FightsList);

        DeleteButton.setText("Delete");
        DeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteButtonActionPerformed(evt);
            }
        });

        UpButton.setText("Up");
        UpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpButtonActionPerformed(evt);
            }
        });

        DownButton.setText("Down");
        DownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DownButtonActionPerformed(evt);
            }
        });

        DeleteAllButton.setText("Delete All");
        DeleteAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteAllButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout FightsPanelLayout = new javax.swing.GroupLayout(FightsPanel);
        FightsPanel.setLayout(FightsPanelLayout);
        FightsPanelLayout.setHorizontalGroup(
            FightsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FightsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(FightsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, FightsPanelLayout.createSequentialGroup()
                        .addComponent(UpButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DownButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                        .addComponent(DeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DeleteAllButton))
                    .addComponent(FightScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE))
                .addContainerGap())
        );

        FightsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {DeleteAllButton, DeleteButton, DownButton, UpButton});

        FightsPanelLayout.setVerticalGroup(
            FightsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FightsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(FightScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FightsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(UpButton)
                    .addComponent(DownButton)
                    .addComponent(DeleteAllButton)
                    .addComponent(DeleteButton))
                .addContainerGap())
        );

        AcceptButton.setText("Accept");
        AcceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcceptButtonActionPerformed(evt);
            }
        });

        TournamentLabel.setText("Tournament:");

        TournamentComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TournamentComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(TournamentLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 99, Short.MAX_VALUE)
                .addComponent(TournamentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(NewFightPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(FightsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AcceptButton, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TournamentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TournamentLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(NewFightPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(FightsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(AcceptButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void Team1ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Team1ComboBoxActionPerformed
        if (refreshTeam1) {
            fillTeam2ComboBox();
        }
    }//GEN-LAST:event_Team1ComboBoxActionPerformed

    private void AddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddButtonActionPerformed
        try {
            boolean answer = false;
            if (!competition.mode.equals("simple")) {
                answer = MessageManager.question("deleteFights", "Warning!", KendoTournamentGenerator.getInstance().language);
                if (answer) {
                    fightsModel.removeAllElements();
                    competition.mode = "simple";
                    fights.clear();
                }
            }
            if (competition.mode.equals("simple") || (answer)) {
                if (Team1ComboBox.getSelectedItem().toString().length() > 0 && Team2ComboBox.getSelectedItem().toString().length() > 0) {
                    Fight fight = new Fight(KendoTournamentGenerator.getInstance().database.getTeamByName(Team1ComboBox.getSelectedItem().toString(), TournamentComboBox.getSelectedItem().toString(), true),
                            KendoTournamentGenerator.getInstance().database.getTeamByName(Team2ComboBox.getSelectedItem().toString(), TournamentComboBox.getSelectedItem().toString(), true),
                            KendoTournamentGenerator.getInstance().database.getTournamentByName(TournamentComboBox.getSelectedItem().toString(), false),
                            FightAreaComboBox.getSelectedIndex(),
                            fights.size()); //each fight is one new level to allow the order of teams to be changed anytime. 
                    int ind = FightsList.getSelectedIndex();
                    if (ind >= 0) {
                        fights.add(ind + 1, fight);
                    } else {
                        fights.add(fight);
                    }

                    fillFights();
                    if (fights.size() > 0) {
                        if (ind >= 0) {
                            FightsList.setSelectedIndex(ind + 1);
                        } else {
                            FightsList.setSelectedIndex(fightsModel.getSize() - 1);
                        }
                    }
                }
            }
        } catch (NullPointerException npe) {
        }
    }//GEN-LAST:event_AddButtonActionPerformed

    private void TournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TournamentComboBoxActionPerformed
        if (refreshTournament) {
            RefreshTournament();
            KendoTournamentGenerator.getInstance().changeLastSelectedTournament(TournamentComboBox.getSelectedItem().toString());
        }
    }//GEN-LAST:event_TournamentComboBoxActionPerformed

    private void RandomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RandomButtonActionPerformed
        try {
            boolean answer = false;
            if (!competition.mode.equals("simple") || fightsModel.size() > 0) {
                answer = MessageManager.question("deleteFights", "Warning!", KendoTournamentGenerator.getInstance().language);
                if (answer) {
                    fightsModel.removeAllElements();
                    competition.mode = "simple";
                    fights.clear();
                }
            }
            if (fightsModel.isEmpty() || (answer)) {
                //Tournament tour = KendoTournamentGenerator.getInstance().database.getTournamentByName(TournamentComboBox.getSelectedItem().toString(), false);
                fights = KendoTournamentGenerator.getInstance().fightManager.obtainRandomFights(listTeams, competition);

                int ind = FightsList.getSelectedIndex();

                fillFights();

                try {
                    FightsList.setSelectedIndex(ind);
                } catch (ArrayIndexOutOfBoundsException aiob) {
                }
            }
        } catch (NullPointerException npe) {
           KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
    }//GEN-LAST:event_RandomButtonActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        try {
            int ind = FightsList.getSelectedIndex();
            fights.remove(ind);
            //fillFights();
            fightsModel.remove(ind);
            if (ind < fightsModel.getSize()) {
                FightsList.setSelectedIndex(ind);
            } else {
                FightsList.setSelectedIndex(fightsModel.getSize() - 1);
            }
        } catch (ArrayIndexOutOfBoundsException aiob) {
        }
    }//GEN-LAST:event_DeleteButtonActionPerformed

    private void AcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AcceptButtonActionPerformed
        if (fights.size() > 0) {
            KendoTournamentGenerator.getInstance().database.storeFights(fights, true, true);
            KendoTournamentGenerator.getInstance().database.cleanLeague(competition.name, listTeams);
            competition.mode = "simple";
            KendoTournamentGenerator.getInstance().database.updateTournament(competition, false);
            this.dispose();
        } else {
            MessageManager.errorMessage("noFight", "New Fight", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
        }
    }//GEN-LAST:event_AcceptButtonActionPerformed

    private void UpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpButtonActionPerformed
        int index = FightsList.getSelectedIndex();
        UpFight(index);
        fillFights();
        if (index > 0) {
            index--;
        }
        FightsList.setSelectedIndex(index);
    }//GEN-LAST:event_UpButtonActionPerformed

    private void DownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DownButtonActionPerformed
        int index = FightsList.getSelectedIndex();
        DownFight(index);
        fillFights();
        if (index < fights.size() - 1) {
            index++;
        }
        FightsList.setSelectedIndex(index);
    }//GEN-LAST:event_DownButtonActionPerformed

    private void DeleteAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteAllButtonActionPerformed
        fights = new ArrayList<Fight>();
        fightsModel.removeAllElements();
    }//GEN-LAST:event_DeleteAllButtonActionPerformed

    private void SortedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SortedButtonActionPerformed
        try {
            //Tournament tour = KendoTournamentGenerator.getInstance().database.getTournamentByName(TournamentComboBox.getSelectedItem().toString(), false);
            fights = KendoTournamentGenerator.getInstance().fightManager.obtainSortedFights(listTeams, competition);

            int ind = FightsList.getSelectedIndex();

            if (fightsModel.size() > 0 && ShowAlert("deleteFights", "Warning!", KendoTournamentGenerator.getInstance().language)) {
                fightsModel.removeAllElements();
            }

            fillFights();

            try {
                FightsList.setSelectedIndex(ind);
            } catch (ArrayIndexOutOfBoundsException aiob) {
            }
        } catch (NullPointerException npe) {
        }
    }//GEN-LAST:event_SortedButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AcceptButton;
    private javax.swing.JButton AddButton;
    private javax.swing.JButton DeleteAllButton;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JButton DownButton;
    private javax.swing.JComboBox<String> FightAreaComboBox;
    private javax.swing.JLabel FightAreaLabel;
    private javax.swing.JScrollPane FightScrollPane;
    private javax.swing.JList FightsList;
    private javax.swing.JPanel FightsPanel;
    private javax.swing.JPanel NewFightPanel;
    private javax.swing.JButton RandomButton;
    private javax.swing.JButton SortedButton;
    private javax.swing.JComboBox<String> Team1ComboBox;
    private javax.swing.JLabel Team1Label;
    private javax.swing.JComboBox<String> Team2ComboBox;
    private javax.swing.JLabel Team2Label;
    private javax.swing.JComboBox<String> TournamentComboBox;
    private javax.swing.JLabel TournamentLabel;
    private javax.swing.JButton UpButton;
    // End of variables declaration//GEN-END:variables

    public boolean ShowAlert(String code, String title, String language) {
        JFrame frame = null;
        int n = JOptionPane.showConfirmDialog(frame, trans.returnTag(code, language), title, JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            return true;
        } else if (n == JOptionPane.NO_OPTION) {
            return false;
        } else {
            return false;
        }
    }
}
