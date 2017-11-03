package com.codebroker.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
    public static List<String> getLocalAreaId(List<String> ids){
        List<String> result=new ArrayList<String>();
        for (String id:
             ids) {
            result.add(getLocalAreaId(id));
        }
        return result;

    }

    public static String getLocalAreaId(String areaId){
        String[] split = areaId.split(":");
        String[] split1 = split[1].split("_");
        return split1[1];
    }
}
