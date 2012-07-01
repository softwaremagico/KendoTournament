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
 *  Created on 09-dic-2008.
 */
package com.softwaremagico.ktg.gui;

import com.softwaremagico.ktg.*;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.pdflist.CompetitorAccreditationCardPDF;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;

/**
 *
 * @author jorge
 */
public class NewRole extends KendoFrame {

    private Translator trans = null;
    private List<Tournament> listTournaments = new ArrayList<>();
    private List<Participant> listParticipants = new ArrayList<>();
    private boolean refreshTournament = true;
    private boolean refreshCompetitor = true;
    private boolean close;

    /**
     * Creates new form NewRole
     */
    public NewRole(boolean closeAfterUpdate) {
        close = closeAfterUpdate;
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage(KendoTournamentGenerator.getInstance().language);
        fillTournaments();
        fillCompetitors();
        fillRoles();
        refreshRole(true);
    }

    public NewRole(boolean closeAfterUpdate, List<Tournament> tournaments) {
        close = closeAfterUpdate;
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage(KendoTournamentGenerator.getInstance().language);
        fillTournaments(tournaments);
        fillCompetitors();
        fillRoles();
        refreshRole(true);
    }

    /**
     * Translate the GUI to the selected language.
     */
    public final void setLanguage(String language) {
        trans = new Translator("gui.xml");
        this.setTitle(trans.returnTag("titleNewRole", language));
        AcceptButton.setText(trans.returnTag("AcceptButton", language));
        CancelButton.setText(trans.returnTag("CancelButton", language));
        DeleteButton.setText(trans.returnTag("DeleteButton", language));
        PDFButton.setText(trans.returnTag("AccreditationPDFButton", language));
        TournamentLabel.setText(trans.returnTag("TournamentLabel", language));
        CompetitorLabel.setText(trans.returnTag("CompetitorLabel", language));
        RoleLabel.setText(trans.returnTag("RoleLabel", language));
    }

    private void fillTournaments() {
        refreshTournament = false;
        try {
            TournamentComboBox.removeAllItems();
            listTournaments = KendoTournamentGenerator.getInstance().database.getAllTournaments();
            for (int i = 0; i < listTournaments.size(); i++) {
                TournamentComboBox.addItem(listTournaments.get(i).name);
            }
            TournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
        } catch (NullPointerException npe) {
        }
        refreshTournament = true;
    }

    private void fillTournaments(List<Tournament> tournaments) {
        refreshTournament = false;
        try {
            TournamentComboBox.removeAllItems();
            listTournaments = tournaments;
            for (int i = 0; i < listTournaments.size(); i++) {
                TournamentComboBox.addItem(listTournaments.get(i).name);
            }
            TournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
        } catch (NullPointerException npe) {
        }
        refreshTournament = true;
    }

    private void fillCompetitors() {
        refreshCompetitor = false;
        try {
            CompetitorComboBox.removeAllItems();
            listParticipants = KendoTournamentGenerator.getInstance().database.getAllParticipants();
            for (int i = 0; i < listParticipants.size(); i++) {
                CompetitorComboBox.addItem(listParticipants.get(i).returnSurname() + ", " + listParticipants.get(i).returnName());
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        refreshCompetitor = true;
    }

    private void fillRoles() {
        try {
            RoleComboBox.removeAllItems();
            RoleComboBox.addItem("");
            for (int i = 0; i < KendoTournamentGenerator.getInstance().getAvailableRoles().size(); i++) {
                RoleComboBox.addItem(KendoTournamentGenerator.getInstance().getAvailableRoles().get(i).name);
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
    }

    @Override
    public String defaultFileName() {
        try {
            return listParticipants.get(CompetitorComboBox.getSelectedIndex()).returnName() + "_" + listParticipants.get(CompetitorComboBox.getSelectedIndex()).getId();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    private void refreshRole(boolean verbose) {
        try {
            String role = KendoTournamentGenerator.getInstance().database.getTagRole(listTournaments.get(TournamentComboBox.getSelectedIndex()), listParticipants.get(CompetitorComboBox.getSelectedIndex()));
            try {
                if (role == null) {
                    RoleComboBox.setSelectedIndex(0);
                } else {
                    RoleComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getAvailableRoles().getTraduction(role));
                }
            } catch (NullPointerException npe) {
                RoleComboBox.setSelectedIndex(0);
            } catch (IllegalArgumentException iae) {
            }
        } catch (ArrayIndexOutOfBoundsException aiofb) {
            KendoTournamentGenerator.getInstance().showErrorInformation(aiofb);
            MessageManager.errorMessage("noTournamentOrCompetitorExist", "MySQL", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            try {
                RoleComboBox.setSelectedItem("");
            } catch (IllegalArgumentException iae) {
            }
        } catch (NullPointerException npe) {
        } catch (IllegalArgumentException iae) {
        }
    }

    private void deleteRole() {
        try {
            KendoTournamentGenerator.getInstance().database.deleteRole(listTournaments.get(TournamentComboBox.getSelectedIndex()), listParticipants.get(CompetitorComboBox.getSelectedIndex()));
            if (RoleComboBox.getItemCount() > 0) {
                RoleComboBox.setSelectedIndex(0);
            }
        } catch (ArrayIndexOutOfBoundsException aiob) {
        }
    }

    public void defaultSelect(Competitor competitor) {
        TournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
        CompetitorComboBox.setSelectedItem(competitor.returnSurname() + ", " + competitor.returnName());
    }

    private void nextOne() {
        if (CompetitorComboBox.getSelectedIndex() < CompetitorComboBox.getItemCount() - 1) {
            CompetitorComboBox.setSelectedIndex(CompetitorComboBox.getSelectedIndex() + 1);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TournamentComboBox = new javax.swing.JComboBox<String>();
        CompetitorComboBox = new javax.swing.JComboBox<String>();
        RoleComboBox = new javax.swing.JComboBox<String>();
        TournamentLabel = new javax.swing.JLabel();
        CompetitorLabel = new javax.swing.JLabel();
        RoleLabel = new javax.swing.JLabel();
        CancelButton = new javax.swing.JButton();
        AcceptButton = new javax.swing.JButton();
        PDFButton = new javax.swing.JButton();
        DeleteButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        TournamentComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TournamentComboBoxActionPerformed(evt);
            }
        });

        CompetitorComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CompetitorComboBoxActionPerformed(evt);
            }
        });

        TournamentLabel.setText("Tournament:");

        CompetitorLabel.setText("Competitor:");

        RoleLabel.setText("Role:");

        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        AcceptButton.setText("Accept");
        AcceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcceptButtonActionPerformed(evt);
            }
        });

        PDFButton.setText("PDF");
        PDFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PDFButtonActionPerformed(evt);
            }
        });

        DeleteButton.setText("Delete");
        DeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(CompetitorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(TournamentLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(RoleLabel)
                    .addComponent(PDFButton, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(AcceptButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DeleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(RoleComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(CompetitorComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TournamentComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AcceptButton, CancelButton, DeleteButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TournamentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TournamentLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CompetitorComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CompetitorLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RoleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RoleLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CancelButton)
                    .addComponent(PDFButton)
                    .addComponent(AcceptButton)
                    .addComponent(DeleteButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        this.dispose();
}//GEN-LAST:event_CancelButtonActionPerformed

    private void AcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AcceptButtonActionPerformed
        try {
            //if (KendoTournamentGenerator.getInstance().database.storeRole(KendoTournamentGenerator.getInstance().getAvailableRoles().get(RoleComboBox.getSelectedIndex()), listTournaments.get(TournamentComboBox.getSelectedIndex()), listParticipants.get(CompetitorComboBox.getSelectedIndex()), true)) {
            int role = RoleComboBox.getSelectedIndex();
            if (role > 0) {
                if (KendoTournamentGenerator.getInstance().database.storeRole(KendoTournamentGenerator.getInstance().getAvailableRoles().get(role - 1), listTournaments.get(TournamentComboBox.getSelectedIndex()), listParticipants.get(CompetitorComboBox.getSelectedIndex()), true)) {
                    if (close) {
                        this.dispose();
                    } else {
                        //nextOne();
                    }
                }
            } else {
                deleteRole();
            }
        } catch (ArrayIndexOutOfBoundsException aiob) {
        }
    }//GEN-LAST:event_AcceptButtonActionPerformed

    private void CompetitorComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CompetitorComboBoxActionPerformed
        if (refreshCompetitor) {
            refreshRole(false);
        }
    }//GEN-LAST:event_CompetitorComboBoxActionPerformed

    private void PDFButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PDFButtonActionPerformed
        try {
            String file;
            if (!(file = exploreWindowsForPdf(trans.returnTag("ExportPDF", KendoTournamentGenerator.getInstance().language),
                    JFileChooser.FILES_AND_DIRECTORIES, "")).equals("")) {
                CompetitorWithPhoto c = KendoTournamentGenerator.getInstance().database.selectCompetitor(listParticipants.get(CompetitorComboBox.getSelectedIndex()).getId(), false);
                CompetitorAccreditationCardPDF pdf = new CompetitorAccreditationCardPDF(c, listTournaments.get(TournamentComboBox.getSelectedIndex()));
                pdf.createFile(file);
            }
        } catch (Exception ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
}//GEN-LAST:event_PDFButtonActionPerformed

    private void TournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TournamentComboBoxActionPerformed
        if (refreshTournament) {
            KendoTournamentGenerator.getInstance().changeLastSelectedTournament(TournamentComboBox.getSelectedItem().toString());
            refreshRole(false);
        }
    }//GEN-LAST:event_TournamentComboBoxActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        deleteRole();
}//GEN-LAST:event_DeleteButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AcceptButton;
    private javax.swing.JButton CancelButton;
    private javax.swing.JComboBox<String> CompetitorComboBox;
    private javax.swing.JLabel CompetitorLabel;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JButton PDFButton;
    private javax.swing.JComboBox<String> RoleComboBox;
    private javax.swing.JLabel RoleLabel;
    private javax.swing.JComboBox<String> TournamentComboBox;
    private javax.swing.JLabel TournamentLabel;
    // End of variables declaration//GEN-END:variables
}
