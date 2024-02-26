package ru.cachehw;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class CacheEntry {
    @Id
    private String key;
    private Object value;
    private LocalDateTime timestamp;

    public CacheEntry() {
        this.timestamp = LocalDateTime.now();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
        this.timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void updateValue(Object value) {
        this.value = value;
        this.timestamp = LocalDateTime.now();
    }
}