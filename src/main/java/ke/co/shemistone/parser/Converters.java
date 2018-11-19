/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ke.co.shemistone.parser;

/**
 *
 * @author shemistone
 */
public class Converters {

    public static String hexToAscii(String hexValue) {
        if (hexValue.length() % 2 != 0) {
            hexValue = "0" + hexValue;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < hexValue.length(); i += 2) {
            String string = hexValue.substring(i, i + 2);
            // Change value from hexadecimal to decimal
            int hex = Integer.parseInt(string, 16);// Base 16 - Hexadecimal radix
            byte[] bytes = String.valueOf(Character.toChars(hex)).getBytes();
            builder.append(new String(bytes));
        }
        return builder.toString();
    }

    public static String asciiToHex(String asciiValue) {
        StringBuilder builder = new StringBuilder();
        char[] chars = asciiValue.toCharArray();
        String string;
        for (int i = 0; i < chars.length; i++) {
            string = Integer.toHexString((int) chars[i]).toUpperCase();
            if (string.length() == 1) {
                builder.append("0");
            }
            builder.append(string);
        }
        return builder.toString();
    }

    public static String hexToBin(String hexValue) {
        if (hexValue.length() % 2 != 0) {
            hexValue = "0" + hexValue;
        }
        StringBuilder output = new StringBuilder();
        // You need two hex digits to change to binary
        for (int i = 0; i < hexValue.length(); i += 2) {
            String string = hexValue.substring(i, i + 2);
            // Change value from hexadecimal to decimal
            int hex = Integer.parseInt(string, 16);
            String bin = Integer.toBinaryString(hex);
            int len = bin.length();
            if (len != 8) {
                for (int j = len; j < 8; j++) {
                    output.append("0");
                }
            }
            output.append(bin);
        }
        return output.toString();
    }

    public static String binToAscii(String binValue) {
        if (binValue.length() % 2 != 0) {
            binValue = "0" + binValue;
        }
        String string = new String();
        int length = binValue.length();
        for (int i = 0; i <= length - 1; i += 8) {
            int byteValue = Integer.parseInt(binValue.substring(i, i + 8), 2);
            string = string + (char) byteValue;
        }
        return string;
    }

    public static String binToHex(String binValue) {
        if (binValue.length() % 2 != 0) {
            binValue = "0" + binValue;
        }
        StringBuilder builder = new StringBuilder();
        int length = binValue.length();
        for (int i = 0; i <= length - 1; i += 8) {
            int intVal = Integer.parseInt(binValue.substring(i, i + 8), 2);
            String hexVal = Integer.toHexString(intVal).toUpperCase();
            if (hexVal.length() == 1) {
                builder.append("0");
            }
            builder.append(hexVal);
        }
        return builder.toString();
    }

    public static String asciiToBin(String asciiValue) {
        return Converters.hexToBin(Converters.asciiToHex(asciiValue));
    }

    public static int hexToInt(String hexValue) {
        if (hexValue.length() % 2 != 0) {
            hexValue = "0" + hexValue;
        }
        return Integer.parseInt(hexValue, 16);
    }

    public static byte[] hexToBytes(String hexValue) {
        if (hexValue.length() % 2 != 0) {
            hexValue = "0" + hexValue;
        }
        byte[] bytes = new byte[hexValue.length() / 2];
        for (int i = 0; i < hexValue.length(); i += 2) {
            String hexVal = hexValue.substring(i, i + 2);
            bytes[i / 2] = (byte) (Integer.parseInt(hexVal, 16) & 0xFF);
        }
        return bytes;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            builder.append(Character.forDigit((bytes[i] >> 4) & 0xF, 16));
            builder.append(Character.forDigit((bytes[i] & 0xF), 16));
        }
        return builder.toString().toUpperCase();
    }

}
