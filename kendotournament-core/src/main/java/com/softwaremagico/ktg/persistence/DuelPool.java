package com.softwaremagico.ktg.persistence;

import com.softwaremagico.ktg.core.Duel;
import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.core.Tournament;
import java.sql.SQLException;
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

    private List<Duel> createDuels(Fight fight) throws SQLException {
        List<Duel> duels = new ArrayList<>();
        try {
            for (int i = 0; i < fight.getTournament().getTeamSize(); i++) {
                Duel duel = new Duel(fight, i);
                duels.add(duel);
                //New duel not in database. Add it. 
                add(fight.getTournament(), duel);
            }
        } catch (NullPointerException npe) {
        }
        return duels;
    }

    public List<Duel> get(RegisteredPerson competitor, boolean team1) throws SQLException {
        List<Duel> allDuels = getAll();
        List<Duel> results = new ArrayList<>();
        for (Duel duel : allDuels) {
            if (team1) {
                if (duel.getFight().getTeam1().getMember(duel.getOrder(), duel.getFight().getIndex()).equals(competitor)) {
                    results.add(duel);
                }
            } else {
                if (duel.getFight().getTeam2().getMember(duel.getOrder(), duel.getFight().getIndex()).equals(competitor)) {
                    results.add(duel);
                }
            }
        }
        return results;
    }

    /**
     * Obtain the duels for a fight. If the fight is new, then create the new
     * duels and enqueue it to store into the database.
     *
     * @param fight
     * @return
     * @throws java.sql.SQLException
     */
    public List<Duel> get(Fight fight) throws SQLException {
        if (duelsPerFight == null) {
            duelsPerFight = new HashMap<>();
        }
        List<Duel> results = duelsPerFight.get(fight);
        if (results == null) {
            List<Duel> allDuels = new ArrayList<>(getMap(fight.getTournament()).values());
            results = new ArrayList<>();
            for (Duel duel : allDuels) {
                if (duel.getFight().equals(fight)) {
                    results.add(duel);
                }
            }
            if (results.size() > 0) {
                Collections.sort(results);
            } else {
                results = createDuels(fight);
            }
            duelsPerFight.put(fight, results);
        }
        return results;
    }

    public List<Duel> get(Tournament tournament, RegisteredPerson competitor) throws SQLException {
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

    public List<Duel> get(Tournament tournament, RegisteredPerson competitor, boolean team1) throws SQLException {
        List<Duel> allDuels = new ArrayList<>(getMap(tournament).values());
        List<Duel> results = new ArrayList<>();
        for (Duel duel : allDuels) {
            if (team1) {
                if (duel.getFight().getTeam1().getMember(duel.getOrder(), duel.getFight().getIndex()).equals(competitor)) {
                    results.add(duel);
                }
            } else {
                if (duel.getFight().getTeam2().getMember(duel.getOrder(), duel.getFight().getIndex()).equals(competitor)) {
                    results.add(duel);
                }
            }
        }
        return results;
    }

    @Override
    protected HashMap<String, Duel> getElementsFromDatabase(Tournament tournament) throws SQLException {
        if (!DatabaseConnection.getInstance().connect()) {
            return null;
        }
        List<Duel> duels = DatabaseConnection.getConnection().getDatabase().getDuels(tournament);
        DatabaseConnection.getInstance().disconnect();
        HashMap<String, Duel> hashMap = new HashMap<>();
        for (Duel duel : duels) {
            hashMap.put(getId(duel), duel);
        }
        return hashMap;
    }

    @Override
    protected String getId(Duel element) {
        return element.hashCode() + "";
    }

    @Override
    public void remove(Tournament tournament) throws SQLException {
        for (Fight fight : FightPool.getInstance().get(tournament)) {
            fight.setOver(false);
            if (duelsPerFight != null) {
                duelsPerFight.remove(fight);
            }
        }
        super.remove(tournament);
    }

    @Override
    public boolean remove(Tournament tournament, Duel element) throws SQLException {
        element.getFight().setOver(false);
        if (duelsPerFight != null) {
            duelsPerFight.remove(element.getFight());
        }
        return super.remove(tournament, element);
    }

    @Override
    public boolean remove(Tournament tournament, List<Duel> elements) throws SQLException {
        for (Duel duel : elements) {
            duel.getFight().setOver(false);
            if (duelsPerFight != null) {
                duelsPerFight.remove(duel.getFight());
            }
        }
        return super.remove(tournament, elements);
    }

    @Override
    public boolean remove(Tournament tournament, String elementName) throws SQLException {
        get(tournament, elementName).getFight().setOver(false);
        if (duelsPerFight != null) {
            duelsPerFight.remove(get(tournament, elementName).getFight());
        }
        return super.remove(tournament, elementName);
    }

    @Override
    protected boolean removeElementsFromDatabase(Tournament tournament, List<Duel> elementsToDelete) throws SQLException {
        if (elementsToDelete.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().removeDuels(elementsToDelete);
        }
        return true;
    }

    @Override
    public void reset() {
        duelsPerFight = new HashMap<>();
        super.reset();
    }

    @Override
    public void reset(Tournament tournament) {
        duelsPerFight = new HashMap<>();
        super.reset(tournament);
    }

    @Override
    protected List<Duel> sort(Tournament tournament) throws SQLException {
        List<Duel> unsorted = new ArrayList<>(getMap(tournament).values());
        Collections.sort(unsorted);
        return unsorted;
    }

    @Override
    protected boolean storeElementsInDatabase(Tournament tournament, List<Duel> elementsToStore) throws SQLException {
        if (elementsToStore.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().addDuels(elementsToStore);
        }
        return true;
    }

    @Override
    protected boolean updateElements(Tournament tournament, HashMap<Duel, Duel> elementsToUpdate) throws SQLException {
        if (elementsToUpdate.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().updateDuels(elementsToUpdate);
        }
        return true;
    }

    @Override
    public void clearCache() {
        super.clearCache();
        duelsPerFight = new HashMap<>();
    }
}
