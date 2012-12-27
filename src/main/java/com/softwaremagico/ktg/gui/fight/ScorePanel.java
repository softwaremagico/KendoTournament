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

import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.FightPool;
import com.softwaremagico.ktg.Tournament;
import java.awt.Dimension;
import javax.swing.Box;

/**
 *
 * @author jorge
 */
public class ScorePanel extends javax.swing.JPanel {

    private Tournament tournament = null;

    /**
     * Creates new form ScorePanel
     */
    public ScorePanel(Tournament selectedTournament) {
        this.tournament = selectedTournament;
        initComponents();
    }

    public void updateTournament(Tournament selectedTournament) {
        this.tournament = selectedTournament;

    }

    public void fillFightsPanel(int fightArena) {
        //roundFights = new ArrayList<RoundFight>();
        removeAll();
        if (FightPool.getManager(tournament).size() > 0) {
            RoundFight rf;
            Fight f;
            int showedFights = 0;
            Dimension minSize = new Dimension(0, 5);
            Dimension prefSize = new Dimension(5, 5);
            Dimension maxSize = new Dimension(5, 5);

            //Penultimus
            if (numberOfFightsToShow() > 4) {
                f = FightPool.getManager(tournament).getPreviousOfPreviousAreaFight((Integer) fightArena);
                if (f != null) {
                    rf = new RoundFight(tournament, f, false, FightPool.getManager(tournament).currentArenaFight((Integer) fightArena) - 2, FightPool.getManager(tournament).arenaSize((Integer) fightArena));
                } else {
                    rf = new RoundFight(tournament.getTeamSize(), false, 0, 0);
                }
                showedFights++;
                rf.updateScorePanels();
                add(rf);
                add(new Box.Filler(minSize, prefSize, maxSize));
            }
            //Previous
            if (numberOfFightsToShow() > 2) {
                f = FightPool.getManager(tournament).getPreviousAreaFight(fightArena);
                if (f != null) {
                    rf = new RoundFight(tournament, f, false, FightPool.getManager(tournament).currentArenaFight((Integer) fightArena) - 1, FightPool.getManager(tournament).arenaSize((Integer) fightArena));
                } else {
                    rf = new RoundFight(tournament.getTeamSize(), false, 0, 0);
                }
                showedFights++;
                rf.updateScorePanels();
                add(rf);
                add(new Box.Filler(minSize, prefSize, maxSize));
            }
            //Current
            if (FightPool.getManager(tournament).size() > 0) {
                rf = new RoundFight(tournament, FightPool.getManager(tournament).getSelectedFight(fightArena), true, FightPool.getManager(tournament).currentArenaFight((Integer) fightArena), FightPool.getManager(tournament).arenaSize((Integer) fightArena));
                rf.updateScorePanels();
                add(rf);
            }
            showedFights++;
            add(new Box.Filler(minSize, prefSize, maxSize));

            //Nexts
            if (numberOfFightsToShow() > 1) {
                for (int i = FightPool.getManager(tournament).currentArenaFight(fightArena);
                        showedFights < numberOfFightsToShow() && i < FightPool.getManager(tournament).arenaSize(fightArena) - 1; i++) {
                    f = FightPool.getManager(tournament).getNextAreaFight(i, fightArena);
                    if (f != null) {
                        rf = new RoundFight(tournament, f, false, FightPool.getManager(tournament).currentArenaFight((Integer) fightArena) + 1, FightPool.getManager(tournament).arenaSize((Integer) fightArena));
                    } else {
                        rf = new RoundFight(tournament.getTeamSize(), false, 0, 0);
                    }
                    showedFights++;
                    rf.updateScorePanels();
                    add(rf);
                    add(new Box.Filler(minSize, prefSize, maxSize));
                }
            }

            //Add null fightManager to complete the panel.
            while (showedFights < numberOfFightsToShow()) {
                try {
                    rf = new RoundFight(tournament.getTeamSize(), false, 0, 0);
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMinimumSize(new java.awt.Dimension(0, 200));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
