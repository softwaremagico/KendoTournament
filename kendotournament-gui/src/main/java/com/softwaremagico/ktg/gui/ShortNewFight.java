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

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.TeamPool;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jorge
 */
public class ShortNewFight extends javax.swing.JFrame {

    Translator trans = null;
    List<Tournament> listTournaments = new ArrayList<>();
    List<Team> listTeams = new ArrayList<>();
    List<Fight> fights = new ArrayList<>();
    Tournament tournament;
    int selectedArena;

    /**
     * Creates new form ShortNewFight
     */
    public ShortNewFight(Tournament tournament, int tmp_arena) {
        this.tournament = tournament;
        selectedArena = tmp_arena;
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage();
        try {
            listTeams = TeamPool.getInstance().get(tournament);
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
        fillTeam1ComboBox();
        fillTeam2ComboBox();
        fillFightingAreas();
    }

    public final void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        this.setTitle(trans.getTranslatedText("titleShortFight"));
        Team1Label.setText(trans.getTranslatedText("Team1Label"));
        Team2Label.setText(trans.getTranslatedText("Team2Label"));
        AddButton.setText(trans.getTranslatedText("AddButton"));
        CancelButton.setText(trans.getTranslatedText("CancelButton"));
        FightAreaLabel.setText(trans.getTranslatedText("FightArea"));
    }

    private void fillTeam1ComboBox() {
        try {
            Team1ComboBox.removeAllItems();
            for (int i = 0; i < listTeams.size(); i++) {
                Team1ComboBox.addItem(listTeams.get(i).getName());
            }
        } catch (NullPointerException npe) {
        }
    }

    private void fillTeam2ComboBox() {
        Team2ComboBox.removeAllItems();
        try {
            for (int i = 0; i < listTeams.size(); i++) {
                if (!listTeams.get(i).getName().equals(Team1ComboBox.getSelectedItem().toString())) {
                    Team2ComboBox.addItem(listTeams.get(i).getName());
                }
            }
        } catch (NullPointerException npe) {
        }
    }

    private void fillFightingAreas() {
        FightAreaComboBox.removeAllItems();
        try {
            for (int i = 0; i < tournament.getFightingAreas(); i++) {
                FightAreaComboBox.addItem(KendoTournamentGenerator.getFightAreaName(i));
            }
            FightAreaComboBox.setSelectedIndex(selectedArena);
        } catch (NullPointerException npe) {
        }
    }

    public Team getTeam1() {
        try {
            return listTeams.get(Team1ComboBox.getSelectedIndex());
        } catch (ArrayIndexOutOfBoundsException aiob) {
            return null;
        }
    }

    public Team getTeam2() {
        try {
            if (Team2ComboBox.getSelectedItem().toString().equals(listTeams.get(Team2ComboBox.getSelectedIndex()).getName())) {
                return listTeams.get(Team2ComboBox.getSelectedIndex());
            } else {
                return listTeams.get(Team2ComboBox.getSelectedIndex() + 1);
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException aiob) {
            return null;
        }
    }

    public int getArena() {
        return FightAreaComboBox.getSelectedIndex();
    }

    public Tournament getTournament() {
        return tournament;
    }

    public boolean filled() {
        return listTeams.size() > 0;
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
    public void addFightListener(ActionListener al) {
        AddButton.addActionListener(al);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        NewFightPanel = new javax.swing.JPanel();
        AddButton = new javax.swing.JButton();
        Team1ComboBox = new javax.swing.JComboBox();
        Team2ComboBox = new javax.swing.JComboBox();
        Team1Label = new javax.swing.JLabel();
        Team2Label = new javax.swing.JLabel();
        FightAreaLabel = new javax.swing.JLabel();
        FightAreaComboBox = new javax.swing.JComboBox();
        CancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Fights");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        NewFightPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        AddButton.setText("Add");

        Team1ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Team1ComboBoxActionPerformed(evt);
            }
        });

        Team1Label.setText("Team 1:");

        Team2Label.setText("Team 2:");

        FightAreaLabel.setText("Fight Area:");

        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                        .addGroup(NewFightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(FightAreaComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Team2ComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Team1ComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, NewFightPanelLayout.createSequentialGroup()
                        .addComponent(AddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CancelButton)))
                .addContainerGap())
        );

        NewFightPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AddButton, CancelButton});

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
                    .addComponent(CancelButton)
                    .addComponent(AddButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(NewFightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(NewFightPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void Team1ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Team1ComboBoxActionPerformed
        fillTeam2ComboBox();
}//GEN-LAST:event_Team1ComboBoxActionPerformed

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_CancelButtonActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.toFront();
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddButton;
    private javax.swing.JButton CancelButton;
    private javax.swing.JComboBox FightAreaComboBox;
    private javax.swing.JLabel FightAreaLabel;
    private javax.swing.JPanel NewFightPanel;
    private javax.swing.JComboBox Team1ComboBox;
    private javax.swing.JLabel Team1Label;
    private javax.swing.JComboBox Team2ComboBox;
    private javax.swing.JLabel Team2Label;
    // End of variables declaration//GEN-END:variables
}
