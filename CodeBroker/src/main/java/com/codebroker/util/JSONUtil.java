package com.codebroker.util;

import com.alibaba.fastjson.JSON;

public class JSONUtil {

	public static String objectToFastJSON(Object object) {
		return JSON.toJSONString(object);
	}

}
