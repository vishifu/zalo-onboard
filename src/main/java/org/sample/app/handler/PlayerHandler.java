package org.sample.app.handler;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.thrift.TException;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.sample.mongo.MongoConstants;
import org.sample.mongo.MongoSupport;
import org.sample.mongo.codec.ByteBufferCodec;
import org.sample.mongo.codec.SongStructCodecProvider;
import org.sample.thrift.Player;
import org.sample.thrift.SongStruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.*;

public class PlayerHandler implements Player.Iface {

    private static final Logger log = LoggerFactory.getLogger(PlayerHandler.class);
    private final MongoCollection<SongStruct> songCollection;

    public PlayerHandler() {
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder()
                .automatic(true).build();
        CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(),
                fromCodecs(new ByteBufferCodec()),
                fromProviders(pojoCodecProvider, new SongStructCodecProvider()));

        MongoDatabase database = MongoSupport.getMongoClient().getDatabase(MongoConstants.DATABASE_NAME)
                .withCodecRegistry(codecRegistry);

        this.songCollection = database.getCollection(MongoConstants.COLLECTION_SAMPLE_SONGS, SongStruct.class);
    }

    @Override
    public SongStruct get(int id) throws TException {
        SongStruct ret = songCollection.find(new Document("id", id)).first();
        if (ret == null) {
            log.info("NOT found song id {}", id);
            return new SongStruct();
        }

        log.info("found song id {}; song={}", id, ret);
        return ret;
    }

    @Override
    public void save(SongStruct song) throws TException {
        songCollection.insertOne(song);
        log.info("saved song={}", song);
    }
}
