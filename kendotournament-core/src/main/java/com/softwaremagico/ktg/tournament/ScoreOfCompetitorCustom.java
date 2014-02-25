package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.RegisteredPerson;

import java.util.List;

public class ScoreOfCompetitorCustom extends ScoreOfCompetitor {

	public ScoreOfCompetitorCustom(RegisteredPerson competitor, List<Fight> fights) {
		super(competitor, fights);
	}

	@Override
	public int compareTo(ScoreOfCompetitor o) {
		if (fights.size() > 0) {
			if (getDuelsWon() * fights.get(0).getTournament().getTournamentScore().getPointsVictory() + getDuelsDraw()
					* fights.get(0).getTournament().getTournamentScore().getPointsDraw() > o.getDuelsWon()
					* fights.get(0).getTournament().getTournamentScore().getPointsVictory() + o.getDuelsDraw()
					* fights.get(0).getTournament().getTournamentScore().getPointsDraw()) {
				return -1;
			}

			if (getDuelsWon() * fights.get(0).getTournament().getTournamentScore().getPointsVictory() + getDuelsDraw()
					* fights.get(0).getTournament().getTournamentScore().getPointsDraw() < o.getDuelsWon()
					* fights.get(0).getTournament().getTournamentScore().getPointsVictory() + o.getDuelsDraw()
					* fights.get(0).getTournament().getTournamentScore().getPointsDraw()) {
				return 1;
			}

			if (getHits() > o.getHits()) {
				return -1;
			}
			if (getHits() < o.getHits()) {
				return 1;
			}

			// More duels done with same score is negative.
			if (getDuelsDone() > o.getDuelsDone()) {
				return 1;
			}

			if (getDuelsDone() < o.getDuelsDone()) {
				return -1;
			}
		}

		// Draw score, order by name;
		return getCompetitor().getSurnameName().compareTo(o.getCompetitor().getSurnameName());
	}
}
