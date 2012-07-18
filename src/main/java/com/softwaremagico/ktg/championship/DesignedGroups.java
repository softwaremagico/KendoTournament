package com.softwaremagico.ktg.championship;
/*
 * #%L
 * KendoTournamentGenerator
 * %%
 * Copyright (C) 2008 - 2012 Softwaremagico
 * %%
 * This software is designed by Jorge Hortelano Otero.
 * Jorge Hortelano Otero <softwaremagico@gmail.com>
 * C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program; If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
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
public class DesignedGroups implements Serializable {

    private List<DesignedGroup> designedGroups = new ArrayList<>();
    private List<Integer> levels = new ArrayList<>();  //"Pointer" to the designedGroup to point fightManager of the next level.
    transient Tournament championship;
    public String mode = "tree";
    private Links links;
    private final String FOLDER = "designer";
    public int default_max_winners = 1;

    public DesignedGroups(Tournament tmp_championship) {
        try {
            mode = tmp_championship.mode;
            championship = tmp_championship;
            levels.add(0);
            links = new Links();
        } catch (NullPointerException npe) {
        }
    }

    public void update() {
        Log.finest("Current number of fights over before updating design groups: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
        for (int i = 0; i < designedGroups.size(); i++) {
            designedGroups.get(i).update();
        }
        Log.finest("Current number of fights over after updating design groups: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
    }

    public void color(boolean color) {
        for (int i = 0; i < designedGroups.size(); i++) {
            designedGroups.get(i).activateColor(color);
        }
    }

    public int sizeOfTournament(String championship) {
        int size = 0;
        for (int i = 0; i < designedGroups.size(); i++) {
            if (designedGroups.get(i).championship.name.equals(championship) && returnLevelOfGroup(i) == 0) {
                size++;
            }
        }
        return size;
    }

    public void enhance(boolean yes) {
        for (int i = 0; i < designedGroups.size(); i++) {
            designedGroups.get(i).enhance(yes);
        }
    }

    public void onlyShow() {
        for (int i = 0; i < designedGroups.size(); i++) {
            designedGroups.get(i).onlyShow();
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
     * What teams ae already used.
     *
     * @param championship
     * @return
     */
    private List<Team> returnUsedTeamsOfTournament(String championship) {
        List<Team> usedTeams = new ArrayList<>();
        List<DesignedGroup> ds = returnGroupsOfTournament(championship);
        for (int i = 0; i < ds.size(); i++) {
            usedTeams.addAll(ds.get(i).teams);
        }
        return usedTeams;
    }

    private List<Team> returnUsedTeams() {
        List<Team> usedTeams = new ArrayList<>();
        for (int i = 0; i < designedGroups.size(); i++) {
            usedTeams.addAll(designedGroups.get(i).teams);
        }
        return usedTeams;
    }

    public boolean containTeamInTournament(Team t, String Championship) {
        List<Team> ts = returnUsedTeamsOfTournament(Championship);
        return isTeamIncludedInList(ts, t);
    }

    int returnNumberOfTotalTeamsPassNextRound(Integer level) {
        int teams = 0;
        List<DesignedGroup> groups = returnGroupsOfLevel(level);
        for (int i = 0; i < groups.size(); i++) {
            teams += groups.get(i).returnMaxNumberOfWinners();
        }
        return teams;
    }

    public void setNumberOfTeamsPassNextRound(int value) {
        default_max_winners = value;
        for (int i = 0; i < designedGroups.size(); i++) {
            if (designedGroups.get(i).getLevel() == 0) {
                designedGroups.get(i).updateMaxNumberOfWinners(value);
            }
        }
    }

    int returnNumberOfmaxTeamsGroupPassNextRound(Integer level) {
        int teams = 0;
        List<DesignedGroup> groups = returnGroupsOfLevel(level);
        for (int i = 0; i < groups.size(); i++) {
            if (teams < groups.get(i).returnMaxNumberOfWinners()) {
                teams = groups.get(i).returnMaxNumberOfWinners();

            }
        }
        return teams;
    }

    int obtainGlobalPositionWinner(int level, int groupIndex, int winner) {
        int total = 0;
        List<DesignedGroup> groups = returnGroupsOfLevel(level);
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
    private void fillGroupWithWinnersPreviousLevel(int groupIndex, ArrayList<Fight> fights, boolean resolvDraw) {
        updateScoreForTeams(fights);
        DesignedGroup dg = designedGroups.get(groupIndex);
        if (returnLevelOfGroup(groupIndex) > 0) {
            int previousLevel = returnLevelOfGroup(groupIndex) - 1;
            List<DesignedGroup> groups = returnGroupsOfLevel(previousLevel);
            for (int i = 0; i < groups.size(); i++) {
                for (int k = 0; k < groups.get(i).returnMaxNumberOfWinners(); k++) {
                    int destination;
                    if (!mode.equals("manual") || previousLevel > 0) {
                        destination = (int) obtainPositonOfOneWinnerInTournament(obtainGlobalPositionWinner(previousLevel, i, k),
                                returnNumberOfTotalTeamsPassNextRound(previousLevel), previousLevel);
                    } else {
                        destination = returnPositionOfGroupInItsLevel(returnIndexOfGroup(obtainManualDestination(groups.get(i), k)));
                    }
                    //System.out.println("Group:" + i + " winner:" + k + " destination: " + destination);
                    //If the searched dg's destination point to the group to complete, then means it is a source. 
                    if (destination == returnPositionOfGroupInItsLevel(groupIndex)) {
                        DesignedGroup previousDg = groups.get(i);
                        dg.addTeam(previousDg.getTeamInOrderOfScore(k, fights, resolvDraw));
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
            DesignedGroup dg = designedGroups.get(designedGroups.size() - 1);
            return dg.getTeamInOrderOfScore(0, fights, true);
        } catch (ArrayIndexOutOfBoundsException aiob) {
            return null;
        }
    }

    private boolean isTeamIncludedInList(List<Team> list, Team team) {
        return list.contains(team);
    }

    public void deleteTeamsOfLevel(Integer level) {
        for (int i = level; i < levels.size(); i++) {
            List<DesignedGroup> groups = returnGroupsOfLevel(i);
            for (int j = groups.size() - 1; j >= 0; j--) {
                groups.get(j).cleanTeams();
            }
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
    public void add(DesignedGroup d, Integer level, boolean selected, boolean autocomplete) {
        //Intermediate level
        if (level < levels.size() - 1) {
            //Store it at the end or in the selected group.
            if (level == 0 && selected) {
                designedGroups.add(returnIndexLastSelected() + 1, d);
            } else {
                designedGroups.add(returnLastPositionOfLevel(level) + 1, d);
            }
            //Change all next level arrows.
            for (int i = level + 1; i < levels.size(); i++) {
                Integer newValue = levels.get(i);
                levels.set(i, newValue + 1);
            }
        } else {
            //Last level only needs to store it. All "pointers" all ok.
            designedGroups.add(d);
            //A new level, store the new level also.
            if (level >= levels.size()) {
                levels.add(designedGroups.size() - 1);
            }
        }
        if (autocomplete) {
            updateInnerLevel(level);
        }
    }

    public DesignedGroup get(int i) {
        if (i >= 0 && i < designedGroups.size()) {
            return designedGroups.get(i);
        }
        return null;
    }

    public void remove(int groupIndex, Integer level) {
        if (level < levels.size()) {
            if (groupIndex < designedGroups.size()) {

                for (int i = 0; i < levels.size(); i++) {
                    //If is a level with only one value and is the last one, remove this level.
                    if ((i < levels.size()) && levels.get(i) == groupIndex && groupIndex == designedGroups.size() - 1) {
                        levels.remove(i);
                    }
                    //If is a level with only one group, remove this level.
                    if ((i < levels.size() - 1) && levels.get(i) == groupIndex && levels.get(i) == (levels.get(i + 1) + 1)) {
                        levels.remove(i);
                        i--;
                    } else { //Correct the levels.
                        if ((i < levels.size()) && levels.get(i) > groupIndex) {
                            //Correct the level
                            Integer newValue = levels.get(i);
                            levels.set(i, newValue - 1);
                        }
                    }
                }
                try {
                    designedGroups.remove(groupIndex);
                } catch (ArrayIndexOutOfBoundsException aiob) {
                }
                //When we add or remove two groups in one level, we must add one in the next one.
                updateInnerLevel(level);
            }
        }
    }

    public int size() {
        return designedGroups.size();
    }

    public int returnIndexOfGroup(DesignedGroup d) {
        for (int i = 0; i < designedGroups.size(); i++) {
            if (designedGroups.get(i).equals(d)) {
                return i;
            }
        }
        return -1;
    }

    public List<DesignedGroup> returnGroupsOfLevel(Integer level) {
        List<DesignedGroup> groups = new ArrayList<>();

        if ((level < levels.size() - 1) && (level >= 0)) {
            for (int i = levels.get(level); i < levels.get(level + 1); i++) {
                try {
                    groups.add(designedGroups.get(i));
                } catch (ArrayIndexOutOfBoundsException aiob) {
                }
            }
        } else {
            if ((level < levels.size()) && (level >= 0)) {
                for (int i = levels.get(level); i < designedGroups.size(); i++) {
                    groups.add(designedGroups.get(i));
                }
            }
        }

        return groups;
    }

    public List<Integer> returnIndexOfGroupsOfLevel(Integer level) {
        List<Integer> groups = new ArrayList<>();
        List<DesignedGroup> groupsLevel = returnGroupsOfLevel(level);
        for (int i = 0; i < groupsLevel.size(); i++) {
            groups.add(returnIndexOfGroup(groupsLevel.get(i)));
        }
        return groups;
    }

    public List<Integer> returnIndexOfGroupsOfLevelOrMore(Integer level) {
        List<Integer> groups = new ArrayList<>();
        if ((level < levels.size()) && (level >= 0)) {
            for (int i = levels.get(level); i < designedGroups.size(); i++) {
                groups.add(i);
            }
        }
        return groups;
    }

    public boolean isSomeSelected() {
        return returnLastSelected() != null;
    }

    public DesignedGroup returnLastSelected() {
        for (int i = 0; i < designedGroups.size(); i++) {
            if (designedGroups.get(i).isSelected()) {
                return designedGroups.get(i);
            }
        }
        return null;
    }

    public DesignedGroup returnGroupOfFight(Fight f) {
        for (DesignedGroup dg : designedGroups) {
            if (dg.isFightOfGroup(f)) {
                return dg;
            }
        }
        return null;
    }

    public int returnIndexLastSelected() {
        for (int i = 0; i < designedGroups.size(); i++) {
            if (designedGroups.get(i).isSelected()) {
                return i;
            }
        }
        return -1;
    }

    public void selectGroup(int groupIndex) {
        if (groupIndex < designedGroups.size() && groupIndex >= 0) {
            designedGroups.get(groupIndex).setSelected(this);
        }
    }

    public void unselectDesignedGroups() {
        for (int i = 0; i < designedGroups.size(); i++) {
            designedGroups.get(i).setUnselected();
        }
    }

    private void selectLastGroup() {
        if (designedGroups.size() > 0) {
            designedGroups.get(designedGroups.size() - 1).setSelected(this);
        }
    }

    public List<DesignedGroup> returnGroupsOfTournament(String championship) {
        List<DesignedGroup> result = new ArrayList<>();
        for (int i = 0; i < designedGroups.size(); i++) {
            if (designedGroups.get(i).championship.name.equals(championship)) {
                result.add(designedGroups.get(i));
            }
        }
        return result;
    }

    /**
     * Return the position of a group relative on its level.
     *
     * @param groupIndex The index of the group in the list of groups.
     * @return
     */
    public int returnPositionOfGroupInItsLevel(Integer groupIndex) {
        int levelOfGroup = returnLevelOfGroup(groupIndex);
        return groupIndex - levels.get(levelOfGroup);
    }

    /**
     * Return the position of a group relative on the list of groups.
     *
     * @param group The position of the group in its level.
     * @return
     */
    public int returnPositionOfGroupInList(Integer group, int level) {
        return group + levels.get(level);
    }

    private boolean allGroupsInSameArena(List<DesignedGroup> groups) {
        for (int i = 0; i < groups.size() - 1; i++) {
            if (groups.get(i).arena != groups.get(i + 1).arena) {
                return false;
            }
        }
        return true;
    }

    public void deleteGroupsOfLevel(Integer level) {
        List<Integer> groups = returnIndexOfGroupsOfLevelOrMore(level);
        for (int i = groups.size() - 1; i >= 0; i--) {
            remove(groups.get(i), level);
        }
        updateInnerLevels();
    }

    public int returnLastGroupUsed() {
        int last = 0;
        for (int i = 0; i < designedGroups.size(); i++) {
            if (designedGroups.get(i).areFightsOver()) {
                last = i;
            }
        }
        return last;
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
    int returnLastPositionOfLevel(Integer level) {
        try {
            if (level < levels.size() - 1) {
                return levels.get(level + 1) - 1;
            } else {
                return levels.get(levels.size() - 1);
            }
        } catch (ArrayIndexOutOfBoundsException aiob) {
            return -1;
        }
    }

    public int returnNumberOfLevels() {
        return levels.size();
    }

    private int calculateNumberOfLevels() {
        return (int) (Math.log(returnActualSizeOfLevel(0)) / Math.log(2)) + default_max_winners;
    }

    public int returnActualSizeOfLevel(Integer level) {
        //Intermediate level.
        if (level < levels.size() - 1) {
            return levels.get(level + 1) - levels.get(level);
        }

        //Last level
        if (level == levels.size() - 1) {
            return designedGroups.size() - levels.get(level);
        }
        return 0;
    }

    public int returnCalculatedSizeOfLevel(int level) {
        switch (level) {
            case 0:
                return returnActualSizeOfLevel(0);
            case 1:
                return (returnNumberOfTotalTeamsPassNextRound(0)) / 2;
            default:
                return returnActualSizeOfLevel(level - 1) / 2;
        }
    }

    public void updateInnerLevels() {
        updateInnerLevels(0);
    }

    public void updateInnerLevels(int level) {
        for (int i = level; i < levels.size(); i++) {
            updateInnerLevel(i);
        }
    }

    public void updateInnerLevel(int level) {
        int arena = 0;
        if (designedGroups.size() > 0) {
            //Next Level has the half groups, distributed in X arenas.
            //int groupsPerArena = (returnCalculatedSizeOfLevel(level + 1)) / championship.fightingAreas;
            double sizeOfNextLevel = returnCalculatedSizeOfLevel(level + 1);
            while ((returnActualSizeOfLevel(level) > 1) && ((float) returnNumberOfTotalTeamsPassNextRound(level) / 2 > returnActualSizeOfLevel(level + 1))) {
                add(new DesignedGroup(2, 1, championship, level + 1, arena), level + 1, false, true);
            }

            //When we remove two groups in one level, we must remove one in the next one.
            if ((returnActualSizeOfLevel(level) == 1) || (Math.ceil((float) returnNumberOfTotalTeamsPassNextRound(level) / 2) < returnActualSizeOfLevel(level + 1))) {
                remove(returnLastPositionOfLevel(level + 1), level + 1);
            }
        }

        updateArenaOfLevels();
    }

    private void updateArenaOfLevels() {
        int levelsC = calculateNumberOfLevels();
        for (int level = 0; level < levelsC; level++) {
            int arena = 0;
            int inserted = 0;
            List<DesignedGroup> groupsOfLevel = returnGroupsOfLevel(level);
            int groupsPerLevel = (int) Math.floor(groupsOfLevel.size() / championship.fightingAreas);
            int remainingGroups = groupsOfLevel.size() - groupsPerLevel * championship.fightingAreas;
            for (int i = 0; i < groupsOfLevel.size(); i++) {
                groupsOfLevel.get(i).arena = arena;
                inserted++;
                if ((inserted > groupsPerLevel)
                        || (remainingGroups == 0 && inserted == groupsPerLevel)) {
                    inserted = 0;
                    arena++;
                }
            }
        }
    }

    void emptyInnerLevels() {
        if (levels.size() > 1) {
            for (int i = designedGroups.size() - 1; i >= levels.get(1); i--) {
                designedGroups.get(i).cleanTeams();
            }
        }
    }

    public int returnLevelOfFight(Fight f) {
        for (int i = 0; i < designedGroups.size(); i++) {
            if (designedGroups.get(i).isFightOfGroup(f)) {
                return designedGroups.get(i).getLevel();
            }
        }
        return -1;
    }

    public int returnLevelOfGroup(Integer groupIndex) {
        for (int i = levels.size() - 1; i >= 0; i--) {
            if (levels.get(i) <= groupIndex) {
                return i;
            }
        }
        return 0;
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
                    return returnNumberOfLevels();
                } //All fightManager finished. We assume that a level is completed.
                return fights.get(fights.size() - 1).level + 1;
            }
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    public boolean isLevelFinished(ArrayList<Fight> fights, int level) {
        List<DesignedGroup> designedGroupsOfLevel = returnGroupsOfLevel(level);

        for (int i = 0; i < designedGroupsOfLevel.size(); i++) {
            if (!designedGroupsOfLevel.get(i).areFightsOverOrNull(fights)) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<Fight> nextLevel(ArrayList<Fight> fights, int fightArea, Tournament championship) {
        int nextLevel = firstLevelNotFinished(fights);
        int arena;
        try {
            //Not finished the championship.
            if (nextLevel >= 0 && nextLevel < returnNumberOfLevels()) {
                Log.finer("Tournament not finished!");
                //Update fightManager to load the fightManager of other arenas and computers.
                if (championship.fightingAreas > 1) {
                    Log.finest("Retrieven data from other arenas.");
                    Log.finest("Current number of fights over before updating data: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
                    KendoTournamentGenerator.getInstance().fightManager.getFightsFromDatabase(championship.name);
                    Log.finest("Current number of fights over after updating data: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
                }

                //But the level is over and need more fights.
                if (KendoTournamentGenerator.getInstance().fightManager.areAllOver()) {
                    Log.finer("All fights are over.");
                    if (MessageManager.questionMessage("nextLevel", "Warning!")) {
                        Log.finest("Current number of fights over before updating winners: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
                        int start = levels.get(nextLevel);
                        int end;
                        if (nextLevel + 1 < levels.size()) {
                            end = levels.get(nextLevel + 1);
                        } else {
                            end = levels.get(levels.size() - 1) + 1;
                        }
                        for (int i = start; i < end; i++) {
                            fillGroupWithWinnersPreviousLevel(i, KendoTournamentGenerator.getInstance().fightManager.getFights(), true);
                        }
                        Log.finest("Current number of fights over after updating winners: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
                    }
                    //set the team members order in the new level.
                    //updateOrderTeamsOfLevel(nextLevel);
                } else {
                    //Only one arena is finished: show message for waiting all fightManager are over.
                    if ((arena = KendoTournamentGenerator.getInstance().fightManager.allArenasAreOver()) != -1) {
                        MessageManager.translatedMessage("waitingArena", "", KendoTournamentGenerator.getInstance().returnShiaijo(arena) + "", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                Log.finest("Current number of fights over at the end updating fights: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
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
                    MessageManager.translatedMessage("leagueFinished", "Finally!", winnername, JOptionPane.INFORMATION_MESSAGE);
                }
                update();
                return new ArrayList<>();
            }
        } catch (NullPointerException npe) {
        }
        update();
        Log.finest("Current number of fights over before generating next level fights: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
        ArrayList<Fight> newFights = generateLevelFights(nextLevel);
        Log.finest("Current number of fights over after generating next level fights: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
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
        List<DesignedGroup> groups = returnGroupsOfLevel(level);

        boolean answer = false;
        int arena;
        //User must distribute the groups of level 0 in the different fightManager areas. 
        if (championship.fightingAreas > 1 && allGroupsInSameArena(groups) && level == 0) {
            answer = MessageManager.questionMessage("noFightsDistributedInArenas", "Warning!");
        }

        for (int i = 0; i < groups.size(); i++) {
            if (answer) {
                if (!mode.equals("manual") || level > 0) {
                    arena = i / (int) Math.ceil((double) returnActualSizeOfLevel(level) / (double) championship.fightingAreas);
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
                if ((branchs / 2) % 2 != 0 && returnNumberOfTotalTeamsPassNextRound(level) > returnActualSizeOfLevel(level)) {
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
        for (int i = 0; i < designedGroups.size(); i++) {
            designedGroups.get(i).updateScoreForTeams(fights);
        }
    }

    private int obtainMaxLevel(ArrayList<Fight> fights) {
        int max = 0;
        for (int i = 0; i < fights.size(); i++) {
            if (fights.get(i).level > max) {
                max = fights.get(i).level;
            }
        }
        return max;
    }

    public int getGroupOfFight(ArrayList<Fight> fights, int fightIndex) {
        for (int i = designedGroups.size() - 1; i >= 0; i--) {
            boolean team1Exist = false;
            boolean team2Exist = false;

            if (fights.get(fightIndex).level == designedGroups.get(i).getLevel()) {
                for (int j = 0; j < designedGroups.get(i).teams.size(); j++) {
                    if (fights.get(fightIndex).team1.returnName().equals(designedGroups.get(i).teams.get(j).returnName())) {
                        team1Exist = true;
                    }
                    if (fights.get(fightIndex).team2.returnName().equals(designedGroups.get(i).teams.get(j).returnName())) {
                        team2Exist = true;
                    }
                }
                if (team1Exist && team2Exist) {
                    return i;
                }
            }
        }
        return -1;
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
    public void updateArenasInLevelZero() {
        if (championship.fightingAreas > 1) {
            List<DesignedGroup> grps = returnGroupsOfLevel(0);
            for (int j = 0; j < grps.size(); j++) {
                double groupsPerArena = Math.ceil((double) grps.size() / (double) championship.fightingAreas);
                grps.get(j).arena = (j) / (int) groupsPerArena;
                //System.out.println("Pos:" + j + ", Level:" + i + ", groupsInLevel:" + grps.size() + ", Areas:" + championship.fightingAreas + " Arena:" + grps.get(j).arena);
            }
            updateArenaInnerLevels();
        }
    }

    /**
     * The arena of a group in a level > 1 is the same arena of the group in the
     * same row.
     */
    public void updateArenaInnerLevels() {
        List<DesignedGroup> grpsLevelZero = returnGroupsOfLevel(0);
        if (levels.size() > 1) {
            for (int i = 1; i < levels.size(); i++) {
                List<DesignedGroup> grps = returnGroupsOfLevel(i);

                for (int j = 0; j < grps.size(); j++) {
                    grps.get(j).arena = grpsLevelZero.get(j * grpsLevelZero.size() / grps.size()).arena;
                }
            }
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
        designedGroups = new ArrayList<>();
        levels = new ArrayList<>();

        //Fill levels with fightManager zero.
        int maxFightLevel = FightManager.getLevelOfFights(tmp_fights);
        for (int i = 0; i <= maxFightLevel; i++) {
            refillLevel(tmp_fights, i);
        }

        //defautl Max winners. Not important this variable now.
        if (tmp_fights.size() > 0) {
            default_max_winners = tmp_fights.get(0).getMaxWinners();
        }

        //Fill Inner Levels
        for (int i = maxFightLevel; i < returnNumberOfLevels(); i++) {
            updateInnerLevel(i);
        }

        /*
         * for (int i = 1; i < returnNumberOfLevels(); i++) { if
         * (isLevelFinished(tmp_fights, i - 1)) { List<Integer> groupsOfLevel =
         * returnIndexOfGroupsOfLevel(i); for (int j = 0; j <
         * groupsOfLevel.size(); j++) {
         * fillGroupWithWinnersPreviousLevel(groupsOfLevel.get(j),
         * KendoTournamentGenerator.getInstance().fightManager.getFights(),
         * false); } } else { break; } }
         */
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
                    DesignedGroup designedFight = new DesignedGroup(teamsOfGroup.size(), fights.get(i).getMaxWinners(), championship, level, fights.get(i - 1).asignedFightArea);
                    designedFight.addTeams(teamsOfGroup);
                    designedFight.update();
                    add(designedFight, level, false, false);
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
                DesignedGroup designedFight = new DesignedGroup(teamsOfGroup.size(), fights.get(fights.size() - 1).getMaxWinners(), championship, level, fights.get(fights.size() - 1).asignedFightArea);
                designedFight.addTeams(teamsOfGroup);
                designedFight.update();
                add(designedFight, level, false, false);
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
            List<DesignedGroup> groupsPrev = returnGroupsOfLevel(level - 1);
            for (int i = 0; i < groupsPrev.size(); i++) {
                if (groupsPrev.get(i).areFightsOverOrNull(tmp_fights)) {
                    winners.addAll(groupsPrev.get(i).getWinners());
                }
            }

            //Delete the winners inserted in a group of this level.
            List<DesignedGroup> groups = returnGroupsOfLevel(level);
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
            DesignedGroup lastg = groups.get(groups.size() - 1);
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
            KendoTournamentGenerator.getInstance().designedGroups = (DesignedGroups) l.get(0);

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

    public boolean load(Tournament c) {
        championship = c;

        for (int i = 0; i < designedGroups.size(); i++) {
            if (!designedGroups.get(i).load(c)) {
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

        void add(DesignedGroup from, DesignedGroup to) {
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

            DesignedGroup source;
            DesignedGroup address;

            Link(DesignedGroup from, DesignedGroup to) {
                source = from;
                address = to;
            }
        }
    }

    void addLink(DesignedGroup from, DesignedGroup to) {
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

    int numberOfSourcesOfLink(DesignedGroup from) {
        int number = 0;

        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).source.equals(from)) {
                number++;
            }
        }
        return number;


    }

    int numberOfAddressesOfLink(DesignedGroup to) {
        int number = 0;
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).address.equals(to)) {
                number++;
            }
        }
        return number;


    }

    void removefirstSourceLink(DesignedGroup from) {
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).source.equals(from)) {
                links.remove(i);
                break;
            }
        }
    }

    void removefirstAddressLink(DesignedGroup to) {
        for (int i = 0; i < links.size(); i++) {
            if (links.get(i).address.equals(to)) {
                links.remove(i);
                break;
            }
        }
    }

    boolean allGroupsHaveManualLink() {
        try {
            List<DesignedGroup> groupslvl = returnGroupsOfLevel(0);
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
                if (links.get(i).source.equals(returnLastSelected())) {
                    links.remove(i);
                    i--;
                }
            }
        } catch (NullPointerException npe) {
        }
    }

    DesignedGroup obtainManualDestination(DesignedGroup source, int winner) {
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
