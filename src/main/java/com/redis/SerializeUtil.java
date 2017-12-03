package com.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author lh
 */
@SuppressWarnings("unused")
public class SerializeUtil {
	/**
	 * 序列化
	 *
	 * @param object
	 * @return
	 */
	public static byte[] serialize(Object object) {
		ObjectOutputStream objectOutputStream;
		ByteArrayOutputStream byteArrayOutputStream;
		try {
			// 序列化
			byteArrayOutputStream = new ByteArrayOutputStream();
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(object);
			byte[] bytes = byteArrayOutputStream.toByteArray();
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 反序列化
	 *
	 * @param bytes
	 * @return
	 */
	public static Object unSerialize(byte[] bytes) {
		ByteArrayInputStream byteArrayInputStream;
		try {
			// 反序列化
			byteArrayInputStream = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(byteArrayInputStream);
			return ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
