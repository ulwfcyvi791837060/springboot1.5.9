package com.yyx.aio.common;

import org.apache.log4j.Logger;

import java.util.Random;

public class RandomUtil {
	private static Logger logger =Logger.getLogger(RandomUtil.class);
	
	/*
	 * 获取随机验证码
	 */
	private static char[] codeSequence = {  '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

	public static String getRandomString(Integer integer) {
		String sRand = "";
		Random random = new Random();

		for (int i = 0; i < integer; i++) {
			String _tmp = String.valueOf(codeSequence[random.nextInt(10)]);
			sRand += _tmp;
		}
		logger.info("随机验证码：" + sRand);
		return sRand;
	}
}
