package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.tools.Tools;
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
        DatabaseConnection.getInstance().connect();
        List<RegisteredPerson> people = DatabaseConnection.getInstance().getDatabase().getRegisteredPeople();
        DatabaseConnection.getInstance().disconnect();
        HashMap<String, RegisteredPerson> hashMap = new HashMap<>();
        for (RegisteredPerson person : people) {
            hashMap.put(getId(person), person);
        }
        return hashMap;
    }

    @Override
    protected boolean storeInDatabase(List<RegisteredPerson> elementsToStore) {
        if (elementsToStore.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().addRegisteredPeople(elementsToStore);
        }
        return true;
    }

    @Override
    protected boolean removeFromDatabase(List<RegisteredPerson> elementsToDelete) {
        if (elementsToDelete.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().removeRegisteredPeople(elementsToDelete);
        }
        return true;
    }

    @Override
    protected boolean updateDatabase(HashMap<RegisteredPerson, RegisteredPerson> elementsToUpdate) {
        if (elementsToUpdate.size() > 0) {
            PhotoPool.getInstance().updateDatabase();
            return DatabaseConnection.getConnection().getDatabase().updateRegisteredPeople(elementsToUpdate);
        }
        return true;
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

    public List<RegisteredPerson> getById(String id) {
        List<RegisteredPerson> result = search(id);
        Collections.sort(result);
        return result;
    }

    public List<RegisteredPerson> getByName(String name) {
        List<RegisteredPerson> result = new ArrayList<>();
        for (RegisteredPerson element : getAll()) {
            if (element.getName() != null && Tools.isSimilar(element.getName(), name)) {
                result.add(element);
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<RegisteredPerson> getBySurname(String surname) {
        List<RegisteredPerson> result = new ArrayList<>();
        for (RegisteredPerson element : getAll()) {
            if (element.getSurname() != null && Tools.isSimilar(element.getSurname(), surname)) {
                result.add(element);
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<RegisteredPerson> getByClub(String club) {
        List<RegisteredPerson> result = new ArrayList<>();
        for (RegisteredPerson element : getAll()) {
            if (element.getClub() != null && Tools.isSimilar(element.getClub().getName(), club)) {
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
