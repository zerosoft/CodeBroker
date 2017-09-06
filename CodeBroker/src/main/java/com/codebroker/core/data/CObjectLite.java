package com.codebroker.core.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CObjectLite extends CObject {


	public static CObject newInstance() {
		return new CObjectLite();
	}

	public Byte getByte(String key) {
		Integer i = getInt(key);
		return i == null ? null : Byte.valueOf(i.byteValue());
	}

	public Short getShort(String key) {
		Integer i = super.getInt(key);
		return i == null ? null : Short.valueOf(i.shortValue());
	}

	public Float getFloat(String key) {
		Double d = super.getDouble(key);
		return d == null ? null : Float.valueOf(d.floatValue());
	}

	public Collection<Boolean> getBoolArray(String key) {
		IArray arr = getSFSArray(key);
		if (arr == null) {
			return null;
		}
		List<Boolean> data = new ArrayList<Boolean>();
		for (int i = 0; i < arr.size(); i++) {
			data.add(arr.getBool(i));
		}

		return data;
	}

	public Collection<Short> getShortArray(String key) {
		IArray arr = getSFSArray(key);
		if (arr == null) {
			return null;
		}
		List<Short> data = new ArrayList<Short>();
		for (int i = 0; i < arr.size(); i++) {
			data.add(arr.getShort(i));
		}

		return data;
	}

	public Collection<Integer> getIntArray(String key) {
		IArray arr = getSFSArray(key);
		if (arr == null) {
			return null;
		}
		List<Integer> data = new ArrayList<Integer>();
		for (int i = 0; i < arr.size(); i++) {
			data.add(arr.getInt(i));
		}

		return data;
	}

	public Collection<Float> getFloatArray(String key) {
		IArray arr = getSFSArray(key);
		if (arr == null) {
			return null;
		}
		List<Float> data = new ArrayList<Float>();
		for (int i = 0; i < arr.size(); i++) {
			data.add(arr.getFloat(i));
		}

		return data;
	}

	public Collection<Double> getDoubleArray(String key) {
		IArray arr = getSFSArray(key);
		if (arr == null) {
			return null;
		}
		List<Double> data = new ArrayList<Double>();
		for (int i = 0; i < arr.size(); i++) {
			data.add(arr.getDouble(i));
		}

		return data;
	}

	public Collection<String> getUtfStringArray(String key) {
		IArray arr = getSFSArray(key);
		if (arr == null) {
			return null;
		}
		List<String> data = new ArrayList<String>();
		for (int i = 0; i < arr.size(); i++) {
			data.add(arr.getUtfString(i));
		}
		return data;
	}
}
