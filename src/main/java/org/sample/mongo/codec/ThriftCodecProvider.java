package org.sample.mongo.codec;

import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.HashMap;
import java.util.Map;

public class ThriftCodecProvider implements CodecProvider {

    private final Map<Class<?>, Codec<?>> codecMap = new HashMap<>();

    public <T extends TBase<T, ? extends TFieldIdEnum>> ThriftCodecProvider register(Class<T> clazz) {
        codecMap.put(clazz, new ThriftStructCodec<>(clazz));
        return this;
    }

    @Override
    public <T> Codec<T> get(Class<T> aClass, CodecRegistry codecRegistry) {
        return (Codec<T>) codecMap.get(aClass);
    }
}
