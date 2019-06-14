package com.yyx.aio.common.entity;

import com.yyx.aio.common.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/*
 * 对实体类的操作
 * @author Yangkai
 * @create 2017-05-15-12:10
 */
public class EntityUtil {
    public static Map getMap(Object model) {
        Map map = new HashMap();
        Field[] field = model.getClass().getDeclaredFields();        //获取实体类的所有属性，返回Field数组
        for (int j = 0; j < field.length; j++) {     //遍历所有属性
            String name = field[j].getName();    //获取属性的名字
            String methName = name.substring(0, 1).toUpperCase() + name.substring(1); //将属性的首字符大写，方便构造get，set方法
            /*String type = field[j].getGenericType().toString();    //获取属性的类型
            if (type.equals("class java.lang.String")) {   //如果type是类类型，则前面包含"class "，后面跟类名*/
            Method m = null;
            String value = null;    //调用getter方法获取属性值
            try {
                m = model.getClass().getMethod("get" + methName);
                value = String.valueOf(m.invoke(model));
                System.out.print(m.invoke(model));
                if (null != value && StringUtil.isNotEmpty(value) && "null" != value) {
                    map.put(name,value);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
