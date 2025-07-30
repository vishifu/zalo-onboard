package org.sample.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MongoApp {
    private static final Logger log = LoggerFactory.getLogger(MongoApp.class);
    static Random rand = new Random(System.nanoTime());

    public static void main(String[] args) {
        MongoClient mongoClient = MongoSupport.getMongoClient();
        log.info("{}", mongoClient.getClusterDescription().toString());

        MongoDatabase database = mongoClient.getDatabase(MongoConstants.DATABASE_NAME);
        MongoCollection<Document> collection = database.getCollection(MongoConstants.COLLECTION_SAMPLE_SONGS);

        collection.deleteMany(new Document());
        workInsertDocument(collection, 10);

        Document retSong = collection.find(new Document("song_id", 1)).first();
        log.info("find song id 1: {}", retSong.toJson());
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
