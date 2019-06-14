package com.yyx.aio.common.excepetion;

/**
 * 
 * 
 * 
 * @author yangzhi
 * @time 2016年4月6日上午11:07:06
 * @email zhi19861117@126.com
 * @version 1.0
 * @类介绍 用户
 */
public enum AppWebApiExceptionEnum {

	user_token_error(3000, "用户token超时或不存在")
	,request_body_is_empty(3001, "请求数据为空")
	,aes_decode_error(3002, "AES解密错误")
	,need_to_log_in(3003, "登录过期，请重新登录")
	
	
	;

	public Integer code;
	public String msg;

	private AppWebApiExceptionEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

}
