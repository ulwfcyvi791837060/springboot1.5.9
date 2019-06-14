package com.yyx.aio.common;

/*
 * @fun 订单枚举
 * @author yaofeng
 * @date 2016-04-20
 */
public enum MergeEnum {
	
	/* 机构审核状态 */
	org_status_deal(0, "待审核")
	,org_status_deal_success(1, "审核成功")
	,org_status_deal_abort(2, "审核失败")
	
	
	/* 平台类型  */
	,platform_merchant(1, "商户后台平台")
	,platform_wallet(2, "钱包后台平台")
	
	/* 用户类型  */
	,user_type_org_manager(0, "机构管理员")
	,user_type_org_ordinary(1, "机构普通用户")
	,user_type_merge_manager(2, "门店管理员")
	,user_type_merge_ordinary(3, "门店普通用户")
	,user_type_merge_shopowner(4, "店长")
	,user_type_merge_cashier(5, "收银员")
	
	/* 树机构类型  */
	,tree_type_org(10, "机构")
	,tree_type_merge(11, "商户（或者门店）")
	,tree_type_terminal(12, "终端")
	,tree_type_org_ordinary(13, "机构用户")
	,tree_type_merge_ordinary(14, "商户用户")
	;
	
	
	public Integer code;
	public String msg;

	private MergeEnum(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
}
