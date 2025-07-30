package org.sample.mongo.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;
import org.sample.thrift.SongStruct;

import java.nio.ByteBuffer;

public class SongStructCodec implements Codec<SongStruct> {
    private final CodecRegistry codecRegistry;

    public SongStructCodec(CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    @Override
    public SongStruct decode(BsonReader reader, DecoderContext decoderContext) {
        Codec<ByteBuffer> bufferCodec = codecRegistry.get(ByteBuffer.class);
        reader.readStartDocument();

        reader.readObjectId("_id");
        int id = reader.readInt32("id");
        String name = reader.readString("name");
        double rating = reader.readDouble("rating");

        reader.readName();
        ByteBuffer buffer = bufferCodec.decode(reader, decoderContext);
        SongStruct songStruct = new SongStruct(id, name, rating, buffer);

        reader.readEndDocument();

        return songStruct;
    }

    @Override
    public void encode(BsonWriter writer, SongStruct value, EncoderContext encoderContext) {
        Codec<ByteBuffer> bufferCodec = codecRegistry.get(ByteBuffer.class);
        writer.writeStartDocument();

        writer.writeObjectId("_id", new ObjectId());
        writer.writeInt32("id", value.id);
        writer.writeString("name", value.name);
        writer.writeDouble("rating", value.rating);

        writer.writeName("content");
        bufferCodec.encode(writer, value.content, encoderContext);

        writer.writeEndDocument();
    }

    @Override
    public Class<SongStruct> getEncoderClass() {
        return SongStruct.class;
    }
}
