package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.Club;
import com.softwaremagico.ktg.core.Tools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ClubPool extends SimplePool<Club> {
    
    private static ClubPool instance;
    
    private ClubPool() {
    }
    
    public static ClubPool getInstance() {
        if (instance == null) {
            instance = new ClubPool();
        }
        return instance;
    }
    
    @Override
    protected String getId(Club element) {
        return element.getName();
    }
    
    @Override
    protected HashMap<String, Club> getFromDatabase() {
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
    protected void updateDatabase(HashMap<Club, Club> elementsToUpdate) {
        DatabaseConnection.getConnection().getDatabase().updateClubs(elementsToUpdate);
    }
    
    @Override
    protected List<Club> sort() {
        List<Club> unsorted = new ArrayList(getMap().values());
        Collections.sort(unsorted);
        return unsorted;
    }

    /**
     * Obtain all elements that contains the desired string
     */
    public List<Club> getByName(String name) {
        List<Club> result = new ArrayList<>();
        for (Club element : getMap().values()) {
            if (Tools.isSimilar(element.getName(), name)) {
                result.add(element);
            }
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Obtain all elements that contains the desired string
     */
    public List<Club> getByCity(String city) {
        List<Club> result = new ArrayList<>();
        for (Club element : getMap().values()) {
            if (Tools.isSimilar(element.getCity(), city)) {
                result.add(element);
            }
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Obtain all elements that contains the desired string
     */
    public List<Club> getByCountry(String country) {
        List<Club> result = new ArrayList<>();
        for (Club element : getMap().values()) {
            if (Tools.isSimilar(element.getCountry(), country)) {
                result.add(element);
            }
        }
        Collections.sort(result);
        return result;
    }
}
