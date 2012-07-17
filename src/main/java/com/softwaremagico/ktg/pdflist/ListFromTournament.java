package com.softwaremagico.ktg.pdflist;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2012 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.gui.KendoFrame;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;

/**
 *
 * @author jorge
 */
public abstract class ListFromTournament extends KendoFrame {

    public Translator trans = null;
    public List<Tournament> listTournaments = new ArrayList<>();
    public boolean voidTournament;  //Add "All tournaments" option.
    private boolean refreshTournament = true;

    public void Start(boolean tmp_voidTournament) {
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLanguage();
        voidTournament = tmp_voidTournament;
        fillTournaments();
        updateArena();
        CheckBox.setVisible(false);
        ArenaComboBox.setEnabled(false);
    }

    public void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        TournamentLabel.setText(trans.returnTag("TournamentLabel"));
        ArenaLabel.setText(trans.returnTag("ArenaLabel"));
        CancelButton.setText(trans.returnTag("CancelButton"));
        GenerateButton.setText(trans.returnTag("GenerateButton"));
    }

    private void fillTournaments() {
        refreshTournament = false;
        try {
            listTournaments = KendoTournamentGenerator.getInstance().database.getAllTournaments();
            if (voidTournament) {
                TournamentComboBox.addItem(trans.returnTag("All"));
            }
            for (int i = 0; i < listTournaments.size(); i++) {
                TournamentComboBox.addItem(listTournaments.get(i).name);
            }
            TournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
        } catch (NullPointerException npe) {
        }
        refreshTournament = true;
    }

    public String returnSelectedTournamentName() {
        if (voidTournament && TournamentComboBox.getSelectedIndex() == 0) {
            return "All";
        }
        return TournamentComboBox.getSelectedItem().toString();
    }

    public Tournament returnSelectedTournament() {
        if (voidTournament) {
            if (TournamentComboBox.getSelectedIndex() > 0) {
                return listTournaments.get(TournamentComboBox.getSelectedIndex() - 1); // -1 to avoid "all".
            } else {
                return null;
            }
        } else {
            return listTournaments.get(TournamentComboBox.getSelectedIndex());
        }

    }

    private int returnSelectedTournamentOfList() {
        if (voidTournament) {
            if (TournamentComboBox.getSelectedIndex() == 0) {
                return -1;
            } else {
                return TournamentComboBox.getSelectedIndex() - 1;
            }
        } else {
            return TournamentComboBox.getSelectedIndex();
        }
    }

    protected abstract ParentList getPdfGenerator();
    
    public void generate() {
        try {
            String file;
            if (!(file = exploreWindowsForPdf(trans.returnTag("ExportPDF"),
                    JFileChooser.FILES_AND_DIRECTORIES, "")).equals("")) {
                ParentList pdf = getPdfGenerator();
                if (pdf.createFile(file)) {
                    this.dispose();
                }
            }
        } catch (Exception ex) {
          KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }

    private void updateArena() {
        ArenaComboBox.removeAllItems();
        try {
            int selectedTourn = returnSelectedTournamentOfList();
            if (selectedTourn >= 0 && listTournaments.get(selectedTourn).fightingAreas > 1) {
                ArenaComboBox.addItem(trans.returnTag("All"));
            }

            if (selectedTourn >= 0) {
                for (int i = 0; i < listTournaments.get(selectedTourn).fightingAreas; i++) {
                    ArenaComboBox.addItem(KendoTournamentGenerator.getInstance().returnShiaijo(i));
                }
            }
        } catch (NullPointerException | IndexOutOfBoundsException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
    }

    public int returnSelectedArena() {
        return (ArenaComboBox.getSelectedIndex() - 1);
    }

    /**
     * **********************************************
     *
     * CHECKBOX
     *
     ***********************************************
     */
    /**
     *
     */
    private void checkBoxClicked() {
    }

    private void comboBoxAction() {
    }

    boolean isCheckBoxSelected() {
        return CheckBox.isSelected();
    }

    void changeCheckBoxText(String text) {
        CheckBox.setText(text);
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
    public void addGenerateButtonListener(ActionListener al) {
        GenerateButton.addActionListener(al);
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
        CancelButton = new javax.swing.JButton();
        GenerateButton = new javax.swing.JButton();
        CheckBox = new javax.swing.JCheckBox();
        ArenaLabel = new javax.swing.JLabel();
        ArenaComboBox = new javax.swing.JComboBox<String>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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

        TournamentLabel.setText("Tournament");

        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        GenerateButton.setText("Generate List");
        GenerateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GenerateButtonActionPerformed(evt);
            }
        });

        CheckBox.setText("CheckBox");
        CheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckBoxActionPerformed(evt);
            }
        });

        ArenaLabel.setText("Arena:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(CheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(GenerateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(TournamentLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(TournamentComboBox, 0, 282, Short.MAX_VALUE)))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(ArenaLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ArenaComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(CancelButton))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {CancelButton, GenerateButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TournamentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TournamentLabel)
                    .addComponent(ArenaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ArenaLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CancelButton)
                    .addComponent(CheckBox)
                    .addComponent(GenerateButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_CancelButtonActionPerformed

    private void GenerateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GenerateButtonActionPerformed
        generate();
    }//GEN-LAST:event_GenerateButtonActionPerformed

    private void TournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TournamentComboBoxActionPerformed
        if (refreshTournament) {
            KendoTournamentGenerator.getInstance().changeLastSelectedTournament(TournamentComboBox.getSelectedItem().toString());
            comboBoxAction();
            if (ArenaComboBox.isEnabled()) {
                updateArena();
            }
        }
    }//GEN-LAST:event_TournamentComboBoxActionPerformed

    private void CheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckBoxActionPerformed
        checkBoxClicked();
    }//GEN-LAST:event_CheckBoxActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
       this.toFront();
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JComboBox<String> ArenaComboBox;
    protected javax.swing.JLabel ArenaLabel;
    private javax.swing.JButton CancelButton;
    public javax.swing.JCheckBox CheckBox;
    protected javax.swing.JButton GenerateButton;
    protected javax.swing.JComboBox<String> TournamentComboBox;
    private javax.swing.JLabel TournamentLabel;
    // End of variables declaration//GEN-END:variables
}
