//package com.codebroker.protocol.serialization;
//
//import akka.actor.typed.ActorRef;
//import akka.actor.typed.ActorRefResolver;
//import akka.actor.typed.ActorSystem;
//import com.codebroker.api.IGameUser;
//import com.codebroker.core.ContextResolver;
//import com.codebroker.core.actortype.message.IGameRootSystemMessage;
//import com.codebroker.core.data.*;
//import com.codebroker.core.entities.GameUser;
//import com.codebroker.exception.CRuntimeException;
//import com.codebroker.exception.CodecException;
//import com.codebroker.protocol.IDataSerializer;
//import com.codebroker.protocol.SerializableType;
//import com.esotericsoftware.reflectasm.FieldAccess;
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.lang.reflect.*;
//import java.nio.ByteBuffer;
//import java.util.*;
//import java.util.Map.Entry;
//import java.util.concurrent.*;
//
///**
// * 默认的序列化器
// * 提供byte序列化及json序列化
// */
//public class DefaultIDataSerializer implements IDataSerializer {
//
//    private static final long serialVersionUID = -6749126348064423022L;
//
//    private  transient final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    private transient static DefaultIDataSerializer instance = new DefaultIDataSerializer();
//
//    private transient final Gson gson;
//
//    private static final String CLASS_MARKER_KEY = "$C";
//    private static final String CLASS_FIELDS_KEY = "$F";
//    private static final String CLASS_ACTOR_KEY = "$A";
//
//    private static final String FIELD_NAME_KEY = "N";
//    private static final String FIELD_VALUE_KEY = "V";
//    private static final String ACTOR_VALUE_KEY = "P";
//
//
//    private static int BUFFER_CHUNK_SIZE = 512;
//
//    private DefaultIDataSerializer() {
//        gson = new Gson();
//    }
//
//    public static DefaultIDataSerializer getInstance() {
//        return instance;
//    }
//
//    public int getUnsignedByte(byte b) {
//        return 255 & b;
//    }
//
//
//    public IArray binary2array(byte[] data) {
//        if (data.length < 3) {
//            throw new IllegalStateException("Can\'t decode an Code Broker Array. Byte data is insufficient. Size: " + data.length + " bytes");
//        } else {
//            ByteBuffer buffer = ByteBuffer.allocate(data.length);
//            buffer.put(data);
//            buffer.flip();
//            return this.decodeIArray(buffer);
//        }
//    }
//
//    private IArray decodeIArray(ByteBuffer buffer) {
//        CArray array = CArray.newInstance();
//        byte headerBuffer = buffer.get();
//        if (headerBuffer != DataType.ARRAY.typeID) {
//            throw new IllegalStateException("Invalid DataType. Expected: " + DataType.ARRAY.typeID + ", found: " + headerBuffer);
//        } else {
//            short size = buffer.getShort();
//            if (size < 0) {
//                throw new IllegalStateException("Can\'t decode Code Broker Array. Size is negative = " + size);
//            } else {
//                try {
//                    for (int codecError = 0; codecError < size; ++codecError) {
//                        DataWrapper decodedObject = this.decodeObject(buffer);
//                        if (decodedObject == null) {
//                            throw new IllegalStateException("Could not decode Code Broker Array item at index: " + codecError);
//                        }
//                        array.add(decodedObject);
//                    }
//                    return array;
//                } catch (CodecException e) {
//                    throw new IllegalArgumentException(e.getMessage());
//                }
//            }
//        }
//    }
//
//    public IObject binary2object(byte[] data) {
//        if (data.length < 3) {
//            throw new IllegalStateException(
//                    "Can\'t decode an Code Broker Object. Byte data is insufficient. Size: " + data.length + " bytes");
//        } else {
//            ByteBuffer buffer = ByteBuffer.allocate(data.length);
//            buffer.put(data);
//            buffer.flip();
//            return this.decodeIObject(buffer);
//        }
//    }
//
//    private IObject decodeIObject(ByteBuffer buffer) {
//        CObject cObject = CObject.newInstance();
//        byte headerBuffer = buffer.get();
//        if (headerBuffer != DataType.OBJECT.typeID) {
//            throw new IllegalStateException(
//                    "Invalid DataType. Expected: " + DataType.OBJECT.typeID + ", found: " + headerBuffer);
//        } else {
//            short size = buffer.getShort();
//            if (size < 0) {
//                throw new IllegalStateException("Can\'t decode Code Broker Object. Size is negative = " + size);
//            } else {
//                try {
//                    for (int codecError = 0; codecError < size; ++codecError) {
//                        short keySize = buffer.getShort();
//                        if (keySize < 0 || keySize > 255) {
//                            throw new IllegalStateException("Invalid Code Broker Object key length. Found = " + keySize);
//                        }
//
//                        byte[] keyData = new byte[keySize];
//                        buffer.get(keyData, 0, keyData.length);
//                        String key = new String(keyData);
//                        DataWrapper decodedObject = this.decodeObject(buffer);
//                        if (decodedObject == null) {
//                            throw new IllegalStateException("Could not decode value for key: " + keyData);
//                        }
//
//                        cObject.put(key, decodedObject);
//                    }
//
//                    return cObject;
//                } catch (CodecException var10) {
//                    throw new IllegalArgumentException(var10.getMessage());
//                }
//            }
//        }
//    }
//
//    public IArray json2array(String jsonStr) {
//        if (jsonStr.length() < 2) {
//            throw new IllegalStateException("Can\'t decode Code Broker Object. JSON String is too short. Len: " + jsonStr.length());
//        } else {
//            JsonArray jsonElements = gson.fromJson(jsonStr, JsonArray.class);
//            return this.decodeIArray(jsonElements);
//        }
//    }
//
//    public IObject json2object(String jsonStr) {
//        if (jsonStr.length() < 2) {
//            throw new IllegalStateException(
//                    "Can\'t decode Code Broker Object. JSON String is too short. Len: " + jsonStr.length());
//        } else {
//            JsonObject jsonObject = gson.fromJson(jsonStr, JsonObject.class);
//            return this.decodeIObject(jsonObject);
//        }
//    }
//
//    private DataWrapper decodeJsonObject(DataType dataType, JsonElement object) {
//        if (dataType.equals(DataType.BYTE)) {
//            return new DataWrapper(DataType.BYTE, object.getAsByte());
//        } else if (dataType.equals(DataType.BOOL)) {
//            return new DataWrapper(DataType.BOOL, object.getAsBoolean());
//        } else if (dataType.equals(DataType.SHORT)) {
//            return new DataWrapper(DataType.SHORT, object.getAsShort());
//        } else if (dataType.equals(DataType.INT)) {
//            return new DataWrapper(DataType.INT, object.getAsInt());
//        } else if (dataType.equals(DataType.FLOAT)) {
//            return new DataWrapper(DataType.FLOAT, object.getAsFloat());
//        } else if (dataType.equals(DataType.LONG)) {
//            return new DataWrapper(DataType.LONG, object.getAsLong());
//        } else if (dataType.equals(DataType.DOUBLE)) {
//            return new DataWrapper(DataType.DOUBLE, object.getAsDouble());
//        } else if (dataType.equals(DataType.UTF_STRING)) {
//            return new DataWrapper(DataType.UTF_STRING, object.getAsString());
//        }
//        else if (dataType.equals(DataType.BOOL_ARRAY)) {
//            JsonArray array=object.getAsJsonArray();
//            Iterator<JsonElement> iterator = array.iterator();
//            boolean[] result=new boolean[array.size()];
//            int index=0;
//            while (iterator.hasNext()){
//                boolean asBoolean = iterator.next().getAsBoolean();
//                result[index++]=asBoolean;
//            }
//            return  new DataWrapper(DataType.BOOL_ARRAY,result);
//        }
//        else if (dataType.equals(DataType.BYTE_ARRAY)) {
//            JsonArray array=object.getAsJsonArray();
//            Iterator<JsonElement> iterator = array.iterator();
//            byte[] result=new byte[array.size()];
//            int index=0;
//             while (iterator.hasNext()){
//                 byte asByte = iterator.next().getAsByte();
//                 result[index++]=asByte;
//             }
//            return  new DataWrapper(DataType.BYTE_ARRAY,result);
//        }
//        else if (dataType.equals(DataType.SHORT_ARRAY)) {
//            JsonArray array=object.getAsJsonArray();
//            Iterator<JsonElement> iterator = array.iterator();
//            short[] result=new short[array.size()];
//            int index=0;
//            while (iterator.hasNext()){
//                short asByte = iterator.next().getAsShort();
//                result[index++]=asByte;
//            }
//            return  new DataWrapper(DataType.SHORT_ARRAY,result);
//        }
//        else if (dataType.equals(DataType.INT_ARRAY)) {
//            JsonArray array=object.getAsJsonArray();
//            Iterator<JsonElement> iterator = array.iterator();
//            int[] result=new int[array.size()];
//            int index=0;
//            while (iterator.hasNext()){
//                int asByte = iterator.next().getAsInt();
//                result[index++]=asByte;
//            }
//            return  new DataWrapper(DataType.INT_ARRAY,result);
//        }
//        else if (dataType.equals(DataType.LONG_ARRAY)) {
//            JsonArray array=object.getAsJsonArray();
//            Iterator<JsonElement> iterator = array.iterator();
//            long[] result=new long[array.size()];
//            int index=0;
//            while (iterator.hasNext()){
//                long asByte = iterator.next().getAsLong();
//                result[index++]=asByte;
//            }
//            return  new DataWrapper(DataType.LONG_ARRAY,result);
//        }
//        else if (dataType.equals(DataType.FLOAT_ARRAY)) {
//            JsonArray array=object.getAsJsonArray();
//            Iterator<JsonElement> iterator = array.iterator();
//            float[] result=new float[array.size()];
//            int index=0;
//            while (iterator.hasNext()){
//                float asByte = iterator.next().getAsFloat();
//                result[index++]=asByte;
//            }
//            return  new DataWrapper(DataType.FLOAT_ARRAY,result);
//        }
//        else if (dataType.equals(DataType.DOUBLE_ARRAY)) {
//            JsonArray array=object.getAsJsonArray();
//            Iterator<JsonElement> iterator = array.iterator();
//            double[] result=new double[array.size()];
//            int index=0;
//            while (iterator.hasNext()){
//                double asByte = iterator.next().getAsDouble();
//                result[index++]=asByte;
//            }
//            return  new DataWrapper(DataType.DOUBLE_ARRAY,result);
//        }
//        else if (dataType.equals(DataType.UTF_STRING_ARRAY)) {
//            JsonArray array=object.getAsJsonArray();
//            Iterator<JsonElement> iterator = array.iterator();
//            String[] result=new String[array.size()];
//            int index=0;
//            while (iterator.hasNext()){
//                String asByte = iterator.next().getAsString();
//                result[index++]=asByte;
//            }
//            return  new DataWrapper(DataType.UTF_STRING_ARRAY,result);
//        }
//        else if (dataType.equals(DataType.OBJECT)){
//            return new DataWrapper(DataType.OBJECT,decodeIObject(object.getAsJsonObject()));
//        }
//        else if (dataType.equals(DataType.ARRAY)){
//            return new DataWrapper(DataType.ARRAY,decodeIArray(object.getAsJsonArray()));
//        }
////        else if (dataType.equals(DataType.CLASS)){
////            return new DataWrapper(DataType.CLASS,object.getAsString());
////        }
//        else if (dataType.equals(DataType.ACTOR_REF)){
//            ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
//            ActorRef objectActorRef = ActorRefResolver.get(actorSystem).resolveActorRef(object.getAsString());
//            DataWrapper decodedObject = new DataWrapper(DataType.ACTOR_REF, objectActorRef);
//            return decodedObject;
//        }else if (dataType.equals(DataType.ACTOR_REF_ARRAY)){
//            ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
//            ActorRef objectActorRef = ActorRefResolver.get(actorSystem).resolveActorRef(object.getAsString());
//            DataWrapper decodedObject = new DataWrapper(DataType.ACTOR_REF, objectActorRef);
//            return decodedObject;
//        }
//
//        else {
//            throw new IllegalArgumentException(
//                    String.format("Unrecognized DataType while converting JSONObject 2 Code Broker Object. Object: %s, Type: %s",
//                            new Object[]{object, object == null ? "null" : object.getClass()}));
//        }
//    }
//
//    public byte[] object2binary(IObject object) {
//        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CHUNK_SIZE);
//        buffer.put((byte) DataType.OBJECT.typeID);
//        buffer.putShort((short) object.size());
//        return this.obj2bin(object, buffer);
//    }
//
//
//    private byte[] obj2bin(IObject object, ByteBuffer buffer) {
//        Set<String> keys = object.getKeys();
//
//        DataWrapper wrapper;
//        Object dataObj;
//        for (Iterator<String> result = keys.iterator();
//             result.hasNext();
//             buffer = this.encodeObject(buffer, wrapper.getTypeId(), dataObj)) {
//            String pos = result.next();
//            wrapper = object.get(pos);
//            dataObj = wrapper.getObject();
//            buffer = this.encodeIObjectKey(buffer, pos);
//        }
//
//        int pos1 = buffer.position();
//        byte[] result1 = new byte[pos1];
//        buffer.flip();
//        buffer.get(result1, 0, pos1);
//        return result1;
//    }
//
//    public byte[] array2binary(IArray array) {
//        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CHUNK_SIZE);
//        buffer.put((byte) DataType.ARRAY.typeID);
//        buffer.putShort((short) array.size());
//        return this.arr2bin(array, buffer);
//    }
//
//    private byte[] arr2bin(IArray array, ByteBuffer buffer) {
//        DataWrapper wrapper;
//        Object pos;
//        for (Iterator<DataWrapper> iter = array.iterator();
//             iter.hasNext();
//             buffer = this.encodeObject(buffer, wrapper.getTypeId(), pos)) {
//            wrapper = iter.next();
//            pos = wrapper.getObject();
//        }
//
//        int pos1 = buffer.position();
//        byte[] result = new byte[pos1];
//        buffer.flip();
//        buffer.get(result, 0, pos1);
//        return result;
//    }
//
//    public void flattenObject(Map<String, Object> map, CObject cObject) {
//        Iterator<Entry<String, DataWrapper>> iterator = cObject.iterator();
//
//        while (iterator.hasNext()) {
//            Entry<String, DataWrapper> entry = iterator.next();
//            String key = entry.getKey();
//            DataWrapper value = entry.getValue();
//            if (value.getTypeId() == DataType.OBJECT) {
//                HashMap<String, Object> objectHashMap = new HashMap<>();
//                map.put(key, objectHashMap);
//                this.flattenObject(objectHashMap, (CObject) value.getObject());
//            } else if (value.getTypeId() == DataType.ARRAY) {
//                ArrayList<Object> objectArrayList = new ArrayList<>();
//                map.put(key, objectArrayList);
//                this.flattenArray(objectArrayList, (CArray) value.getObject());
//            }
//            else {
//
//                map.put(key, value);
//            }
//        }
//
//    }
//
//    public void flattenArray(List<Object> array, CArray cArray) {
//        Iterator<DataWrapper> iterator = cArray.iterator();
//
//        while (iterator.hasNext()) {
//            DataWrapper value = iterator.next();
//            if (value.getTypeId() == DataType.OBJECT) {
//                HashMap<String, Object> newList = new HashMap<>();
//                array.add(newList);
//                this.flattenObject(newList, (CObject) value.getObject());
//            } else if (value.getTypeId() == DataType.ARRAY) {
//                ArrayList<Object> objectArrayList = new ArrayList<>();
//                array.add(objectArrayList);
//                this.flattenArray(objectArrayList, (CArray) value.getObject());
//            } else {
//                array.add(value);
//            }
//        }
//
//    }
//
//    private DataWrapper decodeObject(ByteBuffer buffer) throws CodecException {
//        DataWrapper decodedObject;
//        byte headerByte = buffer.get();
//        if (headerByte == DataType.NULL.typeID) {
//            decodedObject = this.binDecode_NULL(buffer);
//        } else if (headerByte == DataType.BOOL.typeID) {
//            decodedObject = this.binDecode_BOOL(buffer);
//        } else if (headerByte == DataType.BOOL_ARRAY.typeID) {
//            decodedObject = this.binDecode_BOOL_ARRAY(buffer);
//        } else if (headerByte == DataType.BYTE.typeID) {
//            decodedObject = this.binDecode_BYTE(buffer);
//        } else if (headerByte == DataType.BYTE_ARRAY.typeID) {
//            decodedObject = this.binDecode_BYTE_ARRAY(buffer);
//        } else if (headerByte == DataType.SHORT.typeID) {
//            decodedObject = this.binDecode_SHORT(buffer);
//        } else if (headerByte == DataType.SHORT_ARRAY.typeID) {
//            decodedObject = this.binDecode_SHORT_ARRAY(buffer);
//        } else if (headerByte == DataType.INT.typeID) {
//            decodedObject = this.binDecode_INT(buffer);
//        } else if (headerByte == DataType.INT_ARRAY.typeID) {
//            decodedObject = this.binDecode_INT_ARRAY(buffer);
//        } else if (headerByte == DataType.LONG.typeID) {
//            decodedObject = this.binDecode_LONG(buffer);
//        } else if (headerByte == DataType.LONG_ARRAY.typeID) {
//            decodedObject = this.binDecode_LONG_ARRAY(buffer);
//        } else if (headerByte == DataType.FLOAT.typeID) {
//            decodedObject = this.binDecode_FLOAT(buffer);
//        } else if (headerByte == DataType.FLOAT_ARRAY.typeID) {
//            decodedObject = this.binDecode_FLOAT_ARRAY(buffer);
//        } else if (headerByte == DataType.DOUBLE.typeID) {
//            decodedObject = this.binDecode_DOUBLE(buffer);
//        } else if (headerByte == DataType.DOUBLE_ARRAY.typeID) {
//            decodedObject = this.binDecode_DOUBLE_ARRAY(buffer);
//        } else if (headerByte == DataType.UTF_STRING.typeID) {
//            decodedObject = this.binDecode_UTF_STRING(buffer);
//        } else if (headerByte == DataType.UTF_STRING_ARRAY.typeID) {
//            decodedObject = this.binDecode_UTF_STRING_ARRAY(buffer);
//        } else if (headerByte == DataType.ARRAY.typeID) {
//            buffer.position(buffer.position() - 1);
//            decodedObject = new DataWrapper(DataType.ARRAY, this.decodeIArray(buffer));
//        } else if (headerByte == DataType.ACTOR_REF.typeID) {
//            short strLen = buffer.getShort();
//            if (strLen < 0) {
//                throw new CodecException("Error decoding UtfString. Negative size: " + strLen);
//            } else {
//                byte[] strData = new byte[strLen];
//                buffer.get(strData, 0, strLen);
//                String decodedString = new String(strData);
//                ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
//                ActorRef objectActorRef = ActorRefResolver.get(actorSystem).resolveActorRef(decodedString);
//                decodedObject = new DataWrapper(DataType.ACTOR_REF, objectActorRef);
//            }
//        } else {
//            if (headerByte != DataType.OBJECT.typeID) {
//                throw new CodecException("Unknow DataType ID: " + headerByte);
//            }
//
//            buffer.position(buffer.position() - 1);
//            IObject iObject = this.decodeIObject(buffer);
//            DataType type = DataType.OBJECT;
////            if (iObject.containsKey(CLASS_MARKER_KEY) && iObject.containsKey(CLASS_FIELDS_KEY)
////                    || iObject.containsKey(CLASS_ACTOR_KEY) && iObject.containsKey(ACTOR_VALUE_KEY)
////            ) {
////                type = DataType.CLASS;
////                decodedObject = new DataWrapper(type, this.cbo2pojo(iObject));
////            } else {
//                decodedObject = new DataWrapper(type, iObject);
////            }
//        }
//        return decodedObject;
//    }
//
//    private ByteBuffer encodeObject(ByteBuffer buffer, DataType typeId, Object object) {
//        switch (typeId) {
//            case NULL:
//                buffer = this.binEncode_NULL(buffer);
//                break;
//            case BOOL:
//                buffer = this.binEncode_BOOL(buffer, (Boolean) object);
//                break;
//            case BYTE:
//                buffer = this.binEncode_BYTE(buffer, (Byte) object);
//                break;
//            case SHORT:
//                buffer = this.binEncode_SHORT(buffer, (Short) object);
//                break;
//            case INT:
//                buffer = this.binEncode_INT(buffer, (Integer) object);
//                break;
//            case LONG:
//                buffer = this.binEncode_LONG(buffer, (Long) object);
//                break;
//            case FLOAT:
//                buffer = this.binEncode_FLOAT(buffer, (Float) object);
//                break;
//            case DOUBLE:
//                buffer = this.binEncode_DOUBLE(buffer, (Double) object);
//                break;
//            case UTF_STRING:
//                buffer = this.binEncode_UTF_STRING(buffer, (String) object);
//                break;
//            case BOOL_ARRAY:
//                buffer = this.binEncode_BOOL_ARRAY(buffer, (Collection<Boolean>) object);
//                break;
//            case BYTE_ARRAY:
//                buffer = this.binEncode_BYTE_ARRAY(buffer, (byte[]) object);
//                break;
//            case SHORT_ARRAY:
//                buffer = this.binEncode_SHORT_ARRAY(buffer, (Collection<Short>) object);
//                break;
//            case INT_ARRAY:
//                buffer = this.binEncode_INT_ARRAY(buffer, (Collection<Integer>) object);
//                break;
//            case LONG_ARRAY:
//                buffer = this.binEncode_LONG_ARRAY(buffer, (Collection<Long>) object);
//                break;
//            case FLOAT_ARRAY:
//                buffer = this.binEncode_FLOAT_ARRAY(buffer, (Collection<Float>) object);
//                break;
//            case DOUBLE_ARRAY:
//                buffer = this.binEncode_DOUBLE_ARRAY(buffer, (Collection<Double>) object);
//                break;
//            case UTF_STRING_ARRAY:
//                buffer = this.binEncode_UTF_STRING_ARRAY(buffer, (Collection<String>) object);
//                break;
//            case ACTOR_REF:
//                buffer = this.binEncode_UTF_Actor_Ref(buffer, (ActorRef) object);
//                break;
//            case ACTOR_REF_ARRAY:
//                buffer = this.binEncode_UTF_Actor_Ref(buffer, (ActorRef) object);
//                break;
//            case ARRAY:
//                buffer = this.addData(buffer, this.array2binary((CArray) object));
//                break;
//            case OBJECT:
//                buffer = this.addData(buffer, this.object2binary((CObject) object));
//                break;
////            case CLASS:
////                buffer = this.addData(buffer, this.object2binary(this.pojo2cbo(object)));
////                break;
//            default:
//                throw new IllegalArgumentException("Unrecognized type in AVAObject serialization: " + typeId);
//        }
//
//        return buffer;
//    }
//
//    private DataWrapper binDecode_NULL(ByteBuffer buffer) {
//        return new DataWrapper(DataType.NULL, null);
//    }
//
//    private DataWrapper binDecode_BOOL(ByteBuffer buffer) throws CodecException {
//        byte boolByte = buffer.get();
//        Boolean bool = null;
//        if (boolByte == 0) {
//            bool = new Boolean(false);
//        } else {
//            if (boolByte != 1) {
//                throw new CodecException("Error decoding Bool type. Illegal value: " + bool);
//            }
//
//            bool = new Boolean(true);
//        }
//
//        return new DataWrapper(DataType.BOOL, bool);
//    }
//
//    private DataWrapper binDecode_BYTE(ByteBuffer buffer) {
//        byte boolByte = buffer.get();
//        return new DataWrapper(DataType.BYTE, Byte.valueOf(boolByte));
//    }
//
//    private DataWrapper binDecode_SHORT(ByteBuffer buffer) {
//        short shortValue = buffer.getShort();
//        return new DataWrapper(DataType.SHORT, Short.valueOf(shortValue));
//    }
//
//    private DataWrapper binDecode_INT(ByteBuffer buffer) {
//        int intValue = buffer.getInt();
//        return new DataWrapper(DataType.INT, Integer.valueOf(intValue));
//    }
//
//    private DataWrapper binDecode_LONG(ByteBuffer buffer) {
//        long longValue = buffer.getLong();
//        return new DataWrapper(DataType.LONG, Long.valueOf(longValue));
//    }
//
//    private DataWrapper binDecode_FLOAT(ByteBuffer buffer) {
//        float floatValue = buffer.getFloat();
//        return new DataWrapper(DataType.FLOAT, Float.valueOf(floatValue));
//    }
//
//    private DataWrapper binDecode_DOUBLE(ByteBuffer buffer) {
//        double doubleValue = buffer.getDouble();
//        return new DataWrapper(DataType.DOUBLE, Double.valueOf(doubleValue));
//    }
//
//    private DataWrapper binDecode_UTF_STRING(ByteBuffer buffer) throws CodecException {
//        short strLen = buffer.getShort();
//        if (strLen < 0) {
//            throw new CodecException("Error decoding UtfString. Negative size: " + strLen);
//        } else {
//            byte[] strData = new byte[strLen];
//            buffer.get(strData, 0, strLen);
//            String decodedString = new String(strData);
//            return new DataWrapper(DataType.UTF_STRING, decodedString);
//        }
//    }
//
//    private DataWrapper binDecode_BOOL_ARRAY(ByteBuffer buffer) throws CodecException {
//        short arraySize = this.getTypeArraySize(buffer);
//        ArrayList<Boolean> array = new ArrayList<>();
//
//        for (int i = 0; i < arraySize; ++i) {
//            byte boolData = buffer.get();
//            if (boolData == 0) {
//                array.add(Boolean.valueOf(false));
//            } else {
//                if (boolData != 1) {
//                    throw new CodecException("Error decoding BoolArray. Invalid bool value: " + boolData);
//                }
//
//                array.add(Boolean.valueOf(true));
//            }
//        }
//
//        return new DataWrapper(DataType.BOOL_ARRAY, array);
//    }
//
//    private DataWrapper binDecode_BYTE_ARRAY(ByteBuffer buffer) throws CodecException {
//        int arraySize = buffer.getInt();
//        if (arraySize < 0) {
//            throw new CodecException("Error decoding typed array size. Negative size: " + arraySize);
//        } else {
//            byte[] byteData = new byte[arraySize];
//            buffer.get(byteData, 0, arraySize);
//            return new DataWrapper(DataType.BYTE_ARRAY, byteData);
//        }
//    }
//
//    private DataWrapper binDecode_SHORT_ARRAY(ByteBuffer buffer) throws CodecException {
//        short arraySize = this.getTypeArraySize(buffer);
//        ArrayList<Short> array = new ArrayList<>();
//
//        for (int i = 0; i < arraySize; ++i) {
//            short shortValue = buffer.getShort();
//            array.add(Short.valueOf(shortValue));
//        }
//
//        return new DataWrapper(DataType.SHORT_ARRAY, array);
//    }
//
//    private DataWrapper binDecode_INT_ARRAY(ByteBuffer buffer) throws CodecException {
//        short arraySize = this.getTypeArraySize(buffer);
//        ArrayList array = new ArrayList();
//
//        for (int i = 0; i < arraySize; ++i) {
//            int intValue = buffer.getInt();
//            array.add(Integer.valueOf(intValue));
//        }
//
//        return new DataWrapper(DataType.INT_ARRAY, array);
//    }
//
//    private DataWrapper binDecode_LONG_ARRAY(ByteBuffer buffer) throws CodecException {
//        short arraySize = this.getTypeArraySize(buffer);
//        ArrayList array = new ArrayList();
//
//        for (int i = 0; i < arraySize; ++i) {
//            long longValue = buffer.getLong();
//            array.add(Long.valueOf(longValue));
//        }
//
//        return new DataWrapper(DataType.LONG_ARRAY, array);
//    }
//
//    private DataWrapper binDecode_FLOAT_ARRAY(ByteBuffer buffer) throws CodecException {
//        short arraySize = this.getTypeArraySize(buffer);
//        ArrayList array = new ArrayList();
//
//        for (int i = 0; i < arraySize; ++i) {
//            float floatValue = buffer.getFloat();
//            array.add(Float.valueOf(floatValue));
//        }
//
//        return new DataWrapper(DataType.FLOAT_ARRAY, array);
//    }
//
//    private DataWrapper binDecode_DOUBLE_ARRAY(ByteBuffer buffer) throws CodecException {
//        short arraySize = this.getTypeArraySize(buffer);
//        ArrayList array = new ArrayList();
//
//        for (int i = 0; i < arraySize; ++i) {
//            double doubleValue = buffer.getDouble();
//            array.add(Double.valueOf(doubleValue));
//        }
//
//        return new DataWrapper(DataType.DOUBLE_ARRAY, array);
//    }
//
//    private DataWrapper binDecode_UTF_STRING_ARRAY(ByteBuffer buffer) throws CodecException {
//        short arraySize = this.getTypeArraySize(buffer);
//        ArrayList array = new ArrayList();
//
//        for (int i = 0; i < arraySize; ++i) {
//            short strLen = buffer.getShort();
//            if (strLen < 0) {
//                throw new CodecException(
//                        "Error decoding UtfStringArray element. Element has negative size: " + strLen);
//            }
//
//            byte[] strData = new byte[strLen];
//            buffer.get(strData, 0, strLen);
//            array.add(new String(strData));
//        }
//
//        return new DataWrapper(DataType.UTF_STRING_ARRAY, array);
//    }
//
//    private short getTypeArraySize(ByteBuffer buffer) throws CodecException {
//        short arraySize = buffer.getShort();
//        if (arraySize < 0) {
//            throw new CodecException("Error decoding typed array size. Negative size: " + arraySize);
//        } else {
//            return arraySize;
//        }
//    }
//
//    private ByteBuffer binEncode_NULL(ByteBuffer buffer) {
//        return this.addData(buffer, new byte[1]);
//    }
//
//    private ByteBuffer binEncode_BOOL(ByteBuffer buffer, Boolean value) {
//        byte[] data = new byte[]{(byte) DataType.BOOL.typeID, (byte) (value.booleanValue() ? 1 : 0)};
//        return this.addData(buffer, data);
//    }
//
//    private ByteBuffer binEncode_BYTE(ByteBuffer buffer, Byte value) {
//        byte[] data = new byte[]{(byte) DataType.BYTE.typeID, value.byteValue()};
//        return this.addData(buffer, data);
//    }
//
//    private ByteBuffer binEncode_SHORT(ByteBuffer buffer, Short value) {
//        ByteBuffer buf = ByteBuffer.allocate(3);
//        buf.put((byte) DataType.SHORT.typeID);
//        buf.putShort(value.shortValue());
//        return this.addData(buffer, buf.array());
//    }
//
//    private ByteBuffer binEncode_INT(ByteBuffer buffer, Integer value) {
//        ByteBuffer buf = ByteBuffer.allocate(5);
//        buf.put((byte) DataType.INT.typeID);
//        buf.putInt(value.intValue());
//        return this.addData(buffer, buf.array());
//    }
//
//    private ByteBuffer binEncode_LONG(ByteBuffer buffer, Long value) {
//        ByteBuffer buf = ByteBuffer.allocate(9);
//        buf.put((byte) DataType.LONG.typeID);
//        buf.putLong(value.longValue());
//        return this.addData(buffer, buf.array());
//    }
//
//    private ByteBuffer binEncode_FLOAT(ByteBuffer buffer, Float value) {
//        ByteBuffer buf = ByteBuffer.allocate(5);
//        buf.put((byte) DataType.FLOAT.typeID);
//        buf.putFloat(value.floatValue());
//        return this.addData(buffer, buf.array());
//    }
//
//    private ByteBuffer binEncode_DOUBLE(ByteBuffer buffer, Double value) {
//        ByteBuffer buf = ByteBuffer.allocate(9);
//        buf.put((byte) DataType.DOUBLE.typeID);
//        buf.putDouble(value.doubleValue());
//        return this.addData(buffer, buf.array());
//    }
//
//    private ByteBuffer binEncode_UTF_STRING(ByteBuffer buffer, String value) {
//        byte[] stringBytes = value.getBytes();
//        ByteBuffer buf = ByteBuffer.allocate(3 + stringBytes.length);
//        buf.put((byte) DataType.UTF_STRING.typeID);
//        buf.putShort((short) stringBytes.length);
//        buf.put(stringBytes);
//        return this.addData(buffer, buf.array());
//    }
//
//    private ByteBuffer binEncode_BOOL_ARRAY(ByteBuffer buffer, Collection<Boolean> value) {
//        ByteBuffer buf = ByteBuffer.allocate(3 + value.size());
//        buf.put((byte) DataType.BOOL_ARRAY.typeID);
//        buf.putShort((short) value.size());
//        Iterator<Boolean> iterator = value.iterator();
//
//        while (iterator.hasNext()) {
//            boolean b = iterator.next();
//            buf.put((byte) (b ? 1 : 0));
//        }
//
//        return this.addData(buffer, buf.array());
//    }
//
//    private ByteBuffer binEncode_BYTE_ARRAY(ByteBuffer buffer, byte[] value) {
//        ByteBuffer buf = ByteBuffer.allocate(5 + value.length);
//        buf.put((byte) DataType.BYTE_ARRAY.typeID);
//        buf.putInt(value.length);
//        buf.put(value);
//        return this.addData(buffer, buf.array());
//    }
//
//    private ByteBuffer binEncode_SHORT_ARRAY(ByteBuffer buffer, Collection<Short> value) {
//        ByteBuffer buf = ByteBuffer.allocate(3 + 2 * value.size());
//        buf.put((byte) DataType.SHORT_ARRAY.typeID);
//        buf.putShort((short) value.size());
//        Iterator<Short> iterator = value.iterator();
//
//        while (iterator.hasNext()) {
//            short item = iterator.next();
//            buf.putShort(item);
//        }
//
//        return this.addData(buffer, buf.array());
//    }
//
//    private ByteBuffer binEncode_INT_ARRAY(ByteBuffer buffer, Collection<Integer> value) {
//        ByteBuffer buf = ByteBuffer.allocate(3 + 4 * value.size());
//        buf.put((byte) DataType.INT_ARRAY.typeID);
//        buf.putShort((short) value.size());
//        Iterator<Integer> iterator = value.iterator();
//
//        while (iterator.hasNext()) {
//            int item = iterator.next();
//            buf.putInt(item);
//        }
//
//        return this.addData(buffer, buf.array());
//    }
//
//    private ByteBuffer binEncode_LONG_ARRAY(ByteBuffer buffer, Collection<Long> value) {
//        ByteBuffer buf = ByteBuffer.allocate(3 + 8 * value.size());
//        buf.put((byte) DataType.LONG_ARRAY.typeID);
//        buf.putShort((short) value.size());
//        Iterator<Long> iterator = value.iterator();
//
//        while (iterator.hasNext()) {
//            long item = iterator.next();
//            buf.putLong(item);
//        }
//        return this.addData(buffer, buf.array());
//    }
//
//    private ByteBuffer binEncode_FLOAT_ARRAY(ByteBuffer buffer, Collection<Float> value) {
//        ByteBuffer buf = ByteBuffer.allocate(3 + 4 * value.size());
//        buf.put((byte) DataType.FLOAT_ARRAY.typeID);
//        buf.putShort((short) value.size());
//
//        Iterator<Float> iterator = value.iterator();
//        while (iterator.hasNext()) {
//            float item = iterator.next();
//            buf.putFloat(item);
//        }
//
//        return this.addData(buffer, buf.array());
//    }
//
//    private ByteBuffer binEncode_DOUBLE_ARRAY(ByteBuffer buffer, Collection<Double> value) {
//        ByteBuffer buf = ByteBuffer.allocate(3 + 8 * value.size());
//        buf.put((byte) DataType.DOUBLE_ARRAY.typeID);
//        buf.putShort((short) value.size());
//        Iterator<Double> iterator = value.iterator();
//
//        while (iterator.hasNext()) {
//            double item = (iterator.next()).doubleValue();
//            buf.putDouble(item);
//        }
//
//        return this.addData(buffer, buf.array());
//    }
//
//    public ByteBuffer binEncode_UTF_Actor_Ref(ByteBuffer buffer, ActorRef object) {
//        ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
//        String value = ActorRefResolver.get(actorSystem).toSerializationFormat(object);
//        byte[] stringBytes = value.getBytes();
//        ByteBuffer buf = ByteBuffer.allocate(3 + stringBytes.length);
//        buf.put((byte) DataType.ACTOR_REF.typeID);
//        buf.putShort((short) stringBytes.length);
//        buf.put(stringBytes);
//        return this.addData(buffer, buf.array());
//    }
//
//    private ByteBuffer binEncode_UTF_STRING_ARRAY(ByteBuffer buffer, Collection<String> value) {
//        int stringDataLen = 0;
//        byte[][] binStrings = new byte[value.size()][];
//        int count = 0;
//
//        byte[] binStr;
//        for (Iterator<String> binItem = value.iterator(); binItem.hasNext(); stringDataLen += 2 + binStr.length) {
//            String buf = binItem.next();
//            binStr = buf.getBytes();
//            binStrings[count++] = binStr;
//        }
//
//        ByteBuffer byteBuffer = ByteBuffer.allocate(3 + stringDataLen);
//        byteBuffer.put((byte) DataType.UTF_STRING_ARRAY.typeID);
//        byteBuffer.putShort((short) value.size());
//        int length = binStrings.length;
//
//        for (int i = 0; i < length; ++i) {
//            byte[] binString = binStrings[i];
//            byteBuffer.putShort((short) binString.length);
//            byteBuffer.put(binString);
//        }
//
//        return this.addData(buffer, byteBuffer.array());
//    }
//
//    private ByteBuffer encodeIObjectKey(ByteBuffer buffer, String value) {
//        ByteBuffer buf = ByteBuffer.allocate(2 + value.length());
//        buf.putShort((short) value.length());
//        buf.put(value.getBytes());
//        return this.addData(buffer, buf.array());
//    }
//
//    private ByteBuffer addData(ByteBuffer buffer, byte[] newData) {
//        if (buffer.remaining() < newData.length) {
//            int newSize = BUFFER_CHUNK_SIZE;
//            if (newSize < newData.length) {
//                newSize = newData.length;
//            }
//
//            ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() + newSize);
//            buffer.flip();
//            newBuffer.put(buffer);
//            buffer = newBuffer;
//        }
//
//        buffer.put(newData);
//        return buffer;
//    }
//
//    public IObject pojo2cbo(Object pojo) {
//        CObject cObject = CObject.newInstance();
//        try {
//            this.convertPojo(pojo, cObject);
//            return cObject;
//        } catch (Exception exception) {
//            throw new CRuntimeException(exception);
//        }
//    }
//
//    private void convertPojo(Object pojo, IObject iObject) throws IllegalArgumentException {
//        Class pojoClazz = pojo.getClass();
//        String classFullName = pojoClazz.getName();
//        if (classFullName == null) {
//            throw new IllegalArgumentException("Anonymous classes cannot be serialized!");
//        } else if (!(pojo instanceof SerializableType)) {
//            throw new IllegalStateException("Cannot serialize object: " + pojo + ", type: " + classFullName
//                    + " -- It doesn\'t implement the Serializable Type interface");
//        } else if (pojo instanceof IGameUser) {
//            GameUser gameUser = (GameUser) pojo;
//            ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
//            String serializationFormat = ActorRefResolver.get(actorSystem).toSerializationFormat(gameUser.getActorRef());
//            iObject.putUtfString(CLASS_ACTOR_KEY, gameUser.getUserId());
//            iObject.putUtfString(ACTOR_VALUE_KEY, serializationFormat);
//        } else {
//            CArray fieldList = CArray.newInstance();
//            iObject.putUtfString(CLASS_MARKER_KEY, classFullName);
//            iObject.putIArray(CLASS_FIELDS_KEY, fieldList);
//            Field[] fields;
//            int length = (fields = pojoClazz.getDeclaredFields()).length;
//
//            for (int i = 0; i < length; ++i) {
//                Field field = fields[i];
//
//                try {
//                    int err = field.getModifiers();
//                    if (!Modifier.isTransient(err) && !Modifier.isStatic(err)) {
//                        String fieldName = field.getName();
//                        Object fieldValue;
//                        if (Modifier.isPublic(err)) {
//                            fieldValue = field.get(pojo);
//                        } else {
//                            fieldValue = this.readValueFromGetter(fieldName, field.getType().getSimpleName(), pojo);
//                        }
//
//                        CObject fieldDescriptor = CObject.newInstance();
//                        fieldDescriptor.putUtfString(FIELD_NAME_KEY, fieldName);
//                        fieldDescriptor.put(FIELD_VALUE_KEY, this.wrapPojoField(fieldValue));
//                        fieldList.addIObject(fieldDescriptor);
//                    }
//                } catch (Exception e) {
//                    this.logger.info("-- No public getter -- Serializer skipping private field: " + field.getName()
//                            + ", from class: " + pojoClazz);
//                    e.printStackTrace();
//                }
//            }
//
//        }
//    }
//
//    private Object readValueFromGetter(String fieldName, String type, Object pojo) throws Exception {
//        Object value;
//        boolean isBool = type.equalsIgnoreCase("boolean");
//        String getterName = isBool ? "is" + StringUtils.capitalize(fieldName) : "get" + StringUtils.capitalize(fieldName);
//        Method getterMethod = pojo.getClass().getMethod(getterName, new Class[0]);
//        value = getterMethod.invoke(pojo, new Object[0]);
//        return value;
//    }
//
//    private DataWrapper wrapPojoField(Object value) {
//        if (value == null) {
//            return new DataWrapper(DataType.NULL, null);
//        } else {
//            DataWrapper wrapper = null;
//            if (value instanceof Boolean) {
//                wrapper = new DataWrapper(DataType.BOOL, value);
//            } else if (value instanceof Byte) {
//                wrapper = new DataWrapper(DataType.BYTE, value);
//            } else if (value instanceof Short) {
//                wrapper = new DataWrapper(DataType.SHORT, value);
//            } else if (value instanceof Integer) {
//                wrapper = new DataWrapper(DataType.INT, value);
//            } else if (value instanceof Long) {
//                wrapper = new DataWrapper(DataType.LONG, value);
//            } else if (value instanceof Float) {
//                wrapper = new DataWrapper(DataType.FLOAT, value);
//            } else if (value instanceof Double) {
//                wrapper = new DataWrapper(DataType.DOUBLE, value);
//            } else if (value instanceof String) {
//                wrapper = new DataWrapper(DataType.UTF_STRING, value);
//            } else if (value instanceof ActorRef) {
//                wrapper = new DataWrapper(DataType.ACTOR_REF, value);
//            } else if (value instanceof IObject) {
//                wrapper = new DataWrapper(DataType.OBJECT, value);
//            } else if (value.getClass().isArray()) {
//                wrapper = new DataWrapper(DataType.ARRAY, this.unrollArray((Object[]) value));
//            } else if (value instanceof Collection) {
//                wrapper = new DataWrapper(DataType.ARRAY, this.unrollCollection((Collection) value));
//            } else if (value instanceof Map) {
//                wrapper = new DataWrapper(DataType.OBJECT, this.unrollMap((Map) value));
//            } else if (value instanceof SerializableType) {
//                wrapper = new DataWrapper(DataType.OBJECT, this.pojo2cbo(value));
//            }
//            return wrapper;
//        }
//    }
//
//    private IArray unrollArray(Object[] arr) {
//        CArray array = CArray.newInstance();
//        for (int i = 0; i < arr.length; ++i) {
//            Object item = arr[i];
//            array.add(this.wrapPojoField(item));
//        }
//
//        return array;
//    }
//
//    private IArray unrollCollection(Collection collection) {
//        CArray array = CArray.newInstance();
//        Iterator<?> iterator = collection.iterator();
//
//        while (iterator.hasNext()) {
//            Object item = iterator.next();
//            array.add(this.wrapPojoField(item));
//        }
//
//        return array;
//    }
//
//    private IObject unrollMap(Map map) {
//        CObject cObject = CObject.newInstance();
//        Set entries = map.entrySet();
//        Iterator<Entry> iter = entries.iterator();
//
//        while (iter.hasNext()) {
//            Entry item = iter.next();
//            Object key = item.getKey();
//            if (key instanceof String) {
//                cObject.put((String) key, this.wrapPojoField(item.getValue()));
//            }
//        }
//
//        return cObject;
//    }
//
//    public Object cbo2pojo(IObject iObject) {
//        Object pojo;
//        if (!iObject.containsKey(CLASS_MARKER_KEY)
//                && !iObject.containsKey(CLASS_FIELDS_KEY)
//                && !iObject.containsKey(CLASS_ACTOR_KEY)
//                && !iObject.containsKey(ACTOR_VALUE_KEY)
//        ) {
//            throw new CRuntimeException("The IObject passed does not represent any serialized class.");
//        } else {
//            try {
//                if (iObject.containsKey(CLASS_ACTOR_KEY)) {
////                    ActorSystem<IGameRootSystemMessage> actorSystem = ContextResolver.getActorSystem();
////                    ActorRef<IUser> objectActorRef = ActorRefResolver.get(actorSystem).resolveActorRef(iObject.getUtfString(ACTOR_VALUE_KEY));
////                    GameUserProxy gameUserProxy = new GameUserProxy(iObject.getUtfString(CLASS_ACTOR_KEY), objectActorRef);
////                    return gameUserProxy;
//                    return null;
//                } else {
//                    String string = iObject.getUtfString(CLASS_MARKER_KEY);
//                    Class theClass = Class.forName(string);
//
//                    Constructor[] constructors = theClass.getConstructors();
//                    //检查是否有无参数构造,如果多个构造函数选择参数最多的构造
//                    boolean noArg = false;
//                    Constructor use = null;
//                    for (Constructor constructor : constructors) {
//                        if (constructor.getParameterCount() == 0) {
//                            noArg = true;
//                            break;
//                        } else {
//                            if (use == null) {
//                                use = constructor;
//                            } else {
//                                if (use.getParameterCount() < constructor.getParameterCount()) {
//                                    use = constructor;
//                                }
//                            }
//                        }
//                    }
//                    if (noArg) {
//                        pojo = theClass.newInstance();
//                        convertSFSObject(iObject.getIArray(CLASS_FIELDS_KEY), pojo);
//                        return pojo;
//                    } else {
//                        FieldAccess fieldAccess = FieldAccess.get(theClass);
//                        Field[] fields = fieldAccess.getFields();
//                        Object[] arg = new Object[use.getParameterTypes().length];
//                        IArray iArray = iObject.getIArray(CLASS_FIELDS_KEY);
//                        for (int i = 0; i < use.getParameterTypes().length; i++) {
//                            Class<?> parameterType = use.getParameterTypes()[i];
//                            for (Field field : fields) {
//                                if (field.getType().equals(parameterType)) {
//                                    for (int j = 0; j < iArray.size(); j++) {
//                                        IObject object = iArray.getObject(j);
//                                        String utfString = object.getUtfString(FIELD_NAME_KEY);
//                                        if (utfString.equals(field.getName())) {
//                                            arg[i] = object.get(FIELD_VALUE_KEY).getObject();
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        return theClass.getConstructor(use.getParameterTypes()).newInstance(arg);
//                    }
//                }
//            } catch (Exception exception) {
//                throw new CRuntimeException(exception);
//            }
//        }
//    }
//
//    private void convertSFSObject(IArray iArray, Object pojo) throws Exception {
//        for (int i = 0; i < iArray.size(); ++i) {
//            IObject fieldDescriptor = iArray.getObject(i);
//            String fieldName = fieldDescriptor.getUtfString(FIELD_NAME_KEY);
//            Object fieldValue = this.unwrapPojoField(fieldDescriptor.get(FIELD_VALUE_KEY));
//            this.setObjectField(pojo, fieldName, fieldValue);
//        }
//
//    }
//
//    private void setObjectField(Object pojo, String fieldName, Object fieldValue) throws Exception {
//        Class<?> pojoClass = pojo.getClass();
//        Field field = pojoClass.getDeclaredField(fieldName);
//        int fieldModifier = field.getModifiers();
//        if (!Modifier.isTransient(fieldModifier)) {
//            boolean isArray = field.getType().isArray();
//            Collection<?> collection;
//            if (isArray) {
//                if (!(fieldValue instanceof Collection)) {
//                    throw new CRuntimeException(
//                            "Problem during IObject => POJO conversion. Found array field in POJO: " + fieldName
//                                    + ", but data is not a Collection!");
//                }
//
//                collection = (Collection) fieldValue;
//                Object[] fieldValue1 = collection.toArray();
//                int fieldClass = collection.size();
//                Object typedArray = Array.newInstance(field.getType().getComponentType(), fieldClass);
//                System.arraycopy(fieldValue1, 0, typedArray, 0, fieldClass);
//                fieldValue = typedArray;
//            } else if (fieldValue instanceof Collection) {
//                collection = (Collection) fieldValue;
//                String simpleName = field.getType().getSimpleName();
//                if (simpleName.equals("ArrayList") || simpleName.equals("List")) {
//                    fieldValue = new ArrayList(collection);
//                }
//                if (simpleName.equals("CopyOnWriteArrayList")) {
//                    fieldValue = new CopyOnWriteArrayList(collection);
//                } else if (simpleName.equals("LinkedList")) {
//                    fieldValue = new LinkedList(collection);
//                } else if (simpleName.equals("Vector")) {
//                    fieldValue = new Vector(collection);
//                } else if (!simpleName.equals("Set") && !simpleName.equals("HashSet")) {
//                    if (simpleName.equals("LinkedHashSet")) {
//                        fieldValue = new LinkedHashSet(collection);
//                    } else if (simpleName.equals("TreeSet")) {
//                        fieldValue = new TreeSet(collection);
//                    } else if (simpleName.equals("CopyOnWriteArraySet")) {
//                        fieldValue = new CopyOnWriteArraySet(collection);
//                    } else if (!simpleName.equals("Queue") && !simpleName.equals("PriorityQueue")) {
//                        if (!simpleName.equals("BlockingQueue") && !simpleName.equals("LinkedBlockingQueue")) {
//                            if (simpleName.equals("PriorityBlockingQueue")) {
//                                fieldValue = new PriorityBlockingQueue(collection);
//                            } else if (simpleName.equals("ConcurrentLinkedQueue")) {
//                                fieldValue = new ConcurrentLinkedQueue(collection);
//                            } else if (simpleName.equals("DelayQueue")) {
//                                fieldValue = new DelayQueue(collection);
//                            } else if (!simpleName.equals("Deque") && !simpleName.equals("ArrayDeque")) {
//                                if (simpleName.equals("LinkedBlockingDeque")) {
//                                    fieldValue = new LinkedBlockingDeque(collection);
//                                }
//                            } else {
//                                fieldValue = new ArrayDeque(collection);
//                            }
//                        } else {
//                            fieldValue = new LinkedBlockingQueue(collection);
//                        }
//                    } else {
//                        fieldValue = new PriorityQueue(collection);
//                    }
//                } else {
//                    fieldValue = new HashSet(collection);
//                }
//            }
//
//            if (Modifier.isPublic(fieldModifier)) {
//                field.set(pojo, fieldValue);
//            } else {
//                this.writeValueFromSetter(field, pojo, fieldValue);
//            }
//
//        }
//    }
//
//    private void writeValueFromSetter(Field field, Object pojo, Object fieldValue) throws Exception {
//        String setterName = "set" + StringUtils.capitalize(field.getName());
//        try {
//            Method setterMethod = pojo.getClass().getMethod(setterName, new Class[]{field.getType()});
//            setterMethod.invoke(pojo, new Object[]{fieldValue});
//        } catch (NoSuchMethodException exception) {
//            this.logger.info("-- No public setter -- Serializer skipping private field: " + field.getName()
//                    + ", from class: " + pojo.getClass().getName());
//        }
//
//    }
//
//    private Object unwrapPojoField(DataWrapper wrapper) {
//        Object obj = null;
//        DataType type = wrapper.getTypeId();
//        if (type.typeID <= DataType.UTF_STRING.typeID) {
//            obj = wrapper.getObject();
//        } else if (type == DataType.ARRAY) {
//            obj = this.rebuildArray((IArray) wrapper.getObject());
//        } else if (type == DataType.OBJECT) {
//            obj = this.rebuildMap((IObject) wrapper.getObject());
//        }
////        else if (type == DataType.CLASS) {
////            obj = wrapper.getObject();
////        }
//        return obj;
//    }
//
//    private Object rebuildArray(IArray sfsArray) {
//        ArrayList<Object> collection = new ArrayList<>();
//        Iterator<DataWrapper> iterator = sfsArray.iterator();
//
//        while (iterator.hasNext()) {
//            Object item = this.unwrapPojoField(iterator.next());
//            collection.add(item);
//        }
//
//        return collection;
//    }
//
//    private Object rebuildMap(IObject iObject) {
//        HashMap map = new HashMap();
//        Iterator<String> iterator = iObject.getKeys().iterator();
//
//        while (iterator.hasNext()) {
//            String key = iterator.next();
//            DataWrapper wrapper = iObject.get(key);
//            map.put(key, this.unwrapPojoField(wrapper));
//        }
//
//        return map;
//    }
//
//    public String array2json(List list) {
//        return gson.toJson(list);
//    }
//
//    @Override
//    public String object2json(Map map) {
//        return gson.toJson(map);
//    }
//
//
//    private IObject decodeIObject(JsonObject jso) {
//        CObject cObject = CObjectLite.newInstance();
//        Set<Entry<String, JsonElement>> iterator = jso.entrySet();
//        for (Entry<String, JsonElement> jsonElementEntry : iterator) {
//            JsonElement value = jsonElementEntry.getValue();
//            if (value.isJsonObject()) {
//                DataWrapper decodedObject = getDataWrapper(value);
//                cObject.put(jsonElementEntry.getKey(), decodedObject);
//            }else if (value.isJsonArray()){
//                JsonArray asJsonArray = value.getAsJsonArray();
//                IArray iArray = decodeIArray(asJsonArray);
//                cObject.putIArray(jsonElementEntry.getKey(),iArray);
//            }
//        }
//        return cObject;
//    }
//
//    private IArray decodeIArray(JsonArray value) {
//        CArray cArray = CArray.newInstance();
//        JsonArray asJsonArray = value.getAsJsonArray();
//        for (JsonElement jsonElement : asJsonArray) {
//            if (jsonElement.isJsonArray()){
//                JsonArray asJsonArray1 = jsonElement.getAsJsonArray();
//                IArray iArray = decodeIArray(asJsonArray1);
//                cArray.addIArray(iArray);
//            }else{
//                DataWrapper decodedObject = getDataWrapper(jsonElement);
//                cArray.add(decodedObject);
//            }
//
//        }
//        return cArray;
//    }
//
//    private DataWrapper getDataWrapper( JsonElement value) {
//        JsonObject jsonObject = value.getAsJsonObject();
//        if (!jsonObject.has("typeId")){
//            throw new IllegalStateException("JsonElement not has typeId");
//        }
//        JsonElement typeId1 = jsonObject.get("typeId");
//        String asString = typeId1.getAsString();
//        DataType dataType = DataType.valueOf(asString);
//        if (!jsonObject.has("object")){
//            throw new IllegalStateException("JsonElement not has object");
//        }
//        JsonElement object = jsonObject.get("object");
//
//        DataWrapper decodedObject = this.decodeJsonObject(dataType, object);
//        if (decodedObject == null) {
//            throw new IllegalStateException("(json2Iobj) Could not decode value for key: " + value.getAsString());
//        }
//        return decodedObject;
//    }
//
//}
//
