package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.Duel;
import com.softwaremagico.ktg.core.Fight;
import com.softwaremagico.ktg.core.Team;
import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.core.Undraw;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        return element.getTeam1().getName() + element.getTeam2().getName()
                + element.getLevel() + element.getGroupIndex() + element.getTournament();
    }

    @Override
    protected HashMap<String, Fight> getElementsFromDatabase(Tournament tournament) throws SQLException {
        DatabaseConnection.getInstance().connect();
        List<Fight> fights = DatabaseConnection.getConnection().getDatabase().getFights(tournament);
        DatabaseConnection.getInstance().disconnect();
        HashMap<String, Fight> hashMap = new HashMap<>();
        for (Fight fight : fights) {
            hashMap.put(getId(fight), fight);
        }
        return hashMap;
    }

    @Override
    protected boolean storeElementsInDatabase(Tournament tournament, List<Fight> elementsToStore) throws SQLException {
        if (elementsToStore.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().addFights(elementsToStore);
        }
        return true;
    }

    @Override
    protected boolean removeElementsFromDatabase(Tournament tournament, List<Fight> elementsToDelete) throws SQLException {
        if (elementsToDelete.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().removeFights(elementsToDelete);
        }
        return true;
    }

    @Override
    protected boolean updateElements(Tournament tournament, HashMap<Fight, Fight> elementsToUpdate) throws SQLException {
        if (elementsToUpdate.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().updateFights(elementsToUpdate);
        }
        return true;
    }

    @Override
    protected List<Fight> sort(Tournament tournament) throws SQLException {
        List<Fight> unsorted = new ArrayList(getMap(tournament).values());
        Collections.sort(unsorted);
        return unsorted;
    }

    @Override
    public boolean remove(Tournament tournament, Fight element) throws SQLException {
        //Delete duels.
        List<Duel> duels = DuelPool.getInstance().get(tournament, element);
        DuelPool.getInstance().remove(tournament, duels);

        //Delete undraws where this fights is involved.
        Undraw undraw = UndrawPool.getInstance().get(tournament, element.getLevel(), element.getGroupIndex(), element.getTeam1());
        if (undraw != null) {
            UndrawPool.getInstance().remove(tournament, undraw);
        }
        undraw = UndrawPool.getInstance().get(tournament, element.getLevel(), element.getGroupIndex(), element.getTeam2());
        if (undraw != null) {
            UndrawPool.getInstance().remove(tournament, undraw);
        }

        //Delete fight.
        super.remove(tournament, element);
        return true;
    }

    @Override
    public void remove(Tournament tournament) throws SQLException {
        DuelPool.getInstance().remove(tournament);
        UndrawPool.getInstance().remove(tournament);
        super.remove(tournament);
    }

    @Override
    public boolean remove(Tournament tournament, List<Fight> elements) throws SQLException {
        for (Fight element : elements) {
            remove(tournament, element);
        }
        return true;
    }

    @Override
    public boolean remove(Tournament tournament, String elementName) throws SQLException {
        return remove(tournament, get(tournament, elementName));
    }

    @Override
    public void reset() {
        super.reset();
        DuelPool.getInstance().reset();
    }

    public List<Fight> get(Tournament tournament, Integer fightArea) throws SQLException {
        List<Fight> allFights = new ArrayList<>(getMap(tournament).values());
        List<Fight> fightsOfArea = new ArrayList<>();
        for (Fight fight : allFights) {
            if (fight.getAsignedFightArea() == fightArea) {
                fightsOfArea.add(fight);
            }
        }
        Collections.sort(fightsOfArea);
        return fightsOfArea;
    }

    public Fight getFightFromCurrent(Tournament tournament, Integer fightArea, Integer fightIndex) throws SQLException {
        List<Fight> allFights = new ArrayList<>(getMap(tournament).values());
        List<Fight> fightsOfArea = new ArrayList<>();
        for (Fight fight : allFights) {
            if (fight.getAsignedFightArea() == fightArea) {
                fightsOfArea.add(fight);
            }
        }

        Collections.sort(fightsOfArea);

        Integer index = getCurrentFightIndex(tournament, fightArea) + fightIndex;
        if (index >= 0 && index < fightsOfArea.size()) {
            return fightsOfArea.get(index);
        }
        return null;
    }

    public Fight get(Tournament tournament, Integer fightArea, Integer index) throws SQLException {
        List<Fight> arenaFights = get(tournament, fightArea);
        if (index >= 0 && index < arenaFights.size()) {
            return arenaFights.get(index);
        }
        return null;
    }

    public Fight get(Tournament tournament, Team team1, Team team2, Integer level, Integer index) throws SQLException {
        for (Fight fight : getMap(tournament).values()) {
            if (fight.getGroupIndex() == index && fight.getTournament().equals(tournament) && fight.getTeam1().equals(team1) && fight.getTeam2().equals(team2)
                    && fight.getLevel() == level) {
                return fight;
            }
        }
        return null;
    }

    public void setAsOver(Tournament tournament, Fight fight, boolean over) throws SQLException {
        fight.setOver(over);
        update(tournament, fight, fight);
    }

    public void remove(Tournament tournament, Integer minLevel) throws SQLException {
        List<Fight> allFights = new ArrayList<>(getMap(tournament).values());
        for (Fight fight : allFights) {
            if (fight.getLevel() >= minLevel) {
                remove(tournament, fight);
            }
        }
    }

    public Integer getFightIndex(Tournament tournament, Fight fight, Integer fightArea) throws SQLException {
        List<Fight> fightsOfArena = get(tournament, fightArea);
        for (Integer i = 0; i < fightsOfArena.size(); i++) {
            if (fightsOfArena.get(i).equals(fight)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Get the first fight not over.
     *
     * @param tournament
     * @param fightArea
     * @return Null if all fights are over.
     */
    public Fight getCurrentFight(Tournament tournament, Integer fightArea) throws SQLException {
        List<Fight> fightsOfArena = get(tournament, fightArea);
        if (fightsOfArena == null || fightsOfArena.isEmpty()) {
            return null;
        }
        for (Integer i = 0; i < fightsOfArena.size(); i++) {
            if (!fightsOfArena.get(i).isOver()) {
                return fightsOfArena.get(i);
            }
        }
        return null;
    }

    /**
     * Get the first fight not over.
     *
     * @param tournament
     * @param fightArea
     * @return Null if all fights are over.
     */
    public Integer getCurrentFightIndex(Tournament tournament, Integer fightArea) throws SQLException {
        List<Fight> fightsOfArena = get(tournament, fightArea);
        if (fightsOfArena == null || fightsOfArena.isEmpty()) {
            return null;
        }
        for (Integer i = 0; i < fightsOfArena.size(); i++) {
            if (!fightsOfArena.get(i).isOver()) {
                return i;
            }
        }
        return null;
    }

    public Integer getLastLevelUsed(Tournament tournament) throws SQLException {
        Fight fight = getCurrentFight(tournament, 0);
        if (fight == null) {
            return 0;
        }
        return fight.getLevel();
    }

    public Integer getMaxLevel(Tournament tournament) throws SQLException {
        Integer maxLevel = 0;
        List<Fight> fights = get(tournament);
        for (Fight fight : fights) {
            if (fight.getLevel() > maxLevel) {
                maxLevel = fight.getLevel();
            }
        }
        return maxLevel;
    }

    public List<Fight> getFromLevel(Tournament tournament, Integer level) throws SQLException {
        List<Fight> allFights = new ArrayList<>(getMap(tournament).values());
        List<Fight> fightsOfLevel = new ArrayList<>();
        for (Fight fight : allFights) {
            if (fight.getLevel() == level) {
                fightsOfLevel.add(fight);
            }
        }
        Collections.sort(fightsOfLevel);
        return fightsOfLevel;
    }

    public boolean areAllOver(Tournament tournament) throws SQLException {
        List<Fight> fights = get(tournament);
        for (Fight fight : fights) {
            if (!fight.isOver()) {
                return false;
            }
        }
        return true;
    }

    public boolean areAllOver(Tournament tournament, Integer arena) throws SQLException {
        List<Fight> fights = get(tournament);
        for (Fight fight : fights) {
            if (!fight.isOver() && fight.getAsignedFightArea().equals(arena)) {
                return false;
            }
        }
        return true;
    }

    public static boolean existRepeatedFight(List<Fight> fights) {
        Set<Fight> set = new HashSet<>(fights);

        if (set.size() < fights.size()) {
            return true;
        }
        return false;
    }
}
