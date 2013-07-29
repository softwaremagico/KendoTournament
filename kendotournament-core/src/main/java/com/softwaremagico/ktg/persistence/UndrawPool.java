package com.softwaremagico.ktg.persistence;

import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.core.Undraw;
import java.sql.SQLException;
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
    protected HashMap<String, Undraw> getElementsFromDatabase(Tournament tournament) throws SQLException {
        DatabaseConnection.getInstance().connect();
        List<Undraw> undraws = DatabaseConnection.getConnection().getDatabase().getUndraws(tournament);
        DatabaseConnection.getInstance().disconnect();
        HashMap<String, Undraw> hashMap = new HashMap<>();
        for (Undraw undraw : undraws) {
            hashMap.put(getId(undraw), undraw);
        }
        return hashMap;
    }

    @Override
    protected boolean storeElementsInDatabase(Tournament tournament, List<Undraw> elementsToStore) throws SQLException {
        if (elementsToStore.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().addUndraws(elementsToStore);
        }
        return true;
    }

    @Override
    protected boolean removeElementsFromDatabase(Tournament tournament, List<Undraw> elementsToDelete) throws SQLException {
        if (elementsToDelete.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().removeUndraws(elementsToDelete);
        }
        return true;
    }

    @Override
    protected boolean updateElements(Tournament tournament, HashMap<Undraw, Undraw> elementsToUpdate) throws SQLException {
        if (elementsToUpdate.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().updateUndraws(elementsToUpdate);
        }
        return true;
    }

    @Override
    protected List<Undraw> sort(Tournament tournament) throws SQLException {
        List<Undraw> unsorted = new ArrayList(getMap(tournament).values());
        Collections.sort(unsorted);
        return unsorted;
    }

    private List<Undraw> getUndraws(Tournament tournament, Integer level, Integer group) throws SQLException {
        List<Undraw> undrawsOfGroup = new ArrayList<>();
        List<Undraw> undraws = new ArrayList<>(getMap(tournament).values());
        for (Undraw undraw : undraws) {
            if (undraw.getLevel() == level && undraw.getGroupIndex() == group) {
                undrawsOfGroup.add(undraw);
            }
        }
        return undrawsOfGroup;
    }

    public List<Team> getWinners(Tournament tournament, Integer level, Integer group) throws SQLException {
        List<Undraw> undrawsOfGroup = getUndraws(tournament, level, group);
        List<Team> teams = new ArrayList<>();
        for (Undraw undraw : undrawsOfGroup) {
            teams.add(undraw.getTeam());
        }
        return teams;
    }

    public Integer getUndrawsWon(Tournament tournament, Integer level, Integer group, Team team) throws SQLException {
        Integer undrawsWon = 0;
        List<Undraw> undrawsOfGroup = getUndraws(tournament, level, group);
        for (Undraw undraw : undrawsOfGroup) {
            if (team.equals(undraw.getTeam())) {
                undrawsWon += undraw.getPoints();
            }
        }
        return undrawsWon;
    }

    public Undraw get(Tournament tournament, Integer level, Integer group, Team team) throws SQLException {
        List<Undraw> undrawsOfGroup = getUndraws(tournament, level, group);
        for (Undraw undraw : undrawsOfGroup) {
            if (team.equals(undraw.getTeam())) {
                return undraw;
            }
        }
        return null;
    }

    @Override
    public boolean add(Tournament tournament, Undraw element) throws SQLException {
        Undraw undraw = getMap(tournament).get(getId(element));
        if (undraw != null) {
            undraw.setPoints(undraw.getPoints() + 1);
            return true;
        } else {
            return super.add(tournament, element);
        }
    }
}
