package com.softwaremagico.ktg.database;

import com.softwaremagico.ktg.core.Photo;
import java.sql.SQLException;
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

    public Photo get(String competitorId) throws SQLException {
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

    protected Photo getFromDatabase(String competitorId) throws SQLException{
        DatabaseConnection.getInstance().connect();
        Photo photo = DatabaseConnection.getInstance().getDatabase().getPhoto(competitorId);
        DatabaseConnection.getInstance().disconnect();
        return photo;
    }

    protected boolean storeInDatabase(List<Photo> photos) throws SQLException {
        if (photos.size() > 0) {
            Boolean result = DatabaseConnection.getInstance().getDatabase().setPhotos(photos);
            photosToStore = new ArrayList<>();
            return result;
        }
        return true;
    }

    protected boolean updateDatabase() throws SQLException {
        if (photosToStore.size() > 0) {
            return storeInDatabase(photosToStore);
        }
        return true;
    }

    public boolean needsToBeStoredInDatabase() {
        return (photosToStore.size() > 0);
    }
}
