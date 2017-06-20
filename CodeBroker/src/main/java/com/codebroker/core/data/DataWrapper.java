package com.codebroker.core.data;

import java.io.Serializable;

public class DataWrapper implements Serializable {

	private static final long serialVersionUID = -3218718719001693201L;

	private DataType typeId;
	private Object object;

	public DataWrapper(DataType typeId, Object object) {
		this.typeId = typeId;
		this.object = object;
	}

	public DataType getTypeId() {
		return typeId;
	}

	public Object getObject() {
		return object;
	}
}
