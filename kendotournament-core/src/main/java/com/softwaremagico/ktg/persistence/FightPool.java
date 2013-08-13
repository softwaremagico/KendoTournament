package com.softwaremagico.ktg.persistence;

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
    private Integer currentFight = null;
    private HashMap<Tournament, HashMap<Integer, List<Fight>>> figthsPerArea;

    private FightPool() {
        figthsPerArea = new HashMap<>();
    }

    public static boolean existRepeatedFight(List<Fight> fights) {
        Set<Fight> set = new HashSet<>(fights);

        if (set.size() < fights.size()) {
            return true;
        }
        return false;
    }

    public static FightPool getInstance() {
        if (instance == null) {
            instance = new FightPool();
        }
        return instance;
    }

    @Override
    public boolean add(Tournament tournament, Fight fight) throws SQLException {
        resetAuxiliaryParameters(tournament);
        return super.add(tournament, fight);
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

    public List<Fight> get(Tournament tournament, int fightArea) throws SQLException {
        List<Fight> fightsArea = getFightsPerArena(tournament, fightArea);
        if (fightsArea != null) {
            return fightsArea;
        }

        List<Fight> allFights = new ArrayList<>(getMap(tournament).values());
        List<Fight> fightsOfArea = new ArrayList<>();
        for (Fight fight : allFights) {
            if (fight.getAsignedFightArea() == fightArea) {
                fightsOfArea.add(fight);
            }
        }
        Collections.sort(fightsOfArea);
        figthsPerArea.get(tournament).put(fightArea, fightsOfArea);
        return fightsOfArea;
    }

    public Fight get(Tournament tournament, Integer fightArea, Integer index) throws SQLException {
        List<Fight> arenaFights = get(tournament, fightArea);
        if (index >= 0 && index < arenaFights.size()) {
            return arenaFights.get(index);
        }
        return null;
    }

    public Fight get(Tournament tournament, Team team1, Team team2, Integer level, Integer group, Integer index)
            throws SQLException {
        for (Fight fight : getMap(tournament).values()) {
            if (fight.getGroup() == group && fight.getGroupIndex() == index && fight.getTournament().equals(tournament)
                    && fight.getTeam1().equals(team1) && fight.getTeam2().equals(team2) && fight.getLevel() == level) {
                return fight;
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
    public Integer getCurrentFightIndex(Tournament tournament) throws SQLException {
        List<Fight> fightsOfTournament = get(tournament);
        if (fightsOfTournament == null || fightsOfTournament.isEmpty()) {
            return null;
        }
        if (currentFight != null && !fightsOfTournament.get(currentFight).isOver()) {
            return currentFight;
        }
        for (Integer i = 0; i < fightsOfTournament.size(); i++) {
            if (!fightsOfTournament.get(i).isOver()) {
                currentFight = i;
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

    @Override
    protected HashMap<String, Fight> getElementsFromDatabase(Tournament tournament) throws SQLException {
        if (!DatabaseConnection.getInstance().connect()) {
            return null;
        }
        List<Fight> fights = DatabaseConnection.getConnection().getDatabase().getFights(tournament);
        DatabaseConnection.getInstance().disconnect();
        HashMap<String, Fight> hashMap = new HashMap<>();
        for (Fight fight : fights) {
            hashMap.put(getId(fight), fight);
        }
        return hashMap;
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

    public int getFightIndex(Fight fight) throws SQLException {
        List<Fight> fightsOfTournament = get(fight.getTournament());
        return fightsOfTournament.indexOf(fight);
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

    private List<Fight> getFightsPerArena(Tournament tournament, int fightArea) {
        if (figthsPerArea.get(tournament) == null) {
            figthsPerArea.put(tournament, new HashMap<Integer, List<Fight>>());
        }
        return figthsPerArea.get(tournament).get(fightArea);
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

    @Override
    protected String getId(Fight element) {
        return element.hashCode() + "";
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

    @Override
    public void remove(Tournament tournament) throws SQLException {
        DuelPool.getInstance().remove(tournament);
        UndrawPool.getInstance().remove(tournament);
        resetAuxiliaryParameters(tournament);
        super.remove(tournament);
    }

    @Override
    public boolean remove(Tournament tournament, Fight element) throws SQLException {
        // Delete duels.
        List<Duel> duels = DuelPool.getInstance().get(tournament, element);
        DuelPool.getInstance().remove(tournament, duels);

        // Delete undraws where this fights is involved.
        Undraw undraw = UndrawPool.getInstance().get(tournament, element.getLevel(), element.getGroup(),
                element.getTeam1());
        if (undraw != null) {
            UndrawPool.getInstance().remove(tournament, undraw);
        }
        undraw = UndrawPool.getInstance().get(tournament, element.getLevel(), element.getGroup(), element.getTeam2());
        if (undraw != null) {
            UndrawPool.getInstance().remove(tournament, undraw);
        }

        resetAuxiliaryParameters(tournament);

        // Delete fight.
        super.remove(tournament, element);
        return true;
    }

    public void remove(Tournament tournament, Integer minLevel) throws SQLException {
        List<Fight> allFights = new ArrayList<>(getMap(tournament).values());
        for (Fight fight : allFights) {
            if (fight.getLevel() >= minLevel) {
                remove(tournament, fight);
            }
        }
    }

    @Override
    public boolean remove(Tournament tournament, List<Fight> elements) throws SQLException {
        for (Fight element : elements) {
            remove(tournament, element);
        }
        resetAuxiliaryParameters(tournament);
        return true;
    }

    @Override
    public boolean remove(Tournament tournament, String elementName) throws SQLException {
        resetAuxiliaryParameters(tournament);
        return remove(tournament, get(tournament, elementName));
    }

    @Override
    protected boolean removeElementsFromDatabase(Tournament tournament, List<Fight> elementsToDelete)
            throws SQLException {
        if (elementsToDelete.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().removeFights(elementsToDelete);
        }
        return true;
    }

    @Override
    public void reset() {
        resetAuxiliaryParameters();
        super.reset();
        DuelPool.getInstance().reset();
    }

    @Override
    public void reset(Tournament tournament) {
        resetAuxiliaryParameters(tournament);
        super.reset(tournament);
        DuelPool.getInstance().reset(tournament);
    }

    public void setAsOver(Tournament tournament, Fight fight, boolean over) throws SQLException {
        fight.setOver(over);
        update(tournament, fight, fight);
    }

    @Override
    protected List<Fight> sort(Tournament tournament) throws SQLException {
        List<Fight> unsorted = new ArrayList<>(getMap(tournament).values());
        Collections.sort(unsorted);
        return unsorted;
    }

    @Override
    protected boolean storeElementsInDatabase(Tournament tournament, List<Fight> elementsToStore) throws SQLException {
        if (elementsToStore.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().addFights(elementsToStore);
        }
        return true;
    }

    @Override
    public boolean update(Tournament tournament, Fight oldElement, Fight newElement) throws SQLException {
        // Delete fights group per area.
        resetAuxiliaryParameters(tournament);
        return super.update(tournament, oldElement, newElement);
    }

    @Override
    protected boolean updateElements(Tournament tournament, HashMap<Fight, Fight> elementsToUpdate) throws SQLException {
        if (elementsToUpdate.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().updateFights(elementsToUpdate);
        }
        return true;
    }

    /**
     * Reset auxiliary lists and variables used to speed up some methods.
     *
     * @param tournament
     */
    private void resetAuxiliaryParameters(Tournament tournament) {
        figthsPerArea.put(tournament, null);
        currentFight = null;
    }

    /**
     * Reset auxiliary lists and variables used to speed up some methods.
     *
     * @param tournament
     */
    private void resetAuxiliaryParameters() {
        figthsPerArea = new HashMap<>();
        currentFight = null;
    }
}
