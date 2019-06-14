package com.yyx.aio.common;

/*
 * @fun 统计枚举
 * @author yaofeng
 * @date 2016-08-19
 */
public enum CountEnum {
	
	/* 统计枚举描述 */
	count_jg(1,"统计机构总数")
	,count_sh(2,"商户总数")
	,count_zchy(3,"今日新注册会员")
	,count_hyzs(4,"会员总数")
	,count_hy_consume_money(5,"今日会员消费金额")
	,count_smrz_hy_zs(6,"实名认证会员总数")
	,count_all_hy_consume(7,"累计会员消费金额、累计会员余额充值金额、累计会员卡充值金额")
	,count_hy_tx(8,"累计会员提现金额")
	,count_jrsy_money(9,"统计今日收益金额")
	,count_sycz_money(10,"统计收益金额、累计卡充值金额")
	;
	
	public Integer code;
	public String msg;

	private CountEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	/*
	 * @fun 根据：code获取msg信息
	 * @param index
	 * @return
	 */
    public static String getMsg(int index) {  
        for (CountEnum c : CountEnum.values()) {  
            if (c.getCode() == index) {  
                return c.msg;  
            }  
        }  
        return null;  
    }  
    
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
