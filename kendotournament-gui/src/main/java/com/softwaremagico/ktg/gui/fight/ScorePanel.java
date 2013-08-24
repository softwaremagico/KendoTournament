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
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.gui.AlertManager;
import com.softwaremagico.ktg.gui.base.KPanel;
import com.softwaremagico.ktg.persistence.FightPool;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;

public class ScorePanel extends KPanel {

    private Tournament tournament = null;
    private Integer fightArea = null;
    private List<RoundFight> roundFights;

    public ScorePanel() {
        setMinimumSize(new java.awt.Dimension(400, 400));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
    }

    public void updateTournament(Tournament selectedTournament, Integer fightArea, boolean invertedTeam, boolean invertedColor) {
        this.tournament = selectedTournament;
        this.fightArea = fightArea;
        fillFightsPanel(invertedTeam, invertedColor);
    }

    private void fillFightsPanel(boolean invertedTeam, boolean invertedColor) {
        removeAll();
        roundFights = new ArrayList<>();
        try {
            if (tournament != null && FightPool.getInstance().get(tournament).size() > 0) {
                int showedFights = 0;
                Dimension minSize = new Dimension(0, 5);
                Dimension prefSize = new Dimension(5, 5);
                Dimension maxSize = new Dimension(5, 5);

                //Penultimus
                if (numberOfFightsToShow() > 4) {
                    addFightPanel(-2, fightArea, invertedTeam, invertedColor, false);
                    showedFights++;
                }
                //Previous
                if (numberOfFightsToShow() > 2) {
                    addFightPanel(-1, fightArea, invertedTeam, invertedColor, false);
                    showedFights++;
                }
                //Current
                addFightPanel(0, fightArea, invertedTeam, invertedColor, true);
                showedFights++;

                //Nexts
                if (numberOfFightsToShow() > 1) {
                    try {
                        int currentFightIndex = FightPool.getInstance().getCurrentFightIndex(tournament, fightArea);
                        for (int i = currentFightIndex + 1;
                                showedFights < numberOfFightsToShow() && i < FightPool.getInstance().get(tournament, fightArea).size(); i++) {
                            addFightPanel(i - currentFightIndex, fightArea, invertedTeam, invertedColor, false);
                            showedFights++;
                        }
                        //No more fights.
                    } catch (NullPointerException npe) {
                    }
                }

                //Add null fightManager to complete the panel.
                while (showedFights < numberOfFightsToShow()) {
                    try {
                        RoundFight rf = new RoundFight(tournament.getTeamSize(), false, 0, 0, invertedColor);
                        add(rf);
                        add(new Box.Filler(minSize, prefSize, maxSize));
                    } catch (NullPointerException npe) {
                    }
                    showedFights++;
                }
            }
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
        repaint();
        revalidate();
        updateNewPanelWidht();
    }

    private void updateNewPanelWidht() {
        for (RoundFight rf : roundFights) {
            rf.updateCompetitorsName(this.getWidth());
        }
    }

    private RoundFight createFightPanel(int fightRelativeToCurrent, Integer fightArea, boolean invertedTeam, boolean invertedColor, boolean selected) {
        RoundFight rf = null;
        Fight f = null;
        try {
            try {
                f = FightPool.getInstance().get(tournament, fightArea, FightPool.getInstance().getCurrentFightIndex(tournament, fightArea) + fightRelativeToCurrent);
            } catch (NullPointerException npe) {
            }
            if (f != null) {
                rf = new RoundFight(tournament, f, selected, FightPool.getInstance().getCurrentFightIndex(tournament, fightArea) + fightRelativeToCurrent, invertedTeam, invertedColor);
            } else {
                rf = new RoundFight(tournament.getTeamSize(), selected, 0, 0, invertedColor);
            }
            rf.updateScorePanels();
        } catch (SQLException ex) {
            AlertManager.showSqlErrorMessage(ex);
        }
        return rf;
    }

    private void addFightPanel(Integer fightRelativeToCurrent, Integer fightArea, boolean invertedTeam, boolean invertedColor, boolean selected) {
        Dimension minSize = new Dimension(0, 5);
        Dimension prefSize = new Dimension(5, 5);
        Dimension maxSize = new Dimension(5, 5);

        RoundFight rf = createFightPanel(fightRelativeToCurrent, fightArea, invertedTeam, invertedColor, selected);
        roundFights.add(rf);
        add(rf);
        add(new Box.Filler(minSize, prefSize, maxSize));
    }

    private int numberOfFightsToShow() {
        return (int) getHeight() / screenSizeOfTeam();
    }

    private int screenSizeOfTeam() {
        try {
            return 60 * tournament.getTeamSize() + 45;
        } catch (NullPointerException npe) {
            return 225;
        }
    }
}
