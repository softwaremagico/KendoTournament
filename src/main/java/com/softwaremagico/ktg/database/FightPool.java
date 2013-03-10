package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.Duel;
import com.softwaremagico.ktg.Fight;
import com.softwaremagico.ktg.Team;
import com.softwaremagico.ktg.Tournament;
import java.util.ArrayList;
import java.util.Collections;
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
        return element.getTeam1().getName() + element.getTeam2().getName()
                + element.getLevel() + element.getIndex() + element.getTournament();
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
        List<Fight> unsorted = new ArrayList(get(tournament).values());
        Collections.sort(unsorted);
        return unsorted;
    }

    @Override
    public void remove(Tournament tournament, Fight element) {
        //Delete duels.
        List<Duel> duels = DuelPool.getInstance().get(tournament, element);
        DuelPool.getInstance().remove(tournament, duels);
        //Delete fight.
        super.remove(tournament, element);
    }

    public List<Fight> get(Tournament tournament, Integer fightArea) {
        List<Fight> allFights = new ArrayList<>(get(tournament).values());
        List<Fight> fightsOfArea = new ArrayList<>();
        for (Fight fight : allFights) {
            if (fight.getAsignedFightArea() == fightArea) {
                fightsOfArea.add(fight);
            }
        }
        return fightsOfArea;
    }

    public Fight get(Tournament tournament, Team team1, Team team2, Integer level, Integer index) {
        for (Fight fight : get(tournament).values()) {
            if (fight.getTournament().equals(tournament) && fight.getTeam1().equals(team1) && fight.getTeam2().equals(team2)
                    && fight.getLevel() == level && fight.getIndex() == index) {
                return fight;
            }
        }
        return null;
    }

    public void setAsOver(Tournament tournament, Fight fight, boolean over) {
        fight.setOver(over);
        update(tournament, fight, fight);
    }

    public void remove(Tournament tournament, Integer minLevel) {
        List<Fight> allFights = new ArrayList<>(get(tournament).values());
        for (Fight fight : allFights) {
            if (fight.getLevel() >= minLevel) {
                remove(tournament, fight);
            }
        }
    }
}
