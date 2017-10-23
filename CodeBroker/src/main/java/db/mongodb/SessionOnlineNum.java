package db.mongodb;

import org.jongo.marshall.jackson.oid.MongoId;

public class SessionOnlineNum {
    @MongoId // auto
    private String id;
    private int server_id;
    private int session_num;
}
