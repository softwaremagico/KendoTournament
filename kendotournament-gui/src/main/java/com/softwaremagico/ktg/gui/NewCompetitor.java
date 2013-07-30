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
import com.softwaremagico.ktg.core.Photo;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.persistence.ClubPool;
import com.softwaremagico.ktg.persistence.PhotoPool;
import com.softwaremagico.ktg.persistence.RegisteredPersonPool;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.gui.base.KendoFrame;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.tools.Media;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

public class NewCompetitor extends KendoFrame {

    private Translator trans = null;
    //private PhotoFrame photoFrame = null;
    private boolean refreshClub;
    private RegisteredPerson oldCompetitor = null;
    private BufferedImage picture;
    private boolean defaultImage = true;

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
        try {
            clubs = ClubPool.getInstance().getSorted();
            if (clubs != null && clubs.size() > 0) {
                for (int i = 0; i < clubs.size(); i++) {
                    ClubComboBox.addItem(clubs.get(i));
                }
                ClubComboBox.setSelectedItem(ClubPool.getInstance().get(KendoTournamentGenerator.getInstance().getLastSelectedClub()));
            } else {
                NewClub newClub;
                AlertManager.errorMessage(this.getClass().getName(), "noClubsInserted", "MySQL");
                newClub = new NewClub();
                newClub.setVisible(true);
                newClub.updateClubsInCompetitor(this);
                newClub.setAlwaysOnTop(true);
                //this.dispose();
            }
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
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
        this.setTitle(trans.getTranslatedText("titleNewCompetitor"));
        ExploreButton.setText(trans.getTranslatedText("ExploreButton"));
        CleanButton.setText(trans.getTranslatedText("Clean"));
        AcceptButton.setText(trans.getTranslatedText("AcceptButton"));
        CancelButton.setText(trans.getTranslatedText("CancelButton"));
        SearchButton.setText(trans.getTranslatedText("SearchButton"));
        NameLabel.setText(trans.getTranslatedText("NameLabel"));
        SurnameLabel.setText(trans.getTranslatedText("SurnameLabel"));
        IDLabel.setText(trans.getTranslatedText("IDLabel"));
        ClubLabel.setText(trans.getTranslatedText("ClubLabel"));
        PhotoLabel.setText(trans.getTranslatedText("PhotoLabel"));
    }

    private void createPhoto() {
        createPhoto(Path.getDefaultPhoto());
        defaultImage = true;
    }

    private void createPhoto(String path) {
        createPhoto(Media.getImageFitted(path, PhotoPanel));
        defaultImage = false;
    }

    private void createPhoto(Photo photo) {
        createPhoto(Media.getImageFitted(photo, PhotoPanel));
        defaultImage = false;
    }

    private void createPhoto(BufferedImage image) {
        picture = image;
        JLabel picLabel = new JLabel(new ImageIcon(image));
        PhotoPanel.removeAll();
        PhotoPanel.add(picLabel, 0);
        PhotoPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        PhotoPanel.revalidate();
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
            if (competitor.getPhoto() != null) {
                createPhoto(competitor.getPhoto());
            } else {
                createPhoto();
            }
        } catch (NullPointerException npe) {
        }
    }

    private void cleanWindow() {
        oldCompetitor = null;
        NameTextField.setText("");
        SurnameTextField.setText("");
        IDTextField.setText("");
        PhotoTextField.setText("");
        createPhoto();
        this.repaint();
    }

    public RegisteredPerson acceptCompetitor() {
        if (ClubComboBox.getItemCount() > 0) {
            if (IDTextField.getText().length() > 0 && NameTextField.getText().length() > 0 && SurnameTextField.getText().length() > 0) {
                RegisteredPerson comp = new RegisteredPerson(IDTextField.getText(), NameTextField.getText().trim(), SurnameTextField.getText().trim());
                comp.setClub((Club) ClubComboBox.getSelectedItem());
                if (!defaultImage) {
                    try {
                        Photo competitorPhoto = new Photo(comp.getId());
                        competitorPhoto.setImage(picture);
                        PhotoPool.getInstance().set(competitorPhoto);
                    } catch (NullPointerException npe) {
                    }
                }
                try {
                    if (oldCompetitor != null) {
                        RegisteredPersonPool.getInstance().update(oldCompetitor, comp);
                        AlertManager.informationMessage(this.getClass().getName(), "competitorUpdated", "SQL");
                    } else {
                        RegisteredPersonPool.getInstance().add(comp);
                        AlertManager.informationMessage(this.getClass().getName(), "competitorStored", "SQL");
                    }
                } catch (SQLException ex) {
                    AlertManager.showSqlErrorMessage(ex);
                }
                cleanWindow();
                this.repaint();
                return comp;
            } else {
                AlertManager.errorMessage(this.getClass().getName(), "noCompetitiorFieldsFilled", "SQL");
            }
        }
        return null;
    }

    public void correctNif() {
        try {
            Integer dni = Integer.parseInt(IDTextField.getText());
            if (IDTextField.getText().length() == 8 && dni != null) {
                if (AlertManager.questionMessage("isDNI", "DNI -> NIF")) {
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
        Panel.setMaximumSize(new java.awt.Dimension(420, 215));

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

        IDLabel.setText("Id. Number:");

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
                    .addComponent(NameTextField)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelLayout.createSequentialGroup()
                        .addComponent(PhotoTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ExploreButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CleanButton))
                    .addComponent(IDTextField)
                    .addComponent(SurnameTextField)
                    .addComponent(ClubComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        PhotoPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PhotoPanel.setForeground(new java.awt.Color(255, 255, 255));
        PhotoPanel.setMaximumSize(new java.awt.Dimension(300, 200));
        PhotoPanel.setMinimumSize(new java.awt.Dimension(300, 200));
        PhotoPanel.setPreferredSize(new java.awt.Dimension(300, 200));
        java.awt.GridBagLayout PhotoPanelLayout = new java.awt.GridBagLayout();
        PhotoPanelLayout.columnWidths = new int[] {1};
        PhotoPanelLayout.rowHeights = new int[] {1};
        PhotoPanelLayout.columnWeights = new double[] {1.0};
        PhotoPanelLayout.rowWeights = new double[] {1.0};
        PhotoPanel.setLayout(PhotoPanelLayout);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PhotoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(SearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Panel, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PhotoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SearchButton)
                    .addComponent(AcceptButton)
                    .addComponent(CancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void ExploreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExploreButtonActionPerformed
        String file;
        //photo.CleanPhoto();
        if (!(file = exploreWindow("Select",
                JFileChooser.FILES_ONLY)).equals("")) {
            PhotoTextField.setText(file);
            createPhoto(file);
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
            KendoTournamentGenerator.getInstance().setLastSelectedClub(ClubComboBox.getSelectedItem().toString());
        }
    }//GEN-LAST:event_ClubComboBoxActionPerformed

    private void CleanButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CleanButtonActionPerformed
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
