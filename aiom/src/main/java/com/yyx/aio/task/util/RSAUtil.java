package com.yyx.aio.task.util;

import org.assertj.core.util.Strings;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

/**
 * Created by Administrator on 2018/7/25.
 *
 * @ClassName:
 * @Description:
 * @Author: 任永奇
 * @Date: 2018-07-25 17:07
 */

public class RSAUtil {
    /**
     * 生成RAS公钥与私钥字符串，直接返回
     *
     * @return
     */
    public static HashMap<String, String> getKeys() {
        HashMap<String, String> map = new HashMap<String, String>();
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到公钥字符串
        String publicKey = Base64Utils.encode(keyPair.getPublic().getEncoded());
        // 得到私钥字符串
        String privateKey = Base64Utils.encode(keyPair.getPrivate().getEncoded());
        map.put("publicKey", publicKey);
        map.put("privateKey", privateKey);
        return map;
    }

    /**
     * 根据公钥字符串加载公钥
     *
     * @param publicKeyStr 公钥字符串
     * @return
     * @throws Exception
     */
    public static RSAPublicKey loadPublicKey(String publicKeyStr) throws Exception {
        try {
            byte[] buffer = javax.xml.bind.DatatypeConverter.parseBase64Binary(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法", e);
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法", e);
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空", e);
        }
    }

    /**
     * 根据私钥字符串加载私钥
     *
     * @param privateKeyStr 私钥字符串
     * @return
     * @throws Exception
     */
    public static RSAPrivateKey loadPrivateKey(String privateKeyStr) throws Exception {
        try {
            byte[] buffer = javax.xml.bind.DatatypeConverter.parseBase64Binary(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法", e);
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法", e);
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空", e);
        }
    }

    /**
     * 公钥加密
     *
     * @param publicKey     公钥
     * @param plainTextData 明文数据
     * @return
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception {
        if (publicKey == null) {
            throw new Exception("加密公钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            // 使用默认RSA
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(plainTextData);
            return Base64Utils.encode(output);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("加密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏");
        }
    }


    /**
     * 私钥解密
     *
     * @param privateKeyStr 私钥字符串
     * @param cipherData 密文数据
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String privateKeyStr, byte[] cipherData) throws Exception {
        if (Strings.isNullOrEmpty(privateKeyStr)) {
            throw new Exception("解密私钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            byte[] buffer = Base64Utils.decode(privateKeyStr);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPrivateKey privateKey =  (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
            // 使用默认RSA
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] output = cipher.doFinal(cipherData);
            return new String(output);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return "";
        } catch (InvalidKeyException e) {
            throw new Exception("解密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            e.printStackTrace();
            throw new Exception("密文数据已损坏");
        }
    }


    public static void main(String[] args) throws Exception {
        String publicKeyStr="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCvBe6tl15cNgBMhWgeEKlp97JJonySqcfQZMsHkVPfk2JQga7jDw2fPs1+m71uFl9TAZsOuwQRWZXNGHdob2kuCJYVsBaK5qebRPn44KEVzd8atJyOkSl4sJpVLDZbTktd2OI7I4UL9gK4fdkGxv1N9zgmIxihMU8RuShTZrFUmwIDAQAB";
        System.out.println("初始化公为："+publicKeyStr);

        String originData="1111";
        System.out.println("信息原文："+originData);
        String encryptData=encrypt(loadPublicKey(publicKeyStr),originData.getBytes());
        System.out.println("加密后："+encryptData);
    }
}

