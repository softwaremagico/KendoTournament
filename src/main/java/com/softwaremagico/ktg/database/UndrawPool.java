package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.Team;
import com.softwaremagico.ktg.Tournament;
import com.softwaremagico.ktg.Undraw;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class UndrawPool extends TournamentDependentPool<Undraw> {

    private static UndrawPool instance;

    private UndrawPool() {
    }

    public static UndrawPool getInstance() {
        if (instance == null) {
            instance = new UndrawPool();
        }
        return instance;
    }

    @Override
    protected String getId(Undraw element) {
        return element.getTournament().getName() + element.getLevel() + element.getGroupIndex() + element.getTeam();
    }

    @Override
    protected HashMap<String, Undraw> getFromDatabase(Tournament tournament) {
        List<Undraw> undraws = DatabaseConnection.getConnection().getDatabase().getUndraws(tournament);
        HashMap<String, Undraw> hashMap = new HashMap<>();
        for (Undraw undraw : undraws) {
            hashMap.put(getId(undraw), undraw);
        }
        return hashMap;
    }

    @Override
    protected void storeInDatabase(Tournament tournament, List<Undraw> elementsToStore) {
        DatabaseConnection.getConnection().getDatabase().addUndraws(elementsToStore);
    }

    @Override
    protected void removeFromDatabase(Tournament tournament, List<Undraw> elementsToDelete) {
        DatabaseConnection.getConnection().getDatabase().removeUndraws(elementsToDelete);
    }

    @Override
    protected void updateDatabase(Tournament tournament, HashMap<Undraw, Undraw> elementsToUpdate) {
        DatabaseConnection.getConnection().getDatabase().updateUndraws(elementsToUpdate);
    }

    @Override
    protected List<Undraw> sort(Tournament tournament) {
        List<Undraw> unsorted = new ArrayList(getMap(tournament).values());
        Collections.sort(unsorted);
        return unsorted;
    }

    public List<Team> getWinners(Tournament tournament, Integer level, Integer group) {
        List<Team> teams = new ArrayList<>();
        List<Undraw> undraws = new ArrayList<>(getMap(tournament).values());
        for (Undraw undraw : undraws) {
            if (undraw.getLevel() == level && undraw.getGroupIndex() == group) {
                teams.add(undraw.getTeam());
            }
        }
        return teams;
    }

    public Integer getUndrawsWon(Tournament tournament, Integer level, Integer group, Team team) {
        Integer undrawsWon = 0;
        List<Team> teams = getWinners(tournament, level, group);
        for (Team undrawTeam : teams) {
            if (team.equals(undrawTeam)) {
                undrawsWon++;
            }
        }
        return undrawsWon;
    }
}
