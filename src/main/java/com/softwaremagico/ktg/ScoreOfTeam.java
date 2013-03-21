package com.softwaremagico.ktg;

import java.util.List;

public class ScoreOfTeam implements Comparable<ScoreOfTeam> {

    private Team team;
    private List<Fight> fights;
    private Integer wonFights = null;
    private Integer drawFights = null;
    private Integer wonDuels = null;
    private Integer drawDuels = null;
    private Integer hits = null;

    public ScoreOfTeam(Team team, List<Fight> fights) {
        this.team = team;
        this.fights = fights;
    }

    public Team getTeam() {
        return team;
    }

    public Tournament getTournament() {
        if (team != null) {
            return team.getTournament();
        }
        return null;
    }

    public int getWonFights() {
        if (wonFights == null) {
            wonFights = 0;
            for (int j = 0; j < fights.size(); j++) {
                Fight fight = fights.get(j);
                Team winner = fight.winner();
                if (winner != null && winner.equals(team)) {
                    wonFights++;
                }
            }
        }
        return wonFights;
    }

    public int getDrawFights() {
        if (drawFights == null) {
            drawFights = 0;
            for (int j = 0; j < fights.size(); j++) {
                Fight fight = fights.get(j);
                if ((fight.getTeam1().equals(team) || fight.getTeam2().equals(team))) {
                    if (fight.isDrawFight()) {
                        drawFights++;
                    }
                }
            }
        }
        return drawFights;
    }

    public int getWonDuels() {
        if (wonDuels == null) {
            wonDuels = 0;
            for (int j = 0; j < fights.size(); j++) {
                wonDuels += fights.get(j).getWonDuels(team);
            }
        }
        return wonDuels;
    }

    public int getDrawDuels() {
        if (drawDuels == null) {
            drawDuels = 0;
            for (int j = 0; j < fights.size(); j++) {
                drawDuels += fights.get(j).getDrawDuels(team);
            }
        }
        return drawDuels;
    }

    public int getHits() {
        if (hits == null) {
            hits = 0;
            for (int j = 0; j < fights.size(); j++) {
                hits += fights.get(j).getScore(team);
            }
        }
        return hits;
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
