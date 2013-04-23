package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Tournament;
import java.util.List;

/**
 * In a ring tournament, one team fights with all other teams consecutively.
 */
public class LoopTournamentManager extends SimpleTournamentManager {

    protected LoopTournamentManager(Tournament tournament) {
        super(tournament);
    }

    @Override
    public List<Fight> createRandomFights(Integer level) {
        if (group == null || level != 0) {
            return null;
        }
        return group.createLoopFights(true);
    }

    @Override
    public List<Fight> createSortedFights(Integer level) {
        if (group == null || level != 0) {
            return null;
        }
        return group.createLoopFights(false);
    }
}
