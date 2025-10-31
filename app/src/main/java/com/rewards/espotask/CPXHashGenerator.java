package com.rewards.espotask;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CPXHashGenerator {

    /**
     * Generate secure hash for CPX Research
     * Formula: MD5(app_id-user_id-secure_key)
     *
     * @param appId Your CPX Research App ID
     * @param userId Your user's unique ID
     * @param secureKey Your CPX Research Secure Key (from dashboard)
     * @return MD5 hash string
     */
    public static String generateHash(String appId, String userId, String secureKey) {
        try {
            String data = appId + "-" + userId + "-" + secureKey;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(data.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
}