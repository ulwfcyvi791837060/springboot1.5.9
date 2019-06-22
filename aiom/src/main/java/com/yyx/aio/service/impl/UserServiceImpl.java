package com.yyx.aio.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.yyx.aio.common.file.AppendContentToFile;
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

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParsePosition;
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
    private String urlHttpStr;

    @Value("${dbf.store.url}")
    private String urlHttpZip;

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

    DecimalFormat    df   = new DecimalFormat("######0.00");

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
        String dirFile=eodDataBaseUrl+File.separator+date;
        logger.info("输出：dirFile==>"+dirFile);
        try {
            //自动上传时，在配置文件加一个最后上传日期。如果当前日期大于[最后上传日期]才上传表一。表一没有上传成功，
            // 后面的表也不用上传了,表一每天只上传一次 即可
            boolean eodDirExist = dirExists(new File(dirFile));
            String readFile = readFile("_Summary_update_date.txt");
            if(readFile!=null&&"".equals(readFile)){
                readFile="2010-01-01";
            }
            String strDate = readFile.trim()+" 23:59:59";
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ParsePosition pos = new ParsePosition(0);
            Date strtoDate = formatter.parse(strDate, pos);
            /*String strtoDateStr = formatter.format(strtoDate);
            String nowStr = formatter.format(new Date());
            logger.info("updateTime==>"+strtoDate.getTime());
            logger.info("updateTime==>"+strtoDateStr);
            logger.info("currentTimeMillis==>"+System.currentTimeMillis());
            logger.info("currentTimeMillis==>"+nowStr);*/

            boolean theDayIsUploaded = System.currentTimeMillis() < strtoDate.getTime();
            /*logger.info("theDayIsUploaded==>"+theDayIsUploaded);
            logger.info("eodDirExist==>"+eodDirExist);*/

            if(auto){
                //自动上传
                //有清机后目录 只上传一次 如果当前日期大于[最后上传日期]才上传表一
                if(eodDirExist){
                    if(!theDayIsUploaded){
                        uploadEod(dirFile,result,day,date);
                    }else{
                        logger.info("清机后数据已上传成功,自动上传不再上传数据_"+eodDataBaseUrl+date);
                    }
                    //有清机后目录,只上传清机后目录
                    return result;

                    //无清机后目录
                }else{
                    uploadFBPos(dirFile,result,day,date);
                    return result;
                }
            }else{
                //手动上传
                if(eodDirExist){
                    uploadEod(dirFile,result,day,date);
                    return result;
                    //无清机后目录
                }else{
                    result.setMsg(result.getMsg()+","+"清机后数据不存在,数据上传失败_"+eodDataBaseUrl+date);
                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void uploadEod(String dirFile,Result result,String day,String date){
            //自动上传 表一只从清机后取数，只上传一次，最好从【清机后】的数据库中取数据（如果存在这个数据库目录，才上传）
            //表1是一天传一次完整的，
            //自动上传，表一是从 【清机后】的数据库中取数据
            //自动上传 表二到表五 是从 营业中 数据库中取数据
            logger.info("正在上传==>"+dirFile);

            boolean b1 = processSummary(eodDataBaseUrl+date, day,date);
            if(!b1){
                logger.info("summary上传失败_"+eodDataBaseUrl+date);
                result.setSuccess(false);
                result.setMsg(result.getMsg()+","+"summary上传失败_"+eodDataBaseUrl+date);
                //result =b1;
                //return result;
            }else{
                logger.info("summary上传成功_"+eodDataBaseUrl+date);
                result.setMsg(result.getMsg()+","+"summary上传成功_"+eodDataBaseUrl+date);

                boolean b2 = processBusiness(eodDataBaseUrl+date, day,date);
                if(!b2){
                    logger.info("Business上传失败_"+eodDataBaseUrl+date);
                    result.setSuccess(false);
                    result.setMsg(result.getMsg()+","+"Business上传失败_"+eodDataBaseUrl+date);
                    //result =b1;
                    //return result;
                }else{
                    logger.info("Business上传成功_"+eodDataBaseUrl+date);
                    result.setMsg(result.getMsg()+","+"Business上传成功_"+eodDataBaseUrl+date);
                }
                logger.info("正在上传==>"+dirFile);
                boolean b0 = processBillDetail(eodDataBaseUrl+date, day,date);
                if(!b0){
                    logger.info("BillDetail上传失败_"+eodDataBaseUrl+date);
                    result.setSuccess(false);
                    result.setMsg(result.getMsg()+","+"BillDetail上传失败_"+eodDataBaseUrl+date);
                    //result =b1;
                    //return result;
                }else{
                    logger.info("BillDetail上传成功_"+eodDataBaseUrl+date);
                    result.setMsg(result.getMsg()+","+"BillDetail上传成功_"+eodDataBaseUrl+date);
                }
                logger.info("正在上传==>"+dirFile);
                boolean b3 = processPaytypeDetail(eodDataBaseUrl+date, day,date);
                if(!b3){
                    logger.info("PaytypeDetail上传失败_"+eodDataBaseUrl+date);
                    result.setSuccess(false);
                    result.setMsg(result.getMsg()+","+"PaytypeDetail上传失败_"+eodDataBaseUrl+date);
                    //result =b1;
                    //return result;
                }else{
                    logger.info("PaytypeDetail上传成功_"+eodDataBaseUrl+date);
                    result.setMsg(result.getMsg()+","+"PaytypeDetail上传成功_"+eodDataBaseUrl+date);
                }
                logger.info("正在上传==>"+dirFile);
                boolean b4 = processDiscountDetail(eodDataBaseUrl+date, day,date);
                if(!b4){
                    logger.info("DiscountDetail上传失败_"+eodDataBaseUrl+date);
                    result.setSuccess(false);
                    result.setMsg(result.getMsg()+","+"DiscountDetail上传失败_"+eodDataBaseUrl+date);
                    //result =b1;
                    //return result;
                }else{
                    logger.info("DiscountDetail上传成功_"+eodDataBaseUrl+date);
                    result.setMsg(result.getMsg()+","+"DiscountDetail上传成功_"+eodDataBaseUrl+date);
                }
            }
    }

    private void uploadFBPos(String dirFile,Result result,String day,String date) {
        logger.info(dirFile+"不存在");
        result.setMsg(result.getMsg()+","+dirFile+"不存在");
        //result =false;
        //return result;

        //清机前数据只会自动上传
        dirFile=fBPosDataBaseUrl;
        //表2-5是按流水传，3-5分钟传一次即可
        //手动传都传的清机后的吗? 是的

        boolean fb = dirExists(new File(dirFile));

        if(fb){

            //不用上传表1

            //自动上传 表一，只上传一次，最好从【清机后】的数据库中取数据（如果存在这个数据库目录，才上传）
            //表1是一天传一次完整的，
            //自动上传，表一是从 【清机后】的数据库中取数据
            //自动上传 表二到表五 是从 营业中 数据库中取数据
            logger.info("正在上传==>"+dirFile);

            boolean b2 = processBusiness(fBPosDataBaseUrl, day,date);
            if(!b2){
                logger.info("Business上传失败_"+fBPosDataBaseUrl);
                result.setSuccess(false);
                result.setMsg(result.getMsg()+","+"Business上传失败_"+fBPosDataBaseUrl);
                //result =b1;
                //return result;
            }else{
                logger.info("Business上传成功_"+fBPosDataBaseUrl);
                result.setMsg(result.getMsg()+","+"Business上传成功_"+fBPosDataBaseUrl);
            }
            logger.info("正在上传==>"+dirFile);
            boolean b1 = processBillDetail(fBPosDataBaseUrl, day,date);
            if(!b1){
                logger.info("BillDetail上传失败_"+fBPosDataBaseUrl);
                result.setSuccess(false);
                result.setMsg(result.getMsg()+","+"BillDetail上传失败_"+fBPosDataBaseUrl);
                //result =b1;
                //return result;
            }else{
                logger.info("BillDetail上传成功_"+fBPosDataBaseUrl);
                result.setMsg(result.getMsg()+","+"BillDetail上传成功_"+fBPosDataBaseUrl);
            }
            logger.info("正在上传==>"+dirFile);
            boolean b3 = processPaytypeDetail(fBPosDataBaseUrl, day,date);
            if(!b3){
                logger.info("PaytypeDetail上传失败_"+fBPosDataBaseUrl);
                result.setSuccess(false);
                result.setMsg(result.getMsg()+","+"PaytypeDetail上传失败_"+fBPosDataBaseUrl);
                //result =b1;
                //return result;
            }else{
                logger.info("PaytypeDetail上传成功_"+fBPosDataBaseUrl);
                result.setMsg(result.getMsg()+","+"PaytypeDetail上传成功_"+fBPosDataBaseUrl);
            }
            logger.info("正在上传==>"+dirFile);
            boolean b4 = processDiscountDetail(fBPosDataBaseUrl, day,date);
            if(!b4){
                logger.info("DiscountDetail上传失败_"+fBPosDataBaseUrl);
                result.setSuccess(false);
                result.setMsg(result.getMsg()+","+"DiscountDetail上传失败_"+fBPosDataBaseUrl);
                //result =b1;
                //return result;
            }else{
                logger.info("DiscountDetail上传成功_"+fBPosDataBaseUrl);
                result.setMsg(result.getMsg()+","+"DiscountDetail上传成功_"+fBPosDataBaseUrl);
            }

        }else{
            logger.info(dirFile+"不存在");
            result.setMsg(result.getMsg()+","+dirFile+"不存在");
            //result =false;
            //return result;
        }
    }

    /**
     * 1.Summary 日销售汇总表
     * @Author zenghuikang
     * @Description
     * @Date 2019/6/15 11:07
      * @param conStr
     * @param day
     * @return boolean
     * @throws
     **/
    private boolean processSummary(String conStr,String day,String date ) {

        double billNum=0;//账单笔数
        double discNum=0;//优惠笔数

        String sql3 = "SELECT NUMBER,sum(Qty*OPRICE) as receivable,max(DATE) as Saledate,min(TIME) as start_time FROM CTI.dbf group by NUMBER";

        logger.info("sql3=>" + sql3);
        Connection con3 = null;
        Connection con4 = null;
        try {
            con3 = EodGetConn.getGc().getCon(conStr);
            con4 = EodGetConn.getGc().getCon(conStr);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Statement st3 = null;
        ResultSet rs3 = null;
        Statement st4 = null;
        ResultSet rs4 = null;
        try {
            st3 = con3.createStatement();
            rs3 = st3.executeQuery(sql3);

            Business business = new Business();

            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf8 = new SimpleDateFormat("yyyy-MM-dd");
            // 创建Date对象，表示当前时间
            Date now = new Date();
            // 调用format()方法，将日期转换为字符串并输出
            StringBuffer recordsSb = new StringBuffer();

            //某天的应收总额，实收总额，优惠总额。
            double receivableSum=0;
            double realIncomeSum=0;
            double discountSum=0;

            while (rs3.next()) {
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
                String NUMBER = rs3.getString("NUMBER").trim();
                String receivable = rs3.getString("receivable").trim();
                String Saledate = rs3.getString("Saledate").trim();
                String start_time = rs3.getString("start_time").trim();

                /*sql3="SELECT NUMBER,sum(AMOUNT) as real_income,max(TIME) as end_time FROM CTP.dbf where not isnull(AMOUNT) and " +
                        "NUMBER ="+NUMBER+" AND PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES) group by NUMBER \n";*/

                sql3="SELECT NUMBER,sum(AMOUNT) as real_income,max(TIME) as end_time FROM CTP.dbf where not isnull(AMOUNT) AND PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES) group by NUMBER \n";
                st4 = con4.createStatement();

                rs4 = st4.executeQuery(sql3);
                String real_income="";
                String end_time="";
                while (rs4.next()) {
                    String number = rs4.getString("NUMBER").trim();
                    if(NUMBER.equals(number)){
                        real_income = rs4.getString("real_income").trim();
                        end_time = rs4.getString("end_time").trim();
                    }
                }



                business.setLocation_id("");
                business.setStore_id("");
                business.setStore_name("");
                business.setB_date(Saledate);
                DecimalFormat g1=new DecimalFormat("00000");
                String startZeroStr = g1.format(Integer.valueOf(NUMBER));
                business.setSerial(date+startZeroStr);
                business.setStart_time(Saledate+" "+start_time);
                business.setEnd_time(Saledate+" "+end_time);
                business.setReceivable(Double.parseDouble(receivable));
                business.setReal_income(Double.parseDouble(real_income));
                double disc = Double.parseDouble(receivable) - Double.parseDouble(real_income);
                business.setDiscount_amount(Double.parseDouble(df.format(disc)));
                business.setIs_chargeback("否");
                business.setChargeback(0.0D);
                business.setTime(sdf3.format(now));
                business.setRefresh_time(sdf3.format(now));

                receivableSum=receivableSum+Double.parseDouble(receivable);
                realIncomeSum=realIncomeSum+Double.parseDouble(real_income);


                if (Double.parseDouble(receivable)>Double.parseDouble(real_income)) {
                    discNum ++;//优惠笔数
                    discountSum=discountSum+disc;
                }

                billNum ++;//账单笔数
            }

        }catch (SQLException ex) {
            ///错误处理
            logger.info(ex.getMessage());
        }finally{
            EodGetConn.getGc().closeAll(rs3,st3,con3);
            EodGetConn.getGc().closeAll(rs4,st4,con4);
        }

        //return false;



        String sql = "SELECT  sum(AMOUNT) as net_AMOUNT FROM CTP.dbf where not isnull(AMOUNT) AND (PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES))";
        logger.info("sql=>" + sql);

        String sql2 = "SELECT  sum(Qty*OPRICE) as sale_AMOUNT FROM CTI.dbf";
        logger.info("sql2=>" + sql2);

        Connection con = null;
        Connection con2 = null;
        try {
            con = EodGetConn.getGc().getCon(conStr);
            con2 = EodGetConn.getGc().getCon(conStr);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Statement st = null;
        Statement st2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        try {
            st = con.createStatement();
            st2 = con2.createStatement();

            rs = st.executeQuery(sql);
            rs2 = st2.executeQuery(sql2);

            /*表一：
            应收总金额：
            SELECT  sum(Qty*OPRICE) as sale_AMOUNT FROM CTI.dbf
            表一，实收总金额：ＳＱＬ语句：
            SELECT  sum(AMOUNT) as net_AMOUNT FROM CTP.dbf where not isnull(AMOUNT) AND (PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES))
            表一  优惠总额=应收总额-实收总额*/

            Summary summary = new Summary();

            while (rs2.next()) {
                String sale_AMOUNT = rs2.getString("sale_AMOUNT").trim();
                logger.info("sale_AMOUNT:" + sale_AMOUNT);
                if(sale_AMOUNT!=null&&!"".equals(sale_AMOUNT)){
                    summary.setsReceivable(Double.parseDouble(df.format(Double.parseDouble(sale_AMOUNT))));
                }
            }

            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf8 = new SimpleDateFormat("yyyy-MM-dd");
            // 创建Date对象，表示当前时间
            Date now = new Date();
            // 调用format()方法，将日期转换为字符串并输出
            while (rs.next()) {

                String sRealIncome = rs.getString("net_AMOUNT").trim();
                logger.info("net_AMOUNT:" + sRealIncome);

                if(sRealIncome!=null&&!"".equals(sRealIncome)){
                    summary.setLocationId("");
                    summary.setStoreId("");
                    summary.setStoreName("");
                    summary.setbDate("");
                    summary.setsRealIncome(Double.parseDouble(df.format(Double.parseDouble(sRealIncome))));
                    summary.setsBillNum(billNum);
                    summary.setsDiscountTotal(Double.parseDouble(df.format(summary.getsReceivable()-summary.getsRealIncome())));
                    summary.setsDiscountNum(discNum);
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
                    boolean summary1 = apiDataStr(sdf8.format(now), "_Summary", param);
                    if(summary1){
                        //再加点日志，表二的数据日志，加一条汇总的日志：某天的应收总额，实收总额，优惠总额。
                        String log = conStr+"_summary上传成功,应收总额="+summary.getsReceivable()+"，实收总额="+summary.getsRealIncome()+"，优惠总额="+summary.getsDiscountTotal()+"";
                        AppendContentToFile.method2(sdf8.format(now)+"_Summary_and_Business_"+"log.txt",sdf3.format(new Date())+"_Summary==>"+log);
                    }
                    return summary1;
                }
            }
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
     * 2.Business 营业明细表
     * @Author zenghuikang
     * @Description
     * @Date 2019/6/15 11:08
     * @param conStr
     * @param day
     * @return boolean
     * @throws
     **/
    private boolean processBusiness(String conStr,String day,String date ) {
        String sql = "SELECT NUMBER,sum(Qty*OPRICE) as receivable,max(DATE) as Saledate,min(TIME) as start_time FROM CTI.dbf group by NUMBER";

        logger.info("sql=>" + sql);
        Connection con = null;
        Connection con2 = null;
        try {
            con = EodGetConn.getGc().getCon(conStr);
            con2 = EodGetConn.getGc().getCon(conStr);
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
            SimpleDateFormat sdf8 = new SimpleDateFormat("yyyy-MM-dd");
            // 创建Date对象，表示当前时间
            Date now = new Date();
            // 调用format()方法，将日期转换为字符串并输出
            StringBuffer recordsSb = new StringBuffer();

            //某天的应收总额，实收总额，优惠总额。
            double receivableSum=0;
            double realIncomeSum=0;
            double discountSum=0;
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
                String NUMBER = rs.getString("NUMBER").trim();
                String receivable = rs.getString("receivable").trim();
                String Saledate = rs.getString("Saledate").trim();
                String start_time = rs.getString("start_time").trim();

                /*sql="SELECT NUMBER,sum(AMOUNT) as real_income,max(TIME) as end_time FROM CTP.dbf where not isnull(AMOUNT) and " +
                        "NUMBER ="+NUMBER+" AND PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES) group by NUMBER \n";*/

                sql="SELECT NUMBER,sum(AMOUNT) as real_income,max(TIME) as end_time FROM CTP.dbf where not isnull(AMOUNT) AND PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES) group by NUMBER \n";

                st2 = con2.createStatement();

                rs2 = st2.executeQuery(sql);
                String real_income="";
                String end_time="";
                while (rs2.next()) {
                    String number = rs2.getString("NUMBER").trim();
                    if(NUMBER.equals(number)){
                        real_income = rs2.getString("real_income").trim();
                        end_time = rs2.getString("end_time").trim();
                    }
                }



                business.setLocation_id("");
                business.setStore_id("");
                business.setStore_name("");
                business.setB_date(Saledate);
                DecimalFormat g1=new DecimalFormat("00000");
                String startZeroStr = g1.format(Integer.valueOf(NUMBER));
                business.setSerial(date+startZeroStr);
                business.setStart_time(Saledate+" "+start_time);
                business.setEnd_time(Saledate+" "+end_time);
                business.setReceivable(Double.parseDouble(receivable));
                business.setReal_income(Double.parseDouble(real_income));
                double v = Double.parseDouble(receivable) - Double.parseDouble(real_income);
                business.setDiscount_amount(Double.parseDouble(df.format(v)));
                business.setIs_chargeback("否");
                business.setChargeback(0.0D);
                business.setTime(sdf3.format(now));
                business.setRefresh_time(sdf3.format(now));

                receivableSum=receivableSum+Double.parseDouble(receivable);
                realIncomeSum=realIncomeSum+Double.parseDouble(real_income);
                discountSum=discountSum+v;

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

            if(recordsSb!=null&&!"".equals(recordsSb)){
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

                boolean business1 = apiDataStr(sdf8.format(now), "_Business", param);
                if(business1){
                    //再加点日志，表二的数据日志，加一条汇总的日志：某天的应收总额，实收总额，优惠总额。
                    String log = conStr+"_Business上传成功,应收总额="+df.format(receivableSum)+"，实收总额="+df.format(realIncomeSum)+"，优惠总额="+df.format(discountSum)+"";
                    AppendContentToFile.method2(sdf8.format(now)+"_Summary_and_Business_"+"log.txt",sdf3.format(new Date())+"_Business==>"+log);
                }
                return business1;
            }
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
     * @param conStr
     * @param day
     * @return boolean
     * @throws
     **/
    private boolean processBillDetail(String conStr,String day, String date ) {
        String sql = "SELECT NUMBER,sum(Qty*OPRICE) as receivable,max(DATE) as Saledate,min(TIME) as start_time FROM CTI.dbf group by NUMBER";
        logger.info("sql=>" + sql);
        Connection con = null;
        Connection con2 = null;
        try {
            con = EodGetConn.getGc().getCon(conStr);
            con2 = EodGetConn.getGc().getCon(conStr);
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
            SimpleDateFormat sdf8 = new SimpleDateFormat("yyyy-MM-dd");
            // 创建Date对象，表示当前时间
            Date now = new Date();
            // 调用format()方法，将日期转换为字符串并输出
            StringBuffer recordsSb = new StringBuffer();
            while (rs.next()) {

                //NUMBER,receivable,Saledate,start_time,real_income,end_time
                String NUMBER = rs.getString("NUMBER").trim();
                String receivable = rs.getString("receivable").trim();
                String Saledate = rs.getString("Saledate").trim();
                String start_time = rs.getString("start_time").trim();

                /*表三和表二，SQL是一样的，
                菜品名称 = 甜品
                折前单价 = 应收金额*
                        折后单价 = 实际收入*
                        数量 = 1
                        serial = 日期（yyyymmdd）+ NUMBER*/

               /* sql="SELECT NUMBER,sum(AMOUNT) as real_income,max(TIME) as end_time FROM CTP.dbf where not isnull(AMOUNT) and " +
                        "NUMBER ="+NUMBER+" AND PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES) group by NUMBER \n";*/

                sql="SELECT NUMBER,sum(AMOUNT) as real_income,max(TIME) as end_time FROM CTP.dbf where not isnull(AMOUNT) AND PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES) group by NUMBER \n";

                st2 = con2.createStatement();

                rs2 = st2.executeQuery(sql);
                String real_income="";
                String end_time="";
                while (rs2.next()) {
                    String number = rs2.getString("NUMBER").trim();
                    if(NUMBER.equals(number)){
                        real_income = rs2.getString("real_income").trim();
                        end_time = rs2.getString("end_time").trim();
                    }
                }

                billDetail.setLocation_id("");
                billDetail.setStore_id("");
                billDetail.setStore_name("");
                billDetail.setB_date(Saledate);
                DecimalFormat g1=new DecimalFormat("00000");
                String startZeroStr = g1.format(Integer.valueOf(NUMBER));
                billDetail.setSerial(date+startZeroStr);
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
                billDetail.setDisc_money(Double.parseDouble(receivable)-Double.parseDouble(real_income));
                billDetail.setIs_chargeback("否");
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

            if(recordsSb!=null&&!"".equals(recordsSb)){
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
                        "        \"refresh_time\"\n" +
                        "    ],\n" +
                        "    \"keyCol\": \"store_id,serial,item_name\",\n" +
                        "    \"records\": [\n" +
                        recordsSb.toString()+
                        "    ],\n" +
                        "    \"tableName\": \"Bill Detail\"\n" +
                        "}";
                return apiDataStr(sdf8.format(now),"_Bill_Detail",param);
            }


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
     * @param conStr
     * @param day
     * @return boolean
     * @throws
     **/
    private boolean processPaytypeDetail(String conStr,String day,String date ) {
        String sql = "SELECT NUMBER,sum(Qty*OPRICE) as receivable,max(DATE) as Saledate,min(TIME) as start_time FROM CTI.dbf group by NUMBER";
        logger.info("sql=>" + sql);
        Connection con = null;
        Connection con2 = null;
        try {
            con = EodGetConn.getGc().getCon(conStr);
            con2 = EodGetConn.getGc().getCon(conStr);
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
            SimpleDateFormat sdf8 = new SimpleDateFormat("yyyy-MM-dd");
            // 创建Date对象，表示当前时间
            Date now = new Date();
            // 调用format()方法，将日期转换为字符串并输出
            StringBuffer recordsSb = new StringBuffer();
            while (rs.next()) {

                //NUMBER,receivable,Saledate,start_time,real_income,end_time
                String NUMBER = rs.getString("NUMBER").trim();
                String receivable = rs.getString("receivable").trim();
                String Saledate = rs.getString("Saledate").trim();
                String start_time = rs.getString("start_time").trim();

                /*表四，也用表二的SQL ,
                        支付方式 = 现金
                支付金额 = 实际收入*/

                /*sql="SELECT NUMBER,sum(AMOUNT) as real_income,max(TIME) as end_time FROM CTP.dbf where not isnull(AMOUNT) and " +
                        "NUMBER ="+NUMBER+" AND PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES) group by NUMBER \n";*/

                sql="SELECT NUMBER,sum(AMOUNT) as real_income,max(TIME) as end_time FROM CTP.dbf where not isnull(AMOUNT) AND PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES) group by NUMBER \n";

                st2 = con2.createStatement();

                rs2 = st2.executeQuery(sql);
                String real_income="";
                String end_time="";
                while (rs2.next()) {
                    String number = rs2.getString("NUMBER").trim();
                    if(NUMBER.equals(number)){
                        real_income = rs2.getString("real_income").trim();
                        end_time = rs2.getString("end_time").trim();
                    }
                }

                paytypeDetail.setLocation_id("");
                paytypeDetail.setStore_id("");
                paytypeDetail.setStore_name("");
                paytypeDetail.setB_date(Saledate);
                DecimalFormat g1=new DecimalFormat("00000");
                String startZeroStr = g1.format(Integer.valueOf(NUMBER));
                paytypeDetail.setSerial(date+startZeroStr);
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

            if(recordsSb!=null&&!"".equals(recordsSb)){
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
                return apiDataStr(sdf8.format(now),"_Paytype_Detail",param);
            }


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
     * @param conStr
     * @param day
     * @return boolean
     * @throws
     **/
    private boolean processDiscountDetail(String conStr,String day ,String date ) {
        String sql = "SELECT NUMBER,sum(Qty*OPRICE) as receivable,max(DATE) as Saledate,min(TIME) as start_time FROM CTI.dbf group by NUMBER";
        logger.info("sql=>" + sql);
        Connection con = null;
        Connection con2 = null;
        try {
            con = EodGetConn.getGc().getCon(conStr);
            con2 = EodGetConn.getGc().getCon(conStr);
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
            SimpleDateFormat sdf8 = new SimpleDateFormat("yyyy-MM-dd");
            // 创建Date对象，表示当前时间
            Date now = new Date();
            // 调用format()方法，将日期转换为字符串并输出
            StringBuffer recordsSb = new StringBuffer();
            while (rs.next()) {

                //NUMBER,receivable,Saledate,start_time,real_income,end_time
                String NUMBER = rs.getString("NUMBER").trim();
                String receivable = rs.getString("receivable").trim();
                String Saledate = rs.getString("Saledate").trim();
                String start_time = rs.getString("start_time").trim();

                /*sql="SELECT NUMBER,sum(AMOUNT) as real_income,max(TIME) as end_time FROM CTP.dbf where not isnull(AMOUNT) and " +
                        "NUMBER ="+NUMBER+" AND PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES) group by NUMBER \n";
*/
                sql="SELECT NUMBER,sum(AMOUNT) as real_income,max(TIME) as end_time FROM CTP.dbf where not isnull(AMOUNT) AND PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES) group by NUMBER \n";

                st2 = con2.createStatement();

                rs2 = st2.executeQuery(sql);
                String real_income="";
                String end_time="";
                while (rs2.next()) {
                    String number = rs2.getString("NUMBER").trim();
                    if(NUMBER.equals(number)){
                        real_income = rs2.getString("real_income").trim();
                        end_time = rs2.getString("end_time").trim();
                    }

                }

                /*表五，也用表二的SQL
                优惠类型 统一写 【优惠折扣】
                优惠金额* = 应收金额*- 实际收入*
                自动上传，允许重复上传，，SQL可不用加条件，表二到表五每次可上传多条记录，，全部上传吧*/
                discountDetail.setLocation_id("");
                discountDetail.setStore_id("");
                discountDetail.setStore_name("");
                discountDetail.setB_date(Saledate);
                DecimalFormat g1=new DecimalFormat("00000");
                String startZeroStr = g1.format(Integer.valueOf(NUMBER));
                discountDetail.setSerial(date+startZeroStr);
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

            if(recordsSb!=null&&!"".equals(recordsSb)){
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
                        "        \"refresh_time\"\n" +
                        "    ],\n" +
                        "    \"keyCol\": \"store_id,serial,discount_type\",\n" +
                        "    \"records\": [\n" +
                        recordsSb.toString()+
                        "    ],\n" +
                        "    \"tableName\": \"Discount Detail\"\n" +
                        "}";
                return apiDataStr(sdf8.format(now),"_Discount_Detail",param);
            }


        }catch (SQLException ex) {
            ///错误处理
            logger.info(ex.getMessage());
        }finally{
            EodGetConn.getGc().closeAll(rs,st,con);
            EodGetConn.getGc().closeAll(rs2,st2,con2);
        }
        return false;
    }

    public boolean apiDataStr(String dayStr,String table,String param){
        //logger.info("apiDataStr(String param)=>"+param);
        if(false){
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
        String result = restTemplate.postForObject(urlHttpStr, httpEntity, String.class);
        //logger.info("apiDataStr_urlHttpStr=>" + urlHttpStr);
        //logger.info("apiDataStr_httpEntity=>" + httpEntity);
        Result result1 = JSON.parseObject(result, Result.class);

        logger.info("apiDataStr结果=>" + result);
        logger.info("apiDataStr结果=>" + result1.isSuccess());
        if(result1.isSuccess()){
            AppendContentToFile.method2(dayStr+table+"_log.txt",param);
            AppendContentToFile.method4(table+"_update_date.txt",dayStr);
        }
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
            String result = rest.postForObject(urlHttpZip, param, String.class);
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
            //logger.info("file exists");
            return true;
        } /*else {
            logger.info("file not exists, create it ...");
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
                //logger.info("dir exists");
            } else {
                return false;
                //logger.info("the same name file exists, can not create dir");
            }
        } /*else {
            logger.info("dir not exists, create it ...");
            file.mkdir();
        }*/
        return false;
    }

    private String readFile ( String path ) throws IOException {

        //线程不安全
        StringBuffer builder = new StringBuffer();

        try {

            boolean fileExists = fileExists(new File(path));
            if(!fileExists){
                return "";
            }

            InputStreamReader reader = new InputStreamReader( new FileInputStream( path ), "UTF-8" );
            BufferedReader bfReader = new BufferedReader( reader );

            String tmpContent = null;

            while ( ( tmpContent = bfReader.readLine() ) != null ) {
                builder.append( tmpContent );
            }

            bfReader.close();

        } catch ( UnsupportedEncodingException e ) {
            // 忽略
        }
        return builder.toString();
    }
}
