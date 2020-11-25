package com.codebroker.mybatis.gameserver1.model;

/**
 * Table: game_item
 */
public class GameItem {
    /**
     * Column: sid
     */
    private Long sid;

    /**
     * Column: num
     */
    private Integer num;

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}