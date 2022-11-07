/*
 Navicat MySQL Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80022
 Source Host           : localhost
 Source Database       : runscore4

 Target Server Type    : MySQL
 Target Server Version : 80022
 File Encoding         : utf-8

 Date: 05/06/2022 20:51:26 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
