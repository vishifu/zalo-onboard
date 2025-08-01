package org.sample.mongo;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.sample.app.server.service.MongoContext;

public class MongoPersistence<T> {

    protected final MongoContext context;
    protected MongoCollection<T> collection;

    public MongoPersistence(MongoContext context, String collection, Class<T> clazz) {
        this.context = context;
        this.collection = context.useCollection(collection, clazz);
    }

    public void insert(T entry) {
        collection.insertOne(entry);
    }

    public T findOne(Document queryObj) {
        return collection.find(queryObj).first();
    }

}
