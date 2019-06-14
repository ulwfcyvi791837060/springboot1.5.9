package com.yyx.aio.entity;

/**
 * @author: zhk
 * @Date :          2019/6/14 17:54
 */
public class DiscountDetail {

    private String location_id	;//string	商位号*	否	1
    private String store_id	;//string	门店编号*	否	1
    private String store_name	;//;//string	门店名称*	否	天财商龙赛格广场店
    private String b_date	;//Date	营业日	否	2018-07-21(默认00:00-23:59)	自然日
    private String serial	;//string	账单序号	否	1
    private String start_time	;//Date	开始时间(开单)	否	2018-07-01  11:29:30
    private String end_time	;//Date	结束时间(结算)	否	2018-07-01  11:29:30
    private String discount_type	;//string	优惠类型	否	按系统自身定义填写，会员优惠、团购优惠、外卖优惠等	餐饮7系统内部的所有的优惠类型不包含跑单和宴请
    private Double discount_amount	;//double	优惠金额*	否	123.45 （空值填0）
    private String time	;//Date	上传时间	否	2018-07-01  11:29:30	上传龙决策时间，精确到毫秒
    private String refresh_time	;//Date	更新时间	否	2018-07-01  11:29:30	调用更新接口更新数据时的时间，精确到毫秒

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getB_date() {
        return b_date;
    }

    public void setB_date(String b_date) {
        this.b_date = b_date;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getDiscount_type() {
        return discount_type;
    }

    public void setDiscount_type(String discount_type) {
        this.discount_type = discount_type;
    }

    public Double getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(Double discount_amount) {
        this.discount_amount = discount_amount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRefresh_time() {
        return refresh_time;
    }

    public void setRefresh_time(String refresh_time) {
        this.refresh_time = refresh_time;
    }
}
