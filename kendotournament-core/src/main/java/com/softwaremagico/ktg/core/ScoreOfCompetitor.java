package com.softwaremagico.ktg.core;

import java.util.List;

public class ScoreOfCompetitor implements Comparable<ScoreOfCompetitor> {

    private RegisteredPerson competitor;
    private List<Fight> fights;
    private Integer wonDuels = null;
    private Integer drawDuels = null;
    private Integer hits = null;

    public ScoreOfCompetitor(RegisteredPerson competitor, List<Fight> fights) {
        this.competitor = competitor;
        this.fights = fights;
    }

    public RegisteredPerson getCompetitor() {
        return competitor;
    }

    public Integer getWonDuels() {
        if (wonDuels == null) {
            wonDuels = 0;
            for (int j = 0; j < fights.size(); j++) {
                Fight fight = fights.get(j);
                wonDuels += fight.getWonDuels(competitor);
            }
        }
        return wonDuels;
    }

    public Integer getDrawDuels() {
        if (drawDuels == null) {
            drawDuels = 0;
            for (int j = 0; j < fights.size(); j++) {
                drawDuels += fights.get(j).getDrawDuels(competitor);
            }
        }
        return drawDuels;
    }

    public Integer getHits() {
        if (hits == null) {
            hits = 0;
            for (int j = 0; j < fights.size(); j++) {
                hits += fights.get(j).getScore(competitor);
            }
        }
        return hits;
    }

    @Override
    public int compareTo(ScoreOfCompetitor o) {
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
        //Draw score, order by name;
        return getCompetitor().getSurnameName().compareTo(o.getCompetitor().getSurnameName());
    }
}
