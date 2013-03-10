package com.softwaremagico.ktg;

import java.util.List;

public class ScoreOfTeam implements Comparable<ScoreOfTeam> {

    private Team team;
    private List<Fight> fights;

    public ScoreOfTeam(Team team, List<Fight> fights) {
        this.team = team;
        this.fights = fights;
    }

    public Team getTeam() {
        return team;
    }

    public int getWonFights() {
        int total = 0;
        for (int j = 0; j < fights.size(); j++) {
            Fight fight = fights.get(j);
            Team winner = fight.winner();
            if (winner != null && winner.equals(team)) {
                total++;
            }
        }
        return total;
    }

    public int getDrawFights() {
        int total = 0;
        for (int j = 0; j < fights.size(); j++) {
            Fight fight = fights.get(j);
            if ((fight.getTeam1().equals(team) || fight.getTeam2().equals(team))) {
                if (fight.isDrawFight()) {
                    total++;
                }
            }
        }
        return total;
    }

    public int getWonDuels() {
        int total = 0;
        for (int j = 0; j < fights.size(); j++) {
            Fight fight = fights.get(j);
            total += fight.getWonDuels(team);
        }
        return total;
    }

    public int getDrawDuels() {
        int total = 0;
        for (int j = 0; j < fights.size(); j++) {
            Fight fight = fights.get(j);
            if ((fight.getTeam1().equals(team) || fight.getTeam2().equals(team))) {
                total += fight.getDrawDuels();
            }
        }
        return total;
    }

    public int getHits() {
        int total = 0;
        for (int j = 0; j < fights.size(); j++) {
            fights.get(j).getScore(team);
        }
        return total;
    }

    @Override
    public int compareTo(ScoreOfTeam o) {
        if (getWonFights() > o.getWonFights()) {
            return -1;
        }
        if (getWonFights() < o.getWonFights()) {
            return 1;
        }
        if (getWonDuels() > o.getWonDuels()) {
            return -1;
        }
        if (getWonDuels() < o.getWonDuels()) {
            return 1;
        }
        if (getHits() > o.getHits()) {
            return -1;
        }
        if (getHits() < o.getHits()) {
            return 1;
        }
        if (getDrawFights() > o.getDrawFights()) {
            return -1;
        }

        if (getDrawFights() < o.getDrawFights()) {
            return 1;
        }

        if (getDrawDuels() > o.getDrawDuels()) {
            return -1;
        }

        if (getDrawDuels() < o.getDrawDuels()) {
            return 1;
        }
        return getTeam().compareTo(o.getTeam());
    }
}
