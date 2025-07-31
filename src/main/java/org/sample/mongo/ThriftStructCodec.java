package org.sample.mongo;

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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ThriftStructCodec<T extends TBase<T, ? extends TFieldIdEnum>> implements Codec<T> {

    private final Class<T> clazz;
    private final Map<TFieldIdEnum, FieldMetaData> fieldMetaDataMap;

    public ThriftStructCodec(Class<T> clazz) {
        this.clazz = clazz;
        try {
            Field metaDataMapField = clazz.getField("metaDataMap");
            this.fieldMetaDataMap = (Map<TFieldIdEnum, FieldMetaData>) metaDataMapField.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T decode(BsonReader bsonReader, DecoderContext decoderContext) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            bsonReader.readStartDocument();
            while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                String fieldName = bsonReader.readName();

                // skip _id
                if ("_id".equals(fieldName)) {
                    bsonReader.skipValue();
                    continue;
                }

                TFieldIdEnum fieldIdEnum = fieldMetaDataMap.keySet()
                        .stream()
                        .filter(x -> x.getFieldName().equals(fieldName))
                        .findFirst()
                        .get();

                FieldMetaData fieldMetaData = fieldMetaDataMap.get(fieldIdEnum);
                if (fieldMetaData != null) {
                    Object value = readValue(bsonReader, fieldMetaData.valueMetaData.type);
                    setFieldValue(instance, fieldName, value);
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

    @Override
    public void encode(BsonWriter bsonWriter, T t, EncoderContext encoderContext) {
        bsonWriter.writeStartDocument();

        try {
            for (Map.Entry<TFieldIdEnum, FieldMetaData> entry : fieldMetaDataMap.entrySet()) {
                String fieldName = entry.getKey().getFieldName();
                FieldMetaData fieldMetaData = entry.getValue();
                Object fieldValue = getFieldValue(t, fieldName);

                if (fieldValue != null) {
                    bsonWriter.writeName(fieldName);
                    writeValue(bsonWriter, fieldValue, fieldMetaData.valueMetaData.type);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<T> getEncoderClass() {
        return clazz;
    }

    private Object readValue(BsonReader bsonReader, byte thriftType) {
        return switch (thriftType) {
            case TType.BOOL -> bsonReader.readBoolean();
            case TType.BYTE -> (byte) bsonReader.readInt32();
            case TType.I16 -> (short) bsonReader.readInt32();
            case TType.I32 -> bsonReader.readInt32();
            case TType.I64 -> bsonReader.readInt64();
            case TType.DOUBLE -> bsonReader.readDouble();
            case TType.STRING -> {
                BsonType currentType = bsonReader.getCurrentBsonType();
                if (currentType == BsonType.BINARY) {
                    // This is a ByteBuffer field stored as binary
                    BsonBinary binary = bsonReader.readBinaryData();
                    yield  ByteBuffer.wrap(binary.getData());
                } else {
                    // This is a regular string
                    yield  bsonReader.readString();
                }
            }
            case TType.LIST -> readList(bsonReader);
            case TType.SET -> readSet(bsonReader);
            case TType.MAP -> readMap(bsonReader);
            case TType.STRUCT -> {
                // implement recursive for nested struct
                bsonReader.skipValue();
                yield null;
            }
            default -> {
                bsonReader.skipValue();
                yield null;
            }
        };
    }

    private List<Object> readList(BsonReader bsonReader) {
        List<Object> list = new ArrayList<>();
        bsonReader.readStartArray();
        while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            BsonType bsonType = bsonReader.getCurrentBsonType();
            list.add(readBsonValue(bsonReader, bsonType));
        }

        bsonReader.readEndArray();
        return list;
    }

    private Map<String, Object> readMap(BsonReader bsonReader) {
        Map<String, Object> map = new HashMap<>();
        bsonReader.readStartDocument();
        while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String key = bsonReader.readName();
            BsonType bsonType = bsonReader.getCurrentBsonType();
            map.put(key, readBsonValue(bsonReader, bsonType));
        }

        bsonReader.readEndDocument();
        return map;
    }

    private Set<Object> readSet(BsonReader bsonReader) {
        Set<Object> set = new HashSet<>();
        bsonReader.readStartArray();
        while (bsonReader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            BsonType bsonType = bsonReader.getCurrentBsonType();
            set.add(readBsonValue(bsonReader, bsonType));
        }

        bsonReader.readEndArray();
        return set;
    }

    private Object readBsonValue(BsonReader bsonReader, BsonType bsonType) {
        return switch (bsonType) {
            case BOOLEAN -> bsonReader.readBoolean();
            case INT32 -> bsonReader.readInt32();
            case INT64 -> bsonReader.readInt64();
            case DOUBLE -> bsonReader.readDouble();
            case STRING -> bsonReader.readString();
            case BINARY -> {
                BsonBinary binary = bsonReader.readBinaryData();
                yield  ByteBuffer.wrap(binary.getData());
            }
            case NULL -> {
                bsonReader.readNull();
                yield null;
            }
            default -> {
                bsonReader.skipValue();
                yield null;
            }
        };
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
                if (value instanceof ByteBuffer buf) {
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
                // recursive for nested struct
                bsonWriter.writeNull();
                break;
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
        switch (value) {
            case null -> bsonWriter.writeNull();
            case Boolean b -> bsonWriter.writeBoolean(b);
            case Byte b -> bsonWriter.writeInt32(b);
            case Short i -> bsonWriter.writeInt32(i);
            case Long l -> bsonWriter.writeInt64(l);
            case Double v -> bsonWriter.writeDouble(v);
            case String s -> bsonWriter.writeString(s);
            default -> bsonWriter.writeString(value.toString());
        }
    }


    private Object getFieldValue(T instance, String fieldName) throws Exception {
        Field field = clazz.getField(fieldName);
        Object val = field.get(instance);
        return val;
    }

    private void setFieldValue(T instance, String fieldName, Object value) throws Exception {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        Class<?> paramType = field.getType();
        Object converted = convertValue(value, paramType);
        field.set(instance, converted);
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
