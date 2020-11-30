//package com.codebroker.protocol;
//
//import com.codebroker.core.data.IArray;
//import com.codebroker.core.data.IObject;
//
//import java.io.Serializable;
//import java.util.List;
//import java.util.Map;
//
//public interface IDataSerializer extends Serializable {
//
//    byte[] object2binary(IObject iObject);
//
//    byte[] array2binary(IArray var1);
//
//    IObject binary2object(byte[] var1);
//
//    IArray binary2array(byte[] var1);
//
//    String object2json(Map<String, Object> var1);
//
//    String array2json(List<Object> var1);
//
//    IObject json2object(String var1);
//
//    IArray json2array(String var1);
//
//    IObject pojo2cbo(Object var1);
//
//    Object cbo2pojo(IObject var1);
//
//}
