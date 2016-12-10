package com.softwaremagico.ktg.tournament.score;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.RegisteredPerson;
import java.util.List;

/**
 * Same as european
 *
 */
public class ScoreOfCompetitorInternational extends ScoreOfCompetitorEuropean {

	public ScoreOfCompetitorInternational(RegisteredPerson competitor, List<Fight> fights) {
		super(competitor, fights);
	}

}
