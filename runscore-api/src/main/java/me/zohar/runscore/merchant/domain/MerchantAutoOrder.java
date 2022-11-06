package me.zohar.runscore.merchant.domain;

import lombok.Getter;
import lombok.Setter;
import me.zohar.runscore.constants.Constant;
import me.zohar.runscore.gatheringcode.domain.GatheringCode;
import me.zohar.runscore.useraccount.domain.UserAccount;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "merchant_auto_order")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MerchantAutoOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@Id
	@Column(name = "id", length = 32)
	private String id;


	/**
	 * 订单号
	 */
	@Column(name = "order_no", length = 455)
	private String orderNo;

	/**
	 * 收款金额 gathering_amount
	 */
	@Column(name = "gathering_amount", length = 455)
	private Double gatheringAmount;

	/**
	 * 提交时间
	 */
	private Date submitTime;

	/**
	 * 有效时间
	 */
	private Date usefulTime;

	/**
	 * 订单状态 order_state
	 * 1匹配成功，2匹配失败
	 */
	@Column(name = "order_state", length = 255)
	private String orderState;

	/**
	 * 备注信息
	 */
	@Column(name = "note", length = 255)
	private String note;


	@Column(name = "merchant_id", length = 32)
	private String merchantId;

	/**
	 * 接单人账号id
	 */
	@Column(name = "received_account_id", length = 32)
	private String receivedAccountId;



	/**
	 * 处理时间deal_time
	 */
	private Date dealTime;

	/**
	 * 确认时间confirm_time
	 */
	private Date confirmTime;


	/**
	 * 乐观锁版本号
	 */
	@Version
	private Long version;



	/**
	 * 外部商户订单号
	 */
	@Column(name = "out_order_no", length = 255)
	private String outOrderNo;//外部商户订单号



	/**
	 * 系统来源
	 */
	@Column(name = "system_source", length = 32)
	private String systemSource;

	/**
	 * 付款人支付宝和微信使用
	 */
	@Column(name = "pay_fukuan_name", length = 52)
	private String payFukuanName;


	/**
	 * 银行交易流水号
	 */
	@Column(name = "bank_flow", length = 255)
	private String bankFlow;
	/**
	 *银行卡号
	 */
	@Column(name = "bank_number", length = 255)
	private String bankNumber;

	/**
	 * 自动机回调金额
	 */
	@Column(name = "auto_amount", length = 455)
	private String autoAmount;
	/**
	 * 银行卡总余额
	 */
	@Column(name = "balance", length = 255)
	private String balance;

	/**
	 * 备注
	 */
	@Column(name = "remark", length = 255)
	private String remark;

	/**
	 * 银行完整附言信息
	 */
	@Column(name = "bank_remark", length = 2550)
	private String bankRemark;

	/**
	 * 自动机返回的回调数据记录
	 */
	@Column(name = "return_message", length = 2550)
	private String returnMessage;//自动机返回的回调数据记录

	/**
	 * 记录匹配失败错误数据
	 */
	@Column(name = "save_fail_error", length = 255)
	private String    saveFailError;

	/**
	 * 银行名称比如建设银行
	 */
	@Column(name = "bank_name", length = 255)
	private String    bankName;
	/**
	 * 卡姓名比如张三
	 */
	@Column(name = "card_name", length = 255)
	private String    cardName;

}
