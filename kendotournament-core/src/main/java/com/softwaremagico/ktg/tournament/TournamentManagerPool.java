package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.Tournament;
import java.util.HashMap;

public class TournamentManagerPool {

    private static HashMap<Tournament, ITournamentManager> managers = new HashMap<>();

    public static ITournamentManager getManager(Tournament tournament) {
        if (tournament != null) {
            ITournamentManager manager = managers.get(tournament);
            if (manager == null) {
                manager = createManager(tournament);
                managers.put(tournament, manager);
            }
            return manager;
        }
        return null;
    }

    private static ITournamentManager createManager(Tournament tournament) {
        ITournamentManager manager;
        switch (tournament.getType()) {
            case CHAMPIONSHIP:
            case LEAGUE_TREE:
            case MANUAL:
            case SIMPLE:
            default:
                manager = new SimpleTournamentManager(tournament);
        }
        return manager;
    }
}
