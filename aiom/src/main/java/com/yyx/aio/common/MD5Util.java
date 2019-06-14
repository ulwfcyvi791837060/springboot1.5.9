package com.yyx.aio.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    private static final String SALT = "aiom";

	/*
	 * 默认的密码字符串组合，apache校验下载的文件的正确性用的就是默认的这个组合
	 */
	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };
	protected static MessageDigest messagedigest = null;
	static {
		try {
			messagedigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException nsaex) {
			System.err.println(MD5Util.class.getName() + "初始化失败，MessageDigest不支持MD5Util。");
			nsaex.printStackTrace();
		}
	}

	/*
	 * 向getMD5方法传入一个你需要转换的原始字符串，将返回字符串的MD5码
	 * 
	 * @param code
	 *            原始字符串
	 * @return 返回字符串的MD5码
	 */
	public static String getMD5(String code) throws Exception {
		try {
			byte[] strTemp = code.getBytes();
			// 使用MD5创建MessageDigest对象
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte b = md[i];
				// System.out.println((int)b);
				// 将没个数(int)b进行双字节加密
				str[k++] = hexDigits[b >> 4 & 0xf];
				str[k++] = hexDigits[b & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * 适用于上G大的文件
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static String getFileMD5String(File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		FileChannel ch = in.getChannel();
		MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
		messagedigest.update(byteBuffer);
		return bufferToHex(messagedigest.digest());
	}

	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits[(bt & 0xf0) >> 4];
		char c1 = hexDigits[bt & 0xf];
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}

	public static boolean checkPassword(String password, String md5PwdStr) {
		String s = getMD5String(password);
		return s.equals(md5PwdStr);
	}

	public static String getMD5String(String s) {
		return getMD5String(s.getBytes());
	}

	public static String getMD5String(byte[] bytes) {
		messagedigest.update(bytes);
		return bufferToHex(messagedigest.digest());
	}

	/* */
	/*
	 * main方法用于测试
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 字符'a'的MD5码是0cc175b9c0f1b6a831c399e269772661,如果是，则成功。
		try {
			System.out.println(MD5Util.getMD5("a"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final static String get32MD5(String plainText) {
		// 返回字符串
		String md5Str = null;
		try {
			// 操作字符串
			StringBuffer buf = new StringBuffer();

			MessageDigest md = MessageDigest.getInstance("MD5");

			// 添加要进行计算摘要的信息,使用 plainText 的 byte 数组更新摘要。
			md.update(plainText.getBytes());
			// 计算出摘要,完成哈希计算。
			byte b[] = md.digest();
			int i;
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				// 将整型 十进制 i 转换为16位，用十六进制参数表示的无符号整数值的字符串表示形式。
				buf.append(Integer.toHexString(i));
			}
			// 32位的加密
			md5Str = buf.toString();
			// 16位的加密
			// md5Str = buf.toString().md5Strstring(8,24);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return md5Str;
	}
	public final static String get16MD5(String plainText) {
		// 返回字符串
		String md5Str = null;
		try {
			// 操作字符串
			StringBuffer buf = new StringBuffer();
			
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			// 添加要进行计算摘要的信息,使用 plainText 的 byte 数组更新摘要。
			md.update(plainText.getBytes());
			// 计算出摘要,完成哈希计算。
			byte b[] = md.digest();
			int i;
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				// 将整型 十进制 i 转换为16位，用十六进制参数表示的无符号整数值的字符串表示形式。
				buf.append(Integer.toHexString(i));
			}
			// 32位的加密
			//md5Str = buf.toString();
			// 16位的加密
			 md5Str = buf.toString().substring(8,24).toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return md5Str;
	}

	public static String encode(String password) {
		password = password + SALT;
		return processEncode(password);
	}

    public static String processEncode(String password) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        char[] charArray = password.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }

            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

}
