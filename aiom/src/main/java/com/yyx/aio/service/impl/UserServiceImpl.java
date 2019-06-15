package com.yyx.aio.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.yyx.aio.common.file.SelectDbfUtil;
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
  * @param null
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

    @Value("${dbf.store.FBPosDataBaseUrl}")
    private String FBPosDataBaseUrl;

    @Value("${dbf.store.EodDataBaseUrl}")
    private String EodDataBaseUrl;



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
    public boolean uploadAction(String date) {
        logger.info("【job2】开始执行：{}", DateUtil.formatDateTime(new Date()));
        boolean result =true;
        if(date!=null&&date.trim().length()!=8){
            return false;
        }
        String y = date.substring(0, 3);
        String m = date.substring(4, 5);
        String d = date.substring(6, 7);
        String day =y+"-"+m+"-"+d;
        Statement st = null;
        ResultSet rs = null;
        Connection con =null;
        String dirFile=EodDataBaseUrl+"\\"+date;
        logger.info("输出：");
        SelectDbfUtil cont = null;
        try {
            cont = new SelectDbfUtil(dirFile);
            con = cont.getConnection();

            boolean b = dirExists(new File(dirFile));

            if(b){
                //自动上传 表一，只上传一次，最好从【清机后】的数据库中取数据（如果存在这个数据库目录，才上传）
                //表1是一天传一次完整的，
                boolean b1 = processSummary(st, rs, con, day);
                if(!b1){
                    result =b1;
                    return result;
                }
            }else{
                logger.info(dirFile+"不存在");
                result =false;
                return result;
            }

            /*cont = new SelectDbfUtil(EodDataBaseUrl+"\\"+date);
            con = cont.getConnection();*/

            //表二到表五 自动上传成功后，要把最后的NUMBER保存到配置文件，不然会漏传和重复上传
            //表2-5是按流水传，3-5分钟传一次即可
            //手动传都传的清机后的吗?

//            processBillDetail(st, rs, con,day);
//            processBusiness(st, rs, con,day);
//            processDiscountDetail(st, rs, con,day);
//            processPaytypeDetail(st, rs, con,day);

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

        return result;
    }

    private boolean processSummary(Statement st, ResultSet rs, Connection con, String day ) {
        String sql = "SELECT  sum(AMOUNT) as net_AMOUNT FROM CTP.dbf where not isnull(AMOUNT) AND (PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES))";
        //logger.info("结果=>" + sql);
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
                        "            \""+summary.getsRefreshTime()+"\",\n" +
                        "        ]\n" +
                        "    ],\n" +
                        "    \"tableName\": \"Summary\"\n" +
                        "}";
                return apiDataStr(param);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean processBusiness(Statement st, ResultSet rs, Connection con,String day ) {
        String sql = "select a.*,b.net_AMOUNT, b.Settlement_time  from (SELECT NUMBER,sum(Qty*OPRICE) as sale_AMOUNT," +
                "max(DATE) as Saledate,min(TIME) as Saletime FROM CTI.dbf group by NUMBER) a,(SELECT NUMBER,sum(AMOUNT) " +
                "as net_AMOUNT,max(TIME) as Settlement_time FROM CTP.dbf where not isnull(AMOUNT) AND PAYBY NOT in " +
                "(SELECT code FROM PAYMENT.dbf WHERE NOT SALES) group by NUMBER) b where a.NUMBER=b.NUMBER";

        //logger.info("结果=>" + sql);
        try {
            st = con.createStatement();

            rs = st.executeQuery(sql);

            Business business = new Business();

            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 创建Date对象，表示当前时间
            Date now = new Date();
            // 调用format()方法，将日期转换为字符串并输出
            while (rs.next()) {

                String sRealIncome = "net_AMOUNT:" + rs.getString("net_AMOUNT");
                logger.info(sRealIncome);

                business.setLocation_id("");
                business.setStore_id("");
                business.setStore_name("");
                business.setB_date("");
                business.setSerial("");
                business.setStart_time("");
                business.setEnd_time("");
                business.setReceivable(0.0D);
                business.setReal_income(0.0D);
                business.setDiscount_amount(0.0D);
                business.setIs_chargeback("");
                business.setChargeback(0.0D);
                business.setTime(sdf3.format(now));
                business.setRefresh_time(sdf3.format(now));

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
                        "        \"receivable\",\n" +
                        "        \"real_income\",\n" +
                        "        \"discount_amount\",\n" +
                        "        \"is_chargeback\",\n" +
                        "        \"chargeback\",\n" +
                        "        \"time\",\n" +
                        "        \"refresh_time\"\n" +
                        "    ],\n" +
                        "    \"keyCol\": \"store_id,serial\",\n" +
                        "    \"records\": [\n" +
                        "        [\n" +
                        "            \""+locationId+"\",\n" +
                        "            \""+storeId+"\",\n" +
                        "            \""+storeName+"\",\n" +
                        "            \""+day+" 00:00:00\",\n" +
                        "            \""+business.getSerial()+"\",\n" +
                        "            \""+business.getStart_time()+"\",\n" +
                        "            \""+business.getEnd_time()+"\",\n" +
                        "            \""+business.getReceivable()+"\",\n" +
                        "            \""+business.getReal_income()+"\",\n" +
                        "            \""+business.getDiscount_amount()+"\",\n" +
                        "            \""+business.getIs_chargeback()+"\",\n" +
                        "            \""+business.getChargeback()+"\",\n" +
                        "            \""+business.getTime()+"\",\n" +
                        "            \""+business.getRefresh_time()+"\",\n" +
                        "        ]\n" +
                        "    ],\n" +
                        "    \"tableName\": \"Business\"\n" +
                        "}";
                return apiDataStr(param);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean processBillDetail(Statement st, ResultSet rs, Connection con,String day ) {
        String sql = "SELECT  sum(AMOUNT) as net_AMOUNT FROM CTP.dbf where not isnull(AMOUNT) AND (PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES))";
        //logger.info("结果=>" + sql);
        try {
            st = con.createStatement();

            rs = st.executeQuery(sql);

            BillDetail billDetail = new BillDetail();

            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 创建Date对象，表示当前时间
            Date now = new Date();
            // 调用format()方法，将日期转换为字符串并输出
            while (rs.next()) {

                String sRealIncome = "net_AMOUNT:" + rs.getString("net_AMOUNT");
                logger.info(sRealIncome);

                billDetail.setLocation_id("");
                billDetail.setStore_id("");
                billDetail.setStore_name("");
                billDetail.setB_date("");
                billDetail.setSerial("");
                billDetail.setStart_time("");
                billDetail.setEnd_time("");
                billDetail.setItem_name("");
                billDetail.setItem_category("");
                billDetail.setItem_sub_category("");
                billDetail.setOriginal_price(0.0D);
                billDetail.setActual_price(0.0D);
                billDetail.setItem_num(0.0D);
                billDetail.setReceivable(0.0D);
                billDetail.setReal_income(0.0D);
                billDetail.setDisc_money(0.0D);
                billDetail.setIs_chargeback("");
                billDetail.setChargeback_price(0.0D);
                billDetail.setChargeback_num(0.0D);
                billDetail.setTime(sdf3.format(now));
                billDetail.setRefresh_time(sdf3.format(now));

                String param="";
                param = "{\n" +
                        "    \"columnNames\": [\n" +
                        "        \"location_id\",\n" +
                        "        \"store_id\",\n" +
                        "        \"store_name\",\n" +
                        "        \"b_date\",\n" +
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
                        "        [\n" +
                        "            \""+locationId+"\",\n" +
                        "            \""+storeId+"\",\n" +
                        "            \""+storeName+"\",\n" +
                        "            \""+day+" 00:00:00\",\n" +
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
                        "            \""+billDetail.getRefresh_time()+"\",\n" +
                        "        ]\n" +
                        "    ],\n" +
                        "    \"tableName\": \"Bill Detail\"\n" +
                        "}";
                return apiDataStr(param);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean processDiscountDetail(Statement st, ResultSet rs, Connection con,String day ) {
        String sql = "SELECT  sum(AMOUNT) as net_AMOUNT FROM CTP.dbf where not isnull(AMOUNT) AND (PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES))";
        //logger.info("结果=>" + sql);
        try {
            st = con.createStatement();

            rs = st.executeQuery(sql);

            DiscountDetail discountDetail = new DiscountDetail();

            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 创建Date对象，表示当前时间
            Date now = new Date();
            // 调用format()方法，将日期转换为字符串并输出
            while (rs.next()) {

                String sRealIncome = "net_AMOUNT:" + rs.getString("net_AMOUNT");
                logger.info(sRealIncome);

                discountDetail.setLocation_id("");
                discountDetail.setStore_id("");
                discountDetail.setStore_name("");
                discountDetail.setB_date("");
                discountDetail.setSerial("");
                discountDetail.setStart_time("");
                discountDetail.setEnd_time("");
                discountDetail.setDiscount_type("");
                discountDetail.setDiscount_amount(0.0D);
                discountDetail.setTime(sdf3.format(now));
                discountDetail.setRefresh_time(sdf3.format(now));

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
                        "        [\n" +
                        "            \""+locationId+"\",\n" +
                        "            \""+storeId+"\",\n" +
                        "            \""+storeName+"\",\n" +
                        "            \""+day+" 00:00:00\",\n" +
                        "            \""+discountDetail.getSerial()+"\",\n" +
                        "            \""+discountDetail.getStart_time()+"\",\n" +
                        "            \""+discountDetail.getEnd_time()+"\",\n" +
                        "            \""+discountDetail.getDiscount_type()+"\",\n" +
                        "            \""+discountDetail.getDiscount_amount()+"\",\n" +
                        "            \""+discountDetail.getTime()+"\",\n" +
                        "            \""+discountDetail.getRefresh_time()+"\",\n" +
                        "        ]\n" +
                        "    ],\n" +
                        "    \"tableName\": \"Discount Detail\"\n" +
                        "}";
                return apiDataStr(param);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean processPaytypeDetail(Statement st, ResultSet rs, Connection con,String day ) {
        String sql = "SELECT  sum(AMOUNT) as net_AMOUNT FROM CTP.dbf where not isnull(AMOUNT) AND (PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES))";
        //logger.info("结果=>" + sql);
        try {
            st = con.createStatement();

            rs = st.executeQuery(sql);

            PaytypeDetail paytypeDetail = new PaytypeDetail();


            SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 创建Date对象，表示当前时间
            Date now = new Date();
            // 调用format()方法，将日期转换为字符串并输出
            while (rs.next()) {

                String sRealIncome = "net_AMOUNT:" + rs.getString("net_AMOUNT");
                logger.info(sRealIncome);

                paytypeDetail.setLocation_id("");
                paytypeDetail.setStore_id("");
                paytypeDetail.setStore_name("");
                paytypeDetail.setB_date("");
                paytypeDetail.setSerial("");
                paytypeDetail.setStart_time("");
                paytypeDetail.setEnd_time("");
                paytypeDetail.setPaytype("");
                paytypeDetail.setPaytype_income(0.0D);
                paytypeDetail.setTime(sdf3.format(now));
                paytypeDetail.setRefresh_time(sdf3.format(now));

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
                        "        [\n" +
                        "            \""+locationId+"\",\n" +
                        "            \""+storeId+"\",\n" +
                        "            \""+storeName+"\",\n" +
                        "            \""+day+" 00:00:00\",\n" +
                        "            \""+paytypeDetail.getSerial()+"\",\n" +
                        "            \""+paytypeDetail.getStart_time()+"\",\n" +
                        "            \""+paytypeDetail.getEnd_time()+"\",\n" +
                        "            \""+paytypeDetail.getPaytype()+"\",\n" +
                        "            \""+paytypeDetail.getPaytype_income()+"\",\n" +
                        "            \""+paytypeDetail.getTime()+"\",\n" +
                        "            \""+paytypeDetail.getRefresh_time()+"\",\n" +
                        "        ]\n" +
                        "    ],\n" +
                        "    \"tableName\": \"Paytype Detail\"\n" +
                        "}";
                return apiDataStr(param);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean apiDataStr(String param){
        if(true){
            return apiData(param);
        }
        //String url = "/api/data/str";
        //测试环境：https://lb-test.tcsl.com.cn:8079/bi_proxy/
        //String url = "https://lb-test.tcsl.com.cn:8079/bi_proxy//api/data/str";
        logger.info("apiDataStr(String param)=>",param);
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
