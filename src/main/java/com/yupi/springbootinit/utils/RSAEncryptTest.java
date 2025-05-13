package com.yupi.springbootinit.utils;

import java.util.Base64;
import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class RSAEncryptTest {
    private static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0Feps+Vz0W16sofOvA2U" +
            "42FGkeMscxa9Lx3qQ5dgoXsqkFo5lGxFU/VALmRCsVmcERZg7wfgYMOTVpGL1S2u" +
            "AscWKI6zP9yPIVJ4tk+saNT0YhlmEDiTw88v2tcJYF1LWoOv/HyIWIC/rNBpgqvh" +
            "qBFeUq1wptiQ1HvAYhP1jOy3N1111L00SiqhE92L+PVZWjCZMeueWoEZ5blf1G5p" +
            "uwQT28JA8iCacOFNyj7ovxR1C76bGBqdq8aLMOFBNJyXSDqjMhXZlPu7MVPXhKf2" +
            "bwxOdpg2A5Jkr6ITakV/s2Z8eNBRt+FGc/WaDoaWpt55fiAWRNB8rdFiVU1aO2ak" +
            "7wIDAQAB";

    public static String encrypt(String content) {
        try {
            // 将Base64编码的公钥转换为PublicKey对象
            byte[] keyBytes = Base64.getDecoder().decode(PUBLIC_KEY);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            // 创建Cipher对象并初始化为加密模式
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            // 加密数据
            byte[] encryptedBytes = cipher.doFinal(content.getBytes());

            // 将加密后的数据转换为Base64编码
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        // 测试数据
        String userName = "test_user";
        String userPassword = "123456";
        String phoneNumber = "13800138000";

        // 只加密敏感信息（密码和手机号）
        String encryptedPassword = encrypt(userPassword);
        String encryptedPhone = encrypt(phoneNumber);

        System.out.println("注册请求示例:");
        System.out.println("{\n" +
                "    \"userName\": \"" + userName + "\",\n" +
                "    \"userPassword\": \"" + encryptedPassword + "\",\n" +
                "    \"phoneNumber\": \"" + encryptedPhone + "\"\n" +
                "}");

        // 登录请求示例
        System.out.println("\n登录请求示例:");
        System.out.println("{\n" +
                "    \"userName\": \"" + userName + "\",\n" +
                "    \"userPassword\": \"" + encryptedPassword + "\"\n" +
                "}");
    }
} 