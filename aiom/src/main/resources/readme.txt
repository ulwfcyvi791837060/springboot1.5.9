
还要加一个程序的配置文件：
有两个数据库目录，pointsoft_path=C:\pointsoft\EOD\      清机后
pointsoft_path_load=C:\pointsoft\EOD\   营业时
（手动上传从【清机后】的数据库中取数据；自动上传从【营业时】的数据库中取数据）
接口地址：https://bi.tcsl.com.cn:8055/lb/
企业编码：000062
企业秘钥：38ab46f762e63b64
RSA公钥：
上传间隔时间：5（分钟）

唐海军:
1.Summary 日销售汇总表
2.Business 营业明细表
3.Bill Detail  账单销售明细表
4.Paytype Detail 支付方式明细表
5.Discount Detail 优惠金额明细表

唐海军:
表1是一天传一次完整的，表2-5是按流水传，3-5分钟传一次即可

唐海军:
这个数据库有两份，存在两个目录下，一个叫【清机后】库，一个叫【营业时】库，

唐海军:
营业时（即：没清机时），数据文件存放在C:\PointSoft\FBPos\data，

唐海军:
每晚同事做清机工作，系统会将
C:\PointSoft\FBPos\data的数据复制一份到
C:\PointSoft\Eod\YYYYMMDD下，并将C:\PointSoft\FBPos\data的表清空。


表二，营业明细表：
SELECT NUMBER,sum(Qty*OPRICE) as sale_AMOUNT,max([DATE]) as Saledate,min([TIME]) as Saletime
,(SELECT  sum([AMOUNT]) as net_AMOUNT FROM CTP.dbf where not isnull([AMOUNT]) AND (PAYBY NOT in
(SELECT code FROM PAYMENT.dbf WHERE SALES=0) and CTP.dbf.NUMBER=CTI.dbf.NUMBER) as net_AMOUNT
,(SELECT  max([TTIME]) FROM CTP.dbf where CTP.dbf.NUMBER=CTI.dbf.NUMBER) as Settlement_time
FROM CTI.dbf
group by NUMBER
order by NUMBER


说明：

sale_AMOUNT：应收金额

Saletime：开单时间

net_AMOUNT： 实收金额  （有可能没有记录，要做特殊处理）

Settlement_time：结算时间 （有可能没有记录，要做特殊处理）

唐海军:
1.Summary 日销售汇总表
2.Business 营业明细表

3.Bill Detail  账单销售明细表
4.Paytype Detail 支付方式明细表
5.Discount Detail 优惠金额明细表

唐海军:
表1是一天传一次完整的，表2-5是按流水传，3-5分钟传一次即可

唐海军:
这个数据库有两份，存在两个目录下，一个叫【清机后】库，一个叫【营业时】库，

唐海军:
营业时（即：没清机时），数据文件存放在C:\PointSoft\FBPos\data，

唐海军:
每晚同事做清机工作，系统会将
C:\PointSoft\FBPos\data的数据复制一份到
C:\PointSoft\Eod\YYYYMMDD下，并将C:\PointSoft\FBPos\data的表清空。

自动上传 表一，只上传一次，最好从【清机后】的数据库中取数据（如果存在这个数据库目录，才上传）


1.Summary 日销售汇总表	该表主键为store_id+b_date两个字段为联合主键,确保本条数据的唯一性
字段名称	字段类型	字段说明	允许为空	格式示例	备注
location_id	string	商位号*	否	1	门店位置号
store_id	string	门店编号*	否	1
store_name	string	门店名称*	否	天财商龙赛格广场店
b_date	Date	营业日	否	2018-07-21(默认00:00-23:59)	自然日
s_receivable	double	应收金额*	否	8238.45 （空值填0）	小数点保留两位
s_real_income	double	实际收入*	否	7198 （空值填0）	小数点保留两位
s_bill_num	double	账单笔数	否	368（空值填0）
s_discount_total	double	优惠金额*	否	198.01 （空值填0）	小数点保留两位
s_discount_num	double	优惠笔数	否	368 （空值填0）
s_chargeback	double	退单金额*	否	198.01 （空值填0）	退单金额以正数体现
s_chargeback_num	double	退单笔数	否	36 （空值填0）	包含退单的流水笔数
s_time	Date	传输的时间	否	2018-07-01  11:29:30	精确到毫秒
s_refresh_time	Date	更新时间	否	2018-07-01  11:29:30	调用更新接口更新数据时的时间，精确到毫秒

SELECT  sum(AMOUNT) as net_AMOUNT FROM CTP.dbf where not isnull(AMOUNT) AND (PAYBY NOT in (SELECT code FROM PAYMENT.dbf WHERE NOT SALES))

"{\n" +
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
                    "            \"s_receivable\",\n" +
                    "            \"s_real_income\",\n" +
                    "            \"s_bill_num\",\n" +
                    "            \"s_discount_total\",\n" +
                    "            \"s_discount_num\",\n" +
                    "            \"s_chargeback\",\n" +
                    "            \"s_chargeback_num\",\n" +
                    "            \"s_time\",\n" +
                    "            \"s_refresh_time\",\n" +
                    "        ]\n" +
                    "    ],\n" +
                    "    \"tableName\": \"Summary\"\n" +
                    "}";

2.Business 营业明细表	该表主键为store_id+serial 两个字段为联合主键,确保本条数据的唯一性
字段名称	字段类型	字段说明	允许为空	格式示例
location_id	string	商位号*	否	1
store_id	string	门店编号*	否	1
store_name	string	门店名称*	否	天财商龙赛格广场店
b_date	Date	营业日	否	2018-07-21(默认00:00-23:59)	自然日
serial	string	账单序号	否	1
start_time	Date	开始时间(开单)	否	’2018-07-01  11:29:30
end_time	Date	结束时间(结算)	否	’2018-07-01  11:29:30
receivable	double	应收金额*	否	238.45
real_income	double	实际收入*	否	198.01
discount_amount	double	优惠金额*	否	198.01 （空值填0）
is_chargeback	boolean	是否退单	否	是 / 否
chargeback	double	退单金额*	否	198.01 （空值填0）	退单金额以正数体现
time	Date	上传时间	否	2018-07-01  11:29:30	上传龙决策的时间，精确到毫秒
refresh_time	Date	更新时间	否	2018-07-01  11:29:30	调用更新接口更新数据时的时间，精确到毫秒

"{\n" +
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
                    "            \"serial\",\n" +
                    "            \"start_time\",\n" +
                    "            \"end_time\",\n" +
                    "            \"receivable\",\n" +
                    "            \"real_income\",\n" +
                    "            \"discount_amount\",\n" +
                    "            \"is_chargeback\",\n" +
                    "            \"chargeback\",\n" +
                    "            \"time\",\n" +
                    "            \"refresh_time\",\n" +
                    "        ]\n" +
                    "    ],\n" +
                    "    \"tableName\": \"Business\"\n" +
                    "}";



3.Bill Detail  账单销售明细表	该表主键为 store_id + serial + item_name 三个字段为联合主键,确保本条数据的唯一性
字段名称	字段类型	字段说明	允许为空	格式示例
location_id	string	商位号*	否	1
store_id	string	门店编号*	否	1
store_name	string	门店名称*	否	天财商龙赛格广场店
b_date	Date	营业日	否	2018-07-21(默认00:00-23:59)	自然日
serial	string	账单序号	否	1
start_time	Date	开始时间(开单)	否	2018-07-01  11:29:30
end_time	Date	结束时间(结算)	否	2018-07-01  11:29:30
item_name	string	菜品名称	否	宫保鸡丁	要求传输套餐内明细，名称定义为：双人套餐-宫保鸡丁
item_category	string	菜品大类	否	热菜 （空值填缺省）
item_sub_category	string	菜品小类	否	荤菜  （空值填缺省）
original_price	double	折前单价	否	238.45
actual_price	double	折后单价	否	238.45
item_num	double	数量	否	10
receivable	double	应收金额*	否	238.45
real_income	double	实际收入*	否	198
disc_money	double	优惠金额*	否	238.45 （空值填0）
is_chargeback	boolean	是否退单	否	是 / 否
chargeback_price	double	退单单价	否	238.45 （空值填0）	取品项折前单价
chargeback_num	double	退单数量	否	10 （空值填0）
time	Date	上传时间	否	2018-07-01  11:29:30	上传龙决策时间，精确到毫秒
refresh_time	Date	更新时间	否	2018-07-01  11:29:30	 调用更新接口更新数据时的时间，精确到毫秒

唐海军:
前面三个从配置文件中取，

唐海军:
营业日，填数据库目录的日期，其他时间填当前时间，其它没有的填０


"{\n" +
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
                    "            \"start_time\",\n" +
                    "            \"end_time\",\n" +
                    "            \"item_name\",\n" +
                    "            \"item_category\",\n" +
                    "            \"item_sub_category\",\n" +
                    "            \"original_price\",\n" +
                    "            \"actual_price\",\n" +
                    "            \"item_num\",\n" +
                    "            \"receivable\",\n" +
                    "            \"real_income\",\n" +
                    "            \"disc_money\",\n" +
                    "            \"is_chargeback\",\n" +
                    "            \"chargeback_price\",\n" +
                    "            \"chargeback_num\",\n" +
                    "            \"time\",\n" +
                    "            \"refresh_time\",\n" +
                    "        ]\n" +
                    "    ],\n" +
                    "    \"tableName\": \"Bill Detail\"\n" +
                    "}";

4.Paytype Detail 支付方式明细表		该表主键为store_id+serial+paytype三个字段为联合主键,确保本条数据的唯一性
字段名称	字段类型	字段说明	允许为空	格式示例
location_id	string	商位号*	否	1
store_id	string	门店编号*	否	1
store_name	string	门店名称*	否	天财商龙赛格广场店
b_date	Date	营业日	否	2018-07-21(默认00:00-23:59)	自然日
serial	string	账单序号	否	1
start_time	Date	开始时间(开单)	否	2018-07-01  11:29:30
end_time	Date	结束时间(结算)	否	2018-07-01  11:29:30
paytype	string	支付方式	否	现金、微信	按门店现有支付方式名称上传
paytype_income	double	支付金额	否	238.45 （空值填0）	实收金额
time	Date	上传时间	否	2018-07-01  11:29:30	上传龙决策时间，精确到毫秒
refresh_time	Date	更新时间	否	2018-07-01  11:29:30	调用更新接口更新数据时的时间，精确到毫秒

"{\n" +
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
                    "            \"serial\",\n" +
                    "            \"start_time\",\n" +
                    "            \"end_time\",\n" +
                    "            \"paytype\",\n" +
                    "            \"paytype_income\",\n" +
                    "            \"time\",\n" +
                    "            \"refresh_time\",\n" +
                    "        ]\n" +
                    "    ],\n" +
                    "    \"tableName\": \"Paytype Detail\"\n" +
                    "}";

5.Discount Detail 优惠金额明细表		该表主键为store_id+serial+discount_type三个字段为联合主键,确保本条数据的唯一性
字段名称	字段类型	字段说明	允许为空	格式示例
location_id	string	商位号*	否	1
store_id	string	门店编号*	否	1
store_name	string	门店名称*	否	天财商龙赛格广场店
b_date	Date	营业日	否	2018-07-21(默认00:00-23:59)	自然日
serial	string	账单序号	否	1
start_time	Date	开始时间(开单)	否	2018-07-01  11:29:30
end_time	Date	结束时间(结算)	否	2018-07-01  11:29:30
discount_type	string	优惠类型	否	按系统自身定义填写，会员优惠、团购优惠、外卖优惠等	餐饮7系统内部的所有的优惠类型不包含跑单和宴请
discount_amount	double	优惠金额*	否	123.45 （空值填0）
time	Date	上传时间	否	2018-07-01  11:29:30	上传龙决策时间，精确到毫秒
refresh_time	Date	更新时间	否	2018-07-01  11:29:30	调用更新接口更新数据时的时间，精确到毫秒

"{\n" +
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
                    "            \"serial\",\n" +
                    "            \"start_time\",\n" +
                    "            \"end_time\",\n" +
                    "            \"discount_type\",\n" +
                    "            \"discount_amount\",\n" +
                    "            \"time\",\n" +
                    "            \"refresh_time\",\n" +
                    "        ]\n" +
                    "    ],\n" +
                    "    \"tableName\": \"Discount Detail\"\n" +
                    "}";

6.Merchant_archives 商户档案表(依赖excel上传更新或后期录入工具)				该表主键为store_id,确保本次导入数据的唯一性
字段名称	字段类型	字段说明	允许为空	格式示例
store_id	string	门店编号	否	100001
location_id	string	商位号	否	6001
floor	string	楼层	否	6楼
store_name	string	门店名称	否	天财商龙赛格广场店
type	string	业态	否	火锅
rent	double	保底租金	否	3,000.00
Deduction_rate	double	扣率	否	0.35
acreage	double	面积	否	124
contract_start_time	Date	合同开始时间	否	43282.478819444
contract_end_time	Date	合同到期时间	否	43647.478819444



关键字段说明附表
字段名称	公式说明
门店编码	商场方维护,商户唯一序号,门店编号由赛格自己建立,餐饮系统只做对应
商位号	商场方维护,商位号由赛格自己建立,餐饮系统只做对应
门店名称	商场方维护,门店名称由赛格自己建立,餐饮系统只做对应
应收金额	应收为客户消费的总金额，包括赠送、优惠、退单金额
实收金额	实收=应收-优惠-退单金额(包含跑单、免单、宴请、签单等门店免费的金额)
优惠金额	优惠金额包括：不计入实际收入的结算方式[会员卡(积分)(赠送)(券)、支付平台商家优惠、外卖平台商家优惠、外卖平台服务费、自定义设置的不是100%的结算方式]、会员价优惠+促销优惠+赠送金额+折扣金额+定额优惠+抹零金额
退单金额	退单金额（结算后发生的退单）：包括快餐模式的废单、中餐模式的废单、结算后退单、返位退单，以正数体现






