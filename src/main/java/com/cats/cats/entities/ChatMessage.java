package com.cats.cats.entities;

import com.google.gson.Gson;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "chat_messages")
public class ChatMessage {
    private static final Gson GSON = new Gson();
    @Id
    private ObjectId id;
    private ObjectId conversationId;
    private ObjectId senderId;
    private String content;
    private Date timestamp;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getConversationId() {
        return conversationId;
    }

    public void setConversationId(ObjectId conversationId) {
        this.conversationId = conversationId;
    }

    public ObjectId getSenderId() {
        return senderId;
    }

    public void setSenderId(ObjectId senderId) {
        this.senderId = senderId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String toJson() {
        return new Gson().toJson(this);
    }

    public static ChatMessage fromJson(String json) {
        return new Gson().fromJson(json, ChatMessage.class);
    }
}
