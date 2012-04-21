/*
 *  This software is designed by Jorge Hortelano Otero.
 *  softwaremagico@gmail.com
 *  Copyright (C) 2012 Jorge Hortelano Otero.
 *  C/Quart 89, 3. Valencia CP:46008 (Spain).
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.softwaremagico.ktg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author jorge
 */
public class FightManager {

    public boolean inverseColours = false;
    public boolean inverseTeams = false;
    private ArrayList<Fight> fights = new ArrayList<Fight>();

    FightManager() {
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
            KendoTournamentGenerator.getInstance().database.storeFight(f, false);
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

    public void getFightsFromDatabase(String championship) {
        fights = KendoTournamentGenerator.getInstance().database.searchFightsByTournamentName(championship);
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

    public int currentArenaFight(int arena) {
        int arenacounter = 0;
        for (int i = 0; i < fights.size(); i++) {
            if (fights.get(i).asignedFightArea == arena) {
                if (!fights.get(i).isOver() || i == fights.size() - 1) {
                    return arenacounter;
                }
                arenacounter++;
            }
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
        ArrayList<Fight> fightsOfArea = new ArrayList<Fight>();
        for (int i = 0; i < fights.size(); i++) {
            if (fights.get(i).asignedFightArea == arena) {
                fightsOfArea.add(fights.get(i));
            }
        }
        return fightsOfArea;
    }

    public ArrayList<Fight> getFightsOfLevel(int level) {
        ArrayList<Fight> fightsOfLevel = new ArrayList<Fight>();
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

    public boolean deleteAllFights(String championship, boolean verbose) {
        if (KendoTournamentGenerator.getInstance().database.deleteFightsOfTournament(championship, verbose)) {
            KendoTournamentGenerator.getInstance().database.deleteAllMemberChangesInTeams(championship, verbose);
            fights = new ArrayList<Fight>();
            return true;
        }
        return false;
    }

    public boolean deleteAllFightsButNotFromDatabase(String championship, boolean verbose) {
        fights = new ArrayList<Fight>();
        return true;
    }

    public boolean deleteFightsOfLevel(String championship, int level, boolean verbose) {
        if (KendoTournamentGenerator.getInstance().database.deleteFightsOfLevelOfTournament(championship, level, verbose)) {
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
                return false;
            }
        }
        return true;
    }

    private List<Integer> returnArenas() {
        List<Integer> arenas = new ArrayList<Integer>();
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
        fights = new ArrayList<Fight>();
    }

    public void updateFightsWithNewOrderOfTeam(Team t) {
        for (int i = 0; i < fights.size(); i++) {
            if (fights.get(i).team1.returnName().equals(t.returnName())) {
                fights.get(i).team1 = t;
            }
            if (fights.get(i).team2.returnName().equals(t.returnName())) {
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
                if (fights.get(i).team1.returnName().equals(t.returnName())
                        || fights.get(i).team2.returnName().equals(t.returnName())) {
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
     * @param competition
     * @return
     */
    @SuppressWarnings("unchecked")
    private ArrayList<Fight> obtainFightsWithOneFightArea(List<Team> listTeams, Tournament competition) {
        ArrayList<Fight> results = new ArrayList<Fight>();
        Random rnd = new Random();
        List<Integer>[] fightsByTeam = new List[listTeams.size()];

        for (int i = 0; i < listTeams.size(); i++) {
            fightsByTeam[i] = new ArrayList<Integer>();
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
        List<Couple> notContains = new ArrayList<Couple>();
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
        ArrayList<Fight> results = new ArrayList<Fight>();
        int areas = Math.min(competition.fightingAreas, listTeams.size() / 2);
        List<Couple> allFights = new ArrayList<Couple>();
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
            List<Couple> remainFights = new ArrayList<Couple>();
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
            List<Couple> listTeamA = new ArrayList<Couple>();
            for (int i = 0; i < couples.size(); i++) {
                if (couples.get(i).a == teamA || couples.get(i).b == teamA) {
                    listTeamA.add(couples.get(i));
                }
            }


            List<List<Couple>> list = new ArrayList<List<Couple>>();
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

    /**
     * If is the last couple of this team, can not use until is the last or will
     * stop the dominoe effect.
     *
     * @param couples
     * @param teamA
     * @param c
     * @param teamMoreFights
     * @return
     */
    private boolean weCanChooseThisCouple(List<Couple> couples, int teamA, Couple c, List<Team> listTeams) {
        List<List<Couple>> list = new ArrayList<List<Couple>>();

        for (int i = 0; i < listTeams.size(); i++) {
            list.add(new ArrayList<Couple>());
        }

        //List of couples of the teamA ordered by adversary.
        for (int i = 0; i < couples.size(); i++) {
            if (couples.get(i).a == teamA) {
                list.get(couples.get(i).b).add(couples.get(i));
            }

            if (couples.get(i).b == teamA) {
                list.get(couples.get(i).a).add(couples.get(i));
            }
        }

        //The other team of the couple has more than one couple left.
        if ((teamA != c.a) && (existCoupleWithTeam(couples, c.a) > 1) || ((teamA != c.b) && existCoupleWithTeam(couples, c.b) > 1)) {
            //And the couple of the next couple is not my own number.
            return true;
        }

        return false;
    }

    private int existCoupleWithTeam(List<Couple> couples, int teamA) {
        int cont = 0;
        for (int i = 0; i < couples.size(); i++) {
            if (couples.get(i).a == teamA
                    || couples.get(i).b == teamA) {
                cont++;
            }
        }
        return cont;
    }

    private Couple getCouple(List<Couple> couples, int teamA, int teamB, boolean delete) {
        for (int i = 0; i < couples.size(); i++) {
            if ((couples.get(i).a == teamA
                    && couples.get(i).b == teamB)
                    || (couples.get(i).b == teamA
                    && couples.get(i).a == teamB)) {
                Couple c = couples.get(i);
                if (delete) {
                    couples.remove(i);
                }
                return c;
            }
        }
        return null;
    }

    public ArrayList<Fight> obtainSortedFights(List<Team> listTeams, Tournament competition) {
        ArrayList<Fight> results = new ArrayList<Fight>();
        List<Couple> couples = new ArrayList<Couple>();
        List<Integer> lessUsed = new ArrayList<Integer>();
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

    public static int getLevelOfFights(ArrayList<Fight> fightsList) {
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
        return KendoTournamentGenerator.getInstance().database.storeFight(f, false);
    }

    private ArrayList<Fight> notUpdatedFights() {
        ArrayList<Fight> notStored = new ArrayList<Fight>();
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
        //KendoTournamentGenerator.getInstance().database.updateFightAsOver(fight);
    }

    private boolean storeDuel(Duel d, Fight fight, int player) {
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
        for (int i = 0; i < fight.duels.size(); i++) {
            if (!storeDuel(fight.duels.get(i), fight, i)) {
                return false;
            }
        }
        return true;
    }

    public boolean storeNotUpdatedFightsAndDuels() {
        ArrayList<Fight> notUpdatedFights = notUpdatedFights();
        for (Fight f : notUpdatedFights) {
            if (!f.isOverStored() && f.isOver()) {
                KendoTournamentGenerator.getInstance().database.updateFightAsOver(f);
            }

            if (!storeDuelOfFights(f)) {
                return false;
            }
        }
        return true;
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