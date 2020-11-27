package com.codebroker.core.data;


import com.codebroker.api.IGameUser;
import com.codebroker.protocol.SerializableType;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public interface IObject extends SerializableType {

     boolean isNull(String key);

     boolean containsKey(String key);

     boolean removeElement(String key);

     Set<String> getKeys();

     int size();

     Iterator<Entry<String, DataWrapper>> iterator();

     byte[] toBinary();

     String toJson();

     String getDump();

     String getDump(boolean flag);

     String getHexDump();

     DataWrapper get(String key);

     Boolean getBool(String key);

     Byte getByte(String key);

     Integer getUnsignedByte(String key);

     Short getShort(String key);

     Integer getInt(String key);

     Long getLong(String key);

     Float getFloat(String key);

     Double getDouble(String key);

     String getUtfString(String key);

     Collection<Boolean> getBoolArray(String key);

     byte[] getByteArray(String key);

     Collection<Integer> getUnsignedByteArray(String key);

     Collection<Short> getShortArray(String key);

     Collection<Integer> getIntArray(String key);

     Collection<Long> getLongArray(String key);

     Collection<Float> getFloatArray(String key);

     Collection<Double> getDoubleArray(String key);

     Collection<String> getUtfStringArray(String key);

     Collection<IGameUser> getIGameUserArray(String key);

     IArray getIArray(String key);

     IObject getIObject(String key);

     void putNull(String key);

     void putBool(String key, boolean value);

     void putByte(String key, byte value);

     void putShort(String key, short word);

     void putInt(String key, int value);

     void putLong(String key, long value);

     void putFloat(String key, float value);

     void putDouble(String key, double value);

     void putUtfString(String key, String value);

     void putBoolArray(String key, Collection<Boolean> collection);

     void putByteArray(String key, byte[] bytes);

     void putShortArray(String key, Collection<Short> collection);

     void putIntArray(String key, Collection<Integer> collection);

     void putLongArray(String key, Collection<Long> collection);

     void putFloatArray(String key, Collection<Float> collection);

     void putDoubleArray(String key, Collection<Double> collection);

     void putUtfStringArray(String key, Collection<String> collection);

     void putIGameUserArray(String key, Collection<IGameUser> collection);

     void putIArray(String key, IArray iArray);

     void putIObject(String key, IObject iObject);

     void put(String key, DataWrapper wrapper);

     void putIGameUser(String key, IGameUser gameUser);

     IGameUser getIGameUser(String key);
}
