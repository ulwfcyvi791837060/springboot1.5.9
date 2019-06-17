package com.yyx.aio.task.util;

/**
 * @author: zhk
 * @Date :          2019/6/10 11:37
 */
public class test {

    public static void main(String[] args) throws Exception {
        String date="20180208";
        String y = date.substring(0, 4);
        String m = date.substring(4, 6);
        String d = date.substring(6,8 );
        String day =y+"-"+m+"-"+d;
        System.out.println(day);
    }
}
