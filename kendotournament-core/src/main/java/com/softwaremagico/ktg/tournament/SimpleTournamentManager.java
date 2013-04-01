package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import java.util.List;

/**
 * A simple tournament is a tournament with only one group.
 */
public class SimpleTournamentManager implements ITournamentManager {

    TournamentGroup group;

    @Override
    public List<Fight> nextFights() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getNumberOfLevels() {
        return 1;
    }

    @Override
    public List<TournamentGroup> getGroups(Integer level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
