package com.codebroker.util;

import java.nio.ByteBuffer;

public class ByteUtils {

	private static final int HEX_BYTES_PER_LINE = 16;
	private static final char TAB = 9;
	private static final String NEW_LINE = System.getProperty("line.separator");
	private static final char DOT = 46;

	public static byte[] resizeByteArray(byte source[], int pos, int size) {
		byte tmpArray[] = new byte[size];
		System.arraycopy(source, pos, tmpArray, 0, size);
		return tmpArray;
	}

	public static String fullHexDump(ByteBuffer buffer, int bytesPerLine) {
		return fullHexDump(buffer.array(), bytesPerLine);
	}

	public static String fullHexDump(ByteBuffer buffer) {
		return fullHexDump(buffer.array(), HEX_BYTES_PER_LINE);
	}

	public static String fullHexDump(byte buffer[]) {
		return fullHexDump(buffer, HEX_BYTES_PER_LINE);
	}

	public static String fullHexDump(byte buffer[], int bytesPerLine) {
		StringBuilder sb = new StringBuilder("Binary size: ").append(buffer.length).append("\n");
		StringBuilder hexLine = new StringBuilder();
		StringBuilder chrLine = new StringBuilder();
		int index = 0;
		int count = 0;
		do {
			byte currByte = buffer[index];
			String hexByte = Integer.toHexString(currByte & 0xff);
			if (hexByte.length() == 1) {
				hexLine.append("0");
			}
			hexLine.append(hexByte.toUpperCase()).append(" ");
			char currChar = currByte < 33 || currByte > 126 ? DOT : (char) currByte;
			chrLine.append(currChar);
			if (++count == bytesPerLine) {
				count = 0;
				sb.append(((CharSequence) (hexLine))).append(TAB).append(((CharSequence) (chrLine))).append(NEW_LINE);
				hexLine.delete(0, hexLine.length());
				chrLine.delete(0, chrLine.length());
			}
		} while (++index < buffer.length);
		if (count != 0) {
			for (int j = bytesPerLine - count; j > 0; j--) {
				hexLine.append(TAB);
				chrLine.append(" ");
			}

			sb.append(((CharSequence) (hexLine))).append(TAB).append(((CharSequence) (chrLine))).append(NEW_LINE);
		}
		return sb.toString();
	}

}
