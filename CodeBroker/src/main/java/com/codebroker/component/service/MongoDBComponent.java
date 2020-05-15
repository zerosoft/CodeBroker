package com.codebroker.component.service;

import com.codebroker.component.BaseCoreService;
import com.codebroker.util.PropertiesWrapper;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;

/**
 * mogodb的Jongo框架封装
 *
 * @author LongJu
 */
public class MongoDBComponent extends BaseCoreService {

    MongoClient client;
    DB db;
    Jongo jongo;

    @Override
    public void init(Object obj) {
        super.init(obj);
        PropertiesWrapper propertiesWrapper = (PropertiesWrapper) obj;
        String mongodbHost = propertiesWrapper.getProperty("mongodb.host");
        int mongodbPort = propertiesWrapper.getIntProperty("mongodb.port", 27017);
        String mongodbDbName = propertiesWrapper.getProperty("mongodb.dbname");
        client = new MongoClient(mongodbHost, mongodbPort);
        db = client.getDB(mongodbDbName);
        jongo = new Jongo(db);
        super.setActive();
    }

    @Override
    public String getName() {
        return "MongoDBComponent";
    }

    public Jongo getJongo() {
        return jongo;
    }
}
