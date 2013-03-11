package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.Duel;
import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.RegisteredPerson;
import com.softwaremagico.ktg.Tournament;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DuelPool extends TournamentDependentPool<Duel> {

    private static DuelPool instance;
    private static HashMap<Fight, List<Duel>> duelsPerFight;

    private DuelPool() {
    }

    public static DuelPool getInstance() {
        if (instance == null) {
            instance = new DuelPool();
        }
        return instance;
    }

    @Override
    protected String getId(Duel element) {
        return element.getFight().getTeam1().getName() + element.getFight().getTeam2().getName() + element.getOrder()
                + element.getFight().getLevel() + element.getFight().getIndex() + element.getFight().getTournament();
    }

    @Override
    protected HashMap<String, Duel> getFromDatabase(Tournament tournament) {
        List<Duel> duels = DatabaseConnection.getConnection().getDatabase().getDuels(tournament);
        HashMap<String, Duel> hashMap = new HashMap<>();
        for (Duel duel : duels) {
            hashMap.put(getId(duel), duel);
        }
        return hashMap;
    }

    @Override
    protected void storeInDatabase(Tournament tournament, List<Duel> elementsToStore) {
        DatabaseConnection.getConnection().getDatabase().addDuels(elementsToStore);
    }

    @Override
    protected void removeFromDatabase(Tournament tournament, List<Duel> elementsToDelete) {
        DatabaseConnection.getConnection().getDatabase().removeDuels(elementsToDelete);
    }

    @Override
    protected void updateDatabase(Tournament tournament, HashMap<Duel, Duel> elementsToUpdate) {
        DatabaseConnection.getConnection().getDatabase().updateDuels(elementsToUpdate);
    }

    @Override
    protected List<Duel> sort(Tournament tournament) {
        List<Duel> unsorted = new ArrayList(getMap(tournament).values());
        Collections.sort(unsorted);
        return unsorted;
    }

    public List<Duel> get(Tournament tournament, RegisteredPerson competitor) {
        List<Duel> allDuels = new ArrayList<>(getMap(tournament).values());
        List<Duel> results = new ArrayList<>();
        for (Duel duel : allDuels) {
            if (duel.getFight().getTeam1().isMember(competitor)
                    || duel.getFight().getTeam2().isMember(competitor)) {
                results.add(duel);
            }
        }
        return results;
    }

    private List<Duel> createDuels(Tournament tournament, Fight fight) {
        List<Duel> duels = new ArrayList<>();
        try {
            for (int i = 0; i < tournament.getTeamSize(); i++) {
                Duel duel = new Duel(fight, i);
                duels.add(duel);
                //New duel not in database. Add it. 
                add(tournament, duel);
            }
        } catch (NullPointerException npe) {
        }
        return duels;
    }

    /**
     * Obtain the duels for a fight. If the fight is new, then create the new duels and enqueue it to store into the database.
     * @param tournament
     * @param fight
     * @return 
     */
    public List<Duel> get(Tournament tournament, Fight fight) {
        List<Duel> results = duelsPerFight.get(fight);
        if (results == null) {
            List<Duel> allDuels = new ArrayList<>(getMap(tournament).values());
            results = new ArrayList<>();
            for (Duel duel : allDuels) {
                if (duel.getFight().equals(fight)) {
                    results.add(duel);
                }
            }
            if (results.size() > 0) {
                Collections.sort(results);
            } else {
                results = createDuels(tournament, fight);
            }
            duelsPerFight.put(fight, results);
        }
        return results;
    }
}
