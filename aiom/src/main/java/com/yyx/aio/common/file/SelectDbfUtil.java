package com.yyx.aio.common.file;

import java.sql.*;
import java.io.IOException;//数据库连接类：


public class SelectDbfUtil {
    Connection con = null;
    String dataBaseUrl = null;
    public SelectDbfUtil(String dataBaseUrl) throws SQLException {
        this.dataBaseUrl=dataBaseUrl;
        getConnection();
    }
    public Connection getConnection() throws SQLException {
        try {
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

        SelectDbfUtil cont = null;
        try {
            cont = new SelectDbfUtil("C:\\PointSoft\\EOD\\20180208");
            con = cont.getConnection();

            String sql = "select a.*,b.real_income, b.end_time  from (SELECT NUMBER,sum(Qty*OPRICE) as receivable,max([DATE]) as Saledate,min([TIME]) as start_time FROM CTI.dbf group by NUMBER) a inner join (SELECT NUMBER,sum(AMOUNT) as real_income,max([TIME]) as end_time FROM CTP.dbf where not isnull(AMOUNT) AND PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES) group by NUMBER) b on a.NUMBER=b.NUMBER";
            sql = "SELECT [number] as serial,sum(Qty*OPRICE) as receivable,max([date]) as Saledate,min([time]) as start_time FROM CTI.dbf group by number";
            /*sql = "select a.*,b.real_income, b.end_time  from (SELECT NUMBER,sum(Qty*OPRICE) as receivable,max([DATE]) as Saledate,min([TIME]) as " +
                    "start_time FROM CTI.dbf group by NUMBER) a inner join (SELECT NUMBER,sum(AMOUNT) as real_income,max([TIME]) as end_time " +
                    "FROM CTP.dbf where not isnull(AMOUNT) AND PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES) group by NUMBER) b on a.NUMBER=b.NUMBER ";*/
        System.err.print("sql=>" + sql);
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