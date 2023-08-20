package com.softwaremagico.ktg.gui;

import java.awt.Toolkit;
import java.sql.SQLException;
import java.util.List;

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
import com.softwaremagico.ktg.language.ITranslator;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.persistence.AutoSaveByAction;
import com.softwaremagico.ktg.persistence.TournamentPool;
import com.softwaremagico.ktg.tournament.score.ScoreType;
import com.softwaremagico.ktg.tournament.score.TournamentScore;

public class ChooseScore extends javax.swing.JFrame {
    private static final long serialVersionUID = 4593127586769712466L;
    private ITranslator trans = null;
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
        WinOverDrawsRadioButton.setText(trans.getTranslatedText("WinOverDrawsRadioButton"));
        CustomRadioButton.setText(trans.getTranslatedText("CustomRadioButton"));
        InternationalRadioButton.setText(trans.getTranslatedText("InternationlRadioButton"));
        WinLabel.setText(trans.getTranslatedText("WinTag"));
        DrawLabel.setText(trans.getTranslatedText("DrawTag"));
        HierarchicalDrawLabel.setText(trans.getTranslatedText("HierarchicalDrawTag"));
        AcceptButton.setText(trans.getTranslatedText("AcceptButton"));
        CloseButton.setText(trans.getTranslatedText("CloseButton"));
        TournamentLabel.setText(trans.getTranslatedText("TournamentLabel"));
        InternationalUndrawLabel.setText(trans.getTranslatedText("InternationalUndrawLabel"));
    }

    private javax.swing.JRadioButton getRadioButton(ScoreType scoreType) {
        switch (scoreType) {
            case CUSTOM:
                return CustomRadioButton;
            case INTERNATIONAL:
            	return InternationalRadioButton;
            case CLASSIC:
                return ClassicRadioButton;
            case WIN_OVER_DRAWS:
                return WinOverDrawsRadioButton;
        }
        return WinOverDrawsRadioButton;
    }

    private void selectStyle() {
        refreshing = true;

        //Select correct RadioButton
        javax.swing.JRadioButton selectedRadioButton = getRadioButton(((Tournament) (TournamentComboBox
                .getSelectedItem())).getTournamentScore().getScoreType());
        selectedRadioButton.setSelected(true);

        //Set spinners values. 
        if (((Tournament) (TournamentComboBox.getSelectedItem())).getTournamentScore().getScoreType()
                .equals(ScoreType.CUSTOM)) {
            WinSpinner.setValue(((Tournament) (TournamentComboBox.getSelectedItem())).getTournamentScore()
                    .getPointsVictory());
            DrawSpinner.setValue(((Tournament) (TournamentComboBox.getSelectedItem())).getTournamentScore()
                    .getPointsDraw());
        }

        refreshing = false;
    }

    private ScoreType getStyle() {
        if (WinOverDrawsRadioButton.isSelected()) {
            return ScoreType.WIN_OVER_DRAWS;
        }
        if (CustomRadioButton.isSelected()) {
            return ScoreType.CUSTOM;
        }
        if (InternationalRadioButton.isSelected()) {
            return ScoreType.EUROPEAN;
        }
        if (InternationalRadioButton.isSelected()) {
            return ScoreType.INTERNATIONAL;
        }
        if (ClassicRadioButton.isSelected()) {
            return ScoreType.CLASSIC;
        }
        return ScoreType.DEFAULT;
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
                for (Tournament listTournament : listTournaments) {
                    TournamentComboBox.addItem(listTournament);
                }
                TournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
            } catch (NullPointerException npe) {
                AlertManager.showErrorInformation(this.getClass().getName(), npe);
            }
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
        refreshing = false;
        // fillFightsPanel();
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
        jPanel1 = new javax.swing.JPanel();
        TournamentComboBox = new javax.swing.JComboBox();
        TournamentLabel = new javax.swing.JLabel();
        ScorePanel = new javax.swing.JPanel();
        WinOverDrawsRadioButton = new javax.swing.JRadioButton();
        CustomRadioButton = new javax.swing.JRadioButton();
        WinSpinner = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        WinLabel = new javax.swing.JLabel();
        DrawLabel = new javax.swing.JLabel();
        InternationalUndrawLabel = new javax.swing.JLabel();
        DrawSpinner = new javax.swing.JSpinner();
        InternationalRadioButton = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        HierarchicalDrawLabel = new javax.swing.JLabel();
        ClassicRadioButton = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        CloseButton = new javax.swing.JButton();
        AcceptButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Score");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        TournamentComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TournamentComboBoxActionPerformed(evt);
            }
        });

        TournamentLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        TournamentLabel.setText("Tournament:");

        ScorePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        ScoreGroup.add(WinOverDrawsRadioButton);
        WinOverDrawsRadioButton.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        WinOverDrawsRadioButton.setText("Wins Over Draws");

        ScoreGroup.add(CustomRadioButton);
        CustomRadioButton.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        CustomRadioButton.setText("Custom");

        WinSpinner.setValue(1);
        WinSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                WinSpinnerStateChanged(evt);
            }
        });

        jLabel7.setText("1");

        WinLabel.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        WinLabel.setText("Winned");

        DrawLabel.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        DrawLabel.setText("Draw");

        InternationalUndrawLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        InternationalUndrawLabel.setText("For undraw");

        DrawSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                DrawSpinnerStateChanged(evt);
            }
        });

        ScoreGroup.add(InternationalRadioButton);
        InternationalRadioButton.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        InternationalRadioButton.setText("International");

        jLabel1.setText("1");

        HierarchicalDrawLabel.setText("More than lost");

        ScoreGroup.add(ClassicRadioButton);
        ClassicRadioButton.setFont(new java.awt.Font("Ubuntu", 1, 14)); // NOI18N
        ClassicRadioButton.setText("Classical");

        jLabel2.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N
        jLabel2.setText("1");

        jLabel3.setFont(new java.awt.Font("Ubuntu", 0, 14)); // NOI18N
        jLabel3.setText("0");

        javax.swing.GroupLayout ScorePanelLayout = new javax.swing.GroupLayout(ScorePanel);
        ScorePanel.setLayout(ScorePanelLayout);
        ScorePanelLayout.setHorizontalGroup(
            ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ScorePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(ScorePanelLayout.createSequentialGroup()
                        .addGroup(ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(WinOverDrawsRadioButton)
                            .addComponent(InternationalRadioButton)
                            .addComponent(CustomRadioButton))
                        .addGap(100, 100, 100)
                        .addGroup(ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(WinSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(WinLabel, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(ScorePanelLayout.createSequentialGroup()
                        .addComponent(ClassicRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 127, Short.MAX_VALUE)
                .addGroup(ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(InternationalUndrawLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(HierarchicalDrawLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(DrawSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(DrawLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        ScorePanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {DrawSpinner, WinSpinner});

        ScorePanelLayout.setVerticalGroup(
            ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ScorePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(WinLabel)
                    .addComponent(DrawLabel))
                .addGap(18, 18, 18)
                .addGroup(ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ClassicRadioButton)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(WinOverDrawsRadioButton)
                    .addComponent(jLabel7)
                    .addComponent(InternationalUndrawLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(InternationalRadioButton)
                    .addComponent(jLabel1)
                    .addComponent(HierarchicalDrawLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ScorePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(CustomRadioButton)
                    .addComponent(WinSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DrawSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        CloseButton.setText("Close");
        CloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseButtonActionPerformed(evt);
            }
        });

        AcceptButton.setText("Accept");
        AcceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcceptButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TournamentLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TournamentComboBox, 0, 430, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(ScorePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(AcceptButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CloseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AcceptButton, CloseButton});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TournamentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TournamentLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ScorePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CloseButton)
                    .addComponent(AcceptButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_TournamentComboBoxActionPerformed
        selectStyle();
    }// GEN-LAST:event_TournamentComboBoxActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_formWindowOpened
        this.toFront();
    }// GEN-LAST:event_formWindowOpened

    private void AcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_AcceptButtonActionPerformed
        Tournament tournament = ((Tournament) (TournamentComboBox.getSelectedItem()));
        ((Tournament) (TournamentComboBox.getSelectedItem())).setTournamentScore(new TournamentScore(getStyle(),
                getWinnerPoints(), getDrawPoints()));
        try {
            if (TournamentPool.getInstance().update(tournament)) {
                AlertManager.informationMessage(NewTournament.class.getName(), "tournamentUpdated", "Score");
                AutoSaveByAction.getInstance().save();
                this.dispose();
            } else {
                AlertManager.errorMessage(NewTournament.class.getName(), "genericError", "Score");
            }
        } catch (SQLException ex) {
            AlertManager.errorMessage(NewTournament.class.getName(), "genericError", "Score");
            AlertManager.showSqlErrorMessage(ex);
        }

    }// GEN-LAST:event_AcceptButtonActionPerformed

    private void CloseButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_CloseButtonActionPerformed
        this.dispose();
    }// GEN-LAST:event_CloseButtonActionPerformed

    private void DrawSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_DrawSpinnerStateChanged
        if (!refreshing) {
            if ((Integer) DrawSpinner.getValue() < 0) {
                DrawSpinner.setValue(0);
            }
            if ((Integer) DrawSpinner.getValue() >= (Integer) WinSpinner.getValue()) {
                DrawSpinner.setValue((Integer) WinSpinner.getValue() - 1);
            }
        }
    }// GEN-LAST:event_DrawSpinnerStateChanged

    private void WinSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_WinSpinnerStateChanged
        if (!refreshing) {
            if ((Integer) WinSpinner.getValue() < 1) {
                WinSpinner.setValue(1);
            }
        }
    }// GEN-LAST:event_WinSpinnerStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AcceptButton;
    private javax.swing.JRadioButton ClassicRadioButton;
    private javax.swing.JButton CloseButton;
    private javax.swing.JRadioButton CustomRadioButton;
    private javax.swing.JLabel DrawLabel;
    private javax.swing.JSpinner DrawSpinner;
    private javax.swing.JLabel HierarchicalDrawLabel;
    private javax.swing.JRadioButton InternationalRadioButton;
    private javax.swing.JLabel InternationalUndrawLabel;
    private javax.swing.ButtonGroup ScoreGroup;
    private javax.swing.JPanel ScorePanel;
    private javax.swing.JComboBox TournamentComboBox;
    private javax.swing.JLabel TournamentLabel;
    private javax.swing.JLabel WinLabel;
    private javax.swing.JRadioButton WinOverDrawsRadioButton;
    private javax.swing.JSpinner WinSpinner;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
