package com.softwaremagico.ktg.persistence;

import com.softwaremagico.ktg.core.RegisteredPerson;
import com.softwaremagico.ktg.tools.Tools;
import java.sql.SQLException;
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

    public List<RegisteredPerson> getByClub(String club) throws SQLException {
        List<RegisteredPerson> result = new ArrayList<>();
        for (RegisteredPerson element : getAll()) {
            if (element.getClub() != null && Tools.isSimilar(element.getClub().getName(), club)) {
                result.add(element);
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<RegisteredPerson> getById(String id) throws SQLException {
        List<RegisteredPerson> result = search(id);
        Collections.sort(result);
        return result;
    }

    public List<RegisteredPerson> getByName(String name) throws SQLException {
        List<RegisteredPerson> result = new ArrayList<>();
        for (RegisteredPerson element : getAll()) {
            if (element.getName() != null && Tools.isSimilar(element.getName(), name)) {
                result.add(element);
            }
        }
        Collections.sort(result);
        return result;
    }

    public List<RegisteredPerson> getBySurname(String surname) throws SQLException {
        List<RegisteredPerson> result = new ArrayList<>();
        for (RegisteredPerson element : getAll()) {
            if (element.getSurname() != null && Tools.isSimilar(element.getSurname(), surname)) {
                result.add(element);
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    protected HashMap<String, RegisteredPerson> getElementsFromDatabase() throws SQLException {
        if(!DatabaseConnection.getInstance().connect()){
            return null;
        }
        List<RegisteredPerson> people = DatabaseConnection.getInstance().getDatabase().getRegisteredPeople();
        DatabaseConnection.getInstance().disconnect();
        HashMap<String, RegisteredPerson> hashMap = new HashMap<>();
        for (RegisteredPerson person : people) {
            hashMap.put(getId(person), person);
        }
        return hashMap;
    }

    @Override
    protected String getId(RegisteredPerson element) {
        return element.getId();
    }

    public List<RegisteredPerson> getPeopleWithoutClub() throws SQLException {
        List<RegisteredPerson> result = new ArrayList<>();
        for (RegisteredPerson element : getAll()) {
            if (element.getClub() == null) {
                result.add(element);
            }
        }
        Collections.sort(result);
        return result;
    }

    /**
     * If a competitor is deleted, must delete the role.
     *
     * @param element
     * @return
     * @throws java.sql.SQLException
     */
    @Override
    public boolean remove(RegisteredPerson element) throws SQLException {
        RolePool.getInstance().remove(element);
        return super.remove(element);
    }

    @Override
    public void remove(String elementName) throws SQLException {
        remove(get(elementName));
    }

    @Override
    protected boolean removeElementsFromDatabase(List<RegisteredPerson> elementsToDelete) throws SQLException {
        if (elementsToDelete.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().removeRegisteredPeople(elementsToDelete);
        }
        return true;
    }

    @Override
    protected List<RegisteredPerson> sort() throws SQLException {
        List<RegisteredPerson> unsorted = new ArrayList<>(getMap().values());
        Collections.sort(unsorted);
        return unsorted;
    }

    @Override
    protected boolean storeElementsInDatabase(List<RegisteredPerson> elementsToStore) throws SQLException {
        if (elementsToStore.size() > 0) {
            return DatabaseConnection.getConnection().getDatabase().addRegisteredPeople(elementsToStore);
        }
        return true;
    }

    @Override
    protected boolean updateElements(HashMap<RegisteredPerson, RegisteredPerson> elementsToUpdate) throws SQLException {
        if (elementsToUpdate.size() > 0) {
            PhotoPool.getInstance().updateDatabase();
            return DatabaseConnection.getConnection().getDatabase().updateRegisteredPeople(elementsToUpdate);
        }
        return true;
    }

    public void updateId(RegisteredPerson oldPerson, String newId) throws SQLException {
        remove(oldPerson);
        oldPerson.setId(newId);
        add(oldPerson);
    }
}
