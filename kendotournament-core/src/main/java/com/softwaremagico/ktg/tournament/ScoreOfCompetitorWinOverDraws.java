package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.RegisteredPerson;

import java.util.List;

public class ScoreOfCompetitorWinOverDraws extends ScoreOfCompetitor {

	public ScoreOfCompetitorWinOverDraws(RegisteredPerson competitor, List<Fight> fights) {
		super(competitor, fights);
	}

	@Override
	public int compareTo(ScoreOfCompetitor o) {
		if (getDuelsWon() > o.getDuelsWon()) {
			return -1;
		}
		if (getDuelsWon() < o.getDuelsWon()) {
			return 1;
		}

		if (getHits() > o.getHits()) {
			return -1;
		}
		if (getHits() < o.getHits()) {
			return 1;
		}

		if (getDuelsDraw() > o.getDuelsDraw()) {
			return -1;
		}
		if (getDuelsDraw() < o.getDuelsDraw()) {
			return 1;
		}

		// More duels done with same score is negative.
		if (getDuelsDone() > o.getDuelsDone()) {
			return 1;
		}

		if (getDuelsDone() < o.getDuelsDone()) {
			return -1;
		}

		// Draw score, order by name;
		return getCompetitor().getSurnameName().compareTo(o.getCompetitor().getSurnameName());
	}
}
