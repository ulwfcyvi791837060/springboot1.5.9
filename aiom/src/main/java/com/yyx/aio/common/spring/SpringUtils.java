package com.yyx.aio.common.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/*
 * 
 * 
 * 
 * @author yangzhi
 * @time 2016年2月18日上午11:19:37
 * @email zhi19861117@126.com
 * @version 1.0
 * @类介绍 spring 工具类
 */
@Component
public class SpringUtils implements ApplicationContextAware {
	private static ApplicationContext applicationContext;// 声明一个静态变量保存

	@SuppressWarnings("static-access")
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/*
	 * 获取 bean
	 * 
	 * @param name
	 * @param requiredType
	 * @return
	 */
	public static <T> T getBean(String name, Class<T> requiredType) {
		return applicationContext.getBean(name, requiredType);
	}

}
