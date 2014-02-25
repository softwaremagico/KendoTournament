package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.RegisteredPerson;
import java.util.List;

public class ScoreOfCompetitorClassic implements Comparable<ScoreOfCompetitorClassic> {

    private RegisteredPerson competitor;
    private List<Fight> fights;
    private Integer wonDuels = null;
    private Integer drawDuels = null;
    private Integer hits = null;
    private Integer duelsDone = null;
    private Integer fightsWon = null;
    private Integer fightsDraw = null;

    public ScoreOfCompetitorClassic(RegisteredPerson competitor, List<Fight> fights) {
        this.competitor = competitor;
        this.fights = fights;
    }

    public RegisteredPerson getCompetitor() {
        return competitor;
    }

    public Integer getDuelsDone() {
        if (duelsDone == null) {
            duelsDone = 0;
            for (Fight fight : fights) {
                duelsDone += fight.getDuels(competitor);
            }
        }
        return duelsDone;
    }

    public Integer getDuelsWon() {
        if (wonDuels == null) {
            wonDuels = 0;
            for (int j = 0; j < fights.size(); j++) {
                Fight fight = fights.get(j);
                wonDuels += fight.getWonDuels(competitor);
            }
        }
        return wonDuels;
    }

    public Integer getFightsWon() {
        if (fightsWon == null) {
            fightsWon = 0;
            for (int j = 0; j < fights.size(); j++) {
                if (fights.get(j).isWon(competitor)) {
                    fightsWon++;
                }
            }
        }
        return fightsWon;
    }

    public Integer getFightsDraw() {
        if (fightsDraw == null) {
            fightsDraw = 0;
            for (int j = 0; j < fights.size(); j++) {
                if (fights.get(j).getWinner() == 0 && (fights.get(j).getTeam1().isMember(competitor) || fights.get(j).getTeam2().isMember(competitor))) {
                    fightsDraw++;
                }
            }
        }
        return fightsDraw;

    }

    public Integer getDuelsDraw() {
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
    public int compareTo(ScoreOfCompetitorClassic o) {
        if (getDuelsWon() > o.getDuelsWon()) {
            return -1;
        }
        if (getDuelsWon() < o.getDuelsWon()) {
            return 1;
        }
        if (getHits() > o.getHits()) {
            return -1;
        }
        if (getHits() < o.getHits()) {
            return 1;
        }

        // More duels done with same score is negative.
        if (getDuelsDone() > o.getDuelsDone()) {
            return 1;
        }

        if (getDuelsDone() < o.getDuelsDone()) {
            return -1;
        }

        // Draw score, order by name;
        return getCompetitor().getSurnameName().compareTo(o.getCompetitor().getSurnameName());
    }

    @Override
    public String toString() {
        return competitor.getSurnameName() + " D:" + getDuelsWon() + "/" + getDuelsDraw() + ", H:" + getHits();
    }
}
