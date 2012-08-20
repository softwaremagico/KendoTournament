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

    private static final long serialVersionUID = 8486984938854712658L;
    private List<LeagueLevel> levels;
    transient Tournament tournament;
    private final String FOLDER = "designer";
    public int default_max_winners = 1;

    public TournamentGroupManager(Tournament championship) {
        try {
            //mode = tournament.mode;
            tournament = championship;
            // levels.add(0);
            levels = new ArrayList<>();
        } catch (NullPointerException npe) {
            Log.severe("Error when creating a Tournament:" + this.getClass());
        }
    }

    public List<LeagueLevel> getLevels() {
        return levels;
    }

    public void update() {
        Log.debug("Current number of fights over before updating design groups: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
        for (LeagueLevel level : levels) {
            level.updateGroups();
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
        for (LeagueLevel l : levels) {
            l.enhanceGroups(yes);
        }
    }

    public void onlyShow() {
        for (LeagueLevel l : levels) {
            l.onlyShow();
        }
    }

    public Tournament returnTournament() {
        return tournament;
    }

    public TournamentTypes getMode() {
        return tournament.mode;
    }

    public void setMode(TournamentTypes mode) {
        this.tournament.mode = mode;
    }

    /**
     * ********************************************
     *
     * TEAMS MANIPULATION
     *
     *********************************************
     */
    /**
     * Teams already inserted in the tournament.
     *
     * @return
     */
    private List<Team> getUsedTeams() {
        if (levels.size() > 0) {
            return levels.get(0).getUsedTeams();
        }
        return new ArrayList<>();
    }

    public boolean isTeamContainedInTournament(Team team) {
        return getUsedTeams().contains(team);
    }

    protected Integer getNumberOfTotalTeamsPassNextRound(Integer level) {
        if (level > 0 && level < levels.size()) {
            return levels.get(level).getNumberOfTotalTeamsPassNextRound();
        }
        return null;
    }

    public void setNumberOfTeamsPassNextRound(int value) {
        default_max_winners = value;
        List<TournamentGroup> groups = returnGroupsOfLevel(0);

        for (TournamentGroup group : groups) {
            group.updateMaxNumberOfWinners(value);
        }
    }

    protected Integer obtainGlobalPositionWinner(int level, TournamentGroup group, int winner) {
        if (level > 0 && level < levels.size()) {
            return levels.get(level).getGlobalPositionWinner(group, winner);
        }
        return null;
    }

    /**
     * Fill a group with the winners of the previous level.
     *
     * @param group group to complete relative to the level.
     */
    protected void fillGroupWithWinnersPreviousLevel(TournamentGroup group, ArrayList<Fight> fights, boolean resolvDraw) {
        updateScoreForTeams(fights);
        if (group.getLevel() > 0) {
            List<TournamentGroup> groups = returnGroupsOfLevel(group.getLevel() - 1);
            for (TournamentGroup previousGroup : groups) {
                for (int winners = 0; winners < previousGroup.getMaxNumberOfWinners(); winners++) {
                    TournamentGroup destination;
                    destination = levels.get(previousGroup.getLevel()).getGroupDestinationOfWinner(previousGroup, winners);
                    // If the searched dg's destination point to the group to complete, then means it is a source.
                    if (destination.equals(group)) {
                        group.addTeam(previousGroup.getTeamInOrderOfScore(winners, fights, resolvDraw));
                    }
                }
            }
        }
    }

    private Team winnerOfLastFight(ArrayList<Fight> fights) {
        // Last group.
        try {
            TournamentGroup dg = levels.get(levels.size() - 1).getLastGroupOfLevel();
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
     * @param group
     * @param level
     */
    public void add(TournamentGroup group, boolean selected) {
        // Intermediate level
        if (levels.isEmpty()) {
            levels.add(getNewLevelZero(tournament, null, null, this));
        }
        if (group.getLevel() == 0 && selected && getIndexLastSelected() != null) {
            levels.get(group.getLevel()).addGroup(group, getIndexLastSelected() + 1);
        } else {
            levels.get(group.getLevel()).addGroup(group);
        }
    }

    private void deleteGroups(int level) {
        if (level < levels.size() && level >= 0) {
            levels.get(level).removeGroups();
        }
    }

    public TournamentGroup get(int index) {
        List<TournamentGroup> tournamentGroups = new ArrayList<>();

        for (LeagueLevel level : levels) {
            tournamentGroups.addAll(level.getGroups());
        }

        if (index >= 0 && index < tournamentGroups.size()) {
            return tournamentGroups.get(index);
        }
        return null;
    }

    public void removeGroupOfLevelZero(TournamentGroup group) {
        if (levels.size() > 0) {
            levels.get(0).removeGroup(group);
        }
    }

    public int size() {
        int size = 0;
        for (LeagueLevel level : levels) {
            size += level.size();
        }
        return size;
    }

    public Integer returnIndexOfGroup(TournamentGroup group) {
        LeagueLevel level = levels.get(group.getLevel());
        return level.getIndexOfGroup(group);
    }

    public List<TournamentGroup> returnGroupsOfLevel(Integer level) {
        if (level >= 0 && level < levels.size()) {
            return levels.get(level).getGroups();
        }
        return null;
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

    public Integer getIndexLastSelected() {
        try {
            if (levels.size() > 0) {
                return levels.get(0).getIndexLastSelected();
            }
        } catch (NullPointerException npe) {
        }
        return null;
    }

    public void selectGroup(int groupIndex) {
        if (levels.size() > 0) {
            List<TournamentGroup> tournamentGroups = levels.get(0).getGroups();
            if (groupIndex < tournamentGroups.size() && groupIndex >= 0) {
                tournamentGroups.get(groupIndex).setSelected(this);
            }
        }
    }

    public void unselectDesignedGroups() {
        if (levels.size() > 0) {
            List<TournamentGroup> tournamentGroups = levels.get(0).getGroups();
            for (int i = 0; i < tournamentGroups.size(); i++) {
                tournamentGroups.get(i).setUnselected();
            }
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

    /**
     * A level is not used if there are not any team assigned.
     *
     * @return
     */
    public Integer getIndexLastLevelNotUsed() {
        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i).getUsedTeams().isEmpty()) {
                return i;
            }
        }
        //All levels used, return last level.
        return null;
    }

    public boolean allGroupsHaveManualLink() {
        if (!getMode().equals(TournamentTypes.MANUAL)) {
            return false;
        } else {
            return ((LeagueLevelManual) levels.get(0)).allGroupsHaveManualLink();
        }
    }

    public void cleanLinksSelectedGroup() {
        if (getMode().equals(TournamentTypes.MANUAL)) {
            ((LeagueLevelManual) levels.get(0)).removeLinksSelectedGroup();
        }
    }

    public void addLink(TournamentGroup source, TournamentGroup address) {
        if (getMode().equals(TournamentTypes.MANUAL)) {
            ((LeagueLevelManual) levels.get(0)).addLink(source, address);
        }
    }

    /**
     * ********************************************
     *
     * LEVEL MANIPULATION
     *
     *********************************************
     */
    /**
     * Create a new level.
     */
    private LeagueLevel getNewLevelZero(Tournament tournament, LeagueLevel nextLevel, LeagueLevel previousLevel, TournamentGroupManager groupManager) {
        switch (getMode()) {
            case LEAGUE_TREE:
                return new LeagueLevelTree(tournament, 0, nextLevel, previousLevel, groupManager);
            case CHAMPIONSHIP:
                return new LeagueLevelChampionship(tournament, 0, nextLevel, previousLevel, groupManager);
            case MANUAL:
                return new LeagueLevelManual(tournament, 0, nextLevel, previousLevel, groupManager);
            default:
                return new LeagueLevelSimple(tournament, 0, nextLevel, previousLevel, groupManager);
        }
    }

    public void createLevelZero() {
        // Create level zero.
        levels = new ArrayList<>();
        levels.add(getNewLevelZero(tournament, null, null, this));
    }

    /**
     * Convert level zero to tournament or tree league.
     *
     * @param oldLevels
     */
    public void convertFirstLevelsToCurrentChampionship(LeagueLevel oldLevelZero) {
        levels = new ArrayList<>();

        if (oldLevelZero != null) {
            for (TournamentGroup group : oldLevelZero.getGroups()) {
                group.updateMaxNumberOfWinners(default_max_winners);
                add(group, group.isSelected());
            }
        }
        update();
    }

    public Integer getSizeOfLevel(Integer level) {
        if (level >= 0 && level < levels.size()) {
            return levels.get(level).size();
        } else {
            return null;
        }
    }

    protected void emptyInnerLevels() {
        for (LeagueLevel level : levels) {
            level.deleteTeams();
        }
    }

    private void leagueFinished(ArrayList<Fight> fights) {
        Log.finer("League finished");
        // Finished! The winner is...
        Team winner = winnerOfLastFight(fights);
        String winnername = "";
        try {
            winnername = winner.returnName();
        } catch (NullPointerException npe) {
            // npe.printStackTrace();
            Log.severe("No winner obtained!");
        }
        // Show message when last fight is selected.
        if (KendoTournamentGenerator.getInstance().fightManager.areAllOver() && KendoTournamentGenerator.getInstance().fightManager.size() > 0) {
            MessageManager.winnerMessage("leagueFinished", "Finally!", "<html><b>" + winnername + "</b></html>", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private ArrayList<Fight> generateNextLevelFights(int nextLevel) {
        Log.finer("All fights are over.");

        if (MessageManager.questionMessage("nextLevel", "Warning!")) {
            Log.debug("Current number of fights over before updating winners: "
                    + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
            List<TournamentGroup> groups = returnGroupsOfLevel(nextLevel);
            for (TournamentGroup g : groups) {
                fillGroupWithWinnersPreviousLevel(g, KendoTournamentGenerator.getInstance().fightManager.getFights(), true);
            }
            Log.debug("Current number of fights over after updating winners: "
                    + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());

        }
        // set the team members order in the new level.
        // updateOrderTeamsOfLevel(nextLevel);

        Log.debug("Current number of fights over before generating next level fights: "
                + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
        ArrayList<Fight> newFights = generateLevelFights(nextLevel);
        Log.debug("Current number of fights over after generating next level fights: "
                + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
        return newFights;
    }

    public ArrayList<Fight> nextLevel(ArrayList<Fight> fights, int fightArea, Tournament tournament) {
        Integer nextLevel = getIndexLastLevelNotUsed();
        int arena;
        ArrayList<Fight> newFights = new ArrayList<>();
        // Not finished the tournament.
        if (nextLevel != null && nextLevel >= 0 && nextLevel < getLevels().size()) {
            Log.finer("Tournament not finished!");
            // Update fightManager to load the fightManager of other arenas and computers.
            if (tournament.fightingAreas > 1) {
                Log.finest("Retrieving data from other arenas.");
                Log.debug("Current number of fights over before updating data: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
                KendoTournamentGenerator.getInstance().fightManager.getFightsFromDatabase(tournament);
                Log.debug("Current number of fights over after updating data: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
            }

            // But the level is over and need more fights.
            if (KendoTournamentGenerator.getInstance().fightManager.areAllOver()) {
                newFights = generateNextLevelFights(nextLevel);
            } else {
                // Only one arena is finished: show message for waiting all fightManager are over.
                if ((arena = KendoTournamentGenerator.getInstance().fightManager.allArenasAreOver()) != -1) {
                    MessageManager.translatedMessage("waitingArena", "", KendoTournamentGenerator.getInstance().returnShiaijo(arena) + "",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else {
            leagueFinished(fights);
        }
        //update();
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
        // User must distribute the groups of level 0 in the different fightManager areas.
        if (tournament.fightingAreas > 1 && allGroupsInSameArena(groups) && level == 0) {
            answer = MessageManager.questionMessage("noFightsDistributedInArenas", "Warning!");
        }

        for (int i = 0; i < groups.size(); i++) {
            if (answer) {
                if (!getMode().equals(TournamentTypes.MANUAL) || level > 0) {
                    arena = i / (int) Math.ceil((double) getSizeOfLevel(level) / (double) tournament.fightingAreas);
                } else {
                    // grouped by the destination group of the next level.
                    arena = (returnPositionOfGroupInItsLevel(groups.get(i))) % tournament.fightingAreas;
                }
                groups.get(i).arena = arena;
                fights.addAll(groups.get(i).generateGroupFights(level, arena));
            } else {
                fights.addAll(groups.get(i).generateGroupFights(level));
            }
        }
        return fights;
    }

    public void updateScoreForTeams(ArrayList<Fight> fights) {
        for (LeagueLevel level : levels) {
            level.updateScoreOfTeams(fights);
        }
    }

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
        if ((tournament.fightingAreas > 1) && (levels.size() > level)) {
            levels.get(level).updateArenaOfGroups();
        }
    }

    public int returnNumberOfArenas() {
        return tournament.fightingAreas;
    }

    public int getArenasOfLevel(ArrayList<Fight> fights, int level) {
        return levels.get(level).getArenasUsed();
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
    public void refillDesigner(ArrayList<Fight> fights) {
        // if (!loadDesigner(FOLDER + File.separator + KendoTournamentGenerator.getInstance().getLastSelectedTournament() + ".dsg", tournament)) {

        if (fights.size() > 0) {
            default_max_winners = fights.get(0).getMaxWinners();
        }

        if (fights.size() > 0) {
            tournament = fights.get(0).tournament;
            createLevelZero();

            // Fill levels with fights defined.
            int maxFightLevel = FightManager.getMaxLevelOfFights(fights);
            for (int i = 0; i <= maxFightLevel; i++) {
                /*
                 * if (i >= levels.size()) { //LevelZero has been added
                 * previously levels.add(createNextLevel(i)); }
                 */
                refillLevel(fights, i);
            }

            unselectDesignedGroups();
            selectLastGroup();
            update();
        }
    }

    private void refillLevel(ArrayList<Fight> fights, int level) {
        ArrayList<Fight> fightsOfLevel = getFightsOfLevel(fights, level);
        List<Team> teamsOfGroup = new ArrayList<>();
        deleteGroups(level);

        for (int i = 0; i < fightsOfLevel.size(); i++) {
            // If one team exist in the group, then this fight is also of this group.
            if (isTeamIncludedInList(teamsOfGroup, fightsOfLevel.get(i).team1)) {
                if (!isTeamIncludedInList(teamsOfGroup, fightsOfLevel.get(i).team2)) {
                    teamsOfGroup.add(fightsOfLevel.get(i).team2);
                }
            } else if (isTeamIncludedInList(teamsOfGroup, fightsOfLevel.get(i).team2)) {
                if (!isTeamIncludedInList(teamsOfGroup, fightsOfLevel.get(i).team1)) {
                    teamsOfGroup.add(fightsOfLevel.get(i).team1);
                }
            } else {
                // If no team exist in this group, means that we find the fightManager of a new group.
                // Store the previous group.
                if (teamsOfGroup.size() > 0) {
                    TournamentGroup designedFight = new TournamentGroup(fightsOfLevel.get(i).getMaxWinners(), tournament, level,
                            fightsOfLevel.get(i - 1).asignedFightArea);
                    designedFight.addTeams(teamsOfGroup);
                    add(designedFight, false);
                    designedFight.update();
                }
                // Start generating the next group.
                teamsOfGroup = new ArrayList<>();
                teamsOfGroup.add(fightsOfLevel.get(i).team1);
                teamsOfGroup.add(fightsOfLevel.get(i).team2);
            }
        }
        // Insert the last group.
        try {
            if (!fightsOfLevel.isEmpty()) {
                TournamentGroup designedFight = new TournamentGroup(fightsOfLevel.get(fightsOfLevel.size() - 1).getMaxWinners(),
                        tournament, level, fightsOfLevel.get(fightsOfLevel.size() - 1).asignedFightArea);
                designedFight.addTeams(teamsOfGroup);
                add(designedFight, false);
                designedFight.update();
            }
        } catch (ArrayIndexOutOfBoundsException aiob) {
            KendoTournamentGenerator.getInstance().showErrorInformation(aiob);
        }
    }
}
