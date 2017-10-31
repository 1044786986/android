package com.example.ljh.wechat;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ljh on 2017/10/27.
 */

public class EncodeManager {

    public static final String KEY_SHA = "SHA";

    public static String ShaEncode(String data){
        BigInteger bigInteger = null;
        try {
            byte dataByte[] = data.getBytes();
            MessageDigest messageDigest = MessageDigest.getInstance(KEY_SHA);
            messageDigest.update(dataByte);
            bigInteger = new BigInteger(messageDigest.digest());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return bigInteger.toString();
    }
}
