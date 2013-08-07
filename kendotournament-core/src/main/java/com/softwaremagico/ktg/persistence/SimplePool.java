package com.softwaremagico.ktg.persistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class SimplePool<ElementPool> {

    private HashMap<String, ElementPool> elements = null;
    private List<ElementPool> sortedElements = null;
    private HashMap<String, ElementPool> elementsToStore = new HashMap<>();
    private HashMap<String, ElementPool> elementsToDelete = new HashMap<>();
    private HashMap<ElementPool, ElementPool> elementsToUpdate = new HashMap<>(); //New element replace old one.

    protected abstract String getId(ElementPool element);

    protected abstract HashMap<String, ElementPool> getElementsFromDatabase() throws SQLException;

    protected abstract boolean storeElementsInDatabase(List<ElementPool> elementsToStore) throws SQLException;

    protected abstract boolean removeElementsFromDatabase(List<ElementPool> elementsToDelete) throws SQLException;

    protected abstract boolean updateElements(HashMap<ElementPool, ElementPool> elementsToUpdate) throws SQLException;

    protected HashMap<String, ElementPool> getMap() throws SQLException {
        if (elements == null) {
            elements = getElementsFromDatabase();
        }
        return elements;
    }

    private HashMap<String, ElementPool> getElementToStore() {
        if (elementsToStore == null) {
            elementsToStore = new HashMap<>();
        }
        return elementsToStore;
    }

    private HashMap<String, ElementPool> getElementToRemove() {
        if (elementsToDelete == null) {
            elementsToDelete = new HashMap<>();
        }
        return elementsToDelete;
    }

    private HashMap<ElementPool, ElementPool> getElementToUpdate() {
        if (elementsToUpdate == null) {
            elementsToUpdate = new HashMap<>();
        }
        return elementsToUpdate;
    }

    private void addElementToStore(ElementPool element) {
        getElementToStore().put(getId(element), element);
    }

    private void addElementToUpdate(ElementPool oldElement, ElementPool newElement) {
        //Can be an update that older update. Then must be only the last update and the old database element to replace.
        ElementPool element = getElementToUpdate().get(oldElement); //Here, oldElement is the previous newElement.
        if (element != null) {
            //Exist an older update. Must be replaced with the correct information that exist in database.
            getElementToUpdate().remove(oldElement);
            getElementToUpdate().put(newElement, element);
        } else {
            getElementToUpdate().put(newElement, oldElement);
        }
    }

    private void addElementToRemove(ElementPool element) {
        getElementToRemove().put(getId(element), element);
    }

    public ElementPool get(String elementName) throws SQLException {
        return (ElementPool) getMap().get(elementName);
    }

    protected List<ElementPool> getAll() throws SQLException {
        List<ElementPool> results = new ArrayList<>();
        results.addAll(getMap().values());
        return results;
    }

    public boolean add(ElementPool element) throws SQLException {
        if (!getMap().containsValue(element)) {
            sortedElements = null; //Sorted elements need to be recreated.
            getMap().put(getId(element), element);
            addElementToStore(element);
            return true;
        }
        return false;
    }

    /**
     * Update an element that the primary key has no changed.
     *
     * @param elementUpdated
     */
    public boolean update(ElementPool elementUpdated) throws SQLException {
        return update(elementUpdated, elementUpdated);
    }

    /**
     * Exchange one element to the other.
     *
     * @param oldElement
     * @param newElement
     */
    public boolean update(ElementPool oldElement, ElementPool newElement) throws SQLException {
        String id = getId(oldElement);
        sortedElements = null;
        if (!id.equals(getId(newElement))) {
            //Not the same element. Cannot update!
            remove(oldElement);
            add(newElement);
        } else {
            getMap().remove(id);
            getMap().put(id, newElement);

            //Not stored, not update but store the new one. 
            ElementPool elementStillNotInDatabase = getElementToStore().get(id);
            if (elementStillNotInDatabase != null) {
                getElementToStore().remove(id);
                getElementToStore().put(id, newElement);
            } else {
                addElementToUpdate(oldElement, newElement);
            }
        }
        return true;
    }

    public boolean remove(ElementPool element) throws SQLException {
        String id = getId(element);
        if (getMap().remove(id) != null) {
            //Element not stored in the database, therefore not store it. 
            ElementPool elementStillNotInDatabase = getElementToStore().get(id);
            if (elementStillNotInDatabase != null) {
                getElementToStore().remove(id);
            } else {
                addElementToRemove(element);
            }
            if (sortedElements != null) {
                sortedElements.remove(element);
            }
            getElementToUpdate().remove(element);
        }
        return true;
    }

    public void remove(String elementName) throws SQLException {
        remove(get(elementName));
    }

    public void remove(List<ElementPool> elements) throws SQLException {
        for (ElementPool element : elements) {
            remove(element);
        }
    }

    /**
     * Obtain all elements that contains the desired string
     */
    public List<ElementPool> search(String string) throws SQLException {
        List<ElementPool> result = new ArrayList<>();
        for (ElementPool element : getMap().values()) {
            if (getId(element).contains(string)) {
                result.add((ElementPool) element);
            }
        }
        return result;
    }

    protected abstract List<ElementPool> sort() throws SQLException;

    public List<ElementPool> getSorted() throws SQLException {
        if (sortedElements != null) {
            return sortedElements;
        } else if (getMap() != null) {
            sortedElements = sort();
            return sortedElements;
        } else {
            return new ArrayList<>();
        }
    }

    public boolean removeElementsFromDatabase() throws SQLException {
        if (!removeElementsFromDatabase(new ArrayList<ElementPool>(getElementToRemove().values()))) {
            return false;
        }
        elementsToDelete = new HashMap<>();
        return true;
    }

    public boolean addElementsToDatabase() throws SQLException {
        storeElementsInDatabase(new ArrayList<ElementPool>(getElementToStore().values()));
        elementsToStore = new HashMap<>();
        //Update must be done after store. 
        updateElements(getElementToUpdate());
        elementsToUpdate = new HashMap<>();
        return true;
    }

    public List<ElementPool> getAll(int fromRow, int numberOfRows) throws SQLException {
        return getAll().subList(fromRow, fromRow + numberOfRows);
    }

    public boolean needsToBeStoredInDatabase() {
        return (elementsToStore.size() > 0 || elementsToDelete.size() > 0 || elementsToUpdate.size() > 0);
    }

    public void reset() {
        sortedElements = null;
        elements = null;
        elementsToStore = null;
        elementsToDelete = null;
        elementsToUpdate = null;
    }
}
