package org.sample.mongo.codec;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.sample.thrift.SongStruct;

public class SongStructCodecProvider implements CodecProvider {
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == SongStruct.class) {
            return (Codec<T>) new SongStructCodec(registry);
        }

        return null;
    }
}
