package com.suka.model;


// DATA TRANSFER OBJECT (DTO)

public class Packet {
    private String type;
    private String sender;
    private String recipient;
    private String message;

    public Packet(){
    }
    public Packet(String type,String sender,String recipient, String message){
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
