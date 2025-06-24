package com.cats.cats.entities;

import org.bson.types.ObjectId;

public class AdoptionPreferences {
    private ObjectId userId;
    private String preferredSize;
    private String preferredBreed;
    private String originPreference;
    private boolean acceptsDisabled;
    private String preferredAge;

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public String getPreferredSize() {
        return preferredSize;
    }

    public void setPreferredSize(String preferredSize) {
        this.preferredSize = preferredSize;
    }

    public String getPreferredBreed() {
        return preferredBreed;
    }

    public void setPreferredBreed(String preferredBreed) {
        this.preferredBreed = preferredBreed;
    }

    public String getOriginPreference() {
        return originPreference;
    }

    public void setOriginPreference(String originPreference) {
        this.originPreference = originPreference;
    }

    public boolean isAcceptsDisabled() {
        return acceptsDisabled;
    }

    public void setAcceptsDisabled(boolean acceptsDisabled) {
        this.acceptsDisabled = acceptsDisabled;
    }

    public String getPreferredAge() {
        return preferredAge;
    }

    public void setPreferredAge(String preferredAge) {
        this.preferredAge = preferredAge;
    }
}
