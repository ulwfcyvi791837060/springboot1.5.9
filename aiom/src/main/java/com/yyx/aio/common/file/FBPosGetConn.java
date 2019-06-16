package com.yyx.aio.common.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;

public class FBPosGetConn {
    private Logger logger = LoggerFactory.getLogger(getClass());
    //jdbc连接需要用到的三个参数

    //创建一个对象 设置为静态私有  （单例模式）
    private static FBPosGetConn gc=null;

    //将构造函数设为private型 防止外部实例化对象  （通过反射或反序列化可以破解单例）
    private FBPosGetConn(){};

    //静态代码块，在类加载进内存时就完成对对象的特殊的初始化（这个动作发生在类的构造器执行之前，也就是在没有对象存在的情况下，静态代码就已经完成了对对象的特殊的处理 ），此处的作用是，当jdbc的驱动器加载时，就自动创建一个自己。
    static{
        try {
            //注册驱动
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    //判断GetConn实例化的对象是否存在  不存在就实例化一个
    //synchronized  线程锁  防止多个线程抢占资源   防止死锁
    public static FBPosGetConn getGc(){
        if(gc==null){
            synchronized (FBPosGetConn.class) {
                if(gc==null){
                    gc=new FBPosGetConn();
                }
            }
        }
        return gc;
    }

    //返回一个Connection连接
    public Connection getCon(String url) throws SQLException{
        String strUrl = "jdbc:odbc:Driver={Microsoft Visual FoxPro Driver};SourceType=DBF;SourceDB="
                + url + ";Exclusive=No;";
        logger.info("conn==>"+url);
        return DriverManager.getConnection(strUrl);
    }

    //释放资源
    public void closeAll(ResultSet rs, Statement st, Connection con){
        if(rs!=null){
            try {
                rs.close();
            } catch (SQLException e) {
                logger.info(e.getMessage());
                e.printStackTrace();
            }finally{
                if(st!=null){
                    try {
                        st.close();
                    } catch (SQLException e) {
                        logger.info(e.getMessage());
                        e.printStackTrace();
                    }finally{
                        if(con!=null){
                            try {
                                con.close();
                            } catch (SQLException e) {
                                logger.info(e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        if(st!=null){
            try {
                st.close();
            } catch (SQLException e) {
                logger.info(e.getMessage());
                e.printStackTrace();
            }finally{
                if(con!=null){
                    try {
                        con.close();
                    } catch (SQLException e) {
                        logger.info(e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}


