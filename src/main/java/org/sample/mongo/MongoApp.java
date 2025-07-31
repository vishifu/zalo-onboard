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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.*;

/**
 * This is just an example to inspect how mongodb driver work in Java.
 */
public class MongoApp {
    private static final Logger log = LoggerFactory.getLogger(MongoApp.class);
    static Random rand = new Random(System.nanoTime());

    public static void main(String[] args) {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder()
                .automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(),
                fromProviders(pojoCodecProvider));

        MongoClient mongoClient = MongoSupport.getMongoClient();
        log.info("{}", mongoClient.getClusterDescription().toString());

        MongoDatabase database = mongoClient.getDatabase(MongoConstants.DATABASE_NAME)
                .withCodecRegistry(pojoCodecRegistry);
        MongoCollection<SongStruct> collection = database.getCollection(MongoConstants.COLLECTION_SAMPLE_SONGS,
                SongStruct.class);

//        collection.deleteMany(new Document());
//        workInsertDocument(collection, 10);

        SongStruct retSong = collection.find(new Document("id", 1)).first();
        System.out.println(retSong.toString());
//        log.info("{}", retSong.get("id"));
//        log.info("{}", retSong.get("name"));
//        log.info("{}", retSong.get("rating"));
//        log.info("{}", retSong.get("content"));
//        log.info("find song id 1: {}", retSong.toJson());
    }

    static void workInsertDocument(MongoCollection<Document> collection, int num) {
        List<Document> list = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            Document obj = new Document("_id", new ObjectId())
                    .append("song_id", i)
                    .append("name", "song_" + rand.nextInt())
                    .append("rating", rand.nextFloat());

            list.add(obj);
        }

        collection.insertMany(list);
    }
}
