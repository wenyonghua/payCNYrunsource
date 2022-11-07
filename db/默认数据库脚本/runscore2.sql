/*
 Navicat MySQL Data Transfer

 Source Server         : 144.34.163.64
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : 144.34.163.64
 Source Database       : runscore2

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : utf-8

 Date: 09/01/2022 20:22:01 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `account_change_log`
-- ----------------------------
DROP TABLE IF EXISTS `account_change_log`;
CREATE TABLE `account_change_log` (
  `id` varchar(32) NOT NULL,
  `account_change_amount` double DEFAULT NULL,
  `account_change_time` datetime DEFAULT NULL,
  `account_change_type_code` varchar(255) DEFAULT NULL,
  `balance` double DEFAULT NULL,
  `game_code` varchar(255) DEFAULT NULL,
  `issue_num` bigint DEFAULT NULL,
  `order_no` varchar(255) DEFAULT NULL,
  `user_account_id` varchar(32) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `cash_deposit` double DEFAULT NULL,
  `commission` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `account_change_log`
-- ----------------------------
BEGIN;
INSERT INTO `account_change_log` VALUES ('1563881770254860288', '-100', '2022-08-28 21:30:44', '3', null, null, null, '1563881613765378048', '1490598801151361024', '0', null, '426882258270.86', null), ('1563889663976931328', '14', '2022-08-28 22:02:06', '4', null, null, null, '1563889663976931328', '1490598801151361024', '0', '接单返点:1.4%', '426882258284.86', null), ('1563893783249027072', '280000', '2022-08-28 22:18:28', '4', null, null, null, '1563893783249027072', '1490598801151361024', '0', '接单返点:1.4%', '426882538284.86', null), ('1563900104509751296', '1e19', '2022-08-28 22:43:35', '8', null, null, null, null, '1563897711772565504', '0', null, '1e19', null), ('1563900942896594944', '140000', '2022-08-28 22:46:55', '4', null, null, null, '1563900942896594944', '1563897711772565504', '0', '接单返点:1.4%', '1.000000000000014e19', null), ('1563901856529252352', '1e21', '2022-08-28 22:50:33', '8', null, null, null, null, '1563901678845952000', '0', null, '1e21', null), ('1563901968110321664', '1e19', '2022-08-28 22:51:00', '8', null, null, null, null, '1547241827894034432', '0', null, '1e19', null), ('1564988217336266752', '1e29', '2022-08-31 22:47:22', '8', null, null, null, null, '1564988076063719424', '0', null, '1e29', null), ('1564998338510585856', '1.4', '2022-08-31 23:27:35', '4', null, null, null, '1564998338510585856', '1564988076063719424', '0', '接单返点:1.4%', '1e29', null), ('1565000483993550848', '1e26', '2022-08-31 23:36:06', '8', null, null, null, null, '1565000370717982720', '0', null, '1e26', null), ('1565001869497991168', '2.6', '2022-08-31 23:41:37', '4', null, null, null, '1565001869497991168', '1565000370717982720', '0', '接单返点:1.3%', '1e26', null);
COMMIT;

-- ----------------------------
--  Table structure for `account_channel_rebate`
-- ----------------------------
DROP TABLE IF EXISTS `account_channel_rebate`;
CREATE TABLE `account_channel_rebate` (
  `id` varchar(32) NOT NULL,
  `channel_id` varchar(32) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `rebate` double DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `user_account_id` varchar(32) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `account_receive_order_channel`
-- ----------------------------
DROP TABLE IF EXISTS `account_receive_order_channel`;
CREATE TABLE `account_receive_order_channel` (
  `id` varchar(32) NOT NULL,
  `user_account_id` varchar(32) DEFAULT NULL COMMENT '用户ID号',
  `channel_id` varchar(32) DEFAULT NULL COMMENT '渠道表ID',
  `create_time` datetime DEFAULT NULL,
  `rebate` double DEFAULT NULL COMMENT '返点',
  `state` varchar(255) DEFAULT NULL COMMENT '1是开启，2是关闭',
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `account_receive_order_channel`
-- ----------------------------
BEGIN;
INSERT INTO `account_receive_order_channel` VALUES ('1564997616847028224', '1564988076063719424', '1149365394574671872', '2022-08-31 23:24:43', '1.4', '1', '0'), ('1565000436555972608', '1565000370717982720', '1149365394574671872', '2022-08-31 23:35:55', '1.3', '1', '0'), ('1565299139107880960', '1547241827894034432', '1547241032293285888', '2022-09-01 19:22:51', '15', '1', '0');
COMMIT;

-- ----------------------------
--  Table structure for `actual_income_record`
-- ----------------------------
DROP TABLE IF EXISTS `actual_income_record`;
CREATE TABLE `actual_income_record` (
  `id` varchar(32) NOT NULL,
  `actual_income` double DEFAULT NULL COMMENT '商户实际收到金额',
  `available_flag` bit(1) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `merchant_id` varchar(32) DEFAULT NULL COMMENT '商户ID号',
  `merchant_order_id` varchar(32) DEFAULT NULL COMMENT '订单ID号',
  `settlement_time` datetime DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `service_charge` double DEFAULT NULL COMMENT '手续费',
  `merchant_total` double DEFAULT NULL COMMENT '商户结余',
  `merchant_order_no` varchar(255) DEFAULT NULL COMMENT '外部商户订单号',
  `gathering_code_usage_id` varchar(255) DEFAULT NULL COMMENT '收款码ID号',
  `gathering_code_id` varchar(32) DEFAULT NULL,
  `payout_flag` varchar(32) DEFAULT NULL COMMENT '付款标记1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `actual_income_record`
-- ----------------------------
BEGIN;
INSERT INTO `actual_income_record` VALUES ('1564998338514780160', '98.6', b'1', '2022-08-31 23:27:35', '1564984584259502080', '1564997767141523456', '2022-08-31 23:27:37', '0', '1.4', '98.6', 'A02222', null, '1564989014912532480', '0'), ('1565001869497991169', '197.4', b'1', '2022-08-31 23:41:37', '1564982426864713728', '1565001751457693696', '2022-08-31 23:41:39', '0', '2.6', '197.4', 'A0222211', null, '1565000969631039488', '0');
COMMIT;

-- ----------------------------
--  Table structure for `appeal`
-- ----------------------------
DROP TABLE IF EXISTS `appeal`;
CREATE TABLE `appeal` (
  `id` varchar(32) NOT NULL,
  `appeal_type` varchar(255) DEFAULT NULL,
  `initiation_time` datetime DEFAULT NULL,
  `merchant_order_id` varchar(32) DEFAULT NULL,
  `actual_pay_amount` double DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `user_sreenshot_ids` varchar(255) DEFAULT NULL,
  `process_way` varchar(255) DEFAULT NULL,
  `merchant_sreenshot_ids` varchar(255) DEFAULT NULL,
  `initiator_obj` varchar(255) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `appeal`
-- ----------------------------
BEGIN;
INSERT INTO `appeal` VALUES ('1160324771120939008', '1', '2019-08-11 06:59:18', '1160324720973840384', null, '2', '', '3', null, 'user', '1'), ('1456626371110371328', '1', '2021-11-05 22:16:04', '1456623917585137664', null, '2', '', '3', null, 'user', '1'), ('1456629720283086848', '1', '2021-11-05 22:29:23', '1456629391005057024', null, '2', '', '3', null, 'user', '1'), ('1460996362031071232', '1', '2021-11-17 23:40:51', '1460996177100013568', null, '2', '', '3', null, 'user', '1'), ('1483443902185734144', '1', '2022-01-18 22:19:22', '1482729668267212800', null, '2', '', '3', null, 'user', '1'), ('1483444454600736768', '2', '2022-01-18 22:21:34', '1482710466449899520', '100', '2', '1483444454361661440', '2', null, 'user', '1'), ('1493571627521146880', '1', '2022-02-15 21:03:20', '1493559185281908736', null, '2', '', '3', null, 'user', '1'), ('1493571675373961216', '1', '2022-02-15 21:03:31', '1493558732393545728', null, '2', '', '3', null, 'user', '1'), ('1493571697066901504', '1', '2022-02-15 21:03:36', '1493523082080419840', null, '2', '', '3', null, 'user', '1'), ('1493571732647182336', '1', '2022-02-15 21:03:45', '1493523005756669952', null, '2', '', '3', null, 'user', '1'), ('1493571756709904384', '1', '2022-02-15 21:03:51', '1493519898293305344', null, '2', '', '3', null, 'user', '1'), ('1493571772878946304', '1', '2022-02-15 21:03:55', '1493514598769229824', null, '2', '', '3', null, 'user', '1'), ('1493571796639678464', '1', '2022-02-15 21:04:00', '1493481717900509184', null, '2', '', '3', null, 'user', '1'), ('1493907160885624832', '1', '2022-02-16 19:16:37', '1493903940561928192', null, '2', '', '3', null, 'user', '1'), ('1493907231576424448', '1', '2022-02-16 19:16:54', '1493904161454948352', null, '2', '', '3', null, 'user', '1'), ('1493907267181871104', '1', '2022-02-16 19:17:03', '1493904451533012992', null, '2', '', '3', null, 'user', '1'), ('1495599293384359936', '1', '2022-02-21 11:20:33', '1495595612698050560', null, '2', '', '3', null, 'user', '1'), ('1495599335553892352', '1', '2022-02-21 11:20:43', '1495595534201651200', null, '2', '', '3', null, 'user', '1'), ('1495599368286240768', '1', '2022-02-21 11:20:51', '1495592980839399424', null, '2', '', '3', null, 'user', '1'), ('1495599612185018368', '1', '2022-02-21 11:21:49', '1495581744659693568', null, '2', '', '3', null, 'user', '1'), ('1495604720599826432', '1', '2022-02-21 11:42:07', '1495599804380610560', null, '2', '', '3', null, 'user', '1'), ('1495612628502839296', '1', '2022-02-21 12:13:32', '1495607237798789120', null, '2', '', '3', null, 'user', '1'), ('1495612673440612352', '1', '2022-02-21 12:13:43', '1495611095119822848', null, '2', '', '3', null, 'user', '1'), ('1495612730462175232', '1', '2022-02-21 12:13:57', '1495607984661725184', null, '2', '', '3', null, 'user', '1'), ('1495613106561220608', '1', '2022-02-21 12:15:26', '1495611216838524928', null, '2', '', '3', null, 'user', '1'), ('1495618569277276160', '1', '2022-02-21 12:37:09', '1495616322791276544', null, '2', '', '3', null, 'user', '1'), ('1495623220290650112', '1', '2022-02-21 12:55:38', '1495621262402125824', null, '2', '', '3', null, 'user', '1'), ('1495627153050435584', '1', '2022-02-21 13:11:15', '1495624963384672256', null, '2', '', '3', null, 'user', '1'), ('1495627199162613760', '1', '2022-02-21 13:11:26', '1495625132142493696', null, '2', '', '3', null, 'user', '1'), ('1495632477857775616', '1', '2022-02-21 13:32:25', '1495630475425742848', null, '2', '', '3', null, 'user', '1'), ('1495633477779849216', '1', '2022-02-21 13:36:23', '1495631351733288960', null, '2', '', '3', null, 'user', '1'), ('1495636588758040576', '1', '2022-02-21 13:48:45', '1495634145550794752', null, '2', '', '3', null, 'user', '1'), ('1495638565466406912', '1', '2022-02-21 13:56:36', '1495636398969978880', null, '2', '', '3', null, 'user', '1'), ('1495650317981712384', '1', '2022-02-21 14:43:18', '1495647475711606784', null, '2', '', '3', null, 'user', '1'), ('1495657220795269120', '1', '2022-02-21 15:10:44', '1495653731142729728', null, '2', '', '3', null, 'user', '1'), ('1495661613393379328', '1', '2022-02-21 15:28:11', '1495659573531377664', null, '2', '', '3', null, 'user', '1'), ('1495666547358695424', '1', '2022-02-21 15:47:48', '1495664256731840512', null, '2', '', '3', null, 'user', '1'), ('1495673703281721344', '1', '2022-02-21 16:16:14', '1495670523370143744', null, '2', '', '3', null, 'user', '1'), ('1495675679285444608', '1', '2022-02-21 16:24:05', '1495673122794242048', null, '2', '', '3', null, 'user', '1'), ('1495675710763696128', '1', '2022-02-21 16:24:12', '1495673250028453888', null, '2', '', '3', null, 'user', '1'), ('1495688313330204672', '1', '2022-02-21 17:14:17', '1495686321778196480', null, '2', '', '3', null, 'user', '1'), ('1495704178251530240', '1', '2022-02-21 18:17:20', '1495701773128564736', null, '2', '', '3', null, 'user', '1'), ('1495704422498435072', '1', '2022-02-21 18:18:18', '1495701910227779584', null, '2', '', '3', null, 'user', '1'), ('1495704452844224512', '1', '2022-02-21 18:18:25', '1495702360255627264', null, '2', '', '3', null, 'user', '1'), ('1495724159685099520', '1', '2022-02-21 19:36:44', '1495721665538031616', null, '2', '', '3', null, 'user', '1'), ('1495730131933593600', '1', '2022-02-21 20:00:27', '1495727144586706944', null, '2', '', '3', null, 'user', '1'), ('1495737477581766656', '1', '2022-02-21 20:29:39', '1495735194269777920', null, '2', '', '3', null, 'user', '1'), ('1495739415438295040', '1', '2022-02-21 20:37:21', '1495737098177609728', null, '2', '', '3', null, 'user', '1'), ('1495741830879248384', '1', '2022-02-21 20:46:57', '1495739301890097152', null, '2', '', '3', null, 'user', '1'), ('1495741911980310528', '1', '2022-02-21 20:47:16', '1495740557836681216', null, '2', '', '3', null, 'user', '1'), ('1495742760949383168', '1', '2022-02-21 20:50:38', '1495741696720240640', null, '2', '', '3', null, 'user', '1'), ('1495742905992609792', '1', '2022-02-21 20:51:13', '1495741759836127232', null, '2', '', '3', null, 'user', '1'), ('1495743070103142400', '1', '2022-02-21 20:51:52', '1495742345671344128', null, '2', '', '3', null, 'user', '1'), ('1495745698656681984', '1', '2022-02-21 21:02:19', '1495743007045976064', null, '2', '', '3', null, 'user', '1'), ('1495746001007280128', '1', '2022-02-21 21:03:31', '1495744427459608576', null, '2', '', '3', null, 'user', '1'), ('1495747888225976320', '1', '2022-02-21 21:11:01', '1495745268673413120', null, '2', '', '3', null, 'user', '1'), ('1495748565534769152', '1', '2022-02-21 21:13:42', '1495746229710094336', null, '2', '', '3', null, 'user', '1'), ('1495749422426882048', '1', '2022-02-21 21:17:07', '1495746382177239040', null, '2', '', '3', null, 'user', '1'), ('1495749633618477056', '1', '2022-02-21 21:17:57', '1495746501656182784', null, '2', '', '3', null, 'user', '1'), ('1495751300229365760', '1', '2022-02-21 21:24:34', '1495747658583638016', null, '2', '', '3', null, 'user', '1'), ('1495752220182839296', '1', '2022-02-21 21:28:14', '1495747865031475200', null, '2', '', '3', null, 'user', '1'), ('1495752752335159296', '1', '2022-02-21 21:30:21', '1495748195320332288', null, '2', '', '3', null, 'user', '1'), ('1495753202144903168', '1', '2022-02-21 21:32:08', '1495748571624898560', null, '2', '', '3', null, 'user', '1'), ('1495753354972758016', '1', '2022-02-21 21:32:44', '1495748755213778944', null, '2', '', '3', null, 'user', '1'), ('1495754370296315904', '1', '2022-02-21 21:36:46', '1495749200959242240', null, '2', '', '3', null, 'user', '1'), ('1495754496708444160', '1', '2022-02-21 21:37:16', '1495749283951935488', null, '2', '', '3', null, 'user', '1'), ('1495754651612479488', '1', '2022-02-21 21:37:53', '1495749452894306304', null, '2', '', '3', null, 'user', '1'), ('1495754708294303744', '1', '2022-02-21 21:38:07', '1495749653654667264', null, '2', '', '3', null, 'user', '1'), ('1495754758932135936', '1', '2022-02-21 21:38:19', '1495750130505089024', null, '2', '', '3', null, 'user', '1'), ('1495754800938090496', '1', '2022-02-21 21:38:29', '1495750195541966848', null, '2', '', '3', null, 'user', '1'), ('1495755369228533760', '1', '2022-02-21 21:40:44', '1495754808051630080', null, '2', '', '3', null, 'user', '1'), ('1495755414359244800', '1', '2022-02-21 21:40:55', '1495752813160955904', null, '2', '', '3', null, 'user', '1'), ('1495755443136364544', '1', '2022-02-21 21:41:02', '1495755315872792576', null, '2', '', '3', null, 'user', '1'), ('1495767232599293952', '1', '2022-02-21 22:27:53', '1495757771637784576', null, '2', '', '3', null, 'user', '1'), ('1495767384982552576', '1', '2022-02-21 22:28:29', '1495761283398500352', null, '2', '', '3', null, 'user', '1'), ('1495767545691504640', '1', '2022-02-21 22:29:08', '1495762535385661440', null, '2', '', '3', null, 'user', '1'), ('1495767732291895296', '1', '2022-02-21 22:29:52', '1495762638146109440', null, '2', '', '3', null, 'user', '1'), ('1495786036008058880', '1', '2022-02-21 23:42:36', '1495765610255089664', null, '2', '', '3', null, 'user', '1');
COMMIT;

-- ----------------------------
--  Table structure for `bank_card`
-- ----------------------------
DROP TABLE IF EXISTS `bank_card`;
CREATE TABLE `bank_card` (
  `id` varchar(32) NOT NULL,
  `account_holder` varchar(255) DEFAULT NULL,
  `bank_card_account` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `deleted_flag` bit(1) DEFAULT NULL,
  `deleted_time` datetime DEFAULT NULL,
  `lately_modify_time` datetime DEFAULT NULL,
  `open_account_bank` varchar(255) DEFAULT NULL,
  `user_account_id` varchar(32) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `bank_card_record`
-- ----------------------------
DROP TABLE IF EXISTS `bank_card_record`;
CREATE TABLE `bank_card_record` (
  `id` varchar(32) NOT NULL,
  `actual_income` double DEFAULT NULL COMMENT '商户实际收到金额',
  `available_flag` bit(1) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `merchant_id` varchar(32) DEFAULT NULL COMMENT '商户ID号',
  `merchant_order_id` varchar(32) DEFAULT NULL COMMENT '订单ID号',
  `settlement_time` datetime DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `service_charge` double DEFAULT NULL COMMENT '手续费',
  `card_total` double DEFAULT NULL COMMENT '银行卡结余',
  `merchant_order_no` varchar(255) DEFAULT NULL COMMENT '外部商户订单号',
  `gathering_code_id` varchar(32) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `bank_card_record`
-- ----------------------------
BEGIN;
INSERT INTO `bank_card_record` VALUES ('1564998338527363072', '100', b'1', '2022-08-31 23:27:35', '1564984584259502080', '1564997767141523456', '2022-08-31 23:27:37', '0', '0', '100', 'A02222', '1564989014912532480', null), ('1565001869502185472', '200', b'1', '2022-08-31 23:41:37', '1564982426864713728', '1565001751457693696', '2022-08-31 23:41:39', '0', '0', '200', 'A0222211', '1565000969631039488', null);
COMMIT;

-- ----------------------------
--  Table structure for `config_item`
-- ----------------------------
DROP TABLE IF EXISTS `config_item`;
CREATE TABLE `config_item` (
  `id` varchar(32) NOT NULL,
  `config_code` varchar(255) DEFAULT NULL,
  `config_name` varchar(255) DEFAULT NULL,
  `config_value` varchar(255) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `config_item`
-- ----------------------------
BEGIN;
INSERT INTO `config_item` VALUES ('1142342794728177664', 'localStoragePath', '本地存储对象路径', '/Users/wythe/uploadimg', '4'), ('1142344136335032320', 'merchantOrderPayUrl', '商户订单支付地址', 'http://144.34.170.115:8090/payyuenan?orderNo=', '12'), ('1143552378264354816', 'register.inviteRegisterLink', '注册-邀请注册链接', 'http://47.92.167.103:8080/register?inviteCode=', '6'), ('1489550030191722496', 'adminOrderPayUrl', '付款界面回调本地系统', 'http://144.34.170.115:8090/api/payOrder?orderNo=', '7'), ('1509203258659307520', 'FastPaymerchantOrderPayUrl', 'Fastpay支付界面', 'http://107.182.185.162:8089/FastPay?orderNo=', '1'), ('1509203710356488192', 'FastPayadminOrderPayUrl', 'FastPay回调返回success', 'http://107.182.185.162:8089/api/payOrder?orderNo=', '0'), ('1549042357192622080', 'BankQrOrderPayUrl', 'bankQr扫码支付地址', 'http://vn.tapcocos.com:8089/BankQr?orderNo=', '1'), ('1549042567906066432', 'BankQrReturnPayUrl', 'bankQr回调返回地址', 'http://vn.tapcocos.com:8089/api/payOrder?orderNo=', '1'), ('1562797016143101952', 'Pay247ReturnPayUrl', 'Pay247回调地址', 'http://144.34.170.115:8089/api/payOrder?orderNo=', '2'), ('1562797206954573824', 'Pay247merchantOrderPayUrl', 'Pay247 支付界面', 'http://144.34.170.115:8089/Pay247?orderNo=', '2');
COMMIT;

-- ----------------------------
--  Table structure for `customer_qrcode_setting`
-- ----------------------------
DROP TABLE IF EXISTS `customer_qrcode_setting`;
CREATE TABLE `customer_qrcode_setting` (
  `id` varchar(32) NOT NULL,
  `lately_update_time` datetime DEFAULT NULL,
  `qrcode_storage_ids` varchar(255) DEFAULT NULL,
  `customer_service_explain` varchar(255) DEFAULT NULL,
  `qrcode_storage_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `customer_qrcode_setting`
-- ----------------------------
BEGIN;
INSERT INTO `customer_qrcode_setting` VALUES ('1149007275684265984', '2022-04-21 22:55:54', null, '扫描以下二维码,添加客服的微信', '1455919986244583424');
COMMIT;

-- ----------------------------
--  Table structure for `dict_item`
-- ----------------------------
DROP TABLE IF EXISTS `dict_item`;
CREATE TABLE `dict_item` (
  `id` varchar(32) NOT NULL,
  `dict_item_code` varchar(255) DEFAULT NULL,
  `dict_item_name` varchar(255) DEFAULT NULL,
  `dict_type_id` varchar(32) DEFAULT NULL,
  `order_no` double DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjld3d2k4ap5lmg137axsc9r8m` (`dict_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `dict_item`
-- ----------------------------
BEGIN;
INSERT INTO `dict_item` VALUES ('1087722503586971648', '1', '充值', '1087722503477919744', '1', '0'), ('1087722503586971649', '2', '提现', '1087722503477919744', '1', '0'), ('1098859553321123840', '1', '启用', '1098859552956219392', '1', '0'), ('1098859553329512448', '0', '禁用', '1098859552956219392', '2', '0'), ('1105870811094319104', '1', '待支付', '1105870811018821632', '1', '0'), ('1105870811102707712', '2', '已支付', '1105870811018821632', '2', '0'), ('1105870811106902016', '3', '已结算', '1105870811018821632', '3', '0'), ('1105870811111096320', '4', '超时取消', '1105870811018821632', '4', '0'), ('1105870811111096321', '5', '人工取消', '1105870811018821632', '5', '0'), ('1118142200316690432', '1', '正在接单', '1118141051320664064', '1', '0'), ('1118142200362827776', '2', '停止接单', '1118141051320664064', '2', '0'), ('1121452767152439296', '1', '待处理', '1121066481761648640', '1', '0'), ('1121452767181799424', '2', '已完结', '1121066481761648640', '2', '0'), ('1121783158149218304', 'user', '用户', '1121782956281561088', '1', '0'), ('1121783158149218305', 'merchant', '商户', '1121782956281561088', '2', '0'), ('1142321526729605120', '1', '成功', '1142321345992851456', '1', '0'), ('1142321526729605121', '0', '失败', '1142321345992851456', '2', '0'), ('1142719322175569920', '1', '用户撤销申诉', '1121452882311249920', '1', '0'), ('1142719322179764224', '2', '改为实际支付金额', '1121452882311249920', '2', '0'), ('1142719322179764225', '3', '取消订单', '1121452882311249920', '3', '0'), ('1142719322183958528', '4', '不做处理', '1121452882311249920', '4', '0'), ('1142719322183958529', '5', '商户撤销申诉', '1121452882311249920', '5', '0'), ('1142719322183958530', '6', '确认已支付', '1121452882311249920', '6', '0'), ('1143919738011779072', '1', '未通知', '1143919592523956224', '1', '0'), ('1143919738011779073', '2', '通知成功', '1143919592523956224', '2', '0'), ('1143919738015973376', '3', '通知失败', '1143919592523956224', '3', '0'), ('1144914598286065664', 'member', '会员端', '1144553743279194112', '1', '0'), ('1144914598286065665', 'merchant', '商户端', '1144553743279194112', '2', '0'), ('1144914598290259968', 'admin', '后台管理', '1144553743279194112', '3', '0'), ('1144952530199904256', '1', '发起提现', '1099267380833419264', '1', '0'), ('1144952530208292864', '2', '审核通过', '1099267380833419264', '2', '0'), ('1144952530208292865', '3', '审核不通过', '1099267380833419264', '3', '0'), ('1144952530208292866', '4', '已到帐', '1099267380833419264', '4', '0'), ('1147146427277770752', '1', '未支付申请取消订单', '1120978122905223168', '1', '0'), ('1147146427281965056', '2', '实际支付金额小于收款金额', '1120978122905223168', '2', '0'), ('1147146427281965057', '4', '实际支付金额大于收款金额', '1120978122905223168', '3', '0'), ('1147146427281965058', '3', '已支付用户未进行确认', '1120978122905223168', '4', '0'), ('1147720702695047168', 'admin', '管理员', '1103178577832050688', '1', '0'), ('1147720702699241472', 'customerService', '客服', '1103178577832050688', '2', '0'), ('1147720702699241473', 'agent', '代理', '1103178577832050688', '3', '0'), ('1147720702699241474', 'member', '会员', '1103178577832050688', '4', '0'), ('1148590047675547648', '1', '开启中', '1148291148612108288', '1', '0'), ('1148590047675547649', '2', '关闭中', '1148291148612108288', '2', '0'), ('1148590047675547650', '3', '已禁用', '1148291148612108288', '3', '0'), ('1152801754782367744', 'bankCard', '银行卡', '1145273679853125632', '1', '0'), ('1152801754820116480', 'virtualWallet', '电子钱包', '1145273679853125632', '2', '0'), ('1154400519909801984', '1', '审核中', '1154400285704060928', '1', '0'), ('1154400519909801985', '2', '审核通过', '1154400285704060928', '2', '0'), ('1154400519909801986', '3', '审核不通过', '1154400285704060928', '3', '0'), ('1154400519913996288', '4', '已到账', '1154400285704060928', '4', '0'), ('1158360278958604288', '1', '新增', '1158360211107348480', '1', '0'), ('1158360278958604289', '2', '删除', '1158360211107348480', '2', '0'), ('1158374600329920512', '1', '正常', '1115665030281428992', '1', '0'), ('1158374600359280640', '2', '待审核', '1115665030281428992', '2', '0'), ('1158749831255031808', 'bankCard', '银行卡入款', '1145280661767061504', '1', '0'), ('1158749831263420416', 'virtualWallet', '电子钱包', '1145280661767061504', '2', '0'), ('1158749831263420417', 'thirdPartyPay', '第三方支付', '1145280661767061504', '3', '0'), ('1158749831263420418', 'gatheringCode', '收款码', '1145280661767061504', '4', '0'), ('1161278896860037120', '1', '账号充值', '1105159258481098752', '1', '0'), ('1161278896864231424', '2', '充值优惠', '1105159258481098752', '2', '0'), ('1161278896864231425', '3', '接单扣款', '1105159258481098752', '3', '0'), ('1161278896864231426', '4', '接单奖励金', '1105159258481098752', '4', '0'), ('1161278896864231427', '5', '账号提现', '1105159258481098752', '5', '0'), ('1161278896868425728', '6', '退还保证金', '1105159258481098752', '6', '0'), ('1161278896868425729', '7', '提现不符退款', '1105159258481098752', '7', '0'), ('1161278896868425730', '8', '手工增保证金', '1105159258481098752', '8', '0'), ('1161278896872620032', '9', '手工减保证金', '1105159258481098752', '9', '0'), ('1161278896872620033', '10', '改单为实际支付金额并退款', '1105159258481098752', '10', '0'), ('1161278896872620034', '11', '客服取消订单退款', '1105159258481098752', '11', '0'), ('1161278896872620035', '12', '奖励金返点', '1105159258481098752', '12', '0'), ('1161278896872620036', '13', '改单为实际支付金额并扣款', '1105159258481098752', '13', '0'), ('1161278896872620037', '14', '客服取消订单并扣除奖励金', '1105159258481098752', '14', '0'), ('1161278896876814336', '15', '客服取消订单并扣除返点', '1105159258481098752', '15', '0'), ('1161278896876814337', '16', '退还冻结金额', '1105159258481098752', '16', '0'), ('1528009098707075072', '1', '等待接单', '1098626328485167104', '1', '0'), ('1528009098723852288', '2', '已接单', '1098626328485167104', '2', '0'), ('1528009098723852289', '4', '已支付', '1098626328485167104', '3', '0'), ('1528009098723852290', '5', '超时取消', '1098626328485167104', '4', '0'), ('1528009098723852291', '6', '人工取消', '1098626328485167104', '5', '0'), ('1528009098723852292', '7', '未确认超时取消', '1098626328485167104', '6', '0'), ('1528009098723852293', '8', '申诉中', '1098626328485167104', '7', '0'), ('1528009098723852294', '9', '取消订单退款', '1098626328485167104', '8', '0'), ('1528009098723852295', '10', '代付成功', '1098626328485167104', '9', '0'), ('1538897737305554944', '0', '付款人员', '1538897487228567552', '1', '0'), ('1538897737414606848', '1', '不是付款人员', '1538897487228567552', '2', '0'), ('1547241627913814016', 'Pay247', 'Pay247', '1510623836679176192', '1', '0'), ('1547241627918008320', 'FastPay', 'FastPay', '1510623836679176192', '2', '0'), ('1547241627918008321', 'BankQr', 'BankQr', '1510623836679176192', '3', '0'), ('1547593534721228800', '1', '使用(Use)', '1464975387762098176', '1', '0'), ('1547593534721228801', '0', '停用(Stop)', '1464975387762098176', '2', '0'), ('1547595536180183040', '1', '正常(normal)', '1515372682667884544', '1', '0'), ('1547595536180183041', '0', '永久停用(Stop forever)', '1515372682667884544', '2', '0'), ('1547599576314150912', '1', '存款卡(Deposit)', '1462283426789851136', '1', '0'), ('1547599576314150913', '2', '付款卡(Payment)', '1462283426789851136', '2', '0'), ('1547599576314150914', '3', '备用金卡(Reserve)', '1462283426789851136', '3', '0');
COMMIT;

-- ----------------------------
--  Table structure for `dict_type`
-- ----------------------------
DROP TABLE IF EXISTS `dict_type`;
CREATE TABLE `dict_type` (
  `id` varchar(32) NOT NULL,
  `dict_type_code` varchar(255) DEFAULT NULL,
  `dict_type_name` varchar(255) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `dict_type`
-- ----------------------------
BEGIN;
INSERT INTO `dict_type` VALUES ('1087722503477919744', 'rechargeWithdrawLogOrderType', '充提日志订单类型', '1', null), ('1098626328485167104', 'merchantOrderState', '商户订单状态', '3', null), ('1098859552956219392', 'accountState', '账号状态', '0', null), ('1099267380833419264', 'withdrawRecordState', '提现记录状态', '24', ''), ('1103178577832050688', 'accountType', '账号类型', '0', null), ('1105159258481098752', 'accountChangeType', '账变类型', '0', null), ('1105870811018821632', 'rechargeOrderState', '充值订单状态', '0', null), ('1115665030281428992', 'gatheringCodeState', '收款码状态', '0', ''), ('1118141051320664064', 'receiveOrderState', '接单状态', '0', ''), ('1120978122905223168', 'appealType', '申诉类型', '0', ''), ('1121066481761648640', 'appealState', '申诉状态', '0', ''), ('1121452882311249920', 'appealProcessWay', '申诉处理方式', '0', ''), ('1121782956281561088', 'appealInitiatorObj', '申诉发起方', '1', ''), ('1142321345992851456', 'loginState', '登录状态', '0', ''), ('1143919592523956224', 'merchantOrderPayNoticeState', '商户订单支付通知状态', '0', ''), ('1144553743279194112', 'system', '系统', '0', ''), ('1145273679853125632', 'withdrawWay', '提现方式', '0', ''), ('1145280661767061504', 'payCategory', '支付类别', '0', ''), ('1148291148612108288', 'accountReceiveOrderChannelState', '账号接单通道状态', '1', ''), ('1154400285704060928', 'merchantSettlementState', '商户结算状态', '0', ''), ('1158360211107348480', 'gatheringCodeAuditType', '收款码审核类型', '0', ''), ('1462283426789851136', 'cardUseState', 'cardUseState', '0', '卡的使用状态卡用途 1.存款卡，2.付款卡，3.备用金卡，'), ('1464975387762098176', 'cardState', 'cardState', '0', '卡状态'), ('1510623836679176192', 'SystemSource', 'SystemSource', '0', '系统来源'), ('1515372682667884544', 'cardQixianState', '银行卡期限', '0', '1:正常,0:永久停止'), ('1538897487228567552', 'payerMark', '付款标记', '0', '0是付款人员，1不是付款人');
COMMIT;

-- ----------------------------
--  Table structure for `freeze_record`
-- ----------------------------
DROP TABLE IF EXISTS `freeze_record`;
CREATE TABLE `freeze_record` (
  `id` varchar(32) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `deal_time` datetime DEFAULT NULL,
  `merchant_order_id` varchar(32) DEFAULT NULL,
  `received_account_id` varchar(255) DEFAULT NULL,
  `useful_time` datetime DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `freeze_record`
-- ----------------------------
BEGIN;
INSERT INTO `freeze_record` VALUES ('1161280310780887040', '2019-08-13 22:16:16', '2019-08-13 22:17:42', '1161280016751788032', '1143184030414405632', '2019-08-13 22:17:16', '1'), ('1161280361171255296', '2019-08-13 22:16:28', '2019-08-13 22:18:22', '1161280081012719616', '1143184030414405632', '2019-08-13 22:17:28', '1'), ('1161287634467684352', '2019-08-13 22:45:22', '2019-08-13 22:47:34', '1161287338643423232', '1143184030414405632', '2019-08-13 22:47:22', '1'), ('1161287760250667008', '2019-08-13 22:45:52', '2019-08-13 22:48:14', '1161287424135921664', '1143184030414405632', '2019-08-13 22:47:52', '1'), ('1161621739340300288', '2019-08-14 20:52:59', '2019-08-14 20:55:39', '1161324694838706176', '1143184030414405632', '2019-08-14 20:54:59', '1'), ('1161621739386437632', '2019-08-14 20:52:59', '2019-08-14 20:56:19', '1161325118236917760', '1143184030414405632', '2019-08-14 20:54:59', '1'), ('1161627195194474496', '2019-08-14 21:14:40', '2019-08-14 21:16:16', '1161626879917031424', '1143184030414405632', '2019-08-14 21:15:40', '1'), ('1161633929355067392', '2019-08-14 21:41:26', '2019-08-14 21:43:02', '1161633458229870592', '1143184030414405632', '2019-08-14 21:42:26', '1'), ('1161634982557384704', '2019-08-14 21:45:37', '2019-08-14 21:46:09', '1161634711290773504', '1143184030414405632', '2019-08-14 21:46:37', '1');
COMMIT;

-- ----------------------------
--  Table structure for `gathering_channel`
-- ----------------------------
DROP TABLE IF EXISTS `gathering_channel`;
CREATE TABLE `gathering_channel` (
  `id` varchar(32) NOT NULL,
  `channel_code` varchar(255) DEFAULT NULL,
  `channel_name` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `deleted_flag` bit(1) DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `default_receive_order_rabate` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `gathering_channel`
-- ----------------------------
BEGIN;
INSERT INTO `gathering_channel` VALUES ('1147772293259198464', 'wechat', '微信', '2019-07-07 15:40:14', b'0', b'1', '6', '1.5'), ('1149357542279741440', 'alipay', '支付宝', '2019-07-12 00:39:27', b'0', b'1', '3', '1.5'), ('1149365394574671872', 'bankcard', '银行卡', '2019-07-12 01:10:39', b'0', b'1', '0', '1.2'), ('1157697465537789952', 'unionpay', '云闪付', '2019-08-04 00:59:19', b'0', b'1', '0', '1.2'), ('1547241032293285888', 'UnionPayScan', '银联扫码', '2022-07-13 22:26:23', b'0', b'1', '0', '14');
COMMIT;

-- ----------------------------
--  Table structure for `gathering_channel_rate`
-- ----------------------------
DROP TABLE IF EXISTS `gathering_channel_rate`;
CREATE TABLE `gathering_channel_rate` (
  `id` varchar(32) NOT NULL,
  `channel_id` varchar(32) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `merchant_id` varchar(32) DEFAULT NULL,
  `rate` double DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `gathering_channel_rate`
-- ----------------------------
BEGIN;
INSERT INTO `gathering_channel_rate` VALUES ('1564990640708648960', '1149365394574671872', '2022-08-31 22:57:00', b'1', '1564984584259502080', '1.4', '0'), ('1564990733394378752', '1149365394574671872', '2022-08-31 22:57:22', b'1', '1564982426864713728', '1.3', '0'), ('1564990779431059456', '1547241032293285888', '2022-08-31 22:57:33', b'1', '1547242859969642496', '1.5', '0');
COMMIT;

-- ----------------------------
--  Table structure for `gathering_channel_rebate`
-- ----------------------------
DROP TABLE IF EXISTS `gathering_channel_rebate`;
CREATE TABLE `gathering_channel_rebate` (
  `id` varchar(32) NOT NULL,
  `channel_id` varchar(32) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `rebate` double DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `gathering_code`
-- ----------------------------
DROP TABLE IF EXISTS `gathering_code`;
CREATE TABLE `gathering_code` (
  `id` varchar(32) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `gathering_amount` double DEFAULT NULL COMMENT '收款金额',
  `gathering_channel_code` varchar(255) DEFAULT NULL COMMENT '渠道名称',
  `payee` varchar(255) DEFAULT NULL COMMENT '收款人',
  `state` varchar(255) DEFAULT NULL,
  `user_account_id` varchar(32) DEFAULT NULL COMMENT '用户ID号',
  `storage_id` varchar(32) DEFAULT NULL,
  `fixed_gathering_amount` bit(1) DEFAULT NULL,
  `gathering_channel_id` varchar(32) DEFAULT NULL,
  `in_use` bit(1) DEFAULT NULL,
  `audit_type` varchar(255) DEFAULT NULL,
  `initiate_audit_time` datetime DEFAULT NULL,
  `bank_code` varchar(100) DEFAULT NULL COMMENT '银行卡卡号',
  `bank_address` varchar(255) DEFAULT NULL COMMENT '银行名称',
  `bank_username` varchar(30) DEFAULT NULL COMMENT '卡姓名',
  `bank_total_amount` double(255,0) DEFAULT NULL COMMENT '银行卡总金额',
  `bank_reflect` double(255,0) DEFAULT NULL COMMENT '银行卡额度限制',
  `card_use` varchar(255) DEFAULT NULL COMMENT '卡用途 1.存款卡，2.付款卡，3.备用金卡',
  `daily_quota` varchar(555) DEFAULT NULL COMMENT '单日限额',
  `qi_xian_user` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `gathering_code`
-- ----------------------------
BEGIN;
INSERT INTO `gathering_code` VALUES ('1564989014912532480', '2022-08-31 22:50:32', null, null, '张三', '1', '1564988076063719424', null, b'0', '1149365394574671872', b'1', null, null, '11111111', '建设银行', '张三', '100', '500000000', '1', '50000000', '1'), ('1565000969631039488', '2022-08-31 23:38:02', null, null, '王大勇', '1', '1565000370717982720', null, b'0', '1149365394574671872', b'1', null, null, '2222', '北京支行', '王大勇', '200', '200000', '1', '100000', '1'), ('1565299393777631232', '2022-09-01 19:23:52', null, null, '红大炮', '1', '1547241827894034432', '1565299392154435584', b'0', '1547241032293285888', b'1', null, null, '11111', '建设银行', '红大炮', '0', '0', '1', '9999999', '1');
COMMIT;

-- ----------------------------
--  Table structure for `inside_withdraw_record`
-- ----------------------------
DROP TABLE IF EXISTS `inside_withdraw_record`;
CREATE TABLE `inside_withdraw_record` (
  `id` varchar(32) NOT NULL,
  `account_holder` varchar(255) DEFAULT NULL,
  `bank_card_account` varchar(255) DEFAULT NULL,
  `approval_time` datetime DEFAULT NULL,
  `open_account_bank` varchar(255) DEFAULT NULL,
  `order_no` varchar(255) DEFAULT NULL COMMENT '订单号',
  `state` varchar(255) DEFAULT NULL COMMENT '提现状态',
  `submit_time` datetime DEFAULT NULL,
  `withdraw_amount` double DEFAULT NULL COMMENT '收款金额',
  `confirm_credited_time` datetime DEFAULT NULL COMMENT '确认时间',
  `note` varchar(255) DEFAULT NULL COMMENT '备注',
  `virtual_wallet_addr` varchar(255) DEFAULT NULL,
  `withdraw_way` varchar(255) DEFAULT NULL COMMENT '提现方式',
  `virtual_wallet_type` varchar(255) DEFAULT NULL,
  `gathering_code_id` varchar(255) DEFAULT NULL COMMENT '收款码',
  `service_charge` varchar(255) DEFAULT NULL COMMENT '收款手续费',
  `shoukuan_number` varchar(255) DEFAULT NULL COMMENT '收款银行卡号',
  `bank_total` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `invite_code`
-- ----------------------------
DROP TABLE IF EXISTS `invite_code`;
CREATE TABLE `invite_code` (
  `id` varchar(32) NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `period_of_validity` datetime DEFAULT NULL,
  `inviter_id` varchar(32) DEFAULT NULL,
  `account_type` varchar(255) DEFAULT NULL,
  `used` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `invite_code_channel_rebate`
-- ----------------------------
DROP TABLE IF EXISTS `invite_code_channel_rebate`;
CREATE TABLE `invite_code_channel_rebate` (
  `id` varchar(32) NOT NULL,
  `invite_code_id` varchar(32) DEFAULT NULL,
  `rebate` double DEFAULT NULL,
  `channel_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `login_log`
-- ----------------------------
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log` (
  `id` varchar(32) NOT NULL,
  `browser` varchar(255) DEFAULT NULL,
  `ip_addr` varchar(255) DEFAULT NULL,
  `login_location` varchar(255) DEFAULT NULL,
  `login_time` datetime DEFAULT NULL,
  `msg` varchar(255) DEFAULT NULL,
  `os` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `system_info` varchar(255) DEFAULT NULL COMMENT '系统信息',
  `last_access_time` datetime DEFAULT NULL,
  `session_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `login_log`
-- ----------------------------
BEGIN;
INSERT INTO `login_log` VALUES ('1563886755294216192', 'Chrome', '0:0:0:0:0:0:0:1', '127.0.0.1', '2022-08-28 21:50:33', '登录成功', 'OSX', '1', 'system', null, 'admin', '2022-08-28 22:35:15', '8bef4d76-9518-44d5-a8f8-c125d8a2ecc6'), ('1563890284268355584', 'Chrome', '0:0:0:0:0:0:0:1', '127.0.0.1', '2022-08-28 22:04:34', '用户名或密码不正确', 'OSX', '0', 'AG005', null, 'merchant', null, null), ('1563890311745241088', 'Chrome', '0:0:0:0:0:0:0:1', '127.0.0.1', '2022-08-28 22:04:41', '用户名或密码不正确', 'OSX', '0', 'AG005', null, 'merchant', null, null), ('1563890412630835200', 'Chrome', '0:0:0:0:0:0:0:1', '127.0.0.1', '2022-08-28 22:05:05', '登录成功', 'OSX', '1', 'AG005', null, 'merchant', '2022-08-28 22:07:00', '01b4e9e3-ba5b-4024-9d50-516d1f980c0f'), ('1563898017688322048', 'Chrome', '0:0:0:0:0:0:0:1', '127.0.0.1', '2022-08-28 22:35:18', '登录成功', 'OSX', '1', 'system', null, 'admin', '2022-08-28 23:05:21', '4c1b6e78-0f4c-4b32-adb7-76dce7e52d99'), ('1563905602663481344', 'Chrome', '0:0:0:0:0:0:0:1', '127.0.0.1', '2022-08-28 23:05:26', '登录成功', 'Android', '1', 'system', null, 'admin', '2022-08-29 00:39:08', 'ae9a1803-6e42-4702-973c-2c9571801e49'), ('1564169534057742336', 'Chrome', '0:0:0:0:0:0:0:1', '127.0.0.1', '2022-08-29 16:34:12', '登录成功', 'OSX', '1', 'system', null, 'admin', '2022-08-29 16:38:18', '9af964d0-7b33-49cb-b175-da90e117a472'), ('1564170568343748608', 'Chrome', '0:0:0:0:0:0:0:1', '127.0.0.1', '2022-08-29 16:38:19', '登录成功', 'OSX', '1', 'system', null, 'admin', '2022-08-29 16:42:10', '6dbaa898-3688-446e-8bab-689ec6980bf3'), ('1564171802068910080', 'Chrome', '0:0:0:0:0:0:0:1', '127.0.0.1', '2022-08-29 16:43:13', '登录成功', 'OSX', '1', 'system', null, 'admin', '2022-08-29 16:46:52', '3d242b74-b3cd-4c7e-8bb7-e7dbf47548b0'), ('1564172762103152640', 'Chrome', '0:0:0:0:0:0:0:1', '127.0.0.1', '2022-08-29 16:47:02', '登录成功', 'OSX', '1', 'system', null, 'admin', '2022-08-29 16:55:30', '87916c57-7179-4f76-99ab-ebbdf3366219'), ('1564176182864248832', 'Chrome', '0:0:0:0:0:0:0:1', '127.0.0.1', '2022-08-29 17:00:38', '登录成功', 'Android', '1', 'system', null, 'admin', '2022-08-29 17:03:12', 'ac6cf49a-b8e9-4b46-9887-8c25961b8704'), ('1564176878506344448', 'Chrome', '0:0:0:0:0:0:0:1', '127.0.0.1', '2022-08-29 17:03:23', '登录成功', 'Android', '1', 'system', null, 'admin', '2022-08-29 17:07:09', '1f86f657-7d07-4566-8cf1-f03e774fddfa'), ('1564177861940609024', 'Chrome', '0:0:0:0:0:0:0:1', '127.0.0.1', '2022-08-29 17:07:18', '登录成功', 'Android', '1', 'system', null, 'admin', '2022-08-29 17:18:46', '568acae6-80fc-4132-b649-fb48d810e5cd'), ('1564597145351225344', 'Chrome', '0:0:0:0:0:0:0:1', '127.0.0.1', '2022-08-30 20:53:23', '登录成功', 'OSX', '1', 'system', null, 'admin', '2022-08-30 21:03:19', 'a80cb75d-f942-403b-bf17-4cc2b8db4f84'), ('1564981556437581824', 'Chrome', '0:0:0:0:0:0:0:1', '127.0.0.1', '2022-08-31 22:20:54', '登录成功', 'OSX', '1', 'system', null, 'admin', '2022-08-31 23:42:47', '61c98b12-8f37-4d20-8194-5a8145626ceb'), ('1565286774840754176', 'Chrome', '0:0:0:0:0:0:0:1', '127.0.0.1', '2022-09-01 18:33:43', '登录成功', 'OSX', '1', 'system', null, 'admin', '2022-09-01 18:34:28', 'c3364a5b-f1ae-4823-9f10-4b8d111dd48f'), ('1565296303846457344', 'Chrome', '130.105.160.36', '127.0.0.1', '2022-09-01 19:11:35', '登录成功', 'OSX', '1', 'system', null, 'admin', '2022-09-01 20:22:24', '605f7b00-fb7c-4551-9e04-0a0c8bd034ad'), ('1565297362765611008', 'Chrome', '130.105.160.36', '127.0.0.1', '2022-09-01 19:15:48', '登录成功', 'OSX', '1', 'Pay247006', null, 'merchant', '2022-09-01 20:21:30', 'b8b261d2-c12f-4fb3-9403-3a948ed51009');
COMMIT;

-- ----------------------------
--  Table structure for `merchant`
-- ----------------------------
DROP TABLE IF EXISTS `merchant`;
CREATE TABLE `merchant` (
  `id` varchar(32) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `secret_key` varchar(255) DEFAULT NULL COMMENT '密钥',
  `version` bigint DEFAULT NULL,
  `merchant_num` varchar(255) DEFAULT NULL COMMENT '商户号',
  `login_pwd` varchar(255) DEFAULT NULL COMMENT '登录密码',
  `user_name` varchar(255) DEFAULT NULL COMMENT '用户名',
  `lately_login_time` datetime DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `merchant_name` varchar(255) DEFAULT NULL COMMENT '接入商户名称',
  `deleted_flag` bit(1) DEFAULT NULL,
  `withdrawable_amount` double DEFAULT NULL COMMENT '可提现金额',
  `money_pwd` varchar(255) DEFAULT NULL COMMENT '资金密码',
  `notify_url` varchar(255) DEFAULT NULL,
  `return_url` varchar(255) DEFAULT NULL,
  `ip_whitelist` varchar(255) DEFAULT NULL COMMENT 'ip白名单',
  `payout_rate` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `merchant`
-- ----------------------------
BEGIN;
INSERT INTO `merchant` VALUES ('1547242859969642496', '2022-07-13 22:33:39', '8acc080e6cdcd7ae323a907c80939054', '7', 'bankqr001', '$2a$10$FEMMcVJA2VNVw237v3GhN.8m04d0DUa12jAR/gTX3Xe8WUpSd1oE2', 'bankqr', '2022-07-13 22:37:49', '1', 'bankqr001', b'0', '0', '$2a$10$a18MfCDt18JzvFU5g6yDaevEdDE0ifK1xUYXMSXlbBVaxtgcryhba', 'https://pay.payali999999999.net/oln/callback/BankRr/19', 'https://pay.payali999999999.net/oln/callback/BankRr/19', '127.0.0.1;115.85.17.131;122.55.108.34;112.206.149.9;115.146.182.130;222.127.144.113;61.28.174.60;121.127.22.130;115.84.241.150;210.5.95.222;34.96.181.246;130.105.160.78;0:0:0:0:0:0:0:1;144.34.170.115;130.105.160.36', '0.003'), ('1564982426864713728', '2022-08-31 22:24:21', 'c80b330112361f96d80878be6e7d3267', '4', 'FastPay112', '$2a$10$O4GR0HyKngEhRMsH5m8Q0.WjzvbTNuapUMkOOOmKysxBI76rryrXa', 'FastPay112', null, '1', 'FastPay112', b'0', '197.4', '$2a$10$O4GR0HyKngEhRMsH5m8Q0.WjzvbTNuapUMkOOOmKysxBI76rryrXa', 'https://pay.payali999999999.net/oln/callback/FastPay/1', 'https://pay.payali999999999.net/oln/callback/FastPay/1', '115.85.17.131;122.55.108.34;112.206.149.9;115.146.182.130;222.127.144.113;61.28.174.60;121.127.22.130;115.84.241.150;210.5.95.222;34.96.181.246;130.105.160.78;130.105.160.36;0:0:0:0:0:0:0:1;144.34.170.115;130.105.160.36', '0.003'), ('1564984584259502080', '2022-08-31 22:32:56', '7abc0aa16156aec413e6432f8db93079', '5', 'Pay247006', '$2a$10$vQNzTVeXqIMk7graOgzZje/5sG3enpcdQbtGuUBpb.xmpDowOxKqe', 'Pay247006', '2022-09-01 19:15:48', '1', 'Pay247006', b'0', '98.6', '$2a$10$vQNzTVeXqIMk7graOgzZje/5sG3enpcdQbtGuUBpb.xmpDowOxKqe', 'https://pay.payali999999999.net/oln/callback/Pay247/1', 'https://pay.payali999999999.net/oln/callback/Pay247/1', '115.85.17.131;122.55.108.34;112.206.149.9;115.146.182.130;222.127.144.113;61.28.174.60;121.127.22.130;115.84.241.150;210.5.95.222;34.96.181.246;130.105.10.37;0:0:0:0:0:0:0:1;144.34.170.115;130.105.160.36', '0.003');
COMMIT;

-- ----------------------------
--  Table structure for `merchant_bank_card`
-- ----------------------------
DROP TABLE IF EXISTS `merchant_bank_card`;
CREATE TABLE `merchant_bank_card` (
  `id` varchar(32) NOT NULL,
  `account_holder` varchar(255) DEFAULT NULL,
  `bank_card_account` varchar(255) DEFAULT NULL,
  `bank_info_lately_modify_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `deleted_flag` bit(1) DEFAULT NULL,
  `merchant_id` varchar(32) DEFAULT NULL,
  `open_account_bank` varchar(255) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `deleted_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `merchant_order`
-- ----------------------------
DROP TABLE IF EXISTS `merchant_order`;
CREATE TABLE `merchant_order` (
  `id` varchar(32) NOT NULL,
  `gathering_amount` double DEFAULT NULL,
  `gathering_channel_code` varchar(255) DEFAULT NULL,
  `order_no` varchar(255) DEFAULT NULL,
  `submit_time` datetime DEFAULT NULL,
  `order_state` varchar(255) DEFAULT NULL,
  `received_account_id` varchar(255) DEFAULT NULL,
  `received_time` datetime DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `confirm_time` datetime DEFAULT NULL,
  `platform_confirm_time` datetime DEFAULT NULL,
  `bounty` double DEFAULT NULL,
  `bounty_settlement_time` datetime DEFAULT NULL,
  `deal_time` datetime DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `useful_time` datetime DEFAULT NULL,
  `merchant_id` varchar(32) DEFAULT NULL,
  `pay_info_id` varchar(32) DEFAULT NULL,
  `rate` double DEFAULT NULL,
  `rebate` double DEFAULT NULL,
  `gathering_channel_id` varchar(32) DEFAULT NULL COMMENT '渠道ID号',
  `specified_received_account_id` varchar(255) DEFAULT NULL,
  `payee` varchar(255) DEFAULT NULL COMMENT '收款人',
  `gathering_code_id` varchar(255) DEFAULT NULL COMMENT '渠道收款码ID号',
  `gathering_code_storage_id` varchar(255) DEFAULT NULL,
  `out_order_no` varchar(255) DEFAULT NULL,
  `system_source` varchar(255) DEFAULT NULL COMMENT '系统来源',
  `pay_fukuan_name` varchar(52) DEFAULT NULL COMMENT '付款人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `merchant_order`
-- ----------------------------
BEGIN;
INSERT INTO `merchant_order` VALUES ('1564997767141523456', '100', null, '1564997767141523456', '2022-08-31 23:25:19', '4', '1564988076063719424', '2022-08-31 23:26:47', '1', '2022-08-31 23:27:35', null, '1.4', null, '2022-08-31 23:27:35', '操作人[system]ok', '2022-09-01 00:55:19', '1564984584259502080', '1564997770874454016', '1.4', '1.4', '1149365394574671872', null, '张三', '1564989014912532480', null, 'A02222', 'Pay247', null), ('1565001751457693696', '200', null, '1565001751457693696', '2022-08-31 23:41:09', '4', '1565000370717982720', '2022-08-31 23:41:10', '1', '2022-08-31 23:41:37', null, '2.6', null, '2022-08-31 23:41:37', '操作人[system]ok', '2022-09-01 01:11:09', '1564982426864713728', '1565001751457693697', '1.3', '1.3', '1149365394574671872', null, '王大勇', '1565000969631039488', null, 'A0222211', 'FastPay', null), ('1565297070800109568', '100', null, '1565297070800109568', '2022-09-01 19:14:38', '2', '1564988076063719424', '2022-09-01 19:14:38', '0', null, null, null, null, null, null, '2022-09-01 20:44:38', '1564984584259502080', '1565297070850441216', '1.4', '1.4', '1149365394574671872', null, '张三', '1564989014912532480', null, 'A0222', 'Pay247', null), ('1565297925808979968', '100', null, '1565297925808979968', '2022-09-01 19:18:02', '2', '1564988076063719424', '2022-09-01 19:18:02', '0', null, null, null, null, null, null, '2022-09-01 20:48:02', '1564984584259502080', '1565297925808979969', '1.4', '1.4', '1149365394574671872', null, '张三', '1564989014912532480', null, 'A0222ddd', 'Pay247', null), ('1565298406753042432', '100', null, '1565298406753042432', '2022-09-01 19:19:57', '2', '1565000370717982720', '2022-09-01 19:19:57', '0', null, null, null, null, null, null, '2022-09-01 20:49:57', '1564982426864713728', '1565298406753042433', '1.3', '1.3', '1149365394574671872', null, '王大勇', '1565000969631039488', null, 'Akk', 'FastPay', null), ('1565307944885551104', '100', null, '1565307944885551104', '2022-09-01 19:57:51', '2', '1547241827894034432', '2022-09-01 19:57:51', '0', null, null, null, null, null, null, '2022-09-01 21:27:51', '1547242859969642496', '1565307944889745408', '1.5', '15', '1547241032293285888', null, '红大炮', '1565299393777631232', '1565299392154435584', 'A0kkk', 'BankQr', null);
COMMIT;

-- ----------------------------
--  Table structure for `merchant_order_pay_info`
-- ----------------------------
DROP TABLE IF EXISTS `merchant_order_pay_info`;
CREATE TABLE `merchant_order_pay_info` (
  `id` varchar(32) NOT NULL,
  `amount` varchar(255) DEFAULT NULL,
  `attch` varchar(255) DEFAULT NULL,
  `merchant_num` varchar(255) DEFAULT NULL,
  `merchant_order_id` varchar(255) DEFAULT NULL,
  `notice_state` varchar(255) DEFAULT NULL,
  `notify_url` varchar(255) DEFAULT NULL,
  `pay_type` varchar(255) DEFAULT NULL,
  `return_url` varchar(255) DEFAULT NULL,
  `sign` varchar(255) DEFAULT NULL,
  `order_no` varchar(255) DEFAULT NULL,
  `notice_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `merchant_order_pay_info`
-- ----------------------------
BEGIN;
INSERT INTO `merchant_order_pay_info` VALUES ('1564997770874454016', '100.0', '557634', 'Pay247006', '1564997767141523456', '3', 'https://pay.payali999999999.net/oln/callback/Pay247/1', 'bankcard', 'http://66.112.219.47:8089/api/payOrder?orderNo=1564997767141523456', 'df13bc78fd77ed62cf6b861b7bb9f519', 'A02222', null), ('1565001751457693697', '200.0', 't11', 'FastPay112', '1565001751457693696', '3', 'https://pay.payali999999999.net/oln/callback/FastPay/1', 'bankcard', 'http://107.182.185.162:8089/api/payOrder?orderNo=1565001751457693696', '49c39683f4d35fa177ec643398caba60', 'A0222211', null), ('1565297070850441216', '100.0', 'aa', 'Pay247006', '1565297070800109568', '1', 'https://pay.payali999999999.net/oln/callback/Pay247/1', 'bankcard', 'http://66.112.219.47:8089/api/payOrder?orderNo=1565297070800109568', '8561437943cbf20147b91565f8fcc6ad', 'A0222', null), ('1565297925808979969', '100.0', '544452', 'Pay247006', '1565297925808979968', '1', 'https://pay.payali999999999.net/oln/callback/Pay247/1', 'bankcard', 'http://144.34.170.115:8089/api/payOrder?orderNo=1565297925808979968', '0e8a2df68a097b3dc3f97bbd3d1a79f4', 'A0222ddd', null), ('1565298406753042433', '100.0', '55', 'FastPay112', '1565298406753042432', '1', 'https://pay.payali999999999.net/oln/callback/FastPay/1', 'bankcard', 'http://107.182.185.162:8089/api/payOrder?orderNo=1565298406753042432', 'af9f035b686e9f6ea1b6b20e55e89bb5', 'Akk', null), ('1565307944889745408', '100.0', '652413', 'bankqr001', '1565307944885551104', '1', 'https://pay.payali999999999.net/oln/callback/BankRr/19', 'UnionPayScan', 'http://vn.tapcocos.com:8089/api/payOrder?orderNo=1565307944885551104', '99227228439545e2323e74b77b826919', 'A0kkk', null);
COMMIT;

-- ----------------------------
--  Table structure for `merchant_payout_order`
-- ----------------------------
DROP TABLE IF EXISTS `merchant_payout_order`;
CREATE TABLE `merchant_payout_order` (
  `id` varchar(32) NOT NULL,
  `gathering_amount` double DEFAULT NULL,
  `gathering_channel_code` varchar(255) DEFAULT NULL,
  `order_no` varchar(255) DEFAULT NULL,
  `submit_time` datetime DEFAULT NULL,
  `order_state` varchar(255) DEFAULT NULL,
  `received_account_id` varchar(255) DEFAULT NULL,
  `received_time` datetime DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `confirm_time` datetime DEFAULT NULL,
  `platform_confirm_time` datetime DEFAULT NULL,
  `bounty` double DEFAULT NULL,
  `bounty_settlement_time` datetime DEFAULT NULL,
  `deal_time` datetime DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `useful_time` datetime DEFAULT NULL,
  `merchant_id` varchar(32) DEFAULT NULL,
  `pay_info_id` varchar(32) DEFAULT NULL,
  `rate` double DEFAULT NULL,
  `rebate` double DEFAULT NULL,
  `gathering_channel_id` varchar(32) DEFAULT NULL COMMENT '渠道ID号',
  `specified_received_account_id` varchar(255) DEFAULT NULL,
  `payee` varchar(255) DEFAULT NULL COMMENT '收款人',
  `gathering_code_id` varchar(255) DEFAULT NULL COMMENT '渠道收款码ID号',
  `gathering_code_storage_id` varchar(255) DEFAULT NULL,
  `out_order_no` varchar(255) DEFAULT NULL,
  `system_source` varchar(255) DEFAULT NULL COMMENT '系统来源',
  `shouku_bank_payee` varchar(255) DEFAULT NULL COMMENT '收款人',
  `shouku_bank_number` varchar(255) DEFAULT NULL COMMENT '收款银行卡号',
  `shouku_bank_name` varchar(255) DEFAULT NULL COMMENT '收款银行名称',
  `shouku_bank_branch` varchar(255) DEFAULT NULL COMMENT '收款银行支行',
  `fuku_bank_number` varchar(255) DEFAULT NULL COMMENT '付款银行列表',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `merchant_payout_order`
-- ----------------------------
BEGIN;
INSERT INTO `merchant_payout_order` VALUES ('1565298206303059968', '100', null, '1565298206303059968', '2022-09-01 19:19:09', '2', '1490602001984126976', '2022-09-01 19:19:09', '0', null, null, '0.3', null, null, null, '2022-09-01 20:49:09', '1564984584259502080', null, '0.3', null, '1149365394574671872', null, null, null, null, 'A033hh', 'Pay247', '张三', '11111', '建设银行', '建设银行11', null);
COMMIT;

-- ----------------------------
--  Table structure for `merchant_settlement_payout_record`
-- ----------------------------
DROP TABLE IF EXISTS `merchant_settlement_payout_record`;
CREATE TABLE `merchant_settlement_payout_record` (
  `id` varchar(250) NOT NULL,
  `pay_cardno` varchar(255) DEFAULT NULL COMMENT '付款银行卡号',
  `server_charge` varchar(255) DEFAULT NULL COMMENT '付款手续费',
  `note` varchar(255) DEFAULT NULL COMMENT '备注',
  `merchant_settl_id` varchar(255) DEFAULT NULL COMMENT '提现表ID',
  `pay_amount` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `merchant_settlement_record`
-- ----------------------------
DROP TABLE IF EXISTS `merchant_settlement_record`;
CREATE TABLE `merchant_settlement_record` (
  `id` varchar(32) NOT NULL,
  `apply_time` datetime DEFAULT NULL,
  `approval_time` datetime DEFAULT NULL,
  `confirm_credited_time` datetime DEFAULT NULL,
  `merchant_bank_card_id` varchar(32) DEFAULT NULL COMMENT '商户银行卡ID号',
  `merchant_id` varchar(32) DEFAULT NULL COMMENT '商户ID',
  `note` varchar(255) DEFAULT NULL COMMENT '备注',
  `order_no` varchar(255) DEFAULT NULL COMMENT '订单号',
  `state` varchar(255) DEFAULT NULL COMMENT '状态',
  `withdraw_amount` double DEFAULT NULL COMMENT '提现金额',
  `pay_cardno` varchar(255) DEFAULT NULL COMMENT '付款银行卡号',
  `server_charge` varchar(255) DEFAULT NULL COMMENT '手续费',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `order_rebate`
-- ----------------------------
DROP TABLE IF EXISTS `order_rebate`;
CREATE TABLE `order_rebate` (
  `id` varchar(32) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `merchant_order_id` varchar(32) DEFAULT NULL,
  `rebate` double DEFAULT NULL,
  `rebate_account_id` varchar(32) DEFAULT NULL,
  `rebate_amount` double DEFAULT NULL,
  `settlement_time` datetime DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `available_flag` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `order_rebate`
-- ----------------------------
BEGIN;
INSERT INTO `order_rebate` VALUES ('1161141431063019520', '2019-08-13 13:04:25', '1160585620028915712', '0.5', '1143183939259596800', '0.22', '2019-08-13 13:04:26', '1', b'1'), ('1161141436716941312', '2019-08-13 13:04:26', '1160584925905158144', '0.5', '1143183939259596800', '0.055', '2019-08-13 13:04:27', '1', b'1'), ('1161141492371161088', '2019-08-13 13:04:39', '1161141399052091392', '0.5', '1143183939259596800', '0.055', '2019-08-13 13:04:40', '1', b'1'), ('1161146596625219584', '2019-08-13 13:24:56', '1161146476185780224', '0.5', '1143183939259596800', '1', '2019-08-13 13:24:57', '1', b'1'), ('1161146810123681792', '2019-08-13 13:25:47', '1161146783791841280', '0.5', '1143183939259596800', '0.6', '2019-08-13 13:25:48', '1', b'1'), ('1161148342558785536', '2019-08-13 13:31:53', '1161146964406960128', '0.5', '1143183939259596800', '0.61', '2019-08-13 13:31:54', '1', b'1'), ('1161148379086979072', '2019-08-13 13:32:01', '1161148193245757440', '0.5', '1143183939259596800', '0.9', '2019-08-13 13:32:02', '1', b'1'), ('1161324736924352512', '2019-08-14 01:12:48', '1161322896107241472', '0.5', '1143183939259596800', '0.165', '2019-08-14 01:12:49', '1', b'1'), ('1161324742829932544', '2019-08-14 01:12:50', '1161324583001784320', '0.5', '1143183939259596800', '0.165', '2019-08-14 01:12:51', '1', b'1'), ('1161633981435740160', '2019-08-14 21:41:38', '1161633458229870592', '0.5', '1143183939259596800', '0.115', '2019-08-14 21:41:39', '1', b'1'), ('1161635119493021696', '2019-08-14 21:46:09', '1161634711290773504', '0.5', '1143183939259596800', '0.06', '2019-08-14 21:46:10', '1', b'1'), ('1168347534955905024', '2019-09-02 10:18:54', '1164584747918163968', '0.5', '1143183939259596800', '2.22', '2019-09-02 10:18:55', '1', b'1'), ('1168347544565055488', '2019-09-02 10:18:56', '1164584159809634304', '0.5', '1143183939259596800', '1.11', '2019-09-02 10:18:57', '1', b'1'), ('1168366892868435968', '2019-09-02 11:35:49', '1168365120661749760', '0.5', '1143183939259596800', '0.075', '2019-09-02 11:35:50', '1', b'1');
COMMIT;

-- ----------------------------
--  Table structure for `pay_channel`
-- ----------------------------
DROP TABLE IF EXISTS `pay_channel`;
CREATE TABLE `pay_channel` (
  `id` varchar(32) NOT NULL,
  `account_holder` varchar(255) DEFAULT NULL,
  `bank_card_account` varchar(255) DEFAULT NULL,
  `bank_name` varchar(255) DEFAULT NULL,
  `channel_code` varchar(255) DEFAULT NULL,
  `channel_name` varchar(255) DEFAULT NULL,
  `pay_type_id` varchar(32) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `order_no` double DEFAULT NULL,
  `pay_platform_channel_code` varchar(255) DEFAULT NULL,
  `pay_platform_code` varchar(255) DEFAULT NULL,
  `pay_platform_name` varchar(255) DEFAULT NULL,
  `virtual_wallet_addr` varchar(255) DEFAULT NULL,
  `deleted_flag` bit(1) DEFAULT NULL,
  `deleted_time` datetime DEFAULT NULL,
  `gathering_code_storage_id` varchar(255) DEFAULT NULL,
  `gathering_code_type` varchar(255) DEFAULT NULL,
  `payee` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `pay_channel`
-- ----------------------------
BEGIN;
INSERT INTO `pay_channel` VALUES ('1142311660241813504', '张三', '333333', '农业银行', 'bankCard', '农业银行', '1142311455366840320', '2', '2019-06-22 14:01:38', b'0', '1', null, null, null, null, b'1', '2019-07-27 22:47:07', null, null, null), ('1142316147694108672', '李四', '343434', '中国银行', 'bankCard2', '中国银行', '1142311455366840320', '2', '2019-06-22 14:19:27', b'1', '2', null, null, null, null, b'0', null, null, null, null), ('1142316793981829120', '222', '222', '22', 'alipay', '支付宝', '1142311505216143360', '2', '2019-06-22 14:22:02', b'1', '1', 'alipay', 'abcyzf', 'abc云支付', null, b'1', '2019-07-27 22:47:12', null, null, null), ('1142332938247995392', '', '', '', 'wechat', '微信', '1142311539634601984', '3', '2019-06-22 15:26:11', b'1', '1', 'wxpay', 'abcyzf', 'abc云支付', null, b'1', '2019-07-27 22:47:14', null, null, null), ('1145286265000689664', '', '', '', '434', '4343', '1145278602611261440', '2', '2019-06-30 19:01:39', b'1', '2', '', '', '', 'https://unbug.github.io/codelf/#third-party', b'0', null, null, null, null), ('1155322038169108480', '', '', '', '111', '微信', '1155321930002202624', '0', '2019-07-28 11:40:13', b'1', '1', '1', '1', '1', '', b'0', null, null, null, null), ('1158760261641830400', '33', '33', '33', 'bankCard3', '中国英航33', '1142311455366840320', '1', '2019-08-06 23:22:30', b'1', '3', '', '', '', '', b'1', '2019-08-06 23:22:32', '', null, ''), ('1158760342528983040', '', '', '', 'wechat', '微信', '1158750194620170240', '0', '2019-08-06 23:22:49', b'1', '3', '', '', '', '', b'0', null, '1158760342021472256', null, 'aaa'), ('1158760494438285312', '', '', '', 'alipay', '支付宝', '1158750243307651072', '0', '2019-08-06 23:23:25', b'1', '4', '', '', '', '', b'0', null, '1158760493934968832', null, '44'), ('1203917660845244416', '', '', '', '123', '支付宝测试', '1203917416132771840', '0', '2019-12-09 14:01:53', b'1', '1', '', '', '', '', b'0', null, '1203917660270624768', null, '支付宝测试'), ('1455735942982664192', '张三', '9879878978978', '建设银行', 'bankCar2', '建设银行', '1142311455366840320', '0', '2021-11-03 11:17:50', b'1', '2', '', '', '', '', b'0', null, '', null, '');
COMMIT;

-- ----------------------------
--  Table structure for `pay_type`
-- ----------------------------
DROP TABLE IF EXISTS `pay_type`;
CREATE TABLE `pay_type` (
  `id` varchar(32) NOT NULL,
  `bank_card_flag` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `order_no` double DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `pay_category` varchar(255) DEFAULT NULL,
  `deleted_flag` bit(1) DEFAULT NULL,
  `deleted_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `pay_type`
-- ----------------------------
BEGIN;
INSERT INTO `pay_type` VALUES ('1142311455366840320', b'1', '银行卡', 'bankCard', '2', '5', '2019-06-22 14:00:49', b'1', 'bankCard', b'0', null), ('1142311505216143360', b'0', '支付宝', 'alipay', '4', '8', '2019-06-22 14:01:01', b'1', 'thirdPartyPay', b'1', '2019-07-27 22:47:18'), ('1142311539634601984', b'0', '微信', 'wechat', '3', '5', '2019-06-22 14:01:09', b'0', 'thirdPartyPay', b'1', '2019-07-27 22:47:20'), ('1145278602611261440', b'0', '钱包', 'virtualWallet', '1', '4', '2019-06-30 18:31:12', b'1', 'virtualWallet', b'0', null), ('1155127413919711232', null, '44', '44', '44', '1', '2019-07-27 22:46:51', b'1', 'bankCard', b'1', '2019-07-27 22:46:54'), ('1155321930002202624', null, '第三方', '第三方', '1', '2', '2019-07-28 11:39:48', b'1', 'thirdPartyPay', b'1', '2019-08-06 22:41:49'), ('1157691231472451584', null, '测试', '测试', '1', '1', '2019-08-04 00:34:33', b'1', 'bankCard', b'1', '2019-08-04 00:42:13'), ('1157691888132685824', null, '测试22', '测试22', '2', '1', '2019-08-04 00:37:10', b'1', 'bankCard', b'1', '2019-08-04 00:42:09'), ('1158750194620170240', null, '微信', 'wechat', '3', '0', '2019-08-06 22:42:30', b'1', 'gatheringCode', b'0', null), ('1158750243307651072', null, '支付宝', 'alipay', '4', '1', '2019-08-06 22:42:41', b'1', 'gatheringCode', b'1', '2019-08-06 23:48:02'), ('1203917416132771840', null, '支付宝', 'alipay', '4', '0', '2019-12-09 14:00:54', b'1', 'gatheringCode', b'0', null);
COMMIT;

-- ----------------------------
--  Table structure for `receive_order_setting`
-- ----------------------------
DROP TABLE IF EXISTS `receive_order_setting`;
CREATE TABLE `receive_order_setting` (
  `id` varchar(32) NOT NULL,
  `lately_update_time` datetime DEFAULT NULL,
  `unfixed_gathering_code_receive_order` bit(1) DEFAULT NULL,
  `order_pay_effective_duration` int DEFAULT NULL,
  `receive_order_upper_limit` int DEFAULT NULL,
  `cash_deposit_minimum_require` int DEFAULT NULL,
  `receive_order_effective_duration` int DEFAULT NULL,
  `cash_pledge` double DEFAULT NULL,
  `show_all_order` bit(1) DEFAULT NULL,
  `stop_start_and_receive_order` bit(1) DEFAULT NULL,
  `ban_receive_repeat_order` bit(1) DEFAULT NULL,
  `receive_order_sound` bit(1) DEFAULT NULL,
  `audit_gathering_code` bit(1) DEFAULT NULL,
  `open_auto_receive_order` bit(1) DEFAULT NULL,
  `freeze_effective_duration` int DEFAULT NULL,
  `freeze_model_enabled` bit(1) DEFAULT NULL,
  `unconfirmed_auto_freeze_duration` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `receive_order_setting`
-- ----------------------------
BEGIN;
INSERT INTO `receive_order_setting` VALUES ('1117370137402408960', '2022-04-22 19:33:45', b'1', '90', '50000002', '50', '90', '0', b'1', b'0', b'1', b'1', b'0', b'1', '1', b'1', '1');
COMMIT;

-- ----------------------------
--  Table structure for `recharge_order`
-- ----------------------------
DROP TABLE IF EXISTS `recharge_order`;
CREATE TABLE `recharge_order` (
  `id` varchar(32) NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `order_no` varchar(255) DEFAULT NULL,
  `order_state` varchar(255) DEFAULT NULL,
  `pay_time` datetime DEFAULT NULL,
  `recharge_amount` double DEFAULT NULL,
  `submit_time` datetime DEFAULT NULL,
  `user_account_id` varchar(32) DEFAULT NULL,
  `deal_time` datetime DEFAULT NULL,
  `useful_time` datetime DEFAULT NULL,
  `pay_url` varchar(1024) DEFAULT NULL,
  `settlement_time` datetime DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `actual_pay_amount` double DEFAULT NULL,
  `deposit_time` datetime DEFAULT NULL,
  `depositor` varchar(255) DEFAULT NULL,
  `pay_channel_id` varchar(32) DEFAULT NULL,
  `pay_category` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsv4xqo59a5iiedsq372mgpu50` (`user_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Table structure for `recharge_setting`
-- ----------------------------
DROP TABLE IF EXISTS `recharge_setting`;
CREATE TABLE `recharge_setting` (
  `id` varchar(32) NOT NULL,
  `lately_update_time` datetime DEFAULT NULL,
  `order_effective_duration` int DEFAULT NULL,
  `return_water_rate` double DEFAULT NULL,
  `return_water_rate_enabled` bit(1) DEFAULT NULL,
  `quick_input_amount` varchar(255) DEFAULT NULL,
  `recharge_explain` varchar(255) DEFAULT NULL,
  `recharge_lower_limit` double DEFAULT NULL,
  `recharge_upper_limit` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `recharge_setting`
-- ----------------------------
BEGIN;
INSERT INTO `recharge_setting` VALUES ('1117370098537988096', '2022-04-21 23:04:00', '120', '0', b'1', '2000,3000,5000,10000', '最低充值2000元,最高充值10000元.单次充值额度:2000~10000元', '2000', '10000');
COMMIT;

-- ----------------------------
--  Table structure for `register_setting`
-- ----------------------------
DROP TABLE IF EXISTS `register_setting`;
CREATE TABLE `register_setting` (
  `id` varchar(32) NOT NULL,
  `effective_duration` int DEFAULT NULL,
  `lately_update_time` datetime DEFAULT NULL,
  `invite_register_enabled` bit(1) DEFAULT NULL,
  `register_enabled` bit(1) DEFAULT NULL,
  `invite_code_effective_duration` int DEFAULT NULL,
  `agent_explain` varchar(255) DEFAULT NULL,
  `only_open_member_account` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `register_setting`
-- ----------------------------
BEGIN;
INSERT INTO `register_setting` VALUES ('1145172349390159872', '1000', '2019-08-12 00:05:57', b'1', b'1', '1000', '代理说明', b'0');
COMMIT;

-- ----------------------------
--  Table structure for `storage`
-- ----------------------------
DROP TABLE IF EXISTS `storage`;
CREATE TABLE `storage` (
  `id` varchar(32) NOT NULL,
  `associate_biz` varchar(255) DEFAULT NULL,
  `associate_id` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `file_size` bigint DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `upload_time` datetime DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `storage`
-- ----------------------------
BEGIN;
INSERT INTO `storage` VALUES ('1563888691632406528', null, null, '2022-07-13 00.14.14.jpg', '81487', 'image/jpeg', '2022-08-28 21:58:14', null, '0'), ('1563889001671163904', null, null, '2022-07-13 00.14.14.jpg', '81487', 'image/jpeg', '2022-08-28 21:59:28', null, '0'), ('1565299392154435584', 'gatheringCode', '1565299393777631232', '2022-07-13 00.14.14.jpg', '81487', 'image/jpeg', '2022-09-01 19:23:52', null, '1');
COMMIT;

-- ----------------------------
--  Table structure for `system_notice`
-- ----------------------------
DROP TABLE IF EXISTS `system_notice`;
CREATE TABLE `system_notice` (
  `id` varchar(32) NOT NULL,
  `content` varchar(2000) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `publish_time` datetime DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `system_notice`
-- ----------------------------
BEGIN;
INSERT INTO `system_notice` VALUES ('1455868879669559296', '<p><b>测试系统111111jklj可接受的封建时代1112222范德萨范德萨发鼎折覆餗333yuiyiuyiutyuttjjhjkhkjhkhkjh</b></p>', '2021-11-03 20:06:04', '2021-11-03 20:05:00', '测试系统', '7');
COMMIT;

-- ----------------------------
--  Table structure for `system_notice_mark_read_record`
-- ----------------------------
DROP TABLE IF EXISTS `system_notice_mark_read_record`;
CREATE TABLE `system_notice_mark_read_record` (
  `id` varchar(32) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `system_notice_id` varchar(255) DEFAULT NULL,
  `user_account_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `system_notice_mark_read_record`
-- ----------------------------
BEGIN;
INSERT INTO `system_notice_mark_read_record` VALUES ('1459155633944133632', '2021-11-12 21:46:28', '1455868879669559296', '1143183939259596800'), ('1491102264721408000', '2022-02-09 01:30:58', '1455868879669559296', '1490598801151361024');
COMMIT;

-- ----------------------------
--  Table structure for `system_setting`
-- ----------------------------
DROP TABLE IF EXISTS `system_setting`;
CREATE TABLE `system_setting` (
  `id` varchar(32) NOT NULL,
  `lately_update_time` datetime DEFAULT NULL,
  `website_title` varchar(255) DEFAULT NULL,
  `pay_technical_support` varchar(255) DEFAULT NULL,
  `currency_unit` varchar(255) DEFAULT NULL,
  `cash_pledge` double DEFAULT NULL,
  `show_ranking_list_flag` bit(1) DEFAULT NULL,
  `single_device_login_flag` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `system_setting`
-- ----------------------------
BEGIN;
INSERT INTO `system_setting` VALUES ('1145198241474674688', '2022-02-24 12:19:27', 'Pay247', 'Pay247', 'VND', '100', b'1', b'0');
COMMIT;

-- ----------------------------
--  Table structure for `user_account`
-- ----------------------------
DROP TABLE IF EXISTS `user_account`;
CREATE TABLE `user_account` (
  `id` varchar(32) NOT NULL,
  `lately_login_time` datetime DEFAULT NULL,
  `login_pwd` varchar(255) DEFAULT NULL,
  `money_pwd` varchar(255) DEFAULT NULL,
  `real_name` varchar(255) DEFAULT NULL,
  `registered_time` datetime DEFAULT NULL,
  `user_name` varchar(255) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `account_holder` varchar(255) DEFAULT NULL,
  `bank_card_account` varchar(255) DEFAULT NULL,
  `bank_info_lately_modify_time` datetime DEFAULT NULL,
  `open_account_bank` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `inviter_id` varchar(255) DEFAULT NULL,
  `account_type` varchar(255) DEFAULT NULL,
  `cash_deposit` double(255,2) DEFAULT NULL,
  `receive_order_state` varchar(255) DEFAULT NULL,
  `account_level` int DEFAULT NULL,
  `account_level_path` text,
  `rebate` double DEFAULT NULL,
  `virtual_wallet_addr` varchar(255) DEFAULT NULL,
  `virtual_wallet_lately_modify_time` datetime DEFAULT NULL,
  `deleted_flag` bit(1) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `invite_code_quota` int DEFAULT NULL,
  `ip_whitelist` varchar(555) DEFAULT NULL COMMENT 'ip白名单',
  `system_source` varchar(255) DEFAULT NULL COMMENT '系统来源',
  `payer_mark` varchar(255) DEFAULT NULL COMMENT '付款人员标记0是付款人员1不是付款人员',
  `payment_range` varchar(255) DEFAULT NULL COMMENT '付款范围',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `user_account`
-- ----------------------------
BEGIN;
INSERT INTO `user_account` VALUES ('1490602001984126976', '2022-07-17 06:54:22', '$2a$10$1vjhXVW0bFrSVn0PAtqSveZ6rpciPnl9vIlIHe4nF/WQS5Pb4oYnG', '$2a$10$bYeFOnV3NGbzu9Bfd7uY8.yJcn41P.vP4nKyP75geOq9BHTYOT2te', 'admin', '2022-02-07 16:23:06', 'admin', '845', null, null, null, null, '1', '1100805062633979904', 'admin', '0.00', '2', '1', '1100805062633979904.1490602001984126976', null, null, null, b'0', '11111', null, null, 'Pay247', '1', null), ('1491427950799618048', '2022-07-15 22:04:58', '$2a$10$1RYOLwY1uCcd90AJTCnGrOYXOJMraDy0jkHt8NxTto47lVad/Mww6', '$2a$10$wzCdlhhulTDbjFciux4xJ.yKqq5TO9xkhkemriI6kbmlsV4EH5vie', 'admin1', '2022-02-09 23:05:08', 'admin1', '501', null, null, null, null, '1', '1490602001984126976', 'admin', '0.00', '2', '2', '1100805062633979904.1490602001984126976.1491427950799618048', null, null, null, b'0', '133333', null, '217.138.201.92;84.17.57.69;185.134.22.93;37.19.205.185', 'Pay247', '1', null), ('1497941448367013888', '2022-04-02 08:52:27', '$2a$10$EgU2sMkFPcEKNNFCQjrrCeZamAj6Inj1gzYvyTj66Tj5zd0YVMipy', '$2a$10$1P7SocrIb9GzEDBD3.MsnO6IJ/QkZwtHbYWH.csgGLngW6z2RGGvy', 'check1', '2022-02-27 22:27:26', 'check1', '73', null, null, null, null, '1', '1490602001984126976', 'admin', '0.00', '2', '2', '1100805062633979904.1490602001984126976.1497941448367013888', null, null, null, b'0', '13552525', null, '130.105.10.238;45.87.213.20;45.87.213.24;217.138.201.92;84.17.57.69;185.134.22.93;37.19.205.185', 'Pay247', '1', null), ('1497941546870243328', '2022-07-15 23:50:22', '$2a$10$J5iWFN594o3l95WVwyL0zug/7nOIgp9/HLuSDPVRINklToFLqCvYi', '$2a$10$HGtVHC6FBqUlNUbrt1U.2O9aVTGSjw2AmA0qqkfndsrrzGmJAsuF2', 'check2', '2022-02-27 22:27:50', 'check2', '584', null, null, null, null, '1', '1490602001984126976', 'admin', '0.00', '2', '2', '1100805062633979904.1490602001984126976.1497941546870243328', null, null, null, b'0', '1454355', null, '130.105.10.238;45.87.213.20;45.87.213.24;217.138.201.92;84.17.57.69;185.134.22.93;37.19.205.185', 'Pay247', '1', null), ('1497942489858834432', '2022-07-16 07:48:27', '$2a$10$Ao1pcdBYSwaLjkQCIMH.f.9s44e4ECJoAoOIOnsDKToKjsloog75m', '$2a$10$R5DuuqwchQG/YbCciTqkb.fMcACiI6.MpU7P4ZHKsrAUiXEjszL7e', 'check3', '2022-02-27 22:31:35', 'check3', '158', null, null, null, null, '1', '1490602001984126976', 'admin', '0.00', '2', '2', '1100805062633979904.1490602001984126976.1497942489858834432', null, null, null, b'0', '2424242', null, '89.187.161.5;130.105.10.238;45.87.213.20;45.87.213.24;217.138.201.92;84.17.57.69;185.134.22.93;66.112.219.47;37.19.205.185', 'Pay247', '1', null), ('1498646348897976320', '2022-05-30 22:43:52', '$2a$10$.D53GJgIcgsTLiPDZaYkKuxWmvOeEbSGQPTrQI1gOFqcGquF8beQC', '$2a$10$.D53GJgIcgsTLiPDZaYkKuxWmvOeEbSGQPTrQI1gOFqcGquF8beQC', 'zohar001', '2022-03-01 21:08:28', 'zohar001', '390', null, null, null, null, '1', '1490602001984126976', 'admin', '0.00', '2', '2', '1100805062633979904.1490602001984126976.1498646348897976320', null, null, null, b'0', '34234234', null, null, 'Pay247', '1', null), ('1499774547090472960', '2022-07-17 00:18:00', '$2a$10$6yHCiQZe80BfmUckZ/ZXF.sacwp/bLYBXEmiyCZk0PNQ7qSVDlqky', '$2a$10$QdeNwknPu/2nIKqGYcQQAuHURcZEek5plJHaYJoaQYzzha0k/ZSW6', 'check4', '2022-03-04 23:51:31', 'check4', '150', null, null, null, null, '1', '1491427950799618048', 'admin', '0.00', '2', '3', '1100805062633979904.1490602001984126976.1491427950799618048.1499774547090472960', null, null, null, b'0', '123', null, '130.105.10.238;45.87.213.20;45.87.213.24;217.138.201.92;84.17.57.69;185.134.22.93;37.19.205.178;138.199.60.183;37.19.205.185', 'Pay247', '1', null), ('1499819722751868928', '2022-09-01 19:11:35', '$2a$10$eVQ/BeMbERuuxevq7cjCteQpLMqX5Fk2fRPiRoZibwVAGrannQr.S', '$2a$10$XOfh9MXl8RP9nsF.T9t9sO.1CR0zptAs5yVRV6jZTu41dmmjKEWdG', 'system', '2022-03-05 02:51:02', 'system', '372', null, null, null, null, '1', '1490602001984126976', 'admin', '0.00', '2', '2', '1100805062633979904.1490602001984126976.1499819722751868928', null, null, null, b'0', '4242', null, '', 'Pay247', '1', null), ('1508664178003410944', '2022-07-16 15:44:52', '$2a$10$SQ.8Umc9iDNQt5oIVkQlkuVy9kY964/zMZY.FdEl.KIUAHiz7b4ye', '$2a$10$mA8rO0Otr2VglLdhRnwvZOQ2gWU1OgQH507xFCOUufMVt4LIEwoVO', 'check5', '2022-03-29 12:35:44', 'check5', '138', null, null, null, null, '1', '1491427950799618048', 'admin', '0.00', '2', '3', '1100805062633979904.1490602001984126976.1491427950799618048.1508664178003410944', null, null, null, b'0', '332211', null, '89.187.161.5;130.105.10.238;45.87.213.20;45.87.213.24;217.138.201.92;84.17.57.69;185.134.22.93;66.112.219.47;37.19.205.185', 'Pay247', '1', null), ('1510791121184227328', null, '$2a$10$z1g7l46sbX4lfQovsXdquOIfCr4EKG7l22g0khAv6iQNTHx3BG7cm', '$2a$10$52iM5Z4YVCUys/idzJ5wsuunaOHBg3njDJfqyFjl6fuk.xKG5u7si', 'check6', '2022-04-04 09:27:27', 'check6', '4', null, null, null, null, '1', '1491427950799618048', 'admin', '0.00', '2', '3', '1100805062633979904.1490602001984126976.1491427950799618048.1510791121184227328', null, null, null, b'0', '6622266', null, '130.105.10.238;45.87.213.20;45.87.213.24;217.138.201.92;84.17.57.69;185.134.22.93;37.19.205.185', 'Pay247', '1', null), ('1510791670298312704', '2022-07-17 07:38:12', '$2a$10$TOjT2xn8w79vL.NJE4.CqeSgKAB.8vZ8ypbdUMhe1g0MS2L8NsnQO', '$2a$10$..002QlxQXiPgJkGGkzFqe/fC0iI5E06jgHWUavXpcwyiZSQDWy8m', 'check7', '2022-04-04 09:29:38', 'check7', '163', null, null, null, null, '1', '1491427950799618048', 'admin', '0.00', '2', '3', '1100805062633979904.1490602001984126976.1491427950799618048.1510791670298312704', null, null, null, b'0', '770077', null, '130.105.10.238;45.87.213.20;45.87.213.24;217.138.201.92;84.17.57.69;185.134.22.93;37.19.205.185', 'Pay247', '1', null), ('1528773990233931776', '2022-07-16 13:28:11', '$2a$10$AvkMGZXmnaAVOPkfwfijvuPSzZPSoFxiPsl8dqTPxTs1vdBGT88M.', '$2a$10$W5lzm/bQPvwpzceBjPMhY.jmjtLkm0H53s7w2U0f81LbMSB14fFVK', 'payout1', '2022-05-23 23:24:57', 'payout1', '178', null, null, null, null, '1', '1490602001984126976', 'admin', '0.00', '2', '2', '1100805062633979904.1490602001984126976.1528773990233931776', null, null, null, b'0', '42', null, '130.105.10.37;58.97.182.107', '', '0', '0_1000000000'), ('1528774473476472832', '2022-07-16 17:03:36', '$2a$10$ycDVyJkpG1MHCMdyrfb/R.4mXOI8MwoUrQL4GcGI7LYllLZLohHCi', '$2a$10$FTKvJJ6D/GeVXC1nxP1o9.yye4/zGDzGjwgfDchjN07ZsLWmG4tfO', 'payout2', '2022-05-23 23:26:52', 'payout2', '96', null, null, null, null, '1', '1490602001984126976', 'admin', '0.00', '2', '2', '1100805062633979904.1490602001984126976.1528774473476472832', null, null, null, b'0', '23423432', null, '58.97.182.107', '', '0', '0_1000000000'), ('1547241827894034432', null, '$2a$10$prTboKXVB1ETqFBk4pcy5ufqAvRCV13dlsUnu2VKnL7da9P8S74VC', '$2a$10$prTboKXVB1ETqFBk4pcy5ufqAvRCV13dlsUnu2VKnL7da9P8S74VC', 'bankqr', '2022-07-13 22:29:33', 'bankqr', '1', null, null, null, null, '1', '1499819722751868928', 'admin', '10000000000000000000.00', '1', '3', '1100805062633979904.1490602001984126976.1499819722751868928.1547241827894034432', null, null, null, b'0', '11111', null, '', 'BankQr', '1', null), ('1547482359702487040', '2022-07-16 22:16:34', '$2a$10$kJOA36H1darg4kXaJ8z5x.epHKSuRR9xl5zb94RXfDbNS18/YVoK6', '$2a$10$kJOA36H1darg4kXaJ8z5x.epHKSuRR9xl5zb94RXfDbNS18/YVoK6', 'payout3', '2022-07-14 14:25:20', 'payout3', '47', null, null, null, null, '1', '1491427950799618048', 'admin', '0.00', '2', '3', '1100805062633979904.1490602001984126976.1491427950799618048.1547482359702487040', null, null, null, b'0', '61116111', null, '58.97.182.107', '', '0', '0_1000000000'), ('1547483985783816192', '2022-07-17 05:01:53', '$2a$10$BvHqjg2P47x.PKzK4Pfgqet/ghECq6FX1FDmJDZBDjVDxcOQLEERi', '$2a$10$BvHqjg2P47x.PKzK4Pfgqet/ghECq6FX1FDmJDZBDjVDxcOQLEERi', 'payout4', '2022-07-14 14:31:48', 'payout4', '16', null, null, null, null, '1', '1491427950799618048', 'admin', '0.00', '2', '3', '1100805062633979904.1490602001984126976.1491427950799618048.1547483985783816192', null, null, null, b'0', '61111', null, '58.97.182.107', '', '0', '0_1000000000'), ('1564987803211661312', null, '$2a$10$WAi8XS.nLU/uKmCD3JFp/OtLJ2zUMZYvADgqCXezlgJIh47bTajRi', '$2a$10$WAi8XS.nLU/uKmCD3JFp/OtLJ2zUMZYvADgqCXezlgJIh47bTajRi', 'payoutAll', '2022-08-31 22:45:43', 'payoutAll', '0', null, null, null, null, '1', '1499819722751868928', 'admin', '0.00', '2', '3', '1100805062633979904.1490602001984126976.1499819722751868928.1564987803211661312', null, null, null, b'0', '2222', null, '111111', null, '0', null), ('1564988076063719424', null, '$2a$10$avc8agLAT.nK89lLZ9Y6L.69RX7qlxDCJG.hkho.fC7dD8AuYWCw2', '$2a$10$avc8agLAT.nK89lLZ9Y6L.69RX7qlxDCJG.hkho.fC7dD8AuYWCw2', 'Pay247006', '2022-08-31 22:46:48', 'Pay247006', '2', null, null, null, null, '1', '1499819722751868928', 'admin', '100000000000000000000000000000.00', '1', '3', '1100805062633979904.1490602001984126976.1499819722751868928.1564988076063719424', null, null, null, b'0', '11', null, '', 'Pay247', '', ''), ('1565000370717982720', null, '$2a$10$Ls.m0bfJc922y.rwSKrE8u/5mheTNr2PLr05VtBYHmqjVRh.MyqXG', '$2a$10$Ls.m0bfJc922y.rwSKrE8u/5mheTNr2PLr05VtBYHmqjVRh.MyqXG', 'FastPay112', '2022-08-31 23:35:39', 'FastPay112', '2', null, null, null, null, '1', '1499819722751868928', 'admin', '100000000000000000000000000.00', '1', '3', '1100805062633979904.1490602001984126976.1499819722751868928.1565000370717982720', null, null, null, b'0', '12', null, '', 'FastPay', '', '');
COMMIT;

-- ----------------------------
--  Table structure for `v_merchant_payout_trade_situation`
-- ----------------------------
DROP TABLE IF EXISTS `v_merchant_payout_trade_situation`;
CREATE TABLE `v_merchant_payout_trade_situation` (
  `id` varchar(255) COLLATE utf8_bin NOT NULL,
  `merchant_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `month_actual_income` double DEFAULT NULL,
  `month_order_num` bigint DEFAULT NULL,
  `month_paid_order_num` bigint DEFAULT NULL,
  `month_poundage` double DEFAULT NULL,
  `month_success_rate` double DEFAULT NULL,
  `month_trade_amount` double DEFAULT NULL,
  `today_actual_income` double DEFAULT NULL,
  `today_order_num` bigint DEFAULT NULL,
  `today_paid_order_num` bigint DEFAULT NULL,
  `today_poundage` double DEFAULT NULL,
  `today_success_rate` double DEFAULT NULL,
  `today_trade_amount` double DEFAULT NULL,
  `total_actual_income` double DEFAULT NULL,
  `total_order_num` bigint DEFAULT NULL,
  `total_paid_order_num` bigint DEFAULT NULL,
  `total_poundage` double DEFAULT NULL,
  `total_success_rate` double DEFAULT NULL,
  `total_trade_amount` double DEFAULT NULL,
  `yesterday_actual_income` double DEFAULT NULL,
  `yesterday_order_num` bigint DEFAULT NULL,
  `yesterday_paid_order_num` bigint DEFAULT NULL,
  `yesterday_poundage` double DEFAULT NULL,
  `yesterday_success_rate` double DEFAULT NULL,
  `yesterday_trade_amount` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;

-- ----------------------------
--  Table structure for `v_payout_trade_situation`
-- ----------------------------
DROP TABLE IF EXISTS `v_payout_trade_situation`;
CREATE TABLE `v_payout_trade_situation` (
  `total_trade_amount` double NOT NULL,
  `month_order_num` bigint DEFAULT NULL,
  `month_paid_order_num` bigint DEFAULT NULL,
  `month_success_rate` double DEFAULT NULL,
  `month_trade_amount` double DEFAULT NULL,
  `today_order_num` bigint DEFAULT NULL,
  `today_paid_order_num` bigint DEFAULT NULL,
  `today_success_rate` double DEFAULT NULL,
  `today_trade_amount` double DEFAULT NULL,
  `total_order_num` bigint DEFAULT NULL,
  `total_paid_order_num` bigint DEFAULT NULL,
  `total_success_rate` double DEFAULT NULL,
  `yesterday_order_num` bigint DEFAULT NULL,
  `yesterday_paid_order_num` bigint DEFAULT NULL,
  `yesterday_success_rate` double DEFAULT NULL,
  `yesterday_trade_amount` double DEFAULT NULL,
  PRIMARY KEY (`total_trade_amount`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin;

-- ----------------------------
--  Table structure for `virtual_wallet`
-- ----------------------------
DROP TABLE IF EXISTS `virtual_wallet`;
CREATE TABLE `virtual_wallet` (
  `id` varchar(32) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `deleted_flag` bit(1) DEFAULT NULL,
  `deleted_time` datetime DEFAULT NULL,
  `lately_modify_time` datetime DEFAULT NULL,
  `user_account_id` varchar(32) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `virtual_wallet_addr` varchar(255) DEFAULT NULL,
  `virtual_wallet_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `virtual_wallet`
-- ----------------------------
BEGIN;
INSERT INTO `virtual_wallet` VALUES ('1160308400316219392', '2019-08-11 05:54:15', b'1', '2019-08-11 05:55:38', '2019-08-11 05:54:15', '1143184030414405632', '1', '10081', 'btc'), ('1160308783730130944', '2019-08-11 05:55:46', b'0', null, '2019-08-11 05:55:46', '1143184030414405632', '0', '100086', 'btc'), ('1160322964776812544', '2019-08-11 06:52:07', b'0', null, '2019-08-11 06:52:07', '1143183939259596800', '0', '444', '44');
COMMIT;

-- ----------------------------
--  Table structure for `withdraw_record`
-- ----------------------------
DROP TABLE IF EXISTS `withdraw_record`;
CREATE TABLE `withdraw_record` (
  `id` varchar(32) NOT NULL,
  `account_holder` varchar(255) DEFAULT NULL,
  `bank_card_account` varchar(255) DEFAULT NULL,
  `approval_time` datetime DEFAULT NULL,
  `open_account_bank` varchar(255) DEFAULT NULL,
  `order_no` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `submit_time` datetime DEFAULT NULL,
  `user_account_id` varchar(32) DEFAULT NULL,
  `withdraw_amount` double DEFAULT NULL,
  `confirm_credited_time` datetime DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `virtual_wallet_addr` varchar(255) DEFAULT NULL,
  `withdraw_way` varchar(255) DEFAULT NULL,
  `virtual_wallet_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `withdraw_record`
-- ----------------------------
BEGIN;
INSERT INTO `withdraw_record` VALUES ('1461581785556058112', '434346', '3436', '2021-11-19 22:36:43', '3436', '1461581785556058112', '4', '2021-11-19 14:27:07', '1143183939259596800', '100', '2021-11-19 22:36:58', 'tt', null, 'bankCard', null);
COMMIT;

-- ----------------------------
--  Table structure for `withdraw_setting`
-- ----------------------------
DROP TABLE IF EXISTS `withdraw_setting`;
CREATE TABLE `withdraw_setting` (
  `id` varchar(32) NOT NULL,
  `lately_update_time` datetime DEFAULT NULL,
  `withdraw_explain` varchar(255) DEFAULT NULL,
  `withdraw_lower_limit` double DEFAULT NULL,
  `withdraw_upper_limit` double DEFAULT NULL,
  `everyday_withdraw_times_upper_limit` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
--  Records of `withdraw_setting`
-- ----------------------------
BEGIN;
INSERT INTO `withdraw_setting` VALUES ('1147500803427139584', '2022-04-21 23:04:11', '提款下限，10元/次;单笔上限999999999999999元，单日限额1000000元，单日提现次数限制5次', '50', '10000', '60');
COMMIT;

-- ----------------------------
--  View structure for `v_cash_deposit_bounty`
-- ----------------------------
DROP VIEW IF EXISTS `v_cash_deposit_bounty`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_cash_deposit_bounty` AS select sum(`tmp`.`total_cash_deposit`) AS `total_cash_deposit`,sum(`tmp`.`total_bounty`) AS `total_bounty`,sum(`tmp`.`month_bounty`) AS `month_bounty`,sum(`tmp`.`yesterday_bounty`) AS `yesterday_bounty`,sum(`tmp`.`today_bounty`) AS `today_bounty` from (select ifnull(round(sum(`ua`.`cash_deposit`),4),0) AS `total_cash_deposit`,0 AS `total_bounty`,0 AS `month_bounty`,0 AS `yesterday_bounty`,0 AS `today_bounty` from `user_account` `ua` where (`ua`.`deleted_flag` = 0) union all select 0 AS `total_cash_deposit`,ifnull(round(sum(`bounty`.`amount`),4),0) AS `total_bounty`,ifnull(round(sum((case when ((`bounty`.`settlement_time` >= str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s')) and (`bounty`.`settlement_time` < (str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s') + interval 1 month))) then `bounty`.`amount` else 0 end)),4),0) AS `month_bounty`,ifnull(round(sum((case when ((`bounty`.`settlement_time` >= (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval -(1) day)) and (`bounty`.`settlement_time` < str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s'))) then `bounty`.`amount` else 0 end)),4),0) AS `yesterday_bounty`,ifnull(round(sum((case when ((`bounty`.`settlement_time` >= str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s')) and (`bounty`.`settlement_time` < (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval 1 day))) then `bounty`.`amount` else 0 end)),4),0) AS `today_bounty` from (select `mo`.`bounty` AS `amount`,`mo`.`confirm_time` AS `settlement_time` from `merchant_order` `mo` where ((`mo`.`confirm_time` is not null) and (`mo`.`order_state` = '4')) union all select `rebate`.`rebate_amount` AS `amount`,`rebate`.`settlement_time` AS `settlement_time` from `order_rebate` `rebate` where (`rebate`.`settlement_time` is not null)) `bounty`) `tmp`;

-- ----------------------------
--  View structure for `v_gathering_code_usage`
-- ----------------------------
DROP VIEW IF EXISTS `v_gathering_code_usage`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_gathering_code_usage` AS select (case when (`tmp`.`total_order_num` = 0) then 0 else round(((`tmp`.`total_paid_order_num` / `tmp`.`total_order_num`) * 100),1) end) AS `total_success_rate`,(case when (`tmp`.`today_order_num` = 0) then 0 else round(((`tmp`.`today_paid_order_num` / `tmp`.`today_order_num`) * 100),1) end) AS `today_success_rate`,`tmp`.`id` AS `id`,`tmp`.`create_time` AS `create_time`,`tmp`.`gathering_amount` AS `gathering_amount`,`tmp`.`gathering_channel_code` AS `gathering_channel_code`,`tmp`.`payee` AS `payee`,`tmp`.`state` AS `state`,`tmp`.`user_account_id` AS `user_account_id`,`tmp`.`storage_id` AS `storage_id`,`tmp`.`fixed_gathering_amount` AS `fixed_gathering_amount`,`tmp`.`gathering_channel_id` AS `gathering_channel_id`,`tmp`.`in_use` AS `in_use`,`tmp`.`initiate_audit_time` AS `initiate_audit_time`,`tmp`.`audit_type` AS `audit_type`,`tmp`.`total_trade_amount` AS `total_trade_amount`,`tmp`.`total_paid_order_num` AS `total_paid_order_num`,`tmp`.`total_order_num` AS `total_order_num`,`tmp`.`today_trade_amount` AS `today_trade_amount`,`tmp`.`today_paid_order_num` AS `today_paid_order_num`,`tmp`.`today_order_num` AS `today_order_num`,`tmp`.`bank_code` AS `bank_code`,`tmp`.`bank_address` AS `bank_address`,`tmp`.`bank_username` AS `bank_username`,`tmp`.`bank_total_amount` AS `bank_total_amount`,`tmp`.`bank_reflect` AS `bank_reflect`,`tmp`.`card_use` AS `card_use`,`tmp`.`daily_quota` AS `daily_quota`,`tmp`.`qi_xian_user` AS `qi_xian_user` from (select `gc`.`id` AS `id`,`gc`.`create_time` AS `create_time`,`gc`.`gathering_amount` AS `gathering_amount`,`gc`.`gathering_channel_code` AS `gathering_channel_code`,`gc`.`payee` AS `payee`,`gc`.`state` AS `state`,`gc`.`user_account_id` AS `user_account_id`,`gc`.`storage_id` AS `storage_id`,`gc`.`fixed_gathering_amount` AS `fixed_gathering_amount`,`gc`.`gathering_channel_id` AS `gathering_channel_id`,`gc`.`in_use` AS `in_use`,`gc`.`initiate_audit_time` AS `initiate_audit_time`,`gc`.`audit_type` AS `audit_type`,`gc`.`bank_code` AS `bank_code`,`gc`.`bank_address` AS `bank_address`,`gc`.`bank_username` AS `bank_username`,`gc`.`bank_total_amount` AS `bank_total_amount`,`gc`.`bank_reflect` AS `bank_reflect`,`gc`.`card_use` AS `card_use`,`gc`.`daily_quota` AS `daily_quota`,`gc`.`qi_xian_user` AS `qi_xian_user`,ifnull(round(sum((case when (`mo`.`order_state` = '4') then `mo`.`gathering_amount` else 0 end)),4),0) AS `total_trade_amount`,ifnull(sum((case when (`mo`.`order_state` = '4') then 1 else 0 end)),0) AS `total_paid_order_num`,ifnull(sum((case when (`mo`.`order_state` is not null) then 1 else 0 end)),0) AS `total_order_num`,ifnull(round(sum((case when ((`mo`.`submit_time` >= str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s')) and (`mo`.`submit_time` < (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval 1 day)) and (`mo`.`order_state` = '4')) then `mo`.`gathering_amount` else 0 end)),4),0) AS `today_trade_amount`,ifnull(sum((case when ((`mo`.`submit_time` >= str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s')) and (`mo`.`submit_time` < (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval 1 day)) and (`mo`.`order_state` = '4')) then 1 else 0 end)),0) AS `today_paid_order_num`,ifnull(sum((case when ((`mo`.`submit_time` >= str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s')) and (`mo`.`submit_time` < (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval 1 day))) then 1 else 0 end)),0) AS `today_order_num` from ((`gathering_code` `gc` left join `merchant_order` `mo` on((`gc`.`id` = `mo`.`gathering_code_id`))) left join `gathering_channel` `channel` on((`gc`.`gathering_channel_id` = `channel`.`id`))) group by `gc`.`id`,`gc`.`create_time`,`gc`.`gathering_amount`,`gc`.`gathering_channel_code`,`gc`.`payee`,`gc`.`state`,`gc`.`user_account_id`,`gc`.`storage_id`,`gc`.`fixed_gathering_amount`,`gc`.`in_use`,`gc`.`gathering_channel_id`,`gc`.`initiate_audit_time`,`gc`.`audit_type`) `tmp`;

-- ----------------------------
--  View structure for `v_merchant_channel_trade_situation`
-- ----------------------------
DROP VIEW IF EXISTS `v_merchant_channel_trade_situation`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_merchant_channel_trade_situation` AS select concat(`tmp`.`merchant_id`,`tmp`.`gathering_channel_id`) AS `id`,`m`.`merchant_name` AS `merchant_name`,`gc`.`channel_name` AS `channel_name`,round((`tmp`.`total_trade_amount` - `tmp`.`total_poundage`),4) AS `total_actual_income`,round((`tmp`.`month_trade_amount` - `tmp`.`month_poundage`),4) AS `month_actual_income`,round((`tmp`.`yesterday_trade_amount` - `tmp`.`yesterday_poundage`),4) AS `yesterday_actual_income`,round((`tmp`.`today_trade_amount` - `tmp`.`today_poundage`),4) AS `today_actual_income`,(case when (`tmp`.`total_order_num` = 0) then 0 else round(((`tmp`.`total_paid_order_num` / `tmp`.`total_order_num`) * 100),1) end) AS `total_success_rate`,(case when (`tmp`.`month_order_num` = 0) then 0 else round(((`tmp`.`month_paid_order_num` / `tmp`.`month_order_num`) * 100),1) end) AS `month_success_rate`,(case when (`tmp`.`yesterday_order_num` = 0) then 0 else round(((`tmp`.`yesterday_paid_order_num` / `tmp`.`yesterday_order_num`) * 100),1) end) AS `yesterday_success_rate`,(case when (`tmp`.`today_order_num` = 0) then 0 else round(((`tmp`.`today_paid_order_num` / `tmp`.`today_order_num`) * 100),1) end) AS `today_success_rate`,`tmp`.`merchant_id` AS `merchant_id`,`tmp`.`gathering_channel_id` AS `channel_id`,`tmp`.`total_trade_amount` AS `total_trade_amount`,`tmp`.`total_poundage` AS `total_poundage`,`tmp`.`total_paid_order_num` AS `total_paid_order_num`,`tmp`.`total_order_num` AS `total_order_num`,`tmp`.`month_trade_amount` AS `month_trade_amount`,`tmp`.`month_poundage` AS `month_poundage`,`tmp`.`month_paid_order_num` AS `month_paid_order_num`,`tmp`.`month_order_num` AS `month_order_num`,`tmp`.`yesterday_trade_amount` AS `yesterday_trade_amount`,`tmp`.`yesterday_poundage` AS `yesterday_poundage`,`tmp`.`yesterday_paid_order_num` AS `yesterday_paid_order_num`,`tmp`.`yesterday_order_num` AS `yesterday_order_num`,`tmp`.`today_trade_amount` AS `today_trade_amount`,`tmp`.`today_poundage` AS `today_poundage`,`tmp`.`today_paid_order_num` AS `today_paid_order_num`,`tmp`.`today_order_num` AS `today_order_num` from (((select `po`.`merchant_id` AS `merchant_id`,`po`.`gathering_channel_id` AS `gathering_channel_id`,ifnull(round(sum((case when (`po`.`order_state` = '4') then `po`.`gathering_amount` else 0 end)),4),0) AS `total_trade_amount`,ifnull(round(sum((case when (`po`.`order_state` = '4') then ((`po`.`gathering_amount` * `po`.`rate`) / 100) else 0 end)),4),0) AS `total_poundage`,ifnull(sum((case when (`po`.`order_state` = '4') then 1 else 0 end)),0) AS `total_paid_order_num`,count(1) AS `total_order_num`,ifnull(round(sum((case when ((`po`.`order_state` = '4') and (`po`.`submit_time` >= str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s')) and (`po`.`submit_time` < (str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s') + interval 1 month))) then `po`.`gathering_amount` else 0 end)),4),0) AS `month_trade_amount`,ifnull(round(sum((case when ((`po`.`order_state` = '4') and (`po`.`submit_time` >= str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s')) and (`po`.`submit_time` < (str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s') + interval 1 month))) then ((`po`.`gathering_amount` * `po`.`rate`) / 100) else 0 end)),4),0) AS `month_poundage`,sum((case when ((`po`.`order_state` = '4') and (`po`.`submit_time` >= str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s')) and (`po`.`submit_time` < (str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s') + interval 1 month))) then 1 else 0 end)) AS `month_paid_order_num`,sum((case when ((`po`.`submit_time` >= str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s')) and (`po`.`submit_time` < (str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s') + interval 1 month))) then 1 else 0 end)) AS `month_order_num`,ifnull(round(sum((case when ((`po`.`order_state` = '4') and (`po`.`submit_time` >= (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval -(1) day)) and (`po`.`submit_time` < str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s'))) then `po`.`gathering_amount` else 0 end)),4),0) AS `yesterday_trade_amount`,ifnull(round(sum((case when ((`po`.`order_state` = '4') and (`po`.`submit_time` >= (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval -(1) day)) and (`po`.`submit_time` < str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s'))) then ((`po`.`gathering_amount` * `po`.`rate`) / 100) else 0 end)),4),0) AS `yesterday_poundage`,sum((case when ((`po`.`order_state` = '4') and (`po`.`submit_time` >= (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval -(1) day)) and (`po`.`submit_time` < str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s'))) then 1 else 0 end)) AS `yesterday_paid_order_num`,sum((case when ((`po`.`submit_time` >= (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval -(1) day)) and (`po`.`submit_time` < str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s'))) then 1 else 0 end)) AS `yesterday_order_num`,ifnull(round(sum((case when ((`po`.`order_state` = '4') and (`po`.`submit_time` >= str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s')) and (`po`.`submit_time` < (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval 1 day))) then `po`.`gathering_amount` else 0 end)),4),0) AS `today_trade_amount`,ifnull(round(sum((case when ((`po`.`order_state` = '4') and (`po`.`submit_time` >= str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s')) and (`po`.`submit_time` < (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval 1 day))) then ((`po`.`gathering_amount` * `po`.`rate`) / 100) else 0 end)),4),0) AS `today_poundage`,sum((case when ((`po`.`order_state` = '4') and (`po`.`submit_time` >= str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s')) and (`po`.`submit_time` < (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval 1 day))) then 1 else 0 end)) AS `today_paid_order_num`,sum((case when ((`po`.`submit_time` >= str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s')) and (`po`.`submit_time` < (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval 1 day))) then 1 else 0 end)) AS `today_order_num` from `merchant_order` `po` group by `po`.`merchant_id`,`po`.`gathering_channel_id`) `tmp` left join `merchant` `m` on((`tmp`.`merchant_id` = `m`.`id`))) left join `gathering_channel` `gc` on((`tmp`.`gathering_channel_id` = `gc`.`id`))) where (`m`.`id` is not null);

-- ----------------------------
--  View structure for `v_merchant_everyday_statistical`
-- ----------------------------
DROP VIEW IF EXISTS `v_merchant_everyday_statistical`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_merchant_everyday_statistical` AS select concat(`tmp`.`merchant_id`,'_',convert(`tmp`.`everyday` using utf8mb3)) AS `id`,`tmp`.`merchant_id` AS `merchant_id`,str_to_date(`tmp`.`everyday`,'%Y-%m-%d %H:%i:%s') AS `everyday`,`tmp`.`trade_amount` AS `trade_amount`,`tmp`.`paid_order_num` AS `paid_order_num`,`tmp`.`order_num` AS `order_num` from (select `po`.`merchant_id` AS `merchant_id`,date_format(`po`.`submit_time`,'%Y-%m-%d') AS `everyday`,ifnull(round(sum((case when (`po`.`order_state` = '4') then `po`.`gathering_amount` else 0 end)),4),0) AS `trade_amount`,ifnull(sum((case when (`po`.`order_state` = '4') then 1 else 0 end)),0) AS `paid_order_num`,count(1) AS `order_num` from `merchant_order` `po` group by `po`.`merchant_id`,date_format(`po`.`submit_time`,'%Y-%m-%d')) `tmp`;

-- ----------------------------
--  View structure for `v_merchant_trade_situation`
-- ----------------------------
DROP VIEW IF EXISTS `v_merchant_trade_situation`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_merchant_trade_situation` AS select `m`.`id` AS `id`,`m`.`merchant_name` AS `merchant_name`,round((`tmp`.`total_trade_amount` - `tmp`.`total_poundage`),4) AS `total_actual_income`,round((`tmp`.`month_trade_amount` - `tmp`.`month_poundage`),4) AS `month_actual_income`,round((`tmp`.`yesterday_trade_amount` - `tmp`.`yesterday_poundage`),4) AS `yesterday_actual_income`,round((`tmp`.`today_trade_amount` - `tmp`.`today_poundage`),4) AS `today_actual_income`,(case when (`tmp`.`total_order_num` = 0) then 0 else round(((`tmp`.`total_paid_order_num` / `tmp`.`total_order_num`) * 100),1) end) AS `total_success_rate`,(case when (`tmp`.`month_order_num` = 0) then 0 else round(((`tmp`.`month_paid_order_num` / `tmp`.`month_order_num`) * 100),1) end) AS `month_success_rate`,(case when (`tmp`.`yesterday_order_num` = 0) then 0 else round(((`tmp`.`yesterday_paid_order_num` / `tmp`.`yesterday_order_num`) * 100),1) end) AS `yesterday_success_rate`,(case when (`tmp`.`today_order_num` = 0) then 0 else round(((`tmp`.`today_paid_order_num` / `tmp`.`today_order_num`) * 100),1) end) AS `today_success_rate`,`tmp`.`total_trade_amount` AS `total_trade_amount`,`tmp`.`total_poundage` AS `total_poundage`,`tmp`.`total_paid_order_num` AS `total_paid_order_num`,`tmp`.`total_order_num` AS `total_order_num`,`tmp`.`month_trade_amount` AS `month_trade_amount`,`tmp`.`month_poundage` AS `month_poundage`,`tmp`.`month_paid_order_num` AS `month_paid_order_num`,`tmp`.`month_order_num` AS `month_order_num`,`tmp`.`yesterday_trade_amount` AS `yesterday_trade_amount`,`tmp`.`yesterday_poundage` AS `yesterday_poundage`,`tmp`.`yesterday_paid_order_num` AS `yesterday_paid_order_num`,`tmp`.`yesterday_order_num` AS `yesterday_order_num`,`tmp`.`today_trade_amount` AS `today_trade_amount`,`tmp`.`today_poundage` AS `today_poundage`,`tmp`.`today_paid_order_num` AS `today_paid_order_num`,`tmp`.`today_order_num` AS `today_order_num` from (`merchant` `m` left join (select `po`.`merchant_id` AS `merchant_id`,ifnull(round(sum((case when (`po`.`order_state` = '4') then `po`.`gathering_amount` else 0 end)),4),0) AS `total_trade_amount`,ifnull(round(sum((case when (`po`.`order_state` = '4') then ((`po`.`gathering_amount` * `po`.`rate`) / 100) else 0 end)),4),0) AS `total_poundage`,ifnull(sum((case when (`po`.`order_state` = '4') then 1 else 0 end)),0) AS `total_paid_order_num`,count(1) AS `total_order_num`,ifnull(round(sum((case when ((`po`.`order_state` = '4') and (`po`.`submit_time` >= str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s')) and (`po`.`submit_time` < (str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s') + interval 1 month))) then `po`.`gathering_amount` else 0 end)),4),0) AS `month_trade_amount`,ifnull(round(sum((case when ((`po`.`order_state` = '4') and (`po`.`submit_time` >= str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s')) and (`po`.`submit_time` < (str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s') + interval 1 month))) then ((`po`.`gathering_amount` * `po`.`rate`) / 100) else 0 end)),4),0) AS `month_poundage`,sum((case when ((`po`.`order_state` = '4') and (`po`.`submit_time` >= str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s')) and (`po`.`submit_time` < (str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s') + interval 1 month))) then 1 else 0 end)) AS `month_paid_order_num`,sum((case when ((`po`.`submit_time` >= str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s')) and (`po`.`submit_time` < (str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s') + interval 1 month))) then 1 else 0 end)) AS `month_order_num`,ifnull(round(sum((case when ((`po`.`order_state` = '4') and (`po`.`submit_time` >= (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval -(1) day)) and (`po`.`submit_time` < str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s'))) then `po`.`gathering_amount` else 0 end)),4),0) AS `yesterday_trade_amount`,ifnull(round(sum((case when ((`po`.`order_state` = '4') and (`po`.`submit_time` >= (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval -(1) day)) and (`po`.`submit_time` < str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s'))) then ((`po`.`gathering_amount` * `po`.`rate`) / 100) else 0 end)),4),0) AS `yesterday_poundage`,sum((case when ((`po`.`order_state` = '4') and (`po`.`submit_time` >= (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval -(1) day)) and (`po`.`submit_time` < str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s'))) then 1 else 0 end)) AS `yesterday_paid_order_num`,sum((case when ((`po`.`submit_time` >= (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval -(1) day)) and (`po`.`submit_time` < str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s'))) then 1 else 0 end)) AS `yesterday_order_num`,ifnull(round(sum((case when ((`po`.`order_state` = '4') and (`po`.`submit_time` >= str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s')) and (`po`.`submit_time` < (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval 1 day))) then `po`.`gathering_amount` else 0 end)),4),0) AS `today_trade_amount`,ifnull(round(sum((case when ((`po`.`order_state` = '4') and (`po`.`submit_time` >= str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s')) and (`po`.`submit_time` < (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval 1 day))) then ((`po`.`gathering_amount` * `po`.`rate`) / 100) else 0 end)),4),0) AS `today_poundage`,sum((case when ((`po`.`order_state` = '4') and (`po`.`submit_time` >= str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s')) and (`po`.`submit_time` < (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval 1 day))) then 1 else 0 end)) AS `today_paid_order_num`,sum((case when ((`po`.`submit_time` >= str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s')) and (`po`.`submit_time` < (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval 1 day))) then 1 else 0 end)) AS `today_order_num` from `merchant_order` `po` group by `po`.`merchant_id`) `tmp` on((`m`.`id` = `tmp`.`merchant_id`)));

-- ----------------------------
--  View structure for `v_recharge_withdraw_log`
-- ----------------------------
DROP VIEW IF EXISTS `v_recharge_withdraw_log`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_recharge_withdraw_log` AS select `recharge_order`.`id` AS `id`,`recharge_order`.`note` AS `note`,`recharge_order`.`recharge_amount` AS `amount`,`recharge_order`.`order_no` AS `order_no`,`recharge_order`.`order_state` AS `order_state`,`recharge_order`.`submit_time` AS `submit_time`,`recharge_order`.`user_account_id` AS `user_account_id`,'1' AS `order_type` from `recharge_order` union all select `wr`.`id` AS `id`,`wr`.`note` AS `note`,`wr`.`withdraw_amount` AS `amount`,`wr`.`order_no` AS `order_no`,`wr`.`state` AS `order_state`,`wr`.`submit_time` AS `submit_time`,`wr`.`user_account_id` AS `user_account_id`,'2' AS `order_type` from `withdraw_record` `wr`;

-- ----------------------------
--  View structure for `v_today_account_receive_order_situation`
-- ----------------------------
DROP VIEW IF EXISTS `v_today_account_receive_order_situation`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_today_account_receive_order_situation` AS select `b`.`received_account_id` AS `received_account_id`,`b`.`user_name` AS `user_name`,`b`.`gathering_amount` AS `gathering_amount`,`b`.`order_num` AS `order_num`,`b`.`paid_amount` AS `paid_amount`,`b`.`bounty` AS `bounty`,`b`.`paid_order_num` AS `paid_order_num`,`b`.`rebate_amount` AS `rebate_amount`,(case when (`b`.`order_num` = 0) then 0 else round(((`b`.`paid_order_num` / `b`.`order_num`) * 100),1) end) AS `success_rate` from (select `ua`.`id` AS `received_account_id`,`ua`.`user_name` AS `user_name`,ifnull(`b`.`gathering_amount`,0) AS `gathering_amount`,ifnull(`b`.`order_num`,0) AS `order_num`,ifnull(`b`.`paid_amount`,0) AS `paid_amount`,ifnull(`b`.`bounty`,0) AS `bounty`,ifnull(`b`.`paid_order_num`,0) AS `paid_order_num`,ifnull(`c`.`rebate_amount`,0) AS `rebate_amount` from ((`user_account` `ua` left join (select `mo`.`received_account_id` AS `received_account_id`,round(sum(`mo`.`gathering_amount`),4) AS `gathering_amount`,count(1) AS `order_num`,round(sum((case when (`mo`.`order_state` = '4') then `mo`.`gathering_amount` else 0 end)),4) AS `paid_amount`,round(sum((case when (`mo`.`order_state` = '4') then `mo`.`bounty` else 0 end)),4) AS `bounty`,sum((case when (`mo`.`order_state` = '4') then 1 else 0 end)) AS `paid_order_num` from `merchant_order` `mo` where ((`mo`.`received_account_id` is not null) and (`mo`.`received_time` >= str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s')) and (`mo`.`received_time` < (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval 1 day))) group by `mo`.`received_account_id`) `b` on((`ua`.`id` = `b`.`received_account_id`))) left join (select `rebate`.`rebate_account_id` AS `rebate_account_id`,round(sum(`rebate`.`rebate_amount`),4) AS `rebate_amount` from `order_rebate` `rebate` where ((`rebate`.`create_time` >= str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s')) and (`rebate`.`create_time` < (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval 1 day))) group by `rebate`.`rebate_account_id`) `c` on((`ua`.`id` = `c`.`rebate_account_id`)))) `b`;

-- ----------------------------
--  View structure for `v_total_account_receive_order_situation`
-- ----------------------------
DROP VIEW IF EXISTS `v_total_account_receive_order_situation`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_total_account_receive_order_situation` AS select `b`.`received_account_id` AS `received_account_id`,`b`.`user_name` AS `user_name`,`b`.`gathering_amount` AS `gathering_amount`,`b`.`order_num` AS `order_num`,`b`.`paid_amount` AS `paid_amount`,`b`.`bounty` AS `bounty`,`b`.`paid_order_num` AS `paid_order_num`,`b`.`rebate_amount` AS `rebate_amount`,(case when (`b`.`order_num` = 0) then 0 else round(((`b`.`paid_order_num` / `b`.`order_num`) * 100),1) end) AS `success_rate` from (select `ua`.`id` AS `received_account_id`,`ua`.`user_name` AS `user_name`,ifnull(`b`.`gathering_amount`,0) AS `gathering_amount`,ifnull(`b`.`order_num`,0) AS `order_num`,ifnull(`b`.`paid_amount`,0) AS `paid_amount`,ifnull(`b`.`bounty`,0) AS `bounty`,ifnull(`b`.`paid_order_num`,0) AS `paid_order_num`,ifnull(`c`.`rebate_amount`,0) AS `rebate_amount` from ((`user_account` `ua` left join (select `mo`.`received_account_id` AS `received_account_id`,round(sum(`mo`.`gathering_amount`),4) AS `gathering_amount`,count(1) AS `order_num`,round(sum((case when (`mo`.`order_state` = '4') then `mo`.`gathering_amount` else 0 end)),4) AS `paid_amount`,round(sum((case when (`mo`.`order_state` = '4') then `mo`.`bounty` else 0 end)),4) AS `bounty`,sum((case when (`mo`.`order_state` = '4') then 1 else 0 end)) AS `paid_order_num` from `merchant_order` `mo` where (`mo`.`received_account_id` is not null) group by `mo`.`received_account_id`) `b` on((`ua`.`id` = `b`.`received_account_id`))) left join (select `rebate`.`rebate_account_id` AS `rebate_account_id`,round(sum(`rebate`.`rebate_amount`),4) AS `rebate_amount` from `order_rebate` `rebate` group by `rebate`.`rebate_account_id`) `c` on((`ua`.`id` = `c`.`rebate_account_id`)))) `b`;

-- ----------------------------
--  View structure for `v_trade_situation`
-- ----------------------------
DROP VIEW IF EXISTS `v_trade_situation`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_trade_situation` AS select (case when (`tmp`.`total_order_num` = 0) then 0 else round(((`tmp`.`total_paid_order_num` / `tmp`.`total_order_num`) * 100),1) end) AS `total_success_rate`,(case when (`tmp`.`month_order_num` = 0) then 0 else round(((`tmp`.`month_paid_order_num` / `tmp`.`month_order_num`) * 100),1) end) AS `month_success_rate`,(case when (`tmp`.`yesterday_order_num` = 0) then 0 else round(((`tmp`.`yesterday_paid_order_num` / `tmp`.`yesterday_order_num`) * 100),1) end) AS `yesterday_success_rate`,(case when (`tmp`.`today_order_num` = 0) then 0 else round(((`tmp`.`today_paid_order_num` / `tmp`.`today_order_num`) * 100),1) end) AS `today_success_rate`,`tmp`.`total_trade_amount` AS `total_trade_amount`,`tmp`.`total_paid_order_num` AS `total_paid_order_num`,`tmp`.`total_order_num` AS `total_order_num`,`tmp`.`month_trade_amount` AS `month_trade_amount`,`tmp`.`month_paid_order_num` AS `month_paid_order_num`,`tmp`.`month_order_num` AS `month_order_num`,`tmp`.`yesterday_trade_amount` AS `yesterday_trade_amount`,`tmp`.`yesterday_paid_order_num` AS `yesterday_paid_order_num`,`tmp`.`yesterday_order_num` AS `yesterday_order_num`,`tmp`.`today_trade_amount` AS `today_trade_amount`,`tmp`.`today_paid_order_num` AS `today_paid_order_num`,`tmp`.`today_order_num` AS `today_order_num` from (select ifnull(round(sum((case when (`mo`.`order_state` = '4') then `mo`.`gathering_amount` else 0 end)),4),0) AS `total_trade_amount`,sum((case when (`mo`.`order_state` = '4') then 1 else 0 end)) AS `total_paid_order_num`,count(1) AS `total_order_num`,ifnull(round(sum((case when ((`mo`.`order_state` = '4') and (`mo`.`submit_time` >= str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s')) and (`mo`.`submit_time` < (str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s') + interval 1 month))) then `mo`.`gathering_amount` else 0 end)),4),0) AS `month_trade_amount`,sum((case when ((`mo`.`order_state` = '4') and (`mo`.`submit_time` >= str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s')) and (`mo`.`submit_time` < (str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s') + interval 1 month))) then 1 else 0 end)) AS `month_paid_order_num`,sum((case when ((`mo`.`submit_time` >= str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s')) and (`mo`.`submit_time` < (str_to_date(date_format(curdate(),'%Y-%m-01 00:00:00'),'%Y-%m-%d %H:%i:%s') + interval 1 month))) then 1 else 0 end)) AS `month_order_num`,ifnull(round(sum((case when ((`mo`.`order_state` = '4') and (`mo`.`submit_time` >= (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval -(1) day)) and (`mo`.`submit_time` < str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s'))) then `mo`.`gathering_amount` else 0 end)),4),0) AS `yesterday_trade_amount`,sum((case when ((`mo`.`order_state` = '4') and (`mo`.`submit_time` >= (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval -(1) day)) and (`mo`.`submit_time` < str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s'))) then 1 else 0 end)) AS `yesterday_paid_order_num`,sum((case when ((`mo`.`submit_time` >= (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval -(1) day)) and (`mo`.`submit_time` < str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s'))) then 1 else 0 end)) AS `yesterday_order_num`,ifnull(round(sum((case when ((`mo`.`order_state` = '4') and (`mo`.`submit_time` >= str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s')) and (`mo`.`submit_time` < (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval 1 day))) then `mo`.`gathering_amount` else 0 end)),4),0) AS `today_trade_amount`,sum((case when ((`mo`.`order_state` = '4') and (`mo`.`submit_time` >= str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s')) and (`mo`.`submit_time` < (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval 1 day))) then 1 else 0 end)) AS `today_paid_order_num`,sum((case when ((`mo`.`submit_time` >= str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s')) and (`mo`.`submit_time` < (str_to_date(date_format(now(),'%Y-%m-%d'),'%Y-%m-%d %H:%i:%s') + interval 1 day))) then 1 else 0 end)) AS `today_order_num` from `merchant_order` `mo`) `tmp`;

SET FOREIGN_KEY_CHECKS = 1;
