package com.softwaremagico.ktg.tournament.king;

import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.LeagueLevel;
import com.softwaremagico.ktg.tournament.TGroup;

public class KingLevel extends LeagueLevel {
	private static final long serialVersionUID = -1706037125819985383L;

	protected KingLevel(Tournament tournament, int level, LeagueLevel nextLevel, LeagueLevel previousLevel) {
		super(tournament, level, nextLevel, previousLevel);
	}

	@Override
	protected LeagueLevel addNewLevel(Tournament tournament, Integer level, LeagueLevel nextLevel,
			LeagueLevel previousLevel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getGroupIndexDestinationOfWinner(TGroup group, Integer winner) {
		// TODO Auto-generated method stub
		return null;
	}

}
