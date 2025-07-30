package org.sample.mongo.codec;

import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.nio.ByteBuffer;

public class ByteBufferCodec implements Codec<ByteBuffer> {
    @Override
    public ByteBuffer decode(BsonReader reader, DecoderContext decoderContext) {
        byte[] data = reader.readBinaryData().getData();
        return ByteBuffer.wrap(data);
    }

    @Override
    public void encode(BsonWriter writer, ByteBuffer value, EncoderContext encoderContext) {
        if (value.hasArray()) {
            writer.writeBinaryData(new BsonBinary(value.array()));
        }
    }

    @Override
    public Class<ByteBuffer> getEncoderClass() {
        return ByteBuffer.class;
    }
}
