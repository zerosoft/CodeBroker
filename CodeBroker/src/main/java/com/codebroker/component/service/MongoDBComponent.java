package com.codebroker.component.service;

import com.codebroker.component.BaseCoreService;
import com.codebroker.setting.SystemEnvironment;
import com.codebroker.util.PropertiesWrapper;
import com.google.common.base.Strings;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.jongo.Jongo;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * mogodb的Jongo框架封装
 *
 * @author LongJu
 */
public class MongoDBComponent extends BaseCoreService {


    private Map<String, Jongo> dataSourceMap=new HashMap<>();
    private Map<String, MongoClient> dataMongoClientMap=new HashMap<>();

    @Override
    public void init(Object obj) {
        super.init(obj);
        PropertiesWrapper propertiesWrapper = (PropertiesWrapper) obj;
        List<String> listProperty = propertiesWrapper.getListProperty(SystemEnvironment.MONGODB_SOURCE_NAME, String.class, "");
        listProperty.stream().filter(st-> !Strings.isNullOrEmpty(st)).forEach(key->{
            String mongodbHost =propertiesWrapper.getProperty(String.format(SystemEnvironment.MONGODB_HOST, key));
            int mongodbPort = propertiesWrapper.getIntProperty(String.format(SystemEnvironment.MONGODB_PORT, key),0);
            String mongodbDbName = propertiesWrapper.getProperty(String.format(SystemEnvironment.MONGODB_DBNAME, key));
            MongoClient client = new MongoClient(mongodbHost, mongodbPort);
            DB db = client.getDB(mongodbDbName);
            Jongo jongo = new Jongo(db);
            dataSourceMap.put(key,jongo);
            dataMongoClientMap.put(key,client);
        });

        super.setActive();
        name=getClass().getName();

    }

    @Override
    public String getName() {
        return "MongoDBComponent";
    }

    public Optional<Jongo> getJongo(String sourceName) {
        return Optional.ofNullable(dataSourceMap.get(sourceName));
    }

    public Optional<MongoClient> getMongoClient(String sourceName) {
        return Optional.ofNullable(dataMongoClientMap.get(sourceName));
    }
}
