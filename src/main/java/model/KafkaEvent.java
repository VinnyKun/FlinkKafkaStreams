package model;

public class KafkaEvent {
    public String  key;
    public Message value;
    public long timestamp;
    public KafkaEvent() {
        this("", new Message(), 0);
    }
    public KafkaEvent(String key, Message value, long timestamp) {
        this.key = key;
        this.value = value;
        this.timestamp = timestamp;
    }

}
