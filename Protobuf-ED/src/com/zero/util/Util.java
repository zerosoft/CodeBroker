package com.zero.util;

import java.io.File;

public class Util {
	public static String getJavaName(String name) {
		String result = "";
		String[] split = name.split("_");
		for (String string : split) {
			result += string.substring(0, 1) + string.substring(1, string.length()).toLowerCase();
		}
		return result.trim();
	}
	
	public static boolean isExist(String filePath) {
		String paths[] = filePath.split("\\\\");
		String dir = paths[0];
		for (int i = 0; i < paths.length - 2; i++) {// 注意此处循环的长度
			try {
				dir = dir + "/" + paths[i + 1];
				File dirFile = new File(dir);
				if (!dirFile.exists()) {
					dirFile.mkdir();
					System.out.println("创建目录为：" + dir);
				}
			} catch (Exception err) {
				System.err.println("ELS - Chart : 文件夹创建发生异常");
			}
		}
		File fp = new File(filePath);
		if (!fp.exists()) {
			return true; // 文件不存在，执行下载功能
		} else {
			return false; // 文件存在不做处理
		}
	}
}
