/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 *  This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2012 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *  Created on 22-ene-2011, 11:33:29
 */
package com.softwaremagico.ktg.gui;

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.language.Translator;
import java.awt.Toolkit;
import java.util.List;

/**
 *
 * @author Jorge
 */
public class ChooseScoreGUI extends javax.swing.JFrame {

    private List<Tournament> listTournaments = null;
    Translator trans = null;
    private boolean refreshing = false;

    /**
     * Creates new form ChooseScoreGUI
     */
    public ChooseScoreGUI() {
        refreshing = true;
        listTournaments = KendoTournamentGenerator.getInstance().database.getAllTournaments();
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        fillTournaments();
        selectStyle();
        setLanguage();
        refreshing = false;
    }

    private void setLanguage() {
        trans = new Translator("gui.xml");
        this.setTitle(trans.returnTag("Hits"));
        ClassicRadioButton.setText(trans.returnTag("ClassicRadioButton"));
        EuropeanRadioButton.setText(trans.returnTag("EuropeanRadioButton"));
        CustomRadioButton.setText(trans.returnTag("CustomRadioButton"));
        WinLabel.setText(trans.returnTag("WinTag"));
        DrawLabel.setText(trans.returnTag("DrawTag"));
        CloseButton.setText(trans.returnTag("CloseButton"));
        TournamentLabel.setText(trans.returnTag("TournamentLabel"));
        EuropeanUndrawLabel.setText(trans.returnTag("EuropeanUndrawLabel"));
    }

    private void selectStyle() {
        refreshing = true;
        try {
            switch (listTournaments.get(TournamentComboBox.getSelectedIndex()).getChoosedScore()) {
                case "European":
                    EuropeanRadioButton.setSelected(true);
                    break;
                case "Custom":
                    CustomRadioButton.setSelected(true);
                    float tmp_draw = listTournaments.get(TournamentComboBox.getSelectedIndex()).getScoreForDraw();
                    WinSpinner.setValue(listTournaments.get(TournamentComboBox.getSelectedIndex()).getScoreForWin());
                    DrawSpinner.setValue(tmp_draw);
                    break;
                default:
                    ClassicRadioButton.setSelected(true);
                    break;
            }
        } catch (NullPointerException npe) {
        }
        refreshing = false;
    }

    private void fillTournaments() {
        refreshing = true;
        try {
            for (int i = 0; i < listTournaments.size(); i++) {
                TournamentComboBox.addItem(listTournaments.get(i).name);
            }
            TournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        refreshing = false;
        //fillFightsPanel();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ScoreGroup = new javax.swing.ButtonGroup();
        ScorePanel = new javax.swing.JPanel();
        ClassicRadioButton = new javax.swing.JRadioButton();
        EuropeanRadioButton = new javax.swing.JRadioButton();
        CustomRadioButton = new javax.swing.JRadioButton();
        WinSpinner = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        WinLabel = new javax.swing.JLabel();
        DrawLabel = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        EuropeanUndrawLabel = new javax.swing.JLabel();
        DrawSpinner = new javax.swing.JSpinner();
        CloseButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        TournamentComboBox = new javax.swing.JComboBox<String>();
        TournamentLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Score");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        ScorePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        ScoreGroup.add(ClassicRadioButton);
        ClassicRadioButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ClassicRadioButton.setSelected(true);
        ClassicRadioButton.setText("Classic");
        ClassicRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClassicRadioButtonActionPerformed(evt);
            }
        });

        ScoreGroup.add(EuropeanRadioButton);
        EuropeanRadioButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        EuropeanRadioButton.setText("European");
        EuropeanRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EuropeanRadioButtonActionPerformed(evt);
            }
        });

        ScoreGroup.add(CustomRadioButton);
        CustomRadioButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        CustomRadioButton.setText("Custom");
        CustomRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CustomRadioButtonActionPerformed(evt);
            }
        });

        WinSpinner.setValue(1);
        WinSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WinSpinnerStateChanged(evt);
            }
        });

        jLabel7.setText("1");

        jLabel4.setText("1");

        WinLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        WinLabel.setText("Winned");

        DrawLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        DrawLabel.setText("Draw");

        jLabel5.setText("0");

        EuropeanUndrawLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        EuropeanUndrawLabel.setText("For Undraw");

        DrawSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                DrawSpinnerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout ScorePanelLayout = new javax.swing.GroupLayout(ScorePanel);
        ScorePanel.setLayout(ScorePanelLayout);
        ScorePanelLayout.setHorizontalGroup(
            ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ScorePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CustomRadioButton)
                    .addComponent(ClassicRadioButton)
                    .addComponent(EuropeanRadioButton))
                .addGap(46, 46, 46)
                .addGroup(ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(WinLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(WinSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(94, 94, 94)
                .addGroup(ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DrawLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(EuropeanUndrawLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                    .addComponent(DrawSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        ScorePanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {DrawSpinner, WinSpinner});

        ScorePanelLayout.setVerticalGroup(
            ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ScorePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(WinLabel)
                    .addComponent(DrawLabel))
                .addGap(26, 26, 26)
                .addGroup(ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(ClassicRadioButton)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGap(37, 37, 37)
                .addGroup(ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(EuropeanRadioButton)
                    .addComponent(jLabel7)
                    .addComponent(EuropeanUndrawLabel))
                .addGap(36, 36, 36)
                .addGroup(ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(CustomRadioButton)
                    .addComponent(WinSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DrawSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        CloseButton.setText("Close");
        CloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseButtonActionPerformed(evt);
            }
        });

        TournamentComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TournamentComboBoxActionPerformed(evt);
            }
        });

        TournamentLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        TournamentLabel.setText("Tournament:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TournamentLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TournamentComboBox, 0, 367, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TournamentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TournamentLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ScorePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(CloseButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ScorePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CloseButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseButtonActionPerformed
        for (int i = 0; i < listTournaments.size(); i++) { //Update all changes
            KendoTournamentGenerator.getInstance().database.updateTournament(listTournaments.get(i), false);
        }
        this.dispose();
    }//GEN-LAST:event_CloseButtonActionPerformed

    private void ClassicRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClassicRadioButtonActionPerformed
        if (!refreshing) {
            listTournaments.get(TournamentComboBox.getSelectedIndex()).changeScoreOptions("Classic", 1, 0);
        }
    }//GEN-LAST:event_ClassicRadioButtonActionPerformed

    private void EuropeanRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EuropeanRadioButtonActionPerformed
        if (!refreshing) {
            listTournaments.get(TournamentComboBox.getSelectedIndex()).changeScoreOptions("European", 1, (float) 0.001);
        }
    }//GEN-LAST:event_EuropeanRadioButtonActionPerformed

    private void CustomRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CustomRadioButtonActionPerformed
        if (!refreshing) {
            listTournaments.get(TournamentComboBox.getSelectedIndex()).changeScoreOptions("Custom", (Integer) WinSpinner.getValue(), (Integer) DrawSpinner.getValue());
        }
    }//GEN-LAST:event_CustomRadioButtonActionPerformed

    private void WinSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_WinSpinnerStateChanged
        if (!refreshing) {
            if ((Integer) WinSpinner.getValue() < 1) {
                WinSpinner.setValue(1);
            }
            listTournaments.get(TournamentComboBox.getSelectedIndex()).changeScoreOptions("Custom", (Integer) WinSpinner.getValue(), (Integer) DrawSpinner.getValue());
        }
    }//GEN-LAST:event_WinSpinnerStateChanged

    private void DrawSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_DrawSpinnerStateChanged
        if (!refreshing) {
            if ((Integer) DrawSpinner.getValue() < 0) {
                DrawSpinner.setValue(0);
            }
            if ((Integer) DrawSpinner.getValue() >= (Integer) WinSpinner.getValue()) {
                DrawSpinner.setValue((Integer) WinSpinner.getValue() - 1);
            }
            listTournaments.get(TournamentComboBox.getSelectedIndex()).changeScoreOptions("Custom", (Integer) WinSpinner.getValue(), (Integer) DrawSpinner.getValue());
        }
    }//GEN-LAST:event_DrawSpinnerStateChanged

    private void TournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TournamentComboBoxActionPerformed
        selectStyle();
    }//GEN-LAST:event_TournamentComboBoxActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.toFront();
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton ClassicRadioButton;
    private javax.swing.JButton CloseButton;
    private javax.swing.JRadioButton CustomRadioButton;
    private javax.swing.JLabel DrawLabel;
    private javax.swing.JSpinner DrawSpinner;
    private javax.swing.JRadioButton EuropeanRadioButton;
    private javax.swing.JLabel EuropeanUndrawLabel;
    private javax.swing.ButtonGroup ScoreGroup;
    private javax.swing.JPanel ScorePanel;
    private javax.swing.JComboBox<String> TournamentComboBox;
    private javax.swing.JLabel TournamentLabel;
    private javax.swing.JLabel WinLabel;
    private javax.swing.JSpinner WinSpinner;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
