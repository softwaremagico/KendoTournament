package com.softwaremagico.ktg.tournament.score;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import java.util.List;

public enum ScoreType {

	CLASSIC("classic"), WIN_OVER_DRAWS("winOverDraws"), EUROPEAN("european"), CUSTOM("custom"), INTERNATIONAL(
			"international");
	public static ScoreType DEFAULT = ScoreType.INTERNATIONAL;
	private String tag;

	ScoreType(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public static ScoreOfTeam getScoreOfTeam(Team team, List<Fight> fights) {
		switch (team.getTournament().getTournamentScore().getScoreType()) {
		case CLASSIC:
			return new ScoreOfTeamClassic(team, fights);
		case CUSTOM:
			return new ScoreOfTeamCustom(team, fights);
		case WIN_OVER_DRAWS:
			return new ScoreOfTeamWinOverDraws(team, fights);
		case EUROPEAN:
			return new ScoreOfTeamEuropean(team, fights);
		case INTERNATIONAL:
		default:
			return new ScoreOfTeamInternational(team, fights);
		}
	}

	public static ScoreType getScoreType(String tag) {
		for (ScoreType scoreType : ScoreType.values()) {
			if (scoreType.getTag().equals(tag.toLowerCase())) {
				return scoreType;
			}
		}
		return DEFAULT;
	}
}
