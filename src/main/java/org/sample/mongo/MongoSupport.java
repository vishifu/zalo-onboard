package org.sample.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoSupport {

    private static final Logger log = LoggerFactory.getLogger(MongoSupport.class);

    public static MongoClient getMongoClient() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/");
        debugListAll(mongoClient);

        return mongoClient;
    }

    private static void debugListAll(MongoClient mongoClient) {
        MongoIterable<String> databases = mongoClient.listDatabaseNames();
        for (String name : databases) {
            log.debug("Database: {}", name);
        }
    }

}
