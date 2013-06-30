package com.softwaremagico.ktg.tournament;

import com.softwaremagico.ktg.core.KendoLog;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.database.FightPool;
import java.sql.SQLException;
import java.util.HashMap;

public class TournamentManagerFactory {
    
    private static HashMap<Tournament, HashMap<TournamentType, ITournamentManager>> managers = new HashMap<>();

    /**
     * Get the manager of a tournament for a specific type. If it has other
     * tournament type, it will create a new manager.
     *
     * @param tournament
     * @param types
     * @return
     */
    public static ITournamentManager getManager(Tournament tournament, TournamentType type) {
        if (tournament != null) {
            HashMap<TournamentType, ITournamentManager> managersOfTournament = getManagers(tournament);
            ITournamentManager manager = managersOfTournament.get(type);
            if (manager == null) {
                manager = createManager(tournament, type);
                managersOfTournament.put(type, manager);
            }
            return manager;
        }
        return null;
    }
    
    private static HashMap<TournamentType, ITournamentManager> getManagers(Tournament tournament) {
        if (tournament != null) {
            HashMap<TournamentType, ITournamentManager> managersOfTournament = managers.get(tournament);
            if (managersOfTournament == null) {
                managersOfTournament = new HashMap<>();
                managers.put(tournament, managersOfTournament);
            }
            return managersOfTournament;
        }
        return null;
    }
    
    public static ITournamentManager getManager(Tournament tournament) {
        return getManager(tournament, tournament.getType());
    }
    
    private static void removeManager(Tournament tournament, TournamentType type) {
        managers.get(tournament).remove(type);
        try {
            FightPool.getInstance().remove(tournament);
        } catch (SQLException ex) {
            KendoLog.errorMessage(TournamentManagerFactory.class.getName(), ex);
        }
    }
    
    public static void removeManager(Tournament tournament) {
        removeManager(tournament, tournament.getType());
    }
    
    public static void resetManager(Tournament tournament){
        managers.get(tournament).remove(tournament.getType());
    }
    
    private static ITournamentManager createManager(Tournament tournament, TournamentType type) {
        ITournamentManager manager;
        switch (type) {
            case LOOP:
                manager = new LoopTournamentManager(tournament);
                break;
            case CHAMPIONSHIP:
                manager = new Championship(tournament);
                manager.fillGroups();
                break;
            case MANUAL:
                manager = new CustomChampionship(tournament);
                manager.fillGroups();
                break;
            case SIMPLE:
            default:
                manager = new SimpleTournamentManager(tournament);
                break;
        }
        return manager;
    }
}
