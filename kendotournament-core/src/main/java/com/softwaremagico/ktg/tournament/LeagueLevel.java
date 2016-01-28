package com.softwaremagico.ktg.tournament;

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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;

public abstract class LeagueLevel implements Serializable {

    private static final long serialVersionUID = -2913139758103386033L;
    protected int level;
    protected Tournament tournament;
    protected List<TGroup> tournamentGroups;
    protected LeagueLevel nextLevel;
    protected LeagueLevel previousLevel;

    protected LeagueLevel(Tournament tournament, int level, LeagueLevel nextLevel, LeagueLevel previousLevel) {
        this.tournament = tournament;
        tournamentGroups = new ArrayList<>();
        this.level = level;
        this.nextLevel = nextLevel;
        this.previousLevel = previousLevel;
    }

    protected Integer size() {
        return tournamentGroups.size();
    }

    protected void fillGroups(Fight fight) {
        while (fight.getGroup() >= getGroups().size()) {
            addGroup(new TreeTournamentGroup(fight.getTournament(), fight.getLevel(), fight.getAsignedFightArea()));
        }
        TGroup group = getGroups().get(fight.getGroup());
        group.addFight(fight);
    }

    public List<TGroup> getGroups() {
        return tournamentGroups;
    }

    /**
     * Return the las group of the level.
     *
     * @return
     */
    protected TGroup getLastGroupOfLevel() {
        if (tournamentGroups.size() > 0) {
            return tournamentGroups.get(tournamentGroups.size() - 1);
        } else {
            return null;
        }
    }

    protected void removeTeams() {
        for (TGroup g : tournamentGroups) {
            g.removeTeams();
        }
    }

    protected boolean isFightOfLevel(Fight fight) {
        for (TGroup t : tournamentGroups) {
            if (t.isFightOfGroup(fight)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isGroupOfLevel(TGroup tgroup) {
        for (TGroup t : tournamentGroups) {
            if (tgroup.equals(t)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isLevelFinished() {
        for (TGroup t : tournamentGroups) {
            if (!t.areFightsOverOrNull()) {
                return false;
            }
        }
        return true;
    }

    protected int getNumberOfTotalTeamsPassNextRound() {
        int teams = 0;
        for (TGroup tournamentGroup : tournamentGroups) {
            teams += tournamentGroup.getMaxNumberOfWinners();
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
                    tournamentGroups.get(j).setFightArea(
                            previousLevel.getGroups().get((int) (j * (((long) previousLevel.size()) / size())))
                            .getFightArea());
                }
            } else {
                /**
                 * In level zero, all groups are divided by arenas.
                 */
                double groupsPerArena = Math.ceil((double) tournamentGroups.size()
                        / (double) tournament.getFightingAreas());
                for (int j = 0; j < tournamentGroups.size(); j++) {
                    tournamentGroups.get(j).setFightArea((j) / (int) groupsPerArena);
                }
            }
        }
        if (nextLevel != null) {
            nextLevel.updateArenaOfGroups();
        }
    }

    protected TGroup getGroupOfFight(Fight fight) {
        for (TGroup group : tournamentGroups) {
            if (group.getTeams().contains(fight.getTeam1()) && group.getTeams().contains(fight.getTeam2())) {
                return group;
            }
        }
        return null;
    }

    public Set<Team> getUsedTeams() {
        Set<Team> usedTeams = new HashSet<>();
        for (TGroup group : tournamentGroups) {
            usedTeams.addAll(group.getTeams());
        }
        return usedTeams;
    }

    public Integer getIndexOfGroup(TGroup group) {
        for (int i = 0; i < tournamentGroups.size(); i++) {
            if (tournamentGroups.get(i).equals(group)) {
                return i;
            }
        }
        return null;
    }

    protected int getGlobalPositionWinner(TGroup group, Integer winner) {
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
     * Not in all levels the arenas used are the arenas available.
     *
     * @return
     */
    protected int getArenasUsed() {
        List<Integer> arenas = new ArrayList<>();
        for (TGroup tournamentGroup : tournamentGroups) {
            if (!arenas.contains(tournamentGroup.getFightArea())) {
                arenas.add(tournamentGroup.getFightArea());
            }
        }
        return arenas.size();
    }

    public void update() {
        fillTeamsWithWinnersPreviousLevel();
    }

    /**
     * Update level with winners of previous level.
     */
    private void fillTeamsWithWinnersPreviousLevel() {
        if (previousLevel != null && !previousLevel.getGroups().isEmpty()) {
            for (int winner = 0; winner < tournament.getHowManyTeamsOfGroupPassToTheTree(); winner++) {
                for (TGroup previousLevelGroup : previousLevel.getGroups()) {
                    // Add winners only if created
                    if (winner < previousLevelGroup.getWinners().size()) {
                        TGroup group = previousLevel.getGroupDestinationOfWinner(previousLevelGroup, winner);
                        group.addTeam(previousLevelGroup.getWinners().get(winner));
                    }
                }
            }
        }
    }

    public boolean hasFightsAssigned() {
        for (TGroup group : tournamentGroups) {
            if (group.getFights() == null || group.getFights().isEmpty()) {
                return false;
            }
        }
        return true;
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
        while ((previousLevel != null) && ((previousLevel.getNumberOfTotalTeamsPassNextRound() + 1) / 2 > this.size())) {
            addGroup(new TreeTournamentGroup(tournament, level, 0));
        }

        // When we remove two groups in one level, we must remove one in the next one.
        while ((previousLevel != null)
                && (Math.ceil((float) previousLevel.getNumberOfTotalTeamsPassNextRound() / 2) < this.size())) {
            removeGroup();
        }

        updateArenaOfGroups();

        // If only one group. It is laslevel
        if (tournamentGroups.size() < 2) {
            nextLevel = null;
        }

        if (nextLevel != null) {
            nextLevel.updateGroupsSize();
        }
    }

    protected abstract LeagueLevel addNewLevel(Tournament tournament, Integer level, LeagueLevel nextLevel,
            LeagueLevel previousLevel);

    /**
     * Return a new nextLevel if has been created.
     *
     * @param group
     * @return
     */
    protected LeagueLevel addGroup(TGroup group) {
        tournamentGroups.add(group);

        if ((nextLevel == null) && ((getNumberOfTotalTeamsPassNextRound() > 1) || tournamentGroups.size() > 1)) {
            nextLevel = addNewLevel(tournament, level + 1, null, this);
        }

        if (nextLevel != null) {
            nextLevel.updateGroupsSize();
        }

        return nextLevel;
    }

    /**
     *
     * @return true if next Level must be deleted.
     */
    protected boolean removeGroup() {
        if (tournamentGroups.size() > 0) {
            tournamentGroups.remove(tournamentGroups.size() - 1);
            if (nextLevel != null) {
                nextLevel.updateGroupsSize();
                if (nextLevel.size() == 0) {
                    nextLevel = null;
                    return true;
                }
            }
        }
        return false;
    }

    protected void removeGroup(TGroup group) {
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

    protected void removeGroups() {
        tournamentGroups = new ArrayList<>();
        nextLevel = null;
    }

    /**
     *********************************************
     *
     * WINNER DESTINATION
     *
     *********************************************
     */
    /**
     * Position of a group if the tree has odd number of leaves.
     *
     * @param branch
     * @param branchs
     * @return
     */
    protected Integer obtainPositionOfOneWinnerInTreeOdd(Integer branch, Integer branchs) {
        return ((branch + 1) % branchs) / 2;
    }

    protected Integer obtainPositionOfOneWinnerInTreePair(Integer branch, Integer branchs) {
        return (branch / 2);
    }

    protected Integer obtainPositionOfOneWinnerInTree(Integer branch, Integer branchs) {
        // Design a tree grouping the designed groups by two.
        if ((branchs / 2) % 2 == 0) {
            return obtainPositionOfOneWinnerInTreePair(branch, branchs);
        } else {
            // If the number of groups are odd, are one group that never fights. Then, shuffle it.
            return obtainPositionOfOneWinnerInTreeOdd(branch, branchs);
        }
    }

    public abstract Integer getGroupIndexDestinationOfWinner(TGroup group, Integer winner);

    public TGroup getGroupDestinationOfWinner(TGroup group, Integer winner) {
        return nextLevel.getGroups().get(getGroupIndexDestinationOfWinner(group, winner));
    }

    protected Integer getGroupIndexSourceOfWinner(TGroup group, Integer winner) {
        if (level > 0) {
            for (int groupIndex = 0; groupIndex < previousLevel.getGroups().size(); groupIndex++) {
                if (previousLevel.getGroupDestinationOfWinner(previousLevel.getGroups().get(groupIndex), winner)
                        .equals(group)) {
                    return groupIndex;
                }
            }
        }
        return null;
    }

    protected TGroup getGroupSourceOfWinner(TGroup group, Integer winner) {
        return previousLevel.getGroups().get(getGroupIndexSourceOfWinner(group, winner));
    }

    @Override
    public String toString() {
        String text = "-------------------------------------\n";
        text += "Level: " + level + "\n";
        for (TGroup group : tournamentGroups) {
            text += group + "\n";
        }
        text += "-------------------------------------\n";
        if (nextLevel != null) {
            text += nextLevel;
        }
        return text;
    }
}
