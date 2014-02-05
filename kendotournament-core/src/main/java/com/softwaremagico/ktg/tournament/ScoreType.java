package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import java.util.List;

public enum ScoreType {

	CLASSIC("classic"), INTERNATIONAL("international"), HIERARCHICAL("hierarchical"), CUSTOM("custom");

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
		case CUSTOM:
			return new ScoreOfTeamCustom(team, fights);
		case HIERARCHICAL:
			return new ScoreOfTeamHierarchical(team, fights);
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
		return DEFAULT;
	}
}
