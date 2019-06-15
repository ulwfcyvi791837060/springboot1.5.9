DROP TABLE
    IF
        EXISTS `Business`;
CREATE TABLE `Business` (
                            id bigint(20) NOT NULL AUTO_INCREMENT,
                            location_id	varchar(255)	COMMENT '商位号*	否	1',
                            store_id	varchar(255)	COMMENT '门店编号*	否	1',
                            store_name	varchar(255)	COMMENT '门店名称*	否	天财商龙赛格广场店',
                            b_datetime	datetime	COMMENT '营业日	否	2018-07-21(默认00:00-23:59)	自然日',
                            serial	varchar(255)	COMMENT '账单序号	否	1',
                            start_time	datetime	COMMENT '开始时间(开单)	否	’2018-07-01  11:29:30',
                            end_time	datetime	COMMENT '结束时间(结算)	否	’2018-07-01  11:29:30',
                            receivable	double	COMMENT '应收金额*	否	238.45',
                            real_income	double	COMMENT '实际收入*	否	198.01',
                            discount_amount	double	COMMENT '优惠金额*	否	198.01 （空值填0）',
                            is_chargeback	boolean	COMMENT '是否退单	否	是 / 否',
                            chargeback	double	COMMENT '退单金额*	否	198.01 （空值填0）	退单金额以正数体现',
                            time	datetime	COMMENT '上传时间	否	2018-07-01  11:29:30	上传龙决策的时间，精确到毫秒',
                            refresh_time	datetime	COMMENT '更新时间	否	2018-07-01  11:29:30	调用更新接口更新数据时的时间，精确到毫秒',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT = 'Business';



DROP TABLE
    IF
        EXISTS `Bill Detail`;
CREATE TABLE `Bill Detail` (
                               id bigint(20) NOT NULL AUTO_INCREMENT,
                               location_id	varchar(255)	COMMENT '商位号*	否	1',
                               store_id	varchar(255)	COMMENT '门店编号*	否	1',
                               store_name	varchar(255)	COMMENT '门店名称*	否	天财商龙赛格广场店',
                               b_date	datetime	COMMENT '营业日	否	2018-07-21(默认00:00-23:59)	自然日',
                               serial	varchar(255)	COMMENT '账单序号	否	1',
                               start_time	datetime	COMMENT '开始时间(开单)	否	2018-07-01  11:29:30',
                               end_time	datetime	COMMENT '结束时间(结算)	否	2018-07-01  11:29:30',
                               item_name	varchar(255)	COMMENT '菜品名称	否	宫保鸡丁	要求传输套餐内明细，名称定义为：双人套餐-宫保鸡丁',
                               item_category	varchar(255)	COMMENT '菜品大类	否	热菜 （空值填缺省）',
                               item_sub_category	varchar(255)	COMMENT '菜品小类	否	荤菜  （空值填缺省）',
                               original_price	double	COMMENT '折前单价	否	238.45',
                               actual_price	double	COMMENT '折后单价	否	238.45',
                               item_num	double	COMMENT '数量	否	10',
                               receivable	double	COMMENT '应收金额*	否	238.45',
                               real_income	double	COMMENT '实际收入*	否	198',
                               disc_money	double	COMMENT '优惠金额*	否	238.45 （空值填0）',
                               is_chargeback	boolean	COMMENT '是否退单	否	是 / 否',
                               chargeback_price	double	COMMENT '退单单价	否	238.45 （空值填0）	取品项折前单价',
                               chargeback_num	double	COMMENT '退单数量	否	10 （空值填0）',
                               time	datetime	COMMENT '上传时间	否	2018-07-01  11:29:30	上传龙决策时间，精确到毫秒',
                               refresh_time	datetime	COMMENT '更新时间	否	2018-07-01  11:29:30	 调用更新接口更新数据时的时间，精确到毫秒',
                               PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT = 'Bill Detail';






DROP TABLE
    IF
        EXISTS `Paytype Detail`;
CREATE TABLE `Paytype Detail` (
                                  id bigint(20) NOT NULL AUTO_INCREMENT,
                                  location_id	varchar(255)	COMMENT '商位号*	否	1',
                                  store_id	varchar(255)	COMMENT '门店编号*	否	1',
                                  store_name	varchar(255)	COMMENT '门店名称*	否	天财商龙赛格广场店',
                                  b_date	datetime	COMMENT '营业日	否	2018-07-21(默认00:00-23:59)	自然日',
                                  serial	varchar(255)	COMMENT '账单序号	否	1',
                                  start_time	datetime	COMMENT '开始时间(开单)	否	2018-07-01  11:29:30',
                                  end_time	datetime	COMMENT '结束时间(结算)	否	2018-07-01  11:29:30',
                                  paytype	varchar(255)	COMMENT '支付方式	否	现金、微信	按门店现有支付方式名称上传',
                                  paytype_income	double	COMMENT '支付金额	否	238.45 （空值填0）	实收金额',
                                  time	datetime	COMMENT '上传时间	否	2018-07-01  11:29:30	上传龙决策时间，精确到毫秒',
                                  refresh_time	datetime	COMMENT '更新时间	否	2018-07-01  11:29:30	调用更新接口更新数据时的时间，精确到毫秒',
                                  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT = 'Paytype Detail';




DROP TABLE
    IF
        EXISTS `Discount Detail`;
CREATE TABLE `Discount Detail` (
                                   id bigint(20) NOT NULL AUTO_INCREMENT,
                                   location_id	varchar(255)	COMMENT '商位号*	否	1',
                                   store_id	varchar(255)	COMMENT '门店编号*	否	1',
                                   store_name	varchar(255)	COMMENT '门店名称*	否	天财商龙赛格广场店',
                                   b_date	datetime	COMMENT '营业日	否	2018-07-21(默认00:00-23:59)	自然日',
                                   serial	varchar(255)	COMMENT '账单序号	否	1',
                                   start_time	datetime	COMMENT '开始时间(开单)	否	2018-07-01  11:29:30',
                                   end_time	datetime	COMMENT '结束时间(结算)	否	2018-07-01  11:29:30',
                                   discount_type	varchar(255)	COMMENT '优惠类型	否	按系统自身定义填写，会员优惠、团购优惠、外卖优惠等	餐饮7系统内部的所有的优惠类型不包含跑单和宴请',
                                   discount_amount	double	COMMENT '优惠金额*	否	123.45 （空值填0）',
                                   time	datetime	COMMENT '上传时间	否	2018-07-01  11:29:30	上传龙决策时间，精确到毫秒',
                                   refresh_time	datetime	COMMENT '更新时间	否	2018-07-01  11:29:30	调用更新接口更新数据时的时间，精确到毫秒',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT = 'Discount Detail';




