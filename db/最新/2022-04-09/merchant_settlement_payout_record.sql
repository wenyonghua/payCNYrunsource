

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
