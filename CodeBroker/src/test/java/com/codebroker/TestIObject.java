package com.codebroker;

import com.alibaba.fastjson.JSONObject;
import com.codebroker.core.data.CObject;
import com.codebroker.core.data.IObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestIObject {

    @Test
    public void testJSON2IObject() {
        JSONObject json = new JSONObject();
        json.put("name", "ONE");
        json.put("Level", 16);

        IObject iObject = CObject.newFromJsonData(json.toJSONString());
        assert iObject.getUtfString("name").equals("ONE");
        assert iObject.getInt("Level") == 16;
    }

    @Test
    public void testMap2IObject() {
        Map<Object, Object> map = new HashMap<>();
        map.put("name", "ONE");
        map.put("Level", 16);

        assert map.get("name").equals("ONE");
        assert map.get("Level").equals(16);
        assert map.get("Level").equals(19);
    }
}
