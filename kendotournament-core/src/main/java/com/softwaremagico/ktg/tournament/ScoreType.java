package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import java.util.List;

public enum ScoreType {

	CLASSIC("classic"), EUROPEAN("international"), HIERARCHICAL("hierarchical"), CUSTOM("custom");

	public static ScoreType DEFAULT = ScoreType.EUROPEAN;

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
		case HIERARCHICAL:
			return new ScoreOfTeamHierarchical(team, fights);
		case EUROPEAN:
		default:
			return new ScoreOfTeamEuropean(team, fights);
		}
	}

	public static ScoreType getScoreType(String tag) {
		for (ScoreType scoreType : ScoreType.values()) {
			if (scoreType.getTag().equals(tag)) {
				return scoreType;
			}
		}
		return DEFAULT;
	}
}
