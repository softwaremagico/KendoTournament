package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.Tournament;
import java.util.HashMap;
import java.util.List;

public class FightPool extends TournamentDependentPool<Fight> {

    private static FightPool instance;

    private FightPool() {
    }

    public static FightPool getInstance() {
        if (instance == null) {
            instance = new FightPool();
        }
        return instance;
    }

    @Override
    protected String getId(Fight element) {
        return element.getTeam1().getName() + "-" + element.getTeam2().getName() + ":"
                + element.getLevel();
    }

    @Override
    protected HashMap<String, Fight> getFromDatabase(Tournament tournament) {
        List<Fight> fights = DatabaseConnection.getConnection().getDatabase().getFights(tournament);
        HashMap<String, Fight> hashMap = new HashMap<>();
        for (Fight fight : fights) {
            hashMap.put(getId(fight), fight);
        }
        return hashMap;
    }

    @Override
    protected void storeInDatabase(Tournament tournament, List<Fight> elementsToStore) {
        DatabaseConnection.getConnection().getDatabase().addFights(elementsToStore);
    }

    @Override
    protected void removeFromDatabase(Tournament tournament, List<Fight> elementsToDelete) {
        DatabaseConnection.getConnection().getDatabase().removeFights(elementsToDelete);
    }

    @Override
    protected void updateDatabase(Tournament tournament, HashMap<Fight, Fight> elementsToUpdate) {
        DatabaseConnection.getConnection().getDatabase().updateFights(elementsToUpdate);
    }

    @Override
    protected List<Fight> sort(Tournament tournament) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
