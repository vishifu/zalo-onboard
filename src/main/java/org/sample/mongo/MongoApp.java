package org.sample.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.sample.thrift.SongStruct;
import org.sample.util.RandUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.*;

/**
 * This is just an example to inspect how mongodb driver work in Java.
 */
public class MongoApp {
    private static final Logger log = LoggerFactory.getLogger(MongoApp.class);

    public static void main(String[] args) {
        ThriftCodecProvider thriftCodecProvider = new ThriftCodecProvider()
                .register(SongStruct.class);

        CodecProvider pojoCodecProvider = PojoCodecProvider.builder()
                .automatic(true).build();

        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(),
                fromProviders(pojoCodecProvider, thriftCodecProvider));

        MongoClient mongoClient = MongoSupport.getMongoClient();
        log.info("{}", mongoClient.getClusterDescription().toString());

        MongoDatabase database = mongoClient.getDatabase(MongoConstants.DATABASE_NAME)
                .withCodecRegistry(pojoCodecRegistry);
        MongoCollection<SongStruct> collection = database.getCollection(MongoConstants.COLLECTION_SAMPLE_SONGS,
                SongStruct.class);

        workInsertDocument(collection, 10);

        SongStruct retSong = collection.find(new Document("id", 1)).first();
        System.out.println(retSong.toString());
    }

    static void workInsertDocument(MongoCollection<SongStruct> collection, int num) {
        SongStruct songStruct = new SongStruct(1, "test-1", 1.0, Set.of(1,2,3),
                ByteBuffer.wrap(RandUtil.randBytes(20)));
        collection.insertOne(songStruct);
    }
}
