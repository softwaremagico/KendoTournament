package com.softwaremagico.ktg.tournament.loop;

import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.log.KendoLog;
import com.softwaremagico.ktg.persistence.TeamPool;
import com.softwaremagico.ktg.tournament.TGroup;
import com.softwaremagico.ktg.tournament.simple.SimpleTournament;

import java.sql.SQLException;

/**
 * In a ring tournament, one team fights with all other teams consecutively.
 */
public class LoopTournamentManager extends SimpleTournament {

    public LoopTournamentManager(Tournament tournament) {
        super(tournament);
    }

    @Override
    public void addGroup() {
        TGroup group = new LoopTournamentGroup(getTournament(), 0, 0);
        try {
            group.addTeams(TeamPool.getInstance().get(getTournament()));
        } catch (SQLException ex) {
            KendoLog.errorMessage(this.getClass().getName(), ex);
        }
        addGroup(group);
    }
}
