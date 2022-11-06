package me.zohar.runscore.merchant.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import me.zohar.runscore.common.param.PageParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 付款查询对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MerchantPayOutOrderQueryCondParam extends PageParam {

	private String orderNo;

	private String merchantName;

	/**
	 * 商户订单号外部订单号
	 */
	private String merchantOrderNo;

	private String gatheringChannelCode;

	/**
	 * 订单状态
	 */
	private String orderState;

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
	 * 银行卡号
	 */
	private String bankCardAccount;
	/**
	 * 备注
	 */
	private String note;
	/**
	 * 附加信息
	 */
	private String attch;
	/**
	 * 系统来源
	 */
	private String systemSource;
	/**
	 * 收款银行卡号
	 */
	private String shoukuBankNumber;
	/**
	 * 付款银行卡号
	 */
	private String fukuBankNumber;

}
