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

    public void initialize(Tournament tournament) throws SQLException {
        getMap(tournament).values();
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
    protected HashMap<String, Undraw> getElementsFromDatabase(Tournament tournament) throws SQLException {
        if (!DatabaseConnection.getInstance().connect()) {
            return null;
        }
        List<Undraw> undraws = DatabaseConnection.getConnection().getDatabase().getUndraws(tournament);
        DatabaseConnection.getInstance().disconnect();
        HashMap<String, Undraw> hashMap = new HashMap<>();
        for (Undraw undraw : undraws) {
            hashMap.put(getId(undraw), undraw);
        }
        return hashMap;
    }

    @Override
    protected String getId(Undraw element) {
        return element.getTournament().getName() + element.getLevel() + element.getGroupIndex() + element.getTeam();
    }

    private List<Undraw> getUndraws(Tournament tournament, Integer level, Integer groupIndex) throws SQLException {
        List<Undraw> undrawsOfGroup = new ArrayList<>();
        List<Undraw> undraws = new ArrayList<>(getMap(tournament).values());
        for (Undraw undraw : undraws) {
            if (undraw.getLevel().equals(level) && undraw.getGroupIndex().equals(groupIndex)) {
                undrawsOfGroup.add(undraw);
            }
        }
        return undrawsOfGroup;
    }

    public Integer getUndrawsWon(Tournament tournament, Integer level, Integer groupIndex, Team team)
            throws SQLException {
        Integer undrawsWon = 0;
        List<Undraw> undrawsOfGroup = getUndraws(tournament, level, groupIndex);
        for (Undraw undraw : undrawsOfGroup) {
            if (team.equals(undraw.getTeam())) {
                undrawsWon += undraw.getPoints();
            }
        }
        return undrawsWon;
    }

    public List<Team> getWinners(Tournament tournament, Integer level, Integer groupIndex) throws SQLException {
        List<Undraw> undrawsOfGroup = getUndraws(tournament, level, groupIndex);
        List<Team> teams = new ArrayList<>();
        for (Undraw undraw : undrawsOfGroup) {
            teams.add(undraw.getTeam());
        }
        return teams;
    }

    @Override
    protected boolean removeElementsFromDatabase(Tournament tournament, List<Undraw> elementsToDelete)
            throws SQLException {
        if (elementsToDelete.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().removeUndraws(elementsToDelete);
        }
        return true;
    }

    @Override
    protected List<Undraw> sort(Tournament tournament) throws SQLException {
        List<Undraw> unsorted = new ArrayList<>(getMap(tournament).values());
        Collections.sort(unsorted);
        return unsorted;
    }

    @Override
    protected boolean storeElementsInDatabase(Tournament tournament, List<Undraw> elementsToStore) throws SQLException {
        if (elementsToStore.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().addUndraws(elementsToStore);
        }
        return true;
    }

    @Override
    protected boolean updateElements(Tournament tournament, HashMap<Undraw, Undraw> elementsToUpdate)
            throws SQLException {
        if (elementsToUpdate.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().updateUndraws(elementsToUpdate);
        }
        return true;
    }
}
