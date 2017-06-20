package com.codebroker.core.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public interface IObject {

	public boolean isNull(String key);

	public boolean containsKey(String key);

	public boolean removeElement(String key);

	public Set<String> getKeys();

	public int size();

	public Iterator<Entry<String, DataWrapper>> iterator();

	public byte[] toBinary();

	public String toJson();

	public String getDump();

	public String getDump(boolean flag);

	public String getHexDump();

	public DataWrapper get(String key);

	public Boolean getBool(String key);

	public Byte getByte(String key);

	public Integer getUnsignedByte(String key);

	public Short getShort(String key);

	public Integer getInt(String key);

	public Long getLong(String key);

	public Float getFloat(String key);

	public Double getDouble(String key);

	public String getUtfString(String key);

	public Collection<Boolean> getBoolArray(String key);

	public byte[] getByteArray(String key);

	public Collection<Integer> getUnsignedByteArray(String key);

	public Collection<Short> getShortArray(String key);

	public Collection<Integer> getIntArray(String key);

	public Collection<Long> getLongArray(String key);

	public Collection<Float> getFloatArray(String key);

	public Collection<Double> getDoubleArray(String key);

	public Collection<String> getUtfStringArray(String key);

	public IArray getSFSArray(String key);

	public IObject getSFSObject(String key);

	public Object getClass(String key);

	public void putNull(String key);

	public void putBool(String key, boolean value);

	public void putByte(String key, byte value);

	public void putShort(String key, short word);

	public void putInt(String key, int value);

	public void putLong(String key, long value);

	public void putFloat(String key, float value);

	public void putDouble(String key, double value);

	public void putUtfString(String key, String value);

	public void putBoolArray(String key, Collection<Boolean> collection);

	public void putByteArray(String key, byte[] bytes);

	public void putShortArray(String key, Collection<Short> collection);

	public void putIntArray(String key, Collection<Integer> collection);

	public void putLongArray(String key, Collection<Long> collection);

	public void putFloatArray(String key, Collection<Float> collection);

	public void putDoubleArray(String key, Collection<Double> collection);

	public void putUtfStringArray(String s, Collection<String> collection);

	public void putSFSArray(String key, IArray isfsarray);

	public void putSFSObject(String key, IObject isfsobject);

	public void putClass(String key, Object obj);

	public void put(String key, DataWrapper sfsdatawrapper);
}
