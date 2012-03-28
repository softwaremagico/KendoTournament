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
 *  Created on 23-dic-2008.
 */
package com.softwaremagico.ktg.gui;

import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.List;
import com.softwaremagico.ktg.Club;
import com.softwaremagico.ktg.Competitor;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.MessageManager;
import com.softwaremagico.ktg.language.Translator;

/**
 *
 * @author  jorge
 */
public class NewClub extends javax.swing.JFrame {

    private Translator trans = null;
    private Club c;
    private List<Competitor> competitors;
    private NewCompetitor newCompetitor = null;
    private boolean updateClubOfCompetitor = false;

    /** Creates new form NewClub */
    public NewClub() {
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage(KendoTournamentGenerator.getInstance().language);
        FillCompetitors();
        updateClubOfCompetitor = true;
    }

    /**
     * Translate the GUI to the selected language.
     */
    public final void setLanguage(String language) {
        trans = new Translator("gui.xml");
        this.setTitle(trans.returnTag("titleClub", language));
        AcceptButton.setText(trans.returnTag("AcceptButton", language));
        CancelButton.setText(trans.returnTag("CancelButton", language));
        NameLabel.setText(trans.returnTag("NameLabel", language));
        CountryLabel.setText(trans.returnTag("CountryLabel", language));
        CityLabel.setText(trans.returnTag("CityLabel", language));
        AddressLabel.setText(trans.returnTag("AddressLabel", language));
        RepresentativeLabel.setText(trans.returnTag("RepresentativeLabel", language));
        PhoneLabel.setText(trans.returnTag("PhoneLabel", language));
        MailLabel.setText(trans.returnTag("MailLabel", language));
        SearchButton.setText(trans.returnTag("SearchButton", language));
    }

    public void UpdateWindow(Club tmp_c) {
        try {
            c = tmp_c;
            NameTextField.setText(c.returnName());
            CountryTextField.setText(c.returnCountry());
            CityTextField.setText(c.returnCity());
            AddressTextField.setText(c.returnAddress());
            FillCompetitorsFromClub(c);
            selectRepresentative(c);
            PhoneTextField.setText(c.phone);
            MailTextField.setText(c.email);
        } catch (NullPointerException npe) {
        }
    }

    private void CleanWindow() {
        NameTextField.setText("");
        CountryTextField.setText("");
        CityTextField.setText("");
        AddressTextField.setText("");
        PhoneTextField.setText("");
        MailTextField.setText("");
        RepresentativeComboBox.removeAllItems();
    }

    public void FillCompetitorsFromClub(Club club) {
        competitors = KendoTournamentGenerator.getInstance().database.searchCompetitorsByClub(club.returnName(), false);
        RepresentativeComboBox.removeAllItems();
        RepresentativeComboBox.addItem("");
        for (int i = 0; i < competitors.size(); i++) {
            RepresentativeComboBox.addItem(competitors.get(i).returnSurname() + ", " + competitors.get(i).returnName());
            if (club.representativeID != null && club.representativeID.equals(competitors.get(i).getId())) {
                RepresentativeComboBox.setSelectedIndex(i);
            }
        }
        updateClubOfCompetitor = false;
    }

    private void FillCompetitors() {
        competitors = KendoTournamentGenerator.getInstance().database.searchCompetitorsWithoutClub(false);
        RepresentativeComboBox.removeAllItems();
        RepresentativeComboBox.addItem("");
        for (int i = 0; i < competitors.size(); i++) {
            RepresentativeComboBox.addItem(competitors.get(i).returnSurname() + ", " + competitors.get(i).returnName());
        }
        updateClubOfCompetitor = true;
    }

    private void selectRepresentative(Club c) {
        for (int i = 0; i < competitors.size(); i++) {
            if (competitors.get(i).getId().equals(c.representativeID)) {
                RepresentativeComboBox.setSelectedIndex(i + 1);
            }
        }
    }

    private void UpdateRepresentative() {
        try {
            c.RefreshRepresentative(RepresentativeComboBox.getSelectedItem().toString(), MailTextField.getText(), PhoneTextField.getText());
        } catch (NullPointerException npe) {
        }
    }

    public void acceptClub() {
        try {
            setAlwaysOnTop(false);
            if (NameTextField.getText().length() > 0 && CountryTextField.getText().length() > 0 && CityTextField.getText().length() > 0) {
                c = new Club(NameTextField.getText().trim(), CountryTextField.getText().trim(), CityTextField.getText().trim());
                c.storeAddress(AddressTextField.getText());
                try {
                    c.RefreshRepresentative(competitors.get(RepresentativeComboBox.getSelectedIndex() - 1).getId(), MailTextField.getText(), PhoneTextField.getText());
                } catch (NullPointerException npe) {
                } catch (ArrayIndexOutOfBoundsException iob) {
                }
                if (KendoTournamentGenerator.getInstance().database.storeClub(c, true)) {
                    CleanWindow();
                }
                if (newCompetitor != null) {
                    //newCompetitor.fillClub();
                    newCompetitor.addClub(c);  //Uodate competitor window.
                    if (updateClubOfCompetitor) { //Update club of selected competitor.
                        if (RepresentativeComboBox.getSelectedIndex() > 0) {
                            competitors.get(RepresentativeComboBox.getSelectedIndex() - 1).club = c.returnName();
                            KendoTournamentGenerator.getInstance().database.updateClubCompetitor(competitors.get(RepresentativeComboBox.getSelectedIndex() - 1), false);
                        }
                    }
                    this.dispose();
                }
            } else {
                MessageManager.errorMessage("noClubFieldsFilled", "MySQL", KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
    }

    public void updateClubsInCompetitor(NewCompetitor nc) {
        newCompetitor = nc;
    }

    /************************************************
     *
     *                    LISTENERS
     *
     ************************************************/
    /**
     * Add the same action listener to all langugaes of the menu.
     * @param al
     */
    public void addSearchListener(ActionListener al) {
        SearchButton.addActionListener(al);
    }

    public void addAcceptListener(ActionListener al) {
        AcceptButton.addActionListener(al);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ClubPanel = new javax.swing.JPanel();
        NameTextField = new javax.swing.JTextField();
        CountryTextField = new javax.swing.JTextField();
        NameLabel = new javax.swing.JLabel();
        CountryLabel = new javax.swing.JLabel();
        CityTextField = new javax.swing.JTextField();
        CityLabel = new javax.swing.JLabel();
        AddressTextField = new javax.swing.JTextField();
        AddressLabel = new javax.swing.JLabel();
        AcceptButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();
        RepresentativePanel = new javax.swing.JPanel();
        RepresentativeComboBox = new javax.swing.JComboBox<String>();
        PhoneTextField = new javax.swing.JTextField();
        MailTextField = new javax.swing.JTextField();
        RepresentativeLabel = new javax.swing.JLabel();
        PhoneLabel = new javax.swing.JLabel();
        MailLabel = new javax.swing.JLabel();
        SearchButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Insert new Club");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        ClubPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        NameLabel.setText("Name:");

        CountryLabel.setText("Country:");

        CityLabel.setText("City:");

        AddressLabel.setText("Address:");

        javax.swing.GroupLayout ClubPanelLayout = new javax.swing.GroupLayout(ClubPanel);
        ClubPanel.setLayout(ClubPanelLayout);
        ClubPanelLayout.setHorizontalGroup(
            ClubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ClubPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ClubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(NameLabel)
                    .addComponent(CountryLabel)
                    .addComponent(CityLabel)
                    .addComponent(AddressLabel))
                .addGap(68, 68, 68)
                .addGroup(ClubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(AddressTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                    .addComponent(CityTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                    .addComponent(CountryTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                    .addComponent(NameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE))
                .addContainerGap())
        );
        ClubPanelLayout.setVerticalGroup(
            ClubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ClubPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ClubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ClubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CountryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CountryLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ClubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CityLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(ClubPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AddressLabel))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        AcceptButton.setText("Accept");
        AcceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AcceptButtonActionPerformed(evt);
            }
        });

        CancelButton.setText("Close");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        RepresentativePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        RepresentativeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RepresentativeComboBoxActionPerformed(evt);
            }
        });

        PhoneTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                PhoneTextFieldKeyReleased(evt);
            }
        });

        MailTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                MailTextFieldKeyReleased(evt);
            }
        });

        RepresentativeLabel.setText("Representative:");

        PhoneLabel.setText("Phone:");

        MailLabel.setText("Mail:");

        javax.swing.GroupLayout RepresentativePanelLayout = new javax.swing.GroupLayout(RepresentativePanel);
        RepresentativePanel.setLayout(RepresentativePanelLayout);
        RepresentativePanelLayout.setHorizontalGroup(
            RepresentativePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RepresentativePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(RepresentativePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(RepresentativeLabel)
                    .addComponent(PhoneLabel)
                    .addComponent(MailLabel))
                .addGap(36, 36, 36)
                .addGroup(RepresentativePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(MailTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(PhoneTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                    .addComponent(RepresentativeComboBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, 235, Short.MAX_VALUE))
                .addContainerGap())
        );
        RepresentativePanelLayout.setVerticalGroup(
            RepresentativePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RepresentativePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(RepresentativePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RepresentativeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(RepresentativeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(RepresentativePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PhoneTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PhoneLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(RepresentativePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(MailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MailLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        SearchButton.setText("Search");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(SearchButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 139, Short.MAX_VALUE)
                        .addComponent(CancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(AcceptButton)
                        .addGap(2, 2, 2))
                    .addComponent(RepresentativePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ClubPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AcceptButton, CancelButton, SearchButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ClubPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(RepresentativePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AcceptButton)
                    .addComponent(SearchButton)
                    .addComponent(CancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_CancelButtonActionPerformed

    private void RepresentativeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RepresentativeComboBoxActionPerformed
        //UpdateRepresentative();
    }//GEN-LAST:event_RepresentativeComboBoxActionPerformed

    private void PhoneTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PhoneTextFieldKeyReleased
        //UpdateRepresentative();
    }//GEN-LAST:event_PhoneTextFieldKeyReleased

    private void MailTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MailTextFieldKeyReleased
        // UpdateRepresentative();
    }//GEN-LAST:event_MailTextFieldKeyReleased

    private void AcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AcceptButtonActionPerformed
        acceptClub();
    }//GEN-LAST:event_AcceptButtonActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.toFront();
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AcceptButton;
    private javax.swing.JLabel AddressLabel;
    private javax.swing.JTextField AddressTextField;
    private javax.swing.JButton CancelButton;
    private javax.swing.JLabel CityLabel;
    private javax.swing.JTextField CityTextField;
    private javax.swing.JPanel ClubPanel;
    private javax.swing.JLabel CountryLabel;
    private javax.swing.JTextField CountryTextField;
    private javax.swing.JLabel MailLabel;
    private javax.swing.JTextField MailTextField;
    private javax.swing.JLabel NameLabel;
    private javax.swing.JTextField NameTextField;
    private javax.swing.JLabel PhoneLabel;
    private javax.swing.JTextField PhoneTextField;
    private javax.swing.JComboBox<String> RepresentativeComboBox;
    private javax.swing.JLabel RepresentativeLabel;
    private javax.swing.JPanel RepresentativePanel;
    private javax.swing.JButton SearchButton;
    // End of variables declaration//GEN-END:variables
}
