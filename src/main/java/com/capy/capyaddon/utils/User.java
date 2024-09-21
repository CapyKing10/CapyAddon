package com.capy.capyaddon.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class User {
    public static String getHWID() {
        try {
            StringBuilder sb = new StringBuilder();
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : interfaces) {
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X", mac[i]));
                    }
                }
            }
            return hashString(sb.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isDev() {
        return
            Objects.equals(getHWID(), "0b4ecb3c74556a4384e3ffa05b2f9899168353c7d22ee712639d0f843ce96784") ||
            Objects.equals(getHWID(), "be315cb7382e71830edbe681c2d0ac50b4f11dec0638628d299263e4867e9e83");
    }
/*
    public static boolean isBeta() {
    }

 */
}
