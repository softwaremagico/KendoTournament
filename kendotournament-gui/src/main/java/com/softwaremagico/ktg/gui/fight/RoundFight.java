package com.softwaremagico.ktg.gui.fight;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero. Jorge Hortelano Otero
 * <softwaremagico@gmail.com> Valencia (Spain).
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
import com.softwaremagico.ktg.core.Score;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.persistence.FightPool;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.gui.PanelBackground;
import com.softwaremagico.ktg.gui.base.KPanel;
import com.softwaremagico.ktg.language.LanguagePool;
import com.softwaremagico.ktg.language.Translator;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class RoundFight extends JPanel {

    private static final long serialVersionUID = -4168299695100498820L;
    private List<TeamFight> teamFights = new ArrayList<>();
    private Fight fight;
    private int height = 65;
    private Translator trans = null;
    private DrawPanel drawsPanel = null;
    private Tournament tournament;
    private GridBagConstraints teamGridBagConstraints, drawGridBagConstraints;
    private Integer xPadding = 5;
    private int teamSize;

    RoundFight(Tournament tournament, Fight f, boolean selected, int fight_number, boolean invertedTeam, boolean invertedColor) {
        this.tournament = tournament;
        teamSize = tournament.getTeamSize();
        setLanguage();
        decoration(selected);
        if (f != null) {
            fillCurrentFightPanel(f, selected, selected, fight_number, invertedTeam, invertedColor);
        }
    }

    RoundFight(Tournament tournament, Fight f, boolean selected, boolean menu, int fight_number, boolean invertedTeam, boolean invertedColor) {
        this.tournament = tournament;
        teamSize = tournament.getTeamSize();
        setLanguage();
        decoration(selected);
        if (f != null) {
            fillCurrentFightPanel(f, selected, menu, fight_number, invertedTeam, invertedColor);
        }
    }

    RoundFight(int teamSize, boolean selected, int fight_number, int fight_total, boolean invertedColor) {
        this.teamSize = teamSize;
        setLanguage();
        decoration(selected);
        fillCurrentFightPanel(teamSize, fight_number, fight_total, invertedColor);
    }

    private void setTeamGridBagConstraints() {
        teamGridBagConstraints = new GridBagConstraints();
        teamGridBagConstraints.fill = GridBagConstraints.BOTH;
        teamGridBagConstraints.ipadx = xPadding;
        teamGridBagConstraints.ipady = xPadding;
        teamGridBagConstraints.gridy = 0;
        teamGridBagConstraints.gridheight = 1;
        teamGridBagConstraints.gridwidth = 1;
        teamGridBagConstraints.weightx = 0.5;
        teamGridBagConstraints.weighty = 1;
        //teamGridBagConstraints.insets = new Insets(5, 5, 5, 5);
    }

    private void setDrawGridBagConstraints() {
        drawGridBagConstraints = new GridBagConstraints();
        drawGridBagConstraints.fill = GridBagConstraints.BOTH;
        drawGridBagConstraints.ipadx = xPadding;
        teamGridBagConstraints.ipady = xPadding;
        drawGridBagConstraints.gridx = 1;
        drawGridBagConstraints.gridy = 0;
        drawGridBagConstraints.gridheight = 1;
        drawGridBagConstraints.gridwidth = 1;
        drawGridBagConstraints.weightx = 0;
        drawGridBagConstraints.weighty = 1;
        drawGridBagConstraints.insets = new Insets(10, 5, 10, 5);
    }

    /**
     * Translate the GUI.
     */
    public final void setLanguage() {
        trans = LanguagePool.getTranslator("gui.xml");
    }

    protected void updateCompetitorsName(int width) {
        for (TeamFight tf : teamFights) {
            tf.updateCompetitorsName(width / 2 - 50);
        }
    }

    private void decoration(boolean selected) {
        //setLayout(new BoxLayout(this, javax.swing.BoxLayout.X_AXIS));        
        setLayout(new GridBagLayout());
        setTeamGridBagConstraints();
        setDrawGridBagConstraints();

        if (selected) {
            Border blackline = BorderFactory.createLineBorder(Color.black, 5);
            setBorder(blackline);
        } else {
            Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
            setBorder(loweredetched);
        }

    }

    private void fillCurrentFightPanel(Fight f, boolean selected, boolean menu, int fight_number, boolean invertedTeam, boolean invertedColor) {
        fight = f;
        removeAll();
        teamFights = new ArrayList<>();
        TeamFight tf1, tf2;
        int fight_total = 0;
        try {
            fight_total = FightPool.getInstance().get(tournament, fight.getAsignedFightArea()).size();
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
        if (!invertedTeam) {
            tf1 = new TeamFight(tournament, this, f.getTeam1(), f, true, selected, menu, fight_number, fight_total, invertedColor);
        } else {
            tf1 = new TeamFight(tournament, this, f.getTeam2(), f, true, selected, menu, fight_number, fight_total, invertedColor);
        }

        if (!invertedTeam) {
            tf2 = new TeamFight(tournament, this, f.getTeam2(), f, false, selected, menu, fight_number, fight_total, invertedColor);
        } else {
            tf2 = new TeamFight(tournament, this, f.getTeam1(), f, false, selected, menu, fight_number, fight_total, invertedColor);
        }

        teamGridBagConstraints.gridx = 0;
        add(tf1, teamGridBagConstraints);
        teamFights.add(tf1);

        drawsPanel = createDrawPanel(selected && menu);
        add(drawsPanel, drawGridBagConstraints);

        teamGridBagConstraints.gridx = 2;
        add(tf2, teamGridBagConstraints);
        teamFights.add(tf2);

        repaint();
        revalidate();
    }

    private void fillCurrentFightPanel(int teamSize, int fight_number, int fight_total, boolean invertedColor) {
        removeAll();
        teamFights = new ArrayList<>();
        TeamFight tf = new TeamFight(tournament, true, teamSize, fight_number, fight_total, invertedColor);
        teamGridBagConstraints.gridx = 0;
        add(tf, teamGridBagConstraints);
        teamFights.add(tf);

        drawsPanel = createDrawPanel(false);
        add(drawsPanel, drawGridBagConstraints);

        tf = new TeamFight(tournament, false, teamSize, fight_number, fight_total, invertedColor);
        teamGridBagConstraints.gridx = 2;
        add(tf, teamGridBagConstraints);
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

    private class DrawPanel extends KPanel {

        private static final long serialVersionUID = -1487466822879962512L;
        private List<PanelBackground> draws;
        private List<Boolean> drawsAnnoted;
        private GridBagConstraints gridBagConstraints;

        public DrawPanel(boolean selected) {
            this.setLayout(new GridBagLayout());
            setGridBagConstraints();

            createDrawsPanel();
            if (selected) {
                addPopUpMenu();
            }
            updateDrawsPanel();
        }

        private void setGridBagConstraints() {
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.BOTH;
            gridBagConstraints.ipadx = 0;
            gridBagConstraints.ipady = xPadding*2;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridheight = 1;
            gridBagConstraints.gridwidth = 1;
            gridBagConstraints.weightx = 1;
            gridBagConstraints.weighty = 1;
            gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        }

        private void createDrawsPanel() {
            draws = new ArrayList<>();
            drawsAnnoted = new ArrayList<>();

            for (int i = 0; i < teamSize; i++) {
                PanelBackground round = new PanelBackground();
                round.setMinimumSize(new Dimension(15, height));
                round.setPreferredSize(new Dimension(20, height));
                round.setMaximumSize(new Dimension(60, 75));
                draws.add(round);
                gridBagConstraints.gridy = i;
                this.add(round, gridBagConstraints);

                drawsAnnoted.add(false);
            }
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
            if (fight != null) {
                for (int i = 0; i < draws.size(); i++) {
                    boolean nextStarted = false;
                    if (i < fight.getDuels().size() - 1) {
                        nextStarted = fight.getDuels().get(i + 1).isStarted();
                    }
                    updateDrawPanel(draws.get(i), fight.getDuels().get(i).winner(), fight.getWinner(), nextStarted, i);
                }
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
