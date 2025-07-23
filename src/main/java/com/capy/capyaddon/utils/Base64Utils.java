package com.capy.capyaddon.utils;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Base64Utils {

    public static String encodeListToBase64(List<Integer> numbers) {
        byte[] bytes = new byte[numbers.size() * 4]; // 4 bytes per int
        for (int i = 0; i < numbers.size(); i++) {
            int value = numbers.get(i);
            int index = i * 4;
            bytes[index] = (byte) (value >> 24);
            bytes[index + 1] = (byte) (value >> 16);
            bytes[index + 2] = (byte) (value >> 8);
            bytes[index + 3] = (byte) value;
        }
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static List<Integer> decodeBase64ToList(String base64) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < bytes.length; i += 4) {
            int value = ((bytes[i] & 0xFF) << 24) |
                ((bytes[i + 1] & 0xFF) << 16) |
                ((bytes[i + 2] & 0xFF) << 8) |
                (bytes[i + 3] & 0xFF);
            numbers.add(value);
        }
        return numbers;
    }

}
