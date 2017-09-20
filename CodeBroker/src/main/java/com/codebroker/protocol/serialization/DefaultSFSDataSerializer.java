package com.codebroker.protocol.serialization;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.codebroker.api.event.Event;
import com.codebroker.core.data.CArray;
import com.codebroker.core.data.CArrayLite;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.CObjectLite;
import com.codebroker.core.data.DataType;
import com.codebroker.core.data.DataWrapper;
import com.codebroker.core.data.IArray;
import com.codebroker.core.data.IObject;
import com.codebroker.exception.CCodecException;
import com.codebroker.exception.CRuntimeException;
import com.codebroker.protocol.ThriftSerializerFactory;
import com.message.thrift.actor.event.RemoteEventMessage;

public class DefaultSFSDataSerializer implements IDataSerializer {

	private static final long serialVersionUID = -6749126348064423022L;
	
	private static final String CLASS_MARKER_KEY = "$C";
	private static final String CLASS_FIELDS_KEY = "$F";

	private static final String FIELD_NAME_KEY = "N";
	private static final String FIELD_VALUE_KEY = "V";
	private static ThriftSerializerFactory thriftSerializerFactory=new ThriftSerializerFactory();
	private static DefaultSFSDataSerializer instance = new DefaultSFSDataSerializer();
	private static int BUFFER_CHUNK_SIZE = 512;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static DefaultSFSDataSerializer getInstance() {
		return instance;
	}

	private DefaultSFSDataSerializer() {
	}

	public int getUnsignedByte(byte b) {
		return 255 & b;
	}

	public Event binary2Event(byte[] data) {
		Event event=new Event();
		RemoteEventMessage eventMessage = thriftSerializerFactory.getEventMessage(data);
		event.topic = eventMessage.topic;
		byte[] bytes=new byte[eventMessage.iobject.remaining()];
		eventMessage.iobject.get(bytes);
		CObject cObject=CObject.newFromBinaryData(bytes);
		event.message = cObject;
		return event;
	}
	
	
	public byte[] Event2binary(Event event) {
		RemoteEventMessage eventMessage=new RemoteEventMessage();
		eventMessage.topic=event.topic;
		byte[] binary = event.message.toBinary();
		ByteBuffer buffer=ByteBuffer.allocate(binary.length);
		buffer.put(binary);
		buffer.flip();
		eventMessage.iobject=buffer;
		
		byte[] deserializeEventMessage = thriftSerializerFactory.deserializeEventMessage(eventMessage);
		return deserializeEventMessage;
	}
	
	
	public IArray binary2array(byte[] data) {
		if (data.length < 3) {
			throw new IllegalStateException(
					"Can\'t decode an AVAArray. Byte data is insufficient. Size: " + data.length + " bytes");
		} else {
			ByteBuffer buffer = ByteBuffer.allocate(data.length);
			buffer.put(data);
			buffer.flip();
			return this.decodeSFSArray((ByteBuffer) buffer);
		}
	}

	private IArray decodeSFSArray(ByteBuffer buffer) {
		CArray sfsArray = CArray.newInstance();
		byte headerBuffer = buffer.get();
		if (headerBuffer != DataType.ARRAY.typeID) {
			throw new IllegalStateException(
					"Invalid DataType. Expected: " + DataType.ARRAY.typeID + ", found: " + headerBuffer);
		} else {
			short size = buffer.getShort();
			if (size < 0) {
				throw new IllegalStateException("Can\'t decode AVAArray. Size is negative = " + size);
			} else {
				try {
					for (int codecError = 0; codecError < size; ++codecError) {
						DataWrapper decodedObject = this.decodeObject(buffer);
						if (decodedObject == null) {
							throw new IllegalStateException("Could not decode AVAArray item at index: " + codecError);
						}

						sfsArray.add(decodedObject);
					}

					return sfsArray;
				} catch (CCodecException var7) {
					throw new IllegalArgumentException(var7.getMessage());
				}
			}
		}
	}

	public IObject binary2object(byte[] data) {
		if (data.length < 3) {
			throw new IllegalStateException(
					"Can\'t decode an AVAObject. Byte data is insufficient. Size: " + data.length + " bytes");
		} else {
			ByteBuffer buffer = ByteBuffer.allocate(data.length);
			buffer.put(data);
			buffer.flip();
			return this.decodeSFSObject((ByteBuffer) buffer);
		}
	}

	private IObject decodeSFSObject(ByteBuffer buffer) {
		CObject sfsObject = CObject.newInstance();
		byte headerBuffer = buffer.get();
		if (headerBuffer != DataType.OBJECT.typeID) {
			throw new IllegalStateException(
					"Invalid DataType. Expected: " + DataType.OBJECT.typeID + ", found: " + headerBuffer);
		} else {
			short size = buffer.getShort();
			if (size < 0) {
				throw new IllegalStateException("Can\'t decode AVAObject. Size is negative = " + size);
			} else {
				try {
					for (int codecError = 0; codecError < size; ++codecError) {
						short keySize = buffer.getShort();
						if (keySize < 0 || keySize > 255) {
							throw new IllegalStateException("Invalid AVAObject key length. Found = " + keySize);
						}

						byte[] keyData = new byte[keySize];
						buffer.get(keyData, 0, keyData.length);
						String key = new String(keyData);
						DataWrapper decodedObject = this.decodeObject(buffer);
						if (decodedObject == null) {
							throw new IllegalStateException("Could not decode value for key: " + keyData);
						}

						sfsObject.put(key, decodedObject);
					}

					return sfsObject;
				} catch (CCodecException var10) {
					throw new IllegalArgumentException(var10.getMessage());
				}
			}
		}
	}

	public IArray json2array(String jsonStr) {
		if (jsonStr.length() < 2) {
			throw new IllegalStateException(
					"Can\'t decode AVAObject. JSON String is too short. Len: " + jsonStr.length());
		} else {
			JSONArray jsa = JSONArray.parseArray(jsonStr);
			return this.decodeSFSArray((JSONArray) jsa);
		}
	}

	private IArray decodeSFSArray(JSONArray jsa) {
		CArrayLite sfsArray = CArrayLite.newInstance();
		Iterator<Object> iter = jsa.iterator();

		while (iter.hasNext()) {
			Object value = iter.next();
			DataWrapper decodedObject = this.decodeJsonObject(value);
			if (decodedObject == null) {
				throw new IllegalStateException("(json2sfarray) Could not decode value for object: " + value);
			}

			sfsArray.add(decodedObject);
		}

		return sfsArray;
	}

	public IObject json2object(String jsonStr) {
		if (jsonStr.length() < 2) {
			throw new IllegalStateException(
					"Can\'t decode AVAObject. JSON String is too short. Len: " + jsonStr.length());
		} else {
			JSONObject jso = JSONObject.parseObject(jsonStr);
			return this.decodeSFSObject((JSONObject) jso);
		}
	}

	private IObject decodeSFSObject(JSONObject jso) {
		CObject sfsObject = CObjectLite.newInstance();
		Iterator<String> var4 = jso.keySet().iterator();

		while (var4.hasNext()) {
			Object key = var4.next();
			Object value = jso.get(key);
			DataWrapper decodedObject = this.decodeJsonObject(value);
			if (decodedObject == null) {
				throw new IllegalStateException("(json2sfsobj) Could not decode value for key: " + key);
			}

			sfsObject.put((String) key, decodedObject);
		}

		return sfsObject;
	}

	private DataWrapper decodeJsonObject(Object o) {
		if (o instanceof Integer) {
			return new DataWrapper(DataType.INT, o);
		} else if (o instanceof Long) {
			return new DataWrapper(DataType.LONG, o);
		} else if (o instanceof Double) {
			return new DataWrapper(DataType.DOUBLE, o);
		} else if (o instanceof Boolean) {
			return new DataWrapper(DataType.BOOL, o);
		} else if (o instanceof String) {
			return new DataWrapper(DataType.UTF_STRING, o);
		} else if (o instanceof JSONObject) {
			JSONObject jso = (JSONObject) o;
			return jso.isEmpty() ? new DataWrapper(DataType.NULL, (Object) null)
					: new DataWrapper(DataType.OBJECT, this.decodeSFSObject((JSONObject) jso));
		} else if (o instanceof JSONArray) {
			return new DataWrapper(DataType.ARRAY, this.decodeSFSArray((JSONArray) ((JSONArray) o)));
		} else {
			throw new IllegalArgumentException(
					String.format("Unrecognized DataType while converting JSONObject 2 AVAObject. Object: %s, Type: %s",
							new Object[] { o, o == null ? "null" : o.getClass() }));
		}
	}

	public byte[] object2binary(IObject object) {
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CHUNK_SIZE);
		buffer.put((byte) DataType.OBJECT.typeID);
		buffer.putShort((short) object.size());
		return this.obj2bin(object, buffer);
	}

	private byte[] obj2bin(IObject object, ByteBuffer buffer) {
		Set<String> keys = object.getKeys();

		DataWrapper wrapper;
		Object dataObj;
		for (Iterator<String> result = keys.iterator(); 
				result.hasNext(); 
				buffer = this.encodeObject(buffer, wrapper.getTypeId(), dataObj)) {
			String pos = result.next();
			wrapper = object.get(pos);
			dataObj = wrapper.getObject();
			buffer = this.encodeSFSObjectKey(buffer, pos);
		}

		int pos1 = buffer.position();
		byte[] result1 = new byte[pos1];
		buffer.flip();
		buffer.get(result1, 0, pos1);
		return result1;
	}

	public byte[] array2binary(IArray array) {
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CHUNK_SIZE);
		buffer.put((byte) DataType.ARRAY.typeID);
		buffer.putShort((short) array.size());
		return this.arr2bin(array, buffer);
	}

	private byte[] arr2bin(IArray array, ByteBuffer buffer) {
		DataWrapper wrapper;
		Object pos;
		for (Iterator<DataWrapper> iter = array.iterator(); 
				iter.hasNext(); 
				buffer = this.encodeObject(buffer, wrapper.getTypeId(), pos)) {
			wrapper = (DataWrapper) iter.next();
			pos = wrapper.getObject();
		}

		int pos1 = buffer.position();
		byte[] result = new byte[pos1];
		buffer.flip();
		buffer.get(result, 0, pos1);
		return result;
	}

	public void flattenObject(Map<String, Object> map, CObject sfsObj) {
		Iterator<Entry<String, DataWrapper>> it = sfsObj.iterator();

		while (it.hasNext()) {
			Entry<String, DataWrapper> entry = it.next();
			String key = entry.getKey();
			DataWrapper value = entry.getValue();
			if (value.getTypeId() == DataType.OBJECT) {
				HashMap<String, Object> newList = new HashMap<String, Object>();
				map.put(key, newList);
				this.flattenObject(newList, (CObject) value.getObject());
			} else if (value.getTypeId() == DataType.ARRAY) {
				ArrayList<Object> newList1 = new ArrayList<Object>();
				map.put(key, newList1);
				this.flattenArray(newList1, (CArray) value.getObject());
			} else {
				map.put(key, value.getObject());
			}
		}

	}

	public void flattenArray(List<Object> array, CArray sfsArray) {
		Iterator<DataWrapper> it = sfsArray.iterator();

		while (it.hasNext()) {
			DataWrapper value = (DataWrapper) it.next();
			if (value.getTypeId() == DataType.OBJECT) {
				HashMap<String, Object> newList = new HashMap<String, Object>();
				array.add(newList);
				this.flattenObject(newList, (CObject) value.getObject());
			} else if (value.getTypeId() == DataType.ARRAY) {
				ArrayList<Object> newList1 = new ArrayList<Object>();
				array.add(newList1);
				this.flattenArray(newList1, (CArray) value.getObject());
			} else {
				array.add(value.getObject());
			}
		}

	}

	private DataWrapper decodeObject(ByteBuffer buffer) throws CCodecException {
		DataWrapper decodedObject = null;
		byte headerByte = buffer.get();
		if (headerByte == DataType.NULL.typeID) {
			decodedObject = this.binDecode_NULL(buffer);
		} else if (headerByte == DataType.BOOL.typeID) {
			decodedObject = this.binDecode_BOOL(buffer);
		} else if (headerByte == DataType.BOOL_ARRAY.typeID) {
			decodedObject = this.binDecode_BOOL_ARRAY(buffer);
		} else if (headerByte == DataType.BYTE.typeID) {
			decodedObject = this.binDecode_BYTE(buffer);
		} else if (headerByte == DataType.BYTE_ARRAY.typeID) {
			decodedObject = this.binDecode_BYTE_ARRAY(buffer);
		} else if (headerByte == DataType.SHORT.typeID) {
			decodedObject = this.binDecode_SHORT(buffer);
		} else if (headerByte == DataType.SHORT_ARRAY.typeID) {
			decodedObject = this.binDecode_SHORT_ARRAY(buffer);
		} else if (headerByte == DataType.INT.typeID) {
			decodedObject = this.binDecode_INT(buffer);
		} else if (headerByte == DataType.INT_ARRAY.typeID) {
			decodedObject = this.binDecode_INT_ARRAY(buffer);
		} else if (headerByte == DataType.LONG.typeID) {
			decodedObject = this.binDecode_LONG(buffer);
		} else if (headerByte == DataType.LONG_ARRAY.typeID) {
			decodedObject = this.binDecode_LONG_ARRAY(buffer);
		} else if (headerByte == DataType.FLOAT.typeID) {
			decodedObject = this.binDecode_FLOAT(buffer);
		} else if (headerByte == DataType.FLOAT_ARRAY.typeID) {
			decodedObject = this.binDecode_FLOAT_ARRAY(buffer);
		} else if (headerByte == DataType.DOUBLE.typeID) {
			decodedObject = this.binDecode_DOUBLE(buffer);
		} else if (headerByte == DataType.DOUBLE_ARRAY.typeID) {
			decodedObject = this.binDecode_DOUBLE_ARRAY(buffer);
		} else if (headerByte == DataType.UTF_STRING.typeID) {
			decodedObject = this.binDecode_UTF_STRING(buffer);
		} else if (headerByte == DataType.UTF_STRING_ARRAY.typeID) {
			decodedObject = this.binDecode_UTF_STRING_ARRAY(buffer);
		} else if (headerByte == DataType.ARRAY.typeID) {
			buffer.position(buffer.position() - 1);
			decodedObject = new DataWrapper(DataType.ARRAY, this.decodeSFSArray((ByteBuffer) buffer));
		} else {
			if (headerByte != DataType.OBJECT.typeID) {
				throw new CCodecException("Unknow DataType ID: " + headerByte);
			}

			buffer.position(buffer.position() - 1);
			IObject sfsObj = this.decodeSFSObject((ByteBuffer) buffer);
			DataType type = DataType.OBJECT;
			Object finalSfsObj = sfsObj;
			if (sfsObj.containsKey(CLASS_MARKER_KEY) && sfsObj.containsKey(CLASS_FIELDS_KEY)) {
				type = DataType.CLASS;
				finalSfsObj = this.cbo2pojo(sfsObj);
			}

			decodedObject = new DataWrapper(type, finalSfsObj);
		}

		return decodedObject;
	}

	@SuppressWarnings("unchecked")
	private ByteBuffer encodeObject(ByteBuffer buffer, DataType typeId, Object object) {
		switch (typeId) {
		case NULL:
			buffer = this.binEncode_NULL(buffer);
			break;
		case BOOL:
			buffer = this.binEncode_BOOL(buffer, (Boolean) object);
			break;
		case BYTE:
			buffer = this.binEncode_BYTE(buffer, (Byte) object);
			break;
		case SHORT:
			buffer = this.binEncode_SHORT(buffer, (Short) object);
			break;
		case INT:
			buffer = this.binEncode_INT(buffer, (Integer) object);
			break;
		case LONG:
			buffer = this.binEncode_LONG(buffer, (Long) object);
			break;
		case FLOAT:
			buffer = this.binEncode_FLOAT(buffer, (Float) object);
			break;
		case DOUBLE:
			buffer = this.binEncode_DOUBLE(buffer, (Double) object);
			break;
		case UTF_STRING:
			buffer = this.binEncode_UTF_STRING(buffer, (String) object);
			break;
		case BOOL_ARRAY:
			buffer = this.binEncode_BOOL_ARRAY(buffer, (Collection<Boolean>) object);
			break;
		case BYTE_ARRAY:
			buffer = this.binEncode_BYTE_ARRAY(buffer, (byte[]) object);
			break;
		case SHORT_ARRAY:
			buffer = this.binEncode_SHORT_ARRAY(buffer, (Collection<Short>) object);
			break;
		case INT_ARRAY:
			buffer = this.binEncode_INT_ARRAY(buffer, (Collection<Integer>) object);
			break;
		case LONG_ARRAY:
			buffer = this.binEncode_LONG_ARRAY(buffer, (Collection<Long>) object);
			break;
		case FLOAT_ARRAY:
			buffer = this.binEncode_FLOAT_ARRAY(buffer, (Collection<Float>) object);
			break;
		case DOUBLE_ARRAY:
			buffer = this.binEncode_DOUBLE_ARRAY(buffer, (Collection<Double>) object);
			break;
		case UTF_STRING_ARRAY:
			buffer = this.binEncode_UTF_STRING_ARRAY(buffer, (Collection<String>) object);
			break;
		case ARRAY:
			buffer = this.addData(buffer, this.array2binary((CArray) object));
			break;
		case OBJECT:
			buffer = this.addData(buffer, this.object2binary((CObject) object));
			break;
		case CLASS:
			buffer = this.addData(buffer, this.object2binary(this.pojo2cbo(object)));
			break;
		default:
			throw new IllegalArgumentException("Unrecognized type in AVAObject serialization: " + typeId);
		}

		return buffer;
	}

	private DataWrapper binDecode_NULL(ByteBuffer buffer) {
		return new DataWrapper(DataType.NULL, (Object) null);
	}

	private DataWrapper binDecode_BOOL(ByteBuffer buffer) throws CCodecException {
		byte boolByte = buffer.get();
		Boolean bool = null;
		if (boolByte == 0) {
			bool = new Boolean(false);
		} else {
			if (boolByte != 1) {
				throw new CCodecException("Error decoding Bool type. Illegal value: " + bool);
			}

			bool = new Boolean(true);
		}

		return new DataWrapper(DataType.BOOL, bool);
	}

	private DataWrapper binDecode_BYTE(ByteBuffer buffer) {
		byte boolByte = buffer.get();
		return new DataWrapper(DataType.BYTE, Byte.valueOf(boolByte));
	}

	private DataWrapper binDecode_SHORT(ByteBuffer buffer) {
		short shortValue = buffer.getShort();
		return new DataWrapper(DataType.SHORT, Short.valueOf(shortValue));
	}

	private DataWrapper binDecode_INT(ByteBuffer buffer) {
		int intValue = buffer.getInt();
		return new DataWrapper(DataType.INT, Integer.valueOf(intValue));
	}

	private DataWrapper binDecode_LONG(ByteBuffer buffer) {
		long longValue = buffer.getLong();
		return new DataWrapper(DataType.LONG, Long.valueOf(longValue));
	}

	private DataWrapper binDecode_FLOAT(ByteBuffer buffer) {
		float floatValue = buffer.getFloat();
		return new DataWrapper(DataType.FLOAT, Float.valueOf(floatValue));
	}

	private DataWrapper binDecode_DOUBLE(ByteBuffer buffer) {
		double doubleValue = buffer.getDouble();
		return new DataWrapper(DataType.DOUBLE, Double.valueOf(doubleValue));
	}

	private DataWrapper binDecode_UTF_STRING(ByteBuffer buffer) throws CCodecException {
		short strLen = buffer.getShort();
		if (strLen < 0) {
			throw new CCodecException("Error decoding UtfString. Negative size: " + strLen);
		} else {
			byte[] strData = new byte[strLen];
			buffer.get(strData, 0, strLen);
			String decodedString = new String(strData);
			return new DataWrapper(DataType.UTF_STRING, decodedString);
		}
	}

	private DataWrapper binDecode_BOOL_ARRAY(ByteBuffer buffer) throws CCodecException {
		short arraySize = this.getTypeArraySize(buffer);
		ArrayList<Boolean> array = new ArrayList<Boolean>();

		for (int j = 0; j < arraySize; ++j) {
			byte boolData = buffer.get();
			if (boolData == 0) {
				array.add(Boolean.valueOf(false));
			} else {
				if (boolData != 1) {
					throw new CCodecException("Error decoding BoolArray. Invalid bool value: " + boolData);
				}

				array.add(Boolean.valueOf(true));
			}
		}

		return new DataWrapper(DataType.BOOL_ARRAY, array);
	}

	private DataWrapper binDecode_BYTE_ARRAY(ByteBuffer buffer) throws CCodecException {
		int arraySize = buffer.getInt();
		if (arraySize < 0) {
			throw new CCodecException("Error decoding typed array size. Negative size: " + arraySize);
		} else {
			byte[] byteData = new byte[arraySize];
			buffer.get(byteData, 0, arraySize);
			return new DataWrapper(DataType.BYTE_ARRAY, byteData);
		}
	}

	private DataWrapper binDecode_SHORT_ARRAY(ByteBuffer buffer) throws CCodecException {
		short arraySize = this.getTypeArraySize(buffer);
		ArrayList<Short> array = new ArrayList<Short>();

		for (int j = 0; j < arraySize; ++j) {
			short shortValue = buffer.getShort();
			array.add(Short.valueOf(shortValue));
		}

		return new DataWrapper(DataType.SHORT_ARRAY, array);
	}

	private DataWrapper binDecode_INT_ARRAY(ByteBuffer buffer) throws CCodecException {
		short arraySize = this.getTypeArraySize(buffer);
		ArrayList array = new ArrayList();

		for (int j = 0; j < arraySize; ++j) {
			int intValue = buffer.getInt();
			array.add(Integer.valueOf(intValue));
		}

		return new DataWrapper(DataType.INT_ARRAY, array);
	}

	private DataWrapper binDecode_LONG_ARRAY(ByteBuffer buffer) throws CCodecException {
		short arraySize = this.getTypeArraySize(buffer);
		ArrayList array = new ArrayList();

		for (int j = 0; j < arraySize; ++j) {
			long longValue = buffer.getLong();
			array.add(Long.valueOf(longValue));
		}

		return new DataWrapper(DataType.LONG_ARRAY, array);
	}

	private DataWrapper binDecode_FLOAT_ARRAY(ByteBuffer buffer) throws CCodecException {
		short arraySize = this.getTypeArraySize(buffer);
		ArrayList array = new ArrayList();

		for (int j = 0; j < arraySize; ++j) {
			float floatValue = buffer.getFloat();
			array.add(Float.valueOf(floatValue));
		}

		return new DataWrapper(DataType.FLOAT_ARRAY, array);
	}

	private DataWrapper binDecode_DOUBLE_ARRAY(ByteBuffer buffer) throws CCodecException {
		short arraySize = this.getTypeArraySize(buffer);
		ArrayList array = new ArrayList();

		for (int j = 0; j < arraySize; ++j) {
			double doubleValue = buffer.getDouble();
			array.add(Double.valueOf(doubleValue));
		}

		return new DataWrapper(DataType.DOUBLE_ARRAY, array);
	}

	private DataWrapper binDecode_UTF_STRING_ARRAY(ByteBuffer buffer) throws CCodecException {
		short arraySize = this.getTypeArraySize(buffer);
		ArrayList array = new ArrayList();

		for (int j = 0; j < arraySize; ++j) {
			short strLen = buffer.getShort();
			if (strLen < 0) {
				throw new CCodecException(
						"Error decoding UtfStringArray element. Element has negative size: " + strLen);
			}

			byte[] strData = new byte[strLen];
			buffer.get(strData, 0, strLen);
			array.add(new String(strData));
		}

		return new DataWrapper(DataType.UTF_STRING_ARRAY, array);
	}

	private short getTypeArraySize(ByteBuffer buffer) throws CCodecException {
		short arraySize = buffer.getShort();
		if (arraySize < 0) {
			throw new CCodecException("Error decoding typed array size. Negative size: " + arraySize);
		} else {
			return arraySize;
		}
	}

	private ByteBuffer binEncode_NULL(ByteBuffer buffer) {
		return this.addData(buffer, new byte[1]);
	}

	private ByteBuffer binEncode_BOOL(ByteBuffer buffer, Boolean value) {
		byte[] data = new byte[] { (byte) DataType.BOOL.typeID, (byte) (value.booleanValue() ? 1 : 0) };
		return this.addData(buffer, data);
	}

	private ByteBuffer binEncode_BYTE(ByteBuffer buffer, Byte value) {
		byte[] data = new byte[] { (byte) DataType.BYTE.typeID, value.byteValue() };
		return this.addData(buffer, data);
	}

	private ByteBuffer binEncode_SHORT(ByteBuffer buffer, Short value) {
		ByteBuffer buf = ByteBuffer.allocate(3);
		buf.put((byte) DataType.SHORT.typeID);
		buf.putShort(value.shortValue());
		return this.addData(buffer, buf.array());
	}

	private ByteBuffer binEncode_INT(ByteBuffer buffer, Integer value) {
		ByteBuffer buf = ByteBuffer.allocate(5);
		buf.put((byte) DataType.INT.typeID);
		buf.putInt(value.intValue());
		return this.addData(buffer, buf.array());
	}

	private ByteBuffer binEncode_LONG(ByteBuffer buffer, Long value) {
		ByteBuffer buf = ByteBuffer.allocate(9);
		buf.put((byte) DataType.LONG.typeID);
		buf.putLong(value.longValue());
		return this.addData(buffer, buf.array());
	}

	private ByteBuffer binEncode_FLOAT(ByteBuffer buffer, Float value) {
		ByteBuffer buf = ByteBuffer.allocate(5);
		buf.put((byte) DataType.FLOAT.typeID);
		buf.putFloat(value.floatValue());
		return this.addData(buffer, buf.array());
	}

	private ByteBuffer binEncode_DOUBLE(ByteBuffer buffer, Double value) {
		ByteBuffer buf = ByteBuffer.allocate(9);
		buf.put((byte) DataType.DOUBLE.typeID);
		buf.putDouble(value.doubleValue());
		return this.addData(buffer, buf.array());
	}

	private ByteBuffer binEncode_UTF_STRING(ByteBuffer buffer, String value) {
		byte[] stringBytes = value.getBytes();
		ByteBuffer buf = ByteBuffer.allocate(3 + stringBytes.length);
		buf.put((byte) DataType.UTF_STRING.typeID);
		buf.putShort((short) stringBytes.length);
		buf.put(stringBytes);
		return this.addData(buffer, buf.array());
	}

	private ByteBuffer binEncode_BOOL_ARRAY(ByteBuffer buffer, Collection<Boolean> value) {
		ByteBuffer buf = ByteBuffer.allocate(3 + value.size());
		buf.put((byte) DataType.BOOL_ARRAY.typeID);
		buf.putShort((short) value.size());
		Iterator var5 = value.iterator();

		while (var5.hasNext()) {
			boolean b = ((Boolean) var5.next()).booleanValue();
			buf.put((byte) (b ? 1 : 0));
		}

		return this.addData(buffer, buf.array());
	}

	private ByteBuffer binEncode_BYTE_ARRAY(ByteBuffer buffer, byte[] value) {
		ByteBuffer buf = ByteBuffer.allocate(5 + value.length);
		buf.put((byte) DataType.BYTE_ARRAY.typeID);
		buf.putInt(value.length);
		buf.put(value);
		return this.addData(buffer, buf.array());
	}

	private ByteBuffer binEncode_SHORT_ARRAY(ByteBuffer buffer, Collection<Short> value) {
		ByteBuffer buf = ByteBuffer.allocate(3 + 2 * value.size());
		buf.put((byte) DataType.SHORT_ARRAY.typeID);
		buf.putShort((short) value.size());
		Iterator var5 = value.iterator();

		while (var5.hasNext()) {
			short item = ((Short) var5.next()).shortValue();
			buf.putShort(item);
		}

		return this.addData(buffer, buf.array());
	}

	private ByteBuffer binEncode_INT_ARRAY(ByteBuffer buffer, Collection<Integer> value) {
		ByteBuffer buf = ByteBuffer.allocate(3 + 4 * value.size());
		buf.put((byte) DataType.INT_ARRAY.typeID);
		buf.putShort((short) value.size());
		Iterator<Integer> var5 = value.iterator();

		while (var5.hasNext()) {
			int item = ((Integer) var5.next()).intValue();
			buf.putInt(item);
		}

		return this.addData(buffer, buf.array());
	}

	private ByteBuffer binEncode_LONG_ARRAY(ByteBuffer buffer, Collection<Long> value) {
		ByteBuffer buf = ByteBuffer.allocate(3 + 8 * value.size());
		buf.put((byte) DataType.LONG_ARRAY.typeID);
		buf.putShort((short) value.size());
		Iterator<Long> var6 = value.iterator();

		while (var6.hasNext()) {
			long item = ((Long) var6.next()).longValue();
			buf.putLong(item);
		}

		return this.addData(buffer, buf.array());
	}

	private ByteBuffer binEncode_FLOAT_ARRAY(ByteBuffer buffer, Collection<Float> value) {
		ByteBuffer buf = ByteBuffer.allocate(3 + 4 * value.size());
		buf.put((byte) DataType.FLOAT_ARRAY.typeID);
		buf.putShort((short) value.size());
		Iterator<Float> var5 = value.iterator();

		while (var5.hasNext()) {
			float item = ((Float) var5.next()).floatValue();
			buf.putFloat(item);
		}

		return this.addData(buffer, buf.array());
	}

	private ByteBuffer binEncode_DOUBLE_ARRAY(ByteBuffer buffer, Collection<Double> value) {
		ByteBuffer buf = ByteBuffer.allocate(3 + 8 * value.size());
		buf.put((byte) DataType.DOUBLE_ARRAY.typeID);
		buf.putShort((short) value.size());
		Iterator<Double> var6 = value.iterator();

		while (var6.hasNext()) {
			double item = ((Double) var6.next()).doubleValue();
			buf.putDouble(item);
		}

		return this.addData(buffer, buf.array());
	}

	private ByteBuffer binEncode_UTF_STRING_ARRAY(ByteBuffer buffer, Collection<String> value) {
		int stringDataLen = 0;
		byte[][] binStrings = new byte[value.size()][];
		int count = 0;

		byte[] binStr;
		for (Iterator<String> binItem = value.iterator(); binItem.hasNext(); stringDataLen += 2 + binStr.length) {
			String buf = (String) binItem.next();
			binStr = buf.getBytes();
			binStrings[count++] = binStr;
		}

		ByteBuffer byteBuffer = ByteBuffer.allocate(3 + stringDataLen);
		byteBuffer.put((byte) DataType.UTF_STRING_ARRAY.typeID);
		byteBuffer.putShort((short) value.size());
		byte[][] var10 = binStrings;
		int length = binStrings.length;

		for (int i = 0; i < length; ++i) {
			byte[] var12 = var10[i];
			byteBuffer.putShort((short) var12.length);
			byteBuffer.put(var12);
		}

		return this.addData(buffer, byteBuffer.array());
	}

	private ByteBuffer encodeSFSObjectKey(ByteBuffer buffer, String value) {
		ByteBuffer buf = ByteBuffer.allocate(2 + value.length());
		buf.putShort((short) value.length());
		buf.put(value.getBytes());
		return this.addData(buffer, buf.array());
	}

	private ByteBuffer addData(ByteBuffer buffer, byte[] newData) {
		if (buffer.remaining() < newData.length) {
			int newSize = BUFFER_CHUNK_SIZE;
			if (newSize < newData.length) {
				newSize = newData.length;
			}

			ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() + newSize);
			buffer.flip();
			newBuffer.put(buffer);
			buffer = newBuffer;
		}

		buffer.put(newData);
		return buffer;
	}

	public IObject pojo2cbo(Object pojo) {
		CObject sfsObj = CObject.newInstance();
		try {
			this.convertPojo(pojo, sfsObj);
			return sfsObj;
		} catch (Exception var4) {
			throw new CRuntimeException(var4);
		}
	}

	private void convertPojo(Object pojo, IObject sfsObj)
			throws IllegalAccessException, NoSuchMethodException, IllegalArgumentException {
		Class pojoClazz = pojo.getClass();
		String classFullName = pojoClazz.getCanonicalName();
		if (classFullName == null) {
			throw new IllegalArgumentException("Anonymous classes cannot be serialized!");
		} else if (!(pojo instanceof SerializableType)) {
			throw new IllegalStateException("Cannot serialize object: " + pojo + ", type: " + classFullName
					+ " -- It doesn\'t implement the SerializableSFSType interface");
		} else {
			CArray fieldList = CArray.newInstance();
			sfsObj.putUtfString(CLASS_MARKER_KEY, classFullName);
			sfsObj.putSFSArray(CLASS_FIELDS_KEY, fieldList);
			Field[] var9;
			int var8 = (var9 = pojoClazz.getDeclaredFields()).length;

			for (int var7 = 0; var7 < var8; ++var7) {
				Field field = var9[var7];

				try {
					int err = field.getModifiers();
					if (!Modifier.isTransient(err) && !Modifier.isStatic(err)) {
						String fieldName = field.getName();
						Object fieldValue = null;
						if (Modifier.isPublic(err)) {
							fieldValue = field.get(pojo);
						} else {
							fieldValue = this.readValueFromGetter(fieldName, field.getType().getSimpleName(), pojo);
						}

						CObject fieldDescriptor = CObject.newInstance();
						fieldDescriptor.putUtfString(FIELD_NAME_KEY, fieldName);
						fieldDescriptor.put(FIELD_VALUE_KEY, this.wrapPojoField(fieldValue));
						fieldList.addSFSObject(fieldDescriptor);
					}
				} catch (Exception var14) {
					this.logger.info("-- No public getter -- Serializer skipping private field: " + field.getName()
							+ ", from class: " + pojoClazz);
					var14.printStackTrace();
				}
			}

		}
	}

	private Object readValueFromGetter(String fieldName, String type, Object pojo) throws Exception {
		Object value = null;
		boolean isBool = type.equalsIgnoreCase("boolean");
		String getterName = isBool ? "is" + StringUtils.capitalize(fieldName)
				: "get" + StringUtils.capitalize(fieldName);
		Method getterMethod = pojo.getClass().getMethod(getterName, new Class[0]);
		value = getterMethod.invoke(pojo, new Object[0]);
		return value;
	}

	private DataWrapper wrapPojoField(Object value) {
		if (value == null) {
			return new DataWrapper(DataType.NULL, (Object) null);
		} else {
			DataWrapper wrapper = null;
			if (value instanceof Boolean) {
				wrapper = new DataWrapper(DataType.BOOL, value);
			} else if (value instanceof Byte) {
				wrapper = new DataWrapper(DataType.BYTE, value);
			} else if (value instanceof Short) {
				wrapper = new DataWrapper(DataType.SHORT, value);
			} else if (value instanceof Integer) {
				wrapper = new DataWrapper(DataType.INT, value);
			} else if (value instanceof Long) {
				wrapper = new DataWrapper(DataType.LONG, value);
			} else if (value instanceof Float) {
				wrapper = new DataWrapper(DataType.FLOAT, value);
			} else if (value instanceof Double) {
				wrapper = new DataWrapper(DataType.DOUBLE, value);
			} else if (value instanceof String) {
				wrapper = new DataWrapper(DataType.UTF_STRING, value);
			} else if (value.getClass().isArray()) {
				wrapper = new DataWrapper(DataType.ARRAY, this.unrollArray((Object[]) value));
			} else if (value instanceof Collection) {
				wrapper = new DataWrapper(DataType.ARRAY, this.unrollCollection((Collection) value));
			} else if (value instanceof Map) {
				wrapper = new DataWrapper(DataType.OBJECT, this.unrollMap((Map) value));
			} else if (value instanceof SerializableType) {
				wrapper = new DataWrapper(DataType.OBJECT, this.pojo2cbo(value));
			}

			return wrapper;
		}
	}

	private IArray unrollArray(Object[] arr) {
		CArray array = CArray.newInstance();
		Object[] var6 = arr;
		int var5 = arr.length;

		for (int var4 = 0; var4 < var5; ++var4) {
			Object item = var6[var4];
			array.add(this.wrapPojoField(item));
		}

		return array;
	}

	private IArray unrollCollection(Collection collection) {
		CArray array = CArray.newInstance();
		Iterator<?> var4 = collection.iterator();

		while (var4.hasNext()) {
			Object item = var4.next();
			array.add(this.wrapPojoField(item));
		}

		return array;
	}

	private IObject unrollMap(Map map) {
		CObject sfsObj = CObject.newInstance();
		Set entries = map.entrySet();
		Iterator iter = entries.iterator();

		while (iter.hasNext()) {
			Entry item = (Entry) iter.next();
			Object key = item.getKey();
			if (key instanceof String) {
				sfsObj.put((String) key, this.wrapPojoField(item.getValue()));
			}
		}

		return sfsObj;
	}

	public Object cbo2pojo(IObject sfsObj) {
		Object pojo = null;
		if (!sfsObj.containsKey(CLASS_MARKER_KEY) && !sfsObj.containsKey(CLASS_FIELDS_KEY)) {
			throw new CRuntimeException("The AVAObject passed does not represent any serialized class.");
		} else {
			try {
				String e = sfsObj.getUtfString(CLASS_MARKER_KEY);
				Class theClass = Class.forName(e);
				pojo = theClass.newInstance();
				if (!(pojo instanceof SerializableType)) {
					throw new IllegalStateException("Cannot deserialize object: " + pojo + ", type: " + e
							+ " -- It doesn\'t implement the SerializableSFSType interface");
				} else {
					this.convertSFSObject(sfsObj.getSFSArray(CLASS_FIELDS_KEY), pojo);
					return pojo;
				}
			} catch (Exception var5) {
				throw new CRuntimeException(var5);
			}
		}
	}

	private void convertSFSObject(IArray fieldList, Object pojo) throws Exception {
		for (int j = 0; j < fieldList.size(); ++j) {
			IObject fieldDescriptor = fieldList.getObject(j);
			String fieldName = fieldDescriptor.getUtfString(FIELD_NAME_KEY);
			Object fieldValue = this.unwrapPojoField(fieldDescriptor.get(FIELD_VALUE_KEY));
			this.setObjectField(pojo, fieldName, fieldValue);
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setObjectField(Object pojo, String fieldName, Object fieldValue) throws Exception {
		Class<?> pojoClass = pojo.getClass();
		Field field = pojoClass.getDeclaredField(fieldName);
		int fieldModifier = field.getModifiers();
		if (!Modifier.isTransient(fieldModifier)) {
			boolean isArray = field.getType().isArray();
			Collection<?> collection;
			if (isArray) {
				if (!(fieldValue instanceof Collection)) {
					throw new CRuntimeException(
							"Problem during AVAObject => POJO conversion. Found array field in POJO: " + fieldName
									+ ", but data is not a Collection!");
				}

				collection = (Collection) fieldValue;
				Object[] fieldValue1 = collection.toArray();
				int fieldClass = collection.size();
				Object typedArray = Array.newInstance(field.getType().getComponentType(), fieldClass);
				System.arraycopy(fieldValue1, 0, typedArray, 0, fieldClass);
				fieldValue = typedArray;
			} else if (fieldValue instanceof Collection) {
				collection = (Collection) fieldValue;
				String fieldClass1 = field.getType().getSimpleName();
				if (fieldClass1.equals("ArrayList") || fieldClass1.equals("List")) {
					fieldValue = new ArrayList(collection);
				}

				if (fieldClass1.equals("CopyOnWriteArrayList")) {
					fieldValue = new CopyOnWriteArrayList(collection);
				} else if (fieldClass1.equals("LinkedList")) {
					fieldValue = new LinkedList(collection);
				} else if (fieldClass1.equals("Vector")) {
					fieldValue = new Vector(collection);
				} else if (!fieldClass1.equals("Set") && !fieldClass1.equals("HashSet")) {
					if (fieldClass1.equals("LinkedHashSet")) {
						fieldValue = new LinkedHashSet(collection);
					} else if (fieldClass1.equals("TreeSet")) {
						fieldValue = new TreeSet(collection);
					} else if (fieldClass1.equals("CopyOnWriteArraySet")) {
						fieldValue = new CopyOnWriteArraySet(collection);
					} else if (!fieldClass1.equals("Queue") && !fieldClass1.equals("PriorityQueue")) {
						if (!fieldClass1.equals("BlockingQueue") && !fieldClass1.equals("LinkedBlockingQueue")) {
							if (fieldClass1.equals("PriorityBlockingQueue")) {
								fieldValue = new PriorityBlockingQueue(collection);
							} else if (fieldClass1.equals("ConcurrentLinkedQueue")) {
								fieldValue = new ConcurrentLinkedQueue(collection);
							} else if (fieldClass1.equals("DelayQueue")) {
								fieldValue = new DelayQueue(collection);
							} else if (!fieldClass1.equals("Deque") && !fieldClass1.equals("ArrayDeque")) {
								if (fieldClass1.equals("LinkedBlockingDeque")) {
									fieldValue = new LinkedBlockingDeque(collection);
								}
							} else {
								fieldValue = new ArrayDeque(collection);
							}
						} else {
							fieldValue = new LinkedBlockingQueue(collection);
						}
					} else {
						fieldValue = new PriorityQueue(collection);
					}
				} else {
					fieldValue = new HashSet(collection);
				}
			}

			if (Modifier.isPublic(fieldModifier)) {
				field.set(pojo, fieldValue);
			} else {
				this.writeValueFromSetter(field, pojo, fieldValue);
			}

		}
	}

	private void writeValueFromSetter(Field field, Object pojo, Object fieldValue) throws Exception {
		String setterName = "set" + StringUtils.capitalize(field.getName());

		try {
			Method setterMethod = pojo.getClass().getMethod(setterName, new Class[] { field.getType() });
			setterMethod.invoke(pojo, new Object[] { fieldValue });
		} catch (NoSuchMethodException var7) {
			this.logger.info("-- No public setter -- Serializer skipping private field: " + field.getName()
					+ ", from class: " + pojo.getClass().getName());
		}

	}

	private Object unwrapPojoField(DataWrapper wrapper) {
		Object obj = null;
		DataType type = wrapper.getTypeId();
		if (type.typeID <= DataType.UTF_STRING.typeID) {
			obj = wrapper.getObject();
		} else if (type == DataType.ARRAY) {
			obj = this.rebuildArray((IArray) wrapper.getObject());
		} else if (type == DataType.OBJECT) {
			obj = this.rebuildMap((IObject) wrapper.getObject());
		} else if (type == DataType.CLASS) {
			obj = wrapper.getObject();
		}

		return obj;
	}

	private Object rebuildArray(IArray sfsArray) {
		ArrayList<Object> collection = new ArrayList<Object>();
		Iterator<DataWrapper> iter = sfsArray.iterator();

		while (iter.hasNext()) {
			Object item = this.unwrapPojoField((DataWrapper) iter.next());
			collection.add(item);
		}

		return collection;
	}

	private Object rebuildMap(IObject sfsObj) {
		HashMap map = new HashMap();
		Iterator var4 = sfsObj.getKeys().iterator();

		while (var4.hasNext()) {
			String key = (String) var4.next();
			DataWrapper wrapper = sfsObj.get(key);
			map.put(key, this.unwrapPojoField(wrapper));
		}

		return map;
	}

	public String array2json(List list) {
		return JSON.toJSONString(list);
	}

	@Override
	public String object2json(Map map) {
		return JSON.toJSONString(map);
	}

}
