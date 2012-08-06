/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwaremagico.ktg.championship;
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
import com.softwaremagico.ktg.Team;
import com.softwaremagico.ktg.Tournament;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jorge
 */
public abstract class LevelGroups {

    protected int level;
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

    protected int getNumberOfTotalTeamsPassNextRound() {
        int teams = 0;
        for (int i = 0; i < tournamentGroups.size(); i++) {
            teams += tournamentGroups.get(i).getMaxNumberOfWinners();
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
                    if (level == 1) {
                        tournamentGroups.get(j).arena = previousLevel.getGroups().get(j * 2 / groupManager.default_max_winners).arena;
                    } else {
                        tournamentGroups.get(j).arena = previousLevel.getGroups().get(j * 2).arena;
                    }
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
        if (nextLevel != null) {
            nextLevel.updateArenaOfGroups();
        }
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

    protected Integer getIndexLastSelected() {
        for (int i = 0; i < tournamentGroups.size(); i++) {
            if (tournamentGroups.get(i).isSelected()) {
                return new Integer(i);
            }
        }
        return null;
    }

    protected Integer getIndexOfGroup(TournamentGroup group) {
        for (int i = 0; i < tournamentGroups.size(); i++) {
            if (tournamentGroups.get(i).equals(group)) {
                return new Integer(i);
            }
        }
        return null;
    }

    protected int getGlobalPositionWinner(TournamentGroup group, int winner) {
        int total = 0;

        for (int i = 0; i < tournamentGroups.size() && i < getIndexOfGroup(group); i++) {
            if (level > 0) {
                total++;
            } else {
                total += tournamentGroups.get(i).getMaxNumberOfWinners();
            }
        }
        total += winner;
        return total;
    }

    /**
     *********************************************
     *
     * GROUPS MANIPULATION
     *
     *********************************************
     */
    /**
     * Update the number of groups according of the size of the previous level.
     */
    protected void updateGroupsSize() {
        while ((previousLevel != null) && ((float) previousLevel.getNumberOfTotalTeamsPassNextRound() / 2 > this.size())) {
            addGroup(new TournamentGroup(2, 1, tournament, level, 0), false);
        }

        //When we remove two groups in one level, we must remove one in the next one.
        while ((previousLevel != null) && (Math.ceil((float) previousLevel.getNumberOfTotalTeamsPassNextRound() / 2) < this.size())) {
            removeGroup();
        }

        updateArenaOfGroups();

        if (nextLevel != null) {
            nextLevel.updateGroupsSize();
        }

        //If there are no groups left, delete this level..
        if (size() == 0) {
            groupManager.getLevels().remove(level);
        }
    }

    protected abstract LevelGroups addNewLevel(Tournament tournament, int level, LevelGroups nextLevel, LevelGroups previousLevel, TournamentGroupManager groupManager);

    protected void addGroup(TournamentGroup group, boolean selected) {
        addGroup(group, tournamentGroups.size(), selected);
    }

    protected void addGroup(TournamentGroup group, int index, boolean selected) {
        tournamentGroups.add(index, group);

        if ((nextLevel == null) && ((getNumberOfTotalTeamsPassNextRound() > 1) || tournamentGroups.size() > 1)) {
            nextLevel = addNewLevel(tournament, level + 1, null, this, groupManager);
            groupManager.getLevels().add(nextLevel);
        }

        if (nextLevel != null) {
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

    protected void removeGroup(TournamentGroup group) {
        if (tournamentGroups.size() > 0) {
            tournamentGroups.remove(group);
            if (nextLevel != null) {
                nextLevel.updateGroupsSize();
                if (nextLevel.size() == 0) {
                    nextLevel = null;
                }
            }
        }
    }

    /**
     *********************************************
     *
     * WINNER DESTINATION
     *
     *********************************************
     */
    protected Integer obtainPositionOfOneWinnerInTreeOdd(int branch, int branchs) {
        return ((branch + 1) % branchs);
    }

    protected abstract Integer getPositonOfOneWinnerInTournament(int branch, int branchs);

    protected Integer getGroupIndexDestinationOfWinner(TournamentGroup group, int winner) {
        int winnerTeams = getNumberOfTotalTeamsPassNextRound();
        int winnerIndex = getGlobalPositionWinner(group, winner);
        return getPositonOfOneWinnerInTournament(winnerIndex, winnerTeams);
    }

    protected TournamentGroup getGroupDestinationOfWinner(TournamentGroup group, int winner) {
        return nextLevel.tournamentGroups.get(getGroupIndexDestinationOfWinner(group, winner));
    }

    protected Integer getGroupIndexSourceOfWinner(TournamentGroup group, int winner) {
        if (level > 0) {
            for (int groupIndex = 0; groupIndex < previousLevel.tournamentGroups.size(); groupIndex++) {
                if (getGroupDestinationOfWinner(previousLevel.tournamentGroups.get(groupIndex), winner).equals(group)) {
                    return groupIndex;
                }
            }
        }
        return null;
    }

    protected TournamentGroup getGroupSourceOfWinner(TournamentGroup group, int winner) {
        return previousLevel.tournamentGroups.get(getGroupIndexSourceOfWinner(group, winner));
    }
}
