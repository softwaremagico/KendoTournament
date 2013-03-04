package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.Photo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PhotoPool {

    private HashMap<String, Photo> elements;
    private List<Photo> photosToStore;
    private static PhotoPool instance;

    private PhotoPool() {
        elements = new HashMap<>();
        photosToStore = new ArrayList<>();
    }

    public static PhotoPool getInstance() {
        if (instance == null) {
            instance = new PhotoPool();
        }
        return instance;
    }

    protected String getId(Photo element) {
        return element.getId();
    }

    public Photo get(String competitorId) {
        Photo photo = elements.get(competitorId);
        if (photo == null) {
            photo = getFromDatabase(competitorId);
            elements.put(competitorId, photo);
        }
        return photo;
    }

    public void set(Photo element) {
        elements.put(element.getId(), element);
        photosToStore.add(element);
    }

    protected Photo getFromDatabase(String competitorId) {
        return DatabaseConnection.getInstance().getDatabase().getPhoto(competitorId);
    }

    protected boolean storeInDatabase(List<Photo> photos) {
        Boolean result = DatabaseConnection.getInstance().getDatabase().setPhotos(photos);
        photosToStore = new ArrayList<>();
        return result;
    }

    protected boolean updateDatabase() {
        return storeInDatabase(photosToStore);
    }
}
