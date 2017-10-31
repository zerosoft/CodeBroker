package com.avic.sever.game.model;

import org.jongo.marshall.jackson.oid.MongoId;

import java.util.ArrayList;
import java.util.List;

public class UserEntity {
    @MongoId // auto
    private String userId;

    private String accountId;

    private String userName;

    private int level;

    private String headImg;

    private List<String> planes=new ArrayList<>(4);

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<String> getPlanes() {
        return planes;
    }

    public void setPlanes(List<String> planes) {
        this.planes = planes;
    }
}
