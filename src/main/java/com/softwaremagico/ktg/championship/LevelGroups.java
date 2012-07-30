/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.championship;

import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.Tournament;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jorge
 */
public class LevelGroups {

    private int level;
    private Tournament tournament;
    private List<TournamentGroup> tournamentGroups;
    private LevelGroups nextLevel;
    private LevelGroups previousLevel;

    LevelGroups(Tournament tournament, int level, LevelGroups nextLevel, LevelGroups previousLevel) {
        this.tournament = tournament;
        tournamentGroups = new ArrayList<>();
        this.level = level;
        this.nextLevel = nextLevel;
        this.previousLevel = previousLevel;
    }

    protected void updateGroups() {
        for (TournamentGroup t : tournamentGroups) {
            t.update();
        }
    }

    protected void activateGroupsColor(boolean color) {
        for (TournamentGroup t : tournamentGroups) {
            t.activateColor(color);
        }
    }

    protected void enhanceGroups(boolean yes) {
        for (TournamentGroup t : tournamentGroups) {
            t.enhance(yes);
        }
    }

    protected void onlyShow() {
        for (TournamentGroup t : tournamentGroups) {
            t.onlyShow();
        }
    }

    protected int size() {
        return tournamentGroups.size();
    }

    public List<TournamentGroup> getGroups() {
        return tournamentGroups;
    }

    /**
     * Return the las group of the level.
     */
    protected TournamentGroup getLastGroupOfLevel() {
        if (tournamentGroups.size() > 0) {
            return tournamentGroups.get(tournamentGroups.size() - 1);
        } else {
            return null;
        }
    }

    protected void deleteTeams() {
        for (TournamentGroup g : tournamentGroups) {
            g.deleteTeams();
        }
    }

    protected boolean isFightOfLevel(Fight fight) {
        for (TournamentGroup t : tournamentGroups) {
            if (t.isFightOfGroup(fight)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isGroupOfLevel(TournamentGroup tgroup) {
        for (TournamentGroup t : tournamentGroups) {
            if (tgroup.equals(t)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isLevelFinished(ArrayList<Fight> fights) {
        for (TournamentGroup t : tournamentGroups) {
            if (!t.areFightsOverOrNull(fights)) {
                return false;
            }
        }
        return true;
    }

    protected void addGroup(TournamentGroup group, boolean selected) {
        tournamentGroups.add(group);
    }

    /**
     * Update the number of groups according of the size of the previous level.
     */
    protected void updateGroupsSize() {
        while ((previousLevel != null) && (previousLevel.size() > 1) && ((float) previousLevel.returnNumberOfTotalTeamsPassNextRound() / 2 > this.size())) {
            addGroup(new TournamentGroup(2, 1, tournament, level, 0), false);
        }
        updateArenaOfGroups();
        nextLevel.updateGroups();
    }

    protected int returnNumberOfTotalTeamsPassNextRound() {
        int teams = 0;
        for (int i = 0; i < tournamentGroups.size(); i++) {
            teams += tournamentGroups.get(i).returnMaxNumberOfWinners();
        }
        return teams;
    }

    private void updateArenaOfGroups() {
        if (tournamentGroups.size() > 0) {
            /**
             * The arena of a group in a level > 1 is the same arena of the
             * group in the same row.
             */
            if (previousLevel != null) {
                for (int j = 0; j < tournamentGroups.size(); j++) {
                    //tournamentGroups.get(j).arena = grpsLevelZero.get(j * grpsLevelZero.size() / grps.size()).arena;
                    tournamentGroups.get(j).arena = previousLevel.getGroups().get(j * 2).arena;
                }
            } else {
                /**
                 * In level zero, all groups are divided by arenas.
                 */
                double groupsPerArena = Math.ceil((double) tournamentGroups.size() / (double) tournament.fightingAreas);
                for (int j = 0; j < tournamentGroups.size(); j++) {
                    tournamentGroups.get(j).arena = (j) / (int) groupsPerArena;
                }
            }
        }
    }
}
