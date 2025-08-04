package org.sample.mongo.codec;

import org.apache.thrift.TBase;
import org.apache.thrift.TFieldIdEnum;
import org.apache.thrift.meta_data.FieldMetaData;
import org.apache.thrift.protocol.TType;
import org.bson.BsonBinary;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ThriftStructCodec<T extends TBase<T, ? extends TFieldIdEnum>> implements Codec<T> {

    private final Class<T> clazz;

    public ThriftStructCodec(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T decode(BsonReader bsonReader, DecoderContext decoderContext) {
        return (T) readStruct(bsonReader,clazz);
    }

    @Override
    public void encode(BsonWriter bsonWriter, T t, EncoderContext encoderContext) {
        writeStruct(bsonWriter, t);
    }

    @Override
    public Class<T> getEncoderClass() {
        return clazz;
    }

    private Object readValue(BsonReader bsonReader, byte thriftType, Class<?> clazz) {
        switch (thriftType) {
            case TType.BOOL:
                return bsonReader.readBoolean();
            case TType.BYTE:
                return (byte) bsonReader.readInt32();
            case TType.I16:
                return (short) bsonReader.readInt32();
            case TType.I32:
                return bsonReader.readInt32();
            case TType.I64:
                return bsonReader.readInt64();
            case TType.DOUBLE:
                return bsonReader.readDouble();
            case TType.STRING:
                BsonType currentType = bsonReader.getCurrentBsonType();
                if (currentType == BsonType.BINARY) {
                    // This is a ByteBuffer field stored as binary
                    BsonBinary binary = bsonReader.readBinaryData();
                    return ByteBuffer.wrap(binary.getData());
                } else {
                    // This is a regular string
                    return bsonReader.readString();
                }
            case TType.LIST:
                return readList(bsonReader, clazz);
            case TType.SET:
                return readSet(bsonReader, clazz);
            case TType.MAP:
                return readMap(bsonReader, clazz);
            case TType.STRUCT:
                return readStruct(bsonReader, clazz);
            default:
                bsonReader.skipValue();
                return null;
        }
    }

    private Object readStruct(BsonReader bsonReader, Class<?> clazz) {
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Field metaDataMapField = clazz.getField("metaDataMap");
            Map<TFieldIdEnum, FieldMetaData> fieldMetaDataMap = (Map<TFieldIdEnum, FieldMetaData>) metaDataMapField.get(null);

            bsonReader.readStartDocument();
            while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                String fieldName = bsonReader.readName();

                // skip _id
                if ("_id".equals(fieldName)) {
                    bsonReader.skipValue();
                    continue;
                }

                // todo
                TFieldIdEnum fieldIdEnum = fieldMetaDataMap.keySet()
                        .stream()
                        .filter(x -> x.getFieldName().equals(fieldName))
                        .findFirst()
                        .get();

                FieldMetaData fieldMetaData = fieldMetaDataMap.get(fieldIdEnum);
                if (fieldMetaData != null) {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);

                    Class<?> paramType = null;
                    if (field.getGenericType() instanceof ParameterizedType) {
                        paramType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    }

                    if (paramType == null) {
                        paramType= field.getType();
                    }

                    Object value = readValue(bsonReader, fieldMetaData.valueMetaData.type, paramType);
                    field.set(instance, convertValue(value, field.getType()));
                } else {
                    bsonReader.skipValue();
                }
            }

            bsonReader.readEndDocument();
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private List<Object> readList(BsonReader bsonReader, Class<?> clazz) {
        List<Object> list = new ArrayList<>();
        bsonReader.readStartArray();
        while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            BsonType bsonType = bsonReader.getCurrentBsonType();
            list.add(readBsonValue(bsonReader, bsonType, clazz));
        }

        bsonReader.readEndArray();
        return list;
    }

    private Map<String, Object> readMap(BsonReader bsonReader, Class<?> clazz) {
        Map<String, Object> map = new HashMap<>();
        bsonReader.readStartDocument();
        while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String key = bsonReader.readName();
            BsonType bsonType = bsonReader.getCurrentBsonType();
            map.put(key, readBsonValue(bsonReader, bsonType, clazz));
        }

        bsonReader.readEndDocument();
        return map;
    }

    private Set<Object> readSet(BsonReader bsonReader, Class<?> clazz) {
        Set<Object> set = new HashSet<>();
        bsonReader.readStartArray();
        while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            BsonType bsonType = bsonReader.getCurrentBsonType();
            set.add(readBsonValue(bsonReader, bsonType, clazz));
        }

        bsonReader.readEndArray();
        return set;
    }

    private Object readBsonValue(BsonReader bsonReader, BsonType bsonType, Class<?> clazz) {
        switch (bsonType) {
            case BOOLEAN:
                return bsonReader.readBoolean();
            case INT32:
                return bsonReader.readInt32();
            case INT64:
                return bsonReader.readInt64();
            case DOUBLE:
                return bsonReader.readDouble();
            case STRING:
                return bsonReader.readString();
            case BINARY:
                BsonBinary binary = bsonReader.readBinaryData();
                return ByteBuffer.wrap(binary.getData());
            case DOCUMENT:
                return readStruct(bsonReader, clazz);
            case NULL:
                bsonReader.readNull();
                return null;
            default:
                bsonReader.skipValue();
                return null;
        }
    }

    private void writeValue(BsonWriter bsonWriter, Object value, byte type) {
        if (value == null) return;
        switch (type) {
            case TType.BOOL:
                bsonWriter.writeBoolean((Boolean) value);
                break;
            case TType.BYTE:
                bsonWriter.writeInt32((Byte) value);
                break;
            case TType.I16:
                bsonWriter.writeInt32((Short) value);
                break;
            case TType.I32:
                bsonWriter.writeInt32((Integer) value);
                break;
            case TType.I64:
                bsonWriter.writeInt64((Long) value);
                break;
            case TType.DOUBLE:
                bsonWriter.writeDouble((Double) value);
                break;
            case TType.STRING:
                if (value instanceof ByteBuffer) {
                    ByteBuffer buf = (ByteBuffer) value;
                    bsonWriter.writeBinaryData(new BsonBinary(buf.array()));
                } else {
                    bsonWriter.writeString((String) value);
                }
                break;
            case TType.LIST:
                writeList(bsonWriter, (List<?>) value);
                break;
            case TType.MAP:
                writeMap(bsonWriter, (Map<?, ?>) value);
                break;
            case TType.SET:
                writeSet(bsonWriter, (Set<?>) value);
                break;
            case TType.STRUCT:
            default:
                writeStruct(bsonWriter, value);
                break;
        }
    }

    private void writeStruct(BsonWriter bsonWriter, Object struct) {
        try {
            Field metaDataMapField = struct.getClass().getField("metaDataMap");
            Map<TFieldIdEnum, FieldMetaData> fieldMetaDataMap = (Map<TFieldIdEnum, FieldMetaData>) metaDataMapField.get(null);

            bsonWriter.writeStartDocument();
            for (Map.Entry<TFieldIdEnum, FieldMetaData> entry : fieldMetaDataMap.entrySet()) {
                String fieldName = entry.getKey().getFieldName();
                FieldMetaData fieldMetaData = entry.getValue();
                Object fieldValue = getFieldValue(struct,fieldName);

                if (fieldValue != null) {
                    bsonWriter.writeName(fieldName);
                    writeValue(bsonWriter, fieldValue, fieldMetaData.valueMetaData.type);
                }
            }
            bsonWriter.writeEndDocument();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeList(BsonWriter bsonWriter, List<?> list) {
        bsonWriter.writeStartArray();
        for (Object item : list) {
            writeBsonValue(bsonWriter, item);
        }

        bsonWriter.writeEndArray();
    }

    private void writeSet(BsonWriter bsonWriter, Set<?> set) {
        bsonWriter.writeStartArray();
        for (Object item : set) {
            writeBsonValue(bsonWriter, item);
        }

        bsonWriter.writeEndArray();
    }

    private void writeMap(BsonWriter bsonWriter, Map<?, ?> map) {
        bsonWriter.writeStartDocument();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            bsonWriter.writeName(entry.getKey().toString());
            writeBsonValue(bsonWriter, entry.getValue());
        }

        bsonWriter.writeEndDocument();
    }

    private void writeBsonValue(BsonWriter bsonWriter, Object value) {
        if (value == null) {
            bsonWriter.writeNull();
        } else if (value instanceof Boolean) {
            bsonWriter.writeBoolean((Boolean) value);
        } else if (value instanceof Byte) {
            bsonWriter.writeInt32((Byte) value);
        } else if (value instanceof Short) {
            bsonWriter.writeInt32((Short) value);
        } else if (value instanceof Integer) {
            bsonWriter.writeInt32((Integer) value);
        } else if (value instanceof Long) {
            bsonWriter.writeInt64((Long) value);
        } else if (value instanceof Double) {
            bsonWriter.writeDouble((Double) value);
        } else if (value instanceof String) {
            bsonWriter.writeString((String) value);
        } else {
            writeStruct(bsonWriter, value);
        }
    }

    /* USING REFLECTION TO SET FIELD */

    private Object getFieldValue(Object instance, String fieldName) throws Exception {
        Field field = instance.getClass().getField(fieldName);
        Object val = field.get(instance);
        return val;
    }

    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType.isAssignableFrom(value.getClass())) return value;

        // Handle primitive type conversions
        if (targetType == int.class || targetType == Integer.class) {
            return ((Number) value).intValue();
        } else if (targetType == long.class || targetType == Long.class) {
            return ((Number) value).longValue();
        } else if (targetType == short.class || targetType == Short.class) {
            return ((Number) value).shortValue();
        } else if (targetType == byte.class || targetType == Byte.class) {
            return ((Number) value).byteValue();
        } else if (targetType == double.class || targetType == Double.class) {
            return ((Number) value).doubleValue();
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return (Boolean) value;
        }

        return value;
    }
}
