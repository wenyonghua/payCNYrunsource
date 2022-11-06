package me.zohar.runscore.merchant.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.zohar.runscore.common.param.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class MerchantAutoOrderQueryCondParam extends PageParam {


	private  String bankFlow;//银行流水号


	private String orderNo;//订单号


	private String outOrderNo;//商户订单号外部订单号


	private String note;//备注

	/**
	 * 银行卡号
	 */
	private String bankCardAccount;

	/**
	 * 订单状态
	 */
	private String orderState;
	/**
	 * 自动机回调金额
	 */
	private String autoAmount;

	private String receiverUserName;

	/**
	 * 提交开始时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date submitStartTime;

	/**
	 * 提交结束时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date submitEndTime;

	/**
	 * 接单开始时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date receiveOrderStartTime;

	/**
	 * 接单结束时间
	 */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date receiveOrderEndTime;

	/**
	 * 银行完整附言信息
	 */
	private String bankRemark;





}
