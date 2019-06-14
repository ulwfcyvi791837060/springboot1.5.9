package com.yyx.aio.task.util;

/**
 * @author: zhk
 * @Date :          2019/6/5 18:55
 */

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;


public class Base64Utils {

    public static byte[] decode(String base64) {
        return Base64.decodeBase64(base64.getBytes(Charset.forName("UTF-8")));
    }

    public static String encode(byte[] bytes) {
        return new String(Base64.encodeBase64(bytes), Charset.forName("UTF-8"));
    }
}
