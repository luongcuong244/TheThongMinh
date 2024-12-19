/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package thethongminh.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import javax.smartcardio.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ACER
 */
public class CardUtils {

    public static byte[] createData(byte[]... parameters) {

        int totalLength = 0;
        for (byte[] param : parameters) {
            totalLength += param.length;
        }

        byte[] command = new byte[totalLength];

        int currentPosition = 0;
        for (byte[] param : parameters) {
            System.arraycopy(param, 0, command, currentPosition, param.length);
            currentPosition += param.length;
        }

        return command;
    }

    public static byte[][] splitData(byte[] data, byte separator, int numberOfFields) {
        byte[][] result = new byte[numberOfFields][];
        int start = 0, fieldIndex = 0;

        for (int i = 0; i < data.length; i++) {
            if (data[i] == separator) {
                if (fieldIndex < numberOfFields - 1) {
                    result[fieldIndex++] = Arrays.copyOfRange(data, start, i);
                    start = i + 1;
                } else {

                    break;
                }
            }
        }

        result[fieldIndex] = Arrays.copyOfRange(data, start, data.length);

        return result;
    }

    public static String converBytesToHex(byte[] bytes) { // debug
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString().trim();
    }

    public static String convertSWToHex(int sw) { // debug
        return String.format("%04X", sw);
    }

    public static int covertBytesToInt(byte[] responseBytes) {
        int result = 0;
        for (byte b : responseBytes) {
            result = (result << 8) | (b & 0xFF);
        }
        return result;
    }

    public static byte[] convertPinToByte(String pin) {
        byte[] result = new byte[pin.length()];
        for (int i = 0; i < pin.length(); i++) {
            result[i] = (byte) (pin.charAt(i) - '0');
        }
        return result;
    }

    public static byte[] convertStringUTF8ToBytes(String stringData) {
        return stringData.getBytes(StandardCharsets.UTF_8);
    }

    public static String convertBytesToStringUTF8(byte[] byteData) {
        return new String(byteData, StandardCharsets.UTF_8);
    }

    public static byte[] convertLongToBytes(long value) {
        return ByteBuffer.allocate(Long.BYTES).putLong(value).array();
    }

    public static byte[] convertIntToBytes(int value) {
        return ByteBuffer.allocate(Integer.BYTES).putInt(value).array();
    }

}
