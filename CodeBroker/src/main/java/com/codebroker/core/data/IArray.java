package com.codebroker.core.data;

import java.util.Collection;
import java.util.Iterator;

public interface IArray {

     boolean contains(Object paramObject);

     Iterator<DataWrapper> iterator();

     Object getElementAt(int index);

     void removeElementAt(int index);

     int size();

     byte[] toBinary();

     String toJson();

     String getHexDump();

     String getDump();

     String getDump(boolean paramBoolean);

     void addNull();

     void addBool(boolean paramBoolean);

     void addByte(byte paramByte);

     void addShort(short paramShort);

     void addInt(int paramInt);

     void addLong(long paramLong);

     void addFloat(float paramFloat);

     void addDouble(double paramDouble);

     void addUtfString(String paramString);

     void addBoolArray(Collection<Boolean> booleanCollection);

     void addByteArray(byte[] bytes);

     void addShortArray(Collection<Short> shortCollection);

     void addIntArray(Collection<Integer> integerCollection);

     void addLongArray(Collection<Long> longCollection);

     void addFloatArray(Collection<Float> floatCollection);

     void addDoubleArray(Collection<Double> doubleCollection);

     void addUtfStringArray(Collection<String> stringCollection);

     void addIArray(IArray iArray);

     void addIObject(IObject iObject);

     void addClass(Object paramObject);

     void add(DataWrapper dataWrapper);

     boolean isNull(int index);

     Boolean getBool(int index);

     Byte getByte(int index);

     Integer getUnsignedByte(int index);

     Short getShort(int index);

     Integer getInt(int index);

     Long getLong(int index);

     Float getFloat(int index);

     Double getDouble(int index);

     String getUtfString(int index);

     Collection<Boolean> getBoolArray(int index);

     byte[] getByteArray(int index);

     Collection<Integer> getUnsignedByteArray(int index);

     Collection<Short> getShortArray(int index);

     Collection<Integer> getIntArray(int index);

     Collection<Long> getLongArray(int index);

     Collection<Float> getFloatArray(int index);

     Collection<Double> getDoubleArray(int index);

     Collection<String> getUtfStringArray(int index);

     Object getClass(int index);

     IArray getArray(int index);

     IObject getObject(int index);
}
