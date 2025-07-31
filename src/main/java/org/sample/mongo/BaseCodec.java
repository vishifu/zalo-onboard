package org.sample.mongo;

import org.apache.thrift.TBase;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class BaseCodec<T extends TBase> implements Codec<T> {

    @Override
    public T decode(BsonReader reader, DecoderContext decoderContext) {

    }

    @Override
    public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {

    }

    @Override
    public Class<T> getEncoderClass() {
        return null;
    }


}