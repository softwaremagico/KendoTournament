package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.ScoreOfTeam;
import com.softwaremagico.ktg.core.ScoreOfTeamClassic;
import com.softwaremagico.ktg.core.ScoreOfTeamCustom;
import com.softwaremagico.ktg.core.ScoreOfTeamEuropean;
import com.softwaremagico.ktg.core.Team;
import java.util.List;

public enum ScoreType {

    CLASSIC("classic"),
    EUROPEAN("european"),
    CUSTOM("custom");
    private String tag;

    ScoreType(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public static ScoreOfTeam getScoreOfTeam(Team team, List<Fight> fights) {
        switch (team.getTournament().getTournamentScore().getScoreType()) {
            case EUROPEAN:
                return new ScoreOfTeamEuropean(team, fights);
            case CUSTOM:
                return new ScoreOfTeamCustom(team, fights);
            case CLASSIC:
            default:
                return new ScoreOfTeamClassic(team, fights);
        }
    }

    public static ScoreType getScoreType(String tag) {
        for (ScoreType scoreType : ScoreType.values()) {
            if (scoreType.getTag().equals(tag)) {
                return scoreType;
            }
        }
        return CLASSIC;
    }
}
