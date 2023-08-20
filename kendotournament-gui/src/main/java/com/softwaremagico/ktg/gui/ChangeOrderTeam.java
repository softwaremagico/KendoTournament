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

import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;

import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.language.ITranslator;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.persistence.TeamPool;
import com.softwaremagico.ktg.persistence.TournamentPool;

public class ChangeOrderTeam extends javax.swing.JFrame {
	private static final long serialVersionUID = 50457036957789013L;
	private ITranslator trans = null;
    private List<Tournament> listTournaments = new ArrayList<>();
    private List<Team> teams = new ArrayList<>();
    private DefaultListModel<Team> teamsModel = new DefaultListModel<>();
    protected boolean refreshTournament = true;
    private int selectedFightArea = 0;

    /**
     * Creates new form ChangeOrderTeam
     */
    public ChangeOrderTeam(int selectedFightArea) {
        this.selectedFightArea = selectedFightArea;
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage();
        fillTournaments();
        update();
    }

    /**
     * Translate the GUI to the selected language.
     */
    public final void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        this.setTitle(trans.getTranslatedText("TitleOrderTeam"));
        TournamentLabel.setText(trans.getTranslatedText("TournamentLabel"));
        SelectButton.setText(trans.getTranslatedText("SelectButton"));
        CloseButton.setText(trans.getTranslatedText("CloseButton"));
        AvailableTeamLabel.setText(trans.getTranslatedText("AvailableTeam"));
    }

    private void fillTournaments() {
        refreshTournament = false;
        try {
            listTournaments = TournamentPool.getInstance().getSorted();
            for (int i = 0; i < listTournaments.size(); i++) {
                TournamentComboBox.addItem(listTournaments.get(i));
            }
        } catch (NullPointerException npe) {
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
        TournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
        refreshTournament = true;
    }

    private Tournament getSelectedTournament() {
        return (Tournament) TournamentComboBox.getSelectedItem();
    }

    private void update() {
        try {
            int level = FightPool.getInstance().getLastLevelUsed(getSelectedTournament());
            KendoTournamentGenerator.getInstance().setLastSelectedTournament(getSelectedTournament().toString());
            teams = TeamPool.getInstance().get(getSelectedTournament(), level);
            Collections.sort(teams);
            fillTeams();
        } catch (NullPointerException npe) {
            AlertManager.errorMessage(this.getClass().getName(), "noTournament", "Panel");
            dispose();
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
    }

    private void fillTeams() {
        teamsModel.removeAllElements();
        for (int i = 0; i < teams.size(); i++) {
            teamsModel.addElement(teams.get(i));
        }
    }

    private Team getSelectedTeam() {
        if (TeamList.getSelectedIndex() >= 0) {
            return teams.get(TeamList.getSelectedIndex());
        }
        return null;
    }

    private void openOrderTeamWindow() {
        Team team = getSelectedTeam();
        if (team != null) {
            try {
                if (FightPool.getInstance().isInThisFightArea(getSelectedTournament(), team, selectedFightArea, FightPool.getInstance().getMaxLevel(getSelectedTournament())) || 
                        AlertManager.questionMessage("fightInOtherArea", "????")) {
                    try {
                        OrderTeam orderTeam;
                        Integer fightIndex = FightPool.getInstance().getCurrentFightIndex(getSelectedTournament());
                        if (fightIndex == null) {
                            fightIndex = FightPool.getInstance().get(getSelectedTournament()).size() - 1;
                        }
                        orderTeam = new OrderTeam(getSelectedTournament(), fightIndex);
                        orderTeam.updateOrderWindow(team);
                        orderTeam.setVisible(true);
                        orderTeam.toFront();
                    } catch (SQLException ex) {
                    }
                }
            } catch (SQLException ex) {
                AlertManager.errorMessage(ChangeOrderTeam.class.getName(), ex);
            }
        }
    }

    public void addCloseListener(ActionListener al) {
        CloseButton.addActionListener(al);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TournamentLabel = new javax.swing.JLabel();
        TournamentComboBox = new javax.swing.JComboBox();
        TeamPanel = new javax.swing.JPanel();
        TeamScrollPane = new javax.swing.JScrollPane();
        TeamList = new javax.swing.JList();
        SelectButton = new javax.swing.JButton();
        CloseButton = new javax.swing.JButton();
        AvailableTeamLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Order");

        TournamentLabel.setText("Tournament");

        TournamentComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TournamentComboBoxActionPerformed(evt);
            }
        });

        TeamList.setModel(teamsModel);
        TeamList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TeamListMouseClicked(evt);
            }
        });
        TeamScrollPane.setViewportView(TeamList);

        SelectButton.setText("Select");
        SelectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SelectButtonActionPerformed(evt);
            }
        });

        CloseButton.setText("Close");

        AvailableTeamLabel.setText("Available Teams:");

        javax.swing.GroupLayout TeamPanelLayout = new javax.swing.GroupLayout(TeamPanel);
        TeamPanel.setLayout(TeamPanelLayout);
        TeamPanelLayout.setHorizontalGroup(
            TeamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TeamPanelLayout.createSequentialGroup()
                .addComponent(SelectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 130, Short.MAX_VALUE)
                .addComponent(CloseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(TeamScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
            .addGroup(TeamPanelLayout.createSequentialGroup()
                .addComponent(AvailableTeamLabel)
                .addContainerGap())
        );

        TeamPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {CloseButton, SelectButton});

        TeamPanelLayout.setVerticalGroup(
            TeamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, TeamPanelLayout.createSequentialGroup()
                .addComponent(AvailableTeamLabel)
                .addGap(7, 7, 7)
                .addComponent(TeamScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(TeamPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SelectButton)
                    .addComponent(CloseButton)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(TeamPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(TournamentLabel)
                        .addGap(25, 25, 25)
                        .addComponent(TournamentComboBox, 0, 264, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(TournamentLabel)
                    .addComponent(TournamentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TeamPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void TournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TournamentComboBoxActionPerformed
    if (refreshTournament) {
        update();
    }
}//GEN-LAST:event_TournamentComboBoxActionPerformed

private void SelectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SelectButtonActionPerformed
    openOrderTeamWindow();
}//GEN-LAST:event_SelectButtonActionPerformed

private void TeamListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TeamListMouseClicked
    if (evt.getClickCount() == 2) {
        openOrderTeamWindow();
    }
}//GEN-LAST:event_TeamListMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AvailableTeamLabel;
    private javax.swing.JButton CloseButton;
    private javax.swing.JButton SelectButton;
    private javax.swing.JList TeamList;
    private javax.swing.JPanel TeamPanel;
    private javax.swing.JScrollPane TeamScrollPane;
    protected javax.swing.JComboBox TournamentComboBox;
    private javax.swing.JLabel TournamentLabel;
    // End of variables declaration//GEN-END:variables
}
