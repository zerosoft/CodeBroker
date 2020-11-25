package com.codebroker.mybatis.gameserver1.model;

/**
 * Table: game_user
 */
public class GameUser {
    /**
     * Column: uid
     */
    private Long uid;

    /**
     * Column: name
     */
    private String name;

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
}