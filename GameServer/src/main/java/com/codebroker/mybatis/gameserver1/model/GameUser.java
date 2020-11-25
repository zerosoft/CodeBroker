package com.codebroker.mybatis.gameserver1.model;

public class GameUser {
    private Long uid;

    private String name;

    private String accountUid;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getAccountUid() {
        return accountUid;
    }

    public void setAccountUid(String accountUid) {
        this.accountUid = accountUid == null ? null : accountUid.trim();
    }
}