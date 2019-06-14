package com.yyx.aio.task.util;

/**
 * @author: zhk
 * @Date :          2019/6/5 18:53
 */
import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * <b>功能：</b>DES工具类<br>
 * <b>Copyright TCSL</b>
 */
public class DESUtil{

    /**
     * 禁止实例化，形成单例类静态方法集
     */
    private DESUtil() {
    }

    /**
     * <b>功能描述：</b>对字符串进行DES加密<br>
     * <b>修订记录：</b><br>
     * <li>20140313&nbsp;&nbsp;|&nbsp;&nbsp;扈健成&nbsp;&nbsp;|&nbsp;&nbsp;创建方法</li>
     * @param source 要加密的字符串
     * @param keyStr 密钥字符串, 为null使用该类默认key
     * @return 加密的二进制数组
     */
    public static byte[] encrypt(String source, String keyStr){
        try {
            byte[] sourceBytes = source.getBytes("UTF-8");

            //获取秘钥
            Key key = getKey(keyStr);

            //指定DES对象
            Cipher cipher = Cipher.getInstance("DES");

            //通过密钥进行初始化(加密模式)
            cipher.init(Cipher.ENCRYPT_MODE, key);

            //执行操作
            return cipher.doFinal(sourceBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);//如果未提供加密算法会抛此异常
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);//当请求特定填充机制但该环境中未提供时，抛出此异常
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);//不支持字符编码
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);//用于无效 Key（无效的编码、错误的长度、未初始化等）的异常
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);//如果提供给块密码的数据长度不正确（即与密码的块大小不匹配），则抛出此异常
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);//当输入数据期望特定的填充机制而数据又未正确填充时，抛出此异常
        }
    }

    /**
     * {@link #encrypt(String, String)}<br>
     * @see #getKey()
     */
    public static byte[] encrypt(String source){
        return encrypt(source, null);
    }

    /**
     * <b>功能描述：</b>对DES加密的二进制数组进行解密<br>
     * <b>修订记录：</b><br>
     * <li>20140313&nbsp;&nbsp;|&nbsp;&nbsp;扈健成&nbsp;&nbsp;|&nbsp;&nbsp;创建方法</li>
     * @param source 要解密的数据
     * @param keyStr 密钥字符串, 为null使用该类默认key
     * @return 解密后的字符串
     */
    public static String decrypt(byte[] source, String keyStr){
        try {
            //获取秘钥
            Key key = getKey(keyStr);

            //指定DES对象
            Cipher cipher = Cipher.getInstance("DES");

            //通过密钥进行初始化(解密模式)
            cipher.init(Cipher.DECRYPT_MODE, key);

            //执行操作
            byte[] dissect = cipher.doFinal(source);
            return new String(dissect);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);//如果未提供加密算法会抛此异常
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);//当请求特定填充机制但该环境中未提供时，抛出此异常
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);//用于无效 Key（无效的编码、错误的长度、未初始化等）的异常
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);//如果提供给块密码的数据长度不正确（即与密码的块大小不匹配），则抛出此异常
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);//当输入数据期望特定的填充机制而数据又未正确填充时，抛出此异常
        }

    }

    /**
     * {@link #decrypt(byte[], String)}<br>
     * @see #getKey()
     */
    public static String decrypt(byte[] source){
        return decrypt(source, (String) null);
    }

    /**
     * <b>功能描述：</b>根据固定Base64编码获取秘钥，加密使用<br>
     * <b>修订记录：</b><br>
     * <li>20140313&nbsp;&nbsp;|&nbsp;&nbsp;扈健成&nbsp;&nbsp;|&nbsp;&nbsp;创建方法</li>
     * @param keyStr key字符串, 为null使用固定字符串
     * @return 秘钥
     */
    private static Key getKey(String keyStr){

        if( keyStr==null ) {
            return getKey();
        }

        try {
            //通过固定Base64编码获取二进制数组
            byte[] bs = Base64Utils.decode(keyStr);

            //声明Key的规范(DES)
            KeySpec dks = new DESKeySpec(bs);

            //声明秘钥工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

            //获取秘钥
            return keyFactory.generateSecret(dks);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);//用于无效 Key（无效的编码、错误的长度、未初始化等）的异常
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);//如果未提供加密算法会抛此异常
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);//无效密钥规范的异常
        }
    }

    /**
     * 使用特殊字符串当key
     * {@link #getKey(String)}
     */
    private static Key getKey(){
        return getKey("s7Oh75I+1ew=");
    }

    /* ******************main*******************/
    public static void main (String [] args)throws Exception{
        //加密后使用base64编码
        System.out.println(Base64Utils.encode(DESUtil.encrypt("1111","0464d5fd152a49b0")));

//使用base64解码后解密
        System.out.println(DESUtil.decrypt(Base64Utils.decode("9qtXq3yKItA="),"0464d5fd152a49b0"));

    }
}

