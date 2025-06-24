package com.cats.cats.entities;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "conversations")
public class Conversation {
    @Id
    private ObjectId id;
    private ObjectId userId1;
    private ObjectId userId2;
    private ObjectId catId;
    private String lastMessage;
    private Date createdAt;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getUserId1() {
        return userId1;
    }

    public void setUserId1(ObjectId userId1) {
        this.userId1 = userId1;
    }

    public ObjectId getUserId2() {
        return userId2;
    }

    public void setUserId2(ObjectId userId2) {
        this.userId2 = userId2;
    }

    public ObjectId getCatId() {
        return catId;
    }

    public void setCatId(ObjectId catId) {
        this.catId = catId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
