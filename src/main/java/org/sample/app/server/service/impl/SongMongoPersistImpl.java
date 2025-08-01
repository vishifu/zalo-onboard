package org.sample.app.server.service.impl;

import org.sample.mongo.MongoConstants;
import org.sample.mongo.MongoContext;
import org.sample.mongo.MongoPersistence;
import org.sample.thrift.SongStruct;

public class SongMongoPersistImpl extends MongoPersistence<SongStruct> {

    public SongMongoPersistImpl(MongoContext context) {
        super(context, MongoConstants.COLLECTION_SAMPLE_SONGS, SongStruct.class);
    }
}
