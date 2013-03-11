package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.RegisteredPerson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RegisteredPersonPool extends SimplePool<RegisteredPerson> {

    private static RegisteredPersonPool instance;

    private RegisteredPersonPool() {
    }

    public static RegisteredPersonPool getInstance() {
        if (instance == null) {
            instance = new RegisteredPersonPool();
        }
        return instance;
    }

    @Override
    protected String getId(RegisteredPerson element) {
        return element.getId();
    }

    @Override
    protected HashMap<String, RegisteredPerson> getFromDatabase() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void storeInDatabase(List<RegisteredPerson> elementsToStore) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void removeFromDatabase(List<RegisteredPerson> elementsToDelete) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void updateDatabase(HashMap<RegisteredPerson, RegisteredPerson> elementsToUpdate) {
        PhotoPool.getInstance().updateDatabase();
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected List<RegisteredPerson> sort() {
        List<RegisteredPerson> unsorted = new ArrayList(getMap().values());
        Collections.sort(unsorted);
        return unsorted;
    }

    public void updateId(RegisteredPerson oldPerson, String newId) {
        remove(oldPerson);
        oldPerson.setId(newId);
        add(oldPerson);
    }

    public List<RegisteredPerson> searchByName(String name) {
        List<RegisteredPerson> result = new ArrayList<>();
        for (RegisteredPerson element : getAll()) {
            if (element.getName().contains(name)) {
                result.add(element);
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<RegisteredPerson> searchBySurname(String surname) {
        List<RegisteredPerson> result = new ArrayList<>();
        for (RegisteredPerson element : getAll()) {
            if (element.getSurname().contains(surname)) {
                result.add(element);
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<RegisteredPerson> searchByClub(String club) {
        List<RegisteredPerson> result = new ArrayList<>();
        for (RegisteredPerson element : getAll()) {
            if (element.getClub().getName().contains(club)) {
                result.add(element);
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<RegisteredPerson> getPeopleWithoutClub() {
        List<RegisteredPerson> result = new ArrayList<>();
        for (RegisteredPerson element : getAll()) {
            if (element.getClub() == null) {
                result.add(element);
            }
        }
        Collections.sort(result);
        return result;
    }
}
