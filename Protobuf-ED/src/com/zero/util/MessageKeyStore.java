package com.zero.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MessageKeyStore implements Serializable {

	private static final long serialVersionUID = 6340370310507098705L;

	public Map<String, Integer> classNameKey = new HashMap<>();

	public static void writeClass(MessageKeyStore e) {
		FileOutputStream fileOut;
		try {
			Util.isExist(".\\tmp\\mk");
			fileOut = new FileOutputStream(".\\tmp\\mk");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(e);
			out.close();
			fileOut.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static MessageKeyStore readClass() {
		MessageKeyStore e = null;
		File file = new File(".\\tmp\\mk");
		if (!file.exists()) {
			return new MessageKeyStore();
		}
		try {
			FileInputStream fileIn = new FileInputStream("./tmp/mk");
			ObjectInputStream in = new ObjectInputStream(fileIn);
			e = (MessageKeyStore) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			return new MessageKeyStore();
		} catch (ClassNotFoundException c) {
			System.out.println("MessageKeyStore class not found");
			return null;
		}
		return e;
	}
}
