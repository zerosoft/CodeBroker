package com.codebroker.core.manager;

import com.codebroker.core.service.BaseCoreService;
import com.codebroker.util.PropertiesWrapper;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;

/**
 * mogodb的Jongo框架封装
 *
 * @author xl
 */
public class JongoDBService extends BaseCoreService {

    MongoClient client;
    DB db;
    Jongo jongo;

    @SuppressWarnings("deprecation")
    @Override
    public void init(Object obj) {
        super.init(obj);
        PropertiesWrapper configPropertieWrapper = (PropertiesWrapper) obj;
        String mongodbHost = configPropertieWrapper.getProperty("mongodb.host");
        int mongodbPort = configPropertieWrapper.getIntProperty("mongodb.port", 27017);
        String mongodbDbName = configPropertieWrapper.getProperty("mongodb.dbname");
        client = new MongoClient(mongodbHost, mongodbPort);
        db = client.getDB(mongodbDbName);
        jongo = new Jongo(db);
        setName("JongoDBService");
        super.setActive();
    }

    public Jongo getJongo() {
        return jongo;
    }
}
