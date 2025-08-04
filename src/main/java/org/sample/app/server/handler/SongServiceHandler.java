package org.sample.app.server.handler;

import org.apache.thrift.TException;
import org.bson.Document;
import org.sample.app.server.service.impl.SongMongoPersistImpl;
import org.sample.thrift.SongService;
import org.sample.thrift.SongStruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SongServiceHandler implements SongService.Iface {

    private static final Logger log = LoggerFactory.getLogger(SongServiceHandler.class);
    private final SongMongoPersistImpl persistence;

    public SongServiceHandler(SongMongoPersistImpl persistence) {
        this.persistence = persistence;
    }

    @Override
    public SongStruct get(int id) throws TException {
        SongStruct gotOne = persistence.findOne(new Document("id", id));
        log.info("found: {}", gotOne);

        if (gotOne == null) {
            return new SongStruct();
        }

        return gotOne;
    }

    @Override
    public void save(SongStruct song) throws TException {
        persistence.insert(song);
        log.info("saved song={}", song);
    }
}
