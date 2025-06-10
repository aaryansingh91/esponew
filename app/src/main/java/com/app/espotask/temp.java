package com.app.espotask;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public interface temp {

    static String sha256_temp(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        hexString.append("pgm&usr=430t3892035&typeof=game&sourceis=aksofts");

        return hexString.toString();
    }
}
