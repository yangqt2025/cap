package com.yupi.springbootinit.utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Component
public class RSAUtils {

    private static final String PRIVATE_KEY ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0Feps+Vz0W16sofOvA2U" +
            "42FGkeMscxa9Lx3qQ5dgoXsqkFo5lGxFU/VALmRCsVmcERZg7wfgYMOTVpGL1S2u" +
            "AscWKI6zP9yPIVJ4tk+saNT0YhlmEDiTw88v2tcJYF1LWoOv/HyIWIC/rNBpgqvh" +
            "qBFeUq1wptiQ1HvAYhP1jOy3N1111L00SiqhE92L+PVZWjCZMeueWoEZ5blf1G5p" +
            "uwQT28JA8iCacOFNyj7ovxR1C76bGBqdq8aLMOFBNJyXSDqjMhXZlPu7MVPXhKf2" +
            "bwxOdpg2A5Jkr6ITakV/s2Z8eNBRt+FGc/WaDoaWpt55fiAWRNB8rdFiVU1aO2ak" +
            "7wIDAQAB";
    private static final String PUBLIC_KEY =
            "      MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0Feps+Vz0W16sofOvA2U\n" +
            "      42FGkeMscxa9Lx3qQ5dgoXsqkFo5lGxFU/VALmRCsVmcERZg7wfgYMOTVpGL1S2u\n" +
            "      AscWKI6zP9yPIVJ4tk+saNT0YhlmEDiTw88v2tcJYF1LWoOv/HyIWIC/rNBpgqvh\n" +
            "      qBFeUq1wptiQ1HvAYhP1jOy3N1111L00SiqhE92L+PVZWjCZMeueWoEZ5blf1G5p\n" +
            "      uwQT28JA8iCacOFNyj7ovxR1C76bGBqdq8aLMOFBNJyXSDqjMhXZlPu7MVPXhKf2\n" +
            "      bwxOdpg2A5Jkr6ITakV/s2Z8eNBRt+FGc/WaDoaWpt55fiAWRNB8rdFiVU1aO2ak\n" +
            "      7wIDAQAB\n";

    /**
     * 获取公钥
     */
    public String getPublicKey() {
        return PUBLIC_KEY;
    }

    /**
     * 解密
     */
    public String decrypt(String encryptedData) {
        try {
            // 将Base64编码的私钥转换为PrivateKey对象
            byte[] keyBytes = Base64.decodeBase64(PRIVATE_KEY);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyFactory.generatePrivate(keySpec);

            // 解密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.decodeBase64(encryptedData));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("RSA解密失败", e);
        }
    }

    /**
     * 加密
     */
    public static String encrypt(String data) {
        try {
            // 将Base64编码的公钥转换为PublicKey对象
            byte[] keyBytes = Base64.decodeBase64(PUBLIC_KEY);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            // 加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.encodeBase64String(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("RSA加密失败", e);
        }
    }
} 