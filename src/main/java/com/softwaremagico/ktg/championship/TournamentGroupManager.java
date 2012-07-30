package com.softwaremagico.ktg.championship;
/*
 * #%L KendoTournamentGenerator %% Copyright (C) 2008 - 2012 Softwaremagico %%
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
 * this program; If not, see <http://www.gnu.org/licenses/gpl-3.0.html>. #L%
 */

import com.softwaremagico.ktg.*;
import com.softwaremagico.ktg.files.MyFile;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author jorge
 */
public class TournamentGroupManager implements Serializable {

    //private List<TournamentGroup> tournamentGroups = new ArrayList<>();
    private List<LevelGroups> levels;
    //private List<Integer> levels = new ArrayList<>();  //"Index" to the designedGroup that is the first one of the next level. Each element of the list is a leve and the value is the number of designedgrou
    transient Tournament championship;
    public String mode = "tree";
    private Links links;
    private final String FOLDER = "designer";
    public int default_max_winners = 1;

    public TournamentGroupManager(Tournament tmp_championship) {
        try {
            mode = tmp_championship.mode;
            championship = tmp_championship;
            //levels.add(0);
            links = new Links();
            levels = new ArrayList<>();
        } catch (NullPointerException npe) {
        }
    }

    public void update() {
        Log.debug("Current number of fights over before updating design groups: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
        for (int i = 0; i < levels.size(); i++) {
            levels.get(i).updateGroups();
        }
        Log.debug("Current number of fights over after updating design groups: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
    }

    public void color(boolean color) {
        for (int i = 0; i < levels.size(); i++) {
            levels.get(i).activateGroupsColor(color);
        }
    }

    public int sizeOfTournamentLevelZero(String championship) {
        if (levels.size() > 0) {
            return levels.size();
        }
        return 0;
    }

    public void enhance(boolean yes) {
        for (LevelGroups l : levels) {
            l.enhanceGroups(yes);
        }
    }

    public void onlyShow() {
        for (LevelGroups l : levels) {
            l.onlyShow();
        }
    }

    public Tournament returnTournament() {
        return championship;
    }

    /**
     * ********************************************
     *
     * TEAMS MANIPULATION
     *
     *********************************************
     */
    /**
     * Teams already inserted in the championship.
     *
     * @return
     */
    private List<Team> getUsedTeams() {
        if (levels.size() > 0) {
            return levels.get(0).getUsedTeams();
        }
        return new ArrayList<>();
    }

    public boolean containTeamInTournament(Team team, String Championship) {
        return getUsedTeams().contains(team);
    }

    protected int getNumberOfTotalTeamsPassNextRound(Integer level) {
        int teams = 0;
        List<TournamentGroup> groups = returnGroupsOfLevel(level);
        for (int i = 0; i < groups.size(); i++) {
            teams += groups.get(i).returnMaxNumberOfWinners();
        }
        return teams;
    }

    public void setNumberOfTeamsPassNextRound(int value) {
        default_max_winners = value;
        List<TournamentGroup> groups = returnGroupsOfLevel(0);

        for (TournamentGroup group : groups) {
            group.updateMaxNumberOfWinners(value);
        }
    }

    protected int obtainGlobalPositionWinner(int level, int groupIndex, int winner) {
        int total = 0;
        List<TournamentGroup> groups = returnGroupsOfLevel(level);
        for (int i = 0; i < groups.size() && i < returnPositionOfGroupInItsLevel(groupIndex); i++) {
            if (level > 0) {
                total++;
            } else {
                total += groups.get(i).returnMaxNumberOfWinners();
            }
        }
        total += winner;
        return total;
    }

    /**
     * Fill a group with the winners of the previous level.
     *
     * @param group position of the group to complete relative to the level.
     */
    private void fillGroupWithWinnersPreviousLevel(TournamentGroup group, ArrayList<Fight> fights, boolean resolvDraw) {
        updateScoreForTeams(fights);
        //TournamentGroup dg = tournamentGroups.get(groupIndex);
        if (group.getLevel() > 0) {
            List<TournamentGroup> groups = returnGroupsOfLevel(group.getLevel() - 1);
            for (int i = 0; i < groups.size(); i++) {
                for (int k = 0; k < groups.get(i).returnMaxNumberOfWinners(); k++) {
                    int destination;
                    if (!mode.equals("manual") || group.getLevel() - 1 > 0) {
                        destination = (int) obtainPositonOfOneWinnerInTournament(obtainGlobalPositionWinner(group.getLevel() - 1, i, k),
                                getNumberOfTotalTeamsPassNextRound(group.getLevel() - 1), group.getLevel() - 1);
                    } else {
                        destination = returnPositionOfGroupInItsLevel(returnIndexOfGroup(obtainManualDestination(groups.get(i), k)));
                    }
                    //System.out.println("Group:" + i + " winner:" + k + " destination: " + destination);
                    //If the searched dg's destination point to the group to complete, then means it is a source. 
                    if (destination == returnPositionOfGroupInItsLevel(group)) {
                        TournamentGroup previousDg = groups.get(i);
                        group.addTeam(previousDg.getTeamInOrderOfScore(k, fights, resolvDraw));
                        //  System.out.println(previousDg.getTeamInOrderOfScore(k, fightManager, true).returnName());
                    }
                }
                //System.out.println("----------");
            }
        }
    }

    private Team winnerOfLastFight(ArrayList<Fight> fights) {
        //Last fight.
        try {
            TournamentGroup dg = tournamentGroups.get(tournamentGroups.size() - 1);
            return dg.getTeamInOrderOfScore(0, fights, true);
        } catch (ArrayIndexOutOfBoundsException aiob) {
            return null;
        }
    }

    private boolean isTeamIncludedInList(List<Team> list, Team team) {
        return list.contains(team);
    }

    public void deleteTeamsOfLevel(Integer level) {
        if (level >= 0 && level < levels.size()) {
            levels.get(level).deleteTeams();
        }
    }

    /**
     * ********************************************
     *
     * GROUPS MANIPULATION
     *
     *********************************************
     */
    /**
     * Add a new group box to the designer.
     *
     * @param d
     * @param level
     */
    public void add(TournamentGroup d, boolean selected, boolean autocomplete) {
        //Intermediate level
        if (d.getLevel() < getNumberOfLevels() - 1) {
            //Store it at the end or in the selected group.
            if (d.getLevel() == 0 && selected) {
                tournamentGroups.add(returnIndexLastSelected() + 1, d);
            } else {
                tournamentGroups.add(getLastPositionOfLevel(d.getLevel()) + 1, d);
            }
        } else {
            //Last level only needs to store it. All "pointers" all ok.
            tournamentGroups.add(d);
        }
        if (autocomplete) {
            updateInnerLevel(d.getLevel());
        }
    }

    public TournamentGroup get(int i) {
        if (i >= 0 && i < tournamentGroups.size()) {
            return tournamentGroups.get(i);
        }
        return null;
    }

    public void remove(int groupIndex) {
        try {
            int level = tournamentGroups.get(groupIndex).getLevel();
            tournamentGroups.remove(groupIndex);
            updateInnerLevel(level);
        } catch (ArrayIndexOutOfBoundsException aiob) {
        }
        //When we add or remove two groups in one level, we must add one in the next one.
    }

    public int size() {
        int size = 0;
        for (LevelGroups level : levels) {
            size += level.size();
        }
        return size;
    }

    public int returnIndexOfGroup(TournamentGroup d) {
        for (int i = 0; i < tournamentGroups.size(); i++) {
            if (tournamentGroups.get(i).equals(d)) {
                return i;
            }
        }
        return -1;
    }

    public List<TournamentGroup> returnGroupsOfLevel(Integer level) {
        if (level >= 0 && level < levels.size()) {
            return levels.get(level).getGroups();
        }
        return null;
    }

    public List<Integer> returnIndexOfGroupsOfLevel(Integer level) {
        List<Integer> groups = new ArrayList<>();
        List<TournamentGroup> groupsLevel = returnGroupsOfLevel(level);
        for (int i = 0; i < groupsLevel.size(); i++) {
            groups.add(returnIndexOfGroup(groupsLevel.get(i)));
        }
        return groups;
    }

    public List<Integer> returnIndexOfGroupsOfLevelOrMore(Integer level) {
        List<Integer> groups = new ArrayList<>();
        for (int i = 0; i < tournamentGroups.size(); i++) {
            if (tournamentGroups.get(i).getLevel() == level) {
                groups.add(i);
            }
        }
        return groups;
    }

    public boolean isSomeSelected() {
        return getLastGroupSelected() != null;
    }

    public TournamentGroup getLastGroupSelected() {
        if (levels.size() > 0) {
            return levels.get(0).getLastSelected();
        }
        return null;
    }

    public int returnIndexLastSelected() {
        for (int i = 0; i < tournamentGroups.size(); i++) {
            if (tournamentGroups.get(i).isSelected()) {
                return i;
            }
        }
        return -1;
    }

    public void selectGroup(int groupIndex) {
        if (groupIndex < tournamentGroups.size() && groupIndex >= 0) {
            tournamentGroups.get(groupIndex).setSelected(this);
        }
    }

    public void unselectDesignedGroups() {
        for (int i = 0; i < tournamentGroups.size(); i++) {
            tournamentGroups.get(i).setUnselected();
        }
    }

    protected void selectLastGroup() {
        List<TournamentGroup> groups = returnGroupsOfLevel(0);
        if (groups.size() > 0) {
            groups.get(groups.size() - 1).setSelected(this);
        }
    }

    /**
     * Return the position of a group relative on its level.
     *
     * @param groupIndex The index of the group in the list of groups.
     * @return
     */
    public Integer returnPositionOfGroupInItsLevel(Integer groupIndex) {
        try {
            TournamentGroup group = tournamentGroups.get(groupIndex);
            return returnPositionOfGroupInItsLevel(group);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Return the position of a group relative on its level.
     *
     * @param groupIndex The index of the group in the list of groups.
     * @return
     */
    public Integer returnPositionOfGroupInItsLevel(TournamentGroup group) {
        List<TournamentGroup> levelGroups = returnGroupsOfLevel(group.getLevel());
        for (int i = 0; i < levelGroups.size(); i++) {
            if (levelGroups.get(i).equals(group)) {
                return i;
            }
        }
        return null;
    }

    private boolean allGroupsInSameArena(List<TournamentGroup> groups) {
        for (int i = 0; i < groups.size() - 1; i++) {
            if (groups.get(i).arena != groups.get(i + 1).arena) {
                return false;
            }
        }
        return true;
    }

    public void deleteGroupsOfLevel(Integer level) {
        for (int i = 0; i < tournamentGroups.size(); i++) {
            if (tournamentGroups.get(i).getLevel() >= level) {
                tournamentGroups.remove(i);
            }
        }
        updateInnerLevels();
    }

    public int returnLastGroupUsed() {
        int last = 0;
        for (int i = 0; i < tournamentGroups.size(); i++) {
            if (tournamentGroups.get(i).areFightsOver()) {
                last = i;
            }
        }
        return last;
    }

    public int getLastPositionOfLevel(int level) {
        int index = 0;
        for (int i = 0; i < tournamentGroups.size(); i++) {
            if (tournamentGroups.get(i).getLevel() == index) {
                index = tournamentGroups.get(i).getLevel();
            }
        }
        return index;
    }

    /**
     * ********************************************
     *
     * LEVEL MANIPULATION
     *
     *********************************************
     */
    /**
     * Return the las group of the level.
     */
    private TournamentGroup getLastGroupOfLevel(Integer level) {
        if (level >= 0 && level < levels.size()) {
            return levels.get(level).getLastGroupOfLevel();
        } else {
            return null;
        }
    }

    /**
     * Return the number of levels. Levels start in zero but has size one.
     *
     * @return
     */
    public int getNumberOfLevels() {
        return levels.size();
    }

    private int calculateNumberOfLevels() {
        return (int) (Math.log(getSizeOfLevel(0)) / Math.log(2)) + default_max_winners;
    }

    public Integer getSizeOfLevel(Integer level) {
        if (level >= 0 && level < levels.size()) {
            return levels.get(level).size();
        } else {
            return null;
        }
    }

    public int returnCalculatedSizeOfLevel(int level) {
        switch (level) {
            case 0:
                return getSizeOfLevel(0);
            case 1:
                return (getNumberOfTotalTeamsPassNextRound(0)) / 2;
            default:
                return getSizeOfLevel(level - 1) / 2;
        }
    }

    public void updateInnerLevels() {
        updateInnerLevel(0);
    }

    public void updateInnerLevel(int level) {
        if (levels.size() > 0 && level < levels.size()) {
            levels.get(level).updateGroupsSize();
        }
    }

    void emptyInnerLevels() {
        for (int i = 1; i < levels.size(); i++) {
            levels.get(i).deleteTeams();
        }
    }

    public Integer getLevelOfFight(Fight fight) {
        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i).isFightOfLevel(fight)) {
                return i;
            }
        }
        return null;
    }

    public Integer getLevelOfGroup(TournamentGroup group) {
        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i).isGroupOfLevel(group)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Obtain the level of the tree where the program must continue.
     *
     * @param fightManager
     * @return int -1 if the championship is finished. Other value is the level
     * to continue.
     */
    public int firstLevelNotFinished(ArrayList<Fight> fights) {
        //Fight defined but not finished.
        try {
            for (int i = 0; i < fights.size(); i++) {
                if (!fights.get(i).isOver()) {
                    return fights.get(i).level;
                }
            }

            // Not fightManager defined yet.
            if (fights.isEmpty()) {
                return 0;
            } else {
                //The championship is over. It is over if the previous level is the final (only one group)
                if ((returnGroupsOfLevel(fights.get(fights.size() - 1).level)).size() == 1) {
                    return getNumberOfLevels();
                } //All fightManager finished. We assume that a level is completed.
                return fights.get(fights.size() - 1).level + 1;
            }
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    public Boolean isLevelFinished(ArrayList<Fight> fights, int level) {
        if (level >= 0 && level < levels.size()) {
            return levels.get(level).isLevelFinished(fights);
        }
        return null;
    }

    public ArrayList<Fight> nextLevel(ArrayList<Fight> fights, int fightArea, Tournament championship) {
        int nextLevel = firstLevelNotFinished(fights);
        int arena;
        try {
            //Not finished the championship.
            if (nextLevel >= 0 && nextLevel < getNumberOfLevels()) {
                Log.finer("Tournament not finished!");
                //Update fightManager to load the fightManager of other arenas and computers.
                if (championship.fightingAreas > 1) {
                    Log.finest("Retrieving data from other arenas.");
                    Log.debug("Current number of fights over before updating data: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
                    KendoTournamentGenerator.getInstance().fightManager.getFightsFromDatabase(championship.name);
                    Log.debug("Current number of fights over after updating data: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
                }

                //But the level is over and need more fights.
                if (KendoTournamentGenerator.getInstance().fightManager.areAllOver()) {
                    Log.finer("All fights are over.");
                    if (MessageManager.questionMessage("nextLevel", "Warning!")) {
                        Log.debug("Current number of fights over before updating winners: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
                        List<TournamentGroup> groups = returnGroupsOfLevel(nextLevel);
                        for (TournamentGroup g : groups) {
                            fillGroupWithWinnersPreviousLevel(g, KendoTournamentGenerator.getInstance().fightManager.getFights(), true);
                        }
                        Log.debug("Current number of fights over after updating winners: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
                    }
                    //set the team members order in the new level.
                    //updateOrderTeamsOfLevel(nextLevel);
                } else {
                    //Only one arena is finished: show message for waiting all fightManager are over.
                    if ((arena = KendoTournamentGenerator.getInstance().fightManager.allArenasAreOver()) != -1) {
                        MessageManager.translatedMessage("waitingArena", "", KendoTournamentGenerator.getInstance().returnShiaijo(arena) + "", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                Log.debug("Current number of fights over at the end updating fights: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
            } else {
                Log.finer("League finished");
                //Finished! The winner is...
                Team winner = winnerOfLastFight(fights);
                String winnername = "";
                try {
                    winnername = winner.returnName();
                } catch (NullPointerException npe) {
                    //npe.printStackTrace();
                    Log.severe("No winner obtained!");
                }
                //Show message when last fight is selected.
                if (KendoTournamentGenerator.getInstance().fightManager.areAllOver() && KendoTournamentGenerator.getInstance().fightManager.size() > 0) {
                    MessageManager.winnerMessage("leagueFinished", "Finally!", winnername, JOptionPane.INFORMATION_MESSAGE);
                }
                update();
                return new ArrayList<>();
            }
        } catch (NullPointerException npe) {
        }
        update();
        Log.debug("Current number of fights over before generating next level fights: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
        ArrayList<Fight> newFights = generateLevelFights(nextLevel);
        Log.debug("Current number of fights over after generating next level fights: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
        return newFights;
    }

    /**
     * ********************************************
     *
     * FIGHTS MANIPULATION
     *
     *********************************************
     */
    /**
     * Generate each fight for the different groups specified by the GUI.
     *
     * @return
     */
    public ArrayList<Fight> generateLevelFights(int level) {
        ArrayList<Fight> fights = new ArrayList<>();
        List<TournamentGroup> groups = returnGroupsOfLevel(level);

        boolean answer = false;
        int arena;
        //User must distribute the groups of level 0 in the different fightManager areas. 
        if (championship.fightingAreas > 1 && allGroupsInSameArena(groups) && level == 0) {
            answer = MessageManager.questionMessage("noFightsDistributedInArenas", "Warning!");
        }

        for (int i = 0; i < groups.size(); i++) {
            if (answer) {
                if (!mode.equals("manual") || level > 0) {
                    arena = i / (int) Math.ceil((double) getSizeOfLevel(level) / (double) championship.fightingAreas);
                } else {
                    //grouped by the destination group of the next level.
                    arena = (returnPositionOfGroupInItsLevel(returnIndexOfGroup(obtainManualDestination(groups.get(i), 0)))) % championship.fightingAreas;
                }
                groups.get(i).arena = arena;
                fights.addAll(groups.get(i).generateGroupFights(level, arena));
            } else {
                fights.addAll(groups.get(i).generateGroupFights(level));
            }
        }
        return fights;
    }

    private double obtainPositionOfOneWinnerInTreeLevelZero(int branch, int branchs) {
        if (branch % 2 == 0) {
            return branch;
        } else {
            return branchs - branch;
        }
    }

    private double obtainPositionOfOneWinnerInTreeLevelZeroOdd(int branch, int branchs) {
        if (branch % 2 == 0) {
            return branch;
        } else {
            return ((branch + 1) % branchs);
        }
    }

    private double obtainPositionOfOneWinnerInTreeMoreThanLevelZero(int branch, int branchs) {
        //Desgin a tree grouping the designed groups by two.
        if (branchs % 2 == 0) {
            return (branch);
        } else {
            //If the number of groups are odd, are one group that never fightManager. Then, shuffle it.
            return obtainPositionOfOneWinnerInTreeOdd(branch, branchs);
        }
    }

    private double obtainPositionOfOneWinnerInTreeOdd(int branch, int branchs) {
        return ((branch + 1) % branchs);
    }

    private double obtainPositionOfOneWinnerInTree(int branch, int branchs) {
        //Design a tree grouping the designed groups by two.
        if ((branchs / 2) % 2 == 0) {
            return (branch);
        } else {
            //If the number of groups are odd, are one group that never fightManager. Then, shuffle it.
            return obtainPositionOfOneWinnerInTreeOdd(branch, branchs);
        }
    }

    public double obtainPositonOfOneWinnerInTournament(int branch, int branchs, int level) {
        if (mode.equals("championship")) {
            if (level == 0) {
                //If the number of groups are odd, then some teams fightManager again between them. 
                if ((branchs / 2) % 2 != 0 && getNumberOfTotalTeamsPassNextRound(level) > getSizeOfLevel(level)) {
                    return obtainPositionOfOneWinnerInTreeLevelZeroOdd(branch, branchs) / 2;
                } else {
                    return obtainPositionOfOneWinnerInTreeLevelZero(branch, branchs) / 2;
                }
            } else {
                return obtainPositionOfOneWinnerInTreeMoreThanLevelZero(branch, branchs) / 2;
            }
        } else {
            return obtainPositionOfOneWinnerInTree(branch, branchs) / 2;
        }
    }

    public void updateScoreForTeams(ArrayList<Fight> fights) {
        for (LevelGroups level : levels) {
            level.updateScoreOfTeams(fights);
        }
    }

    /*
     * private int obtainMaxLevel(ArrayList<Fight> fights) { int max = 0; for
     * (int i = 0; i < fights.size(); i++) { if (fights.get(i).level > max) {
     * max = fights.get(i).level; } } return max; }
     */
    public TournamentGroup getGroupOfFight(ArrayList<Fight> fights, int fightIndex) {
        if (fightIndex >= 0 && fightIndex < fights.size()) {
            return getGroupOfFight(fights.get(fightIndex));
        }
        return null;
    }

    public TournamentGroup getGroupOfFight(Fight fight) {
        if (fight != null) {
            return levels.get(fight.level).getGroupOfFight(fight);
        }
        return null;
    }

    public ArrayList<Fight> getFightsOfLevel(ArrayList<Fight> fights, int level) {
        ArrayList<Fight> fightsOfLevel = new ArrayList<>();
        for (int i = 0; i < fights.size(); i++) {
            if (fights.get(i).level == level) {
                fightsOfLevel.add(fights.get(i));
            }
        }
        return fightsOfLevel;
    }

    public int getArenasOfLevel(ArrayList<Fight> fights, int level) {
        ArrayList<Fight> fightsOfLevel = getFightsOfLevel(fights, level);
        List<Integer> arenas = new ArrayList<>();
        for (int i = 0; i < fightsOfLevel.size(); i++) {
            boolean found = false;
            for (int j = 0; j < arenas.size(); j++) {
                if (arenas.get(j) == fightsOfLevel.get(i).asignedFightArea) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                arenas.add(fightsOfLevel.get(i).asignedFightArea);
            }
        }
        return arenas.size();
    }

    /**
     * ********************************************
     *
     * ARENAS MANIPULATION
     *
     *********************************************
     */
    /**
     * The default arena of the groups into level zero is distributed
     * proportionally.
     */
    public void updateArenas(int level) {
        if ((championship.fightingAreas > 1) && (levels.size() > level)) {
            levels.get(level).updateArenaOfGroups();
        }
    }

    public int returnNumberOfArenas() {
        return championship.fightingAreas;
    }

    /**
     * ********************************************
     *
     * RECONSTRUCTION
     *
     *********************************************
     */
    /**
     * Restore a designer with the data stored in a database.
     */
    public void refillDesigner(ArrayList<Fight> tmp_fights) {
        //if (!loadDesigner(FOLDER + File.separator + KendoTournamentGenerator.getInstance().getLastSelectedTournament() + ".dsg", tournament)) {
        levels = new ArrayList<>();

        //Fill levels with fights defined.
        int maxFightLevel = FightManager.getMaxLevelOfFights(tmp_fights);
        for (int i = 0; i <= maxFightLevel; i++) {
            refillLevel(tmp_fights, i);
        }

        //defautl Max winners. Not important this variable now.
        if (tmp_fights.size() > 0) {
            default_max_winners = tmp_fights.get(0).getMaxWinners();
        }

        //Fill Inner Levels
        for (int i = maxFightLevel; i < calculateNumberOfLevels(); i++) {
            updateInnerLevel(i);
        }

        unselectDesignedGroups();
        selectLastGroup();
        update();
    }

    private void refillLevel(ArrayList<Fight> tmp_fights, int level) {
        ArrayList<Fight> fights = getFightsOfLevel(tmp_fights, level);
        List<Team> teamsOfGroup = new ArrayList<>();

        for (int i = 0; i < fights.size(); i++) {
            //If one team exist in the group, then this fight is also of this group.
            if (isTeamIncludedInList(teamsOfGroup, fights.get(i).team1)) {
                if (!isTeamIncludedInList(teamsOfGroup, fights.get(i).team2)) {
                    teamsOfGroup.add(fights.get(i).team2);
                }
            } else if (isTeamIncludedInList(teamsOfGroup, fights.get(i).team2)) {
                if (!isTeamIncludedInList(teamsOfGroup, fights.get(i).team1)) {
                    teamsOfGroup.add(fights.get(i).team1);
                }
            } else {
                //If no team exist in this group, means that we find the fightManager of a new group.
                //Store the previous group.
                if (teamsOfGroup.size() > 0) {
                    TournamentGroup designedFight = new TournamentGroup(teamsOfGroup.size(), fights.get(i).getMaxWinners(), championship, level, fights.get(i - 1).asignedFightArea);
                    designedFight.addTeams(teamsOfGroup);
                    designedFight.update();
                    add(designedFight, false, false);
                }
                //Start generating the next group.
                teamsOfGroup = new ArrayList<>();
                teamsOfGroup.add(fights.get(i).team1);
                teamsOfGroup.add(fights.get(i).team2);
            }
        }
        //Insert the last group.
        try {
            if (!fights.isEmpty()) {
                TournamentGroup designedFight = new TournamentGroup(teamsOfGroup.size(), fights.get(fights.size() - 1).getMaxWinners(), championship, level, fights.get(fights.size() - 1).asignedFightArea);
                designedFight.addTeams(teamsOfGroup);
                designedFight.update();
                add(designedFight, false, false);
            }
        } catch (ArrayIndexOutOfBoundsException aiob) {
            KendoTournamentGenerator.getInstance().showErrorInformation(aiob);
        }
    }

    /**
     * Groups that only have one team.
     *
     * @param tmp_fights
     * @param level
     */
    private void completeGroupsWithOneTeam(ArrayList<Fight> tmp_fights, int level) {
        List<Team> winners = new ArrayList<>();
        if (level > 0 && (getFightsOfLevel(tmp_fights, level - 1).size() > 0) && (getFightsOfLevel(tmp_fights, level).size() > 0)) {
            //obtain all winners of previous level.
            List<TournamentGroup> groupsPrev = returnGroupsOfLevel(level - 1);
            for (int i = 0; i < groupsPrev.size(); i++) {
                if (groupsPrev.get(i).areFightsOverOrNull(tmp_fights)) {
                    winners.addAll(groupsPrev.get(i).getWinners());
                }
            }

            //Delete the winners inserted in a group of this level.
            List<TournamentGroup> groups = returnGroupsOfLevel(level);
            for (int i = 0; i < groups.size(); i++) {
                for (int j = 0; j < groups.get(i).teams.size(); j++) {
                    for (int k = 0; k < winners.size(); k++) {
                        if (winners.get(k).returnName().equals(groups.get(i).teams.get(j).returnName())) {
                            winners.remove(k);
                            k--;
                        }
                    }
                }
            }

            //Add the winner without group to the last one.
            TournamentGroup lastg = groups.get(groups.size() - 1);
            if (lastg != null) {
                for (int i = 0; i < winners.size(); i++) {
                    lastg.addTeam(winners.get(i));
                }
            }
        }
    }

    /**
     * *************************************************************
     *
     * SAVE AND LOAD
     *
     **************************************************************
     */
    /**
     * Inherit from SerialParent this class allow to save the Computer structure
     * in a file.
     *
     * @see SerialParent
     */
    class SerialDesignerStream extends SerialParent {

        public SerialDesignerStream(String file) {
            FILENAME = file;
        }
    }

    public boolean storeDesigner() {
        String path = FOLDER + File.separator + KendoTournamentGenerator.getInstance().getLastSelectedTournament() + ".dsg";
        try {
            new SerialDesignerStream(path).save(this);
        } catch (IOException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
            return false;
        }
        return true;
    }

    public boolean loadDesigner() {
        return loadDesigner(FOLDER + File.separator + KendoTournamentGenerator.getInstance().getLastSelectedTournament() + ".dsg");
    }

    private boolean loadDesigner(String path) {
        List l;
        try {
            l = new SerialDesignerStream(path).load();
            KendoTournamentGenerator.getInstance().designedGroups = (TournamentGroupManager) l.get(0);

            if (KendoTournamentGenerator.getInstance().designedGroups.load(championship)) {
                return true;
            }
        } catch (ClassNotFoundException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (IOException ex) {
            //File not found...  do nothing!
        }
        return false;
    }

    public boolean load(Tournament tournament) {
        championship = tournament;

        for (LevelGroups level : levels) {
            if (!level.loadLevel(tournament)) {
                return false;
            }
        }
        return true;
    }

    public void deleteUsedDesigner() {
        MyFile.deleteFile(FOLDER + File.separator + championship.name + ".dsg");
    }

    /**
     * *************************************************************
     *
     * MANUAL LINKING
     *
     **************************************************************
     */
    /**
     * Stores the arrows of the designer.
     */
    class Links implements Serializable {

        private List<Link> links = new ArrayList<>();

        Links() {
        }

        void add(TournamentGroup from, TournamentGroup to) {
            if (to.getLevel() == from.getLevel() + 1) {
                links.add(new Link(from, to));
            }
        }

        int size() {
            return links.size();
        }

        Link get(int index) {
            return links.get(index);
        }

        void remove(int index) {
            links.remove(index);
        }

        void showlinks() {
            System.out.println("---");
            for (int i = 0; i < links.size(); i++) {
                System.out.println(returnIndexOfGroup(links.get(i).source) + " (" + returnPositionOfGroupInItsLevel((returnIndexOfGroup(links.get(i).source))) + ")" + " -> " + returnIndexOfGroup(links.get(i).address) + " (" + returnPositionOfGroupInItsLevel((returnIndexOfGroup(links.get(i).address))) + ")");
            }
        }

        class Link implements Serializable {

            TournamentGroup source;
            TournamentGroup address;

            Link(TournamentGroup from, TournamentGroup to) {
                source = from;
                address = to;
            }
        }
    }

    void addLink(TournamentGroup from, TournamentGroup to) {
        if (from.getLevel() == to.getLevel() - 1) {
            if (numberOfSourcesOfLink(from) >= from.returnMaxNumberOfWinners()) {
                removefirstSourceLink(from);
            }
            if (numberOfAddressesOfLink(to) >= 2) {
                removefirstAddressLink(to);
            }
            links.add(from, to);


        }
    }

    int numberOfSourcesOfLink(TournamentGroup from) {
        int number = 0;

        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).source.equals(from)) {
                number++;
            }
        }
        return number;


    }

    int numberOfAddressesOfLink(TournamentGroup to) {
        int number = 0;
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).address.equals(to)) {
                number++;
            }
        }
        return number;


    }

    void removefirstSourceLink(TournamentGroup from) {
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).source.equals(from)) {
                links.remove(i);
                break;
            }
        }
    }

    void removefirstAddressLink(TournamentGroup to) {
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).address.equals(to)) {
                links.remove(i);
                break;
            }
        }
    }

    boolean allGroupsHaveManualLink() {
        try {
            List<TournamentGroup> groupslvl = returnGroupsOfLevel(0);
            for (int i = 0; i < groupslvl.size(); i++) {
                boolean found = false;

                for (int j = 0; j < links.size(); j++) {
                    if (links.get(j).source.equals(groupslvl.get(i))) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
            return true;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    void cleanLinksSelectedGroup() {
        try {
            for (int i = 0; i < links.size(); i++) {
                if (links.get(i).source.equals(getLastGroupSelected())) {
                    links.remove(i);
                    i--;
                }
            }
        } catch (NullPointerException npe) {
        }
    }

    TournamentGroup obtainManualDestination(TournamentGroup source, int winner) {
        int found = 0; //Winners in the manual linking are stored by order.


        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).source.equals(source)) {
                if (found == winner) {
                    return links.get(i).address;
                }
                found++;
            }
        }
        return null;

    }
}
