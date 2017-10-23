package com.codebroker.core.data;

import com.codebroker.protocol.serialization.DefaultObjectDumpFormatter;
import com.codebroker.protocol.serialization.DefaultSFSDataSerializer;
import com.codebroker.protocol.serialization.IDataSerializer;
import com.codebroker.util.ByteUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class CObject implements IObject {

    private Map<String, DataWrapper> dataHolder = null;
    private IDataSerializer serializer = null;

    public CObject() {
        dataHolder = new ConcurrentHashMap<String, DataWrapper>();
        serializer = DefaultSFSDataSerializer.getInstance();
    }

    public static CObject newFromObject(Object o) {
        return (CObject) DefaultSFSDataSerializer.getInstance().pojo2cbo(o);
    }

    public static CObject newFromBinaryData(byte bytes[]) {
        return (CObject) DefaultSFSDataSerializer.getInstance().binary2object(bytes);
    }

    public static IObject newFromJsonData(String jsonStr) {
        return DefaultSFSDataSerializer.getInstance().json2object(jsonStr);
    }

    public static CObject newInstance() {
        return new CObject();
    }

    public Iterator<Entry<String, DataWrapper>> iterator() {
        Iterator<Entry<String, DataWrapper>> iterator = dataHolder.entrySet().iterator();
        return iterator;
    }

    public boolean containsKey(String key) {
        return dataHolder.containsKey(key);
    }

    public boolean removeElement(String key) {
        return dataHolder.remove(key) != null;
    }

    public int size() {
        return dataHolder.size();
    }

    public byte[] toBinary() {
        return serializer.object2binary(this);
    }

    public String toJson() {
        return serializer.object2json(flatten());
    }

    public String getDump() {
        if (size() == 0) {
            return "[ Empty AVAObject ]";
        } else {
            return DefaultObjectDumpFormatter.prettyPrintDump(dump());
        }
    }

    public String getDump(boolean noFormat) {
        if (!noFormat) {
            return dump();
        } else {
            return getDump();
        }
    }

    private String dump() {
        StringBuilder buffer = new StringBuilder();
        buffer.append('{');
        for (Iterator<String> iterator1 = getKeys().iterator(); iterator1.hasNext(); buffer.append(';')) {
            String key = (String) iterator1.next();
            DataWrapper wrapper = get(key);
            buffer.append("(").append(wrapper.getTypeId().toString().toLowerCase()).append(") ").append(key)
                    .append(": ");
            if (wrapper.getTypeId() == DataType.OBJECT)
                buffer.append(((CObject) wrapper.getObject()).getDump(false));
            else if (wrapper.getTypeId() == DataType.ARRAY)
                buffer.append(((CArray) wrapper.getObject()).getDump(false));
            else if (wrapper.getTypeId() == DataType.BYTE_ARRAY)
                buffer.append(DefaultObjectDumpFormatter.prettyPrintByteArray((byte[]) wrapper.getObject()));
            else if (wrapper.getTypeId() == DataType.CLASS)
                buffer.append(wrapper.getObject().getClass().getName());
            else
                buffer.append(wrapper.getObject());
        }

        buffer.append('}');
        return buffer.toString();
    }

    public String getHexDump() {
        return ByteUtils.fullHexDump(toBinary());
    }

    public boolean isNull(String key) {
        DataWrapper wrapper = dataHolder.get(key);
        if (wrapper == null)
            return false;
        return wrapper.getTypeId() == DataType.NULL;
    }

    public DataWrapper get(String key) {
        return dataHolder.get(key);
    }

    public Boolean getBool(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null)
            return null;
        else
            return (Boolean) o.getObject();
    }

    @SuppressWarnings("unchecked")
    public Collection<Boolean> getBoolArray(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null) {
            return null;
        } else {
            return (Collection<Boolean>) o.getObject();
        }
    }

    public Byte getByte(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null) {
            return null;
        } else {
            return (Byte) o.getObject();
        }
    }

    public byte[] getByteArray(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null) {
            return null;
        } else {
            return (byte[]) o.getObject();
        }
    }

    public Double getDouble(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null) {
            return null;
        } else {
            return (Double) o.getObject();
        }
    }

    @SuppressWarnings("unchecked")
    public Collection<Double> getDoubleArray(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null) {
            return null;
        } else {
            return (Collection<Double>) o.getObject();
        }
    }

    public Float getFloat(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null)
            return null;
        else
            return (Float) o.getObject();
    }

    @SuppressWarnings("unchecked")
    public Collection<Float> getFloatArray(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null)
            return null;
        else
            return (Collection<Float>) o.getObject();
    }

    public Integer getInt(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null)
            return null;
        else
            return (Integer) o.getObject();
    }

    @SuppressWarnings("unchecked")
    public Collection<Integer> getIntArray(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null) {
            return null;
        } else {
            return (Collection<Integer>) o.getObject();
        }
    }

    public Set<String> getKeys() {
        return dataHolder.keySet();
    }

    public Long getLong(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null) {
            return null;
        } else {
            return (Long) o.getObject();
        }
    }

    @SuppressWarnings("unchecked")
    public Collection<Long> getLongArray(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null) {
            return null;
        } else {
            return (Collection<Long>) o.getObject();
        }
    }

    public IArray getSFSArray(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null) {
            return null;
        } else {
            return (IArray) o.getObject();
        }
    }

    public IObject getSFSObject(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null)
            return null;
        else
            return (IObject) o.getObject();
    }

    public Short getShort(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null) {
            return null;
        } else {
            return (Short) o.getObject();
        }
    }

    @SuppressWarnings("unchecked")
    public Collection<Short> getShortArray(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null) {
            return null;
        } else {
            return (Collection<Short>) o.getObject();
        }
    }

    public Integer getUnsignedByte(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null) {
            return null;
        } else {
            return Integer.valueOf(
                    DefaultSFSDataSerializer.getInstance().getUnsignedByte(((Byte) o.getObject()).byteValue()));
        }
    }

    public Collection<Integer> getUnsignedByteArray(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null) {
            return null;
        }
        DefaultSFSDataSerializer serializer = DefaultSFSDataSerializer.getInstance();
        Collection<Integer> intCollection = new ArrayList<Integer>();
        byte raw[];
        int j = (raw = (byte[]) o.getObject()).length;
        for (int i = 0; i < j; i++) {
            byte b = raw[i];
            intCollection.add(Integer.valueOf(serializer.getUnsignedByte(b)));
        }

        return intCollection;
    }

    public String getUtfString(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null) {
            return null;
        } else {
            return (String) o.getObject();
        }
    }

    @SuppressWarnings("unchecked")
    public Collection<String> getUtfStringArray(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null) {
            return null;
        } else {
            return (Collection<String>) o.getObject();
        }
    }

    public Object getClass(String key) {
        DataWrapper o = dataHolder.get(key);
        if (o == null) {
            return null;
        } else {
            return o.getObject();
        }
    }

    public void putBool(String key, boolean value) {
        putObj(key, value, DataType.BOOL);
    }

    public void putBoolArray(String key, Collection<Boolean> value) {
        putObj(key, value, DataType.BOOL_ARRAY);
    }

    public void putByte(String key, byte value) {
        putObj(key, value, DataType.BYTE);
    }

    public void putByteArray(String key, byte value[]) {
        putObj(key, value, DataType.BYTE_ARRAY);
    }

    public void putDouble(String key, double value) {
        putObj(key, value, DataType.DOUBLE);
    }

    public void putDoubleArray(String key, Collection<Double> value) {
        putObj(key, value, DataType.DOUBLE_ARRAY);
    }

    public void putFloat(String key, float value) {
        putObj(key, Float.valueOf(value), DataType.FLOAT);
    }

    public void putFloatArray(String key, Collection<Float> value) {
        putObj(key, value, DataType.FLOAT_ARRAY);
    }

    public void putInt(String key, int value) {
        putObj(key, Integer.valueOf(value), DataType.INT);
    }

    public void putIntArray(String key, Collection<Integer> value) {
        putObj(key, value, DataType.INT_ARRAY);
    }

    public void putLong(String key, long value) {
        putObj(key, value, DataType.LONG);
    }

    public void putLongArray(String key, Collection<Long> value) {
        putObj(key, value, DataType.LONG_ARRAY);
    }

    public void putNull(String key) {
        dataHolder.put(key, new DataWrapper(DataType.NULL, null));
    }

    public void putSFSArray(String key, IArray value) {
        putObj(key, value, DataType.ARRAY);
    }

    public void putSFSObject(String key, IObject value) {
        putObj(key, value, DataType.OBJECT);
    }

    public void putShort(String key, short value) {
        putObj(key, value, DataType.SHORT);
    }

    public void putShortArray(String key, Collection<Short> value) {
        putObj(key, value, DataType.SHORT_ARRAY);
    }

    public void putUtfString(String key, String value) {
        putObj(key, value, DataType.UTF_STRING);
    }

    public void putUtfStringArray(String key, Collection<String> value) {
        putObj(key, value, DataType.UTF_STRING_ARRAY);
    }

    public void put(String key, DataWrapper wrappedObject) {
        putObj(key, wrappedObject, null);
    }

    public void putClass(String key, Object o) {
        putObj(key, o, DataType.CLASS);
    }

    public String toString() {
        return "[Object, size: " + size() + "]";
    }

    private void putObj(String key, Object value, DataType typeId) {
        if (key == null)
            throw new IllegalArgumentException("Object requires a non-null key for a 'put' operation!");
        if (key.length() > 255)
            throw new IllegalArgumentException("Object keys must be less than 255 characters!");
        if (value == null)
            throw new IllegalArgumentException(
                    "Object requires a non-null value! If you need to add a null use the putNull() method.");
        if (value instanceof DataWrapper)
            dataHolder.put(key, (DataWrapper) value);
        else
            dataHolder.put(key, new DataWrapper(typeId, value));
    }

    private Map<String, Object> flatten() {
        Map<String, Object> map = new HashMap<String, Object>();
        DefaultSFSDataSerializer.getInstance().flattenObject(map, this);
        return map;
    }


}
