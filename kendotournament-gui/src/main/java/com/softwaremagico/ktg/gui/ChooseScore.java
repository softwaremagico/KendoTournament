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

import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.persistence.TournamentPool;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.awt.Toolkit;
import java.sql.SQLException;
import java.util.List;

public class ChooseScore extends javax.swing.JFrame {

    Translator trans = null;
    private boolean refreshing = false;

    /**
     * Creates new form ChooseScoreGUI
     */
    public ChooseScore() {
        refreshing = true;
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        fillTournaments();
        selectStyle();
        setLanguage();
        refreshing = false;
    }

    private void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        this.setTitle(trans.getTranslatedText("Hits"));
        ClassicRadioButton.setText(trans.getTranslatedText("ClassicRadioButton"));
        EuropeanRadioButton.setText(trans.getTranslatedText("EuropeanRadioButton"));
        CustomRadioButton.setText(trans.getTranslatedText("CustomRadioButton"));
        WinLabel.setText(trans.getTranslatedText("WinTag"));
        DrawLabel.setText(trans.getTranslatedText("DrawTag"));
        AcceptButton.setText(trans.getTranslatedText("AcceptButton"));
        CloseButton.setText(trans.getTranslatedText("CloseButton"));
        TournamentLabel.setText(trans.getTranslatedText("TournamentLabel"));
        EuropeanUndrawLabel.setText(trans.getTranslatedText("EuropeanUndrawLabel"));
    }

    private void selectStyle() {
        refreshing = true;
        try {
            switch (((Tournament) (TournamentComboBox.getSelectedItem())).getChoosedScore()) {
                case "European":
                    EuropeanRadioButton.setSelected(true);
                    break;
                case "Custom":
                    CustomRadioButton.setSelected(true);
                    float tmp_draw = ((Tournament) (TournamentComboBox.getSelectedItem())).getScoreForDraw();
                    WinSpinner.setValue(((Tournament) (TournamentComboBox.getSelectedItem())).getScoreForWin());
                    DrawSpinner.setValue(tmp_draw);
                    break;
                default:
                    ClassicRadioButton.setSelected(true);
                    break;
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException aiob) {
        }
        refreshing = false;
    }

    private String getStyle() {
        if (ClassicRadioButton.isSelected()) {
            return "Classic";
        }
        if (CustomRadioButton.isSelected()) {
            return "Custom";
        }
        return "European";
    }

    private Integer getWinnerPoints() {
        if (CustomRadioButton.isSelected()) {
            return (Integer) WinSpinner.getValue();
        }
        return 1;
    }

    private Integer getDrawPoints() {
        if (CustomRadioButton.isSelected()) {
            return (Integer) DrawSpinner.getValue();
        }
        return 0;
    }

    private void fillTournaments() {
        refreshing = true;
        try {
            List<Tournament> listTournaments = TournamentPool.getInstance().getSorted();
            try {
                for (int i = 0; i < listTournaments.size(); i++) {
                    TournamentComboBox.addItem(listTournaments.get(i));
                }
                TournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
            } catch (NullPointerException npe) {
                AlertManager.showErrorInformation(this.getClass().getName(), npe);
            }
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
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
        TournamentComboBox = new javax.swing.JComboBox();
        TournamentLabel = new javax.swing.JLabel();
        AcceptButton = new javax.swing.JButton();

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

        ScoreGroup.add(EuropeanRadioButton);
        EuropeanRadioButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        EuropeanRadioButton.setText("European");

        ScoreGroup.add(CustomRadioButton);
        CustomRadioButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        CustomRadioButton.setText("Custom");

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

        AcceptButton.setText("Accept");
        AcceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcceptButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ScorePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(AcceptButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CloseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AcceptButton, CloseButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ScorePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CloseButton)
                    .addComponent(AcceptButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void WinSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_WinSpinnerStateChanged
        if (!refreshing) {
            if ((Integer) WinSpinner.getValue() < 1) {
                WinSpinner.setValue(1);
            }
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
        }
    }//GEN-LAST:event_DrawSpinnerStateChanged

    private void TournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TournamentComboBoxActionPerformed
        selectStyle();
    }//GEN-LAST:event_TournamentComboBoxActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.toFront();
    }//GEN-LAST:event_formWindowOpened

    private void AcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AcceptButtonActionPerformed
        Tournament tournament = ((Tournament) (TournamentComboBox.getSelectedItem()));
        ((Tournament) (TournamentComboBox.getSelectedItem())).changeScoreOptions(getStyle(), getWinnerPoints(), getDrawPoints());
        try {
            if (TournamentPool.getInstance().update(tournament)) {
                AlertManager.informationMessage(NewTournament.class.getName(), "tournamentUpdated", "Score");
                this.dispose();
            }else{
                AlertManager.errorMessage(NewTournament.class.getName(), "genericError", "Score");
            }
        } catch (SQLException ex) {
            AlertManager.errorMessage(NewTournament.class.getName(), "genericError", "Score");
            AlertManager.showSqlErrorMessage(ex);
        }

    }//GEN-LAST:event_AcceptButtonActionPerformed

    private void CloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_CloseButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AcceptButton;
    private javax.swing.JRadioButton ClassicRadioButton;
    private javax.swing.JButton CloseButton;
    private javax.swing.JRadioButton CustomRadioButton;
    private javax.swing.JLabel DrawLabel;
    private javax.swing.JSpinner DrawSpinner;
    private javax.swing.JRadioButton EuropeanRadioButton;
    private javax.swing.JLabel EuropeanUndrawLabel;
    private javax.swing.ButtonGroup ScoreGroup;
    private javax.swing.JPanel ScorePanel;
    private javax.swing.JComboBox TournamentComboBox;
    private javax.swing.JLabel TournamentLabel;
    private javax.swing.JLabel WinLabel;
    private javax.swing.JSpinner WinSpinner;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
