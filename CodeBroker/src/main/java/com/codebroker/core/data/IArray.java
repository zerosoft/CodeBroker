package com.codebroker.core.data;

import java.util.Collection;
import java.util.Iterator;

public interface IArray {

    public boolean contains(Object paramObject);

    public Iterator<DataWrapper> iterator();

    public Object getElementAt(int paramInt);

    public void removeElementAt(int paramInt);

    public int size();

    public byte[] toBinary();

    public String toJson();

    public String getHexDump();

    public String getDump();

    public String getDump(boolean paramBoolean);

    public void addNull();

    public void addBool(boolean paramBoolean);

    public void addByte(byte paramByte);

    public void addShort(short paramShort);

    public void addInt(int paramInt);

    public void addLong(long paramLong);

    public void addFloat(float paramFloat);

    public void addDouble(double paramDouble);

    public void addUtfString(String paramString);

    public void addBoolArray(Collection<Boolean> paramCollection);

    public void addByteArray(byte[] paramArrayOfByte);

    public void addShortArray(Collection<Short> paramCollection);

    public void addIntArray(Collection<Integer> paramCollection);

    public void addLongArray(Collection<Long> paramCollection);

    public void addFloatArray(Collection<Float> paramCollection);

    public void addDoubleArray(Collection<Double> paramCollection);

    public void addUtfStringArray(Collection<String> paramCollection);

    public void addSFSArray(IArray paramISFSArray);

    public void addSFSObject(IObject paramISFSObject);

    public void addClass(Object paramObject);

    public void add(DataWrapper paramSFSDataWrapper);

    public boolean isNull(int paramInt);

    public Boolean getBool(int paramInt);

    public Byte getByte(int paramInt);

    public Integer getUnsignedByte(int paramInt);

    public Short getShort(int paramInt);

    public Integer getInt(int paramInt);

    public Long getLong(int paramInt);

    public Float getFloat(int paramInt);

    public Double getDouble(int paramInt);

    public String getUtfString(int paramInt);

    public Collection<Boolean> getBoolArray(int paramInt);

    public byte[] getByteArray(int paramInt);

    public Collection<Integer> getUnsignedByteArray(int paramInt);

    public Collection<Short> getShortArray(int paramInt);

    public Collection<Integer> getIntArray(int paramInt);

    public Collection<Long> getLongArray(int paramInt);

    public Collection<Float> getFloatArray(int paramInt);

    public Collection<Double> getDoubleArray(int paramInt);

    public Collection<String> getUtfStringArray(int paramInt);

    public Object getClass(int paramInt);

    public IArray getArray(int paramInt);

    public IObject getObject(int paramInt);
}
