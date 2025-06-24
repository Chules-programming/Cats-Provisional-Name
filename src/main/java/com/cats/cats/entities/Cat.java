package com.cats.cats.entities;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "catsadded") // Esto indica que se guardará en la colección "catsadded"
public class Cat {
    @Id
    private ObjectId id;
    private String breed;
    private String age;
    private String sex;
    private String color;
    private double height;
    private double width;
    private String friendlyWithKids;
    private String friendlyWithAnimals;
    private String description;
    private String image1Path;
    private String image2Path;
    private String image3Path;
    private String videoPath;
    private String name;
    private String imageId1;
    private String imageId2;
    private String imageId3;
    private String videoID;
    private String bornDate;
    private String ongName;
    private String ongPhone;
    private String catLocation;
    private boolean adopted = false;
    private ObjectId caregiverId;


    // Constructores
    public Cat() {
    }

    public Cat(ObjectId id, String breed, String age, String sex, String color, double height, double width,
               String friendlyWithKids, String friendlyWithAnimals, String description,
               String image1Path, String image2Path, String image3Path, String videoPath, String name, String imageId1, String imageId2, String imageId3, String videoID, String bornDate, String ongName, String catLocation, boolean adopted, String ongPhone) {
        this.id = id;
        this.breed = breed;
        this.age = age;
        this.sex = sex;
        this.color = color;
        this.height = height;
        this.width = width;
        this.friendlyWithKids = friendlyWithKids;
        this.friendlyWithAnimals = friendlyWithAnimals;
        this.description = description;
        this.image1Path = image1Path;
        this.image2Path = image2Path;
        this.image3Path = image3Path;
        this.videoPath = videoPath;
        this.name = name;
        this.imageId1 = imageId1;
        this.imageId2 = imageId2;
        this.imageId3 = imageId3;
        this.videoID = videoID;
        this.adopted = adopted;
        this.bornDate = bornDate;
        this.ongName = ongName;
        this.catLocation = catLocation;
        this.ongPhone = ongPhone;
    }

    public boolean isAdopted() {
        return adopted;
    }

    public void setAdopted(boolean adopted) {
        this.adopted = adopted;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getImageId1() {
        return imageId1;
    }

    public void setImageId1(String imageId1) {
        this.imageId1 = imageId1;
    }

    public String getImageId2() {
        return imageId2;
    }

    public void setImageId2(String imageId2) {
        this.imageId2 = imageId2;
    }

    public String getImageId3() {
        return imageId3;
    }

    public void setImageId3(String imageId3) {
        this.imageId3 = imageId3;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    // Getters y Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public String getFriendlyWithKids() {
        return friendlyWithKids;
    }

    public void setFriendlyWithKids(String friendlyWithKids) {
        this.friendlyWithKids = friendlyWithKids;
    }

    public String getFriendlyWithAnimals() {
        return friendlyWithAnimals;
    }

    public void setFriendlyWithAnimals(String friendlyWithAnimals) {
        this.friendlyWithAnimals = friendlyWithAnimals;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage1Path() {
        return image1Path;
    }

    public void setImage1Path(String image1Path) {
        this.image1Path = image1Path;
    }

    public String getImage2Path() {
        return image2Path;
    }

    public void setImage2Path(String image2Path) {
        this.image2Path = image2Path;
    }

    public String getImage3Path() {
        return image3Path;
    }

    public void setImage3Path(String image3Path) {
        this.image3Path = image3Path;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getBornDate() {
        return bornDate;
    }

    public void setBornDate(String bornDate) {
        this.bornDate = bornDate;
    }

    public String getOngName() {
        return ongName;
    }

    public void setOngName(String ongName) {
        this.ongName = ongName;
    }

    public String getCatLocation() {
        return catLocation;
    }

    public void setCatLocation(String catLocation) {
        this.catLocation = catLocation;
    }

    public String getOngPhone() {
        return ongPhone;
    }

    public void setOngPhone(String ongPhone) {
        this.ongPhone = ongPhone;
    }

    public ObjectId getCaregiverId() {
        return caregiverId;
    }

    public void setCaregiverId(ObjectId caregiverId) {
        this.caregiverId = caregiverId;
    }
}
