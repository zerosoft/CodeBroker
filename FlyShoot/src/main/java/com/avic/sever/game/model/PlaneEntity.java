package com.avic.sever.game.model;

import org.jongo.marshall.jackson.oid.MongoId;

import java.util.ArrayList;
import java.util.List;

public class PlaneEntity {
    @MongoId // auto
    private String planeId;

    private String userIdId;

    public String getPlaneId() {
        return planeId;
    }

    public void setPlaneId(String planeId) {
        this.planeId = planeId;
    }

    public String getUserIdId() {
        return userIdId;
    }

    public void setUserIdId(String userIdId) {
        this.userIdId = userIdId;
    }
}
