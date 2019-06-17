package com.yyx.aio.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.yyx.aio.common.file.EodGetConn;
import com.yyx.aio.common.file.FBPosGetConn;
import com.yyx.aio.entity.*;
import com.yyx.aio.mapper.UserMapper;
import com.yyx.aio.service.UserService;
import com.yyx.aio.task.util.DESUtil;
import com.yyx.aio.task.util.RSAUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 方法
 * @Author zenghuikang
 * @Description 
 * @Date 2019/6/14 18:06 
 * @return
 * @throws 
 **/
@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired(required = false)
    private UserMapper userMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${dbf.store.location_id}")
    private String locationId;

    @Value("${dbf.store.store_id}")
    private String storeId;

    @Value("${dbf.store.store_name}")
    private String storeName;

    @Value("${dbf.store.urlStr}")
    private String urlStr;

    @Value("${dbf.store.url}")
    private String url;

    @Value("${dbf.store.desKey}")
    private String desKey ;

    @Value("${dbf.store.publicKeyStr}")
    private String publicKeyStr;

    @Value("${dbf.store.corporationCode}")
    private String corporationCode;

    @Value("${dbf.store.EodDataBaseUrl}")
    private String eodDataBaseUrl;

    @Value("${dbf.store.FBPosDataBaseUrl}")
    private String fBPosDataBaseUrl;


    @Override
    public User getByLoginName(String loginName) {
        try {
            Map map = new HashMap();
            map.put("loginName",loginName);
            logger.info("根据用户名获取用户！");
            return userMapper.getByUserName(map);
        } catch (Exception e){
            logger.error(getClass().getName() + "error");
            return null;
        }
    }

    @Override
    public Result uploadAction(String date, boolean auto) {
        Result result = new Result();
        logger.info("【job2】开始执行：{}", DateUtil.formatDateTime(new Date()));
        if(date!=null&&date.trim().length()!=8){
            result.setSuccess(false);
            result.setMsg("输入的日期格式不对");
            return result;
        }
        String y = date.substring(0, 4);
        String m = date.substring(4, 6);
        String d = date.substring(6,8 );
        String day =y+"-"+m+"-"+d;
        String dirFile=eodDataBaseUrl+"\\"+date;
        logger.info("输出：");
        try {

            boolean b = dirExists(new File(dirFile));

            if(b){
                //自动上传 表一，只上传一次，最好从【清机后】的数据库中取数据（如果存在这个数据库目录，才上传）
                //表1是一天传一次完整的，
                //自动上传，表一是从 【清机后】的数据库中取数据
                //自动上传 表二到表五 是从 营业中 数据库中取数据
                logger.info("正在上传==>"+dirFile);

                boolean b1 = processSummary(eodDataBaseUrl+date, day);
                if(!b1){
                    logger.info("summary上传失败");
                    result.setSuccess(false);
                    result.setMsg(result.getMsg()+","+"summary上传失败");
                    //result =b1;
                    //return result;
                }else{
                    logger.info("summary上传成功");
                    result.setMsg(result.getMsg()+","+"summary上传成功");
                }
            }else{
                logger.info(dirFile+"不存在");
                result.setMsg(result.getMsg()+","+dirFile+"不存在");
                //result =false;
                //return result;
            }

            if(auto){
                //没清机的有20180207这种形式的目录吗
                //dirFile=fBPosDataBaseUrl+"\\"+date;
                dirFile=fBPosDataBaseUrl;
            }

            //表2-5是按流水传，3-5分钟传一次即可
            //手动传都传的清机后的吗? 是的

            boolean fb = dirExists(new File(dirFile));

            if(fb){
                //自动上传 表一，只上传一次，最好从【清机后】的数据库中取数据（如果存在这个数据库目录，才上传）
                //表1是一天传一次完整的，
                //自动上传，表一是从 【清机后】的数据库中取数据
                //自动上传 表二到表五 是从 营业中 数据库中取数据
                logger.info("正在上传==>"+dirFile);
                String con =null;
                if(auto){
                    con = fBPosDataBaseUrl;
                }else{
                    con = eodDataBaseUrl+date;
                }

                boolean b2 = processBusiness(con, day,date);
                if(!b2){
                    logger.info("Business上传失败");
                    result.setSuccess(false);
                    result.setMsg(result.getMsg()+","+"Business上传失败");
                    //result =b1;
                    //return result;
                }else{
                    logger.info("Business上传成功");
                    result.setMsg(result.getMsg()+","+"Business上传成功");
                }
                logger.info("正在上传==>"+dirFile);
                if(auto){
                    con = fBPosDataBaseUrl;
                }else{
                    con = eodDataBaseUrl+date;
                }
                boolean b1 = processBillDetail(con, day,date);
                if(!b1){
                    logger.info("BillDetail上传失败");
                    result.setSuccess(false);
                    result.setMsg(result.getMsg()+","+"BillDetail上传失败");
                    //result =b1;
                    //return result;
                }else{
                    logger.info("BillDetail上传成功");
                    result.setMsg(result.getMsg()+","+"BillDetail上传成功");
                }
                logger.info("正在上传==>"+dirFile);
                if(auto){
                    con = fBPosDataBaseUrl;
                }else{
                    con =eodDataBaseUrl+date;
                }
                boolean b3 = processPaytypeDetail(con, day,date);
                if(!b3){
                    logger.info("PaytypeDetail上传失败");
                    result.setSuccess(false);
                    result.setMsg(result.getMsg()+","+"PaytypeDetail上传失败");
                    //result =b1;
                    //return result;
                }else{
                    logger.info("PaytypeDetail上传成功");
                    result.setMsg(result.getMsg()+","+"PaytypeDetail上传成功");
                }
                logger.info("正在上传==>"+dirFile);
                if(auto){
                    con = fBPosDataBaseUrl;
                }else{
                    con =eodDataBaseUrl+date;
                }
                boolean b4 = processDiscountDetail(con, day,date);
                if(!b4){
                    logger.info("DiscountDetail上传失败");
                    result.setSuccess(false);
                    result.setMsg(result.getMsg()+","+"DiscountDetail上传失败");
                    //result =b1;
                    //return result;
                }else{
                    logger.info("DiscountDetail上传成功");
                    result.setMsg(result.getMsg()+","+"DiscountDetail上传成功");
                }

            }else{
                logger.info(dirFile+"不存在");
                result.setMsg(result.getMsg()+","+dirFile+"不存在");
                //result =false;
                //return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 1.Summary 日销售汇总表
     * @Author zenghuikang
     * @Description
     * @Date 2019/6/15 11:07
      * @param url
     * @param day
     * @return boolean
     * @throws
     **/
    private boolean processSummary(String url,String day ) {
        String sql = "SELECT  sum(AMOUNT) as net_AMOUNT FROM CTP.dbf where not isnull(AMOUNT) AND (PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES))";
        logger.info("sql=>" + sql);

        Connection con = null;
        try {
            con = EodGetConn.getGc().getCon(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Statement st = null;
        ResultSet rs = null;
        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);

            Summary summary = new Summary();
            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 创建Date对象，表示当前时间
            Date now = new Date();
            // 调用format()方法，将日期转换为字符串并输出
            while (rs.next()) {

                String sRealIncome = rs.getString("net_AMOUNT");
                logger.info("net_AMOUNT:" + sRealIncome);

                summary.setLocationId("");
                summary.setStoreId("");
                summary.setStoreName("");
                summary.setbDate("");
                summary.setsReceivable(0.0D);
                summary.setsRealIncome(Double.parseDouble(sRealIncome));
                summary.setsBillNum(0.0D);
                summary.setsDiscountTotal(0.0D);
                summary.setsDiscountNum(0.0D);
                summary.setsChargeback(0.0D);
                summary.setsChargebackNum(0.0D);
                summary.setsTime(sdf3.format(now));
                summary.setsRefreshTime(sdf3.format(now));

                String param="";
                param = "{\n" +
                        "    \"columnNames\": [\n" +
                        "        \"location_id\",\n" +  //商位
                        "        \"store_id\",\n" +     //主键 1
                        "        \"store_name\",\n" +   //门店
                        "        \"b_date\",\n" + //主键 2 营业日 2017-01-21
                        "        \"s_receivable\",\n" +
                        "        \"s_real_income\",\n" +
                        "        \"s_bill_num\",\n" +
                        "        \"s_discount_total\",\n" +
                        "        \"s_discount_num\",\n" +
                        "        \"s_chargeback\",\n" +
                        "        \"s_chargeback_num\",\n" +
                        "        \"s_time\",\n" +
                        "        \"s_refresh_time\"\n" +
                        "    ],\n" +
                        "    \"keyCol\": \"store_id,b_date\",\n" +
                        "    \"records\": [\n" +
                        "        [\n" +
                        "            \""+locationId+"\",\n" +
                        "            \""+storeId+"\",\n" +
                        "            \""+storeName+"\",\n" +
                        "            \""+day+" 00:00:00\",\n" +
                        "            \""+summary.getsReceivable()+"\",\n" +
                        "            \""+summary.getsRealIncome()+"\",\n" +
                        "            \""+summary.getsBillNum()+"\",\n" +
                        "            \""+summary.getsDiscountTotal()+"\",\n" +
                        "            \""+summary.getsDiscountNum()+"\",\n" +
                        "            \""+summary.getsChargeback()+"\",\n" +
                        "            \""+summary.getsChargebackNum()+"\",\n" +
                        "            \""+summary.getsTime()+"\",\n" +
                        "            \""+summary.getsRefreshTime()+"\"\n" +
                        "        ]\n" +
                        "    ],\n" +
                        "    \"tableName\": \"Summary\"\n" +
                        "}";
                return apiDataStr(param);
            }
        }catch (SQLException ex) {
            ///错误处理
            logger.info(ex.getMessage());
        }finally{
            EodGetConn.getGc().closeAll(rs,st,con);
        }
        return false;
    }

    /**
     * 2.Business 营业明细表
     * @Author zenghuikang
     * @Description
     * @Date 2019/6/15 11:08
     * @param url
     * @param day
     * @return boolean
     * @throws
     **/
    private boolean processBusiness(String url,String day,String date ) {
        String sql = "SELECT NUMBER,sum(Qty*OPRICE) as receivable,max(DATE) as Saledate,min(TIME) as start_time FROM CTI.dbf group by NUMBER";

        logger.info("sql=>" + sql);
        Connection con = null;
        Connection con2 = null;
        try {
            con = EodGetConn.getGc().getCon(url);
            con2 = EodGetConn.getGc().getCon(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Statement st = null;
        ResultSet rs = null;
        Statement st2 = null;
        ResultSet rs2 = null;
        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);

            Business business = new Business();

            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 创建Date对象，表示当前时间
            Date now = new Date();
            // 调用format()方法，将日期转换为字符串并输出
            StringBuilder recordsSb = new StringBuilder();

            while (rs.next()) {
                //start_time ，end_time 只有时间，，你加上日期
                //优惠金额*=应收金额* -实际收入* receivable-real_income
                /*表三和表二，SQL是一样的，
                菜品名称 = 甜品
                折前单价 = 应收金额*
                        折后单价 = 实际收入*
                        数量 = 1
                        serial = 日期（yyyymmdd）+ NUMBER
                表四，也用表二的SQL ,
                        支付方式 = 现金
                支付金额 = 实际收入
                表五，也用表二的SQL
                优惠类型 统一写 【优惠折扣】
                优惠金额* = 应收金额*- 实际收入*
                自动上传，允许重复上传，，SQL可不用加条件，表二到表五每次可上传多条记录，，全部上传吧*/

                //NUMBER,receivable,Saledate,start_time,real_income,end_time
                String NUMBER = rs.getString("NUMBER");
                String receivable = rs.getString("receivable");
                String Saledate = rs.getString("Saledate");
                String start_time = rs.getString("start_time");

                sql="SELECT NUMBER,sum(AMOUNT) as real_income,max(TIME) as end_time FROM CTP.dbf where not isnull(AMOUNT) and " +
                        "NUMBER ="+NUMBER+" AND PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES) group by NUMBER \n";
                st2 = con2.createStatement();

                rs2 = st2.executeQuery(sql);
                String real_income="";
                String end_time="";
                while (rs2.next()) {
                    real_income = rs2.getString("real_income");
                    end_time = rs2.getString("end_time");
                }



                business.setLocation_id("");
                business.setStore_id("");
                business.setStore_name("");
                business.setB_date(Saledate);
                business.setSerial(date+NUMBER);
                business.setStart_time(Saledate+" "+start_time);
                business.setEnd_time(Saledate+" "+end_time);
                business.setReceivable(Double.parseDouble(receivable));
                business.setReal_income(Double.parseDouble(real_income));
                business.setDiscount_amount(0.0D);
                business.setIs_chargeback("");
                business.setChargeback(0.0D);
                business.setTime(sdf3.format(now));
                business.setRefresh_time(sdf3.format(now));

                if(recordsSb.length()>0){
                    recordsSb.append(",");
                }
                recordsSb.append("        [\n" +
                        "            \""+locationId+"\",\n" +
                        "            \""+storeId+"\",\n" +
                        "            \""+storeName+"\",\n" +
                        "            \""+business.getB_date()+" 00:00:00\",\n" +
                        "            \""+business.getSerial()+"\",\n" +
                        "            \""+business.getStart_time()+"\",\n" +
                        "            \""+business.getEnd_time()+"\",\n" +
                        "            \""+business.getReceivable()+"\",\n" +
                        "            \""+business.getReal_income()+"\",\n" +
                        "            \""+business.getDiscount_amount()+"\",\n" +
                        "            \""+business.getIs_chargeback()+"\",\n" +
                        "            \""+business.getChargeback()+"\",\n" +
                        "            \""+business.getTime()+"\",\n" +
                        "            \""+business.getRefresh_time()+"\"\n" +
                        "        ]\n" );

            }
            String param = "{\n" +
                    "    \"columnNames\": [\n" +
                    "        \"location_id\",\n" +
                    "        \"store_id\",\n" +
                    "        \"store_name\",\n" +
                    "        \"b_date\",\n" +
                    "        \"serial\",\n" +
                    "        \"start_time\",\n" +
                    "        \"end_time\",\n" +
                    "        \"receivable\",\n" +
                    "        \"real_income\",\n" +
                    "        \"discount_amount\",\n" +
                    "        \"is_chargeback\",\n" +
                    "        \"chargeback\",\n" +
                    "        \"time\",\n" +
                    "        \"refresh_time\"\n" +
                    "    ],\n" +
                    "    \"keyCol\": \"store_id,serial\",\n" +
                    "    \"records\": [\n"
                    +recordsSb.toString()+
                    "    ],\n" +
                    "    \"tableName\": \"Business\"\n" +
                    "}";

            return apiDataStr(param);
        }catch (SQLException ex) {
            ///错误处理
            logger.info(ex.getMessage());
        }finally{
            EodGetConn.getGc().closeAll(rs,st,con);
            EodGetConn.getGc().closeAll(rs2,st2,con2);
        }

        return false;
    }

    /**
     * 3.Bill Detail  账单销售明细表
     * @Author zenghuikang
     * @Description
     * @Date 2019/6/15 11:08
     * @param url
     * @param day
     * @return boolean
     * @throws
     **/
    private boolean processBillDetail(String url,String day, String date ) {
        String sql = "SELECT NUMBER,sum(Qty*OPRICE) as receivable,max(DATE) as Saledate,min(TIME) as start_time FROM CTI.dbf group by NUMBER";
        logger.info("sql=>" + sql);
        Connection con = null;
        Connection con2 = null;
        try {
            con = EodGetConn.getGc().getCon(url);
            con2 = EodGetConn.getGc().getCon(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Statement st = null;
        ResultSet rs = null;
        Statement st2 = null;
        ResultSet rs2 = null;
        try {
            st = con.createStatement();

            rs = st.executeQuery(sql);

            BillDetail billDetail = new BillDetail();

            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 创建Date对象，表示当前时间
            Date now = new Date();
            // 调用format()方法，将日期转换为字符串并输出
            StringBuilder recordsSb = new StringBuilder();
            while (rs.next()) {

                //NUMBER,receivable,Saledate,start_time,real_income,end_time
                String NUMBER = rs.getString("NUMBER");
                String receivable = rs.getString("receivable");
                String Saledate = rs.getString("Saledate");
                String start_time = rs.getString("start_time");

                /*表三和表二，SQL是一样的，
                菜品名称 = 甜品
                折前单价 = 应收金额*
                        折后单价 = 实际收入*
                        数量 = 1
                        serial = 日期（yyyymmdd）+ NUMBER*/

                sql="SELECT NUMBER,sum(AMOUNT) as real_income,max(TIME) as end_time FROM CTP.dbf where not isnull(AMOUNT) and " +
                        "NUMBER ="+NUMBER+" AND PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES) group by NUMBER \n";
                st2 = con2.createStatement();

                rs2 = st2.executeQuery(sql);
                String real_income="";
                String end_time="";
                while (rs2.next()) {
                    real_income = rs2.getString("real_income");
                    end_time = rs2.getString("end_time");
                }

                billDetail.setLocation_id("");
                billDetail.setStore_id("");
                billDetail.setStore_name("");
                billDetail.setB_date(Saledate);
                billDetail.setSerial(date+NUMBER);
                billDetail.setStart_time(Saledate+" "+start_time);
                billDetail.setEnd_time(Saledate+" "+end_time);
                billDetail.setItem_name("甜品");
                billDetail.setItem_category("");
                billDetail.setItem_sub_category("");
                //折前单价 = receivable
                billDetail.setOriginal_price(Double.parseDouble(receivable));
                billDetail.setActual_price(Double.parseDouble(real_income));
                billDetail.setItem_num(1D);
                billDetail.setReceivable(Double.parseDouble(receivable));
                billDetail.setReal_income(0.0D);
                billDetail.setDisc_money(0.0D);
                billDetail.setIs_chargeback("");
                billDetail.setChargeback_price(0.0D);
                billDetail.setChargeback_num(0.0D);
                billDetail.setTime(sdf3.format(now));
                billDetail.setRefresh_time(sdf3.format(now));

                if(recordsSb.length()>0){
                    recordsSb.append(",");
                }
                recordsSb.append("        [\n" +
                        "            \""+locationId+"\",\n" +
                        "            \""+storeId+"\",\n" +
                        "            \""+storeName+"\",\n" +
                        "            \""+billDetail.getB_date()+" 00:00:00\",\n" +
                        "            \""+billDetail.getSerial()+"\",\n" +
                        "            \""+billDetail.getStart_time()+"\",\n" +
                        "            \""+billDetail.getEnd_time()+"\",\n" +
                        "            \""+billDetail.getItem_name()+"\",\n" +
                        "            \""+billDetail.getItem_category()+"\",\n" +
                        "            \""+billDetail.getItem_sub_category()+"\",\n" +
                        "            \""+billDetail.getOriginal_price()+"\",\n" +
                        "            \""+billDetail.getActual_price()+"\",\n" +
                        "            \""+billDetail.getItem_num()+"\",\n" +
                        "            \""+billDetail.getReceivable()+"\",\n" +
                        "            \""+billDetail.getReal_income()+"\",\n" +
                        "            \""+billDetail.getDisc_money()+"\",\n" +
                        "            \""+billDetail.getIs_chargeback()+"\",\n" +
                        "            \""+billDetail.getChargeback_price()+"\",\n" +
                        "            \""+billDetail.getChargeback_num()+"\",\n" +
                        "            \""+billDetail.getTime()+"\",\n" +
                        "            \""+billDetail.getRefresh_time()+"\"\n" +
                        "        ]\n"  );


            }
            String param="";
            param = "{\n" +
                    "    \"columnNames\": [\n" +
                    "        \"location_id\",\n" +
                    "        \"store_id\",\n" +
                    "        \"store_name\",\n" +
                    "        \"b_date\",\n" +
                    "        \"serial\",\n" +
                    "        \"start_time\",\n" +
                    "        \"end_time\",\n" +
                    "        \"item_name\",\n" +
                    "        \"item_category\",\n" +
                    "        \"item_sub_category\",\n" +
                    "        \"original_price\",\n" +
                    "        \"actual_price\",\n" +
                    "        \"item_num\",\n" +
                    "        \"receivable\",\n" +
                    "        \"real_income\",\n" +
                    "        \"disc_money\",\n" +
                    "        \"is_chargeback\",\n" +
                    "        \"chargeback_price\",\n" +
                    "        \"chargeback_num\",\n" +
                    "        \"time\",\n" +
                    "        \"refresh_time\",\n" +
                    "    ],\n" +
                    "    \"keyCol\": \"store_id,serial,item_name\",\n" +
                    "    \"records\": [\n" +
                    recordsSb.toString()+
                    "    ],\n" +
                    "    \"tableName\": \"Bill Detail\"\n" +
                    "}";
            return apiDataStr(param);
        }catch (SQLException ex) {
            ///错误处理
            logger.info(ex.getMessage());
        }finally{
            EodGetConn.getGc().closeAll(rs,st,con);
            EodGetConn.getGc().closeAll(rs2,st2,con2);
        }
        return false;
    }

    /**
     * 4.Paytype Detail 支付方式明细表
     * @Author zenghuikang
     * @Description
     * @Date 2019/6/15 11:09
     * @param url
     * @param day
     * @return boolean
     * @throws
     **/
    private boolean processPaytypeDetail(String url,String day,String date ) {
        String sql = "SELECT NUMBER,sum(Qty*OPRICE) as receivable,max(DATE) as Saledate,min(TIME) as start_time FROM CTI.dbf group by NUMBER";
        logger.info("sql=>" + sql);
        Connection con = null;
        Connection con2 = null;
        try {
            con = EodGetConn.getGc().getCon(url);
            con2 = EodGetConn.getGc().getCon(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Statement st = null;
        ResultSet rs = null;
        Statement st2 = null;
        ResultSet rs2 = null;
        try {

            st = con.createStatement();

            rs = st.executeQuery(sql);

            PaytypeDetail paytypeDetail = new PaytypeDetail();


            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 创建Date对象，表示当前时间
            Date now = new Date();
            // 调用format()方法，将日期转换为字符串并输出
            StringBuilder recordsSb = new StringBuilder();
            while (rs.next()) {

                //NUMBER,receivable,Saledate,start_time,real_income,end_time
                String NUMBER = rs.getString("NUMBER");
                String receivable = rs.getString("receivable");
                String Saledate = rs.getString("Saledate");
                String start_time = rs.getString("start_time");

                /*表四，也用表二的SQL ,
                        支付方式 = 现金
                支付金额 = 实际收入*/

                sql="SELECT NUMBER,sum(AMOUNT) as real_income,max(TIME) as end_time FROM CTP.dbf where not isnull(AMOUNT) and " +
                        "NUMBER ="+NUMBER+" AND PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES) group by NUMBER \n";
                st2 = con2.createStatement();

                rs2 = st2.executeQuery(sql);
                String real_income="";
                String end_time="";
                while (rs2.next()) {
                    real_income = rs2.getString("real_income");
                    end_time = rs2.getString("end_time");
                }

                paytypeDetail.setLocation_id("");
                paytypeDetail.setStore_id("");
                paytypeDetail.setStore_name("");
                paytypeDetail.setB_date(Saledate);
                paytypeDetail.setSerial(date+NUMBER);
                paytypeDetail.setStart_time(Saledate+" "+start_time);
                paytypeDetail.setEnd_time(Saledate+" "+end_time);
                paytypeDetail.setPaytype("现金");
                paytypeDetail.setPaytype_income(Double.parseDouble(real_income));
                paytypeDetail.setTime(sdf3.format(now));
                paytypeDetail.setRefresh_time(sdf3.format(now));

                if(recordsSb.length()>0){
                    recordsSb.append(",");
                }
                recordsSb.append("        [\n" +
                        "            \""+locationId+"\",\n" +
                        "            \""+storeId+"\",\n" +
                        "            \""+storeName+"\",\n" +
                        "            \""+paytypeDetail.getB_date()+" 00:00:00\",\n" +
                        "            \""+paytypeDetail.getSerial()+"\",\n" +
                        "            \""+paytypeDetail.getStart_time()+"\",\n" +
                        "            \""+paytypeDetail.getEnd_time()+"\",\n" +
                        "            \""+paytypeDetail.getPaytype()+"\",\n" +
                        "            \""+paytypeDetail.getPaytype_income()+"\",\n" +
                        "            \""+paytypeDetail.getTime()+"\",\n" +
                        "            \""+paytypeDetail.getRefresh_time()+"\"\n" +
                        "        ]\n"  );


            }
            String param="";
            param = "{\n" +
                    "    \"columnNames\": [\n" +
                    "        \"location_id\",\n" +
                    "        \"store_id\",\n" +
                    "        \"store_name\",\n" +
                    "        \"b_date\",\n" +
                    "        \"serial\",\n" +
                    "        \"start_time\",\n" +
                    "        \"end_time\",\n" +
                    "        \"paytype\",\n" +
                    "        \"paytype_income\",\n" +
                    "        \"time\",\n" +
                    "        \"refresh_time\"\n" +
                    "    ],\n" +
                    "    \"keyCol\": \"store_id,serial,paytype\",\n" +
                    "    \"records\": [\n" +
                    recordsSb.toString()+
                    "    ],\n" +
                    "    \"tableName\": \"Paytype Detail\"\n" +
                    "}";
            return apiDataStr(param);
        }catch (SQLException ex) {
            ///错误处理
            logger.info(ex.getMessage());
        }finally{
            EodGetConn.getGc().closeAll(rs,st,con);
            EodGetConn.getGc().closeAll(rs2,st2,con2);
        }

        return false;
    }


    /**
     * 5.Discount Detail 优惠金额明细表
     * @Author zenghuikang
     * @Description
     * @Date 2019/6/15 11:09
     * @param url
     * @param day
     * @return boolean
     * @throws
     **/
    private boolean processDiscountDetail(String url,String day ,String date ) {
        String sql = "SELECT NUMBER,sum(Qty*OPRICE) as receivable,max(DATE) as Saledate,min(TIME) as start_time FROM CTI.dbf group by NUMBER";
        logger.info("sql=>" + sql);
        Connection con = null;
        Connection con2 = null;
        try {
            con = EodGetConn.getGc().getCon(url);
            con2 = EodGetConn.getGc().getCon(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Statement st = null;
        ResultSet rs = null;
        Statement st2 = null;
        ResultSet rs2 = null;
        try {

            st = con.createStatement();

            rs = st.executeQuery(sql);

            DiscountDetail discountDetail = new DiscountDetail();

            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 创建Date对象，表示当前时间
            Date now = new Date();
            // 调用format()方法，将日期转换为字符串并输出
            StringBuilder recordsSb = new StringBuilder();
            while (rs.next()) {

                //NUMBER,receivable,Saledate,start_time,real_income,end_time
                String NUMBER = rs.getString("NUMBER");
                String receivable = rs.getString("receivable");
                String Saledate = rs.getString("Saledate");
                String start_time = rs.getString("start_time");

                sql="SELECT NUMBER,sum(AMOUNT) as real_income,max(TIME) as end_time FROM CTP.dbf where not isnull(AMOUNT) and " +
                        "NUMBER ="+NUMBER+" AND PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES) group by NUMBER \n";
                st2 = con2.createStatement();

                rs2 = st2.executeQuery(sql);
                String real_income="";
                String end_time="";
                while (rs2.next()) {
                    real_income = rs2.getString("real_income");
                    end_time = rs2.getString("end_time");
                }

                /*表五，也用表二的SQL
                优惠类型 统一写 【优惠折扣】
                优惠金额* = 应收金额*- 实际收入*
                自动上传，允许重复上传，，SQL可不用加条件，表二到表五每次可上传多条记录，，全部上传吧*/
                discountDetail.setLocation_id("");
                discountDetail.setStore_id("");
                discountDetail.setStore_name("");
                discountDetail.setB_date(Saledate);
                discountDetail.setSerial(date+NUMBER);
                discountDetail.setStart_time(Saledate+" "+start_time);
                discountDetail.setEnd_time(Saledate+" "+end_time);
                discountDetail.setDiscount_type("优惠折扣");
                discountDetail.setDiscount_amount(Double.parseDouble(receivable)-Double.parseDouble(real_income));
                discountDetail.setTime(sdf3.format(now));
                discountDetail.setRefresh_time(sdf3.format(now));

                if(recordsSb.length()>0){
                    recordsSb.append(",");
                }
                recordsSb.append("        [\n" +
                        "            \""+locationId+"\",\n" +
                        "            \""+storeId+"\",\n" +
                        "            \""+storeName+"\",\n" +
                        "            \""+discountDetail.getB_date()+" 00:00:00\",\n" +
                        "            \""+discountDetail.getSerial()+"\",\n" +
                        "            \""+discountDetail.getStart_time()+"\",\n" +
                        "            \""+discountDetail.getEnd_time()+"\",\n" +
                        "            \""+discountDetail.getDiscount_type()+"\",\n" +
                        "            \""+discountDetail.getDiscount_amount()+"\",\n" +
                        "            \""+discountDetail.getTime()+"\",\n" +
                        "            \""+discountDetail.getRefresh_time()+"\"\n" +
                        "        ]\n"  );


            }
            String param="";
            param = "{\n" +
                    "    \"columnNames\": [\n" +
                    "        \"location_id\",\n" +
                    "        \"store_id\",\n" +
                    "        \"store_name\",\n" +
                    "        \"b_date\",\n" +
                    "        \"serial\",\n" +
                    "        \"start_time\",\n" +
                    "        \"end_time\",\n" +
                    "        \"discount_type\",\n" +
                    "        \"discount_amount\",\n" +
                    "        \"time\",\n" +
                    "        \"refresh_time\",\n" +
                    "    ],\n" +
                    "    \"keyCol\": \"store_id,serial,discount_type\",\n" +
                    "    \"records\": [\n" +
                    recordsSb.toString()+
                    "    ],\n" +
                    "    \"tableName\": \"Discount Detail\"\n" +
                    "}";
            return apiDataStr(param);
        }catch (SQLException ex) {
            ///错误处理
            logger.info(ex.getMessage());
        }finally{
            EodGetConn.getGc().closeAll(rs,st,con);
            EodGetConn.getGc().closeAll(rs2,st2,con2);
        }
        return false;
    }

    public boolean apiDataStr(String param){
        logger.info("apiDataStr(String param)=>"+param);
        if(true){
            return apiData(param);
        }
        //String url = "/api/data/str";
        //测试环境：https://lb-test.tcsl.com.cn:8079/bi_proxy/
        //String url = "https://lb-test.tcsl.com.cn:8079/bi_proxy//api/data/str";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> map = new HashMap<String, String>();
        //企业秘钥DES加密转base64
        map.put("data",com.yyx.aio.task.util.Base64Utils.encode(com.yyx.aio.task.util.DESUtil.encrypt(param,desKey)));
        try {
            //RSA公钥加密转base64
            map.put("corporationCode",com.yyx.aio.task.util.RSAUtil.encrypt(
                    com.yyx.aio.task.util.RSAUtil.loadPublicKey(publicKeyStr),corporationCode.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(map), headers);
        String result = restTemplate.postForObject(urlStr, httpEntity, String.class);

        Result result1 = JSON.parseObject(result, Result.class);

        logger.info("apiDataStr结果=>" + result);
        logger.info("apiDataStr结果=>" + result1.isSuccess());
        return result1.isSuccess();
    }


    public boolean apiData(String str){
        File file = null;
        try {
            file = File.createTempFile(String.valueOf(UUID.randomUUID()), ".zip");
            ZipOutputStream zos =  new ZipOutputStream(new FileOutputStream(file));
            //加密后压缩
            createZip(zos, DESUtil.encrypt(str,desKey));
            zos.closeEntry();
            zos.close();

            RestTemplate rest = new RestTemplate();
            FileSystemResource resource = new FileSystemResource(file);
            MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
            param.add("file", resource);
            param.add("corporationCode", RSAUtil.encrypt(RSAUtil.loadPublicKey(publicKeyStr),corporationCode.getBytes()));
            String result = rest.postForObject(url, param, String.class);
            file.delete();
            Result result1 = JSON.parseObject(result, Result.class);

            logger.info("apiData 结果=>" + result);
            logger.info("apiData 结果=>" + result1.isSuccess());
            return result1.isSuccess();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void createZip(ZipOutputStream zos, byte[] b) {
        try {
            zos.putNextEntry(new ZipEntry("param.txt"));
            zos.setComment("by zip test!");
            zos.write(b, 0, b.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 判断文件是否存在
    public static boolean fileExists(File file) {

        if (file.exists()) {
            //System.out.println("file exists");
            return true;
        } /*else {
            System.out.println("file not exists, create it ...");
            try {
                file.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }*/
        return false;
    }

    // 判断文件夹是否存在
    public static boolean dirExists(File file) {

        if (file.exists()) {
            if (file.isDirectory()) {
                return true;
                //System.out.println("dir exists");
            } else {
                return false;
                //System.out.println("the same name file exists, can not create dir");
            }
        } /*else {
            System.out.println("dir not exists, create it ...");
            file.mkdir();
        }*/
        return false;
    }
}
