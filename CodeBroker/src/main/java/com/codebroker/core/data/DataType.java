package com.codebroker.core.data;

public enum DataType {
	NULL, BOOL, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, UTF_STRING, BOOL_ARRAY, BYTE_ARRAY, SHORT_ARRAY, INT_ARRAY, LONG_ARRAY, FLOAT_ARRAY, DOUBLE_ARRAY, UTF_STRING_ARRAY, ARRAY, OBJECT, CLASS;

	private int typeID;

	public static DataType fromTypeId(int typeId) {
		for (DataType item : values()) {
			if (item.getTypeID() == typeId) {
				return item;
			}
		}

		throw new IllegalArgumentException("Unknown typeId for DataType");
	}

	public static DataType fromClass(Class<?> clazz) {
		return null;
	}

	public int getTypeID() {
		return this.typeID;
	}
}
