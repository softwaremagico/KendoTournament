package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import java.util.List;

public class ScoreOfTeamEuropean extends ScoreOfTeam {

    public ScoreOfTeamEuropean(Team team, List<Fight> fights) {
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
