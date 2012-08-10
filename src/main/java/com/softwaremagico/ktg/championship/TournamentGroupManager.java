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
import com.softwaremagico.ktg.TournamentTypes;
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
    private List<LevelGroups> levels;
    transient Tournament tournament;
    private final String FOLDER = "designer";
    public int default_max_winners = 1;

    public TournamentGroupManager(Tournament championship) {
        try {
            //mode = championship.mode;
            tournament = championship;
            // levels.add(0);
            levels = new ArrayList<>();
        } catch (NullPointerException npe) {
            Log.severe("Error when creating a Tournament:" + this.getClass());
        }
    }

    public void createLevelZero() {
        // Create level zero.
        levels = new ArrayList<>();
        levels.add(getNewLevel(tournament, 0, null, null, this));
    }

    public List<LevelGroups> getLevels() {
        return levels;
    }

    public void update() {
        Log.debug("Current number of fights over before updating design groups: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
        for (LevelGroups level : levels) {
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

    public boolean isTeamContainedInTournament(Team team, String Championship) {
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
        // TournamentGroup dg = tournamentGroups.get(groupIndex);
        if (group.getLevel() > 0) {
            List<TournamentGroup> groups = returnGroupsOfLevel(group.getLevel() - 1);
            for (TournamentGroup previousGroup : groups) {
                for (int winners = 0; winners < previousGroup.getMaxNumberOfWinners(); winners++) {
                    TournamentGroup destination;
                    destination = levels.get(group.getLevel()).getGroupSourceOfWinner(group, winners);
                    // System.out.println("Group:" + i + " winner:" + k + " destination: " + destination);
                    // If the searched dg's destination point to the group to complete, then means it is a source.
                    if (destination.equals(group)) {
                        group.addTeam(previousGroup.getTeamInOrderOfScore(winners, fights, resolvDraw));
                        // System.out.println(previousDg.getTeamInOrderOfScore(k, fightManager, true).returnName());
                    }
                }
                // System.out.println("----------");
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
            levels.add(getNewLevel(tournament, 0, null, null, this));
        }
        if (group.getLevel() == 0 && selected && getIndexLastSelected() != null) {
            levels.get(group.getLevel()).addGroup(group, getIndexLastSelected() + 1);
        } else {
            levels.get(group.getLevel()).addGroup(group);
        }
    }

    public TournamentGroup get(int index) {
        List<TournamentGroup> tournamentGroups = new ArrayList<>();

        for (LevelGroups level : levels) {
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
        for (LevelGroups level : levels) {
            size += level.size();
        }
        return size;
    }

    public Integer returnIndexOfGroup(TournamentGroup group) {
        LevelGroups level = levels.get(group.getLevel());
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
        if (levels.size() > 0) {
            return levels.get(0).getIndexLastSelected();
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
    public Integer getIndexLastLevelUsed() {
        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i).getUsedTeams().isEmpty()) {
                return i;
            }
        }
        return null;
    }

    public boolean allGroupsHaveManualLink() {
        if (!getMode().equals(TournamentTypes.MANUAL)) {
            return false;
        } else {
            return ((LevelGroupsManual)levels.get(0)).allGroupsHaveManualLink();
        }
    }
    
    public void cleanLinksSelectedGroup(){
        if (getMode().equals(TournamentTypes.MANUAL)) {
            ((LevelGroupsManual)levels.get(0)).cleanLinksSelectedGroup();
        }
    }
    
    public void addLink(TournamentGroup source, TournamentGroup address){
         if (getMode().equals(TournamentTypes.MANUAL)) {
             ((LevelGroupsManual)levels.get(0)).addLink(source, address);
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
    private LevelGroups getNewLevel(Tournament tournament, int level, LevelGroups nextLevel, LevelGroups previousLevel, TournamentGroupManager groupManager) {
        switch (getMode()) {
            case LEAGUE_TREE:
                return new LevelGroupsTreeChampionship(tournament, level, nextLevel, previousLevel, groupManager);
            case CHAMPIONSHIP:
                return new LevelGroupsChampionship(tournament, level, nextLevel, previousLevel, groupManager);
            case MANUAL:
                return new LevelGroupsManual(tournament, level, nextLevel, previousLevel, groupManager);
        }
        return null;
    }

    /**
     * Create a new level depending of the number of level and the championship
     * mode.
     *
     * @param levelToCreate
     * @return
     */
    private LevelGroups createNextLevel(int levelToCreate) {
        if (levelToCreate == 0) {
            return getNewLevel(tournament, levelToCreate, null, null, this);
        } else {
            return getNewLevel(tournament, levelToCreate, null, levels.get(levels.size() - 1), this);
        }
    }

    /**
     * Convert level zero to championship or tree league.
     *
     * @param oldLevels
     */
    public void convertFirstLevelsToCurrentChampionship(LevelGroups oldLevelZero) {
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
        for (LevelGroups level : levels) {
            level.deleteTeams();
        }
    }

    public ArrayList<Fight> nextLevel(ArrayList<Fight> fights, int fightArea, Tournament championship) {
        int nextLevel = getIndexLastLevelUsed();
        int arena;
        try {
            // Not finished the tournament.
            if (nextLevel >= 0 && nextLevel < getLevels().size()) {
                Log.finer("Tournament not finished!");
                // Update fightManager to load the fightManager of other arenas and computers.
                if (championship.fightingAreas > 1) {
                    Log.finest("Retrieving data from other arenas.");
                    Log.debug("Current number of fights over before updating data: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
                    KendoTournamentGenerator.getInstance().fightManager.getFightsFromDatabase(championship.name);
                    Log.debug("Current number of fights over after updating data: " + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
                }

                // But the level is over and need more fights.
                if (KendoTournamentGenerator.getInstance().fightManager.areAllOver()) {
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
                } else {
                    // Only one arena is finished: show message for waiting all fightManager are over.
                    if ((arena = KendoTournamentGenerator.getInstance().fightManager.allArenasAreOver()) != -1) {
                        MessageManager.translatedMessage("waitingArena", "", KendoTournamentGenerator.getInstance().returnShiaijo(arena) + "",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                Log.debug("Current number of fights over at the end updating fights: "
                        + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
            } else {
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
                    MessageManager.winnerMessage("leagueFinished", "Finally!", winnername, JOptionPane.INFORMATION_MESSAGE);
                }
                update();
                return new ArrayList<>();
            }
        } catch (NullPointerException npe) {
        }
        update();
        Log.debug("Current number of fights over before generating next level fights: "
                + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
        ArrayList<Fight> newFights = generateLevelFights(nextLevel);
        Log.debug("Current number of fights over after generating next level fights: "
                + KendoTournamentGenerator.getInstance().fightManager.numberOfFightsOver());
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
        for (LevelGroups level : levels) {
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
            tournament = fights.get(0).competition;
            createLevelZero();

            // Fill levels with fights defined.
            int maxFightLevel = FightManager.getMaxLevelOfFights(fights);
            for (int i = 0; i <= maxFightLevel; i++) {
                if (i >= levels.size()) {   //LevelZero has been added previously
                    levels.add(createNextLevel(i));
                }
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
            KendoTournamentGenerator.getInstance().tournamentManager = (TournamentGroupManager) l.get(0);

            if (KendoTournamentGenerator.getInstance().tournamentManager.load(tournament)) {
                return true;
            }
        } catch (ClassNotFoundException ex) {
            KendoTournamentGenerator.getInstance().showErrorInformation(ex);
        } catch (IOException ex) {
            // File not found... do nothing!
        }
        return false;
    }

    public boolean load(Tournament tournament) {
        this.tournament = tournament;

        for (LevelGroups level : levels) {
            if (!level.loadLevel(tournament)) {
                return false;
            }
        }
        return true;
    }

    public void deleteUsedDesigner() {
        MyFile.deleteFile(FOLDER + File.separator + tournament.name + ".dsg");
    }
    /**
     * *************************************************************
     *
     * MANUAL LINKING
     *
     **************************************************************
     */
}
