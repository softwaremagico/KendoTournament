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

import com.softwaremagico.ktg.core.KendoLog;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.MessageManager;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Role;
import com.softwaremagico.ktg.core.RoleTag;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.RegisteredPersonPool;
import com.softwaremagico.ktg.database.RolePool;
import com.softwaremagico.ktg.database.TournamentPool;
import com.softwaremagico.ktg.language.LanguagePool;
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
    private boolean refreshTournament = true;
    private boolean refreshCompetitor = true;
    private boolean close;

    /**
     * Creates new form NewRole
     */
    public NewRole(boolean closeAfterUpdate) {
        createWindow(closeAfterUpdate);
        refreshRole();
    }

    public NewRole(boolean closeAfterUpdate, RegisteredPerson competitor) {
        createWindow(closeAfterUpdate);
        defaultSelect(competitor);
    }

    private void createWindow(boolean closeAfterUpdate) {
        close = closeAfterUpdate;
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage();
        fillTournaments();
        fillCompetitors();
        fillRoles();
    }

    /**
     * Translate the GUI to the selected language.
     */
    public final void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        this.setTitle(trans.returnTag("titleNewRole"));
        AcceptButton.setText(trans.returnTag("AcceptButton"));
        CancelButton.setText(trans.returnTag("CloseButton"));
        DeleteButton.setText(trans.returnTag("DeleteButton"));
        PDFButton.setText(trans.returnTag("AccreditationPDFButton"));
        TournamentLabel.setText(trans.returnTag("TournamentLabel"));
        CompetitorLabel.setText(trans.returnTag("CompetitorLabel"));
        RoleLabel.setText(trans.returnTag("RoleLabel"));
    }

    private void fillTournaments() {
        KendoLog.finest(this.getClass().getName(), "Updating tournament list of NewRole");
        refreshTournament = false;
        try {
            TournamentComboBox.removeAllItems();
            listTournaments = TournamentPool.getInstance().getAll();
            for (int i = 0; i < listTournaments.size(); i++) {
                TournamentComboBox.addItem(listTournaments.get(i));
            }
            TournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
        } catch (NullPointerException npe) {
        }
        refreshTournament = true;
    }

    private void fillCompetitors() {
        KendoLog.finest(this.getClass().getName(), "Updating competitors list of NewRole");
        refreshCompetitor = false;
        try {
            CompetitorComboBox.removeAllItems();
            List<RegisteredPerson> listParticipants = RegisteredPersonPool.getInstance().getAll();
            for (int i = 0; i < listParticipants.size(); i++) {
                CompetitorComboBox.addItem(listParticipants.get(i));
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        refreshCompetitor = true;
    }

    private void fillRoles() {
        KendoLog.finest(this.getClass().getName(), "Updating role list of NewRole");
        try {
            RoleTagsComboBox.removeAllItems();
            RoleTagsComboBox.addItem("");
            for (int i = 0; i < RolePool.getInstance().getRoleTags().size(); i++) {
                RoleTagsComboBox.addItem(RolePool.getInstance().getRoleTags().get(i));
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
    }

    @Override
    public String defaultFileName() {
        try {
            return ((RegisteredPerson) CompetitorComboBox.getSelectedItem()).getShortName() + "_"
                    + ((RegisteredPerson) CompetitorComboBox.getSelectedItem()).getId();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    /**
     * Obtaining role of selected Competitor
     */
    private void refreshRole() {
        try {
            Role role = RolePool.getInstance().getRole((Tournament) TournamentComboBox.getSelectedItem(), (RegisteredPerson) CompetitorComboBox.getSelectedItem());
            try {
                if (role == null) {
                    RoleTagsComboBox.setSelectedIndex(0);
                } else {
                    RoleTagsComboBox.setSelectedItem(role.getTag());
                }
            } catch (NullPointerException | IllegalArgumentException npe) {
                RoleTagsComboBox.setSelectedIndex(0);
            }
        } catch (ArrayIndexOutOfBoundsException aiofb) {
            MessageManager.errorMessage(this.getClass().getName(), "noTournamentOrCompetitorExist", "MySQL");
            try {
                RoleTagsComboBox.setSelectedItem("");
            } catch (IllegalArgumentException iae) {
            }
        } catch (NullPointerException npe) {
        } catch (IllegalArgumentException iae) {
        }
    }

    private void deleteRole() {
        try {
            RolePool.getInstance().remove((Tournament) TournamentComboBox.getSelectedItem(), (RegisteredPerson) CompetitorComboBox.getSelectedItem());
            if (RoleTagsComboBox.getItemCount() > 0) {
                RoleTagsComboBox.setSelectedIndex(0);
            }
        } catch (ArrayIndexOutOfBoundsException aiob) {
        }
    }

    private void defaultSelect(RegisteredPerson competitor) {
        refreshTournament = false;
        TournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
        refreshTournament = true;
        CompetitorComboBox.setSelectedItem(competitor);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TournamentComboBox = new javax.swing.JComboBox();
        CompetitorComboBox = new javax.swing.JComboBox();
        RoleTagsComboBox = new javax.swing.JComboBox();
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
                    .addComponent(RoleTagsComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addComponent(RoleTagsComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            int roleIndex = RoleTagsComboBox.getSelectedIndex();
            if (roleIndex > 0) {
                Role oldRole = RolePool.getInstance().getRole((Tournament) TournamentComboBox.getSelectedItem(), (RegisteredPerson) CompetitorComboBox.getSelectedItem());
                Role newRole = new Role((Tournament) TournamentComboBox.getSelectedItem(), (RegisteredPerson) CompetitorComboBox.getSelectedItem(), (RoleTag) RoleTagsComboBox.getSelectedItem(), false, false);
                //Update or insert?
                if (oldRole != null) {
                    RolePool.getInstance().update((Tournament) TournamentComboBox.getSelectedItem(), oldRole, newRole);
                } else {
                    RolePool.getInstance().add((Tournament) TournamentComboBox.getSelectedItem(), newRole);
                }
                if (close) {
                    this.dispose();
                }
            } else {
                deleteRole();
            }
        } catch (ArrayIndexOutOfBoundsException aiob) {
        }
    }//GEN-LAST:event_AcceptButtonActionPerformed

    private void CompetitorComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CompetitorComboBoxActionPerformed
        if (refreshCompetitor) {
            refreshRole();
        }
    }//GEN-LAST:event_CompetitorComboBoxActionPerformed

    private void PDFButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PDFButtonActionPerformed
        try {
            String file;
            if (!(file = exploreWindowsForPdf(trans.returnTag("ExportPDF"),
                    JFileChooser.FILES_AND_DIRECTORIES, "")).equals("")) {
                RegisteredPerson person = RegisteredPersonPool.getInstance().get(((RegisteredPerson) CompetitorComboBox.getSelectedItem()).getId());
                CompetitorAccreditationCardPDF pdf = new CompetitorAccreditationCardPDF(((Tournament) TournamentComboBox.getSelectedItem()), person);
                pdf.createFile(file);
            }
        } catch (Exception ex) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), ex);
        }
}//GEN-LAST:event_PDFButtonActionPerformed

    private void TournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TournamentComboBoxActionPerformed
        if (refreshTournament) {
            KendoTournamentGenerator.getInstance().changeLastSelectedTournament(TournamentComboBox.getSelectedItem().toString());
            refreshRole();
        }
    }//GEN-LAST:event_TournamentComboBoxActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        deleteRole();
}//GEN-LAST:event_DeleteButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AcceptButton;
    private javax.swing.JButton CancelButton;
    private javax.swing.JComboBox CompetitorComboBox;
    private javax.swing.JLabel CompetitorLabel;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JButton PDFButton;
    private javax.swing.JLabel RoleLabel;
    private javax.swing.JComboBox RoleTagsComboBox;
    private javax.swing.JComboBox TournamentComboBox;
    private javax.swing.JLabel TournamentLabel;
    // End of variables declaration//GEN-END:variables
}
