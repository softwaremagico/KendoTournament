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
import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import java.util.ArrayList;
import java.util.List;

public abstract class LeagueLevel {

    protected int level;
    private Tournament tournament;
    protected List<TournamentGroup> tournamentGroups;
    protected LeagueLevel nextLevel;
    protected LeagueLevel previousLevel;

    LeagueLevel(Tournament tournament, int level, LeagueLevel nextLevel, LeagueLevel previousLevel) {
        this.tournament = tournament;
        tournamentGroups = new ArrayList<>();
        this.level = level;
        this.nextLevel = nextLevel;
        this.previousLevel = previousLevel;
    }

    protected Integer size() {
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
            g.removeTeams();
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
                    tournamentGroups.get(j).setFightArea(previousLevel.getGroups().get((int) (j * (((long) previousLevel.size()) / size()))).getFightArea());
                }
            } else {
                /**
                 * In level zero, all groups are divided by arenas.
                 */
                double groupsPerArena = Math.ceil((double) tournamentGroups.size() / (double) tournament.getFightingAreas());
                for (int j = 0; j < tournamentGroups.size(); j++) {
                    tournamentGroups.get(j).setFightArea((j) / (int) groupsPerArena);
                }
            }
        }
        if (nextLevel != null) {
            nextLevel.updateArenaOfGroups();
        }
    }

    protected TournamentGroup getGroupOfFight(Fight fight) {
        for (TournamentGroup group : tournamentGroups) {
            if (group.getTeams().contains(fight.getTeam1()) && group.getTeams().contains(fight.getTeam2())) {
                return group;
            }
        }
        return null;
    }

    protected List<Team> getUsedTeams() {
        List<Team> usedTeams = new ArrayList<>();
        for (TournamentGroup group : tournamentGroups) {
            usedTeams.addAll(group.getTeams());
        }
        return usedTeams;
    }

    protected Integer getIndexOfGroup(TournamentGroup group) {
        for (int i = 0; i < tournamentGroups.size(); i++) {
            if (tournamentGroups.get(i).equals(group)) {
                return i;
            }
        }
        return null;
    }

    protected int getGlobalPositionWinner(TournamentGroup group, Integer winner) {
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
        for (int i = 0; i < tournamentGroups.size(); i++) {
            if (!arenas.contains(tournamentGroups.get(i).getFightArea())) {
                arenas.add(tournamentGroups.get(i).getFightArea());
            }
        }
        return arenas.size();
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
            addGroup(new TournamentGroup(tournament, level, 0));
        }

        // When we remove two groups in one level, we must remove one in the next one.
        while ((previousLevel != null) && (Math.ceil((float) previousLevel.getNumberOfTotalTeamsPassNextRound() / 2) < this.size())) {
            removeGroup();
        }

        updateArenaOfGroups();

        if (nextLevel != null) {
            nextLevel.updateGroupsSize();
        }
    }

    protected abstract LeagueLevel addNewLevel(Tournament tournament, Integer level, LeagueLevel nextLevel, LeagueLevel previousLevel);

    protected void addGroup(TournamentGroup group) {
        addGroup(group, tournamentGroups.size());
    }

    /**
     * Return a new nextLevel if has been created. 
     * @param group
     * @param index
     * @return 
     */
    protected LeagueLevel addGroup(TournamentGroup group, Integer index) {
        tournamentGroups.add(index, group);

        if ((nextLevel == null) && ((getNumberOfTotalTeamsPassNextRound() > 1) || tournamentGroups.size() > 1)) {
            nextLevel = addNewLevel(tournament, level + 1, null, this);
            //groupManager.getLevels().add(nextLevel);
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

    protected void removeGroups() {
        tournamentGroups = new ArrayList<>();
    }

    /**
     *********************************************
     *
     * WINNER DESTINATION
     *
     *********************************************
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

    public abstract Integer getGroupIndexDestinationOfWinner(TournamentGroup group, Integer winner);

    protected TournamentGroup getGroupDestinationOfWinner(TournamentGroup group, Integer winner) {
        return nextLevel.tournamentGroups.get(getGroupIndexDestinationOfWinner(group, winner));
    }

    protected Integer getGroupIndexSourceOfWinner(TournamentGroup group, Integer winner) {
        if (level > 0) {
            for (int groupIndex = 0; groupIndex < previousLevel.tournamentGroups.size(); groupIndex++) {
                if (previousLevel.getGroupDestinationOfWinner(previousLevel.tournamentGroups.get(groupIndex), winner).equals(group)) {
                    return groupIndex;
                }
            }
        }
        return null;
    }

    protected TournamentGroup getGroupSourceOfWinner(TournamentGroup group, Integer winner) {
        return previousLevel.tournamentGroups.get(getGroupIndexSourceOfWinner(group, winner));
    }

    public String levelInfo() {
        String info = "Level: " + level + " Groups: ";

        for (TournamentGroup group : tournamentGroups) {
            info += group.getTeams().size() + "\t";
        }
        return info;
    }

    public void showTree() {
        System.out.println("-------------------------------------");
        System.out.println(levelInfo());
        if (nextLevel != null) {
            nextLevel.showTree();
        }
        System.out.println("-------------------------------------");
    }

    public void showTeams() {
        System.out.println("-------------------------------------");
        for (TournamentGroup group : tournamentGroups) {
            for (Team team : group.getTeams()) {
                System.out.print(team.getName() + " ");
            }
            System.out.println();
        }
        if (nextLevel != null) {
            nextLevel.showTeams();
        }
        System.out.println("-------------------------------------");
    }
}
