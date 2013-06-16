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
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.gui.NewTeam;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.tournament.TournamentGroup;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import javax.swing.DefaultListModel;

public class DesignGroupWindow extends javax.swing.JFrame {

    private static final long serialVersionUID = 9161777233520978498L;
    private DefaultListModel<String> groupModel = new DefaultListModel<>();
    private TournamentGroup tournamentGroup;
    private Translator trans = null;
    private boolean refresh = true;

    public DesignGroupWindow(TournamentGroup tournamentGroup) {
        this.tournamentGroup = tournamentGroup;
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage();
        fillGroupArea();
        PassSpinner.setValue(tournamentGroup.getMaxNumberOfWinners());
        fillFightingAreas();
        refresh = false;
    }

    private void fillFightingAreas() {
        FightAreaComboBox.removeAllItems();
        try {
            for (int i = 0; i < tournamentGroup.getTournament().getFightingAreas(); i++) {
                FightAreaComboBox.addItem(KendoTournamentGenerator.getFightAreaName(i));
            }
        } catch (NullPointerException npe) {
        }
        FightAreaComboBox.setSelectedIndex(tournamentGroup.getFightArea());
    }

    private void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        this.setTitle(trans.getTranslatedText("titleDesignGroupWindow"));
        UpButton.setText(trans.getTranslatedText("UpButton"));
        DeleteButton.setText(trans.getTranslatedText("DeleteButton"));
        DownButton.setText(trans.getTranslatedText("DownButton"));
        PassLabel.setText(trans.getTranslatedText("PassLabel"));
        ArenaLabel.setText(trans.getTranslatedText("FightArea"));
        ShowButton.setText(trans.getTranslatedText("ShowTeam"));
        CloseButton.setText(trans.getTranslatedText("CloseButton"));
        TeamLabel.setText(trans.getTranslatedText("Teams"));
    }

    private void fillGroupArea() {
        groupModel.removeAllElements();
        if (tournamentGroup.getTeams() != null) {
            for (int i = 0; i < tournamentGroup.getTeams().size(); i++) {
                groupModel.addElement(tournamentGroup.getTeams().get(i).getName());
            }
        }
        if (tournamentGroup.getTeams().isEmpty()) {
            groupModel.addElement(trans.getTranslatedText("noTeams"));
            disable(true);
        } else {
            GroupList.setSelectedIndex(0);
            disable(false);
        }
    }

    private void disable(boolean value) {
        UpButton.setEnabled(!value);
        DownButton.setEnabled(!value);
        DeleteButton.setEnabled(!value);
    }

    private void showTeam() {
        int index = GroupList.getSelectedIndex();
        Team t = tournamentGroup.getTeams().get(index);
        NewTeam newTeam;
        newTeam = new NewTeam(t);
        newTeam.setVisible(true);
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
    public void addWindowCloseListener(WindowAdapter wa) {
        this.addWindowListener(wa);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        GroupScrollPane = new javax.swing.JScrollPane();
        GroupList = new javax.swing.JList();
        UpButton = new javax.swing.JButton();
        DownButton = new javax.swing.JButton();
        PassLabel = new javax.swing.JLabel();
        PassSpinner = new javax.swing.JSpinner();
        ArenaLabel = new javax.swing.JLabel();
        FightAreaComboBox = new javax.swing.JComboBox();
        DeleteButton = new javax.swing.JButton();
        ShowButton = new javax.swing.JButton();
        CloseButton = new javax.swing.JButton();
        TeamLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        GroupList.setModel(groupModel);
        GroupList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        GroupList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                GroupListMouseClicked(evt);
            }
        });
        GroupScrollPane.setViewportView(GroupList);

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

        PassLabel.setText("Pass:");

        PassSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                PassSpinnerStateChanged(evt);
            }
        });

        ArenaLabel.setText("Arena:");

        FightAreaComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FightAreaComboBoxActionPerformed(evt);
            }
        });

        DeleteButton.setText("Delete");
        DeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteButtonActionPerformed(evt);
            }
        });

        ShowButton.setText("Show");
        ShowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ShowButtonActionPerformed(evt);
            }
        });

        CloseButton.setText("Close");
        CloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseButtonActionPerformed(evt);
            }
        });

        TeamLabel.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        TeamLabel.setText("Team order:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ShowButton)
                            .addComponent(DeleteButton)
                            .addComponent(UpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(DownButton, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(GroupScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE))
                    .addComponent(CloseButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(TeamLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 275, Short.MAX_VALUE)
                        .addComponent(ArenaLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(FightAreaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PassLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PassSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {DeleteButton, DownButton, ShowButton, UpButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(PassSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PassLabel)
                    .addComponent(FightAreaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ArenaLabel)
                    .addComponent(TeamLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(UpButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DownButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ShowButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DeleteButton))
                    .addComponent(GroupScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CloseButton)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {DeleteButton, DownButton, ShowButton, UpButton});

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void UpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpButtonActionPerformed
        int index = GroupList.getSelectedIndex();
        Team t = tournamentGroup.getTeams().remove(index);
        if (index > 0) {
            index--;
        }
        tournamentGroup.getTeams().add(index, t);
        fillGroupArea();
        GroupList.setSelectedIndex(index);
    }//GEN-LAST:event_UpButtonActionPerformed

    private void DownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DownButtonActionPerformed
        int index = GroupList.getSelectedIndex();
        Team t = tournamentGroup.getTeams().remove(index);
        if (index < tournamentGroup.getTeams().size()) {
            index++;
        }
        tournamentGroup.getTeams().add(index, t);
        fillGroupArea();
        GroupList.setSelectedIndex(index);
    }//GEN-LAST:event_DownButtonActionPerformed

    private void PassSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_PassSpinnerStateChanged
        if ((Integer) PassSpinner.getValue() < 0) {
            PassSpinner.setValue(0);
        }
        /*
         * if (dg.getTeams().isEmpty()) { if ((Integer) PassSpinner.getValue() >
         * dg.getMaxNumberOfWinners()) {
         * PassSpinner.setValue(dg.getMaxNumberOfWinners()); } } else { //More
         * than one team. if((Integer) PassSpinner.getValue() >
         * dg.getTeams().size()-1){ PassSpinner.setValue(dg.getTeams().size()-1); }
        }
         */
        if (tournamentGroup.getTeams().size() > 1) {
            if ((Integer) PassSpinner.getValue() >= tournamentGroup.getTeams().size()) {
                PassSpinner.setValue(tournamentGroup.getTeams().size() - 1);
            }
        }
        tournamentGroup.setMaxNumberOfWinners((Integer) PassSpinner.getValue());
    }//GEN-LAST:event_PassSpinnerStateChanged

    private void FightAreaComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FightAreaComboBoxActionPerformed
        if (!refresh) {
            tournamentGroup.setFightArea(FightAreaComboBox.getSelectedIndex());
        }
    }//GEN-LAST:event_FightAreaComboBoxActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        if (AlertManager.questionMessage("questionRemoveTeam", "Warning!")) {
            int index = GroupList.getSelectedIndex();
            tournamentGroup.getTeams().remove(index);
            fillGroupArea();
            index--;
            if (index < tournamentGroup.getTeams().size() && index >= 0) {
                GroupList.setSelectedIndex(index);
            }
        }
    }//GEN-LAST:event_DeleteButtonActionPerformed

    private void ShowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ShowButtonActionPerformed
        showTeam();
    }//GEN-LAST:event_ShowButtonActionPerformed

    private void GroupListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_GroupListMouseClicked
        if (evt.getClickCount() == 2) {
            showTeam();
        }
    }//GEN-LAST:event_GroupListMouseClicked

    private void CloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_CloseButtonActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.toFront();
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ArenaLabel;
    private javax.swing.JButton CloseButton;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JButton DownButton;
    private javax.swing.JComboBox FightAreaComboBox;
    private javax.swing.JList GroupList;
    private javax.swing.JScrollPane GroupScrollPane;
    private javax.swing.JLabel PassLabel;
    private javax.swing.JSpinner PassSpinner;
    private javax.swing.JButton ShowButton;
    private javax.swing.JLabel TeamLabel;
    private javax.swing.JButton UpButton;
    // End of variables declaration//GEN-END:variables
}
