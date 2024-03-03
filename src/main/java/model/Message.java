package model;

public class Message {
    private String senderName;
    private String recepientName;
    private  String body;

    public Message() {
        this.senderName = "";
        this.recepientName = "";
        this.body = "";
    }

    public Message(String senderName, String recepientName, String body) {
        this.senderName = senderName;
        this.recepientName = recepientName;
        this.body = body;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    @Override
    public String toString() {
        return "Message{" +
                "senderName='" + senderName + '\'' +
                ", recepientName='" + recepientName + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    public String getRecepientName() {
        return recepientName;
    }

    public void setRecepientName(String recepientName) {
        this.recepientName = recepientName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
