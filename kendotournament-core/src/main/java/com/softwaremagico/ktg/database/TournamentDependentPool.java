package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.Tools;
import com.softwaremagico.ktg.core.Tournament;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class TournamentDependentPool<ElementPool> {

    private HashMap<Tournament, HashMap<String, ElementPool>> elements = new HashMap<>();
    private HashMap<Tournament, List<ElementPool>> sortedElements = new HashMap<>();
    private HashMap<Tournament, HashMap<String, ElementPool>> elementsToStore = new HashMap<>();
    private HashMap<Tournament, HashMap<String, ElementPool>> elementsToDelete = new HashMap<>();
    private HashMap<Tournament, HashMap<ElementPool, ElementPool>> elementsToUpdate = new HashMap<>(); //New element replace old one.

    /**
     * Ensure that the class knows all existent tournaments.
     */
    protected TournamentDependentPool() {
        for (Tournament tournament : TournamentPool.getInstance().getAll()) {
            elements.put(tournament, null);
        }
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

    protected abstract List<ElementPool> sort(Tournament tournament);

    private void addElementToStore(Tournament tournament, ElementPool element) {
        HashMap<String, ElementPool> elementGroup = elementsToStore.get(tournament);
        if (elementGroup == null) {
            elementGroup = new HashMap<>();
            elementGroup.put(getId(element), element);
        }
        elementsToStore.put(tournament, elementGroup);
    }

    private void addElementToUpdate(Tournament tournament, ElementPool oldElement, ElementPool newElement) {
        HashMap<ElementPool, ElementPool> elementGroup = elementsToUpdate.get(tournament);
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
        HashMap<String, ElementPool> elementGroup = elementsToDelete.get(tournament);
        if (elementGroup == null) {
            elementGroup = new HashMap<>();
            elementGroup.put(getId(element), element);
        }
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

    public void add(Tournament tournament, ElementPool element) {
        if (!elements.get(tournament).containsValue(element)) {
            sortedElements = new HashMap<>(); //Sorted elements need to be recreated.
            getMap(tournament).put(getId(element), element);
            addElementToStore(tournament, element);
        }
    }

    public void add(Tournament tournament, List<ElementPool> elements) {
        for (ElementPool element : elements) {
            add(tournament, element);
        }
    }

    public void update(Tournament tournament, ElementPool elementUpdated) {
        update(tournament, elementUpdated, elementUpdated);
    }

    public void update(Tournament tournament, ElementPool oldElement, ElementPool newElement) {
        String id = getId(oldElement);
        if (!id.equals(getId(newElement))) {
            //Not the same element. Cannot update!
            remove(tournament, oldElement);
            add(tournament, newElement);
        } else {
            getMap(tournament).remove(id);
            getMap(tournament).put(id, newElement);

            ElementPool elementStillNotInDatabase = elementsToStore.get(tournament).get(id);
            if (elementStillNotInDatabase != null) {
                elementsToStore.get(tournament).remove(id);
                elementsToStore.get(tournament).put(id, newElement);
            } else {
                addElementToUpdate(tournament, oldElement, newElement);
            }
        }
    }

    public void remove(Tournament tournament) {
        for (ElementPool element : getMap(tournament).values()) {
            remove(tournament, element);
        }
    }

    public void remove(Tournament tournament, ElementPool element) {
        String id = getId(element);
        if (getMap(tournament).remove(id) != null) {
            //Element not stored in the database, therefore not store it. 
            ElementPool elementStillNotInDatabase = elementsToStore.get(tournament).get(id);
            if (elementStillNotInDatabase != null) {
                elementsToStore.get(tournament).remove(id);
            } else {
                addElementToRemove(tournament, element);
            }
            sortedElements.get(tournament).remove(element);
            elementsToUpdate.get(tournament).remove(element);
        }
    }

    public void remove(Tournament tournament, List<ElementPool> elements) {
        for (ElementPool element : elements) {
            remove(tournament, element);
        }
    }

    public void remove(Tournament tournament, String elementName) {
        remove(tournament, get(tournament, elementName));
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
        List<ElementPool> sorted = sortedElements.get(tournament);
        if (sorted != null) {
            return sorted;
        } else if (getMap(tournament) != null) {
            sorted = sort(tournament);
            sortedElements.put(tournament, sorted);
            return sorted;
        } else {
            return new ArrayList<>();
        }
    }

    private void updateDatabase(Tournament tournament) {
        removeFromDatabase(tournament, new ArrayList(elementsToDelete.get(tournament).values()));
        elementsToDelete = new HashMap<>();
        storeInDatabase(tournament, new ArrayList(elementsToStore.get(tournament).values()));
        elementsToStore = new HashMap<>();
        //Update must be done after store. 
        updateDatabase(tournament, elementsToUpdate.get(tournament));
        elementsToUpdate = new HashMap<>();
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
}
