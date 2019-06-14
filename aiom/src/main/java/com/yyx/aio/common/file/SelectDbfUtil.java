package com.yyx.aio.common.file;

import java.sql.*;
import java.io.IOException;//数据库连接类：


public class SelectDbfUtil {
    Connection con = null;
    public SelectDbfUtil() throws SQLException {
        getConnection();
    }
    public Connection getConnection() throws SQLException {
        try {
            String dataBaseUrl = "C:\\Users\\Administrator\\Desktop\\EOD\\20180208";
            String strurl = "jdbc:odbc:Driver={Microsoft Visual FoxPro Driver};SourceType=DBF;SourceDB="
                    + dataBaseUrl + ";Exclusive=No;";
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            con = DriverManager.getConnection(strurl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }


    //测试代码：
    public static void main(String[] args) throws SQLException, IOException {

        Statement st = null;
        ResultSet rs = null;
        Connection con =null;

        System.out.println("输出：");
        SelectDbfUtil cont = null;
        try {
            cont = new SelectDbfUtil();
            con = cont.getConnection();

            String sql = "SELECT  sum(Qty*OPRICE) as sale_AMOUNT FROM CTI.dbf";
            //System.err.print("结果=>" + sql);
            st = con.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                String str = "sale_AMOUNT:" + rs.getString("sale_AMOUNT");
                System.out.println(str);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}