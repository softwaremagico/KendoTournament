package com.softwaremagico.ktg.gui.tournament;

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
import com.softwaremagico.ktg.tournament.TournamentGroup;
import java.util.ArrayList;
import java.util.List;

public abstract class LeagueLevel {

    protected int level;
    private Tournament tournament;
    protected List<TournamentGroupBox> tournamentGroupBoxs;
    protected LeagueLevel nextLevel;
    protected LeagueLevel previousLevel;
    protected TournamentGroupManager groupManager;

    LeagueLevel(Tournament tournament, int level, LeagueLevel nextLevel, LeagueLevel previousLevel, TournamentGroupManager groupManager) {
        this.tournament = tournament;
        tournamentGroupBoxs = new ArrayList<>();
        this.level = level;
        this.nextLevel = nextLevel;
        this.previousLevel = previousLevel;
        this.groupManager = groupManager;
    }

    protected void updateGroups() {
        for (TournamentGroupBox tgb : tournamentGroupBoxs) {
            tgb.update();
        }
    }

    protected void activateGroupsColor(boolean color) {
        for (TournamentGroupBox tgb : tournamentGroupBoxs) {
            tgb.activateColor(color);
        }
    }

    protected void enhanceGroups(boolean yes) {
        for (TournamentGroupBox tgb : tournamentGroupBoxs) {
            tgb.enhance(yes);
        }
    }

    protected void onlyShow() {
        for (TournamentGroupBox tgb : tournamentGroupBoxs) {
            tgb.onlyShow();
        }
    }

    protected int size() {
        return tournamentGroupBoxs.size();
    }

    public List<TournamentGroup> getGroups() {
        return tournamentGroupBoxs;
    }

    /**
     * Return the las group of the level.
     */
    protected TournamentGroup getLastGroupOfLevel() {
        if (tournamentGroupBoxs.size() > 0) {
            return tournamentGroupBoxs.get(tournamentGroupBoxs.size() - 1);
        } else {
            return null;
        }
    }

    protected void deleteTeams() {
        for (TournamentGroup g : tournamentGroupBoxs) {
            g.deleteTeams();
        }
    }

    protected boolean isFightOfLevel(Fight fight) {
        for (TournamentGroup t : tournamentGroupBoxs) {
            if (t.isFightOfGroup(fight)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isGroupOfLevel(TournamentGroup tgroup) {
        for (TournamentGroup t : tournamentGroupBoxs) {
            if (tgroup.equals(t)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isLevelFinished(ArrayList<Fight> fights) {
        for (TournamentGroup t : tournamentGroupBoxs) {
            if (!t.areFightsOverOrNull(fights)) {
                return false;
            }
        }
        return true;
    }

    protected int getNumberOfTotalTeamsPassNextRound() {
        int teams = 0;
        for (int i = 0; i < tournamentGroupBoxs.size(); i++) {
            teams += tournamentGroupBoxs.get(i).getMaxNumberOfWinners();
        }
        return teams;
    }

    protected void updateArenaOfGroups() {
        if (tournamentGroupBoxs.size() > 0) {
            /**
             * The arena of a group in a level > 1 is the same arena of the
             * group in the same row.
             */
            if (previousLevel != null) {
                for (int j = 0; j < tournamentGroupBoxs.size(); j++) {
                    tournamentGroupBoxs.get(j).arena = previousLevel.getGroups().get((int) (j * (((long) previousLevel.size()) / size()))).arena;
                }
            } else {
                /**
                 * In level zero, all groups are divided by arenas.
                 */
                double groupsPerArena = Math.ceil((double) tournamentGroupBoxs.size() / (double) tournament.getFightingAreas());
                for (int j = 0; j < tournamentGroupBoxs.size(); j++) {
                    tournamentGroupBoxs.get(j).arena = (j) / (int) groupsPerArena;
                }
            }
        }
        if (nextLevel != null) {
            nextLevel.updateArenaOfGroups();
        }
    }

    protected boolean loadLevel(Tournament tournament) {
        for (TournamentGroup group : tournamentGroupBoxs) {
            if (!group.loadTeams(tournament)) {
                return false;
            }
        }
        return true;
    }

    protected TournamentGroup getGroupOfFight(Fight fight) {
        for (TournamentGroup group : tournamentGroupBoxs) {
            if (group.getTeams().contains(fight.getTeam1()) && group.getTeams().contains(fight.getTeam2())) {
                return group;
            }
        }
        return null;
    }

    protected List<Team> getUsedTeams() {
        List<Team> usedTeams = new ArrayList<>();
        for (TournamentGroup group : tournamentGroupBoxs) {
            usedTeams.addAll(group.getTeams());
        }
        return usedTeams;
    }

    protected TournamentGroup getLastSelected() {
        for (int i = 0; i < tournamentGroupBoxs.size(); i++) {
            if (tournamentGroupBoxs.get(i).isSelected()) {
                return tournamentGroupBoxs.get(i);
            }
        }
        return null;
    }

    protected Integer getIndexLastSelected() {
        for (int i = 0; i < tournamentGroupBoxs.size(); i++) {
            if (tournamentGroupBoxs.get(i).isSelected()) {
                return new Integer(i);
            }
        }
        return null;
    }

    protected Integer getIndexOfGroup(TournamentGroupBox group) {
        for (int i = 0; i < tournamentGroupBoxs.size(); i++) {
            if (tournamentGroupBoxs.get(i).equals(group)) {
                return i;
            }
        }
        return null;
    }

    protected int getGlobalPositionWinner(TournamentGroup group, int winner) {
        int total = 0;

        for (int i = 0; i < tournamentGroupBoxs.size() && i < getIndexOfGroup(group); i++) {
            if (level > 0) {
                total++;
            } else {
                total += tournamentGroupBoxs.get(i).getMaxNumberOfWinners();
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
        for (int i = 0; i < tournamentGroupBoxs.size(); i++) {
            if (!arenas.contains(tournamentGroupBoxs.get(i).arena)) {
                arenas.add(tournamentGroupBoxs.get(i).arena);
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
            addGroup(new TournamentGroup(1, tournament, level, 0));
        }

        // When we remove two groups in one level, we must remove one in the next one.
        while ((previousLevel != null) && (Math.ceil((float) previousLevel.getNumberOfTotalTeamsPassNextRound() / 2) < this.size())) {
            removeGroup();
        }

        updateArenaOfGroups();

        if (nextLevel != null) {
            nextLevel.updateGroupsSize();
        }

        // If there are no groups left, delete this level..
        if (size() == 0) {
            if (level < groupManager.getLevels().size()) {
                groupManager.getLevels().remove(level);
            }
        }
    }

    protected abstract LeagueLevel addNewLevel(Tournament tournament, int level, LeagueLevel nextLevel, LeagueLevel previousLevel,
            TournamentGroupManager groupManager);

    protected void addGroup(TournamentGroup group) {
        addGroup(group, tournamentGroupBoxs.size());
    }

    protected void addGroup(TournamentGroup group, int index) {
        tournamentGroupBoxs.add(index, group);

        if ((nextLevel == null) && ((getNumberOfTotalTeamsPassNextRound() > 1) || tournamentGroupBoxs.size() > 1)) {
            nextLevel = addNewLevel(tournament, level + 1, null, this, groupManager);
            groupManager.getLevels().add(nextLevel);
        }

        if (nextLevel != null) {
            nextLevel.updateGroupsSize();
        }
    }

    protected void removeGroup() {
        if (tournamentGroupBoxs.size() > 0) {
            tournamentGroupBoxs.remove(tournamentGroupBoxs.size() - 1);
            if (nextLevel != null) {
                nextLevel.updateGroupsSize();
                if (nextLevel.size() == 0) {
                    nextLevel = null;
                }
            }
        }
    }

    protected void removeGroup(TournamentGroupBox group) {
        if (tournamentGroupBoxs.size() > 0) {
            tournamentGroupBoxs.remove(group);
            if (nextLevel != null) {
                nextLevel.updateGroupsSize();
                if (nextLevel.size() == 0) {
                    nextLevel = null;
                }
            }
        }
    }

    protected void removeGroups() {
        tournamentGroupBoxs = new ArrayList<>();
    }

    /**
     *********************************************
     *
     * WINNER DESTINATION
     *
     *********************************************
     */
    protected Integer obtainPositionOfOneWinnerInTreeOdd(int branch, int branchs) {
        return ((branch + 1) % branchs) / 2;
    }

    protected Integer obtainPositionOfOneWinnerInTreePair(int branch, int branchs) {
        return (branch / 2);
    }

    protected Integer obtainPositionOfOneWinnerInTree(int branch, int branchs) {
        // Design a tree grouping the designed groups by two.
        if ((branchs / 2) % 2 == 0) {
            return obtainPositionOfOneWinnerInTreePair(branch, branchs);
        } else {
            // If the number of groups are odd, are one group that never fights. Then, shuffle it.
            return obtainPositionOfOneWinnerInTreeOdd(branch, branchs);
        }
    }

    protected abstract Integer getGroupIndexDestinationOfWinner(TournamentGroup group, int winner);

    protected TournamentGroup getGroupDestinationOfWinner(TournamentGroup group, int winner) {
        return nextLevel.tournamentGroupBoxs.get(getGroupIndexDestinationOfWinner(group, winner));
    }

    protected Integer getGroupIndexSourceOfWinner(TournamentGroup group, int winner) {
        if (level > 0) {
            for (int groupIndex = 0; groupIndex < previousLevel.tournamentGroupBoxs.size(); groupIndex++) {
                if (previousLevel.getGroupDestinationOfWinner(previousLevel.tournamentGroupBoxs.get(groupIndex), winner).equals(group)) {
                    return groupIndex;
                }
            }
        }
        return null;
    }

    protected TournamentGroup getGroupSourceOfWinner(TournamentGroup group, int winner) {
        return previousLevel.tournamentGroupBoxs.get(getGroupIndexSourceOfWinner(group, winner));
    }

    public String levelInfo() {
        String info = "Level: " + level + " Groups: ";

        for (TournamentGroup group : tournamentGroupBoxs) {
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
        for (TournamentGroup group : tournamentGroupBoxs) {
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
