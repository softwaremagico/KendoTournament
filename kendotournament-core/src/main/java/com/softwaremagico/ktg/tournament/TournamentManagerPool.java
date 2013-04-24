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
    
    public static void removeManager(Tournament tournament){
        managers.remove(tournament);
    }

    private static ITournamentManager createManager(Tournament tournament) {
        ITournamentManager manager;
        switch (tournament.getType()) {
            case LOOP:
                manager = new LoopTournamentManager(tournament);
                break;
            case CHAMPIONSHIP:
                manager = new Championship(tournament);
                break;
            case MANUAL:
                manager = new ManualChampionship(tournament);
                break;
            case SIMPLE:
            default:
                manager = new SimpleTournamentManager(tournament);
                break;
        }
        return manager;
    }
}
