package com.yyx.aio.entity;

import java.util.Date;

/**
 * @author: zhk
 * @Date :          2019/6/14 17:40
 */
public class Summary {
    private String locationId	;//string	商位号*	否	1	门店位置号
    private String storeId	;//string	门店编号*	否	1
    private String storeName	;//string	门店名称*	否	天财商龙赛格广场店
    private String bDate	;//Date	营业日	否	2018-07-21(默认00:00-23:59)	自然日
    private Double sReceivable	;//double	应收金额*	否	8238.45 （空值填0）	小数点保留两位
    private Double sRealIncome	;//double	实际收入*	否	7198 （空值填0）	小数点保留两位
    private Double sBillNum	;//double	账单笔数	否	368（空值填0）
    private Double sDiscountTotal	;//double	优惠金额*	否	198.01 （空值填0）	小数点保留两位
    private Double sDiscountNum	;//double	优惠笔数	否	368 （空值填0）
    private Double sChargeback	;//double	退单金额*	否	198.01 （空值填0）	退单金额以正数体现
    private Double sChargebackNum	;//double	退单笔数	否	36 （空值填0）	包含退单的流水笔数
    private String sTime	;//Date	传输的时间	否	2018-07-01  11:29:30	精确到毫秒
    private String sRefreshTime	;//Date	更新时间	否	2018-07-01  11:29:30	调用更新接口更新数据时的时间，精确到毫秒


    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getbDate() {
        return bDate;
    }

    public void setbDate(String bDate) {
        this.bDate = bDate;
    }

    public Double getsReceivable() {
        return sReceivable;
    }

    public void setsReceivable(Double sReceivable) {
        this.sReceivable = sReceivable;
    }

    public Double getsRealIncome() {
        return sRealIncome;
    }

    public void setsRealIncome(Double sRealIncome) {
        this.sRealIncome = sRealIncome;
    }

    public Double getsBillNum() {
        return sBillNum;
    }

    public void setsBillNum(Double sBillNum) {
        this.sBillNum = sBillNum;
    }

    public Double getsDiscountTotal() {
        return sDiscountTotal;
    }

    public void setsDiscountTotal(Double sDiscountTotal) {
        this.sDiscountTotal = sDiscountTotal;
    }

    public Double getsDiscountNum() {
        return sDiscountNum;
    }

    public void setsDiscountNum(Double sDiscountNum) {
        this.sDiscountNum = sDiscountNum;
    }

    public Double getsChargeback() {
        return sChargeback;
    }

    public void setsChargeback(Double sChargeback) {
        this.sChargeback = sChargeback;
    }

    public Double getsChargebackNum() {
        return sChargebackNum;
    }

    public void setsChargebackNum(Double sChargebackNum) {
        this.sChargebackNum = sChargebackNum;
    }

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public String getsRefreshTime() {
        return sRefreshTime;
    }

    public void setsRefreshTime(String sRefreshTime) {
        this.sRefreshTime = sRefreshTime;
    }
}
