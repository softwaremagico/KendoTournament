package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.Club;
import com.softwaremagico.ktg.Tournament;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ClubPool extends TournamentDependentPool<Club> {

    @Override
    protected String getId(Club element) {
        return element.getName();
    }

    @Override
    protected HashMap<String, Club> getFromDatabase(Tournament tournament) {
        List<Club> clubs = DatabaseConnection.getConnection().getDatabase().getClubs();
        HashMap<String, Club> hashMap = new HashMap<>();
        for (Club c : clubs) {
            hashMap.put(getId(c), c);
        }
        return hashMap;
    }

    @Override
    protected void storeInDatabase(List<Club> elementsToStore) {
        DatabaseConnection.getConnection().getDatabase().addClubs(elementsToStore);
    }

    @Override
    protected void removeFromDatabase(List<Club> elementsToDelete) {
        DatabaseConnection.getConnection().getDatabase().removeClubs(elementsToDelete);
    }

    @Override
    protected void updateDatabase(Tournament tournament, HashMap<Club, Club> elementsToUpdate) {
        DatabaseConnection.getConnection().getDatabase().updateClubs(elementsToUpdate);
    }

    @Override
    protected List<Club> sort(Tournament tournament) {
        List<Club> unsorted = new ArrayList(get(tournament).values());
        Collections.sort(unsorted);
        return unsorted;
    }

    /**
     * Obtain all elements that contains the desired string
     */
    public List<Club> searchClubByCity(Tournament tournament, String city) {
        List<Club> result = new ArrayList<>();
        for (Club element : get(tournament).values()) {
            if (element.getCity().contains(city)) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * Obtain all elements that contains the desired string
     */
    public List<Club> searchClubByCountry(Tournament tournament, String country) {
        List<Club> result = new ArrayList<>();
        for (Club element : get(tournament).values()) {
            if (element.getCountry().contains(country)) {
                result.add(element);
            }
        }
        return result;
    }
}
