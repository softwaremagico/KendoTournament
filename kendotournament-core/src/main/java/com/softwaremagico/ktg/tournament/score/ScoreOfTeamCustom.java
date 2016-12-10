package com.softwaremagico.ktg.tournament.score;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import java.util.List;

public class ScoreOfTeamCustom extends ScoreOfTeam {

    public ScoreOfTeamCustom(Team team, List<Fight> fights) {
        super(team, fights);
    }

    @Override
    public int compareTo(ScoreOfTeam o) {
        if (getWonFights() * getTeam().getTournament().getTournamentScore().getPointsVictory() + getDrawFights() * getTeam().getTournament().getTournamentScore().getPointsDraw()
                > o.getWonFights() * getTeam().getTournament().getTournamentScore().getPointsVictory() + o.getDrawFights() * getTeam().getTournament().getTournamentScore().getPointsDraw()) {
            return -1;
        }

        if (getWonFights() * getTeam().getTournament().getTournamentScore().getPointsVictory() + getDrawFights() * getTeam().getTournament().getTournamentScore().getPointsDraw()
                < o.getWonFights() * getTeam().getTournament().getTournamentScore().getPointsVictory() + o.getDrawFights() * getTeam().getTournament().getTournamentScore().getPointsDraw()) {
            return 1;
        }

        if (getWonDuels() * getTeam().getTournament().getTournamentScore().getPointsVictory() + getDrawDuels() * getTeam().getTournament().getTournamentScore().getPointsDraw()
                > o.getWonDuels() * getTeam().getTournament().getTournamentScore().getPointsVictory() + o.getDrawDuels() * getTeam().getTournament().getTournamentScore().getPointsDraw()) {
            return -1;
        }

        if (getWonDuels() * getTeam().getTournament().getTournamentScore().getPointsVictory() + getDrawDuels() * getTeam().getTournament().getTournamentScore().getPointsDraw()
                < o.getWonDuels() * getTeam().getTournament().getTournamentScore().getPointsVictory() + o.getDrawDuels() * getTeam().getTournament().getTournamentScore().getPointsDraw()) {
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
