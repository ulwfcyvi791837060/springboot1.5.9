package com.yyx.aio.common;

/*
 * @fun 订单枚举
 * @author yaofeng
 * @date 2016-04-20
 */
public enum OrderEnum {
	
	/* 支付方式 */
	pay_method_balance(0,"账户余额")
	,pay_method_bank(1,"银行卡")
	,pay_method_alipay(2,"支付宝")
	,pay_method_weixin_pay(3,"微信支付")
	,pay_method_other_pay(4,"别人代付")
	,pay_method_member_card_pay(5,"会员卡支付")
	
	
	/* 订单状态 */
	,order_status_deal(0, "处理中")
	,order_status_deal_success(1, "成功")
	,order_status_deal_abort(2, "作废")
	,order_status_deal_lose(3, "失败")
	,order_status_deal_cancel(4, "取消")
	
	/* 订单类型 */
	,order_type_consume(1,"消费")
	,order_type_refund(2,"退款")
	,order_type_balance(3,"充值虚户")
	,order_type_transfer(4,"转账")
	,order_type_accept_transfer(5,"接收转账")
	,order_type_receipt(6,"收款")
	,order_type_pay_order(7,"代付订单")
	,order_type_cash(8,"提现")
	,order_type_member_card(9,"充值会员卡")
	,order_type_red_package(10,"发红包")
	
	/*
	 * 业务处理状态
	 */
	//消费业务处理状态
	,order_consume_business_deal_status_not_pay(1, "待支付")
	,order_consume_business_deal_status_pay_success(1, "支付成功")
	,order_consume_business_deal_status_pay_fail(1, "支付失败")
	
	//账户余额充值业务处理状态
	,order_acc_business_deal_status_not_pay(3, "待支付")
	,order_acc_business_deal_status_pay_success(3, "充值成功")
	,order_acc_business_deal_status_pay_fail(3, "充值失败")
	
	//会员卡充值业务处理状态
	,order_membercard_business_deal_status_not_pay(9, "待支付")
	,order_membercard_business_deal_status_pay_success(9, "充值成功")
	,order_membercard_business_deal_status_pay_fail(9, "充值失败")
	
	//转账业务处理状态
	,order_transfer_business_deal_status_not_pay(4, "待支付")
	,order_transfer_business_deal_status_pay_success(4, "转账成功")
	,order_transfer_business_deal_status_pay_fail(4, "转账失败")
	
	//代付业务处理状态
	,order_paid_business_deal_status_not_pay(7, "已申请代付")
	,order_paid_business_deal_status_pay_success(7, "代付成功")
	,order_paid_business_deal_status_pay_refuse(7, "代付已拒绝")
	,order_paid_business_deal_status_pay_fail(7, "代付失败")
	
	//红包业务处理状态
	,order_red_business_deal_status_not_open(10, "待拆红包")
	,order_red_business_deal_status_pay_success(10, "红包已领")
	
	//红包退款业务处理状态
	,order_red_refund_business_deal_status_pay_fail(2, "红包退款")
	
	//提现业务处理状态
	,order_with_business_deal_status_apply(8, "已申请提现")
	,order_with_business_deal_status_success(8, "提现成功")
	,order_with_business_deal_status_fail(8, "提现失败")
	,order_with_business_deal_status_not_pay(8, "待支付")
	
	/* 支付状态 */
	,pay_status_not_pay(0,"未支付")
	,pay_status_success(1,"支付成功")
	,pay_status_failure(2,"支付失败")
	
	/* 转账类型 */
	,transfer_account_un_income(0,"已转出，未转入")
	,transfer_account_finish_income(1,"已转入")
	
	/* 收款类型 */
	,transfer_no_receiving(2,"待收款")
	,transfer_has_receiving(3,"已收款")
	
	/* 代付类型 */
	,transfer_has_paid(4,"已代付")
	;
	
	public Integer code;
	public String msg;

	private OrderEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	/*
	 * @fun 根据：code获取msg信息
	 * @param index
	 * @return
	 */
    public static String getMsg(int index) {  
        for (OrderEnum c : OrderEnum.values()) {  
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
