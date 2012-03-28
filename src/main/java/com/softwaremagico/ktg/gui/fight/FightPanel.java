/*
 *   This software is designed by Jorge Hortelano Otero.
 *   softwaremagico@gmail.com
 *   Copyright (C) 2012 Jorge Hortelano Otero.
 *   C/Quart 89, 3. Valencia CP:46008 (Spain).
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version 2
 *   of the License, or (at your option) any later version.
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *   Created on 02-ene-2009.
 */
package com.softwaremagico.ktg.gui.fight;

import com.softwaremagico.ktg.files.Path;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import com.softwaremagico.ktg.gui.PhotoFrame;
import com.softwaremagico.ktg.*;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.leaguedesigner.DesignedGroup;
import com.softwaremagico.ktg.leaguedesigner.DesignedGroups;

/**
 *
 * @author Jorge
 */
public final class FightPanel extends javax.swing.JFrame {

    private Translator trans = null;
    private List<Tournament> listTournaments = null;
    private Tournament selectedTournament = null;
    private PhotoFrame banner;
    private boolean refreshTournament = true;
    private MonitorPosition mp;

    /**
     * Creates new form FightPanel
     */
    public FightPanel() {
        initComponents();
        listTournaments = KendoTournamentGenerator.getInstance().database.getAllTournaments();
        setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        setLanguage(KendoTournamentGenerator.getInstance().language);
        fillTournaments();
        createBanner();

        try {
            selectedTournament = KendoTournamentGenerator.getInstance().database.getTournamentByName(TournamentComboBox.getSelectedItem().toString(), true);
            updateTournament();
            if (KendoTournamentGenerator.getInstance().designedGroups == null) {
                KendoTournamentGenerator.getInstance().designedGroups = new DesignedGroups(selectedTournament, KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
                KendoTournamentGenerator.getInstance().designedGroups.refillDesigner(KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(TournamentComboBox.getSelectedItem().toString()));
            }
        } catch (NullPointerException npe) {
        }
        changeNextButtonText();
        hideTreeButton();

        ColourCheckBox.setSelected(KendoTournamentGenerator.getInstance().inverseColours);
        InverseCheckBox.setSelected(KendoTournamentGenerator.getInstance().inverseTeams);
    }

    /**
     * Translate the GUI to the selected language.
     */
    public void setLanguage(String language) {
        trans = new Translator("gui.xml");
        this.setTitle(trans.returnTag("titleFightPanel", language));
        CloseButton.setText(trans.returnTag("CloseButton", language));
        TreeButton.setText(trans.returnTag("TreeButton", language));
        TournamentLabel.setText(trans.returnTag("TournamentLabel", language));
        FightAreaLabel.setText(trans.returnTag("FightArea", language));
        PreviousButton.setText(trans.returnTag("PreviousButton", language));
        NextButton.setText(trans.returnTag("NextButton", language));
        DeleteButton.setText(trans.returnTag("DeleteFigthtButton", language));
        AddButton.setText(trans.returnTag("AddFigthtButton", language));
        InverseCheckBox.setText(trans.returnTag("InverseCheckBox", language));
        ColourCheckBox.setText(trans.returnTag("ColourCheckBox", language));
        RefreshButton.setText(trans.returnTag("RefreshButton", language));
        RankingButton.setText(trans.returnTag("NumberOfWinnedTopTen", language));
    }

    private void hideTreeButton() {
        try {
            if (KendoTournamentGenerator.getInstance().designedGroups.size() > 1 && !selectedTournament.mode.equals("simple")) {
                TreeButton.setVisible(true);
            } else {
                TreeButton.setVisible(false);
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            TreeButton.setVisible(false);
        }
    }

    private int numberOfFightsToShow() {
        return (int) FightsPanel.getHeight() / screenSizeOfTeam();
    }

    private int screenSizeOfTeam() {
        try {
            return 60 * selectedTournament.teamSize + 45;
        } catch (NullPointerException npe) {
            return 225;
        }
    }

    public void fillFightsPanel() {
        //roundFights = new ArrayList<RoundFight>();
        FightsPanel.removeAll();
        if (KendoTournamentGenerator.getInstance().fights.size() > 0) {
            RoundFight rf;
            Fight f;
            int showedFights = 0;
            Dimension minSize = new Dimension(0, 5);
            Dimension prefSize = new Dimension(5, 5);
            Dimension maxSize = new Dimension(5, 5);

            //Penultimus
            if (numberOfFightsToShow() > 4) {
                f = KendoTournamentGenerator.getInstance().fights.getPreviousOfPreviousAreaFight((Integer) FightAreaComboBox.getSelectedIndex());
                if (f != null) {
                    rf = new RoundFight(f, false, KendoTournamentGenerator.getInstance().fights.currentArenaFight((Integer) FightAreaComboBox.getSelectedIndex()) - 2, KendoTournamentGenerator.getInstance().fights.arenaSize((Integer) FightAreaComboBox.getSelectedIndex()));
                } else {
                    rf = new RoundFight(selectedTournament.teamSize, false, 0, 0);
                }
                showedFights++;
                rf.updateScorePanels();
                FightsPanel.add(rf);
                FightsPanel.add(new Box.Filler(minSize, prefSize, maxSize));
            }
            //Previous
            if (numberOfFightsToShow() > 2) {
                f = KendoTournamentGenerator.getInstance().fights.getPreviousAreaFight(FightAreaComboBox.getSelectedIndex());
                if (f != null) {
                    rf = new RoundFight(f, false, KendoTournamentGenerator.getInstance().fights.currentArenaFight((Integer) FightAreaComboBox.getSelectedIndex()) - 1, KendoTournamentGenerator.getInstance().fights.arenaSize((Integer) FightAreaComboBox.getSelectedIndex()));
                } else {
                    rf = new RoundFight(selectedTournament.teamSize, false, 0, 0);
                }
                showedFights++;
                rf.updateScorePanels();
                FightsPanel.add(rf);
                FightsPanel.add(new Box.Filler(minSize, prefSize, maxSize));
            }
            //Current
            if (KendoTournamentGenerator.getInstance().fights.size() > 0) {
                rf = new RoundFight(KendoTournamentGenerator.getInstance().fights.getSelectedFight(FightAreaComboBox.getSelectedIndex()), true, KendoTournamentGenerator.getInstance().fights.currentArenaFight((Integer) FightAreaComboBox.getSelectedIndex()), KendoTournamentGenerator.getInstance().fights.arenaSize((Integer) FightAreaComboBox.getSelectedIndex()));
                rf.updateScorePanels();
                FightsPanel.add(rf);
            }
            showedFights++;
            FightsPanel.add(new Box.Filler(minSize, prefSize, maxSize));

            //Nexts
            if (numberOfFightsToShow() > 1) {
                for (int i = KendoTournamentGenerator.getInstance().fights.currentArenaFight(FightAreaComboBox.getSelectedIndex());
                        showedFights < numberOfFightsToShow() && i < KendoTournamentGenerator.getInstance().fights.arenaSize(FightAreaComboBox.getSelectedIndex()) - 1; i++) {
                    f = KendoTournamentGenerator.getInstance().fights.getNextAreaFight(i, FightAreaComboBox.getSelectedIndex());
                    if (f != null) {
                        rf = new RoundFight(f, false, KendoTournamentGenerator.getInstance().fights.currentArenaFight((Integer) FightAreaComboBox.getSelectedIndex()) + 1, KendoTournamentGenerator.getInstance().fights.arenaSize((Integer) FightAreaComboBox.getSelectedIndex()));
                    } else {
                        rf = new RoundFight(selectedTournament.teamSize, false, 0, 0);
                    }
                    showedFights++;
                    rf.updateScorePanels();
                    FightsPanel.add(rf);
                    FightsPanel.add(new Box.Filler(minSize, prefSize, maxSize));
                }
            }

            //Add null fights to complete the panel.
            while (showedFights < numberOfFightsToShow()) {
                try {
                    rf = new RoundFight(selectedTournament.teamSize, false, 0, 0);
                    FightsPanel.add(rf);
                    FightsPanel.add(new Box.Filler(minSize, prefSize, maxSize));
                } catch (NullPointerException npe) {
                }
                showedFights++;
            }
        }
        FightsPanel.repaint();
        FightsPanel.revalidate();
    }

    private void fillTournaments() {
        refreshTournament = false;
        try {
            for (int i = 0; i < listTournaments.size(); i++) {
                TournamentComboBox.addItem(listTournaments.get(i).name);
            }
            TournamentComboBox.setSelectedItem(KendoTournamentGenerator.getInstance().getLastSelectedTournament());
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        refreshTournament = true;
        //fillFightsPanel();
    }

    private void fillFightingAreas() {
        refreshTournament = false;
        FightAreaComboBox.removeAllItems();
        //selectedTournament = KendoTournamentGenerator.getInstance().database.getTournamentByName(TournamentComboBox.getSelectedItem().toString());
        try {
            for (int i = 0; i < selectedTournament.fightingAreas; i++) {
                if (i < KendoTournamentGenerator.getInstance().shiaijosName.length) {
                    FightAreaComboBox.addItem(KendoTournamentGenerator.getInstance().shiaijosName[i] + "");
                } else {
                    FightAreaComboBox.addItem((i + 1) + "");
                }
            }
        } catch (NullPointerException npe) {
        }
        refreshTournament = true;
    }

    private void updateTournament() {
        try {
            banner.CleanPhoto();
            //banner.ChangeInputStream(selectedTournament.BannerInput, selectedTournament.bannerSize);
            banner.ChangePhoto(selectedTournament.banner(), selectedTournament.bannerInput, selectedTournament.bannerSize);
            BannerPanel.repaint();
            BannerPanel.revalidate();
            fillFightingAreas();
            KendoTournamentGenerator.getInstance().fights.getFightsFromDatabase(TournamentComboBox.getSelectedItem().toString());
            fillFightsPanel();
        } catch (IOException ex) {
        }
    }

    private void changeTournament() {
        try {
            selectedTournament = KendoTournamentGenerator.getInstance().database.getTournamentByName(TournamentComboBox.getSelectedItem().toString(), true);
            KendoTournamentGenerator.getInstance().changeLastSelectedTournament(selectedTournament.name);
            KendoTournamentGenerator.getInstance().designedGroups = new DesignedGroups(selectedTournament, KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().designedGroups.refillDesigner(KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(TournamentComboBox.getSelectedItem().toString()));
            updateTournament();
        } catch (IllegalArgumentException iae) {
        } catch (NullPointerException npe) {
        }
    }

    /**
     * Show the photo of the selected user or a default one.
     */
    private void createBanner() {
        banner = new PhotoFrame(BannerPanel, Path.returnDefaultBanner());
        //banner.setPreferredSize(new Dimension(BannerPanel.getWidth(), BannerPanel.getHeight()));
        BannerPanel.add(banner, 0);
        BannerPanel.revalidate();
        banner.repaint();
    }

    public String getSelectedTournament() {
        try {
            return TournamentComboBox.getSelectedItem().toString();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public int getSelectedArena() {
        return FightAreaComboBox.getSelectedIndex();
    }

    private boolean isThisGroupOver(DesignedGroup dg) {
        /*
         * Only applied to middle combats
         */
        /*
         * if
         * (KendoTournamentGenerator.getInstance().fights.getPreviousAreaFight(FightAreaComboBox.getSelectedIndex())
         * != null &&
         * KendoTournamentGenerator.getInstance().fights.getNextAreaFight(FightAreaComboBox.getSelectedIndex())
         * != null) { if
         * (KendoTournamentGenerator.getInstance().designedGroups.getGroupOfFight(KendoTournamentGenerator.getInstance().fights.getFights(),
         * KendoTournamentGenerator.getInstance().fights.getPositionOfPreviousAreaFight(FightAreaComboBox.getSelectedIndex()))
         * !=
         * KendoTournamentGenerator.getInstance().designedGroups.getGroupOfFight(KendoTournamentGenerator.getInstance().fights.getFights(),
         * KendoTournamentGenerator.getInstance().fights.currentFight(FightAreaComboBox.getSelectedIndex())))
         * { return true; } }
         */
        try {
            if (dg.areFightsOver()) {
                return true;
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
            return true;
        }
        return false;
    }

    private void showWinnersOfGroup(DesignedGroup groupFinished, boolean message) {
        String text = "";
        //int groupFinished = KendoTournamentGenerator.getInstance().designedGroups.getGroupOfFight(KendoTournamentGenerator.getInstance().fights.getFights(), KendoTournamentGenerator.getInstance().fights.getPositionOfPreviousAreaFight(FightAreaComboBox.getSelectedIndex()));
        List<Team> winnersOfGroup = new ArrayList<Team>();
        for (int i = 0; i < groupFinished.returnMaxNumberOfWinners(); i++) {
            winnersOfGroup.add(groupFinished.getTeamInOrderOfScore(i, KendoTournamentGenerator.getInstance().fights.getFights(), true));
        }
        for (int i = 0; i < winnersOfGroup.size(); i++) {
            if (i > 0) {
                if (winnersOfGroup.get(i).returnName().contains(", ")) {
                    text += "; ";
                } else {
                    text += ", ";
                }
            }
            text += winnersOfGroup.get(i).returnName();
        }
        if (message) {
            MessageManager.customMessage("winnerOfgroup", "!!!!!!!", KendoTournamentGenerator.getInstance().language, text, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
        }
    }

    private void changeNextButtonText() {
        //Change label of next button when is last fight.
        if (KendoTournamentGenerator.getInstance().fights.currentArenaFight(FightAreaComboBox.getSelectedIndex()) >= KendoTournamentGenerator.getInstance().fights.arenaSize(FightAreaComboBox.getSelectedIndex()) - 1) {
            NextButton.setText(trans.returnTag("FinishtButton", KendoTournamentGenerator.getInstance().language));
        } else {
            NextButton.setText(trans.returnTag("NextButton", KendoTournamentGenerator.getInstance().language));
        }
    }

    private Fight getCurrentFight() {
        return KendoTournamentGenerator.getInstance().fights.getSelectedFight(FightAreaComboBox.getSelectedIndex());
    }

    private void messagesFinishedGroup(DesignedGroup currentGroup) {
        //When a group is finished, show different messages with the winner, score, etc.
        if (isThisGroupOver(currentGroup)) {
            //Show score.
            MonitorFightPosition mfp = new MonitorFightPosition(KendoTournamentGenerator.getInstance().fights.getFights(), currentGroup, true);
            mfp.setVisible(true);
            mfp.setExtendedState(mfp.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            boolean message;

            //The message of next group will be shown only if there are more levels in the championship.
            //The last level always only has one group!
            if (KendoTournamentGenerator.getInstance().designedGroups.returnGroupsOfLevel(currentGroup.getLevel()).size() > 1) {
                message = true;
            } else {
                message = false;
            }

            //Alert message with the passing teams. 
            showWinnersOfGroup(currentGroup, message);
        }
    }

    private void messagesFinishedSimpleChampionship() {
        if (KendoTournamentGenerator.getInstance().fights.areAllOver()) {
            //Avoid showing more than one window if the button is pressed several times. 
            if (mp != null) {
                mp.dispose();
            }
            mp = new MonitorPosition(selectedTournament);
            mp.setVisible(true);
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
        TournamentComboBox = new javax.swing.JComboBox<String>();
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
                    .addComponent(FightsPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 913, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PreviousButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(NextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(AddButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(ColourCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(DeleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(InverseCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(TreeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(RefreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(RankingButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CloseButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BannerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
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
                .addComponent(FightsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
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
            KendoTournamentGenerator.getInstance().fights.setFightAsOver(currentFight);

            //If championship or similar...
            if (!selectedTournament.mode.equals("simple") && KendoTournamentGenerator.getInstance().designedGroups.size() > 1) {
                DesignedGroup currentGroup = KendoTournamentGenerator.getInstance().designedGroups.returnGroupOfFight(currentFight);
                //Show scores, messages, etc. 
                messagesFinishedGroup(currentGroup);

                //If all arena fights are over.
                if (KendoTournamentGenerator.getInstance().fights.areArenaOver(FightAreaComboBox.getSelectedIndex())) {
                    //Update the fights from the other arenas. 
                    KendoTournamentGenerator.getInstance().fights.getFightsFromDatabase(selectedTournament.name);

                    //Obtain next fights.
                    KendoTournamentGenerator.getInstance().fights.add(KendoTournamentGenerator.getInstance().designedGroups.nextLevel(
                            KendoTournamentGenerator.getInstance().fights.getFights(), FightAreaComboBox.getSelectedIndex(), selectedTournament));
                }
            } else { //Simple championship
                messagesFinishedSimpleChampionship();
            }

            //Update GUI
            fillFightsPanel();
            changeNextButtonText();

        } catch (IndexOutOfBoundsException iob) {
            KendoTournamentGenerator.getInstance().showErrorInformation(iob);
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
    }//GEN-LAST:event_NextButtonActionPerformed

    private void PreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PreviousButtonActionPerformed
        try {
            KendoTournamentGenerator.getInstance().fights.setSelectedFightAsNotOver(FightAreaComboBox.getSelectedIndex());
            fillFightsPanel();
            changeNextButtonText();
        } catch (IndexOutOfBoundsException iob) {
            KendoTournamentGenerator.getInstance().showErrorInformation(iob);
        }
    }//GEN-LAST:event_PreviousButtonActionPerformed

    private void FightAreaComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FightAreaComboBoxActionPerformed
        //Update the fights of the other computers.
        if (refreshTournament) {
            if (selectedTournament.fightingAreas > 1) {
                KendoTournamentGenerator.getInstance().fights.getFightsFromDatabase(selectedTournament.name);
                KendoTournamentGenerator.getInstance().designedGroups.refillDesigner(KendoTournamentGenerator.getInstance().fights.getFights());
            }
            fillFightsPanel();
            changeNextButtonText();
        }
    }//GEN-LAST:event_FightAreaComboBoxActionPerformed

    private void DeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteButtonActionPerformed
        try {
            KendoTournamentGenerator.getInstance().fights.deleteSelectedFight((Integer) FightAreaComboBox.getSelectedIndex(), true);
            if (TournamentComboBox.getItemCount() > 0) {
                KendoTournamentGenerator.getInstance().designedGroups.refillDesigner(KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(TournamentComboBox.getSelectedItem().toString()));
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
        KendoTournamentGenerator.getInstance().fights.getFightsFromDatabase(selectedTournament.name);
        fillFightsPanel();
        changeNextButtonText();
        KendoTournamentGenerator.getInstance().designedGroups.refillDesigner(KendoTournamentGenerator.getInstance().fights.getFights());
        MessageManager.customMessage("RefresehdData", "MySQL", KendoTournamentGenerator.getInstance().language, JOptionPane.INFORMATION_MESSAGE, KendoTournamentGenerator.getInstance().getLogOption());
    }//GEN-LAST:event_RefreshButtonActionPerformed

    private void RankingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RankingButtonActionPerformed
        Fight currentFight = KendoTournamentGenerator.getInstance().fights.getSelectedFight(FightAreaComboBox.getSelectedIndex());

        DesignedGroup groupFinished;
        if (!KendoTournamentGenerator.getInstance().fights.getSelectedFight(FightAreaComboBox.getSelectedIndex()).equals(currentFight)) {
            groupFinished = KendoTournamentGenerator.getInstance().designedGroups.get(KendoTournamentGenerator.getInstance().designedGroups.getGroupOfFight(KendoTournamentGenerator.getInstance().fights.getFights(),
                    KendoTournamentGenerator.getInstance().fights.getPositionOfPreviousAreaFight(FightAreaComboBox.getSelectedIndex())));
        } else {
            //Last fight of the panel is a special case.
            groupFinished = KendoTournamentGenerator.getInstance().designedGroups.get(KendoTournamentGenerator.getInstance().designedGroups.getGroupOfFight(KendoTournamentGenerator.getInstance().fights.getFights(),
                    KendoTournamentGenerator.getInstance().fights.currentFight(FightAreaComboBox.getSelectedIndex())));
        }

        MonitorFightPosition mfp = new MonitorFightPosition(KendoTournamentGenerator.getInstance().fights.getFights(), groupFinished, false);
        mfp.setVisible(true);
    }//GEN-LAST:event_RankingButtonActionPerformed
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
    private javax.swing.JComboBox<String> TournamentComboBox;
    private javax.swing.JLabel TournamentLabel;
    private javax.swing.JButton TreeButton;
    // End of variables declaration//GEN-END:variables
}
