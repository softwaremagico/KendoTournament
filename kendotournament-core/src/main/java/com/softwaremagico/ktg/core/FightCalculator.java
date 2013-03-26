package com.softwaremagico.ktg.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class FightCalculator {

    public static List<Fight> getSortedFightsOfGroup(Tournament tournament, List<Team> teams, int asignedArea, int level) {
        List<Fight> results = new ArrayList<>();
        AdversaryPool adversaryPool = new AdversaryPool(teams);

        //Create fights.
        while (adversaryPool.remainTeams()) {
            Team team1 = adversaryPool.getTeamMaxFights();
            Team team2;
            while ((team2 = adversaryPool.getFirstAdversary(team1)) != null) {
                Fight fight;
                if (results.size() % 2 == 0) {
                    fight = new Fight(tournament, team1, team2, asignedArea, level, results.size());
                } else {
                    fight = new Fight(tournament, team2, team1, asignedArea, level, results.size());
                }
                results.add(fight);
                team1 = team2;
            }
        }

        return results;
    }

    public static List<Fight> obtainRandomFightsOfGroup(Tournament tournament, List<Team> teams, int asignedArea, int level) {
        List<Fight> results = new ArrayList<>();
        AdversaryPool adversaryPool = new AdversaryPool(teams);

        //Create fights.
        while (adversaryPool.remainTeams()) {
            Team team1 = adversaryPool.getRandomTeam();
            Team team2;
            while ((team2 = adversaryPool.getRandomAdversary(team1)) != null) {
                Fight fight;
                if (results.size() % 2 == 0) {
                    fight = new Fight(tournament, team1, team2, asignedArea, level, results.size());
                } else {
                    fight = new Fight(tournament, team2, team1, asignedArea, level, results.size());
                }
                results.add(fight);
                team1 = team2;
            }
        }

        return results;
    }
}

class AdversaryPool {

    HashMap<Team, List<Team>> remainingFights = new HashMap<>();

    AdversaryPool(List<Team> teams) {
        //All combinations
        for (Team team : teams) {
            remainingFights.put(team, new ArrayList<Team>());
            for (Team otherTeam : teams) {
                if (!team.equals(otherTeam)) {
                    remainingFights.get(team).add(otherTeam);
                }
            }
        }
    }

    public Team getTeamMaxFights() {
        Integer maxRemainingFights = 0;
        Team selected = null;
        for (Team team : remainingFights.keySet()) {
            //Obtain team with more remaining fights.
            if (remainingFights.get(team).size() > maxRemainingFights) {
                maxRemainingFights = remainingFights.get(team).size();
                selected = team;
            }
        }
        return selected;
    }

    public Team getRandomTeam() {
        List<Team> possibleTeams = new ArrayList<>();
        Team selected = null;
        //Only teams with one or more fight left.
        for (Team team : remainingFights.keySet()) {
            if (remainingFights.get(team).size() > 0) {
                possibleTeams.add(team);
            }
        }

        if (possibleTeams.size() > 0) {
            Random rnd = new Random();
            selected = possibleTeams.get(rnd.nextInt(possibleTeams.size()));
        }

        return selected;
    }

    public Team getFirstAdversary(Team team) {
        List<Team> adversaries = remainingFights.get(team);
        Integer maxRemainingFights = 0;
        Team adversary = null;
        //Obtain team with more remaining fights.
        for (Team t : adversaries) {
            if (remainingFights.get(t).size() > maxRemainingFights) {
                maxRemainingFights = remainingFights.get(t).size();
                adversary = t;
            }
        }
        return adversary;
    }

    public Team getRandomAdversary(Team team) {
        List<Team> adversaries = remainingFights.get(team);
        Integer maxRemainingFights = 0;
        Team adversary = null;
        //Obtain maxAdversary Number.
        for (Team t : adversaries) {
            if (remainingFights.get(t).size() > maxRemainingFights) {
                maxRemainingFights = remainingFights.get(t).size();
            }
        }

        //Obtain a list with maxAdversaries.
        List<Team> maxAdversaries = new ArrayList<>();
        for (Team t : adversaries) {
            if (remainingFights.get(t).size() == maxRemainingFights) {
                maxAdversaries.add(t);
            }
        }

        //Select a random team
        if (maxAdversaries.size() > 0) {
            Random rnd = new Random();
            adversary = maxAdversaries.get(rnd.nextInt(maxAdversaries.size()));
        }


        return adversary;
    }

    public void removeFight(Team team1, Team team2) {
        remainingFights.get(team1).remove(team2);
        remainingFights.get(team2).remove(team1);
    }

    public boolean remainTeams() {
        for (Team team : remainingFights.keySet()) {
            if (!remainingFights.get(team).isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
