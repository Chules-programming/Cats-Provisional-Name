package com.cats.cats;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cats")
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
}
