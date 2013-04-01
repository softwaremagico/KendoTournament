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
import com.softwaremagico.ktg.core.Duel;
import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.KendoLog;
import com.softwaremagico.ktg.core.KendoTournamentGenerator;
import com.softwaremagico.ktg.core.MessageManager;
import com.softwaremagico.ktg.core.Ranking;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.core.TournamentType;
import com.softwaremagico.ktg.core.Undraw;
import com.softwaremagico.ktg.database.FightPool;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class TournamentGroupManager {

    private static final long serialVersionUID = 8486984938854712658L;
    private List<LeagueLevel> levels;
    private Tournament tournament;
    public int default_max_winners = 1;

    public TournamentGroupManager(Tournament tournament) {
        try {
            //mode = tournament.mode;
            this.tournament = tournament;
            // levels.add(0);
            levels = new ArrayList<>();
        } catch (NullPointerException npe) {
            KendoLog.severe(this.getClass().getName(), "Error when creating a Tournament:" + this.getClass());
        }
    }

    public List<LeagueLevel> getLevels() {
        return levels;
    }

    public int sizeOfTournamentLevelZero(String championship) {
        if (levels.size() > 0) {
            return levels.size();
        }
        return 0;
    }

    public Tournament returnTournament() {
        return tournament;
    }

    public TournamentType getMode() {
        return tournament.getMode();
    }

    public void setMode(TournamentType mode) {
        this.tournament.setMode(mode);
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

    public Integer getDefaultNumberOfTeamsPassNextRound() {
        return default_max_winners;
    }

    public void setNumberOfTeamsPassNextRound(int value) {
        default_max_winners = value;
        List<TournamentGroup> groups = returnGroupsOfLevel(0);

        if (groups != null) {
            for (TournamentGroup group : groups) {
                group.updateMaxNumberOfWinners(value);
            }
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
    protected void fillGroupWithWinnersPreviousLevel(TournamentGroup group, List<Fight> fights, boolean resolvDraw) {
        if (group.getLevel() > 0) {
            List<TournamentGroup> groups = returnGroupsOfLevel(group.getLevel() - 1);
            for (TournamentGroup previousGroup : groups) {
                List<Team> winnersRanking = previousGroup.getWinners();
                for (int winner = 0; winner < previousGroup.getMaxNumberOfWinners(); winner++) {
                    TournamentGroup destination;
                    destination = levels.get(previousGroup.getLevel()).getGroupDestinationOfWinner(previousGroup, winner);
                    // If the searched dg's destination point to the group to complete, then means it is a source.
                    if (destination.equals(group)) {
                        //group.addTeam(previousGroup.getTeamInOrderOfScore(winners, fights, resolvDraw));
                        if (winner < winnersRanking.size()) {
                            group.addTeam(winnersRanking.get(winner));
                        }
                    }
                }
            }
        }
    }

    private Team winnerOfLastFight(List<Fight> fights) {
        // Last group.
        try {
            TournamentGroup dg = levels.get(levels.size() - 1).getLastGroupOfLevel();
            return Ranking.getTeam(fights, 0);
        } catch (ArrayIndexOutOfBoundsException aiob) {
            return null;
        }
    }

    private boolean isTeamIncludedInList(List<Team> list, Team team) {
        return list.contains(team);
    }

    public void deleteTeamsOfLevel(Integer level) {
        if (level >= 0) {
            for (int i = level; i < levels.size(); i++) {
                levels.get(i).deleteTeams();
            }
        }
    }

    public void showTeams() {
        if (levels.size() > 0) {
            levels.get(0).showTeams();
        } else {
            KendoLog.debug(this.getClass().getName(), "Levels empty!");
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
     * @param index
     */
    public void add(TournamentGroup group, Integer index) {
        // Intermediate level
        if (levels.isEmpty()) {
            levels.add(getNewLevelZero(tournament, null, null, this));
        }
        if (group.getLevel() == 0 && index != null) {
            levels.get(group.getLevel()).addGroup(group, index);
        } else {
            levels.get(group.getLevel()).addGroup(group);
        }
    }

    private void deleteGroups(int level) {
        if (level < levels.size() && level >= 0) {
            levels.get(level).removeGroups();
        }
    }

    public TournamentGroup getGroup(int index) {
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

    public Integer getIndexOfGroup(TournamentGroup group) {
        LeagueLevel level = levels.get(group.getLevel());
        return level.getIndexOfGroup(group);
    }

    public List<TournamentGroup> returnGroupsOfLevel(Integer level) {
        KendoLog.entering(this.getClass().getName(), "returnGroupsOfLevel");
        if (level >= 0 && level < levels.size()) {
            return levels.get(level).getGroups();
        }
        KendoLog.exiting(this.getClass().getName(), "returnGroupsOfLevel");
        return null;
    }

    /*protected void selectLastGroup() {
        List<TournamentGroup> groups = returnGroupsOfLevel(0);
        if (groups.size() > 0) {
            groups.get(groups.size() - 1).setSelected(this);
        }
    }*/

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
            if (groups.get(i).getFightArea() != groups.get(i + 1).getFightArea()) {
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
        KendoLog.entering(this.getClass().getName(), "getIndexLastLevelNotUsed");
        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i).getUsedTeams().isEmpty()) {
                KendoLog.exiting(this.getClass().getName(), "getIndexLastLevelNotUsed");
                return i;
            }
        }
        //All levels used, return last level.
        KendoLog.exiting(this.getClass().getName(), "getIndexLastLevelNotUsed");
        return null;
    }

    public boolean allGroupsHaveManualLink() {
        if (!getMode().equals(TournamentType.MANUAL)) {
            return false;
        } else {
            return ((LeagueLevelManual) levels.get(0)).allGroupsHaveManualLink();
        }
    }

    public void addLink(TournamentGroup source, TournamentGroup address) {
        if (getMode().equals(TournamentType.MANUAL)) {
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

    private void leagueFinished(List<Fight> fights) {
        KendoLog.finer(this.getClass().getName(), "League finished");
        // Finished! The winner is...
        Team winner = winnerOfLastFight(fights);
        String winnername = "";
        try {
            winnername = winner.getName();
        } catch (NullPointerException npe) {
            // npe.printStackTrace();
            KendoLog.severe(this.getClass().getName(), "No winner obtained!");
        }
        // Show message when last fight is selected.
        if (FightPool.getInstance().areAllOver(tournament) && FightPool.getInstance().get(tournament).size() > 0) {
            MessageManager.winnerMessage(this.getClass().getName(), "leagueFinished", "Finally!", "<html><b>" + winnername + "</b></html>", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private List<Fight> generateNextLevelFights(int nextLevel) {
        KendoLog.entering(this.getClass().getName(), "generateNextLevelFights");
        KendoLog.finer(this.getClass().getName(), "All fights are over.");

        if (MessageManager.questionMessage("nextLevel", "Warning!")) {
            List<TournamentGroup> groups = returnGroupsOfLevel(nextLevel);
            for (TournamentGroup g : groups) {
                fillGroupWithWinnersPreviousLevel(g, FightPool.getInstance().get(tournament), true);
            }
        }
        // set the team members order in the new level.
        // updateOrderTeamsOfLevel(nextLevel);

        ArrayList<Fight> newFights = generateLevelFights(nextLevel);
        KendoLog.exiting(this.getClass().getName(), "generateNextLevelFights");
        return newFights;
    }

    public List<Fight> nextLevel(List<Fight> fights, Integer fightArea, Tournament tournament) {
        KendoLog.entering(this.getClass().getName(), "nextLevel");
        Integer nextLevel = getIndexLastLevelNotUsed();
        int arena;
        List<Fight> newFights = new ArrayList<>();
        // Not finished the tournament.
        if (nextLevel != null && nextLevel >= 0 && nextLevel < getLevels().size()) {
            KendoLog.finer(this.getClass().getName(), "Tournament not finished!");
            // Update fightManager to load the fightManager of other arenas and computers.
            if (tournament.getFightingAreas() > 1) {
                KendoLog.finest(this.getClass().getName(), "Retrieving data from other arenas.");
                FightPool.getInstance().reset();
            }

            // But the level is over and need more fights.
            if (FightPool.getInstance().areAllOver(tournament)) {
                newFights = generateNextLevelFights(nextLevel);
            } else {
                // Only one arena is finished: show message for waiting all fightManager are over.
                if (FightPool.getInstance().areAllOver(tournament, fightArea)) {
                    MessageManager.informationMessage(this.getClass().getName(), "waitingArena", "", KendoTournamentGenerator.getInstance().returnShiaijo(fightArea) + "");
                }
            }
        } else {
            leagueFinished(fights);
        }
        //update();
        KendoLog.exiting(this.getClass().getName(), "nextLevel");
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
        int fightArea;
        // User must distribute the groups of level 0 in the different fightManager areas.
        if (tournament.getFightingAreas() > 1 && allGroupsInSameArena(groups) && level == 0) {
            answer = MessageManager.questionMessage("noFightsDistributedInArenas", "Warning!");
        }

        for (int i = 0; i < groups.size(); i++) {
            if (answer) {
                if (!getMode().equals(TournamentType.MANUAL) || level > 0) {
                    fightArea = i / (int) Math.ceil((double) getSizeOfLevel(level) / (double) tournament.getFightingAreas());
                } else {
                    // grouped by the destination group of the next level.
                    fightArea = (returnPositionOfGroupInItsLevel(groups.get(i))) % tournament.getFightingAreas();
                }
                groups.get(i).setFightArea(fightArea);
                fights.addAll(groups.get(i).generateGroupFights(level, fightArea));
            } else {
                fights.addAll(groups.get(i).generateGroupFights(level, 0));
            }
        }
        return fights;
    }

    public TournamentGroup getGroupOfFight(List<Fight> fights, int fightIndex) {
        if (fightIndex >= 0 && fightIndex < fights.size()) {
            return getGroupOfFight(fights.get(fightIndex));
        }
        return null;
    }

    public TournamentGroup getGroupOfFight(Fight fight) {
        if (fight != null) {
            return levels.get(fight.getLevel()).getGroupOfFight(fight);
        }
        return null;
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
        if ((tournament.getFightingAreas() > 1) && (levels.size() > level)) {
            levels.get(level).updateArenaOfGroups();
        }
    }

    public int returnNumberOfArenas() {
        return tournament.getFightingAreas();
    }

    public int getArenasOfLevel(List<Fight> fights, int level) {
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
     * Restore a designer with the data obtained from a database.
     */
    public void refillDesigner() {
        List<Fight> fights = FightPool.getInstance().get(tournament);
        if (fights.size() > 0) {
            default_max_winners = fights.get(0).getMaxWinners();
        }

        if (fights.size() > 0) {
            createLevelZero();

            // Fill levels with fights defined.
            int maxFightLevel = FightPool.getInstance().getMaxLevel(tournament);
            for (int i = 0; i <= maxFightLevel; i++) {
                refillLevel(i);
            }

            /*unselectDesignedGroups();
            selectLastGroup();
            update();*/
        }
    }

    private void refillLevel(int level) {
        List<Fight> fightsOfLevel = FightPool.getInstance().getFromLevel(tournament, level);
        List<Team> teamsOfGroup = new ArrayList<>();
        deleteGroups(level);

        for (int i = 0; i < fightsOfLevel.size(); i++) {
            // If one team exist in the group, then this fight is also of this group.
            if (isTeamIncludedInList(teamsOfGroup, fightsOfLevel.get(i).getTeam1())) {
                if (!isTeamIncludedInList(teamsOfGroup, fightsOfLevel.get(i).getTeam2())) {
                    teamsOfGroup.add(fightsOfLevel.get(i).getTeam2());
                }
            } else if (isTeamIncludedInList(teamsOfGroup, fightsOfLevel.get(i).getTeam2())) {
                if (!isTeamIncludedInList(teamsOfGroup, fightsOfLevel.get(i).getTeam1())) {
                    teamsOfGroup.add(fightsOfLevel.get(i).getTeam1());
                }
            } else {
                // If no team exist in this group, means that we find the fightManager of a new group.
                // Store the previous group.
                if (teamsOfGroup.size() > 0) {
                    TournamentGroup designedFight = new TournamentGroup(fightsOfLevel.get(i).getMaxWinners(), tournament, level,
                            fightsOfLevel.get(i - 1).getAsignedFightArea());
                    designedFight.addTeams(teamsOfGroup);
                    add(designedFight, null);
                    //designedFight.update();
                }
                // Start generating the next group.
                teamsOfGroup = new ArrayList<>();
                teamsOfGroup.add(fightsOfLevel.get(i).getTeam1());
                teamsOfGroup.add(fightsOfLevel.get(i).getTeam2());
            }
        }
        // Insert the last group.
        try {
            if (!fightsOfLevel.isEmpty()) {
                TournamentGroup designedFight = new TournamentGroup(fightsOfLevel.get(fightsOfLevel.size() - 1).getMaxWinners(),
                        tournament, level, fightsOfLevel.get(fightsOfLevel.size() - 1).getAsignedFightArea());
                designedFight.addTeams(teamsOfGroup);
                add(designedFight, null);
                //designedFight.update();
            }
        } catch (ArrayIndexOutOfBoundsException aiob) {
            KendoTournamentGenerator.showErrorInformation(this.getClass().getName(), aiob);
        }
    }

    /**
     * **********************************************
     *
     * LISTENERS
     *
     ***********************************************
     */
    /**
     *
     */
    public List<String> exportToCsv() {
        List<String> csv = new ArrayList<>();
        for (LeagueLevel level : levels) {
            for (TournamentGroup group : level.getGroups()) {
                //csv.addAll(group.exportToCsv());
            }
        }
        return csv;
    }

    public boolean importFromCsv(List<String> csv) {
        int duelsCount = 0;
        Fight fight = null;
        int fightsInFile = 0;
        int fightsImported = 0;
        for (String csvLine : csv) {
            String[] fields = csvLine.split(";");
            if (csvLine.startsWith(Fight.getTag())) {
                fight = null;
                fightsInFile++;
                duelsCount = 0;
                //Obtain fight.
                int fightNumber = Integer.parseInt(fields[1]);

                if (fightNumber < FightPool.getInstance().get(tournament).size() && fightNumber >= 0) {
                    //Fight not finished and correct.
                    Fight readedFight = levels.get(Integer.parseInt(fields[3]))
                            .getGroups().get(Integer.parseInt(fields[2]))
                            .getFights().get(Integer.parseInt(fields[1]));
                    if (readedFight.getTeam1().getName().equals(fields[4]) && readedFight.getTeam2().getName().equals(fields[5])) {
                        if (!readedFight.isOver()) {
                            fight = readedFight;
                            fightsImported++;
                            fight.setOver(true);
                            fight.setOverStored(false);
                        }
                        //Add the fight.
                        FightPool.getInstance().add(tournament, fight);
                    } else {
                        MessageManager.errorMessage(this.getClass().getName(), "csvNotImported", "Error");
                        return false;
                    }
                }
            } else if (csvLine.startsWith(Duel.getCsvTag())) {
                if (fight != null) {
                    fight.getDuels().get(duelsCount).importFromCsv(csvLine);
                    duelsCount++;
                }
            } else if (csvLine.startsWith(Undraw.getCsvTag())) {
               // UndrawPool.getInstance().add(tournament, new Undraw(tournament, getGroupOfFight(fight), TeamPool.getInstance().get(tournament, fields[1]), Integer.parseInt(fields[2]), Integer.parseInt(fields[3])));
            }
        }

        if (fightsImported > 0) {
            MessageManager.informationMessage(this.getClass().getName(), "csvImported", "CSV", " (" + fightsImported + "/" + fightsInFile + ")");
            return true;
        } else {
            MessageManager.errorMessage(this.getClass().getName(), "csvNotImported", "Error");
            return false;
        }
    }
}
