package com.softwaremagico.ktg.gui.fight;
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

import com.softwaremagico.ktg.FightPool;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.files.Path;
import com.softwaremagico.ktg.gui.PhotoFrame;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.tournament.TournamentGroup;
import com.softwaremagico.ktg.gui.tournament.TournamentGroupPool;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 *
 * @author jorge
 */
public final class Monitor extends javax.swing.JFrame {

    Tournament selectedTournament = null;
    int fightArea = 0;
    private PhotoFrame banner;
    Timer timer;
    private final int seconds = 5;
    Translator trans = null;
    //private int useOnlyShiaijo = -1;
    private List<TournamentGroup> finishedGroups = new ArrayList<>();
    private List<TournamentGroup> showedRanking = new ArrayList<>();
    private ScorePanel scorePanel;
    private boolean showAllArenas = false;

    /**
     * Creates new form Monitor
     */
    public Monitor(Tournament championship, int shiaijos) {
        selectedTournament = championship;
        scorePanel = new ScorePanel(selectedTournament);
        if (shiaijos < 0) {
            showAllArenas = true;
        } else {
            fightArea = shiaijos;
        }
        trans = LanguagePool.getTranslator("gui.xml");
        initComponents();
        //setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
        //        (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        this.setSize(new Dimension((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
        setLanguage();
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        scorePanel.setBounds(new Rectangle(FightsPanel.getSize().width, FightsPanel.getSize().height));
        FightsPanel.add(scorePanel);

        ColourCheckBox.setSelected(KendoTournamentGenerator.getInstance().inverseColours);
        InverseCheckBox.setSelected(KendoTournamentGenerator.getInstance().inverseTeams);

        if (selectedTournament.getFightingAreas() == 1) {
            fightArea = 0;
            showAllArenas = false;
        }

        createBanner();
        setTournament();
        //fillFightsPanel();
        //updateArenaLabel();
        updateFightPanel();
        try {
            startTimer();
        } catch (NullPointerException npe) {
        }
    }

    public void fillFightsPanel() {
        scorePanel.fillFightsPanel(fightArea);
    }

    /**
     * Translate the GUI to the selected language.
     */
    public void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
        this.setTitle(trans.returnTag("titleFightPanel"));
        InverseCheckBox.setText(trans.returnTag("InverseCheckBox"));
        ColourCheckBox.setText(trans.returnTag("ColourCheckBox"));
    }

    private void updateArenaLabel() {
        ArenaLabel.setText(trans.returnTag("FightArea") + " " + KendoTournamentGenerator.getInstance().returnShiaijo(fightArea));
    }

    private void showRemainingStatistics() {
        for (int i = 0; i < finishedGroups.size(); i++) {
            if (!showedRanking.contains(finishedGroups.get(i))) {
                MonitorFightPosition mfp = new MonitorFightPosition(finishedGroups.get(i), true);
                mfp.setVisible(true);
                showedRanking.add(finishedGroups.get(i));
                break; //Only show one window each timer loop. 
            }
        }
    }

    private void startTimer() {
        timer = new Timer(seconds * 1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                //Show the winner of the group
                for (int i = 0; i < TournamentGroupPool.getManager(selectedTournament).size(); i++) {
                    //Only groups of this arena. 
                    if (TournamentGroupPool.getManager(selectedTournament).getGroup(i).arena == fightArea || fightArea == -1) {
                        //Only groups of this level.
                        if (TournamentGroupPool.getManager(selectedTournament).getGroup(i).getLevel() == FightPool.getManager(selectedTournament).getLastLevel()) {
                            if (TournamentGroupPool.getManager(selectedTournament).getGroup(i).areFightsOver()) {
                                if (!finishedGroups.contains(TournamentGroupPool.getManager(selectedTournament).getGroup(i))) {
                                    finishedGroups.add(TournamentGroupPool.getManager(selectedTournament).getGroup(i));
                                }
                            }
                        }
                    }
                }
                showRemainingStatistics();
                updateFightPanel();
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    private void updateFightPanel() {
        if (showAllArenas) {
            fightArea++;
        }
        FightPool.getManager(selectedTournament).getFightsFromDatabase(selectedTournament);

            //If the designedgroups are loaded, we can select only the shiaijos used in this level.
            try {
                if (fightArea >= TournamentGroupPool.getManager(selectedTournament).getArenasOfLevel(FightPool.getManager(selectedTournament).getFights(), FightPool.getManager(selectedTournament).get(FightPool.getManager(selectedTournament).size() - 1).getLevel())) {
                    fightArea = 0;
                }
            } catch (NullPointerException npe2) {
                fightArea = 0;
            }
        updateArenaLabel();
        fillFightsPanel();
    }

    private void setTournament() {
        try {
            banner.CleanPhoto();
            banner.ChangePhoto(selectedTournament.banner(), selectedTournament.getBannerInput(), selectedTournament.getBannerSize());
            BannerPanel.repaint();
            BannerPanel.revalidate();
            FightPool.getManager(selectedTournament).getFightsFromDatabase(selectedTournament);
        } catch (IOException | IllegalArgumentException | NullPointerException ex) {
            //ex.printStackTrace();
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

    private void close() {
        try {
            timer.stop();
            timer = null;
        } catch (NullPointerException npe) {
        }
        this.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        FightsPanel = new javax.swing.JPanel();
        BannerPanel = new javax.swing.JPanel();
        ArenaLabel = new javax.swing.JLabel();
        InverseCheckBox = new javax.swing.JCheckBox();
        ColourCheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        FightsPanel.setLayout(new javax.swing.BoxLayout(FightsPanel, javax.swing.BoxLayout.Y_AXIS));

        BannerPanel.setLayout(new java.awt.BorderLayout());

        ArenaLabel.setFont(new java.awt.Font("Dialog", 1, 48)); // NOI18N
        ArenaLabel.setText("Shiaijo A");

        InverseCheckBox.setText("Inverse");
        InverseCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InverseCheckBoxActionPerformed(evt);
            }
        });
        InverseCheckBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                InverseCheckBoxKeyReleased(evt);
            }
        });

        ColourCheckBox.setText("Colour");
        ColourCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ColourCheckBoxActionPerformed(evt);
            }
        });
        ColourCheckBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ColourCheckBoxKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(FightsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1003, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BannerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                        .addGap(58, 58, 58)
                        .addComponent(ArenaLabel)
                        .addGap(148, 148, 148))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(ColourCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(InverseCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(BannerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(ArenaLabel)))
                .addGap(19, 19, 19)
                .addComponent(FightsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(InverseCheckBox)
                    .addComponent(ColourCheckBox))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        fillFightsPanel();
    }//GEN-LAST:event_formComponentResized

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        int ke = evt.getKeyCode();

        // ESC or q pressed (with bloq num and without)
        if ((ke == 1048689) || (ke == 1048603) || (ke == 27) || (ke == 113)) {
            close();
        }
    }//GEN-LAST:event_formKeyReleased

    private void InverseCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InverseCheckBoxActionPerformed
        KendoTournamentGenerator.getInstance().inverseTeams = InverseCheckBox.isSelected();
        fillFightsPanel();
    }//GEN-LAST:event_InverseCheckBoxActionPerformed

    private void InverseCheckBoxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_InverseCheckBoxKeyReleased
        int ke = evt.getKeyCode();

        // ESC or q pressed (with bloq num and without)
        if ((ke == 1048689) || (ke == 1048603) || (ke == 27) || (ke == 113)) {
            close();
        }
    }//GEN-LAST:event_InverseCheckBoxKeyReleased

    private void ColourCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ColourCheckBoxActionPerformed
        KendoTournamentGenerator.getInstance().inverseColours = ColourCheckBox.isSelected();
        fillFightsPanel();
    }//GEN-LAST:event_ColourCheckBoxActionPerformed

    private void ColourCheckBoxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ColourCheckBoxKeyReleased
        int ke = evt.getKeyCode();

        // ESC or q pressed (with bloq num and without)
        if ((ke == 1048689) || (ke == 1048603) || (ke == 27) || (ke == 113)) {
            close();
        }
    }//GEN-LAST:event_ColourCheckBoxKeyReleased

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        this.toFront();
    }//GEN-LAST:event_formWindowOpened
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ArenaLabel;
    private javax.swing.JPanel BannerPanel;
    private javax.swing.JCheckBox ColourCheckBox;
    private javax.swing.JPanel FightsPanel;
    private javax.swing.JCheckBox InverseCheckBox;
    // End of variables declaration//GEN-END:variables
}
