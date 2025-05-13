package com.yupi.springbootinit.utils;

import org.apache.commons.codec.binary.Base64;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RSAKeyGenerator {
    public static void main(String[] args) {
        try {
            // 生成密钥对
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            
            // 获取公钥和私钥
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            
            // 转换为Base64字符串
            String publicKeyStr = Base64.encodeBase64String(publicKey.getEncoded());
            String privateKeyStr = Base64.encodeBase64String(privateKey.getEncoded());
            
            // 打印密钥
            System.out.println("Public Key:");
            System.out.println(publicKeyStr);
            System.out.println("\nPrivate Key:");
            System.out.println(privateKeyStr);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 