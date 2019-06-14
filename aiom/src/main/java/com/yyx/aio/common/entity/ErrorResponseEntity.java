package com.yyx.aio.common.entity;

import com.yyx.aio.common.ConstantsErrorMsg;

/*
 * 
 * 
 * 
 * @author yangzhi
 * @time 2016年1月19日下午12:25:17
 * @email zhi19861117@126.com
 * @version 1.0
 * @类介绍 返回错误的实体
 */
@SuppressWarnings("serial")
public class ErrorResponseEntity extends ResponseEntity {
	
	public ErrorResponseEntity(){
		super(1000, ConstantsErrorMsg.system_error_msg);
	}
	public ErrorResponseEntity(String msg){
		super(0, msg);
	}
	public ErrorResponseEntity(Integer code,String msg){
		super(code, msg);
	}

}
