/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.championship;

import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.Team;
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
    private TournamentGroupManager groupManager;

    LevelGroups(Tournament tournament, int level, LevelGroups nextLevel, LevelGroups previousLevel, TournamentGroupManager groupManager) {
        this.tournament = tournament;
        tournamentGroups = new ArrayList<>();
        this.level = level;
        this.nextLevel = nextLevel;
        this.previousLevel = previousLevel;
        this.groupManager = groupManager;
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
        if ((nextLevel == null) && (returnNumberOfTotalTeamsPassNextRound() > 1)) {
            nextLevel = new LevelGroups(tournament, level + 1, null, this, groupManager);
            groupManager.getLevels().add(nextLevel);
            nextLevel.updateGroupsSize();
        } else {
            nextLevel.updateGroupsSize();
        }
    }

    protected void removeGroup() {
        if (tournamentGroups.size() > 0) {
            tournamentGroups.remove(tournamentGroups.size() - 1);
            if (nextLevel != null) {
                nextLevel.updateGroupsSize();
                if (nextLevel.size() == 0) {
                    nextLevel = null;
                }
            }
        }
    }

    /**
     * Update the number of groups according of the size of the previous level.
     */
    protected void updateGroupsSize() {
        while ((previousLevel != null) && ((float) previousLevel.returnNumberOfTotalTeamsPassNextRound() / 2 > this.size())) {
            addGroup(new TournamentGroup(2, 1, tournament, level, 0), false);
        }

        //When we remove two groups in one level, we must remove one in the next one.
        while ((previousLevel != null) && (Math.ceil((float) previousLevel.returnNumberOfTotalTeamsPassNextRound() / 2) < this.size())) {
            removeGroup();
        }

        updateArenaOfGroups();

        //If there are no groups left, delete this level and the next one..
        if (size() == 0) {
            for (int i = groupManager.getLevels().size() - 1; i >= level; i--) {
                groupManager.getLevels().remove(i);
            }
        }

        if (nextLevel != null) {
            nextLevel.updateGroupsSize();
        }

    }

    protected int returnNumberOfTotalTeamsPassNextRound() {
        int teams = 0;
        for (int i = 0; i < tournamentGroups.size(); i++) {
            teams += tournamentGroups.get(i).returnMaxNumberOfWinners();
        }
        return teams;
    }

    protected void updateArenaOfGroups() {
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
        nextLevel.updateArenaOfGroups();
    }

    protected boolean loadLevel(Tournament tournament) {
        for (TournamentGroup group : tournamentGroups) {
            if (!group.load(tournament)) {
                return false;
            }
        }
        return true;
    }

    protected void updateScoreOfTeams(ArrayList<Fight> fights) {
        for (TournamentGroup group : tournamentGroups) {
            group.updateScoreForTeams(fights);
        }
    }

    protected TournamentGroup getGroupOfFight(Fight fight) {
        for (int i = 0; i < tournamentGroups.size(); i++) {
            boolean team1Exist = false;
            boolean team2Exist = false;

            for (int j = 0; j < tournamentGroups.get(i).teams.size(); j++) {
                if (fight.team1.returnName().equals(tournamentGroups.get(i).teams.get(j).returnName())) {
                    team1Exist = true;
                }
                if (fight.team2.returnName().equals(tournamentGroups.get(i).teams.get(j).returnName())) {
                    team2Exist = true;
                }
            }
            if (team1Exist && team2Exist) {
                return tournamentGroups.get(i);
            }
        }
        return null;
    }

    protected List<Team> getUsedTeams() {
        List<Team> usedTeams = new ArrayList<>();
        for (TournamentGroup group : tournamentGroups) {
            usedTeams.addAll(group.teams);
        }
        return usedTeams;
    }

    protected TournamentGroup getLastSelected() {
        for (int i = 0; i < tournamentGroups.size(); i++) {
            if (tournamentGroups.get(i).isSelected()) {
                return tournamentGroups.get(i);
            }
        }
        return null;
    }
}
