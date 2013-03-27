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
import com.softwaremagico.ktg.database.FightPool;
import java.awt.Dimension;
import javax.swing.Box;

public class ScorePanel extends javax.swing.JPanel {

    private Tournament tournament = null;

    public ScorePanel(Tournament selectedTournament) {
        this.tournament = selectedTournament;
        setMinimumSize(new java.awt.Dimension(0, 200));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
    }

    public void updateTournament(Tournament selectedTournament) {
        this.tournament = selectedTournament;

    }

    public void fillFightsPanel(int fightArea) {
        //roundFights = new ArrayList<RoundFight>();
        removeAll();
        if (FightPool.getInstance().get(tournament).size() > 0) {
            int showedFights = 0;
            Dimension minSize = new Dimension(0, 5);
            Dimension prefSize = new Dimension(5, 5);
            Dimension maxSize = new Dimension(5, 5);

            //Penultimus
            if (numberOfFightsToShow() > 4) {
                addFightPanel(-2, fightArea);
                showedFights++;
            }
            //Previous
            if (numberOfFightsToShow() > 2) {
                addFightPanel(-1, fightArea);
                showedFights++;
            }
            //Current
            addFightPanel(0, fightArea);
            showedFights++;

            //Nexts
            if (numberOfFightsToShow() > 1) {
                for (int i = FightPool.getInstance().getCurrentFightIndex(tournament, fightArea) + 1;
                        showedFights < numberOfFightsToShow() && i < FightPool.getInstance().get(tournament, fightArea).size(); i++) {
                    addFightPanel(i, fightArea);
                    showedFights++;
                }
            }

            //Add null fightManager to complete the panel.
            while (showedFights < numberOfFightsToShow()) {
                try {
                    RoundFight rf = new RoundFight(tournament.getTeamSize(), false, 0, 0);
                    add(rf);
                    add(new Box.Filler(minSize, prefSize, maxSize));
                } catch (NullPointerException npe) {
                }
                showedFights++;
            }
        }
        repaint();
        revalidate();
    }

    private void addFightPanel(Integer fightRelativeToCurrent, Integer fightArea) {
        Dimension minSize = new Dimension(0, 5);
        Dimension prefSize = new Dimension(5, 5);
        Dimension maxSize = new Dimension(5, 5);

        RoundFight rf;
        Fight f = FightPool.getInstance().get(tournament, fightArea, fightRelativeToCurrent);
        if (f != null) {
            rf = new RoundFight(tournament, f, false, FightPool.getInstance().getCurrentFightIndex(tournament, fightArea) + fightRelativeToCurrent);
        } else {
            rf = new RoundFight(tournament.getTeamSize(), false, 0, 0);
        }
        rf.updateScorePanels();
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
