package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import java.util.List;

public enum ScoreType {

    INTERNATIONAL("international"),
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
            case CUSTOM:
                return new ScoreOfTeamCustom(team, fights);
            case INTERNATIONAL:
            default:
                return new ScoreOfTeamInternational(team, fights);
        }
    }

    public static ScoreType getScoreType(String tag) {
        for (ScoreType scoreType : ScoreType.values()) {
            if (scoreType.getTag().equals(tag)) {
                return scoreType;
            }
        }
        return INTERNATIONAL;
    }
}
