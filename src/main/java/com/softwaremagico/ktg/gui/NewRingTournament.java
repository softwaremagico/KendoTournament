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
 *   
 */
package com.softwaremagico.ktg.gui;

import com.softwaremagico.ktg.*;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.DefaultListModel;

/**
 *
 * @author jorge
 */
public class NewRingTournament extends javax.swing.JFrame {

    Translator trans = null;
    DefaultListModel<String> teamsModel = new DefaultListModel<>();
    List<Tournament> listTournaments = new ArrayList<>();
    List<Team> listTeams = new ArrayList<>();
    List<Team> teams = new ArrayList<>();
    Tournament competition = null;
    private boolean refreshTournament = true;

    /** Creates new form NewRingTournament */
    public NewRingTournament() {
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage();
        fillTournaments();
        RefreshTournament();
        listTeams = KendoTournamentGenerator.getInstance().database.searchTeamsByTournament(TournamentComboBox.getSelectedItem().toString(), false);
        fillTeam1ComboBox();
    }

    /**
     * Translate the GUI to the selected language.
     */
    private void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        this.setTitle(trans.returnTag("titleNewFight"));
        TournamentLabel.setText(trans.returnTag("TournamentLabel"));
        Team1Label.setText(trans.returnTag("Team1Label"));
        AddButton.setText(trans.returnTag("AddButton"));
        AcceptButton.setText(trans.returnTag("AcceptButton"));
        DeleteButton.setText(trans.returnTag("DeleteButton"));
        RandomButton.setText(trans.returnTag("RandomButton"));
        UpButton.setText(trans.returnTag("UpButton"));
        DownButton.setText(trans.returnTag("DownButton"));
        DeleteAllButton.setText(trans.returnTag("CleanAllButton"));
        AvoidRepetitionCheckBox.setText(trans.returnTag("AvoidRepetitions"));
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

    private List<Team> remainingTeams() {
        List<Team> remainingTeams = new ArrayList<>();
        remainingTeams.addAll(listTeams);
        remainingTeams.removeAll(teams);
        return remainingTeams;
    }

    private void fillTeam1ComboBox() {
        try {
            Team1ComboBox.removeAllItems();
            for (int i = 0; i < remainingTeams().size(); i++) {
                Team1ComboBox.addItem(remainingTeams().get(i).returnName());
            }
        } catch (NullPointerException npe) {
        }
    }

    private void UpTeam(int index) {
        if (index >= 0 && index < teams.size()) {
            Team t = teams.get(index);
            teams.remove(index);
            if (index > 0) {
                index--;
            }
            teams.add(index, t);
        }
    }

    private void DownTeam(int index) {
        if (index >= 0 && index < teams.size()) {
            Team t = teams.get(index);
            teams.remove(index);
            if (index < teams.size()) {
                index++;
            }
            teams.add(index, t);
        }
    }

    private void RefreshTournament() {
        try {
            if (TournamentComboBox.getItemCount() > 0) {
                competition = KendoTournamentGenerator.getInstance().database.getTournamentByName(TournamentComboBox.getSelectedItem().toString(), false);
                fillTeam1ComboBox();
                fillTeams();
            } else {
                Team1ComboBox.removeAllItems();
                teamsModel.removeAllElements();
            }
        } catch (NullPointerException npe) {
        }
    }

    /**
     * Fill the list with the results obtained
     */
    public void fillTeams() {
        teamsModel.removeAllElements();
        for (int i = 0; i < teams.size(); i++) {
            String text = teams.get(i).returnName();
            teamsModel.addElement(text);
        }
    }

    private List<Team> obtainRandomTeams() {
        List<Team> results = new ArrayList<>();
        List<Team> teamsListed = new ArrayList<>();
        Random rnd = new Random();

        teamsListed.addAll(listTeams);

        while (teamsListed.size() > 0) {
            int t = rnd.nextInt(teamsListed.size());
            results.add(teamsListed.get(t));
            teamsListed.remove(t);
        }

        return results;
    }

    private ArrayList<Fight> obtainRingFightsWithoutRepetition() {
        ArrayList<Fight> fights = new ArrayList<>();
        for (int i = 0; i < teams.size(); i++) {
            int level = i;
            for (int j = i + 1; j < teams.size(); j++) {
                fights.add(new Fight(teams.get(i), teams.get(j % teams.size()), competition, 0, level));
            }
        }
        return fights;
    }

    private ArrayList<Fight> obtainRingFightsWithRepetition() {
        ArrayList<Fight> fights = new ArrayList<>();
        for (int i = 0; i < teams.size(); i++) {
            int level = i;
            for (int j = i + 1; j < teams.size(); j++) { //First, fightManager with teams that not have played the ring.
                fights.add(new Fight(teams.get(i), teams.get(j % teams.size()), competition, 0, level));
            }
            for (int j = 0; j < i ; j++) { //Next, fightManager with exhausted teams.
                fights.add(new Fight(teams.get(i), teams.get(j % teams.size()), competition, 0, level));
            }
        }
        return fights;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TournamentComboBox = new javax.swing.JComboBox<String>();
        TournamentLabel = new javax.swing.JLabel();
        NewFightPanel = new javax.swing.JPanel();
        AddButton = new javax.swing.JButton();
        Team1ComboBox = new javax.swing.JComboBox<String>();
        Team1Label = new javax.swing.JLabel();
        RandomButton = new javax.swing.JButton();
        AvoidRepetitionCheckBox = new javax.swing.JCheckBox();
        AcceptButton = new javax.swing.JButton();
        FightsPanel = new javax.swing.JPanel();
        FightScrollPane = new javax.swing.JScrollPane();
        TeamList = new javax.swing.JList();
        DeleteButton = new javax.swing.JButton();
        UpButton = new javax.swing.JButton();
        DownButton = new javax.swing.JButton();
        DeleteAllButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        TournamentComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TournamentComboBoxActionPerformed(evt);
            }
        });

        TournamentLabel.setText("Tournament:");

        NewFightPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        AddButton.setText("Add");
        AddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddButtonActionPerformed(evt);
            }
        });

        Team1Label.setText("Team 1:");

        RandomButton.setText("Random");
        RandomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RandomButtonActionPerformed(evt);
            }
        });

        AvoidRepetitionCheckBox.setText("Repetitions");

        javax.swing.GroupLayout NewFightPanelLayout = new javax.swing.GroupLayout(NewFightPanel);
        NewFightPanel.setLayout(NewFightPanelLayout);
        NewFightPanelLayout.setHorizontalGroup(
            NewFightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NewFightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(NewFightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Team1Label)
                    .addComponent(RandomButton, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(NewFightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(NewFightPanelLayout.createSequentialGroup()
                        .addComponent(AvoidRepetitionCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(Team1ComboBox, 0, 339, Short.MAX_VALUE))
                .addContainerGap())
        );
        NewFightPanelLayout.setVerticalGroup(
            NewFightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NewFightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(NewFightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(Team1ComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Team1Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(NewFightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RandomButton)
                    .addComponent(AddButton)
                    .addComponent(AvoidRepetitionCheckBox))
                .addContainerGap())
        );

        AcceptButton.setText("Accept");
        AcceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcceptButtonActionPerformed(evt);
            }
        });

        FightsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        TeamList.setModel(teamsModel);
        TeamList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        FightScrollPane.setViewportView(TeamList);

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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                        .addComponent(DeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DeleteAllButton))
                    .addComponent(FightScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE))
                .addContainerGap())
        );

        FightsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {DeleteAllButton, DeleteButton, DownButton, UpButton});

        FightsPanelLayout.setVerticalGroup(
            FightsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FightsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(FightScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(FightsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(UpButton)
                    .addComponent(DownButton)
                    .addComponent(DeleteAllButton)
                    .addComponent(DeleteButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(TournamentLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TournamentComboBox, 0, 327, Short.MAX_VALUE)
                .addGap(23, 23, 23))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(FightsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(NewFightPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FightsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(AcceptButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TournamentComboBoxActionPerformed
        if (refreshTournament) {
            RefreshTournament();
            KendoTournamentGenerator.getInstance().changeLastSelectedTournament(TournamentComboBox.getSelectedItem().toString());
        }
}//GEN-LAST:event_TournamentComboBoxActionPerformed

    private void AddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddButtonActionPerformed
        try {
            if (Team1ComboBox.getSelectedItem().toString().length() > 0) {
                Team team = remainingTeams().get(Team1ComboBox.getSelectedIndex());

                if (!teams.contains(team)) {
                    int ind = TeamList.getSelectedIndex();
                    if (ind >= 0) {
                        teams.add(ind + 1, team);
                    } else {
                        teams.add(team);
                    }

                    fillTeam1ComboBox();
                    fillTeams();
                    if (teams.size() > 0) {
                        if (ind >= 0) {
                            TeamList.setSelectedIndex(ind + 1);
                        } else {
                            TeamList.setSelectedIndex(teamsModel.getSize() - 1);
                        }
                    }
                }
            }
        } catch (NullPointerException npe) {
        }
}//GEN-LAST:event_AddButtonActionPerformed

    private void RandomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RandomButtonActionPerformed
        try {
            int ind = TeamList.getSelectedIndex();

            if (listTeams.size() > 0 && MessageManager.questionMessage("deleteFights", "Warning!")) {
                teams = obtainRandomTeams();
                teamsModel.removeAllElements();
                fillTeams();
                fillTeam1ComboBox();
            }
            try {
                TeamList.setSelectedIndex(ind);
            } catch (ArrayIndexOutOfBoundsException aiob) {
            }
        } catch (NullPointerException npe) {
        }
}//GEN-LAST:event_RandomButtonActionPerformed

    private void AcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AcceptButtonActionPerformed
        if (teams.size() > 0) {
            if (!AvoidRepetitionCheckBox.isSelected()) {
                KendoTournamentGenerator.getInstance().database.storeFights(obtainRingFightsWithRepetition(), true, true);
            } else {
                KendoTournamentGenerator.getInstance().database.storeFights(obtainRingFightsWithoutRepetition(), true, true);
            }
            KendoTournamentGenerator.getInstance().database.deleteGroupsOfTournament(competition.name, listTeams);
            competition.mode = "simple";
            KendoTournamentGenerator.getInstance().database.updateTournament(competition, false);
            this.dispose();
        } else {
            MessageManager.errorMessage("noTeam", "New Fight");
        }
}//GEN-LAST:event_AcceptButtonActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        try {
            int ind = TeamList.getSelectedIndex();
            teams.remove(ind);
            teamsModel.remove(ind);
            if (ind < teamsModel.getSize()) {
                TeamList.setSelectedIndex(ind);
            } else {
                TeamList.setSelectedIndex(teamsModel.getSize() - 1);
            }
            fillTeam1ComboBox();
        } catch (ArrayIndexOutOfBoundsException aiob) {
        }
}//GEN-LAST:event_DeleteButtonActionPerformed

    private void UpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpButtonActionPerformed
        int index = TeamList.getSelectedIndex();
        UpTeam(index);
        fillTeams();
        if (index > 0) {
            index--;
        }
        TeamList.setSelectedIndex(index);
}//GEN-LAST:event_UpButtonActionPerformed

    private void DownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DownButtonActionPerformed
        int index = TeamList.getSelectedIndex();
        DownTeam(index);
        fillTeams();
        if (index < teams.size() - 1) {
            index++;
        }
        TeamList.setSelectedIndex(index);
}//GEN-LAST:event_DownButtonActionPerformed

    private void DeleteAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteAllButtonActionPerformed
        teams = new ArrayList<>();
        teamsModel.removeAllElements();
        fillTeam1ComboBox();
}//GEN-LAST:event_DeleteAllButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AcceptButton;
    private javax.swing.JButton AddButton;
    private javax.swing.JCheckBox AvoidRepetitionCheckBox;
    private javax.swing.JButton DeleteAllButton;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JButton DownButton;
    private javax.swing.JScrollPane FightScrollPane;
    private javax.swing.JPanel FightsPanel;
    private javax.swing.JPanel NewFightPanel;
    private javax.swing.JButton RandomButton;
    private javax.swing.JComboBox<String> Team1ComboBox;
    private javax.swing.JLabel Team1Label;
    private javax.swing.JList TeamList;
    private javax.swing.JComboBox<String> TournamentComboBox;
    private javax.swing.JLabel TournamentLabel;
    private javax.swing.JButton UpButton;
    // End of variables declaration//GEN-END:variables


}
