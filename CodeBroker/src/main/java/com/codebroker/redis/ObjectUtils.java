package com.codebroker.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 套用java原生的序列化
 * 
 * @author xl
 *
 */
public class ObjectUtils {

	/**
	 * 将Object序列化为byte数组
	 *
	 * @param obj
	 *            对象
	 * @return
	 * @throws IOException
	 */
	public static byte[] ObjectToBytes(Object obj) throws IOException {
		byte[] bytes = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;

		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(obj);
			bytes = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				bos.close();
			}
			if (oos != null) {
				oos.close();
			}
		}
		return bytes;
	}

	/**
	 * 将bytes数组反序列化为Object
	 *
	 * @param bytes
	 *            对象bytes
	 * @param clazz
	 *            对象类型
	 * @param <T>
	 * @return
	 * @throws IOException
	 */
	public static <T extends Serializable> T ObjectFromBytes(byte[] bytes, Class<T> clazz) throws IOException {
		ByteArrayInputStream byis = null;
		ObjectInputStream bis = null;
		T obj = null;
		try {
			byis = new ByteArrayInputStream(bytes);
			bis = new ObjectInputStream(byis);
			// 将对象进行强转
			obj = (T) bis.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (byis != null) {
				byis.close();
			}
			if (bis != null) {
				bis.close();
			}
		}
		return obj;
	}

	/**
	 * 将对象转换成字符串
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public String ObjectToString(Object obj) throws IOException {
		byte[] bytes = ObjectToBytes(obj);
		return new String(bytes);
	}

	/**
	 * 将对象String字符串转换为Object
	 * 
	 * @param objStr
	 *            对象字符串
	 * @param clazz
	 *            需要转换的类型
	 * @param <T>
	 * @return
	 * @throws IOException
	 */
	public <T extends Serializable> T ObjectFromString(String objStr, Class<T> clazz) throws IOException {
		return ObjectFromBytes(objStr.getBytes(), clazz);
	}
}
