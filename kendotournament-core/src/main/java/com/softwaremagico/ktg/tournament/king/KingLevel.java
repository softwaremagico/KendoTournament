package com.softwaremagico.ktg.tournament.king;

import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.championship.LeagueLevel;

public class KingLevel extends LeagueLevel {
	private static final long serialVersionUID = -1706037125819985383L;

	protected KingLevel(Tournament tournament, int level, LeagueLevel nextLevel, LeagueLevel previousLevel) {
		super(tournament, level, nextLevel, previousLevel);
	}

	@Override
	protected LeagueLevel addNewLevel(Tournament tournament, Integer level, LeagueLevel nextLevel, LeagueLevel previousLevel) {
		return new KingLevel(tournament, level, nextLevel, previousLevel);
	}

	@Override
	public Integer getGroupIndexDestinationOfWinner(TGroup group, Integer winner) {
		return 0;
	}

}
