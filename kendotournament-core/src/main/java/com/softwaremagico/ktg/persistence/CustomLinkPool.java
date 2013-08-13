package com.softwaremagico.ktg.persistence;

import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.CustomWinnerLink;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CustomLinkPool extends TournamentDependentPool<CustomWinnerLink> {

    private static CustomLinkPool instance;

    private CustomLinkPool() {
    }

    public static CustomLinkPool getInstance() {
        if (instance == null) {
            instance = new CustomLinkPool();
        }
        return instance;
    }

    @Override
    protected HashMap<String, CustomWinnerLink> getElementsFromDatabase(Tournament tournament) throws SQLException {
        if (!DatabaseConnection.getInstance().connect()) {
            return null;
        }
        List<CustomWinnerLink> links = DatabaseConnection.getConnection().getDatabase().getCustomWinnerLinks(tournament);
        DatabaseConnection.getInstance().disconnect();
        HashMap<String, CustomWinnerLink> hashMap = new HashMap<>();
        if (links != null) {
            for (CustomWinnerLink t : links) {
                hashMap.put(getId(t), t);
            }
        }
        return hashMap;
    }

    @Override
    protected String getId(CustomWinnerLink element) {
        return element.getId();
    }

    public void remove(Tournament tournament, Integer source) throws SQLException {
        List<CustomWinnerLink> links = get(tournament);
        List<CustomWinnerLink> linksToRemove = new ArrayList<>();
        for (CustomWinnerLink link : links) {
            if (link.getSource() == source) {
                linksToRemove.add(link);
            }
        }
        remove(tournament, linksToRemove);
    }

    @Override
    protected boolean removeElementsFromDatabase(Tournament tournament, List<CustomWinnerLink> elementsToDelete) throws SQLException {
        if (elementsToDelete.size() > 0) {
            //Remove by tournament
            List<Tournament> tournaments = new ArrayList<>();
            tournaments.add(tournament);
            return DatabaseConnection.getConnection().getDatabase().removeCustomWinnerLinks(tournaments);
        }
        return true;
    }

    @Override
    protected List<CustomWinnerLink> sort(Tournament tournament) throws SQLException {
        List<CustomWinnerLink> unsorted = new ArrayList<CustomWinnerLink>(getMap(tournament).values());
        Collections.sort(unsorted);
        return unsorted;
    }

    @Override
    protected boolean storeElementsInDatabase(Tournament tournament, List<CustomWinnerLink> elementsToStore) throws SQLException {
        if (elementsToStore.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().addCustomWinnerLinks(elementsToStore);
        }
        return true;
    }

    @Override
    protected boolean updateElements(Tournament tournament, HashMap<CustomWinnerLink, CustomWinnerLink> elementsToUpdate) throws SQLException {
        if (elementsToUpdate.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().updateCustomWinnerLinks(elementsToUpdate);
        }
        return true;
    }
}
