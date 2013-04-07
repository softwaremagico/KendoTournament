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

import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.MessageManager;
import com.softwaremagico.ktg.core.Photo;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.database.ClubPool;
import com.softwaremagico.ktg.database.PhotoPool;
import com.softwaremagico.ktg.database.RegisteredPersonPool;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JFileChooser;

public class NewCompetitor extends KendoFrame {

    private Translator trans = null;
    private PhotoFrame photoFrame = null;
    private boolean refreshClub;
    private RegisteredPerson oldCompetitor = null;

    /**
     * Creates new form NewCompetitor
     */
    public NewCompetitor() {
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage();
        fillClub();
        createPhoto();
    }

    public final void fillClub() {
        refreshClub = false;
        List<Club> clubs;
        ClubComboBox.removeAllItems();
        clubs = ClubPool.getInstance().getSorted();
        if (clubs != null && clubs.size() > 0) {
            for (int i = 0; i < clubs.size(); i++) {
                ClubComboBox.addItem(clubs.get(i));
            }
            ClubComboBox.setSelectedItem(ClubPool.getInstance().get(KendoTournamentGenerator.getInstance().getLastSelectedClub()));
        } else {
            NewClub newClub;
            MessageManager.errorMessage(this.getClass().getName(), "noClubsInserted", "MySQL");
            newClub = new NewClub();
            newClub.setVisible(true);
            newClub.updateClubsInCompetitor(this);
            newClub.setAlwaysOnTop(true);
            //this.dispose();
        }
        refreshClub = true;
    }

    public final void addClub(Club club) {
        refreshClub = false;
        ClubComboBox.addItem(club);
        refreshClub = true;
    }

    /**
     * Translate the GUI to the selected language.
     */
    public final void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        this.setTitle(trans.returnTag("titleNewCompetitor"));
        ExploreButton.setText(trans.returnTag("ExploreButton"));
        CleanButton.setText(trans.returnTag("CleanPhoto"));
        AcceptButton.setText(trans.returnTag("AcceptButton"));
        CancelButton.setText(trans.returnTag("CancelButton"));
        SearchButton.setText(trans.returnTag("SearchButton"));
        NameLabel.setText(trans.returnTag("NameLabel"));
        SurnameLabel.setText(trans.returnTag("SurnameLabel"));
        IDLabel.setText(trans.returnTag("IDLabel"));
        ClubLabel.setText(trans.returnTag("ClubLabel"));
        PhotoLabel.setText(trans.returnTag("PhotoLabel"));
    }

    /**
     * Show the photo of the selected user or a default one.
     */
    public final void createPhoto() {
        //photo = new PhotoFrame(PhotoPanel, Path.getDefaultPhoto());
        photoFrame = new PhotoFrame(PhotoPanel, Path.getDefaultPhoto());
        Dimension d;
        try {
            if (PhotoPanel.getWidth() / photoFrame.getWidth() > photoFrame.getHeight() / photoFrame.getHeight()) {
                d = new Dimension(PhotoPanel.getWidth(), (PhotoPanel.getWidth() / photoFrame.getWidth()) * photoFrame.getHeight());
            } else {
                d = new Dimension((PhotoPanel.getHeight() / photoFrame.getHeight()) * photoFrame.getWidth(), PhotoPanel.getHeight());
            }
        } catch (ArithmeticException ae) {
            d = new Dimension(PhotoPanel.getHeight(), PhotoPanel.getHeight());
        }
        photoFrame.setPreferredSize(d);
        PhotoPanel.removeAll();
        PhotoPanel.setBackground(new Color(255, 255, 255));
        PhotoPanel.add(photoFrame, 0);
        PhotoPanel.revalidate();
        photoFrame.repaint();
        PhotoPanel.repaint();
    }

    @Override
    public String defaultFileName() {
        return "";
    }

    public void updateWindow(RegisteredPerson competitor) {
        try {
            oldCompetitor = competitor;
            NameTextField.setText(competitor.getName());
            SurnameTextField.setText(competitor.getSurname());
            IDTextField.setText(competitor.getId());
            ClubComboBox.setSelectedItem(competitor.getClub());
            PhotoTextField.setText("");
            try {
                photoFrame.cleanPhoto();
            } catch (NullPointerException npe) {
            }
            try {
                //photo.ChangeInputStream(c.photoInput, c.photoSize);
                PhotoPanel.removeAll();
                PhotoPanel.setBackground(new Color(255, 255, 255));
                if (competitor.getPhoto()!= null) {
                    photoFrame.changePhoto(competitor.getPhoto());
                    PhotoPanel.add(photoFrame, 0);
                    photoFrame.repaint();
                } else {
                    photoFrame.changePhoto(Path.getDefaultPhoto());
                }
            } catch (IllegalArgumentException iae) {
                //iae.printStackTrace();
                createPhoto();
            }
            PhotoPanel.repaint();
        } catch (NullPointerException npe) {
        }
    }

    private void cleanWindow() {
        oldCompetitor = null;
        NameTextField.setText("");
        SurnameTextField.setText("");
        IDTextField.setText("");
        PhotoTextField.setText("");
        if (photoFrame != null) {
            photoFrame.cleanPhoto();
        }
        createPhoto();
        this.repaint();
    }

    public RegisteredPerson acceptCompetitor() {
        if (ClubComboBox.getItemCount() > 0) {
            if (IDTextField.getText().length() > 0 && NameTextField.getText().length() > 0 && SurnameTextField.getText().length() > 0) {
                RegisteredPerson comp = new RegisteredPerson(IDTextField.getText(), NameTextField.getText().trim(), SurnameTextField.getText().trim());
                comp.setClub((Club) ClubComboBox.getSelectedItem());
                    try {
                        Photo competitorPhoto = new Photo(comp.getId());
                        competitorPhoto.setImage(photoFrame.getPhoto().getInput(), photoFrame.getPhoto().getSize());
                        PhotoPool.getInstance().set(competitorPhoto);
                    } catch (NullPointerException npe) {
                    }
                if (oldCompetitor != null) {
                    RegisteredPersonPool.getInstance().update(oldCompetitor, comp);
                } else {
                    RegisteredPersonPool.getInstance().add(comp);
                }
                cleanWindow();
                this.repaint();
                return comp;

            } else {
                MessageManager.errorMessage(this.getClass().getName(), "noCompetitiorFieldsFilled", "SQL");
            }
        }
        return null;
    }

    public void correctNif() {
        try {
            Integer dni = Integer.parseInt(IDTextField.getText());
            if (IDTextField.getText().length() == 8 && dni != null) {
                if (MessageManager.questionMessage("isDNI", "DNI -> NIF")) {
                    IDTextField.setText(RegisteredPerson.nifFromDni(dni));
                }
            }
        } catch (NumberFormatException nfe) {
        }
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
    public void addSearchListener(ActionListener al) {
        SearchButton.addActionListener(al);
    }

    public void addAcceptListener(ActionListener al) {
        AcceptButton.addActionListener(al);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Panel = new javax.swing.JPanel();
        NameLabel = new javax.swing.JLabel();
        SurnameTextField = new javax.swing.JTextField();
        SurnameLabel = new javax.swing.JLabel();
        NameTextField = new javax.swing.JTextField();
        IDTextField = new javax.swing.JTextField();
        IDLabel = new javax.swing.JLabel();
        ClubLabel = new javax.swing.JLabel();
        PhotoLabel = new javax.swing.JLabel();
        PhotoTextField = new javax.swing.JTextField();
        ExploreButton = new javax.swing.JButton();
        ClubComboBox = new javax.swing.JComboBox();
        CleanButton = new javax.swing.JButton();
        CancelButton = new javax.swing.JButton();
        AcceptButton = new javax.swing.JButton();
        SearchButton = new javax.swing.JButton();
        PhotoPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        Panel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        NameLabel.setText("Name:");

        SurnameLabel.setText("Surname:");

        IDTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                IDTextFieldFocusLost(evt);
            }
        });
        IDTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                IDTextFieldKeyReleased(evt);
            }
        });

        IDLabel.setText("Identification Number:");

        ClubLabel.setText("Club:");

        PhotoLabel.setText("Photo:");

        ExploreButton.setText("Explore");
        ExploreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExploreButtonActionPerformed(evt);
            }
        });

        ClubComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ClubComboBoxActionPerformed(evt);
            }
        });

        CleanButton.setText("Clean");
        CleanButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CleanButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelLayout = new javax.swing.GroupLayout(Panel);
        Panel.setLayout(PanelLayout);
        PanelLayout.setHorizontalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PhotoLabel)
                    .addComponent(ClubLabel)
                    .addComponent(IDLabel)
                    .addComponent(SurnameLabel)
                    .addComponent(NameLabel))
                .addGap(26, 26, 26)
                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(NameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelLayout.createSequentialGroup()
                        .addComponent(PhotoTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ExploreButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CleanButton))
                    .addComponent(IDTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                    .addComponent(SurnameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                    .addComponent(ClubComboBox, 0, 351, Short.MAX_VALUE))
                .addContainerGap())
        );

        PanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {CleanButton, ExploreButton});

        PanelLayout.setVerticalGroup(
            PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SurnameLabel)
                    .addComponent(SurnameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(IDLabel)
                    .addComponent(IDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ClubLabel)
                    .addComponent(ClubComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PhotoLabel)
                    .addComponent(PhotoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CleanButton)
                    .addComponent(ExploreButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {IDTextField, NameTextField, PhotoTextField, SurnameTextField});

        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        AcceptButton.setText("Accept");

        SearchButton.setText("Search");

        PhotoPanel.setBackground(new java.awt.Color(255, 255, 255));
        PhotoPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        PhotoPanel.setMaximumSize(new java.awt.Dimension(20, 30));
        PhotoPanel.setOpaque(false);
        PhotoPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(PhotoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(SearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 543, Short.MAX_VALUE)
                        .addComponent(AcceptButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CancelButton)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AcceptButton, CancelButton, SearchButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PhotoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                    .addComponent(Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SearchButton)
                    .addComponent(CancelButton)
                    .addComponent(AcceptButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void ExploreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExploreButtonActionPerformed
        String file;
        //photo.CleanPhoto();
        if (!(file = exploreWindow("Select",
                JFileChooser.FILES_ONLY)).equals("")) {
            photoFrame.cleanPhoto();
            PhotoTextField.setText(file);
            PhotoPanel.removeAll();
            photoFrame.changePhoto(file);
            PhotoPanel.add(photoFrame, 0);
        }
    }//GEN-LAST:event_ExploreButtonActionPerformed

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_CancelButtonActionPerformed

    private void IDTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_IDTextFieldKeyReleased
        if (IDTextField.getText().length() > 12) {
            IDTextField.setText(IDTextField.getText().subSequence(0, 12).toString());
        }
        IDTextField.setText(IDTextField.getText().replace("-", ""));
        IDTextField.setText(IDTextField.getText().replace(" ", ""));
        IDTextField.setText(IDTextField.getText().toUpperCase());
    }//GEN-LAST:event_IDTextFieldKeyReleased

    private void ClubComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClubComboBoxActionPerformed
        if (refreshClub) {
            KendoTournamentGenerator.getInstance().changeLastSelectedClub(ClubComboBox.getSelectedItem().toString());
        }
    }//GEN-LAST:event_ClubComboBoxActionPerformed

    private void CleanButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CleanButtonActionPerformed
        photoFrame.cleanPhoto();
        createPhoto();
        this.repaint();
    }//GEN-LAST:event_CleanButtonActionPerformed

    private void IDTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_IDTextFieldFocusLost
        //testNIF();
    }//GEN-LAST:event_IDTextFieldFocusLost

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.toFront();
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AcceptButton;
    private javax.swing.JButton CancelButton;
    private javax.swing.JButton CleanButton;
    private javax.swing.JComboBox ClubComboBox;
    private javax.swing.JLabel ClubLabel;
    private javax.swing.JButton ExploreButton;
    private javax.swing.JLabel IDLabel;
    private javax.swing.JTextField IDTextField;
    private javax.swing.JLabel NameLabel;
    private javax.swing.JTextField NameTextField;
    private javax.swing.JPanel Panel;
    private javax.swing.JLabel PhotoLabel;
    private javax.swing.JPanel PhotoPanel;
    private javax.swing.JTextField PhotoTextField;
    private javax.swing.JButton SearchButton;
    private javax.swing.JLabel SurnameLabel;
    private javax.swing.JTextField SurnameTextField;
    // End of variables declaration//GEN-END:variables
}
