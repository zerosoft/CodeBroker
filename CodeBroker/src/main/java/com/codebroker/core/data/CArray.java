package com.codebroker.core.data;

import com.codebroker.protocol.serialization.DefaultObjectDumpFormatter;
import com.codebroker.protocol.serialization.DefaultIDataSerializer;
import com.codebroker.protocol.IDataSerializer;
import com.codebroker.util.ByteUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CArray implements IArray {
    private IDataSerializer serializer;
    private List<DataWrapper> dataHolder;

    public CArray() {
        this.dataHolder = new ArrayList<>();
        this.serializer = DefaultIDataSerializer.getInstance();
    }

    public static CArray newFromBinaryData(byte[] bytes) {
        return ((CArray) DefaultIDataSerializer.getInstance().binary2array(bytes));
    }

    public static CArray newFromJsonData(String jsonStr) {
        return ((CArray) DefaultIDataSerializer.getInstance().json2array(jsonStr));
    }

    public static CArray newInstance() {
        return new CArray();
    }

    public String getDump() {
        if (size() == 0) {
            return "[ Empty IArray ]";
        }
        return DefaultObjectDumpFormatter.prettyPrintDump(dump());
    }

    public String getDump(boolean noFormat) {
        if (!(noFormat)) {
            return dump();
        }
        return getDump();
    }

    private String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        Object objDump;

        for (Iterator<DataWrapper> iterator = this.dataHolder.iterator(); iterator.hasNext(); ) {
            DataWrapper wrappedObject =  iterator.next();

            if (wrappedObject.getTypeId() == DataType.OBJECT) {
                objDump = ((IObject) wrappedObject.getObject()).getDump(false);
            } else if (wrappedObject.getTypeId() == DataType.ARRAY) {
                objDump = ((IArray) wrappedObject.getObject()).getDump(false);
            } else if (wrappedObject.getTypeId() == DataType.BYTE_ARRAY) {
                objDump = DefaultObjectDumpFormatter.prettyPrintByteArray((byte[]) wrappedObject.getObject());
            } else if (wrappedObject.getTypeId() == DataType.CLASS) {
                objDump = wrappedObject.getObject().getClass().getName();
            } else {
                objDump = wrappedObject.getObject();
            }
            sb.append(" (").append(wrappedObject.getTypeId().name().toLowerCase()).append(") ").append(objDump)
                    .append(';');
        }

        if (size() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        sb.append('}');

        return sb.toString();
    }

    public String getHexDump() {
        return ByteUtils.fullHexDump(toBinary());
    }

    public byte[] toBinary() {
        return this.serializer.array2binary(this);
    }

    public String toJson() {
        return DefaultIDataSerializer.getInstance().array2json(flatten());
    }

    public boolean isNull(int index) {
        DataWrapper wrapper = this.dataHolder.get(index);

        if (wrapper == null) {
            return false;
        }
        return (wrapper.getTypeId() == DataType.NULL);
    }

    public DataWrapper get(int index) {
        return (this.dataHolder.get(index));
    }

    public Boolean getBool(int index) {
        DataWrapper wrapper = this.dataHolder.get(index);
        return ((wrapper != null) ? (Boolean) wrapper.getObject() : null);
    }

    public Byte getByte(int index) {
        DataWrapper wrapper =  this.dataHolder.get(index);
        return ((wrapper != null) ? (Byte) wrapper.getObject() : null);
    }

    public Integer getUnsignedByte(int index) {
        DataWrapper wrapper =  this.dataHolder.get(index);
        return ((wrapper != null) ? Integer.valueOf(
                DefaultIDataSerializer.getInstance().getUnsignedByte(((Byte) wrapper.getObject()).byteValue()))
                : null);
    }

    public Short getShort(int index) {
        DataWrapper wrapper =  this.dataHolder.get(index);
        return ((wrapper != null) ? (Short) wrapper.getObject() : null);
    }

    public Integer getInt(int index) {
        DataWrapper wrapper =  this.dataHolder.get(index);
        return ((wrapper != null) ? (Integer) wrapper.getObject() : null);
    }

    public Long getLong(int index) {
        DataWrapper wrapper =  this.dataHolder.get(index);
        return ((wrapper != null) ? (Long) wrapper.getObject() : null);
    }

    public Float getFloat(int index) {
        DataWrapper wrapper =  this.dataHolder.get(index);
        return ((wrapper != null) ? (Float) wrapper.getObject() : null);
    }

    public Double getDouble(int index) {
        DataWrapper wrapper = this.dataHolder.get(index);
        return ((wrapper != null) ? (Double) wrapper.getObject() : null);
    }

    public String getUtfString(int index) {
        DataWrapper wrapper = this.dataHolder.get(index);
        return ((wrapper != null) ? (String) wrapper.getObject() : null);
    }


    public Collection<Boolean> getBoolArray(int index) {
        DataWrapper wrapper = this.dataHolder.get(index);
        return (wrapper != null ? (Collection<Boolean>) wrapper.getObject() : null);
    }

    public byte[] getByteArray(int index) {
        DataWrapper wrapper =  this.dataHolder.get(index);
        return ((wrapper != null) ? (byte[]) wrapper.getObject() : null);
    }

    public Collection<Integer> getUnsignedByteArray(int index) {
        DataWrapper wrapper =  this.dataHolder.get(index);

        if (wrapper == null) {
            return null;
        }

        DefaultIDataSerializer serializer = DefaultIDataSerializer.getInstance();
        Collection<Integer> intCollection = new ArrayList<>();

        for (byte b : (byte[]) wrapper.getObject()) {
            intCollection.add(Integer.valueOf(serializer.getUnsignedByte(b)));
        }

        return intCollection;
    }

    public Collection<Short> getShortArray(int index) {
        DataWrapper wrapper = this.dataHolder.get(index);
        return ((wrapper != null) ? (Collection<Short>) wrapper.getObject() : null);
    }


    public Collection<Integer> getIntArray(int index) {
        DataWrapper wrapper = this.dataHolder.get(index);
        return ((wrapper != null) ? (Collection<Integer>) wrapper.getObject() : null);
    }


    public Collection<Long> getLongArray(int index) {
        DataWrapper wrapper = this.dataHolder.get(index);
        return ((wrapper != null) ? (Collection<Long>) wrapper.getObject() : null);
    }


    public Collection<Float> getFloatArray(int index) {
        DataWrapper wrapper =  this.dataHolder.get(index);
        return ((wrapper != null) ? (Collection<Float>) wrapper.getObject() : null);
    }


    public Collection<Double> getDoubleArray(int index) {
        DataWrapper wrapper = this.dataHolder.get(index);
        return ((wrapper != null) ? (Collection<Double>) wrapper.getObject() : null);
    }


    public Collection<String> getUtfStringArray(int index) {
        DataWrapper wrapper = this.dataHolder.get(index);
        Collection<String> object = (Collection<String>) wrapper.getObject();
        return ((wrapper != null) ? object : null);
    }

    public IArray getArray(int index) {
        DataWrapper wrapper = this.dataHolder.get(index);
        return ((wrapper != null) ? (IArray) wrapper.getObject() : null);
    }

    public IObject getObject(int index) {
        DataWrapper wrapper = this.dataHolder.get(index);
        return ((wrapper != null) ? (IObject) wrapper.getObject() : null);
    }

    public Object getClass(int index) {
        DataWrapper wrapper =  this.dataHolder.get(index);
        return ((wrapper != null) ? wrapper.getObject() : null);
    }

    public void addBool(boolean value) {
        addObject(Boolean.valueOf(value), DataType.BOOL);
    }

    public void addBoolArray(Collection<Boolean> booleanCollection) {
        addObject(booleanCollection, DataType.BOOL_ARRAY);
    }

    public void addByte(byte value) {
        addObject(Byte.valueOf(value), DataType.BYTE);
    }

    public void addByteArray(byte[] bytes) {
        addObject(bytes, DataType.BYTE_ARRAY);
    }

    public void addDouble(double value) {
        addObject(Double.valueOf(value), DataType.DOUBLE);
    }

    public void addDoubleArray(Collection<Double> doubleCollection) {
        addObject(doubleCollection, DataType.DOUBLE_ARRAY);
    }

    public void addFloat(float value) {
        addObject(Float.valueOf(value), DataType.FLOAT);
    }

    public void addFloatArray(Collection<Float> floatCollection) {
        addObject(floatCollection, DataType.FLOAT_ARRAY);
    }

    public void addInt(int value) {
        addObject(Integer.valueOf(value), DataType.INT);
    }

    public void addIntArray(Collection<Integer> integerCollection) {
        addObject(integerCollection, DataType.INT_ARRAY);
    }

    public void addLong(long value) {
        addObject(Long.valueOf(value), DataType.LONG);
    }

    public void addLongArray(Collection<Long> longCollection) {
        addObject(longCollection, DataType.LONG_ARRAY);
    }

    public void addNull() {
        addObject(null, DataType.NULL);
    }

    public void addIArray(IArray iArray) {
        addObject(iArray, DataType.ARRAY);
    }

    public void addIObject(IObject iObject) {
        addObject(iObject, DataType.OBJECT);
    }

    public void addShort(short value) {
        addObject(Short.valueOf(value), DataType.SHORT);
    }

    public void addShortArray(Collection<Short> shortCollection) {
        addObject(shortCollection, DataType.SHORT_ARRAY);
    }

    public void addUtfString(String value) {
        addObject(value, DataType.UTF_STRING);
    }

    public void addUtfStringArray(Collection<String> stringCollection) {
        addObject(stringCollection, DataType.UTF_STRING_ARRAY);
    }

    public void addClass(Object o) {
        addObject(o, DataType.CLASS);
    }

    public void add(DataWrapper dataWrapper) {
        this.dataHolder.add(dataWrapper);
    }

    public boolean contains(Object obj) {
        if ((obj instanceof IArray) || (obj instanceof IObject)) {
            throw new UnsupportedOperationException("IArray and IObject are not supported by this method.");
        }
        boolean found = false;

        for (Iterator<DataWrapper> iterator = this.dataHolder.iterator(); iterator.hasNext(); ) {
            Object item = iterator.next().getObject();

            if (!(item.equals(obj)))
                continue;
            found = true;
            break;
        }

        return found;
    }

    public Object getElementAt(int index) {
        Object item;

        DataWrapper wrapper = this.dataHolder.get(index);

        if (wrapper != null)
            ;
        item = wrapper.getObject();

        return item;
    }

    public Iterator<DataWrapper> iterator() {
        return this.dataHolder.iterator();
    }

    public void removeElementAt(int index) {
        this.dataHolder.remove(index);
    }

    public int size() {
        return this.dataHolder.size();
    }

    public String toString() {
        return "[IArray, size: " + size() + "]";
    }

    private void addObject(Object value, DataType typeId) {
        this.dataHolder.add(new DataWrapper(typeId, value));
    }

    private List<Object> flatten() {
        List<Object> list = new ArrayList<>();
        DefaultIDataSerializer.getInstance().flattenArray(list, this);
        return list;
    }
}
