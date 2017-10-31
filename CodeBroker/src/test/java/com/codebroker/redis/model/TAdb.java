package com.codebroker.redis.model;

import java.io.Serializable;

public class TAdb implements Serializable {

    private String key;

    private String name;

    private Long value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }


}
