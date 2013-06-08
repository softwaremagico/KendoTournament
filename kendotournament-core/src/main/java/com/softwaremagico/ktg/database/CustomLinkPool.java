package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tournament.CustomWinnerLink;
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
    protected String getId(CustomWinnerLink element) {
        return element.getId();
    }

    @Override
    protected HashMap<String, CustomWinnerLink> getElementsFromDatabase(Tournament tournament) {
        DatabaseConnection.getInstance().connect();
        List<CustomWinnerLink> links = DatabaseConnection.getConnection().getDatabase().getCustomWinnerLinks(tournament);
        DatabaseConnection.getInstance().disconnect();
        HashMap<String, CustomWinnerLink> hashMap = new HashMap<>();
        for (CustomWinnerLink t : links) {
            hashMap.put(getId(t), t);
        }
        return hashMap;
    }

    @Override
    protected boolean storeElementsInDatabase(Tournament tournament, List<CustomWinnerLink> elementsToStore) {
        if (elementsToStore.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().addCustomWinnerLinks(elementsToStore);
        }
        return true;
    }

    @Override
    protected boolean removeElementsFromDatabase(Tournament tournament, List<CustomWinnerLink> elementsToDelete) {
        if (elementsToDelete.size() > 0) {
            //Remove by tournament
            List<Tournament> tournaments = new ArrayList<>();
            tournaments.add(tournament);
            return DatabaseConnection.getConnection().getDatabase().removeCustomWinnerLinks(tournaments);
        }
        return true;
    }

    @Override
    protected boolean updateElements(Tournament tournament, HashMap<CustomWinnerLink, CustomWinnerLink> elementsToUpdate) {
        if (elementsToUpdate.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().updateCustomWinnerLinks(elementsToUpdate);
        }
        return true;
    }

    @Override
    protected List<CustomWinnerLink> sort(Tournament tournament) {
        List<CustomWinnerLink> unsorted = new ArrayList(getMap(tournament).values());
        Collections.sort(unsorted);
        return unsorted;
    }
    
    public void remove(Tournament tournament, Integer source){
        List<CustomWinnerLink> links = get(tournament);
        List<CustomWinnerLink> linksToRemove = new ArrayList<>();
        for(CustomWinnerLink link : links){
            if(link.getSource() == source){
                linksToRemove.add(link);
            }
        }
        remove(tournament, linksToRemove);
    }
}
