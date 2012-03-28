/*
 * Monitor.java
 *
 * Created on 9 de septiembre de 2009, 11:14
 */
package com.softwaremagico.ktg.gui.fight;

import com.softwaremagico.ktg.files.Path;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.Timer;
import com.softwaremagico.ktg.gui.PhotoFrame;
import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.language.Translator;
import com.softwaremagico.ktg.leaguedesigner.DesignedGroup;
import com.softwaremagico.ktg.leaguedesigner.DesignedGroups;

/**
 *
 * @author  jorge
 */
public final class Monitor extends javax.swing.JFrame {

    Tournament selectedTournament = null;
    int fightArea = 0;
    private PhotoFrame banner;
    Timer timer;
    private final int seconds = 5;
    Translator trans = null;
    private int useOnlyShiaijo = -1;
    private List<DesignedGroup> finishedGroups = new ArrayList<DesignedGroup>();
    private List<DesignedGroup> showedRanking = new ArrayList<DesignedGroup>();

    /** Creates new form Monitor */
    public Monitor(Tournament championship, int shiaijos) {
        selectedTournament = championship;
        useOnlyShiaijo = shiaijos;
        trans = new Translator("gui.xml");
        initComponents();
        //setLocation((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - (int) (this.getWidth() / 2),
        //        (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - (int) (this.getHeight() / 2));
        this.setSize(new Dimension((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
        setLanguage(KendoTournamentGenerator.getInstance().language);
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        ColourCheckBox.setSelected(KendoTournamentGenerator.getInstance().inverseColours);
        InverseCheckBox.setSelected(KendoTournamentGenerator.getInstance().inverseTeams);

        if (selectedTournament.fightingAreas == 1) {
            useOnlyShiaijo = 0;
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

    /**
     * Translate the GUI to the selected language.
     */
    public void setLanguage(String language) {
        trans = new Translator("gui.xml");
        this.setTitle(trans.returnTag("titleFightPanel", language));
        InverseCheckBox.setText(trans.returnTag("InverseCheckBox", language));
        ColourCheckBox.setText(trans.returnTag("ColourCheckBox", language));
    }

    private void updateArenaLabel() {
        ArenaLabel.setText(trans.returnTag("FightArea", KendoTournamentGenerator.getInstance().language) + " " + KendoTournamentGenerator.getInstance().shiaijosName[fightArea]);
    }

    private void showRemainingStatistics() {
        for (int i = 0; i < finishedGroups.size(); i++) {
            if (!showedRanking.contains(finishedGroups.get(i))) {
                MonitorFightPosition mfp = new MonitorFightPosition(KendoTournamentGenerator.getInstance().fights.getFights(), finishedGroups.get(i), true);
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
                //if (useOnlyShiaijo > -1) {
                //Show the winner of the group
                for (int i = 0; i < KendoTournamentGenerator.getInstance().designedGroups.size(); i++) {
                    //DesignedGroup groupFinished = KendoTournamentGenerator.getInstance().designedGroups.get(KendoTournamentGenerator.getInstance().designedGroups.getGroupOfFight(KendoTournamentGenerator.getInstance().fights.getFights(), KendoTournamentGenerator.getInstance().fights.getPositionOfPreviousAreaFight(useOnlyShiaijo)));
                    if (KendoTournamentGenerator.getInstance().designedGroups.get(i).arena == useOnlyShiaijo || useOnlyShiaijo == -1) {
                        if (KendoTournamentGenerator.getInstance().designedGroups.get(i).areFightsOver()) {
//                            if(KendoTournamentGenerator.getInstance().designedGroups.get(i).getLevel() >= KendoTournamentGenerator.getInstance().designedGroups.firstLevelNotFinished(KendoTournamentGenerator.getInstance().fights.getFights()))
                            if (!finishedGroups.contains(KendoTournamentGenerator.getInstance().designedGroups.get(i))) {
                                finishedGroups.add(KendoTournamentGenerator.getInstance().designedGroups.get(i));
                            }
                        }
                    }
                }
                showRemainingStatistics();
                // }
                updateFightPanel();
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    private void updateFightPanel() {
        if (useOnlyShiaijo > -1) {
            fightArea = useOnlyShiaijo;
        } else {
            fightArea++;
        }

        KendoTournamentGenerator.getInstance().fights.getFightsFromDatabase(selectedTournament.name);

        if (KendoTournamentGenerator.getInstance().designedGroups == null || !KendoTournamentGenerator.getInstance().designedGroups.returnTournament().name.equals(selectedTournament.name)) {
            KendoTournamentGenerator.getInstance().designedGroups = new DesignedGroups(selectedTournament, KendoTournamentGenerator.getInstance().language, KendoTournamentGenerator.getInstance().getLogOption());
            KendoTournamentGenerator.getInstance().designedGroups.refillDesigner(KendoTournamentGenerator.getInstance().fights.getFights());
        }

        //If the designedgroups are not loaded, we use the default number of shiaijos.
        if (KendoTournamentGenerator.getInstance().designedGroups == null) {
            try {
                if (fightArea >= selectedTournament.fightingAreas) {
                    fightArea = 0;
                }
            } catch (NullPointerException npe2) {
                fightArea = 0;
            }
        } else {
            //If the designedgroups are loaded, we can select only the shiaijos used in this level.
            try {
                if (fightArea >= KendoTournamentGenerator.getInstance().designedGroups.getArenasOfLevel(KendoTournamentGenerator.getInstance().fights.getFights(), KendoTournamentGenerator.getInstance().fights.get(KendoTournamentGenerator.getInstance().fights.size() - 1).level)) {
                    fightArea = 0;
                }
            } catch (NullPointerException npe2) {
                fightArea = 0;
            }
        }
        updateArenaLabel();
        fillFightsPanel();
    }

    public void fillFightsPanel() {
        FightsPanel.removeAll();
        if (useOnlyShiaijo > -1) {
            fightArea = useOnlyShiaijo;
        }
        if (KendoTournamentGenerator.getInstance().fights.size() > 0) {
            RoundFight rf;
            Fight f;
            int showedFights = 0;
            Dimension minSize = new Dimension(0, 5);
            Dimension prefSize = new Dimension(5, 5);
            Dimension maxSize = new Dimension(5, 5);

            //Penultimus
            if (numberOfFightsToShow() > 4) {
                f = KendoTournamentGenerator.getInstance().fights.getPreviousOfPreviousAreaFight((Integer) fightArea);
                if (f != null) {
                    rf = new RoundFight(f, false, KendoTournamentGenerator.getInstance().fights.currentArenaFight(fightArea) - 2, KendoTournamentGenerator.getInstance().fights.arenaSize(fightArea));
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
                f = KendoTournamentGenerator.getInstance().fights.getPreviousAreaFight(fightArea);
                if (f != null) {
                    rf = new RoundFight(f, false, KendoTournamentGenerator.getInstance().fights.currentArenaFight(fightArea) - 1, KendoTournamentGenerator.getInstance().fights.arenaSize(fightArea));
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
                rf = new RoundFight(KendoTournamentGenerator.getInstance().fights.getSelectedFight(fightArea), true, KendoTournamentGenerator.getInstance().fights.currentArenaFight(fightArea), KendoTournamentGenerator.getInstance().fights.arenaSize(fightArea));
                rf.updateScorePanels();
                FightsPanel.add(rf);
            }
            showedFights++;
            FightsPanel.add(new Box.Filler(minSize, prefSize, maxSize));

            //Nexts
            if (numberOfFightsToShow() > 1) {
                for (int i = KendoTournamentGenerator.getInstance().fights.currentArenaFight(fightArea);
                        showedFights < numberOfFightsToShow() && i < KendoTournamentGenerator.getInstance().fights.arenaSize(fightArea) - 1; i++) {
                    f = KendoTournamentGenerator.getInstance().fights.getNextAreaFight(i, fightArea);
                    if (f != null) {
                        rf = new RoundFight(f, false, KendoTournamentGenerator.getInstance().fights.currentArenaFight(fightArea) + 1, KendoTournamentGenerator.getInstance().fights.arenaSize(fightArea));
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

    private int numberOfFightsToShow() {
        return (int) FightsPanel.getHeight() / screenSizeOfTeam();
    }

    private int screenSizeOfTeam() {
        return 60 * selectedTournament.teamSize + 45;
    }

    private void setTournament() {
        try {
            banner.CleanPhoto();
            //banner.ChangeInputStream(selectedTournament.BannerInput, selectedTournament.bannerSize);
            banner.ChangePhoto(selectedTournament.banner(), selectedTournament.bannerInput, selectedTournament.bannerSize);
            BannerPanel.repaint();
            BannerPanel.revalidate();
            KendoTournamentGenerator.getInstance().fights.getFightsFromDatabase(selectedTournament.name);
        } catch (IOException ex) {
            //ex.printStackTrace();
        } catch (IllegalArgumentException iae) {
            //iae.printStackTrace();
        } catch (NullPointerException npe) {
            //npe.printStackTrace();
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

    private void close() {
        try {
            timer.stop();
            timer = null;
        } catch (NullPointerException npe) {
        }
        this.dispose();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ArenaLabel;
    private javax.swing.JPanel BannerPanel;
    private javax.swing.JCheckBox ColourCheckBox;
    private javax.swing.JPanel FightsPanel;
    private javax.swing.JCheckBox InverseCheckBox;
    // End of variables declaration//GEN-END:variables
}
