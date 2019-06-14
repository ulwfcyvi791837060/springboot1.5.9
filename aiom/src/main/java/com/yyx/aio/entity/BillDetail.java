package com.yyx.aio.entity;

/**
 * @author: zhk
 * @Date :          2019/6/14 17:50
 */
public class BillDetail {

    private String location_id	;//string	商位号*	否	1
    private String store_id	;//string	门店编号*	否	1
    private String store_name	;//string	门店名称*	否	天财商龙赛格广场店
    private String b_date	;//Date	营业日	否	2018-07-21(默认00:00-23:59)	自然日
    private String serial	;//string	账单序号	否	1
    private String start_time	;//Date	开始时间(开单)	否	2018-07-01  11:29:30
    private String end_time	;//Date	结束时间(结算)	否	2018-07-01  11:29:30
    private String item_name	;//string	菜品名称	否	宫保鸡丁	要求传输套餐内明细，名称定义为：双人套餐-宫保鸡丁
    private String item_category	;//string	菜品大类	否	热菜 （空值填缺省）
    private String item_sub_category	;//string	菜品小类	否	荤菜  （空值填缺省）
    private Double original_price	;//double	折前单价	否	238.45
    private Double actual_price	;//double	折后单价	否	238.45
    private Double item_num	;//double	数量	否	10
    private Double receivable	;//double	应收金额*	否	238.45
    private Double real_income	;//double	实际收入*	否	198
    private Double disc_money	;//double	优惠金额*	否	238.45 （空值填0）
    private String is_chargeback	;//boolean	是否退单	否	是 / 否
    private Double chargeback_price	;//double	退单单价	否	238.45 （空值填0）	取品项折前单价
    private Double chargeback_num	;//double	退单数量	否	10 （空值填0）
    private String time	;//Date	上传时间	否	2018-07-01  11:29:30	上传龙决策时间，精确到毫秒
    private String refresh_time	;//Date	更新时间	否	2018-07-01  11:29:30	 调用更新接口更新数据时的时间，精确到毫秒


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

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_category() {
        return item_category;
    }

    public void setItem_category(String item_category) {
        this.item_category = item_category;
    }

    public String getItem_sub_category() {
        return item_sub_category;
    }

    public void setItem_sub_category(String item_sub_category) {
        this.item_sub_category = item_sub_category;
    }

    public Double getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(Double original_price) {
        this.original_price = original_price;
    }

    public Double getActual_price() {
        return actual_price;
    }

    public void setActual_price(Double actual_price) {
        this.actual_price = actual_price;
    }

    public Double getItem_num() {
        return item_num;
    }

    public void setItem_num(Double item_num) {
        this.item_num = item_num;
    }

    public Double getReceivable() {
        return receivable;
    }

    public void setReceivable(Double receivable) {
        this.receivable = receivable;
    }

    public Double getReal_income() {
        return real_income;
    }

    public void setReal_income(Double real_income) {
        this.real_income = real_income;
    }

    public Double getDisc_money() {
        return disc_money;
    }

    public void setDisc_money(Double disc_money) {
        this.disc_money = disc_money;
    }

    public String getIs_chargeback() {
        return is_chargeback;
    }

    public void setIs_chargeback(String is_chargeback) {
        this.is_chargeback = is_chargeback;
    }

    public Double getChargeback_price() {
        return chargeback_price;
    }

    public void setChargeback_price(Double chargeback_price) {
        this.chargeback_price = chargeback_price;
    }

    public Double getChargeback_num() {
        return chargeback_num;
    }

    public void setChargeback_num(Double chargeback_num) {
        this.chargeback_num = chargeback_num;
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
