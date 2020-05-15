package com.codebroker.protocol.serialization;


import java.util.Arrays;

public class DefaultObjectDumpFormatter {

    public static String prettyPrintByteArray(byte bytes[]) {
        if (bytes == null) {
            return "Null";
        } else {
            return String.format("Byte[%s]", new Object[]{Integer.valueOf(bytes.length)});
        }
    }

    public static String prettyPrintDump(String rawDump) {
        StringBuilder buf = new StringBuilder();
        int indentPos = 0;
        for (int i = 0; i < rawDump.length(); i++) {
            char ch = rawDump.charAt(i);
            if (ch == '{') {
                indentPos++;
                buf.append("\n").append(getFormatTabs(indentPos));
            } else if (ch == '}') {
                if (--indentPos < 0)
                    throw new IllegalStateException("Argh! The indentPos is negative. TOKENS ARE NOT BALANCED!");
                buf.append("\n").append(getFormatTabs(indentPos));
            } else if (ch == ';')
                buf.append("\n").append(getFormatTabs(indentPos));
            else
                buf.append(ch);
        }

        if (indentPos != 0)
            throw new IllegalStateException("Argh! The indentPos is not == 0. TOKENS ARE NOT BALANCED!");
        else
            return buf.toString();
    }

    private static String getFormatTabs(int howMany) {
        return strFill('\t', howMany);
    }

    private static String strFill(char c, int howMany) {
        char chars[] = new char[howMany];
        Arrays.fill(chars, c);
        return new String(chars);
    }
}
