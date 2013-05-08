package com.softwaremagico.ktg.core;

import com.softwaremagico.ktg.database.UndrawPool;
import java.util.List;

public class ScoreOfTeam implements Comparable<ScoreOfTeam> {

    private Team team;
    private List<Fight> fights;
    private Integer wonFights = null;
    private Integer drawFights = null;
    private Integer wonDuels = null;
    private Integer drawDuels = null;
    private Integer goldenPoint = null;
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

    public Integer getWonFights() {
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

    public Integer getDrawFights() {
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

    public Integer getWonDuels() {
        if (wonDuels == null) {
            wonDuels = 0;
            for (int j = 0; j < fights.size(); j++) {
                wonDuels += fights.get(j).getWonDuels(team);
            }
        }
        return wonDuels;
    }

    public Integer getDrawDuels() {
        if (drawDuels == null) {
            drawDuels = 0;
            for (int j = 0; j < fights.size(); j++) {
                drawDuels += fights.get(j).getDrawDuels(team);
            }
        }
        return drawDuels;
    }

    public Integer getHits() {
        if (hits == null) {
            hits = 0;
            for (int j = 0; j < fights.size(); j++) {
                hits += fights.get(j).getScore(team);
            }
        }
        return hits;
    }

    public Integer getGoldenPoints() {
        if (goldenPoint == null) {
            goldenPoint = UndrawPool.getInstance().getUndrawsWon(fights.get(0).getTournament(), fights.get(0).getLevel(), fights.get(0).getGroupIndex(), team);
        }
        return goldenPoint;
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

        if (getGoldenPoints() > o.getGoldenPoints()) {
            return -1;
        }

        if (getGoldenPoints() < o.getGoldenPoints()) {
            return 1;
        }

        return 0;
    }
}
