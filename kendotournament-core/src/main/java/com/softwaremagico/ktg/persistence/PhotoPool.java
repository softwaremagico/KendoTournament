package com.softwaremagico.ktg.persistence;

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
    }

    public static PhotoPool getInstance() {
        if (instance == null) {
            instance = new PhotoPool();
        }
        return instance;
    }

    public boolean addElementsToDatabase() throws SQLException {
        if (getPhotosToStore().size() > 0) {
            return storeInDatabase(getPhotosToStore());
        }
        return true;
    }

    public Photo get(String competitorId) throws SQLException {
        Photo photo = getElements().get(competitorId);
        if (photo == null) {
            photo = getFromDatabase(competitorId);
            getElements().put(competitorId, photo);
        }
        return photo;
    }

    private HashMap<String, Photo> getElements() {
        if (elements == null) {
            elements = new HashMap<>();
        }
        return elements;
    }

    protected Photo getFromDatabase(String competitorId) throws SQLException {
        if (!DatabaseConnection.getInstance().connect()) {
            return null;
        }
        Photo photo = DatabaseConnection.getInstance().getDatabase().getPhoto(competitorId);
        DatabaseConnection.getInstance().disconnect();
        return photo;
    }

    protected String getId(Photo element) {
        return element.getId();
    }

    private List<Photo> getPhotosToStore() {
        if (photosToStore == null) {
            photosToStore = new ArrayList<>();
        }
        return photosToStore;
    }

    public boolean needsToBeStoredInDatabase() {
        return (getPhotosToStore().size() > 0);
    }

    public boolean removeElementsFromDatabase() throws SQLException {
        return true;
    }

    public void reset() {
        elements = null;
        photosToStore = null;
    }

    public void set(Photo element) {
        getElements().put(element.getId(), element);
        getPhotosToStore().add(element);
    }

    protected boolean storeInDatabase(List<Photo> photos) throws SQLException {
        if (photos.size() > 0) {
            Boolean result = DatabaseConnection.getInstance().getDatabase().setPhotos(photos);
            photosToStore = null;
            return result;
        }
        return true;
    }

    protected boolean updateDatabase() throws SQLException {
        if (getPhotosToStore().size() > 0) {
            return storeInDatabase(getPhotosToStore());
        }
        return true;
    }

    public void clearCache() {
        elements = null;
        photosToStore = null;
    }
}
