package com.vcbl.tabbanking.tools;

import java.io.UnsupportedEncodingException;

public class Conversions {

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    private static Conversions conversions = null;

    private Conversions() {
    }

    public static Conversions getConversionsInstance() {
        if (conversions == null) conversions = new Conversions();
        return conversions;
    }

    public static String stringToHex(String input) {
        if (input == null) throw new NullPointerException();
        return asHex(input.getBytes());
    }

    public static String hexToString(String txtInHex) {
        byte[] txtInByte = new byte[txtInHex.length() / 2];
        int j = 0;
        for (int i = 0; i < txtInHex.length(); i += 2) {
            txtInByte[j++] = Byte.parseByte(txtInHex.substring(i, i + 2), 16);
        }
        return new String(txtInByte);
    }

    public static String hexToAscii(String s) {
        StringBuilder sb = new StringBuilder(s.length() / 2);
        for (int i = 0; i < s.length(); i += 2) {
            String hex = "" + s.charAt(i) + s.charAt(i + 1);
            int ival = Integer.parseInt(hex, 16);
            sb.append((char) ival);
        }
        return sb.toString();
    }

    private static String asHex(byte[] buf) {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i) {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }

    public static byte[] hexToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return data;
    }

}

