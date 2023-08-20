package com.softwaremagico.ktg.persistence;

public enum AutoSaveOption {

    DISABLED("disabled"),
    BY_TIME("by_time"),
    BY_ACTION("by_action");
    private String tag;

    AutoSaveOption(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return getTag();
    }

    public static AutoSaveOption getAutoSave(String tag) {
        for (AutoSaveOption autoSave : AutoSaveOption.values()) {
            if (autoSave.getTag().equals(tag)) {
                return autoSave;
            }
        }
        return BY_TIME;
    }
}
