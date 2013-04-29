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
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.Score;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.FightPool;
import com.softwaremagico.ktg.gui.PanelBackground;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class RoundFight extends JPanel {

    private List<TeamFight> teamFights = new ArrayList<>();
    private Fight fight;
    private int height = 65;
    Translator trans = null;
    DrawPanel DW = null;
    private Tournament tournament;

    RoundFight(Tournament tournament, Fight f, boolean selected, int fight_number) {
        this.tournament = tournament;
        setLanguage();
        decoration(selected);
        if (f != null) {
            fillCurrentFightPanel(f, selected, selected, fight_number);
        }
    }

    RoundFight(Tournament tournament, Fight f, boolean selected, boolean menu, int fight_number) {
        this.tournament = tournament;
        setLanguage();
        decoration(selected);
        if (f != null) {
            fillCurrentFightPanel(f, selected, menu, fight_number);
        }
    }

    RoundFight(int teamSize, boolean selected, int fight_number, int fight_total) {
        setLanguage();
        decoration(selected);
        fillCurrentFightPanel(teamSize, fight_number, fight_total);
    }

    /**
     * Translate the GUI.
     */
    public final void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
    }

    private void decoration(boolean selected) {
        setLayout(new BoxLayout(this, javax.swing.BoxLayout.X_AXIS));
        if (selected) {
            Border blackline = BorderFactory.createLineBorder(Color.black, 5);
            setBorder(blackline);
        } else {
            Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
            setBorder(loweredetched);
        }

    }

    private void fillCurrentFightPanel(Fight f, boolean selected, boolean menu, int fight_number) {
        fight = f;
        removeAll();
        teamFights = new ArrayList<>();
        TeamFight tf;
        int fight_total = FightPool.getInstance().get(tournament, fight.getAsignedFightArea()).size();
        if (!KendoTournamentGenerator.getInstance().isInverseTeams()) {
            tf = new TeamFight(tournament, this, f.getTeam1(), f, true, selected, menu, fight_number, fight_total);
        } else {
            tf = new TeamFight(tournament, this, f.getTeam2(), f, true, selected, menu, fight_number, fight_total);
        }
        add(tf, BorderLayout.WEST);
        teamFights.add(tf);

        DW = createDrawPanel(selected && menu);
        add(DW, BorderLayout.EAST);
        if (!KendoTournamentGenerator.getInstance().isInverseTeams()) {
            tf = new TeamFight(tournament, this, f.getTeam2(), f, false, selected, menu, fight_number, fight_total);
        } else {
            tf = new TeamFight(tournament, this, f.getTeam1(), f, false, selected, menu, fight_number, fight_total);
        }
        add(tf, BorderLayout.EAST);
        teamFights.add(tf);

        repaint();
        revalidate();
    }

    private void fillCurrentFightPanel(int teamSize, int fight_number, int fight_total) {
        removeAll();
        teamFights = new ArrayList<>();
        TeamFight tf = new TeamFight(tournament, true, teamSize, fight_number, fight_total);
        add(tf, BorderLayout.WEST);
        teamFights.add(tf);

        Dimension minSize = new Dimension(10, 2);
        Dimension prefSize = new Dimension(10, 5);
        Dimension maxSize = new Dimension(20, 5);
        add(new Box.Filler(minSize, prefSize, maxSize));

        tf = new TeamFight(tournament, false, teamSize, fight_number, fight_total);
        add(tf, BorderLayout.EAST);
        teamFights.add(tf);

        repaint();
        revalidate();
    }

    protected void updateScorePanels() {
        for (int i = 0; i < teamFights.size(); i++) {
            teamFights.get(i).updateScorePanel();
        }
    }

    private DrawPanel createDrawPanel(boolean selected) {
        DrawPanel drawPanel = new DrawPanel(selected);
        return drawPanel;
    }

    private class DrawPanel extends JPanel {

        private List<PanelBackground> draws;
        private List<Boolean> drawsAnnoted;

        DrawPanel(boolean selected) {
            setMinimumSize(new Dimension(15, 20));
            setPreferredSize(new Dimension(20, 60));
            setMaximumSize(new Dimension(50, Short.MAX_VALUE));
            setLayout(new BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

            createDrawsPanel();
            if (selected) {
                addPopUpMenu();
            }
            updateDrawsPanel();
        }

        private void createDrawsPanel() {
            draws = new ArrayList<>();
            drawsAnnoted = new ArrayList<>();

            Dimension minSize = new Dimension(0, 5);
            Dimension prefSize = new Dimension(5, 12);
            Dimension maxSize = new Dimension(5, Short.MAX_VALUE);

            add(new Box.Filler(minSize, prefSize, maxSize));
            //add(Box.createVerticalGlue());

            for (int i = 0; i < fight.getTeam1().getNumberOfMembers(fight.getLevel()); i++) {
                JPanel competitorPanel = new JPanel();
                competitorPanel.setLayout(new BoxLayout(competitorPanel, javax.swing.BoxLayout.X_AXIS));
                competitorPanel.setMinimumSize(new Dimension(50, 50));
                competitorPanel.setPreferredSize(new Dimension(500, 60));
                competitorPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 70));


                PanelBackground round = new PanelBackground();
                round.setMinimumSize(new Dimension(15, height));
                round.setPreferredSize(new Dimension(20, height));
                round.setMaximumSize(new Dimension(60, 70));
                competitorPanel.add(round, BorderLayout.EAST);
                draws.add(round);

                add(competitorPanel);

                minSize = new Dimension(5, 0);
                prefSize = new Dimension(5, 5);
                maxSize = new Dimension(5, Short.MAX_VALUE);
                add(new Box.Filler(minSize, prefSize, maxSize));
                drawsAnnoted.add(false);
            }

            minSize = new Dimension(5, 0);
            prefSize = new Dimension(5, 4);
            maxSize = new Dimension(5, Short.MAX_VALUE);

            add(new Box.Filler(minSize, prefSize, maxSize));
        }

        private void addPopUpMenu() {
            for (int i = 0; i < draws.size(); i++) {
                draws.get(i).setComponentPopupMenu(createContextMenu(i));
            }
        }

        private JPopupMenu createContextMenu(int fight) {
            JPopupMenu contextMenu = new JPopupMenu();
            JMenuItem drawMenu = new JMenuItem();
            try {
                drawMenu.setText(trans.getTranslatedText("DrawMenuItem"));
                drawMenu.addActionListener(new MenuListener(fight));
                contextMenu.add(drawMenu);

                drawMenu = new JMenuItem();
                drawMenu.setText(trans.getTranslatedText("ClearMenuItem"));
                drawMenu.addActionListener(new MenuListener(fight));
                contextMenu.add(drawMenu);
            } catch (IndexOutOfBoundsException iofb) {
            }

            return contextMenu;
        }

        private class MenuListener implements ActionListener {

            private int fightIndex;

            MenuListener(int tmp_round) {
                fightIndex = tmp_round;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                JMenuItem sourceItem = (JMenuItem) e.getSource();
                if (sourceItem.getText().equals(trans.getTranslatedText("DrawMenuItem"))) {
                    updateFight('X');
                } else if (sourceItem.getText().equals(trans.getTranslatedText("ClearMenuItem"))) {
                    updateFight(' ');
                }
            }

            private void updateFight(char point) {
                if (point == 'X' && fight.getDuels().get(fightIndex).winner() == 0) {
                    drawsAnnoted.set(fightIndex, true);
                } else {
                    drawsAnnoted.set(fightIndex, false);
                }
                updateDrawsPanel();
            }
        }

        private void updateDrawsPanel() {
            for (int i = 0; i < draws.size(); i++) {
                boolean nextStarted = false;
                if (i < fight.getDuels().size() - 1) {
                    nextStarted = fight.getDuels().get(i + 1).isStarted();
                }
                updateDrawPanel(draws.get(i), fight.getDuels().get(i).winner(), fight.getWinner(), nextStarted, i);
            }
        }

        private void updateDrawPanel(PanelBackground panel, int winner, int over, boolean nextStarted, int index) {
            if ((winner == 0 && over != 2) || //fight over and the duel is draw.
                    (winner == 0 && nextStarted) || //fight not over, but next duel started
                    (drawsAnnoted.get(index) && winner == 0)) { //Set by the user.
                panel.setBackgroundExtended(Score.getImage(Score.DRAW.getImageName()));
            } else {
                drawsAnnoted.set(index, false);
                panel.removeBackground();
            }
            panel.revalidate();
            panel.repaint();
        }

        @Override
        public void paint(Graphics g) {
            updateDrawsPanel();
        }
    }
}
