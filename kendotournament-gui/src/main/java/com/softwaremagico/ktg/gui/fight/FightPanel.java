package com.softwaremagico.ktg.gui.fight;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> C/Quart 89, 3. Valencia CP:46008 (Spain).
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

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.KendoLog;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.MessageManager;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.core.TournamentType;
import com.softwaremagico.ktg.database.DatabaseConnection;
import com.softwaremagico.ktg.database.FightPool;
import com.softwaremagico.ktg.database.TournamentPool;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.gui.PhotoFrame;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.tournament.TournamentGroup;
import com.softwaremagico.ktg.tournament.TournamentManagerPool;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public final class FightPanel extends javax.swing.JFrame {

    private Translator trans = null;
    private List<Tournament> listTournaments = null;
    private Tournament selectedTournament = null;
    private PhotoFrame banner;
    private boolean refreshTournament = true;
    private ScorePanel scorePanel;
    private MonitorPosition mp;

    /**
     * Creates new form FightPanel
     */
    public FightPanel() {
        initComponents();
        listTournaments = TournamentPool.getInstance().getAll();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage();
        fillTournaments();
        createBanner();

        try {
            selectedTournament = (Tournament) TournamentComboBox.getSelectedItem();
            scorePanel = new ScorePanel(selectedTournament);
            updateTournament();
            changeNextButtonText();
            hideTreeButton();

            scorePanel.setBounds(new Rectangle(FightsPanel.getSize().width, FightsPanel.getSize().height));
            FightsPanel.add(scorePanel);

            ColourCheckBox.setSelected(KendoTournamentGenerator.getInstance().inverseColours);
            InverseCheckBox.setSelected(KendoTournamentGenerator.getInstance().inverseTeams);
        } catch (NullPointerException npe) {
            MessageManager.errorMessage(this.getClass().getName(), "noTournament", "Panel");
            dispose();
        }

        if (FightPool.getInstance().get((Tournament) TournamentComboBox.getSelectedItem()).isEmpty()) {
            MessageManager.informationMessage(this.getClass().getName(), "noFight", "Error");
        }
    }

    /**
     * Translate the GUI to the selected language.
     */
    public void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        this.setTitle(trans.returnTag("titleFightPanel"));
        CloseButton.setText(trans.returnTag("CloseButton"));
        TreeButton.setText(trans.returnTag("TreeButton"));
        TournamentLabel.setText(trans.returnTag("TournamentLabel"));
        FightAreaLabel.setText(trans.returnTag("FightArea"));
        PreviousButton.setText(trans.returnTag("PreviousButton"));
        NextButton.setText(trans.returnTag("NextButton"));
        DeleteButton.setText(trans.returnTag("DeleteFigthtButton"));
        AddButton.setText(trans.returnTag("AddFigthtButton"));
        InverseCheckBox.setText(trans.returnTag("InverseCheckBox"));
        ColourCheckBox.setText(trans.returnTag("ColourCheckBox"));
        RefreshButton.setText(trans.returnTag("RefreshButton"));
        RankingButton.setText(trans.returnTag("NumberOfWinnedTopTen"));
    }

    private void hideTreeButton() {
        try {
            if (!selectedTournament.getType().equals(TournamentType.SIMPLE) && TournamentManagerPool.getManager(selectedTournament).getGroups().size() > 1) {
                TreeButton.setVisible(true);
            } else {
                TreeButton.setVisible(false);
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
            TreeButton.setVisible(false);
        }
    }

    public void fillFightsPanel() {
        try {
            scorePanel.fillFightsPanel(FightAreaComboBox.getSelectedIndex());
        } catch (NullPointerException npe) {
        }
    }

    private void fillTournaments() {
        refreshTournament = false;
        try {
            for (int i = 0; i < listTournaments.size(); i++) {
                TournamentComboBox.addItem(listTournaments.get(i));
            }
            selectedTournament = KendoTournamentGenerator.getInstance().getLastSelectedTournament();
            if (selectedTournament != null) {
                TournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
            } else if (TournamentComboBox.getItemCount() > 0) {
                TournamentComboBox.setSelectedIndex(0);
                KendoTournamentGenerator.getInstance().changeLastSelectedTournament(((Tournament) TournamentComboBox.getSelectedItem()).toString());
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        refreshTournament = true;
    }

    private void fillFightingAreas() {
        refreshTournament = false;
        FightAreaComboBox.removeAllItems();
        try {
            for (int i = 0; i < selectedTournament.getFightingAreas(); i++) {
                FightAreaComboBox.addItem(KendoTournamentGenerator.getFightAreaName(i));
            }
        } catch (NullPointerException npe) {
        }
        refreshTournament = true;
    }

    private void updateTournament() {
        banner.cleanPhoto();
        banner.changePhoto(selectedTournament.getBanner());
        BannerPanel.repaint();
        BannerPanel.revalidate();
        fillFightingAreas();
        fillFightsPanel();
    }

    private void changeTournament() {
        try {
            selectedTournament = (Tournament) TournamentComboBox.getSelectedItem();
            scorePanel.updateTournament(selectedTournament);
            KendoTournamentGenerator.getInstance().changeLastSelectedTournament(selectedTournament.getName());
            updateTournament();
        } catch (IllegalArgumentException | NullPointerException iae) {
        }
    }

    /**
     * Show the photo of the selected user or a default one.
     */
    private void createBanner() {
        banner = new PhotoFrame(BannerPanel, Path.getDefaultBanner());
        //banner.setPreferredSize(new Dimension(BannerPanel.getWidth(), BannerPanel.getHeight()));
        BannerPanel.add(banner, 0);
        BannerPanel.revalidate();
        banner.repaint();
    }

    public Tournament getSelectedTournament() {
        try {
            return (Tournament) TournamentComboBox.getSelectedItem();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public int getSelectedArena() {
        return FightAreaComboBox.getSelectedIndex();
    }

    private void changeNextButtonText() {
        //Change label of next button when is last fight.
        if (FightPool.getInstance().get((Tournament) TournamentComboBox.getSelectedItem()).isLastFightOfArena(FightAreaComboBox.getSelectedIndex())) {
            NextButton.setText(trans.returnTag("FinishtButton"));
        } else {
            NextButton.setText(trans.returnTag("NextButton"));
        }
    }

    private void messagesFinishedGroup(TournamentGroup currentGroup) {
        //When a group is finished, show different messages with the winner, score, etc.
        if (currentGroup.areFightsOver()) {
            //Show score.
            MonitorFightPosition mfp = new MonitorFightPosition(currentGroup, true);
            mfp.setVisible(true);
            mfp.setExtendedState(mfp.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            boolean message;

            //The message of next group will be shown only if there are more levels in the championship.
            //The last level always only has one group!
            if (TournamentManagerPool.getManager(selectedTournament).getGroups(currentGroup.getLevel()).size() > 1) {
                message = true;
            } else {
                message = false;
            }

            //Alert message with the passing teams. 
            currentGroup.showWinnersOfGroup();
        }
    }

    private void messagesFinishedSimpleChampionship(boolean verbose) {
        if (FightPool.getManager((Tournament) TournamentComboBox.getSelectedItem()).areAllOver()) {
            KendoLog.info(this.getClass().getName(), "Tournament over!");
            //Avoid showing more than one window if the button is pressed several times. 
            if (mp != null) {
                mp.dispose();
            }
            mp = new MonitorPosition(selectedTournament);
            mp.setVisible(true);
        } else {
            if (verbose) {
                MessageManager.informationMessage(this.getClass().getName(), "tournamentNotOver", "Tournament");
            }
        }
    }

    private void closeAction() {
        FightPool.getManager((Tournament) TournamentComboBox.getSelectedItem()).storeNotUpdatedFightsAndDuels();
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
    public void addTreeListener(ActionListener al) {
        TreeButton.addActionListener(al);
    }

    public void addFightListener(ActionListener al) {
        AddButton.addActionListener(al);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        CloseButton = new javax.swing.JButton();
        PreviousButton = new javax.swing.JButton();
        NextButton = new javax.swing.JButton();
        TournamentComboBox = new javax.swing.JComboBox<Tournament>();
        TournamentLabel = new javax.swing.JLabel();
        BannerPanel = new javax.swing.JPanel();
        FightAreaComboBox = new javax.swing.JComboBox<String>();
        FightAreaLabel = new javax.swing.JLabel();
        TreeButton = new javax.swing.JButton();
        DeleteButton = new javax.swing.JButton();
        AddButton = new javax.swing.JButton();
        FightsPanel = new javax.swing.JPanel();
        InverseCheckBox = new javax.swing.JCheckBox();
        ColourCheckBox = new javax.swing.JCheckBox();
        RefreshButton = new javax.swing.JButton();
        RankingButton = new javax.swing.JButton();

        setTitle("League Panel");
        setMinimumSize(new java.awt.Dimension(900, 450));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        CloseButton.setText("Close");
        CloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CloseButtonActionPerformed(evt);
            }
        });

        PreviousButton.setText("Previous Fight");
        PreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PreviousButtonActionPerformed(evt);
            }
        });

        NextButton.setText("Next Fight");
        NextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NextButtonActionPerformed(evt);
            }
        });

        TournamentComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TournamentComboBoxActionPerformed(evt);
            }
        });

        TournamentLabel.setText("Tournament:");

        BannerPanel.setOpaque(false);
        BannerPanel.setLayout(new java.awt.BorderLayout());

        FightAreaComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FightAreaComboBoxActionPerformed(evt);
            }
        });

        FightAreaLabel.setText("Fight area:");

        TreeButton.setText("Tree");

        DeleteButton.setText("Delete Fight");
        DeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteButtonActionPerformed(evt);
            }
        });

        AddButton.setText("Add Fight");

        FightsPanel.setMinimumSize(new java.awt.Dimension(0, 200));
        FightsPanel.setLayout(new javax.swing.BoxLayout(FightsPanel, javax.swing.BoxLayout.Y_AXIS));

        InverseCheckBox.setText("Inverse");
        InverseCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InverseCheckBoxActionPerformed(evt);
            }
        });

        ColourCheckBox.setText("Colour");
        ColourCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ColourCheckBoxActionPerformed(evt);
            }
        });

        RefreshButton.setText("Refresh");
        RefreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RefreshButtonActionPerformed(evt);
            }
        });

        RankingButton.setText("Ranking");
        RankingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RankingButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(FightsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PreviousButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(NextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(AddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(ColourCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(DeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(InverseCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(TreeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(RefreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(RankingButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CloseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BannerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(FightAreaLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(TournamentLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(FightAreaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TournamentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {FightAreaComboBox, TournamentComboBox});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {AddButton, CloseButton, DeleteButton, NextButton, PreviousButton, RankingButton, RefreshButton, TreeButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(BannerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TournamentComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TournamentLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(FightAreaComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(FightAreaLabel))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(FightsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PreviousButton, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(AddButton)
                            .addComponent(ColourCheckBox)
                            .addComponent(TreeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(RankingButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(DeleteButton)
                            .addComponent(InverseCheckBox)
                            .addComponent(RefreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CloseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(NextButton, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {AddButton, CloseButton, DeleteButton, RankingButton, RefreshButton, TreeButton});

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void CloseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CloseButtonActionPerformed
        closeAction();
        this.dispose();
    }//GEN-LAST:event_CloseButtonActionPerformed

    private void TournamentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TournamentComboBoxActionPerformed
        if (refreshTournament) {
            changeTournament();
            hideTreeButton();
        }
    }//GEN-LAST:event_TournamentComboBoxActionPerformed

    private void NextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NextButtonActionPerformed
        try {
            Fight currentFight = getCurrentFight();
            FightPool.getManager((Tournament) TournamentComboBox.getSelectedItem()).setFightAsOver(currentFight);

            //Store fights if it is necesary.
            FightPool.getManager((Tournament) TournamentComboBox.getSelectedItem()).storeLazyFights(FightAreaComboBox.getSelectedIndex());

            //If championship or similar...
            if (!((Tournament) TournamentComboBox.getSelectedItem()).getType().equals(TournamentType.SIMPLE) && TournamentGroupPool.getManager(((Tournament) TournamentComboBox.getSelectedItem())).size() > 1) {
                TournamentGroup currentGroup = TournamentGroupPool.getManager(((Tournament) TournamentComboBox.getSelectedItem())).getGroupOfFight(currentFight);
                //Show scores, messages, etc. 
                messagesFinishedGroup(currentGroup);

                //If all arena fights are over.
                if (FightPool.getManager((Tournament) TournamentComboBox.getSelectedItem()).areArenaOver(FightAreaComboBox.getSelectedIndex())) {
                    //Obtain next fights.
                    FightPool.getManager((Tournament) TournamentComboBox.getSelectedItem()).add(TournamentGroupPool.getManager(((Tournament) TournamentComboBox.getSelectedItem())).nextFights(
                            FightPool.getManager((Tournament) TournamentComboBox.getSelectedItem()).getFights(), FightAreaComboBox.getSelectedIndex(), ((Tournament) TournamentComboBox.getSelectedItem())));
                }
            } else { //Simple championship
                messagesFinishedSimpleChampionship(false);
            }

            //Update GUI
            fillFightsPanel();
            changeNextButtonText();

        } catch (IndexOutOfBoundsException | NullPointerException iob) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), iob);
        }
    }//GEN-LAST:event_NextButtonActionPerformed

    private void PreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PreviousButtonActionPerformed
        try {
            FightPool.getManager((Tournament) TournamentComboBox.getSelectedItem()).setSelectedFightAsNotOver(FightAreaComboBox.getSelectedIndex());
            fillFightsPanel();
            changeNextButtonText();
        } catch (IndexOutOfBoundsException iob) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), iob);
        }
    }//GEN-LAST:event_PreviousButtonActionPerformed

    private void FightAreaComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FightAreaComboBoxActionPerformed
        //Update the fightManager of the other computers.
        if (refreshTournament) {
            /*
             * if (selectedTournament.fightingAreas > 1) {
             * KendoTournamentGenerator.getInstance().fightManager.getFightsFromDatabase(selectedTournament);
             * TournamentGroupPool.getManager(selectedTournament).refillDesigner(KendoTournamentGenerator.getInstance().fightManager.getFights());
             * }
             */
            fillFightsPanel();
            changeNextButtonText();
        }
    }//GEN-LAST:event_FightAreaComboBoxActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        try {
            FightPool.getManager((Tournament) TournamentComboBox.getSelectedItem()).deleteSelectedFight((Integer) FightAreaComboBox.getSelectedIndex(), true);
            if (TournamentComboBox.getItemCount() > 0) {
                TournamentGroupPool.getManager(selectedTournament).refillDesigner(DatabaseConnection.getInstance().getDatabase().searchFightsByTournament((Tournament) TournamentComboBox.getSelectedItem()));
            }
        } catch (ArrayIndexOutOfBoundsException aiob) {
        }
        fillFightsPanel();
    }//GEN-LAST:event_DeleteButtonActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        fillFightsPanel();
    }//GEN-LAST:event_formComponentResized

    private void InverseCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InverseCheckBoxActionPerformed
        KendoTournamentGenerator.getInstance().inverseTeams = InverseCheckBox.isSelected();
        fillFightsPanel();
    }//GEN-LAST:event_InverseCheckBoxActionPerformed

    private void ColourCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ColourCheckBoxActionPerformed
        KendoTournamentGenerator.getInstance().inverseColours = ColourCheckBox.isSelected();
        fillFightsPanel();
    }//GEN-LAST:event_ColourCheckBoxActionPerformed

    private void RefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RefreshButtonActionPerformed
        FightPool.getManager((Tournament) TournamentComboBox.getSelectedItem()).getFightsFromDatabase(selectedTournament);
        fillFightsPanel();
        changeNextButtonText();
        TournamentGroupPool.getManager(selectedTournament).refillDesigner(FightPool.getManager((Tournament) TournamentComboBox.getSelectedItem()).getFights());
        MessageManager.translatedMessage(this.getClass().getName(), "RefresehdData", "MySQL", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_RefreshButtonActionPerformed

    private void RankingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RankingButtonActionPerformed
        if (((Tournament) TournamentComboBox.getSelectedItem()).getType().equals(TournamentType.SIMPLE)) {
            messagesFinishedSimpleChampionship(true);
        } else {
            Fight currentFight = FightPool.getManager((Tournament) TournamentComboBox.getSelectedItem()).getSelectedFight(FightAreaComboBox.getSelectedIndex());

            TournamentGroup groupFinished;
            if (!FightPool.getManager((Tournament) TournamentComboBox.getSelectedItem()).getSelectedFight(FightAreaComboBox.getSelectedIndex()).equals(currentFight)) {
                groupFinished = TournamentGroupPool.getManager(selectedTournament).getGroupOfFight(FightPool.getManager((Tournament) TournamentComboBox.getSelectedItem()).getFights(),
                        FightPool.getManager((Tournament) TournamentComboBox.getSelectedItem()).getPositionOfPreviousAreaFight(FightAreaComboBox.getSelectedIndex()));
            } else {
                //Last fight of the panel is a special case.
                groupFinished = TournamentGroupPool.getManager(selectedTournament).getGroupOfFight(FightPool.getManager((Tournament) TournamentComboBox.getSelectedItem()).getFights(),
                        FightPool.getManager((Tournament) TournamentComboBox.getSelectedItem()).currentFight(FightAreaComboBox.getSelectedIndex()));
            }

            MonitorFightPosition mfp = new MonitorFightPosition(groupFinished, false);
            mfp.setVisible(true);
        }
    }//GEN-LAST:event_RankingButtonActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        //Store score into database.
        //FightPool.getManager((Tournament)TournamentComboBox.getSelectedItem()).storeNotUpdatedFightsAndDuels();
    }//GEN-LAST:event_formWindowClosed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        closeAction();
    }//GEN-LAST:event_formWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddButton;
    private javax.swing.JPanel BannerPanel;
    private javax.swing.JButton CloseButton;
    private javax.swing.JCheckBox ColourCheckBox;
    private javax.swing.JButton DeleteButton;
    private javax.swing.JComboBox<String> FightAreaComboBox;
    private javax.swing.JLabel FightAreaLabel;
    private javax.swing.JPanel FightsPanel;
    private javax.swing.JCheckBox InverseCheckBox;
    private javax.swing.JButton NextButton;
    private javax.swing.JButton PreviousButton;
    private javax.swing.JButton RankingButton;
    private javax.swing.JButton RefreshButton;
    private javax.swing.JComboBox<Tournament> TournamentComboBox;
    private javax.swing.JLabel TournamentLabel;
    private javax.swing.JButton TreeButton;
    // End of variables declaration//GEN-END:variables
}
