package com.suka.model;

import java.sql.Timestamp;

public class Message {
    private int id;
    private String sender,recipient,type,content;
    private Timestamp createAt;

    public Message(
            String sender,
            String recipient,
            String type,
            String content
    ) {

        this.sender = sender;
        this.recipient = recipient;
        this.type = type;
        this.content = content;
    }

    public Message() {

    }

    public Timestamp getCreatedAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
