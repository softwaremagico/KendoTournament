package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Tools;
import com.softwaremagico.ktg.core.Tournament;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TournamentPool extends SimplePool<Tournament> {

    private static TournamentPool instance;

    private TournamentPool() {
    }

    public static TournamentPool getInstance() {
        if (instance == null) {
            instance = new TournamentPool();
        }
        return instance;
    }

    @Override
    protected String getId(Tournament element) {
        return element.getName();
    }

    @Override
    protected HashMap<String, Tournament> getFromDatabase() {
        DatabaseConnection.getInstance().connect();
        List<Tournament> tournaments = DatabaseConnection.getInstance().getDatabase().getTournaments();
        DatabaseConnection.getInstance().disconnect();
        HashMap<String, Tournament> hashMap = new HashMap<>();
        for (Tournament t : tournaments) {
            hashMap.put(getId(t), t);
        }
        return hashMap;
    }

    @Override
    protected void storeInDatabase(List<Tournament> elementsToStore) {
        if (elementsToStore.size() > 0) {
            DatabaseConnection.getConnection().getDatabase().addTournaments(elementsToStore);
        }
    }

    @Override
    protected void removeFromDatabase(List<Tournament> elementsToDelete) {
        if (elementsToDelete.size() > 0) {
            for (Tournament tournament : elementsToDelete) {
                //Delete fights.
                FightPool.getInstance().remove(tournament);
                //Delete teams.
                TeamPool.getInstance().remove(tournament);
                //Delete roles.            
                RolePool.getInstance().remove(tournament);
            }
            DatabaseConnection.getConnection().getDatabase().removeTournaments(elementsToDelete);
        }
    }

    @Override
    protected void updateDatabase(HashMap<Tournament, Tournament> elementsToUpdate) {
        if (elementsToUpdate.size() > 0) {
            DatabaseConnection.getConnection().getDatabase().updateTournaments(elementsToUpdate);
        }
    }

    @Override
    protected List<Tournament> sort() {
        List<Tournament> unsorted = new ArrayList(getMap().values());
        Collections.sort(unsorted);
        return unsorted;
    }

    public List<Tournament> getByName(String name) {
        List<Tournament> result = new ArrayList<>();
        for (Tournament element : getAll()) {
            if (Tools.isSimilar(element.getName(), name)) {
                result.add(element);
            }
        }
        Collections.sort(result);
        return result;
    }

    public Integer getLevelTournament(Tournament tournament) {
        int level = -1;
        for (Fight fight : FightPool.getInstance().getMap(tournament).values()) {
            if (fight.getLevel() > level) {
                level = fight.getLevel();
            }
        }
        return level;
    }
}
