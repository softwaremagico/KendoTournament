package com.softwaremagico.ktg.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class SimplePool<ElementPool> {

    private HashMap<String, ElementPool> elements;
    private List<ElementPool> sortedElements = null;
    private HashMap<String, ElementPool> elementsToStore = new HashMap<>();
    private HashMap<String, ElementPool> elementsToDelete = new HashMap<>();
    private HashMap<ElementPool, ElementPool> elementsToUpdate = new HashMap<>(); //New element replace old one.

    protected abstract String getId(ElementPool element);

    protected abstract HashMap<String, ElementPool> getFromDatabase();

    protected abstract void storeInDatabase(List<ElementPool> elementsToStore);

    protected abstract void removeFromDatabase(List<ElementPool> elementsToDelete);

    protected abstract void updateDatabase(HashMap<ElementPool, ElementPool> elementsToUpdate);

    protected HashMap<String, ElementPool> get() {
        if (elements == null) {
            elements = getFromDatabase();
        }
        return elements;
    }

    private void addElementToStore(ElementPool element) {
        if (elementsToStore == null) {
            elementsToStore = new HashMap<>();
        }
        elementsToStore.put(getId(element), element);
    }

    private void addElementToUpdate(ElementPool oldElement, ElementPool newElement) {
        if (elementsToUpdate == null) {
            elementsToUpdate = new HashMap<>();
            elementsToUpdate.put(newElement, oldElement);
        } else {
            //Can be an update that older update. Then must be only the last update and the old database element to replace.
            ElementPool element = elementsToUpdate.get(oldElement); //Here, oldElement is the previous newElement.
            if (element != null) {
                //Exist an older update. Must be replaced with the correct information that exist in database.
                elementsToUpdate.remove(oldElement);
                elementsToUpdate.put(newElement, element);
            } else {
                elementsToUpdate.put(newElement, oldElement);
            }
        }
    }

    private void addElementToRemove(ElementPool element) {
        if (elementsToDelete == null) {
            elementsToDelete = new HashMap<>();
        }
        elementsToDelete.put(getId(element), element);
    }

    public ElementPool get(String elementName) {
        return (ElementPool) get().get(elementName);
    }

    public List<ElementPool> getAll() {
        List<ElementPool> results = new ArrayList<>();
        results.addAll(get().values());
        return results;
    }

    public void add(ElementPool element) {
        sortedElements = null; //Sorted elements need to be recreated.
        get().put(getId(element), element);
        addElementToStore(element);
    }

    public void update(ElementPool oldElement, ElementPool newElement) {
        String id = getId(oldElement);
        if (!id.equals(getId(newElement))) {
            //Not the same element. Cannot update!
            remove(oldElement);
            add(newElement);
        } else {
            get().remove(id);
            get().put(id, newElement);

            ElementPool elementStillNotInDatabase = elementsToStore.get(id);
            if (elementStillNotInDatabase != null) {
                elementsToStore.remove(id);
                elementsToStore.put(id, newElement);
            } else {
                addElementToUpdate(oldElement, newElement);
            }
        }
    }

    public void remove(ElementPool element) {
        String id = getId(element);
        if (get().remove(id) != null) {
            //Element not stored in the database, therefore not store it. 
            ElementPool elementStillNotInDatabase = elementsToStore.get(id);
            if (elementStillNotInDatabase != null) {
                elementsToStore.remove(id);
            } else {
                addElementToRemove(element);
            }
            sortedElements.remove(element);
            elementsToUpdate.remove(element);
        }
    }

    public void remove(String elementName) {
        remove(get(elementName));
    }

    /**
     * Obtain all elements that contains the desired string
     */
    public List<ElementPool> search(String string) {
        List<ElementPool> result = new ArrayList<>();
        for (ElementPool element : get().values()) {
            if (getId(element).contains(string)) {
                result.add((ElementPool) element);
            }
        }
        return result;
    }

    protected abstract List<ElementPool> sort();

    public List<ElementPool> getSorted() {
        if (sortedElements != null) {
            return sortedElements;
        } else if (get() != null) {
            sortedElements = sort();
            return sortedElements;
        } else {
            return new ArrayList<>();
        }
    }

    public void updateDatabase() {
        removeFromDatabase(new ArrayList(elementsToDelete.values()));
        elementsToDelete = new HashMap<>();
        storeInDatabase(new ArrayList(elementsToStore.values()));
        elementsToStore = new HashMap<>();
        //Update must be done after store. 
        updateDatabase(elementsToUpdate);
        elementsToUpdate = new HashMap<>();
    }

    public List<ElementPool> getAll(int fromRow, int numberOfRows) {
        return getAll().subList(fromRow, fromRow + numberOfRows);
    }
}
