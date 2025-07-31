package org.sample.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoIterable;
import org.apache.thrift.TBase;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoSupport {

    private static final Logger log = LoggerFactory.getLogger(MongoSupport.class);

    public static MongoClient getMongoClient() {
        String conn = System.getenv("MONGODB_CONNECTION_STRING");
        log.info("detect MONGO connection string: {}", conn);
        MongoClient mongoClient = MongoClients.create(conn);
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
