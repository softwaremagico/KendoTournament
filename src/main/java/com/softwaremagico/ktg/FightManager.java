package com.softwaremagico.ktg;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author jorge
 */
public class FightManager {

    private ArrayList<Fight> fights = new ArrayList<>();
    private Tournament tournament;

    FightManager(Tournament tournament) {
        this.tournament = tournament;
    }

    public Fight get(int i) {
        return fights.get(i);
    }

    public boolean existFight(Fight f) {
        return fights.contains(f);
    }

    public void add(Fight f) {
        fights.add(f);
        if (KendoTournamentGenerator.getInstance().isLocallyConnected()) {
            storeFight(f);
        }
    }

    public void addNewFight(Fight f) {
        if (!existFight(f)) {
            fights.add(f);
            storeFight(f);
        }
    }

    public void add(ArrayList<Fight> newFights) {
        for (int i = 0; i < newFights.size(); i++) {
            addNewFight(newFights.get(i));
            //newFights.get(i).team1.completeToLevel(newFights.get(i).level);
            //newFights.get(i).team2.completeToLevel(newFights.get(i).level);
        }
    }

    public boolean setAll(ArrayList<Fight> newFights, boolean storeDatabase) {
        fights = newFights;
        if (storeDatabase && newFights.size() > 0) {
            return KendoTournamentGenerator.getInstance().database.storeFights(fights, true, true);
        }
        return false;
    }

    public void getFightsFromDatabase(Tournament tournament) {
        fights = KendoTournamentGenerator.getInstance().database.searchFightsByTournament(tournament);
    }

    public int size() {
        return fights.size();
    }

    public int arenaSize(int arena) {
        return getFightsOfArena(arena).size();
    }

    public int indexInArenaOfCurrentFight(int arena) {
        int current = currentFight(arena);
        int index = 0;
        for (int i = 0; i < current; i++) {
            if (fights.get(i).asignedFightArea == arena) {
                index++;
            }
        }
        return index;
    }

    public ArrayList<Fight> getFights() {
        return fights;
    }

    /**
     * First fight not over or the last one.
     */
    public int currentFight(int arena) {
        int lastArenaFight = 0;
        for (int i = 0; i < fights.size(); i++) {
            if (fights.get(i).asignedFightArea == arena) {
                if (!fights.get(i).isOver()) {
                    return i;
                }
                lastArenaFight = i;
            }
        }
        return lastArenaFight;
    }

    public boolean isLastFightOfArena(int arena) {
        return currentArenaFight(arena) >= arenaSize(arena) - 1;
    }

    public int currentArenaFight(int arena) {
        int arenacounter = 0;
        ArrayList<Fight> arenaFights = getFightsOfArena(arena);
        for (int i = 0; i < arenaFights.size(); i++) {
            if (arenaFights.get(i).asignedFightArea == arena) {
                if (!arenaFights.get(i).isOver() || i == arenaFights.size() - 1) {
                    return arenacounter;
                }
                arenacounter++;
            }
        }
        if (arenacounter >= arenaFights.size()) {
            arenacounter = arenaFights.size() - 1;
        }
        return arenacounter;
    }

    public Fight getNextAreaFight(int from, int arena) {
        ArrayList<Fight> arenaFights = getFightsOfArena(arena);
        for (int i = from + 1; i < arenaFights.size(); i++) {
            if (arenaFights.get(i).asignedFightArea == arena) {
                return arenaFights.get(i);
            }
        }
        return null;
    }

    public ArrayList<Fight> getFightsOfArena(int arena) {
        ArrayList<Fight> fightsOfArea = new ArrayList<>();
        for (int i = 0; i < fights.size(); i++) {
            if (fights.get(i).asignedFightArea == arena) {
                fightsOfArea.add(fights.get(i));
            }
        }
        return fightsOfArea;
    }

    public ArrayList<Fight> getFightsOfLevel(int level) {
        ArrayList<Fight> fightsOfLevel = new ArrayList<>();
        for (int i = 0; i < fights.size(); i++) {
            if (fights.get(i).level == level) {
                fightsOfLevel.add(fights.get(i));
            }
        }
        return fightsOfLevel;
    }

    public Fight getSelectedFight(int arena) {
        return fights.get(currentFight(arena));
    }

    public int getPositionOfPreviousAreaFight(int arena) {
        for (int i = currentFight(arena) - 1; i >= 0; i--) {
            if (fights.get(i).asignedFightArea == arena) {
                return i;
            }
        }
        return -1;
    }

    public Fight getPreviousAreaFight(int arena) {
        for (int i = currentFight(arena) - 1; i >= 0; i--) {
            if (fights.get(i).asignedFightArea == arena) {
                return fights.get(i);
            }
        }
        return null;
    }

    public Fight getPreviousAreaFight(int from, int arena) {
        for (int i = from - 1; i >= 0; i--) {
            if (fights.get(i).asignedFightArea == arena) {
                return fights.get(i);
            }
        }
        return null;
    }

    public Fight getPreviousOfPreviousAreaFight(int arena) {
        return getPreviousAreaFight(getPositionOfPreviousAreaFight(arena), arena);
    }

    public Fight getNextAreaFight(int arena) {
        for (int i = currentFight(arena) + 1; i < fights.size(); i++) {
            if (fights.get(i).asignedFightArea == arena) {
                return fights.get(i);
            }
        }
        return null;
    }

    public void setFightAsNotOver(int fight) {
        if (fight >= 0 && fight < fights.size()) {
            fights.get(fight).setAsNotOver();
            KendoTournamentGenerator.getInstance().database.updateFightAsNotOver(fights.get(fight));
        }
    }

    public void setSelectedFightAsOver(int arena) {
        setFightAsOver(currentFight(arena));
    }

    public void setSelectedFightAsNotOver(int arena) {
        setFightAsNotOver(currentFight(arena));
        //To select the previous one.
        setFightAsNotOver(getPositionOfPreviousAreaFight(arena));
    }

    public void deleteSelectedFight(int arena, boolean verbose) {
        try {
            if (KendoTournamentGenerator.getInstance().database.deleteFight(fights.get(currentFight(arena)), verbose)) {
                KendoTournamentGenerator.getInstance().database.deleteTeamInLevel(fights.get(currentFight(arena)).team1, fights.get(currentFight(arena)).level, false);
                KendoTournamentGenerator.getInstance().database.deleteTeamInLevel(fights.get(currentFight(arena)).team2, fights.get(currentFight(arena)).level, false);
                fights.remove(currentFight(arena));
            }
        } catch (IndexOutOfBoundsException iob) {
        }
    }

    public boolean deleteAllFights(Tournament tournament, boolean verbose) {
        if (KendoTournamentGenerator.getInstance().database.deleteFightsOfTournament(tournament, verbose)) {
            KendoTournamentGenerator.getInstance().database.deleteAllMemberChangesInTeams(tournament, verbose);
            fights = new ArrayList<>();
            return true;
        }
        return false;
    }

    public boolean deleteAllFightsButNotFromDatabase(Tournament tournament, boolean verbose) {
        fights = new ArrayList<>();
        return true;
    }

    public boolean deleteFightsOfLevel(Tournament tournament, int level, boolean verbose) {
        if (KendoTournamentGenerator.getInstance().database.deleteFightsOfLevelOfTournament(tournament, level, verbose)) {
            Log.finer("Delete fights in memory.");
            for (int i = 0; i < fights.size(); i++) {
                if (fights.get(i).level >= level) {
                    fights.remove(i);
                    i--;
                }
            }
            return true;
        }
        return false;
    }

    public void showFights() {
        for (int i = 0; i < fights.size(); i++) {
            fights.get(i).showDuels();
        }
    }

    public int numberOfFightsOver() {
        int over = 0;
        for (int i = 0; i < fights.size(); i++) {
            if (fights.get(i).isOver()) {
                over++;
            }
        }
        return over;
    }

    public boolean areAllOver() {
        for (int i = 0; i < fights.size(); i++) {
            if (!fights.get(i).isOver()) {
                return false;
            }
        }
        return true;
    }

    public boolean areArenaOver(int arena) {
        for (int i = 0; i < fights.size(); i++) {
            if (!fights.get(i).isOver() && fights.get(i).asignedFightArea == arena) {
                Log.debug("Fight '" + fights.get(i).team1.getName() + " vs " + fights.get(i).team2.getName() + "' is not over.");
                return false;
            }
        }
        Log.finest("All arena fights are over.");
        return true;
    }

    private List<Integer> returnArenas() {
        List<Integer> arenas = new ArrayList<>();
        for (int i = 0; i < fights.size(); i++) {
            if (!arenas.contains(fights.get(i).asignedFightArea)) {
                arenas.add(fights.get(i).asignedFightArea);
            }
        }
        return arenas;
    }

    /**
     * Detect if the fightManager of an arena are over but not fightManager of
     * other arenas.
     *
     * @param arena
     * @return -1 if all are over, or the number of the first arena not
     * finished.
     */
    public int allArenasAreOver() {
        for (int i = 0; i < returnArenas().size(); i++) {
            if (!areArenaOver(returnArenas().get(i))) {
                return i;
            }
        }
        return -1;
    }

    private boolean addFightToList(Fight f, ArrayList<Fight> fightsList) {
        if (fightsList.contains(f)) {
            return false;
        } else {
            fightsList.add(f);
        }
        return true;
    }

    public boolean areFightsStarted() {
        for (int i = 0; i < fights.size(); i++) {
            if (fights.get(i).isOver()) {
                return true;
            }
        }
        return false;
    }

    public int getLastLevel() {
        int level = 0;
        for (int i = 0; i < fights.size(); i++) {
            if (level < fights.get(i).level) {
                level = fights.get(i).level;
            }
        }
        return level;
    }

    public void removeAll() {
        fights = new ArrayList<>();
    }

    public void updateFightsWithNewOrderOfTeam(Team t) {
        for (int i = 0; i < fights.size(); i++) {
            if (fights.get(i).team1.getName().equals(t.getName())) {
                fights.get(i).team1 = t;
            }
            if (fights.get(i).team2.getName().equals(t.getName())) {
                fights.get(i).team2 = t;
            }
        }
    }

    /**
     * One fight that uses the team in this level is already started.
     *
     * @param t
     * @param level
     * @return
     */
    public boolean someFightWithTeamAndLevelIsStarted(Team t, int level) {
        for (int i = 0; i < fights.size(); i++) {
            //Is a fight of this level
            if (fights.get(i).level == level) {
                //The team is in this fight.
                if (fights.get(i).team1.getName().equals(t.getName())
                        || fights.get(i).team2.getName().equals(t.getName())) {
                    if (fights.get(i).isOver()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * ****************************************************************************
     *
     *
     * RANDOM FIGHTS
     *
     *
     *****************************************************************************
     */
    /**
     *
     * @param listTeams
     * @param tournament
     * @return
     */
    @SuppressWarnings("unchecked")
    private ArrayList<Fight> obtainFightsWithOneFightArea(List<Team> listTeams, Tournament competition) {
        ArrayList<Fight> results = new ArrayList<>();
        Random rnd = new Random();
        List<Integer>[] fightsByTeam = new List[listTeams.size()];

        for (int i = 0; i < listTeams.size(); i++) {
            fightsByTeam[i] = new ArrayList<>();
            for (int j = 0; j < listTeams.size(); j++) {
                if (j != i) {
                    fightsByTeam[i].add(j);
                }
            }
        }

        int totalFights = listTeams.size() * (listTeams.size() - 1) / 2;

        Integer team;
        team = rnd.nextInt(listTeams.size());
        while (totalFights > 0) {
            Integer versus = fightsByTeam[team].get(rnd.nextInt(fightsByTeam[team].size()));
            fightsByTeam[team].remove(versus);
            fightsByTeam[versus].remove(team);
            Fight f;
            //Do not change the color of the participant.
            if (totalFights % 2 == 0) {
                f = new Fight(listTeams.get(team), listTeams.get(versus), competition, 0);
            } else {
                f = new Fight(listTeams.get(versus), listTeams.get(team), competition, 0);
            }
            totalFights--;
            //Ensure that a fight is not repeated!
            addFightToList(f, results);

            //If there is remaining combats, repeat the last team.
            if (fightsByTeam[versus].size() > 0) {
                team = versus;
            } else {
                int forbidden;
                do {
                    forbidden = team;
                    team = rnd.nextInt(listTeams.size());
                } while ((fightsByTeam[team].isEmpty() && totalFights > 0) || team == forbidden);
            }
        }
        return results;
    }

    private List<Couple> deleteAllCouplesWhereIsTeamOfCouple(Couple c, List<Couple> remainFights) {
        List<Couple> notContains = new ArrayList<>();
        for (int i = 0; i < remainFights.size(); i++) {
            if ((remainFights.get(i).a != c.a)
                    && (remainFights.get(i).a != c.b)
                    && (remainFights.get(i).b != c.a)
                    && (remainFights.get(i).b != c.b)) {
                notContains.add(remainFights.get(i));
            }
        }
        return notContains;
    }

    private ArrayList<Fight> obtainFightsWithTwoOrMoreFightArea(List<Team> listTeams, Tournament competition) {
        ArrayList<Fight> results = new ArrayList<>();
        int areas = Math.min(competition.fightingAreas, listTeams.size() / 2);
        List<Couple> allFights = new ArrayList<>();
        Random rnd = new Random();
        for (int i = 0; i < listTeams.size(); i++) {
            for (int j = i + 1; j < listTeams.size(); j++) {
                if (j != i) {
                    allFights.add(new Couple(i, j));
                }
            }
        }
        int numberOfFights = allFights.size();
        do {
            List<Couple> remainFights = new ArrayList<>();
            remainFights.addAll(allFights);
            for (int j = 0; (j < areas && (numberOfFights > 0)); j++) {
                try {
                    Couple c;
                    int selected;
                    selected = rnd.nextInt(remainFights.size());
                    c = remainFights.get(selected);
                    remainFights = deleteAllCouplesWhereIsTeamOfCouple(c, remainFights);
                    addFightToList(new Fight(listTeams.get(c.a), listTeams.get(c.b), competition, j), results);
                    allFights.remove(c);
                    numberOfFights--;
                } catch (IllegalArgumentException iae) {
                    continue;
                }
            }
        } while (numberOfFights > 0);
        return results;
    }

    public ArrayList<Fight> obtainRandomFights(List<Team> listTeams, Tournament competition) {
        if (competition.fightingAreas < 2) {
            return obtainFightsWithOneFightArea(listTeams, competition);
        } else {
            return obtainFightsWithTwoOrMoreFightArea(listTeams, competition);
        }
    }

    private Couple versusMoreRemainingFights(List<Couple> couples, int teamA, List<Integer> lessUsed, List<Team> listTeams) {
        //Obtain team with more remaining fightManager.
        int max = -1000000;
        int teamMoreFights = 0;
        Couple c = null;
        int less = 0;

        while (c == null) {
            //Couples of TeamA
            List<Couple> listTeamA = new ArrayList<>();
            for (int i = 0; i < couples.size(); i++) {
                if (couples.get(i).a == teamA || couples.get(i).b == teamA) {
                    listTeamA.add(couples.get(i));
                }
            }


            List<List<Couple>> list = new ArrayList<>();
            for (int i = 0; i < listTeams.size(); i++) {
                list.add(new ArrayList<Couple>());
            }
            //Max fightManager to do for teamA.
            for (int i = 0; i < listTeamA.size(); i++) {
                if (listTeamA.get(i).a == teamA) {
                    for (int j = 0; j < couples.size(); j++) {
                        if (couples.get(j).a == listTeamA.get(i).b || couples.get(j).b == listTeamA.get(i).b) {
                            list.get(listTeamA.get(i).b).add(couples.get(j));
                        }
                    }
                }

                if (listTeamA.get(i).b == teamA) {
                    for (int j = 0; j < couples.size(); j++) {
                        if (couples.get(j).a == listTeamA.get(i).a || couples.get(j).b == listTeamA.get(i).a) {
                            list.get(listTeamA.get(i).a).add(couples.get(j));
                        }
                    }
                }
            }

            for (int i = 0; i < list.size(); i++) {
                if (max < list.get(i).size()) {
                    max = list.get(i).size();
                    teamMoreFights = i;
                }
            }

            //Select the team less exhausted.
            boolean found = false;
            for (int j = 0; j < lessUsed.size(); j++) {
                for (int i = 0; i < list.get(teamMoreFights).size(); i++) {
                    if (list.get(teamMoreFights).get(i).a == teamA) {
                        if (list.get(teamMoreFights).get(i).b == lessUsed.get(j)) {
                            c = list.get(teamMoreFights).get(i);
                            found = true;
                            break;
                        }
                    }

                    if (list.get(teamMoreFights).get(i).b == teamA) {
                        if (list.get(teamMoreFights).get(i).a == lessUsed.get(j)) {
                            c = list.get(teamMoreFights).get(i);
                            found = true;
                            break;
                        }
                    }
                }
                if (found) {
                    break;
                }
            }

            if (c == null && couples.size() > 0) {
                c = couples.get(0);
            }

            couples.remove(c);
            max = -1000000;
        }
        //Put the less used and teamA as the last used.
        Integer lu = lessUsed.get(less);
        lessUsed.remove(lu);
        lessUsed.add(lu);

        int indexTeamA = 0;
        for (int i = 0; i < lessUsed.size(); i++) {
            if (lessUsed.get(i) == teamA) {
                indexTeamA = i;
            }
        }
        lu = lessUsed.get(indexTeamA);
        lessUsed.remove(lu);
        lessUsed.add(lu);

        return c;
    }

    public ArrayList<Fight> obtainSortedFights(List<Team> listTeams, Tournament competition) {
        ArrayList<Fight> results = new ArrayList<>();
        List<Couple> couples = new ArrayList<>();
        List<Integer> lessUsed = new ArrayList<>();
        int area = 0;
        boolean teamAInLeft = true;

        //All possibles couples
        for (int i = 0; i < listTeams.size(); i++) {
            for (int j = i + 1; j < listTeams.size(); j++) {
                if (j != i) {
                    couples.add(new Couple(i, j));
                }
            }
        }

        //Last Used
        for (int i = 0; i < listTeams.size(); i++) {
            lessUsed.add(i);
        }

        int teamA = lessUsed.get(0);
        int level = 0;
        while (couples.size() > 0) {
            Couple c = versusMoreRemainingFights(couples, teamA, lessUsed, listTeams);
            if ((teamAInLeft && c.a == teamA) || (!teamAInLeft && c.a != teamA)) {
                addFightToList(new Fight(listTeams.get(c.a), listTeams.get(c.b), competition, area, level), results);
            } else {
                addFightToList(new Fight(listTeams.get(c.b), listTeams.get(c.a), competition, area, level), results);
            }

            //Continue with the other team.
            if (c.a != teamA) {
                teamA = c.a;
            } else {
                teamA = c.b;
            }
            teamAInLeft = !teamAInLeft;
            level++;
        }


        return results;
    }

    public static int getMaxLevelOfFights(ArrayList<Fight> fightsList) {
        int level = 0;
        for (Fight f : fightsList) {
            if (f.level > level) {
                level = f.level;
            }
        }
        return level;
    }

    /**
     * **********************************************
     *
     * UPDATE FIGHTS
     *
     ***********************************************
     */
    /**
     * Store a fight into the database.
     *
     * @param f
     */
    private boolean storeFight(Fight f) {
        return KendoTournamentGenerator.getInstance().database.storeFight(f, false, false);
    }

    private ArrayList<Fight> notUpdatedFights() {
        ArrayList<Fight> notStored = new ArrayList<>();
        for (Fight f : fights) {
            if (!f.isOverStored() || !f.areUpdatedDuelsOfFight()) {
                notStored.add(f);
            }
        }
        return notStored;
    }

    public void setFightAsOver(int fight) {
        if (fight >= 0 && fight < fights.size()) {
            setFightAsOver(fights.get(fight));
        }
    }

    public void setFightAsOver(Fight fight) {
        fight.setOver();
        Log.finest("Fight '" + fight.team1.getName() + " vs " + fight.team2.getName() + "' is set to over.");
        //KendoTournamentGenerator.getInstance().database.updateFightAsOver(fight);
    }

    private boolean storeDuel(Duel d, Fight fight, int player) {
        Log.fine("Storing duel '" + d.showScore());
        if (d.needsToBeStored()) {
            if (KendoTournamentGenerator.getInstance().database.storeDuel(d, fight, player)) {
                d.setStored(true);
                return true;
            }
        } else {
            d.setStored(true);
            return true;
        }
        return false;
    }

    private boolean storeDuelOfFights(Fight fight) {
        Log.fine("Storing duels of fight " + fight.show() + ".");
        for (int i = 0; i < fight.duels.size(); i++) {
            if (!storeDuel(fight.duels.get(i), fight, i)) {
                return false;
            }
        }
        return true;
    }

    public boolean storeNotUpdatedFightsAndDuels() {
        Log.fine("Storing all not updated fights and duels.");
        ArrayList<Fight> notUpdatedFights = notUpdatedFights();
        for (Fight f : notUpdatedFights) {
            if (!f.isOverStored() && f.isOver()) {
                Log.finest("Fight " + f.show() + " is not over.");
                KendoTournamentGenerator.getInstance().database.updateFightAsOver(f);
            }

            if (!storeDuelOfFights(f)) {
                return false;
            }
        }
        return true;
    }

    public List<String> convert2Csv() {
        List<String> Csv = new ArrayList<>();
        for (int i = 0; i < fights.size(); i++) {
            Csv.addAll(fights.get(i).convert2Csv(i));
        }
        return Csv;
    }

    public void importFromCsv(List<String> csv) {
        int duelsCount = 0;
        Fight fight = null;
        for (String csvLine : csv) {
            if (csvLine.startsWith(Fight.getTag())) {
                duelsCount = 0;
                String[] fields = csvLine.split(";");
                //Obtain fight.
                if (Integer.parseInt(fields[1]) < fights.size()) {
                    fight = fights.get(Integer.parseInt(fields[1]));
                    //Fight is correct.
                    if (!fight.team1.getName().equals(fields[2]) || !fight.team2.getName().equals(fields[3])) {
                        fight = null;
                    }
                }
            } else if (csvLine.startsWith(Duel.getCsvTag())) {
                if (fight != null) {
                    fight.duels.get(duelsCount).importFromCsv(csvLine);
                    duelsCount++;
                }
            }
        }
    }

    public boolean storeLazyFights(int arena) {
        //If all arena fights are over or strict store is selected.
        if (areArenaOver(arena) || !KendoTournamentGenerator.getInstance().isDatabaseLazyUpdate()) {
            //Store score into database.
            return storeNotUpdatedFightsAndDuels();
        }
        return false;
    }

    /**
     * **********************************************
     *
     * SECONDARY CLASSES
     *
     ***********************************************
     */
    /**
     * A couple of teams for a fight.
     */
    private class Couple {

        int a;
        int b;
        int weight = 1;

        Couple(int valuea, int valueb) {
            a = valuea;
            b = valueb;
        }
    }
}
