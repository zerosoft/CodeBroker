//package com.codebroker.core.data;
//
//import java.util.ArrayList;
//import java.util.Collection;
//
//public class CArrayLite extends CArray {
//
//    public static CArrayLite newInstance() {
//        return new CArrayLite();
//    }
//
//    public Byte getByte(int index) {
//        Integer i = super.getInt(index);
//        return i != null ? Byte.valueOf(i.byteValue()) : null;
//    }
//
//    public Short getShort(int index) {
//        Integer i = super.getInt(index);
//        return i != null ? Short.valueOf(i.shortValue()) : null;
//    }
//
//    public Float getFloat(int index) {
//        Double d = super.getDouble(index);
//        return d != null ? Float.valueOf(d.floatValue()) : null;
//    }
//
//    public Collection<Boolean> getBoolArray(int key) {
//        IArray arr = this.getArray(key);
//        if (arr == null) {
//            return null;
//        } else {
//            ArrayList<Boolean> data = new ArrayList<>();
//
//            for (int i = 0; i < arr.size(); ++i) {
//                data.add(arr.getBool(i));
//            }
//
//            return data;
//        }
//    }
//
//    public Collection<Short> getShortArray(int key) {
//        IArray arr = this.getArray(key);
//        if (arr == null) {
//            return null;
//        } else {
//            ArrayList<Short> data = new ArrayList<>();
//
//            for (int i = 0; i < arr.size(); ++i) {
//                data.add(Short.valueOf(arr.getInt(i).shortValue()));
//            }
//
//            return data;
//        }
//    }
//
//    public Collection<Integer> getIntArray(int key) {
//        IArray arr = this.getArray(key);
//        if (arr == null) {
//            return null;
//        } else {
//            ArrayList<Integer> data = new ArrayList<>();
//
//            for (int i = 0; i < arr.size(); ++i) {
//                data.add(arr.getInt(i));
//            }
//
//            return data;
//        }
//    }
//
//    public Collection<Float> getFloatArray(int key) {
//        IArray arr = this.getArray(key);
//        if (arr == null) {
//            return null;
//        } else {
//            ArrayList<Float> data = new ArrayList<>();
//
//            for (int i = 0; i < arr.size(); ++i) {
//                data.add(Float.valueOf(arr.getDouble(i).floatValue()));
//            }
//
//            return data;
//        }
//    }
//
//    public Collection<Double> getDoubleArray(int key) {
//        IArray arr = this.getArray(key);
//        if (arr == null) {
//            return null;
//        } else {
//            ArrayList<Double> data = new ArrayList<>();
//
//            for (int i = 0; i < arr.size(); ++i) {
//                data.add(arr.getDouble(i));
//            }
//
//            return data;
//        }
//    }
//
//    public Collection<String> getUtfStringArray(int key) {
//        IArray arr = this.getArray(key);
//        if (arr == null) {
//            return null;
//        } else {
//            ArrayList<String> data = new ArrayList<>();
//
//            for (int i = 0; i < arr.size(); ++i) {
//                data.add(arr.getUtfString(i));
//            }
//
//            return data;
//        }
//    }
//}
