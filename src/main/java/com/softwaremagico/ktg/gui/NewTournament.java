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

import com.softwaremagico.ktg.*;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.pdflist.TournamentAccreditationPDF;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

public class NewTournament extends KendoFrame {

    Translator trans = null;
    private PhotoFrame banner;
    private Integer maxCompetitorTeam = null;

    /**
     * Creates new form NewTournament
     */
    public NewTournament() {
        initComponents();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage();
        CreateBanner();
        NameTextField.setEditable(true);
    }

    /**
     * Translate the GUI to the selected language.
     */
    public final void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        this.setTitle(trans.returnTag("titleNewTournament"));
        ExploreButton.setText(trans.returnTag("ExploreButton"));
        AcceptButton.setText(trans.returnTag("AcceptButton"));
        CancelButton.setText(trans.returnTag("CancelButton"));
        NameLabel.setText(trans.returnTag("NameTournamentLabel"));
        NumberCompetitorsLabel.setText(trans.returnTag("NumberLabel"));
        BannerLabel.setText(trans.returnTag("BannerLabel"));
        FightingAreasLabel.setText(trans.returnTag("FightArea"));
        PDFButton.setText(trans.returnTag("AccreditationPDFButton"));
        SearchButton.setText(trans.returnTag("SearchButton"));
    }

    /**
     * Show the photo of the selected user or a default one.
     */
    public final void CreateBanner() {
        banner = new PhotoFrame(BannerPanel, Path.getDefaultBanner());
        //banner.setPreferredSize(new Dimension(BannerPanel.getWidth(), BannerPanel.getHeight()));
        /*
         * try { banner.Resize(BannerPanel.getWidth(), BannerPanel.getHeight());
         * } catch (Exception ex) {
         * Logger.getLogger(NewTournament.class.getName()).log(Level.SEVERE,
         * null, ex); }
         */
        BannerPanel.add(banner, 0);
        banner.repaint();
        BannerPanel.repaint();
        BannerPanel.revalidate();
    }

    private void cleanWindow() {
        NameTextField.setText("");
        NameTextField.setEditable(true);
        BannerTextField.setText("");
        banner.CleanPhoto();
        CreateBanner();
    }

    public void updateWindow(Tournament tournament) {
        try {
            maxCompetitorTeam = tournament.getTeamSize();
            NameTextField.setText(tournament.getName());
            NameTextField.setEditable(false);
            NumCompetitorsSpinner.setValue(tournament.getTeamSize());
            BannerTextField.setText("");
            banner.CleanPhoto();
            try {
                banner.ChangePhoto(tournament.banner(), tournament.getBannerInput(), tournament.getBannerSize());
                //banner.ChangeInputStream(t.BannerInput, t.bannerSize);
                banner.repaint();
                BannerPanel.repaint();
                BannerPanel.revalidate();
            } catch (IllegalArgumentException iae) {
            }
            AreasSpinner.setValue(tournament.getFightingAreas());
        } catch (IOException ex) {
            Logger.getLogger(NewCompetitor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException npe) {
        }
    }

    @Override
    public String defaultFileName() {
        try {
            if (NameTextField.getText().length() > 0) {
                return NameTextField.getText().replace("/", "-");
            } else {
                return "TournamentAccreditationCards";
            }
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public boolean storeTournament() {
        if (NameTextField.getText().length() > 0) {
            Tournament tournament = new Tournament(NameTextField.getText().trim(), (Integer) AreasSpinner.getValue(), 1, (Integer) NumCompetitorsSpinner.getValue(), TournamentType.SIMPLE);
            tournament.addBanner(banner.photoInput, banner.size);
            //Store tournament into database
            if (KendoTournamentGenerator.getInstance().database.storeTournament(tournament, true)) {
                KendoTournamentGenerator.getInstance().changeLastSelectedTournament(NameTextField.getText());
                TournamentPool.addTournament(tournament);
                cleanWindow();
            }
            //If tournamnet team size has changed (tournament update), delete old teams of tournament.
            if(maxCompetitorTeam != null && maxCompetitorTeam != tournament.getTeamSize()){
                KendoTournamentGenerator.getInstance().database.deleteTeamsOfTournament(tournament, false);
            }
            return true;
        } else {
            MessageManager.errorMessage("noTournamentFieldsFilled", "MySQL");
        }
        return false;
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

    /**
     * Add the same action listener to all langugaes of the menu.
     *
     * @param al
     */
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

        TournamentPanel = new javax.swing.JPanel();
        NameLabel = new javax.swing.JLabel();
        NameTextField = new javax.swing.JTextField();
        BannerLabel = new javax.swing.JLabel();
        BannerTextField = new javax.swing.JTextField();
        ExploreButton = new javax.swing.JButton();
        BannerPanel = new javax.swing.JPanel();
        FightingAreasLabel = new javax.swing.JLabel();
        AreasSpinner = new javax.swing.JSpinner();
        NumCompetitorsSpinner = new javax.swing.JSpinner();
        NumberCompetitorsLabel = new javax.swing.JLabel();
        CancelButton = new javax.swing.JButton();
        AcceptButton = new javax.swing.JButton();
        SearchButton = new javax.swing.JButton();
        PDFButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Generate a new tournament");
        setResizable(false);

        TournamentPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        NameLabel.setText("Name:");

        BannerLabel.setText("Banner:");

        ExploreButton.setText("Explore");
        ExploreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExploreButtonActionPerformed(evt);
            }
        });

        BannerPanel.setLayout(new java.awt.BorderLayout());

        FightingAreasLabel.setText("Fighting Areas:");

        AreasSpinner.setValue(1);
        AreasSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                AreasSpinnerStateChanged(evt);
            }
        });

        NumCompetitorsSpinner.setModel(new javax.swing.SpinnerNumberModel(3, 1, 7, 2));
        NumCompetitorsSpinner.setValue(3);
        NumCompetitorsSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                NumCompetitorsSpinnerStateChanged(evt);
            }
        });

        NumberCompetitorsLabel.setText("Num Competitors:");

        javax.swing.GroupLayout TournamentPanelLayout = new javax.swing.GroupLayout(TournamentPanel);
        TournamentPanel.setLayout(TournamentPanelLayout);
        TournamentPanelLayout.setHorizontalGroup(
            TournamentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TournamentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TournamentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BannerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
                    .addGroup(TournamentPanelLayout.createSequentialGroup()
                        .addGroup(TournamentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(FightingAreasLabel)
                            .addComponent(BannerLabel)
                            .addComponent(NameLabel)
                            .addComponent(NumberCompetitorsLabel))
                        .addGap(39, 39, 39)
                        .addGroup(TournamentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(NumCompetitorsSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                            .addGroup(TournamentPanelLayout.createSequentialGroup()
                                .addComponent(BannerTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ExploreButton))
                            .addComponent(AreasSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                            .addComponent(NameTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE))))
                .addContainerGap())
        );
        TournamentPanelLayout.setVerticalGroup(
            TournamentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TournamentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(TournamentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER, false)
                    .addComponent(NameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(TournamentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NumCompetitorsSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NumberCompetitorsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(TournamentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(AreasSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FightingAreasLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(TournamentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(BannerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BannerLabel)
                    .addComponent(ExploreButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BannerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });

        AcceptButton.setText("Accept");

        SearchButton.setText("Search");

        PDFButton.setText("PDF");
        PDFButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PDFButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(TournamentPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(SearchButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PDFButton, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 129, Short.MAX_VALUE)
                        .addComponent(AcceptButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AcceptButton, CancelButton, PDFButton, SearchButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TournamentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CancelButton)
                    .addComponent(AcceptButton)
                    .addComponent(SearchButton)
                    .addComponent(PDFButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void ExploreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExploreButtonActionPerformed
        String file;
        if (!(file = exploreWindow("Select",
                JFileChooser.FILES_ONLY)).equals("")) {
            BannerTextField.setText(file);
            banner.ChangePhoto(file);
            banner.repaint();
            BannerPanel.repaint();
            BannerPanel.revalidate();
        }
    }//GEN-LAST:event_ExploreButtonActionPerformed

    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_CancelButtonActionPerformed

    private void AreasSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_AreasSpinnerStateChanged
        if ((Integer) AreasSpinner.getValue() < 1) {
            AreasSpinner.setValue(1);
        }
    }//GEN-LAST:event_AreasSpinnerStateChanged

    private void PDFButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PDFButtonActionPerformed
        if (NameTextField.getText().length() > 0) {
            Tournament t = new Tournament(NameTextField.getText(), (Integer) AreasSpinner.getValue(), 1, (Integer) NumCompetitorsSpinner.getValue(), TournamentType.SIMPLE);

            try {
                String file;
                if (!(file = exploreWindowsForPdf(trans.returnTag("ExportPDF"),
                        JFileChooser.FILES_AND_DIRECTORIES, "")).equals("")) {
                    TournamentAccreditationPDF pdf = new TournamentAccreditationPDF(t);
                    pdf.setPrintAll(true);
                    pdf.createFile(file);
                }
            } catch (Exception ex) {
                KendoTournamentGenerator.showErrorInformation(ex);
            }
        } else {
            MessageManager.errorMessage("noTournamentFieldsFilled", "MySQL");
        }
    }//GEN-LAST:event_PDFButtonActionPerformed

    private void NumCompetitorsSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_NumCompetitorsSpinnerStateChanged
        if ((Integer) NumCompetitorsSpinner.getValue() < 1) {
            NumCompetitorsSpinner.setValue(1);
        }
    }//GEN-LAST:event_NumCompetitorsSpinnerStateChanged
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AcceptButton;
    private javax.swing.JSpinner AreasSpinner;
    private javax.swing.JLabel BannerLabel;
    private javax.swing.JPanel BannerPanel;
    private javax.swing.JTextField BannerTextField;
    private javax.swing.JButton CancelButton;
    private javax.swing.JButton ExploreButton;
    private javax.swing.JLabel FightingAreasLabel;
    private javax.swing.JLabel NameLabel;
    private javax.swing.JTextField NameTextField;
    private javax.swing.JSpinner NumCompetitorsSpinner;
    private javax.swing.JLabel NumberCompetitorsLabel;
    private javax.swing.JButton PDFButton;
    private javax.swing.JButton SearchButton;
    private javax.swing.JPanel TournamentPanel;
    // End of variables declaration//GEN-END:variables
}
