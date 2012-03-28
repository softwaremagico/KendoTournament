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
 *   Created on 14-jul-2009.
 */
package com.softwaremagico.ktg.gui.fight;

import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.Duel;
import com.softwaremagico.ktg.KendoTournamentGenerator;
import com.softwaremagico.ktg.Competitor;
import com.softwaremagico.ktg.Score;
import com.softwaremagico.ktg.files.Path;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import com.softwaremagico.ktg.gui.PanelBackground;
import com.softwaremagico.ktg.language.Translator;

/**
 *
 * @author Jorge
 */
public class CompetitorFight extends JPanel {

    private PanelBackground round1;
    private PanelBackground round2;
    private PanelBackground faults;
    private JLabel nameLabel;
    private int height = 10;
    private Competitor competitor;
    private Fight fight;
    private Translator trans = null;
    private TeamFight teamFight;

    CompetitorFight(TeamFight tf, Competitor c, Fight f, boolean left, boolean selected, boolean menu) {
        competitor = c;
        fight = f;
        teamFight = tf;
        setLanguage(KendoTournamentGenerator.getInstance().language);
        decoration();
        try {
            if (left) {
                try {
                    fillLeftToRight(c.returnSurnameNameIni());
                } catch (NullPointerException npe) {
                    fillLeftToRight(" ");
                }
            } else {
                try {
                    fillRightToLeft(c.returnSurnameNameIni());
                } catch (NullPointerException npe) {
                    fillRightToLeft(" ");
                }
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        if (menu) {
            addAllMenus();
        }
    }

    CompetitorFight(boolean left) {
        competitor = null;
        fight = null;
        setLanguage(KendoTournamentGenerator.getInstance().language);
        decoration();
        if (left) {
            fillLeftToRight(" --- --- ");
        } else {
            fillRightToLeft(" --- --- ");
        }
    }

    /**
     * Translate the GUI to the selected language.
     */
    public final void setLanguage(String language) {
        trans = new Translator("gui.xml");
    }

    private void fillRightToLeft(String name) {
        round1 = new PanelBackground();
        round1.setBackground(new Color(255, 255, 255));
        round1.setMinimumSize(new Dimension(40, height));
        round1.setPreferredSize(new Dimension(50, height));
        round1.setMaximumSize(new Dimension(50, 60));
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(round1, BorderLayout.EAST);
        add(Box.createRigidArea(new Dimension(10, 0)));

        round2 = new PanelBackground();
        round2.setBackground(new Color(255, 255, 255));
        round2.setMinimumSize(new Dimension(40, height));
        round2.setPreferredSize(new Dimension(50, height));
        round2.setMaximumSize(new Dimension(50, 60));
        add(round2, BorderLayout.CENTER);
        add(Box.createRigidArea(new Dimension(10, 0)));

        faults = new PanelBackground();
        faults.setBackground(new Color(255, 255, 255));
        faults.setMinimumSize(new Dimension(height / 2, height));
        faults.setPreferredSize(new Dimension(15, height));
        faults.setMaximumSize(new Dimension(height / 2, 60));
        add(faults, BorderLayout.EAST);

        add(Box.createHorizontalGlue());

        nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(nameLabel, BorderLayout.EAST);

        add(Box.createRigidArea(new Dimension(10, 0)));
    }

    private void fillLeftToRight(String name) {
        nameLabel = new JLabel(name);
        Font f = nameLabel.getFont();
        //nameLabel.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(nameLabel, BorderLayout.EAST);

        add(Box.createHorizontalGlue());

        faults = new PanelBackground();
        faults.setBackground(new Color(255, 255, 255));
        faults.setMinimumSize(new Dimension(height / 2, height));
        faults.setPreferredSize(new Dimension(15, height));
        faults.setMaximumSize(new Dimension(height / 2, 60));
        add(faults, BorderLayout.EAST);

        round2 = new PanelBackground();
        round2.setBackground(new Color(255, 255, 255));
        round2.setMinimumSize(new Dimension(40, height));
        round2.setPreferredSize(new Dimension(50, height));
        round2.setMaximumSize(new Dimension(50, 60));
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(round2, BorderLayout.CENTER);

        round1 = new PanelBackground();
        round1.setBackground(new Color(255, 255, 255));
        round1.setMinimumSize(new Dimension(40, height));
        round1.setPreferredSize(new Dimension(50, height));
        round1.setMaximumSize(new Dimension(50, 60));
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(round1, BorderLayout.EAST);

        add(Box.createRigidArea(new Dimension(10, 0)));
    }

    private void decoration() {
        setLayout(new BoxLayout(this, javax.swing.BoxLayout.X_AXIS));
        setMinimumSize(new Dimension(50, 50));
        setPreferredSize(new Dimension(500, 60));
        setMaximumSize(new Dimension(Short.MAX_VALUE, 70));
    }

    private File getBackground(String image) {
        File file = new File(image);
        if (!file.exists()) {
            file = new File(Path.returnImagePath() + image);
            if (!file.exists()) {
            }
        }
        return file;
    }

    private void updateScorePanel(PanelBackground panel, Score hit) {
        try {
            panel.setBackground(getBackground(Path.returnScoreFolder() + hit.getImageName()));
            panel.revalidate();
            panel.repaint();
        } catch (IOException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }

    private void updateFaultPanel(PanelBackground panel, int fault) {
        try {
            if (fault > 0) {
                panel.setBackground(getBackground(Path.returnScoreFolder() + Score.FAULT.getImageName()));
            } else {
                panel.setBackground(getBackground(Path.returnScoreFolder() + Score.EMPTY.getImageName()));
            }
            panel.revalidate();
            panel.repaint();
        } catch (IOException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        }
    }

    void updateScorePanels() {
        int player;
        try {
            if ((player = fight.team1.getIndexOfMember(fight.level, competitor)) != -1) {
                Duel d = fight.duels.get(player);
                updateScorePanel(round1, d.hitsFromCompetitorA.get(0));
                updateScorePanel(round2, d.hitsFromCompetitorA.get(1));
                updateFaultPanel(faults, d.faultsCompetitorA);
            }
            if ((player = fight.team2.getIndexOfMember(fight.level, competitor)) != -1) {
                Duel d = fight.duels.get(player);
                updateScorePanel(round1, d.hitsFromCompetitorB.get(0));
                updateScorePanel(round2, d.hitsFromCompetitorB.get(1));
                updateFaultPanel(faults, d.faultsCompetitorB);
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
    }

    private void addAllMenus() {
        int player;
        if (fight.team1.getNumberOfMembers(fight.level) > 0
                || fight.team2.getNumberOfMembers(fight.level) > 0) {
            if ((player = fight.team1.getIndexOfMember(fight.level, competitor)) != -1) {
                addPopUpMenu(player);
            }
            if ((player = fight.team2.getIndexOfMember(fight.level, competitor)) != -1) {
                addPopUpMenu(player);
            }
        }
    }

    private void addPopUpMenu(int player) {
        round1.setComponentPopupMenu(createContextMenu(player, 0));
        round2.setComponentPopupMenu(createContextMenu(player, 1));
        faults.setComponentPopupMenu(createFaultMenu());
    }

    private JPopupMenu createContextMenu(int player, int round) {
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem menMenu = new JMenuItem();
        try {
            for (Score s : Score.getValidPoints()) {
                menMenu.setText(s.getName());
                menMenu.addActionListener(new MenuListener(player, round));
                contextMenu.add(menMenu);
            }

            menMenu = new JMenuItem();
            menMenu.setText(trans.returnTag(Score.EMPTY.getName(), KendoTournamentGenerator.getInstance().language));
            menMenu.addActionListener(new MenuListener(player, round));
            contextMenu.add(menMenu);
        } catch (IndexOutOfBoundsException iofb) {
        }

        return contextMenu;
    }

    private class MenuListener implements ActionListener {

        private int player;
        private int round;

        MenuListener(int tmp_player, int tmp_round) {
            player = tmp_player;  //Player1, Player2 or Player3.
            round = tmp_round;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem sourceItem = (JMenuItem) e.getSource();

            if (sourceItem.getText().equals(trans.returnTag(Score.EMPTY.getName(), KendoTournamentGenerator.getInstance().language))) {
                updateDuel(Score.EMPTY, round);
            } else {
                updateDuel(Score.getScore(sourceItem.getText()), round);
            }
        }
    }

    private void updateDuel(Score point, int round) {
        int index = 0;
        Duel d = null;
        try {
            if ((index = fight.team1.getIndexOfMember(fight.level, competitor)) != -1) {
                d = fight.duels.get(index);
                if (!point.equals(Score.EMPTY)) {
                    d.setResultInRound(round, point, true);
                } else {
                    d.clearResultInRound(round, true);
                }
                KendoTournamentGenerator.getInstance().database.storeDuel(d, fight, index);
                //  updateScorePanels();
            }
            if ((index = fight.team2.getIndexOfMember(fight.level, competitor)) != -1) {
                d = fight.duels.get(index);
                if (!point.equals(Score.EMPTY)) {
                    d.setResultInRound(round, point, false);
                } else {
                    d.clearResultInRound(round, false);
                }
                KendoTournamentGenerator.getInstance().database.storeDuel(d, fight, index);
                //   updateScorePanels();
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.getInstance().showErrorInformation(npe);
        }
        teamFight.roundFight.updateScorePanels();
    }

    private void increaseFault() {
        int index = 0;
        Duel d = null;
        try {
            if ((index = fight.team1.getIndexOfMember(fight.level, competitor)) != -1) {
                d = fight.duels.get(index);
                d.setFaultInRound(true);
                KendoTournamentGenerator.getInstance().database.storeDuel(d, fight, index);
                updateScorePanels();
            }
            if ((index = fight.team2.getIndexOfMember(fight.level, competitor)) != -1) {
                d = fight.duels.get(index);
                d.setFaultInRound(false);
                KendoTournamentGenerator.getInstance().database.storeDuel(d, fight, index);
                updateScorePanels();
            }
        } catch (NullPointerException npe) {
        }
        teamFight.roundFight.updateScorePanels();
    }

    private void resetFault() {
        int index = 0;
        Duel d = null;
        try {
            if ((index = fight.team1.getIndexOfMember(fight.level, competitor)) != -1) {
                d = fight.duels.get(index);
                d.resetFaults(true);
                KendoTournamentGenerator.getInstance().database.storeDuel(d, fight, index);
                updateScorePanels();
            }
            if ((index = fight.team2.getIndexOfMember(fight.level, competitor)) != -1) {
                d = fight.duels.get(index);
                d.resetFaults(false);
                KendoTournamentGenerator.getInstance().database.storeDuel(d, fight, index);
                updateScorePanels();
            }
        } catch (NullPointerException npe) {
        }
    }

    private class FaultMenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            JMenuItem sourceItem = (JMenuItem) e.getSource();
            if (sourceItem.getText().equals(trans.returnTag(Score.EMPTY.getName(), KendoTournamentGenerator.getInstance().language))) {
                resetFault();
            } else if (sourceItem.getText().equals(trans.returnTag(Score.FAULT.getName(), KendoTournamentGenerator.getInstance().language))) {
                increaseFault();
            }
        }
    }

    private JPopupMenu createFaultMenu() {
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem menMenu = new JMenuItem();
        try {

            menMenu.setText(trans.returnTag(Score.FAULT.getName(), KendoTournamentGenerator.getInstance().language));
            menMenu.addActionListener(new FaultMenuListener());
            contextMenu.add(menMenu);

            menMenu = new JMenuItem();
            menMenu.setText(trans.returnTag(Score.EMPTY.getName(), KendoTournamentGenerator.getInstance().language));
            menMenu.addActionListener(new FaultMenuListener());
            contextMenu.add(menMenu);

        } catch (IndexOutOfBoundsException iofb) {
        }

        return contextMenu;
    }
}
