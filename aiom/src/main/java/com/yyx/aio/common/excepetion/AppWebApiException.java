package com.yyx.aio.common.excepetion;

/**
 * 
 * 
 * 
 * @author yangzhi
 * @time 2016年4月7日下午1:30:21
 * @email zhi19861117@126.com
 * @version 1.0
 * @类介绍 app web api 异常处理类
 */
@SuppressWarnings("serial")
public class AppWebApiException extends RuntimeException {

	public Integer code;
	public String msg;

	public AppWebApiException(Integer code, String msg) {
		super(msg);
		this.code = code;
		this.msg = msg;
	}
}
