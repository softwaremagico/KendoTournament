package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.Tournament;
import com.softwaremagico.ktg.tools.Tools;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class TournamentDependentPool<ElementPool> {

    private HashMap<Tournament, HashMap<String, ElementPool>> elements = new HashMap<>();
    private HashMap<Tournament, List<ElementPool>> sortedElements = new HashMap<>();
    private HashMap<Tournament, HashMap<String, ElementPool>> elementsToStore = new HashMap<>();
    private HashMap<Tournament, HashMap<String, ElementPool>> elementsToDelete = new HashMap<>();
    private HashMap<Tournament, HashMap<ElementPool, ElementPool>> elementsToUpdate = new HashMap<>(); //New element replace old one.

    protected TournamentDependentPool() {
    }

    protected abstract String getId(ElementPool element);

    protected abstract HashMap<String, ElementPool> getFromDatabase(Tournament tournament);

    protected abstract void storeInDatabase(Tournament tournament, List<ElementPool> elementsToStore);

    protected abstract void removeFromDatabase(Tournament tournament, List<ElementPool> elementsToDelete);

    protected abstract void updateDatabase(Tournament tournament, HashMap<ElementPool, ElementPool> elementsToUpdate);

    protected HashMap<String, ElementPool> getMap(Tournament tournament) {
        HashMap<String, ElementPool> elementsOfTournament = elements.get(tournament);
        if (elementsOfTournament == null) {
            elementsOfTournament = getFromDatabase(tournament);
            elements.put(tournament, elementsOfTournament);
        }
        return elementsOfTournament;
    }

    private HashMap<String, ElementPool> getElementToStore(Tournament tournament) {
        HashMap<String, ElementPool> elementsOfTournament = elementsToStore.get(tournament);
        if (elementsOfTournament == null) {
            elementsOfTournament = new HashMap<>();
            elementsToStore.put(tournament, elementsOfTournament);
        }
        return elementsOfTournament;
    }

    private HashMap<String, ElementPool> getElementToRemove(Tournament tournament) {
        HashMap<String, ElementPool> elementsOfTournament = elementsToDelete.get(tournament);
        if (elementsOfTournament == null) {
            elementsOfTournament = new HashMap<>();
            elementsToDelete.put(tournament, elementsOfTournament);
        }
        return elementsOfTournament;
    }

    private HashMap<ElementPool, ElementPool> getElementToUpdate(Tournament tournament) {
        HashMap<ElementPool, ElementPool> elementsOfTournament = elementsToUpdate.get(tournament);
        if (elementsOfTournament == null) {
            elementsOfTournament = new HashMap<>();
            elementsToUpdate.put(tournament, elementsOfTournament);
        }
        return elementsOfTournament;
    }

    private List<ElementPool> getSortedElements(Tournament tournament) {
        List<ElementPool> elementsOfTournament = sortedElements.get(tournament);
        if (elementsOfTournament == null) {
            elementsOfTournament = new ArrayList<>();
            sortedElements.put(tournament, elementsOfTournament);
        }
        return elementsOfTournament;
    }

    protected abstract List<ElementPool> sort(Tournament tournament);

    private void addElementToStore(Tournament tournament, ElementPool element) {
        HashMap<String, ElementPool> elementGroup = getElementToStore(tournament);
        if (elementGroup == null) {
            elementGroup = new HashMap<>();
        }
        elementGroup.put(getId(element), element);
        elementsToStore.put(tournament, elementGroup);
    }

    private void addElementToUpdate(Tournament tournament, ElementPool oldElement, ElementPool newElement) {
        HashMap<ElementPool, ElementPool> elementGroup = getElementToUpdate(tournament);
        if (elementGroup == null) {
            elementGroup = new HashMap<>();
            elementGroup.put(newElement, oldElement);
        } else {
            //Can be an update that older update. Then must be only the last update and the old database element to replace.
            ElementPool element = elementGroup.get(oldElement); //Here, oldElement is the previous newElement.
            if (element != null) {
                //Exist an older update. Must be replaced with the correct information that exist in database.
                elementGroup.remove(oldElement);
                elementGroup.put(newElement, element);
            } else {
                elementGroup.put(newElement, oldElement);
            }
        }
        elementsToUpdate.put(tournament, elementGroup);
    }

    private void addElementToRemove(Tournament tournament, ElementPool element) {
        HashMap<String, ElementPool> elementGroup = getElementToRemove(tournament);
        elementGroup.put(getId(element), element);
        elementsToDelete.put(tournament, elementGroup);
    }

    public ElementPool get(Tournament tournament, String elementName) {
        return (ElementPool) getMap(tournament).get(elementName);
    }

    public List<ElementPool> getAll() {
        List<ElementPool> results = new ArrayList<>();
        for (Tournament tournament : elements.keySet()) {
            results.addAll(getMap(tournament).values());
        }
        return results;
    }

    public List<ElementPool> get(Tournament tournament) {
        return getSorted(tournament);
    }

    public boolean add(Tournament tournament, ElementPool element) {
        if (!getMap(tournament).containsValue(element)) {
            sortedElements = new HashMap<>(); //Sorted elements need to be recreated.
            getMap(tournament).put(getId(element), element);
            addElementToStore(tournament, element);
            return true;
        } else {
            return false;
        }
    }

    public void add(Tournament tournament, List<ElementPool> elements) {
        for (ElementPool element : elements) {
            add(tournament, element);
        }
    }

    public boolean update(Tournament tournament, ElementPool elementUpdated) {
        return update(tournament, elementUpdated, elementUpdated);
    }

    public boolean update(Tournament tournament, ElementPool oldElement, ElementPool newElement) {
        String oldId = getId(oldElement);
        String newId = getId(newElement);
        if (!oldId.equals(newId)) {
            //Not the same element. Cannot update!
            remove(tournament, oldElement);
            add(tournament, newElement);
        } else {
            //Change element. 
            getMap(tournament).remove(oldId);
            getMap(tournament).put(newId, newElement);
            sortedElements =  new HashMap<>();

            //Element added previously but not stored in database.
            ElementPool elementStillNotInDatabase = getElementToStore(tournament).get(oldId);
            if (elementStillNotInDatabase != null) {
                getElementToStore(tournament).remove(oldId);
                getElementToStore(tournament).put(newId, newElement);
            }             
            //Element previously updated. Change the new one.            
            else {
                addElementToUpdate(tournament, oldElement, newElement);
            }
        }
        return true;
    }

    public void remove(Tournament tournament) {
        List<ElementPool> listToDelete = new ArrayList<>();
        for (ElementPool element : getMap(tournament).values()) {
            listToDelete.add(element);

        }
        //Two loops to avoid ConcurrentModificationException.
        for (ElementPool element : listToDelete) {
            remove(tournament, element);
        }
    }

    public boolean remove(Tournament tournament, ElementPool element) {
        String id = getId(element);
        if (getMap(tournament).remove(id) != null) {
            //Element not stored in the database, therefore not store it. 
            ElementPool elementStillNotInDatabase = getElementToStore(tournament).get(id);
            if (elementStillNotInDatabase != null) {
                getElementToStore(tournament).remove(id);
            } else {
                addElementToRemove(tournament, element);
            }
            getSortedElements(tournament).remove(element);
            getElementToUpdate(tournament).remove(element);
        }
        return true;
    }

    public boolean remove(Tournament tournament, List<ElementPool> elements) {
        for (ElementPool element : elements) {
            remove(tournament, element);
        }
        return true;
    }

    public boolean remove(Tournament tournament, String elementName) {
        return remove(tournament, get(tournament, elementName));
    }

    /**
     * Obtain all elements that contains the desired string
     */
    public List<ElementPool> getById(Tournament tournament, String string) {
        List<ElementPool> result = new ArrayList<>();
        for (ElementPool element : getMap(tournament).values()) {
            if (Tools.isSimilar(getId(element), string)) {
                result.add((ElementPool) element);
            }
        }
        return result;
    }

    public List<ElementPool> getSorted(Tournament tournament) {
        List<ElementPool> sorted = getSortedElements(tournament);
        if (!sorted.isEmpty()) {
            return sorted;
        } else if (!getMap(tournament).isEmpty()) {
            sorted = sort(tournament);
            sortedElements.put(tournament, sorted);
            return sorted;
        } else {
            return new ArrayList<>();
        }
    }

    private void updateDatabase(Tournament tournament) {
        if (getElementToRemove(tournament).size() > 0) {
            removeFromDatabase(tournament, new ArrayList(getElementToRemove(tournament).values()));
        }
        elementsToDelete.put(tournament, new HashMap<String, ElementPool>());
        if (getElementToStore(tournament) != null) {
            storeInDatabase(tournament, new ArrayList(getElementToStore(tournament).values()));
        }
        elementsToStore.put(tournament, new HashMap<String, ElementPool>());
        //Update must be done after store. 
        if (getElementToUpdate(tournament).size() > 0) {
            updateDatabase(tournament, getElementToUpdate(tournament));
        }
        elementsToUpdate.put(tournament, new HashMap<ElementPool, ElementPool>());
    }

    public void updateDatabase() {
        for (Tournament tournament : elements.keySet()) {
            updateDatabase(tournament);
        }
    }

    public List<ElementPool> getAll(int fromRow, int numberOfRows) {
        return getAll().subList(fromRow, fromRow + numberOfRows);
    }

    public void reset() {
        sortedElements = new HashMap<>();
        elements = new HashMap<>();
        elementsToStore = new HashMap<>();
        elementsToDelete = new HashMap<>();
        elementsToUpdate = new HashMap<>();
    }

    public boolean needsToBeStoredInDatabase() {
        for (Tournament tournament : elementsToStore.keySet()) {
            if (elementsToStore.get(tournament).size() > 0) {
                return true;
            }
        }

        for (Tournament tournament : elementsToDelete.keySet()) {
            if (elementsToDelete.get(tournament).size() > 0) {
                return true;
            }
        }

        for (Tournament tournament : elementsToUpdate.keySet()) {
            if (elementsToUpdate.get(tournament).size() > 0) {
                return true;
            }
        }

        return false;
    }
}
