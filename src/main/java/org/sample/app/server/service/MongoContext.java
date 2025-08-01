package org.sample.app.server.service;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.sample.mongo.codec.ThriftCodecProvider;
import org.sample.thrift.SongStruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoContext {

    private static final Logger log = LoggerFactory.getLogger(MongoContext.class);

    private MongoDatabase mongoDatabase;

    /**
     * Uses a connection string to connect to a database
     */
    public void connect(String conn, String databaseName) {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(conn))
                .codecRegistry(initCodecRegistry())
                .build();

        MongoClient mongoClient = MongoClients.create(settings);
        debugListOfDB(mongoClient);

        mongoDatabase = mongoClient.getDatabase(databaseName);
    }

    /**
     * After initiating a connection to mongo database, we can use a collection in that db.
     */
    public <T> MongoCollection<T> useCollection(String collection, Class<T> clazz) {
        return mongoDatabase.getCollection(collection, clazz);
    }

    private static void debugListOfDB(MongoClient mongoClient) {
        MongoIterable<String> databases = mongoClient.listDatabaseNames();
        for (String name : databases) {
            log.debug("Database: {}", name);
        }
    }

    private CodecRegistry initCodecRegistry() {
        ThriftCodecProvider thriftCodecProvider = new ThriftCodecProvider()
                .register(SongStruct.class);

        CodecProvider pojoCodecProvider = PojoCodecProvider.builder()
                .automatic(true).build();

        return fromRegistries(getDefaultCodecRegistry(),
                fromProviders(pojoCodecProvider, thriftCodecProvider));
    }
}
