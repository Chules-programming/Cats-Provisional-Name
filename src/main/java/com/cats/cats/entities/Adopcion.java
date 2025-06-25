package com.cats.cats.entities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "adoptions")
public class Adopcion {

    @Id
    private ObjectId id;

    private String nameSurname;
    private String adress;
    private String Postal;
    private String phone;
    private String catName;
    private ObjectId userId;
    private String additionalContact;
    private boolean ratingCompleted;
    private ObjectId caregiverId;
    private ObjectId catId;
    private boolean confirmedByCaregiver = false;
    private boolean confirmedByAdopter = false;
    private boolean chatEnabled = false;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNameSurname() {
        return nameSurname;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getPostal() {
        return Postal;
    }

    public void setPostal(String postal) {
        Postal = postal;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getAdditionalContact() {
        return additionalContact;
    }

    public void setAdditionalContact(String additionalContact) {
        this.additionalContact = additionalContact;
    }

    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public boolean isConfirmedByCaregiver() {
        return confirmedByCaregiver;
    }

    public void setConfirmedByCaregiver(boolean confirmedByCaregiver) {
        this.confirmedByCaregiver = confirmedByCaregiver;
    }

    public boolean isConfirmedByAdopter() {
        return confirmedByAdopter;
    }

    public void setConfirmedByAdopter(boolean confirmedByAdopter) {
        this.confirmedByAdopter = confirmedByAdopter;
    }

    public boolean isRatingCompleted() {
        return ratingCompleted;
    }

    public void setRatingCompleted(boolean ratingCompleted) {
        this.ratingCompleted = ratingCompleted;
    }

    public ObjectId getCaregiverId() {
        return caregiverId;
    }

    public void setCaregiverId(ObjectId caregiverId) {
        this.caregiverId = caregiverId;
    }

    public ObjectId getCatId() {
        return catId;
    }

    public void setCatId(ObjectId catId) {
        this.catId = catId;
    }

    public boolean isChatEnabled() {
        return chatEnabled;
    }

    public void setChatEnabled(boolean chatEnabled) {
        this.chatEnabled = chatEnabled;
    }
}


