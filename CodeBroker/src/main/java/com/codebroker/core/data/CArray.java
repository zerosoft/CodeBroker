package com.codebroker.core.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.codebroker.protocol.serialization.DefaultObjectDumpFormatter;
import com.codebroker.protocol.serialization.DefaultSFSDataSerializer;
import com.codebroker.protocol.serialization.IDataSerializer;
import com.codebroker.util.ByteUtils;

public class CArray implements IArray {
	private IDataSerializer serializer;
	private List<DataWrapper> dataHolder;

	public CArray() {
		this.dataHolder = new ArrayList<DataWrapper>();
		this.serializer = DefaultSFSDataSerializer.getInstance();
	}

	public static CArray newFromBinaryData(byte[] bytes) {
		return ((CArray) DefaultSFSDataSerializer.getInstance().binary2array(bytes));
	}

	public static CArray newFromJsonData(String jsonStr) {
		return ((CArray) DefaultSFSDataSerializer.getInstance().json2array(jsonStr));
	}

	public static CArray newInstance() {
		return new CArray();
	}

	public String getDump() {
		if (size() == 0) {
			return "[ Empty AVAArray ]";
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
		Object objDump = null;

		for (Iterator<DataWrapper> iter = this.dataHolder.iterator(); iter.hasNext();) {
			DataWrapper wrappedObject = (DataWrapper) iter.next();

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
		return DefaultSFSDataSerializer.getInstance().array2json(flatten());
	}

	public boolean isNull(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);

		if (wrapper == null) {
			return false;
		}
		return (wrapper.getTypeId() == DataType.NULL);
	}

	public DataWrapper get(int index) {
		return ((DataWrapper) this.dataHolder.get(index));
	}

	public Boolean getBool(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		return ((wrapper != null) ? (Boolean) wrapper.getObject() : null);
	}

	public Byte getByte(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		return ((wrapper != null) ? (Byte) wrapper.getObject() : null);
	}

	public Integer getUnsignedByte(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		return ((wrapper != null) ? Integer.valueOf(
				DefaultSFSDataSerializer.getInstance().getUnsignedByte(((Byte) wrapper.getObject()).byteValue()))
				: null);
	}

	public Short getShort(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		return ((wrapper != null) ? (Short) wrapper.getObject() : null);
	}

	public Integer getInt(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		return ((wrapper != null) ? (Integer) wrapper.getObject() : null);
	}

	public Long getLong(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		return ((wrapper != null) ? (Long) wrapper.getObject() : null);
	}

	public Float getFloat(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		return ((wrapper != null) ? (Float) wrapper.getObject() : null);
	}

	public Double getDouble(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		return ((wrapper != null) ? (Double) wrapper.getObject() : null);
	}

	public String getUtfString(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		return ((wrapper != null) ? (String) wrapper.getObject() : null);
	}

	public Collection<Boolean> getBoolArray(int index) {
		DataWrapper wrapper = this.dataHolder.get(index);
		return (wrapper != null ? (Collection<Boolean>) wrapper.getObject() : null);
	}

	public byte[] getByteArray(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		return ((wrapper != null) ? (byte[]) wrapper.getObject() : null);
	}

	public Collection<Integer> getUnsignedByteArray(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);

		if (wrapper == null) {
			return null;
		}

		DefaultSFSDataSerializer serializer = DefaultSFSDataSerializer.getInstance();
		Collection<Integer> intCollection = new ArrayList<Integer>();

		for (byte b : (byte[]) wrapper.getObject()) {
			intCollection.add(Integer.valueOf(serializer.getUnsignedByte(b)));
		}

		return intCollection;
	}

	public Collection<Short> getShortArray(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		return ((wrapper != null) ? (Collection<Short>) wrapper.getObject() : null);
	}

	public Collection<Integer> getIntArray(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		return ((wrapper != null) ? (Collection<Integer>) wrapper.getObject() : null);
	}

	public Collection<Long> getLongArray(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		return ((wrapper != null) ? (Collection<Long>) wrapper.getObject() : null);
	}

	public Collection<Float> getFloatArray(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		return ((wrapper != null) ? (Collection<Float>) wrapper.getObject() : null);
	}

	public Collection<Double> getDoubleArray(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		return ((wrapper != null) ? (Collection<Double>) wrapper.getObject() : null);
	}

	public Collection<String> getUtfStringArray(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		Collection<String> object = (Collection<String>) wrapper.getObject();
		return ((wrapper != null) ? object : null);
	}

	public IArray getArray(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		return ((wrapper != null) ? (IArray) wrapper.getObject() : null);
	}

	public IObject getObject(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		return ((wrapper != null) ? (IObject) wrapper.getObject() : null);
	}

	public Object getClass(int index) {
		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);
		return ((wrapper != null) ? wrapper.getObject() : null);
	}

	public void addBool(boolean value) {
		addObject(Boolean.valueOf(value), DataType.BOOL);
	}

	public void addBoolArray(Collection<Boolean> value) {
		addObject(value, DataType.BOOL_ARRAY);
	}

	public void addByte(byte value) {
		addObject(Byte.valueOf(value), DataType.BYTE);
	}

	public void addByteArray(byte[] value) {
		addObject(value, DataType.BYTE_ARRAY);
	}

	public void addDouble(double value) {
		addObject(Double.valueOf(value), DataType.DOUBLE);
	}

	public void addDoubleArray(Collection<Double> value) {
		addObject(value, DataType.DOUBLE_ARRAY);
	}

	public void addFloat(float value) {
		addObject(Float.valueOf(value), DataType.FLOAT);
	}

	public void addFloatArray(Collection<Float> value) {
		addObject(value, DataType.FLOAT_ARRAY);
	}

	public void addInt(int value) {
		addObject(Integer.valueOf(value), DataType.INT);
	}

	public void addIntArray(Collection<Integer> value) {
		addObject(value, DataType.INT_ARRAY);
	}

	public void addLong(long value) {
		addObject(Long.valueOf(value), DataType.LONG);
	}

	public void addLongArray(Collection<Long> value) {
		addObject(value, DataType.LONG_ARRAY);
	}

	public void addNull() {
		addObject(null, DataType.NULL);
	}

	public void addSFSArray(IArray value) {
		addObject(value, DataType.ARRAY);
	}

	public void addSFSObject(IObject value) {
		addObject(value, DataType.OBJECT);
	}

	public void addShort(short value) {
		addObject(Short.valueOf(value), DataType.SHORT);
	}

	public void addShortArray(Collection<Short> value) {
		addObject(value, DataType.SHORT_ARRAY);
	}

	public void addUtfString(String value) {
		addObject(value, DataType.UTF_STRING);
	}

	public void addUtfStringArray(Collection<String> value) {
		addObject(value, DataType.UTF_STRING_ARRAY);
	}

	public void addClass(Object o) {
		addObject(o, DataType.CLASS);
	}

	public void add(DataWrapper wrappedObject) {
		this.dataHolder.add(wrappedObject);
	}

	public boolean contains(Object obj) {
		if ((obj instanceof IArray) || (obj instanceof IObject)) {
			throw new UnsupportedOperationException("IArray and IObject are not supported by this method.");
		}
		boolean found = false;

		for (Iterator<DataWrapper> iter = this.dataHolder.iterator(); iter.hasNext();) {
			Object item = ((DataWrapper) iter.next()).getObject();

			if (!(item.equals(obj)))
				continue;
			found = true;
			break;
		}

		return found;
	}

	public Object getElementAt(int index) {
		Object item = null;

		DataWrapper wrapper = (DataWrapper) this.dataHolder.get(index);

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
		return "[AVAArray, size: " + size() + "]";
	}

	private void addObject(Object value, DataType typeId) {
		this.dataHolder.add(new DataWrapper(typeId, value));
	}

	private List<Object> flatten() {
		List<Object> list = new ArrayList<Object>();
		DefaultSFSDataSerializer.getInstance().flattenArray(list, this);
		return list;
	}
}
