package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.Duel;
import com.softwaremagico.ktg.RegisteredPerson;
import com.softwaremagico.ktg.Tournament;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class DuelPool extends TournamentDependentPool<Duel> {

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
        List<Duel> unsorted = new ArrayList(get(tournament).values());
        Collections.sort(unsorted);
        return unsorted;
    }

    public List<Duel> get(Tournament tournament, RegisteredPerson competitor) {
        List<Duel> allDuels = new ArrayList<>(get(tournament).values());
        List<Duel> results = new ArrayList<>();
        for (Duel duel : allDuels) {
            if (duel.getFight().getTeam1().isMember(competitor)
                    || duel.getFight().getTeam2().isMember(competitor)) {
                results.add(duel);
            }
        }
        return results;
    }
}
