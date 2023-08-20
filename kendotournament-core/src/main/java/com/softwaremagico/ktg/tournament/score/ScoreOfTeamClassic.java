package com.softwaremagico.ktg.tournament.score;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import java.util.List;

public class ScoreOfTeamClassic extends ScoreOfTeam {

    public ScoreOfTeamClassic(Team team, List<Fight> fights) {
        super(team, fights);
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

        if (getGoldenPoints() > o.getGoldenPoints()) {
            return -1;
        }

        if (getGoldenPoints() < o.getGoldenPoints()) {
            return 1;
        }

        return 0;
    }
}
