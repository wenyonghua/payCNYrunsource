package me.zohar.runscore.merchant.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 付款对象
 */
@Data
public class ManualPayOutStartOrderParam {

	/**
	 * 商户号
	 */
	@NotBlank
	private String merchantNum;

	/**
	 * 收款渠道
	 */
	@NotBlank
	private String gatheringChannelCode;

	/**
	 * 收款金额
	 */
	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double gatheringAmount;

	/**
	 * 商户订单号
	 */
	@NotBlank
	private String orderNo;

	@NotBlank
	private String notifyUrl;

	private String returnUrl;

	private String attch;

	private String sign;

	private String specifiedReceivedAccountId;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+7")
	public Date publishTime;
	/**
	 * 系统来源
	 */
	private String systemSource;


	/**
	 * 收款人名字
	 */
	private String shoukuBankPayee;

	/**
	 * 收款银行卡号
	 */
	private String shoukuBankNumber;
	/**
	 * 收款银行名称
	 */
	private String shoukuBankName;
	/**
	 * 收款银行支行信息
	 */
	private String shoukuBankBranch;


}
