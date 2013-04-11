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

import com.softwaremagico.ktg.core.Duel;
import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Score;
import com.softwaremagico.ktg.gui.PanelBackground;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author Jorge
 */
public class CompetitorFight extends JPanel {

    private PanelBackground round1;
    private PanelBackground round2;
    private PanelBackground faults;
    private JLabel nameLabel;
    private RegisteredPerson competitor;
    private Fight fight;
    private Translator trans = null;
    private TeamFight teamFight;

    CompetitorFight(TeamFight tf, RegisteredPerson c, Fight f, boolean left, boolean selected, boolean menu) {
        competitor = c;
        fight = f;
        teamFight = tf;
        setLanguage();
        decoration();
        try {
            if (left) {
                try {
                    fillLeftToRight(c.getSurnameNameIni());
                } catch (NullPointerException npe) {
                    fillLeftToRight(" ");
                }
            } else {
                try {
                    fillRightToLeft(c.getSurnameNameIni());
                } catch (NullPointerException npe) {
                    fillRightToLeft(" ");
                }
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        if (menu) {
            addAllMenus();
        }
    }

    CompetitorFight(boolean left) {
        competitor = null;
        fight = null;
        setLanguage();
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
    public final void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
    }

    private void fillRightToLeft(String name) {
        add(Box.createRigidArea(new Dimension(6, 0)));

        round1 = new PanelBackground();
        round1.setBackground(new Color(255, 255, 255));
        round1.setMinimumSize(new Dimension(40, 40));
        round1.setPreferredSize(new Dimension(50, 50));
        round1.setMaximumSize(new Dimension(60, 60));
        add(round1, BorderLayout.EAST);
        add(Box.createRigidArea(new Dimension(6, 0)));

        round2 = new PanelBackground();
        round2.setBackground(new Color(255, 255, 255));
        round2.setMinimumSize(new Dimension(40, 40));
        round2.setPreferredSize(new Dimension(50, 50));
        round2.setMaximumSize(new Dimension(60, 60));
        add(round2, BorderLayout.CENTER);
        add(Box.createRigidArea(new Dimension(10, 0)));

        faults = new PanelBackground();
        faults.setBackground(new Color(255, 255, 255));
        faults.setMinimumSize(new Dimension(10, 40));
        faults.setPreferredSize(new Dimension(15, 50));
        faults.setMaximumSize(new Dimension(20, 60));
        add(faults, BorderLayout.EAST);
        add(Box.createRigidArea(new Dimension(6, 0)));

        add(Box.createHorizontalGlue());

        nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        add(Box.createRigidArea(new Dimension(6, 0)));
        add(nameLabel, BorderLayout.EAST);

        add(Box.createRigidArea(new Dimension(6, 0)));
    }

    private void fillLeftToRight(String name) {
        nameLabel = new JLabel(name);
        Font f = nameLabel.getFont();
        //nameLabel.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        add(nameLabel, BorderLayout.EAST);
        add(Box.createRigidArea(new Dimension(6, 0)));

        add(Box.createHorizontalGlue());

        faults = new PanelBackground();
        faults.setBackground(new Color(255, 255, 255));
        faults.setMinimumSize(new Dimension(10, 40));
        faults.setPreferredSize(new Dimension(15, 50));
        faults.setMaximumSize(new Dimension(20, 60));
        add(faults, BorderLayout.EAST);
        add(Box.createRigidArea(new Dimension(10, 0)));

        round2 = new PanelBackground();
        round2.setBackground(new Color(255, 255, 255));
        round2.setMinimumSize(new Dimension(40, 40));
        round2.setPreferredSize(new Dimension(50, 50));
        round2.setMaximumSize(new Dimension(50, 60));
        add(round2, BorderLayout.CENTER);
        add(Box.createRigidArea(new Dimension(6, 0)));

        round1 = new PanelBackground();
        round1.setBackground(new Color(255, 255, 255));
        round1.setMinimumSize(new Dimension(40, 40));
        round1.setPreferredSize(new Dimension(50, 50));
        round1.setMaximumSize(new Dimension(50, 60));
        add(round1, BorderLayout.EAST);
        add(Box.createRigidArea(new Dimension(6, 0)));

    }

    private void decoration() {
        setLayout(new BoxLayout(this, javax.swing.BoxLayout.X_AXIS));
        setMinimumSize(new Dimension(50, 50));
        setPreferredSize(new Dimension(500, 60));
        setMaximumSize(new Dimension(Short.MAX_VALUE, 70));
    }

    private void updateScorePanel(PanelBackground panel, Score hit) {
        panel.setBackground(Score.getImage(hit.getImageName()));
        panel.revalidate();
        panel.repaint();
    }

    private void updateFaultPanel(PanelBackground panel, boolean fault) {
        if (fault) {
            panel.setBackground(Score.getImage(Score.FAULT.getImageName()));
        } else {
            panel.setBackground(Score.getImage(Score.EMPTY.getImageName()));
        }
        panel.revalidate();
        panel.repaint();
    }

    void updateScorePanels() {
        Integer player;
        try {
            if (fight != null) {
                if ((player = fight.getTeam1().getMemberOrder(fight.getLevel(), competitor)) != null) {
                    Duel d = fight.getDuels().get(player);
                    updateScorePanel(round1, d.getHits(true).get(0));
                    updateScorePanel(round2, d.getHits(true).get(1));
                    updateFaultPanel(faults, d.getFaults(true));
                }
                if ((player = fight.getTeam2().getMemberOrder(fight.getLevel(), competitor)) != null) {
                    Duel d = fight.getDuels().get(player);
                    updateScorePanel(round1, d.getHits(false).get(0));
                    updateScorePanel(round2, d.getHits(false).get(1));
                    updateFaultPanel(faults, d.getFaults(false));
                }
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
    }

    private void addAllMenus() {
        if (fight.getTeam1().getNumberOfMembers(fight.getLevel()) > 0
                || fight.getTeam2().getNumberOfMembers(fight.getLevel()) > 0) {
            addPopUpMenu();
        }
    }

    private void addPopUpMenu() {
        round1.setComponentPopupMenu(createContextMenu(0));
        round2.setComponentPopupMenu(createContextMenu(1));
        faults.setComponentPopupMenu(createFaultMenu());
    }

    private JPopupMenu createContextMenu(int round) {
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem menMenu;
        try {
            for (Score s : Score.getValidPoints()) {
                menMenu = new JMenuItem();
                menMenu.setText(s.getName());
                menMenu.addActionListener(new MenuListener(round));
                contextMenu.add(menMenu);
            }

            menMenu = new JMenuItem();
            menMenu.setText(trans.getTranslatedText(Score.EMPTY.getName()));
            menMenu.addActionListener(new MenuListener(round));
            contextMenu.add(menMenu);
        } catch (IndexOutOfBoundsException iofb) {
        }

        return contextMenu;
    }

    private class MenuListener implements ActionListener {

        private int round;

        MenuListener(int tmp_round) {
            round = tmp_round;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem sourceItem = (JMenuItem) e.getSource();

            if (sourceItem.getText().equals(trans.getTranslatedText(Score.EMPTY.getName()))) {
                updateDuel(Score.EMPTY, round);
            } else {
                updateDuel(Score.getScore(sourceItem.getText()), round);
            }
        }
    }

    private void updateDuel(Score point, int round) {
        Integer index;
        Duel d;
        try {
            if ((index = fight.getTeam1().getMemberOrder(fight.getLevel(), competitor)) != null) {
                d = fight.getDuels().get(index);
                fight.setOverStored(false);
                if (!point.equals(Score.EMPTY)) {
                    d.setResultInRound(round, point, true);
                } else {
                    d.clearResultInRound(round, true);
                }
            }
            if ((index = fight.getTeam2().getMemberOrder(fight.getLevel(), competitor)) != null) {
                d = fight.getDuels().get(index);
                if (!point.equals(Score.EMPTY)) {
                    d.setResultInRound(round, point, false);
                } else {
                    d.clearResultInRound(round, false);
                }
            }
        } catch (NullPointerException npe) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), npe);
        }
        teamFight.roundFight.updateScorePanels();
    }

    private void increaseFault() {
        Integer index;
        Duel d;
        try {
            if ((index = fight.getTeam1().getMemberOrder(fight.getLevel(), competitor)) != null) {
                d = fight.getDuels().get(index);
                fight.setOverStored(false);
                d.setFaults(true);
                // KendoTournamentGenerator.getInstance().fightManager.storeDuel(d, fight, index);
                updateScorePanels();
            }
            if ((index = fight.getTeam2().getMemberOrder(fight.getLevel(), competitor)) != null) {
                d = fight.getDuels().get(index);
                d.setFaults(false);
                //KendoTournamentGenerator.getInstance().fightManager.storeDuel(d, fight, index);
                updateScorePanels();
            }
        } catch (NullPointerException npe) {
        }
        teamFight.roundFight.updateScorePanels();
    }

    private void resetFault() {
        Integer index;
        Duel d;
        try {
            if ((index = fight.getTeam1().getMemberOrder(fight.getLevel(), competitor)) != null) {
                d = fight.getDuels().get(index);
                d.resetFaults(true);
                fight.setOverStored(false);
                //KendoTournamentGenerator.getInstance().fightManager.storeDuel(d, fight, index);
                updateScorePanels();
            }
            if ((index = fight.getTeam2().getMemberOrder(fight.getLevel(), competitor)) != null) {
                d = fight.getDuels().get(index);
                d.resetFaults(false);
                //KendoTournamentGenerator.getInstance().fightManager.storeDuel(d, fight, index);
                updateScorePanels();
            }
        } catch (NullPointerException npe) {
        }
    }

    private class FaultMenuListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            JMenuItem sourceItem = (JMenuItem) e.getSource();
            if (sourceItem.getText().equals(trans.getTranslatedText(Score.EMPTY.getName()))) {
                resetFault();
            } else if (sourceItem.getText().equals(trans.getTranslatedText(Score.FAULT.getName()))) {
                increaseFault();
            }
        }
    }

    private JPopupMenu createFaultMenu() {
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem menMenu = new JMenuItem();
        try {

            menMenu.setText(trans.getTranslatedText(Score.FAULT.getName()));
            menMenu.addActionListener(new FaultMenuListener());
            contextMenu.add(menMenu);

            menMenu = new JMenuItem();
            menMenu.setText(trans.getTranslatedText(Score.EMPTY.getName()));
            menMenu.addActionListener(new FaultMenuListener());
            contextMenu.add(menMenu);

        } catch (IndexOutOfBoundsException iofb) {
        }

        return contextMenu;
    }
}
